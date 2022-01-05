package net.runelite.client.plugins.raidscouterext;

import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.SpriteID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.raids.RaidRoom;
import net.runelite.client.plugins.raids.solver.Room;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class RaidScouterExtOverlay extends OverlayPanel
{
    static final String BROADCAST_ACTION = "Broadcast layout";
    static final String SCREENSHOT_ACTION = "Screenshot";
    private static final int BORDER_OFFSET = 2;
    private static final int ICON_SIZE = 32;
    private static final int SMALL_ICON_SIZE = 21;

    private final Client client;
    private final RaidScouterExtPlugin plugin;
    private final RaidScouterExtConfig config;

    private final ItemManager itemManager;
    private final SpriteManager spriteManager;
    private final PanelComponent panelImages = new PanelComponent();

    @Inject
    private WorldService worldService;

    @Inject
    private ConfigManager configManager;

    @Inject
    private RaidScouterExtOverlay(Client client, RaidScouterExtPlugin plugin, RaidScouterExtConfig config, ItemManager itemManager, SpriteManager spriteManager)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.spriteManager = spriteManager;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Raids overlay"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, BROADCAST_ACTION, "Raids overlay"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, SCREENSHOT_ACTION, "Raids overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        plugin.shouldShowOverlays();
        if (!plugin.isShouldShowOverlays())
        {
            return null;
        }

        Boolean ccDisplay = configManager.getConfiguration("raids", "ccDisplay", Boolean.class);
        Boolean enabledWhitelist = configManager.getConfiguration("raids", "enableLayoutWhitelist", Boolean.class);
        Boolean rotationWhitelist = configManager.getConfiguration("raids", "enableRotationWhitelist", Boolean.class);

        if (config.hideBackground())
        {
            panelComponent.setBackgroundColor(null);
        }
        else
        {
            panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
        }

        Color color = Color.WHITE;
        String layout;
        try
        {
            layout = plugin.getRaid().getLayout().toCodeString();
        }
        catch (NullPointerException e)
        {
            layout = "";
        }
        FontMetrics metrics = graphics.getFontMetrics();

        String displayLayout;
        if (config.displayFloorBreak())
        {
            displayLayout = plugin.getRaid().getLayout().toCode();
            displayLayout = displayLayout.substring(0, displayLayout.length() - 1).replaceAll("#", "").replaceFirst("¤", " | ");
        }
        else
        {
            displayLayout = layout;
        }
        if (enabledWhitelist && !plugin.getLayoutWhitelist().contains(layout.toLowerCase()))
        {
            color = Color.RED;
        }

        boolean hide = false;
        HashSet<String> roomNames = new HashSet<>();
        for (Room layoutRoom : plugin.getRaid().getLayout().getRooms())
        {
            int position = layoutRoom.getPosition();
            RaidRoom room = plugin.getRaid().getRoom(position);

            if (room == null)
            {
                continue;
            }
            roomNames.add(room.getName().toLowerCase());

            if (config.hideBlacklisted() && plugin.getRoomBlacklist().contains(room.getName().toLowerCase()))
            {
                hide = true;
                break;
            }
        }

        if (!hide)
        {
            if (config.hideMissingHighlighted())
            {
                int hCount = 0;
                for (String requiredRoom : plugin.getRoomHighlightedList())
                {
                    if (roomNames.contains(requiredRoom))
                    {
                        hCount++;
                    }
                }
                if(hCount < config.highlightedShowThreshold())
                {
                    hide = true;
                }
            }
            if (config.hideMissingLayout())
            {
                if (enabledWhitelist && !plugin.getLayoutWhitelist().contains(layout.toLowerCase()))
                {
                    hide = true;
                }
            }

            if (config.hideRopeless() != RaidScouterExtConfig.ropelessMode.OFF) {
                if (config.hideRopeless() == RaidScouterExtConfig.ropelessMode.CRABS_AND_ROPE){
                    if (!roomNames.contains("tightrope") && !roomNames.contains("crabs")) {
                        hide = true;
                    }
                }else{
                    if (!roomNames.contains("tightrope")){
                        hide = true;
                    }
                }
            }

            if (config.hideCustom()){
                hide = true;
                String[] strArr = config.hideCustomList().split(",");
                for(String str : strArr){
                    if (str.equalsIgnoreCase("msm")) {
                        if (roomNames.contains("muttadiles") && roomNames.contains("mystics") && roomNames.contains("shamans")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("mtm")) {
                        if (roomNames.contains("muttadiles") && roomNames.contains("tekton") && roomNames.contains("mystics")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("vtv")) {
                        if (roomNames.contains("vasa") && roomNames.contains("tekton") && roomNames.contains("vespula")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("tvg") || str.equalsIgnoreCase("gvt")) {
                        if (roomNames.contains("tekton") && roomNames.contains("vasa") && roomNames.contains("guardians")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("gvtv") || str.equalsIgnoreCase("vtvg")) {
                        if (roomNames.contains("vasa") && roomNames.contains("tekton") && roomNames.contains("vespula") && roomNames.contains("guardians")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("mgv") || str.equalsIgnoreCase("vgm")) {
                        if (roomNames.contains("muttadiles") && roomNames.contains("guardians") && roomNames.contains("vespula")){
                            hide = false;
                        }
                    }else if (str.equalsIgnoreCase("tmg") || str.equalsIgnoreCase("gmt")) {
                        if (roomNames.contains("muttadiles") && roomNames.contains("guardians") && roomNames.contains("tekton")){
                            hide = false;
                        }
                    }
                }
            }
        }

        if (hide) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bad Raid!")
                    .color(Color.RED)
                    .build());

            return super.render(graphics);
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(displayLayout)
                .color(color)
                .build());

        if (ccDisplay)
        {
            color = Color.RED;
            FriendsChatManager friendsChatManager = client.getFriendsChatManager();

            String worldString = "W" + client.getWorld();
            WorldResult worldResult = worldService.getWorlds();
            if (worldResult != null)
            {
                World world = worldResult.findWorld(client.getWorld());
                WorldRegion region = world.getRegion();
                if (region != null)
                {
                    String countryCode = region.getAlpha2();
                    worldString += " (" + countryCode + ")";
                }
            }

            String owner = "Join a FC";
            if (friendsChatManager != null)
            {
                owner = friendsChatManager.getOwner();
                color = Color.ORANGE;
            }

            panelComponent.setPreferredSize(new Dimension(Math.max(ComponentConstants.STANDARD_WIDTH, metrics.stringWidth(worldString) + metrics.stringWidth(owner) + 14), 0));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(worldString)
                    .right(owner)
                    .leftColor(Color.ORANGE)
                    .rightColor(color)
                    .build());
        }

        Set<Integer> imageIds = new HashSet<>();
        int roomWidth = 0;
        int temp;

        for (Room layoutRoom : plugin.getRaid().getLayout().getRooms())
        {
            int position = layoutRoom.getPosition();
            RaidRoom room = plugin.getRaid().getRoom(position);

            if (room == null)
            {
                continue;
            }

            temp = metrics.stringWidth(room.getName());
            if (temp > roomWidth)
            {
                roomWidth = temp;
            }

            color = Color.WHITE;

            switch (room.getType())
            {
                case COMBAT:
                    String bossName = room == RaidRoom.UNKNOWN_COMBAT ? "Unknown" : room.getName();
                    String bossNameLC = room.getName().toLowerCase();
                    if (config.showRecommendedItems() && plugin.getRecommendedItemsList().get(bossNameLC) != null)
                        imageIds.addAll(plugin.getRecommendedItemsList().get(bossNameLC));
                    if (plugin.getRoomHighlightedList().contains(bossNameLC) && !config.highlightColor().equals(Color.WHITE))
                    {
                        color = config.highlightColor();
                    }
                    else if (plugin.getRoomWhitelist().contains(bossNameLC))
                    {
                        color = Color.GREEN;
                    }
                    else if (plugin.getRoomBlacklist().contains(bossNameLC)
                            || rotationWhitelist && !plugin.getRotationMatches())
                    {
                        color = Color.RED;
                    }

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(config.showRecommendedItems() ? "" : room.getType().getName())
                            .right(bossName)
                            .rightColor(color)
                            .build());

                    break;

                case PUZZLE:
                    String puzzleName = room == RaidRoom.UNKNOWN_PUZZLE ? "Unknown" : room.getName();
                    String puzzleNameLC = room.getName().toLowerCase();
                    if (config.showRecommendedItems() && plugin.getRecommendedItemsList().get(puzzleNameLC) != null)
                        imageIds.addAll(plugin.getRecommendedItemsList().get(puzzleNameLC));
                    if (plugin.getRoomHighlightedList().contains(puzzleNameLC))
                    {
                        color = config.highlightColor();
                    }
                    else if (plugin.getRoomWhitelist().contains(puzzleNameLC))
                    {
                        color = Color.GREEN;
                    }
                    else if (plugin.getRoomBlacklist().contains(puzzleNameLC))
                    {
                        color = Color.RED;
                    }

                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(config.showRecommendedItems() ? "" : room.getType().getName())
                            .right(puzzleName)
                            .rightColor(color)
                            .build());
                    break;
            }
        }

        //add recommended items
        Dimension panelDims = super.render(graphics);
        if (config.showRecommendedItems() && imageIds.size() > 0)
        {
            panelImages.getChildren().clear();
            Integer[] idArray = imageIds.toArray(new Integer[0]);
            int fontHeight = metrics.getHeight();
            int imagesVerticalOffset = 2 + BORDER_OFFSET + fontHeight + (ccDisplay ? fontHeight : 0);
            int imagesMaxHeight = (int) panelDims.getHeight() - BORDER_OFFSET - imagesVerticalOffset;
            boolean smallImages = false;

            panelImages.setPreferredLocation(new Point(0, imagesVerticalOffset));
            panelImages.setBackgroundColor(null);
            panelImages.setWrap(true);
            panelImages.setPreferredSize(new Dimension(2 * ICON_SIZE, 0));
            if (2 * (imagesMaxHeight / ICON_SIZE) < idArray.length)
            {
                smallImages = true;
                panelImages.setPreferredSize(new Dimension(3 * SMALL_ICON_SIZE, 0));
            }

            panelImages.setOrientation(ComponentOrientation.HORIZONTAL);
            for (Integer e : idArray)
            {
                final BufferedImage image = getImage(e, smallImages);
                if (image != null)
                {
                    panelImages.getChildren().add(new ImageComponent(image));
                }
            }

            panelImages.render(graphics);
        }
        return panelDims;
    }

    private BufferedImage getImage(int id, boolean small)
    {
        BufferedImage bim;
        if (id != SpriteID.SPELL_ICE_BARRAGE)
            bim = itemManager.getImage(id);
        else
            bim = spriteManager.getSprite(id, 0);
        if (bim == null)
            return null;
        if (!small)
            return ImageUtil.resizeCanvas(bim, ICON_SIZE, ICON_SIZE);
        if (id != SpriteID.SPELL_ICE_BARRAGE)
            return ImageUtil.resizeImage(bim, SMALL_ICON_SIZE, SMALL_ICON_SIZE);
        return ImageUtil.resizeCanvas(bim, SMALL_ICON_SIZE, SMALL_ICON_SIZE);
    }
}
