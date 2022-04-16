package net.runelite.client.plugins.raidscouterext;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.raids.Raid;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.RoomType;
import net.runelite.client.plugins.raids.events.RaidReset;
import net.runelite.client.plugins.raids.events.RaidScouted;
import net.runelite.client.plugins.raids.solver.Room;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUploadStyle;
import net.runelite.client.util.Text;
import net.runelite.client.ws.PartyMember;
import net.runelite.client.ws.PartyService;
import net.runelite.client.ws.WSClient;
import net.runelite.http.api.ws.messages.party.PartyChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Raid Scouter Ext",
        description = "Can be used with runelite's cox plugin",
        tags = {"chambers", "xeric", "raids", "spoon", "cox", "scout", "layout"}
)
public class RaidScouterExtPlugin extends Plugin {
    @Getter
    private Raid raid;
    @Inject
    private Client client;
    @Inject
    private RuneLiteConfig runeLiteConfig;
    @Inject
    private ImageCapture imageCapture;
    @Inject
    private RaidScouterExtConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private RaidScouterExtOverlay overlay;
    @Inject
    private ItemManager itemManager;
    @Inject
    private PartyService party;
    @Inject
    private WSClient ws;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Getter
    private final Set<String> roomWhitelist = new HashSet<>();
    @Getter
    private final Set<String> roomBlacklist = new HashSet<>();
    @Getter
    private final Set<String> rotationWhitelist = new HashSet<>();
    @Getter
    private final Set<String> layoutWhitelist = new HashSet<>();
    @Getter
    private final Set<String> roomHighlightedList = new HashSet<>();
    @Getter
    private final Map<String, List<Integer>> recommendedItemsList = new HashMap<>();
    @Getter
    private int raidPartyID;
    @Getter
    private boolean shouldShowOverlays;

    // if the player is inside of a raid or not
    @Getter
    private boolean inRaidChambers;
    private static int raidState;
    private static final Pattern ROTATION_REGEX = Pattern.compile("\\[(.*?)]");
    private static final int OLM_PLANE = 0;
    private static final String RAID_START_MESSAGE = "The raid has begun!";
    private static final String LEVEL_COMPLETE_MESSAGE = "level complete!";
    private static final String RAID_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");
    private static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");
    private static final int RAID_TIMER_VARBIT = 6386;
    public int raidTime = -1;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        updateLists();
        this.clientThread.invokeLater(this::checkRaidPresence);
        keyManager.registerKeyListener(screenshotHotkeyListener);
    }

    @Override
    protected void shutDown() throws Exception
    {
        reset();
        overlayManager.remove(overlay);
        inRaidChambers = false;
        keyManager.unregisterKeyListener(screenshotHotkeyListener);
    }

    private void reset() {
        raidTime = -1;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("raids") && !event.getGroup().equals("spoonraidscouter")) {
            return;
        }

        updateLists();
    }

    @Subscribe
    public void onRaidScouted(RaidScouted raidScouted)
    {
        this.raid = raidScouted.getRaid();
    }

    @Subscribe
    public void onRaidReset(RaidReset raidReset)
    {
        this.raid = null;
    }

    @Subscribe
    public void onOverlayMenuClicked(final OverlayMenuClicked event)
    {
        if (!(event.getEntry().getMenuAction() == MenuAction.RUNELITE_OVERLAY
                && event.getOverlay() == overlay))
        {
            return;
        }

        if (event.getEntry().getOption().equals(RaidScouterExtOverlay.BROADCAST_ACTION))
        {
            sendRaidLayoutMessage();
        }
        else if (event.getEntry().getOption().equals(RaidScouterExtOverlay.SCREENSHOT_ACTION))
        {
            clientThread.invoke(RaidScouterExtPlugin.this::screenshotScoutOverlay);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        this.clientThread.invokeLater(this::checkRaidPresence);

        inRaidChambers = client.getVarbitValue(Varbits.IN_RAID) == 1;
        if(!inRaidChambers){
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        shouldShowOverlays = shouldShowOverlays();
    }

    @Provides
    RaidScouterExtConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RaidScouterExtConfig.class);
    }

    @VisibleForTesting
    private void updateLists()
    {
        updateList(roomWhitelist, configManager.getConfiguration("raids", "whitelistedRooms"));
        updateList(roomBlacklist, configManager.getConfiguration("raids", "blacklistedRooms"));
        updateList(layoutWhitelist, configManager.getConfiguration("raids", "whitelistedLayouts"));
        updateList(roomHighlightedList, config.highlightedRooms());
        updateMap(recommendedItemsList, config.recommendedItems());

        // Update rotation whitelist
        rotationWhitelist.clear();
        if (configManager.getConfiguration("raids", "whitelistedRotations") != null)
        {
            for (String line : configManager.getConfiguration("raids", "whitelistedRotations").split("\\n"))
            {
                rotationWhitelist.add(line.toLowerCase().replace(" ", ""));
            }
        }
    }

    private void updateList(Collection<String> list, String input)
    {
        if (input == null)
        {
            return;
        }

        list.clear();
        for (String s : Text.fromCSV(input.toLowerCase()))
        {
            if (s.equals("unknown"))
            {
                list.add("unknown (combat)");
                list.add("unknown (puzzle)");
            }
            else
            {
                list.add(s);
            }
        }
    }

    private void updateMap(Map<String, List<Integer>> map, String input)
    {
        map.clear();

        Matcher m = ROTATION_REGEX.matcher(input);
        while (m.find())
        {
            String everything = m.group(1).toLowerCase();
            int split = everything.indexOf(',');
            if (split < 0)
                continue;
            String key = everything.substring(0, split);
            if (key.length() < 1)
                continue;
            List<String> itemNames = Text.fromCSV(everything.substring(split));

            map.computeIfAbsent(key, k -> new ArrayList<>());

            for (String itemName : itemNames)
            {
                if (itemName.equals(""))
                    continue;
                if (itemName.equals("ice barrage"))
                    map.get(key).add(SpriteID.SPELL_ICE_BARRAGE);
                else if (itemName.startsWith("salve"))
                    map.get(key).add(ItemID.SALVE_AMULETEI);
                else if (itemName.contains("blowpipe"))
                    map.get(key).add(ItemID.TOXIC_BLOWPIPE);
                else if (itemManager.search(itemName).size() > 0)
                    map.get(key).add(itemManager.search(itemName).get(0).getId());
                else
                    log.info("RaidsPlugin: Could not find an item ID for item: " + itemName);
            }
        }
    }

    boolean getRotationMatches()
    {
        RaidRoom[] combatRooms = getCombatRooms();
        String rotation = Arrays.stream(combatRooms)
                .map(RaidRoom::getName)
                .map(String::toLowerCase)
                .collect(Collectors.joining(","));

        return rotationWhitelist.contains(rotation);
    }

    private RaidRoom[] getCombatRooms()
    {
        List<RaidRoom> combatRooms = new ArrayList<>();

        for (Room room : raid.getLayout().getRooms())
        {
            if (room == null)
            {
                continue;
            }

            if (raid.getRooms()[room.getPosition()].getType() == RoomType.COMBAT)
            {
                combatRooms.add(raid.getRooms()[room.getPosition()]);
            }
        }

        return combatRooms.toArray(new RaidRoom[0]);
    }

    private void sendRaidLayoutMessage()
    {
        final String layout = getRaid().getLayout().toCodeString();
        final String rooms = toRoomString(getRaid());
        final String raidData = "[" + layout + "]: " + rooms;

        final String layoutMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append("Layout: ")
                .append(ChatColorType.NORMAL)
                .append(raidData)
                .build();

        final PartyMember localMember = party.getLocalMember();

        if (party.getMembers().isEmpty() || localMember == null)
        {
            chatMessageManager.queue(QueuedMessage.builder()
                    .type(ChatMessageType.FRIENDSCHATNOTIFICATION)
                    .runeLiteFormattedMessage(layoutMessage)
                    .build());
        }
        else
        {
            final PartyChatMessage message = new PartyChatMessage(layoutMessage);
            message.setMemberId(localMember.getMemberId());
            ws.send(message);
        }
    }

    private String toRoomString(Raid raid)
    {
        final StringBuilder sb = new StringBuilder();

        for (RaidRoom room : getOrderedRooms(raid))
        {
            switch (room.getType())
            {
                case PUZZLE:
                case COMBAT:
                    sb.append(room.getName()).append(", ");
                    break;
            }
        }

        final String roomsString = sb.toString();
        return roomsString.substring(0, roomsString.length() - 2);
    }

    private List<RaidRoom> getOrderedRooms(Raid raid)
    {
        List<RaidRoom> orderedRooms = new ArrayList<>();
        for (Room r : raid.getLayout().getRooms())
        {
            final int position = r.getPosition();
            final RaidRoom room = raid.getRoom(position);

            if (room == null)
            {
                continue;
            }

            orderedRooms.add(room);
        }

        return orderedRooms;
    }

    private void screenshotScoutOverlay()
    {
        if (!shouldShowOverlays)
        {
            return;
        }

        Rectangle overlayDimensions = overlay.getBounds();
        BufferedImage overlayImage = new BufferedImage(overlayDimensions.width, overlayDimensions.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = overlayImage.createGraphics();
        graphic.setFont(runeLiteConfig.interfaceFontType().getFont());
        graphic.setColor(Color.BLACK);
        graphic.fillRect(0, 0, overlayDimensions.width, overlayDimensions.height);
        overlay.render(graphic);

        imageCapture.takeScreenshot(overlayImage, "CoX_scout-", false, configManager.getConfiguration("raids", "uploadScreenshot", ImageUploadStyle.class));
        graphic.dispose();
    }

    private final HotkeyListener screenshotHotkeyListener = new HotkeyListener(() -> config.screenshotHotkey())
    {
        @Override
        public void hotkeyPressed()
        {
            clientThread.invoke(RaidScouterExtPlugin.this::screenshotScoutOverlay);
        }
    };

    boolean shouldShowOverlays()
    {
        if (raid == null || raid.getLayout() == null || !config.scoutOverlay()) {
            return false;
        }

        if (isInRaidChambers())
        {
            // If the raid has started
            if (raidState > 0)
            {
                if (client.getPlane() == OLM_PLANE)
                {
                    return false;
                }

                return configManager.getConfiguration("raids", "scoutOverlayInRaid", Boolean.class);
            }
            else
            {
                return true;
            }
        }

        Boolean overlayAtBank = configManager.getConfiguration("raids", "scoutOverlayAtBank", Boolean.class);
        return getRaidPartyID() != -1 && overlayAtBank;
    }

    private void checkRaidPresence()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        int tempRaidState = client.getVarbitValue(Varbits.RAID_STATE);
        int tempPartyID = client.getVar(VarPlayer.IN_RAID_PARTY);
        boolean tempInRaid = client.getVarbitValue(Varbits.IN_RAID) == 1;

        // if the player's party state has changed
        if (tempPartyID != raidPartyID)
        {
            raidPartyID = tempPartyID;
        }

        // if the player's raid state has changed
        if (tempInRaid != inRaidChambers)
        {
            inRaidChambers = tempInRaid;
        }

        // if the player's raid state has changed
        if (tempRaidState != raidState)
        {
            raidState = tempRaidState;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (inRaidChambers && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION)
        {
            String message = Text.removeTags(event.getMessage());

            if (message.startsWith(RAID_COMPLETE_MESSAGE))
            {
                raidTime = timeToSeconds(getTime());

                if (config.ptsHr()) {
                    int totalPoints = client.getVarbitValue(Varbits.TOTAL_POINTS);
                    int personalPoints = client.getVarbitValue(Varbits.PERSONAL_POINTS);
                    int partySize = client.getVarbitValue(Varbits.RAID_PARTY_SIZE);

                    double percentage = personalPoints / (totalPoints / 100.0);

                    String ptssolo;{
                        ptssolo = POINTS_FORMAT.format(((float) personalPoints / (float) raidTime) * 3600);
                    }

                    String ptsteam;{
                        ptsteam = POINTS_FORMAT.format(((float) totalPoints / (float) raidTime) * 3600);
                    }

                    String ptssplit;{
                        ptssplit = POINTS_FORMAT.format(((totalPoints / (float) raidTime) * 3600) / (partySize));
                    }

                    String chatMessage = new ChatMessageBuilder()
                            .append(ChatColorType.NORMAL)
                            .append("Solo Pts/Hr: ")
                            .append(ChatColorType.HIGHLIGHT)
                            .append(ptssolo)
                            .append(ChatColorType.NORMAL)
                            .append(" Team Pts/Hr: ")
                            .append(ChatColorType.HIGHLIGHT)
                            .append(ptsteam)
                            .append(ChatColorType.NORMAL)
                            .append(" Split Pts/Hr: ")
                            .append(ChatColorType.HIGHLIGHT)
                            .append(ptssplit)
                            .build();

                    chatMessageManager.queue(QueuedMessage.builder()
                            .type(ChatMessageType.FRIENDSCHATNOTIFICATION)
                            .runeLiteFormattedMessage(chatMessage)
                            .build());
                }
            }
        }
    }

    String getTime()
    {
        int seconds = (int) Math.floor(client.getVarbitValue(RAID_TIMER_VARBIT) * .6);
        return secondsToTime(seconds);
    }

    private int timeToSeconds(String s)
    {
        int seconds = -1;
        String[] split = s.split(":");
        if (split.length == 2)
        {
            seconds = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        }
        if (split.length == 3)
        {
            seconds = Integer.parseInt(split[0]) * 3600 + Integer.parseInt(split[1]) * 60 + Integer.parseInt(split[2]);
        }
        return seconds;
    }

    public String secondsToTime(int seconds)
    {
        StringBuilder builder = new StringBuilder();
        if (seconds >= 3600)
        {
            builder.append((int)Math.floor(seconds / 3600) + ":");
        }
        seconds %= 3600;
        if (builder.toString().equals(""))
        {
            builder.append((int)Math.floor(seconds / 60));
        }
        else
        {
            builder.append(StringUtils.leftPad(String.valueOf((int)Math.floor(seconds / 60)), 2, '0'));
        }
        builder.append(":");
        seconds %= 60;
        builder.append(StringUtils.leftPad(String.valueOf(seconds), 2, '0'));
        return builder.toString();
    }
}
