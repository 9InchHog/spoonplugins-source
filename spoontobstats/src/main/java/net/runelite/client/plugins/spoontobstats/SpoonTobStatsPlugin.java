package net.runelite.client.plugins.spoontobstats;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Theatre Stats",
        description = "Theatre of Blood room splits and damage",
        tags = {"combat", "raid", "pve", "pvm", "bosses", "timer"},
        enabledByDefault = true,
        conflicts = "Theatre of Blood Stats"
)
public class SpoonTobStatsPlugin extends Plugin {
    private static final DecimalFormat DMG_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.0");
    private static final int THEATRE_OF_BLOOD_ROOM_STATUS = 6447;
    private static final int THEATRE_OF_BLOOD_BOSS_HP = 6448;
    private static final int TOB_LOBBY = 14642;
    private static final int MAIDEN_REGION = 12613;
    private static final int BLOAT_REGION = 13125;
    private static final int NYLOCAS_REGION = 13122;
    private static final int SOTETSEG_REGION = 13123;
    private static final int SOTETSEG_MAZE_REGION = 13379;
    private static final int NYLOCAS_WAVES_TOTAL = 31;
    private static final int TICK_LENGTH = 600;
    private static final int MAIDEN_ID = 25748;
    private static final int BLOAT_ID = 25749;
    private static final int NYLOCAS_ID = 25750;
    private static final int SOTETSEG_ID = 25751;
    private static final int XARPUS_ID = 25752;
    private static final int VERZIK_ID = 22473;
    private static final Pattern MAIDEN_WAVE = Pattern.compile("Wave 'The Maiden of Sugadinti' \\(.*\\) complete!");
    private static final Pattern BLOAT_WAVE = Pattern.compile("Wave 'The Pestilent Bloat' \\(.*\\) complete!Duration: (\\d+):(\\d+)\\.?(\\d+)");
    private static final Pattern NYLOCAS_WAVE = Pattern.compile("Wave 'The Nylocas' \\(.*\\) complete!");
    private static final Pattern SOTETSEG_WAVE = Pattern.compile("Wave 'Sotetseg' \\(.*\\) complete!");
    private static final Pattern XARPUS_WAVE = Pattern.compile("Wave 'Xarpus' \\(.*\\) complete!");
    private static final Pattern VERZIK_WAVE = Pattern.compile("Wave 'The Final Challenge' \\(.*\\) complete!");
    private static final Pattern COMPLETION = Pattern.compile("Theatre of Blood total completion time:");
    private static final Set<Integer> NYLOCAS_IDS = ImmutableSet.of(
            NpcID.NYLOCAS_HAGIOS, NpcID.NYLOCAS_HAGIOS_8347, NpcID.NYLOCAS_HAGIOS_8350, NpcID.NYLOCAS_HAGIOS_8353,
            NpcID.NYLOCAS_HAGIOS_10776, NpcID.NYLOCAS_HAGIOS_10779, NpcID.NYLOCAS_HAGIOS_10782, NpcID.NYLOCAS_HAGIOS_10785,
            NpcID.NYLOCAS_HAGIOS_10793, NpcID.NYLOCAS_HAGIOS_10796, NpcID.NYLOCAS_HAGIOS_10799, NpcID.NYLOCAS_HAGIOS_10802,
            NpcID.NYLOCAS_TOXOBOLOS_8343, NpcID.NYLOCAS_TOXOBOLOS_8346, NpcID.NYLOCAS_TOXOBOLOS_8349, NpcID.NYLOCAS_TOXOBOLOS_8352,
            NpcID.NYLOCAS_TOXOBOLOS_10775, NpcID.NYLOCAS_TOXOBOLOS_10778, NpcID.NYLOCAS_TOXOBOLOS_10781, NpcID.NYLOCAS_TOXOBOLOS_10784,
            NpcID.NYLOCAS_TOXOBOLOS_10792, NpcID.NYLOCAS_TOXOBOLOS_10795, NpcID.NYLOCAS_TOXOBOLOS_10798, NpcID.NYLOCAS_TOXOBOLOS_10801,
            NpcID.NYLOCAS_ISCHYROS_8342, NpcID.NYLOCAS_ISCHYROS_8345, NpcID.NYLOCAS_ISCHYROS_8348, NpcID.NYLOCAS_ISCHYROS_8351,
            NpcID.NYLOCAS_ISCHYROS_10774, NpcID.NYLOCAS_ISCHYROS_10777, NpcID.NYLOCAS_ISCHYROS_10780, NpcID.NYLOCAS_ISCHYROS_10783,
            NpcID.NYLOCAS_ISCHYROS_10791, NpcID.NYLOCAS_ISCHYROS_10794, NpcID.NYLOCAS_ISCHYROS_10797, NpcID.NYLOCAS_ISCHYROS_10800
    );
    private static final Set<Point> NYLOCAS_VALID_SPAWNS = ImmutableSet.of(
            new Point(17, 24), new Point(17, 25), new Point(18, 24), new Point(18, 25),
            new Point(31, 9), new Point(31, 10), new Point(32, 9), new Point(32, 10),
            new Point(46, 24), new Point(46, 25), new Point(47, 24), new Point(47, 25)
    );
    private static final Set<String> BOSS_NAMES = ImmutableSet.of(
            "The Maiden of Sugadinti", "Pestilent Bloat", "Nylocas Vasilias", "Sotetseg", "Xarpus", "Verzik Vitur"
    );

    private static final String MAIDEN = "Maiden";
    private static final String BLOAT = "Bloat";
    private static final String NYLO = "Nylo";
    private static final String SOTETSEG = "Sote";
    private static final String XARPUS = "Xarpus";
    private static final String VERZIK = "Verzik";

    @Inject
    private Client client;

    @Inject
    private SpoonTobStatsConfig config;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    private SpoonTobStatsInfobox maidenInfoBox;
    private SpoonTobStatsInfobox bloatInfoBox;
    private SpoonTobStatsInfobox nyloInfoBox;
    private SpoonTobStatsInfobox soteInfoBox;
    private SpoonTobStatsInfobox xarpusInfoBox;
    private SpoonTobStatsInfobox verzikInfoBox;

    @Inject
    protected OverlayManager overlayManager;

    @Inject
    private SpoonTobStatsOverlay overlay;

    private NavigationButton navButton;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private MaidenFlash flashOverlay;

    @Inject
    private ClientThread clientThread;

    private int prevRegion;
    @Getter
    private boolean tobInside;
    private boolean instanced;
    private boolean preciseTimers;

    private int maidenStartTick = -1;
    private boolean maiden70;
    private int maiden70time;
    private boolean maiden50;
    private int maiden50time;
    private boolean maiden30;
    private int maiden30time;
    private int maidenProcTime;
    @Getter(AccessLevel.PACKAGE)
    @Setter
    private boolean flash = false;

    private int bloatStartTick = -1;

    private int nyloStartTick = -1;
    private int currentNylos;
    private boolean nyloWavesFinished;
    private boolean nyloCleanupFinished;
    private boolean waveThisTick = false;
    private int waveTime;
    private int cleanupTime;
    private int bossSpawnTime;
    private int nyloWave = 0;

    private int soteStartTick = -1;
    private boolean sote66;
    private int sote66time;
    private boolean sote33;
    private int sote33time;

    private int xarpusStartTick = -1;
    private int xarpusAcidTime;
    private int xarpusRecoveryTime;
    private int xarpusPreScreech;
    private int xarpusPreScreechTotal;

    private int verzikStartTick = -1;
    private int verzikP1time;
    private int verzikP2time;
    private double verzikP1personal;
    private double verzikP1total;
    private double verzikP2personal;
    private double verzikP2total;
    private double verzikP2healed;
    private NPC verziknpc;
    boolean verzikRedTimerFlag = false;
    private int verzikRedCrabTime;

    public static final File TIMES_DIR = new File(RuneLite.RUNELITE_DIR.getPath() + File.separator + "times");
    public ArrayList<String> timeFileStr = new ArrayList<>();
    public String mode = "";

    @Getter
    private final Map<String, Integer> room = new HashMap<>();
    @Getter
    private final Map<String, Integer> time = new HashMap<>();
    @Getter
    private final LinkedList <String> phase = new LinkedList<>();
    @Getter
    private final Map<String, Integer> phaseTime = new HashMap<>();
    @Getter
    private final Map<String, Integer> phaseSplit = new HashMap<>();

    private final Map<String, Integer> personalDamage = new HashMap<>();
    private final Map<String, Integer> totalDamage = new HashMap<>();
    private final Map<String, Integer> totalHealing = new HashMap<>();

    @Provides
    SpoonTobStatsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SpoonTobStatsConfig.class);
    }

    @Override
    protected void shutDown() throws Exception {
        resetAll();
        resetAllInfoBoxes();
        overlayManager.remove(overlay);
        overlayManager.remove(flashOverlay);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
        overlayManager.add(flashOverlay);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        int tobVar = client.getVar(Varbits.THEATRE_OF_BLOOD);
        tobInside = tobVar == 2 || tobVar == 3;

        /*boolean ingame_setting = client.getVarbitValue(11866) == 1;
        if (config.preciseTimers() == SpoonTobStatsConfig.PreciseTimersSetting.TICKS
                || (config.preciseTimers() == SpoonTobStatsConfig.PreciseTimersSetting.INGAME_SETTING && ingame_setting)) {
            System.out.println("Varbit Changed: " + preciseTimers);
            preciseTimers = true;
        }*/

        if (!tobInside) {
            resetAll();
        }

        int region = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
        int status = client.getVarbitValue(THEATRE_OF_BLOOD_ROOM_STATUS);

        if (status == 1 && region != prevRegion && region != SOTETSEG_MAZE_REGION) {
            prevRegion = region;
            if (region == BLOAT_REGION) {
                if (bloatStartTick == -1) {
                    bloatStartTick = client.getTickCount();
                    resetTimer();
                    room.put(BLOAT, bloatStartTick);
                }
            }
        }

        int bosshp = client.getVarbitValue(THEATRE_OF_BLOOD_BOSS_HP);

        if (region == TOB_LOBBY) {
            resetMaiden();
            resetBloat();
            resetNylo();
            resetSote();
            resetXarpus();
            resetVerzik();
            resetTimer();
        }
    }

    @Subscribe
    protected void onAnimationChanged(AnimationChanged animationChanged) {
        if (tobInside) {
            int id = animationChanged.getActor().getAnimation();
            if (id == 1816 && soteStartTick != -1) {
                int ticks = client.getTickCount() - soteStartTick;
                String P1 = "P1";
                String P2 = "P2";
                if (phaseTime.get(P1) == null) {
                    phase(P1, ticks, false, SOTETSEG, null);
                    sote66 = true;
                    sote66time = client.getTickCount() - soteStartTick;
                } else if (phaseTime.get(P2) == null && ticks > phaseTime.get(P1) + 10) {
                    phase(P2, ticks, true, SOTETSEG, null);
                    sote33 = true;
                    sote33time = client.getTickCount() - soteStartTick;
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) throws Exception {
        if (tobInside && event.getType() == ChatMessageType.GAMEMESSAGE) {
            String strippedMessage = Text.removeTags(event.getMessage());
            List<String> messages = new ArrayList<>(Collections.emptyList());

            if (MAIDEN_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("The Maiden of Sugadinti", 0);
                double total = totalDamage.getOrDefault("The Maiden of Sugadinti", 0);
                int healed = totalHealing.getOrDefault("The Maiden of Sugadinti", 0);
                String healing = "Total Healing - " + DMG_FORMAT.format(healed);
                double percent = (personal / total) * 100;
                int roomTicks;
                String roomTime = "";
                String splits = "";
                String damage = "";
                messages.clear();

                if (maidenStartTick > 0) {
                    roomTicks = client.getTickCount() - maidenStartTick;
                    phase(MAIDEN, roomTicks, true , MAIDEN, event);
                    roomTime = formatTime(roomTicks);
                    splits = "70% - " + formatTime(maiden70time) +
                            "</br>" +
                            "50% - " + formatTime(maiden50time) + " (" + formatTime(maiden50time - maiden70time) + ")" +
                            "</br>" +
                            "30% - " + formatTime(maiden30time) + " (" + formatTime(maiden30time - maiden50time) + ")" +
                            "</br>" +
                            "Room Complete - " + roomTime + " (" + formatTime(roomTicks - maiden30time) + ")";

                    if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END) {
                        if (config.simpleMessage()) {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "70% - <col=ff0000>" + formatTime(maiden70time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "50% - <col=ff0000>" + formatTime(maiden50time)
                                    + "</col> (<col=ff0000>" + formatTime(maiden50time - maiden70time) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "30% - <col=ff0000>" + formatTime(maiden30time)
                                    + "</col> (<col=ff0000>" + formatTime(maiden30time - maiden50time) + "</col>)", null);
                            timeFileStr.add("70% - " + formatTime(maiden70time));
                            timeFileStr.add("50% - " + formatTime(maiden50time));
                            timeFileStr.add("30% - " + formatTime(maiden30time));
                        } else {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Maiden - 70%' completed! Duration: <col=ff0000>"
                                    + formatTime(maiden70time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Maiden - 50%' completed! Duration: <col=ff0000>"
                                    + formatTime(maiden50time) + "</col> (<col=ff0000>" + formatTime(maiden50time - maiden70time) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Maiden - 30%' completed! Duration: <col=ff0000>"
                                    + formatTime(maiden30time) + "</col> (<col=ff0000>" + formatTime(maiden30time - maiden50time) + "</col>)", null);
                            timeFileStr.add("Wave 'Maiden - 70%' completed! Duration: " + formatTime(maiden70time));
                            timeFileStr.add("Wave 'Maiden - 50%' completed! Duration: " + formatTime(maiden50time));
                            timeFileStr.add("Wave 'Maiden - 30%' completed! Duration: " + formatTime(maiden30time));
                        }
                    }
                }

                if (personal > 0) {
                    damage = "Personal Boss Damage - " + DMG_FORMAT.format(personal);
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Personal Boss Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(percent) + "%)")
                                        .build()
                        );
                    }
                }

                if (config.healMsg()) {
                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("Total Healing - ")
                                    .append(Color.RED, DMG_FORMAT.format(healed))
                                    .build()
                    );
                }

                maidenInfoBox = createInfoBox(MAIDEN_ID, "Maiden", roomTime, DECIMAL_FORMAT.format(percent), damage, splits, healing);
                infoBoxManager.addInfoBox(maidenInfoBox);
                resetMaiden();
            } else if (BLOAT_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("Pestilent Bloat", 0);
                double total = totalDamage.getOrDefault("Pestilent Bloat", 0);
                double percent = (personal / total) * 100;
                Matcher m = BLOAT_WAVE.matcher(strippedMessage);
                String roomTime = "";
                if (m.find()) {
                    if (preciseTimers) {
                        roomTime = m.group(1) + ":" + m.group(2) + "." + m.group(3).charAt(0);
                    } else {
                        roomTime = m.group(1) + ":" + m.group(2);
                    }
                }
                String damage = "";
                messages.clear();

                if (bloatStartTick > 0) {
                    int roomTicks = client.getTickCount() - bloatStartTick;
                    phase(BLOAT, roomTicks, false, BLOAT, event);
                }

                if (personal > 0) {
                    damage = "Personal Boss Damage - " + DMG_FORMAT.format(personal);
                    if (config.dmgMsg())
                    {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Personal Boss Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(percent) + "%)")
                                        .build()
                        );
                    }
                }

                bloatInfoBox = createInfoBox(BLOAT_ID, "Bloat", roomTime, DECIMAL_FORMAT.format(percent), damage, "Room Complete - " + roomTime, "");
                infoBoxManager.addInfoBox(bloatInfoBox);
                resetBloat();
            } else if (NYLOCAS_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("Nylocas Vasilias", 0);
                double total = totalDamage.getOrDefault("Nylocas Vasilias", 0);
                int healed = totalHealing.getOrDefault("Nylocas Vasilias", 0);
                String healing = "Total Healing - " + DMG_FORMAT.format(healed);
                double percent = (personal / total) * 100;
                int roomTicks;
                String roomTime = "";
                String splits = "";
                String damage = "";
                messages.clear();

                if (nyloStartTick > 0) {
                    roomTicks = client.getTickCount() - nyloStartTick;
                    phase(NYLO, roomTicks, true, NYLO, event);
                    roomTime = formatTime(roomTicks);
                    splits = "Waves - " + formatTime(waveTime) +
                            "</br>" +
                            "Cleanup - " + formatTime(cleanupTime) + " (" + formatTime(cleanupTime - waveTime) + ")" +
                            "</br>" +
                            "Boss Spawn - " + formatTime(bossSpawnTime) + " (" + formatTime(bossSpawnTime - cleanupTime) + ")" +
                            "</br>" +
                            "Room Complete - " + roomTime + " (" + formatTime(roomTicks - bossSpawnTime) + ")";

                    if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END) {
                        if (config.simpleMessage()) {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Waves - <col=ff0000>" + formatTime(waveTime) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Cleanup - <col=ff0000>" + formatTime(cleanupTime)
                                    + "</col> (<col=ff0000>" + formatTime(cleanupTime-waveTime) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Boss Spawn - <col=ff0000>" + formatTime(bossSpawnTime)
                                    + "</col> (<col=ff0000>" + formatTime(bossSpawnTime-cleanupTime) + "</col>)", null);
                            timeFileStr.add("Waves - " + formatTime(waveTime));
                            timeFileStr.add("Cleanup - " + formatTime(cleanupTime) + " (" + formatTime(cleanupTime-waveTime) + ")");
                            timeFileStr.add("Boss Spawn - " + formatTime(bossSpawnTime) + " (" + formatTime(bossSpawnTime-cleanupTime) + ")");
                        } else {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Nylo - Waves' completed! Duration: <col=ff0000>"
                                    + formatTime(waveTime) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Nylo - Cleanup' completed! Duration: <col=ff0000>"
                                    + formatTime(cleanupTime) + "</col> (<col=ff0000>" + formatTime(cleanupTime-waveTime) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Nylo - Boss Spawn' completed! Duration: <col=ff0000>"
                                    + formatTime(bossSpawnTime) + "</col> (<col=ff0000>" + formatTime(bossSpawnTime-cleanupTime) + "</col>)", null);
                            timeFileStr.add("Wave 'Nylo - Waves' completed! Duration: " + formatTime(waveTime));
                            timeFileStr.add("Wave 'Nylo - Cleanup' completed! Duration: " + formatTime(cleanupTime) + " (" + formatTime(cleanupTime-waveTime) + ")");
                            timeFileStr.add("Wave 'Nylo - Boss Spawn' completed! Duration: " + formatTime(bossSpawnTime) + " (" + formatTime(bossSpawnTime-cleanupTime) + ")");
                        }
                    }
                }

                if (personal > 0) {
                    damage = "Personal Boss Damage - " + DMG_FORMAT.format(personal);
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Personal Boss Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(percent) + "%)")
                                        .build()
                        );
                    }
                }

                if (config.healMsg()) {
                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("Total Healing - ")
                                    .append(Color.RED, DMG_FORMAT.format(healed))
                                    .build()
                    );
                }

                nyloInfoBox = createInfoBox(NYLOCAS_ID, "Nylocas", roomTime, DECIMAL_FORMAT.format(percent), damage, splits, healing);
                infoBoxManager.addInfoBox(nyloInfoBox);
                resetNylo();
            } else if (SOTETSEG_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("Sotetseg", 0);
                double total = totalDamage.getOrDefault("Sotetseg", 0);
                double percent = (personal / total) * 100;
                int roomTicks;
                String roomTime = "";
                String splits = "";
                String damage = "";
                messages.clear();

                if (soteStartTick > 0) {
                    roomTicks = client.getTickCount() - soteStartTick;
                    phase(SOTETSEG, roomTicks, true, SOTETSEG, event);
                    roomTime = formatTime(roomTicks);
                    splits = "66% - " + formatTime(sote66time) +
                            "</br>" +
                            "33% - " + formatTime(sote33time) + " (" + formatTime(sote33time - sote66time) + ")" +
                            "</br>" +
                            "Room Complete - " + roomTime + " (" + formatTime(roomTicks - sote33time) + ")";

                    if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END) {
                        if (config.simpleMessage()) {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P1 - <col=ff0000>" + formatTime(sote66time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P2 - <col=ff0000>" + formatTime(sote33time - sote66time)
                                    + "</col> (<col=ff0000>" + formatTime(sote33time) + "</col>)", null);
                            timeFileStr.add("P1 - " + formatTime(sote66time));
                            timeFileStr.add("P2 - " + formatTime(sote33time - sote66time) + " (" + formatTime(sote33time) + ")");
                        } else {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Sote - P1' completed!" + " Duration: <col=ff0000>"
                                    + formatTime(sote66time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Sote - P2' completed!" + " Duration: <col=ff0000>"
                                    + formatTime(sote33time) + "</col> (<col=ff0000>" + formatTime(sote33time - sote66time) + "</col>)", null);
                            timeFileStr.add("Wave 'Sote - P1' completed!" + " Duration: " + formatTime(sote66time));
                            timeFileStr.add("Wave 'Sote - P2' completed!" + " Duration: " + formatTime(sote33time) + " (" + formatTime(sote33time - sote66time) + ")");
                        }
                    }
                }

                if (personal > 0) {
                    damage = "Personal Boss Damage - " + DMG_FORMAT.format(personal);
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Personal Boss Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(percent) + "%)")
                                        .build()
                        );
                    }
                }

                soteInfoBox = createInfoBox(SOTETSEG_ID, "Sotetseg", roomTime, DECIMAL_FORMAT.format(percent), damage, splits, "");
                infoBoxManager.addInfoBox(soteInfoBox);
                resetSote();
            } else if (XARPUS_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("Xarpus", 0);
                double total = totalDamage.getOrDefault("Xarpus", 0);
                int healed = totalHealing.getOrDefault("Xarpus", 0);
                String healing = "Total Healing - " + DMG_FORMAT.format(healed);
                double xarpusPostScreech = personal - xarpusPreScreech;
                double personalPercent = (personal / total) * 100;
                double preScreechPercent = ((double) xarpusPreScreech / (double) xarpusPreScreechTotal) * 100;
                double postScreechPercent = (xarpusPostScreech / (total - xarpusPreScreechTotal)) * 100;
                int roomTicks;
                String roomTime = "";
                String splits = "";
                String damage = "";
                messages.clear();

                if (xarpusStartTick > 0) {
                    roomTicks = client.getTickCount() - xarpusStartTick;
                    phase(XARPUS, roomTicks, true, XARPUS, event);
                    roomTime = formatTime(roomTicks);
                    splits = "Recovery Phase - " + formatTime(xarpusRecoveryTime) +
                            "</br>" +
                            "Screech Time - " + formatTime(xarpusAcidTime) + " (" + formatTime(xarpusAcidTime - xarpusRecoveryTime) + ")" +
                            "</br>" +
                            "Room Complete - " + roomTime + " (" + formatTime(roomTicks - xarpusAcidTime) + ")";

                    if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END) {
                        if (config.simpleMessage()){
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Recovery - <col=ff0000>" + formatTime(xarpusRecoveryTime) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Acid - <col=ff0000>" + formatTime(xarpusAcidTime)
                                    + "</col> (<col=ff0000>" + formatTime(xarpusAcidTime - xarpusRecoveryTime) + "</col>)", null);
                            timeFileStr.add("Recovery - " + formatTime(xarpusRecoveryTime));
                            timeFileStr.add("Acid - " + formatTime(xarpusAcidTime) + " (" + formatTime(xarpusAcidTime - xarpusRecoveryTime) + ")");
                        } else {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Xarpus - Recovery' completed! Duration: <col=ff0000>"
                                    + formatTime(xarpusRecoveryTime) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Xarpus - Acid' completed! Duration: <col=ff0000>"
                                    + formatTime(xarpusAcidTime) + "</col> (<col=ff0000>" + formatTime(xarpusAcidTime - xarpusRecoveryTime) + "</col>)", null);
                            timeFileStr.add("Wave 'Xarpus - Recovery' completed! Duration: " + formatTime(xarpusRecoveryTime));
                            timeFileStr.add("Wave 'Xarpus - Acid' completed! Duration: " + formatTime(xarpusAcidTime) + " (" + formatTime(xarpusAcidTime - xarpusRecoveryTime) + ")");
                        }
                    }
                }

                if (xarpusPreScreech > 0) {
                    damage += "Pre Screech Damage - " + DMG_FORMAT.format(xarpusPreScreech) + " (" + DECIMAL_FORMAT.format(preScreechPercent) + "%)" + "</br>";
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Pre Screech Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(xarpusPreScreech) + " (" + DECIMAL_FORMAT.format(preScreechPercent) + "%)")
                                        .build()
                        );
                    }
                }

                if (xarpusPostScreech > 0) {
                    damage += "Post Screech Damage - " + DMG_FORMAT.format(xarpusPostScreech) + " (" + DECIMAL_FORMAT.format(postScreechPercent) + "%)" + "</br>";
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Post Screech Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(xarpusPostScreech) + " (" + DECIMAL_FORMAT.format(postScreechPercent) + "%)")
                                        .build()
                        );
                    }
                }

                if (personal > 0) {
                    damage += "Personal Boss Damage - " + DMG_FORMAT.format(personal);
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Personal Boss Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(personalPercent) + "%)")
                                        .build()
                        );
                    }
                }

                if (config.healMsg()) {
                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("Total Healed - ")
                                    .append(Color.RED, DMG_FORMAT.format(healed))
                                    .build()
                    );
                }

                xarpusInfoBox = createInfoBox(XARPUS_ID, "Xarpus", roomTime, DECIMAL_FORMAT.format(personalPercent), damage, splits, healing);
                infoBoxManager.addInfoBox(xarpusInfoBox);
                resetXarpus();
            } else if (VERZIK_WAVE.matcher(strippedMessage).find()) {
                double personal = personalDamage.getOrDefault("Verzik Vitur", 0) ;
                double total = totalDamage.getOrDefault("Verzik Vitur", 0);
                double p3personal = personalDamage.getOrDefault("Verzik Vitur", 0) - (verzikP1personal + verzikP2personal);
                double p3total = totalDamage.getOrDefault("Verzik Vitur", 0) - (verzikP1total + verzikP2total);
                double p3healed = totalHealing.getOrDefault("Verzik Vitur", 0) - verzikP2healed;
                double healed = totalHealing.getOrDefault("Verzik Vitur", 0);
                double p3percent = (p3personal / p3total) * 100;
                double p1percent = (verzikP1personal / verzikP1total) * 100;
                double p2percent = (verzikP2personal / verzikP2total) * 100;
                double percent = (personal / total) * 100;
                int roomTicks;
                String roomTime = "";
                String splits = "";
                String damage = "";
                String healing = "P2 Healed - " + DMG_FORMAT.format(verzikP2healed) +
                        "</br>" +
                        "P3 Healed - " + DMG_FORMAT.format(p3healed) +
                        "</br>" +
                        "Total Healed - " + DMG_FORMAT.format(healed);
                messages.clear();

                if (verzikStartTick > 0) {
                    roomTicks = client.getTickCount() - verzikStartTick;
                    phase(VERZIK, roomTicks, true, VERZIK, event);
                    roomTime = formatTime(roomTicks);
                    splits = "P1 - " + formatTime(verzikP1time) +
                            "</br>" +
                            "P2 - " + formatTime(verzikP2time) + " (" + formatTime(verzikP2time - verzikP1time) + ")" +
                            "</br>" +
                            "P3 - " + roomTime + " (" + formatTime(roomTicks - verzikP2time) + ")";

                    if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END) {
                        if (config.simpleMessage()) {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P1 - <col=ff0000>" + formatTime(verzikP1time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Reds - <col=ff0000>" + formatTime(verzikRedCrabTime)
                                    + "</col> (<col=ff0000>" + formatTime(verzikRedCrabTime - verzikP1time) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P2 - <col=ff0000>" + formatTime(verzikP2time - verzikP1time)
                                    + "</col> (<col=ff0000>" + formatTime(verzikP2time) + "</col>)", null);
                            timeFileStr.add("P1 - " + formatTime(verzikP1time));
                            timeFileStr.add("Reds - " + formatTime(verzikRedCrabTime) + " (" + formatTime(verzikRedCrabTime - verzikP1time) + ")");
                            timeFileStr.add("P2 - " + formatTime(verzikP2time - verzikP1time) + " (" + formatTime(verzikP2time) + ")");
                        } else {
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Verzik - P1' completed! Duration: <col=ff0000>"
                                    + formatTime(verzikP1time) + "</col>", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Verzik - Reds' completed! Duration: <col=ff0000>"
                                    + formatTime(verzikRedCrabTime) + "</col> (<col=ff0000>" + formatTime(verzikRedCrabTime - verzikP1time) + "</col>)", null);
                            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Wave 'Verzik - P2' completed! Duration: <col=ff0000>"
                                    + formatTime(verzikP2time) + "</col> (<col=ff0000>" + formatTime(verzikP2time - verzikP1time) + "</col>)", null);
                            timeFileStr.add("Wave 'Verzik - P1' completed! Duration: " + formatTime(verzikP1time));
                            timeFileStr.add("Wave 'Verzik - Reds' completed! Duration: " + formatTime(verzikRedCrabTime) + " (" + formatTime(verzikRedCrabTime - verzikP1time) + ")");
                            timeFileStr.add("Wave 'Verzik - P2' completed! Duration: " + formatTime(verzikP2time) + " (" + formatTime(verzikP2time - verzikP1time) + ")");
                        }
                    }
                }

                if (verzikP1personal > 0) {
                    damage += "P1 Personal Damage - " + DMG_FORMAT.format(verzikP1personal) + " (" + DECIMAL_FORMAT.format(p1percent) + "%)" + "</br>";
                }

                if (verzikP2personal > 0) {
                    damage += "P2 Personal Damage - " + DMG_FORMAT.format(verzikP2personal) + " (" + DECIMAL_FORMAT.format(p2percent) + "%)" + "</br>";
                }

                if (p3personal > 0) {
                    damage += "P3 Personal Damage - " + DMG_FORMAT.format(p3personal) + " (" + DECIMAL_FORMAT.format(p3percent) + "%)" + "</br>";
                }

                if (personal > 0) {
                    damage += "Total Personal Damage - " + DMG_FORMAT.format(personal);
                }

                verzikInfoBox = createInfoBox(VERZIK_ID, "Verzik", roomTime, DECIMAL_FORMAT.format(percent), damage, splits, healing);
                infoBoxManager.addInfoBox(verzikInfoBox);
                resetVerzik();
            }else if (strippedMessage.contains("Your completed Theatre of Blood: ") && strippedMessage.contains(" count is:")){
                timeFileStr.add(0, strippedMessage);
                if(config.timeExporter() && !mode.equals("")) {
                    exportTimes();
                }
            } else if (strippedMessage.contains("You enter the Theatre of Blood (") && strippedMessage.contains("Mode)...")){
                if(strippedMessage.contains("(Entry Mode)")){
                    mode = "SM";
                }else if(strippedMessage.contains("(Normal Mode)")){
                    mode = "REG";
                }else if(strippedMessage.contains("(Hard Mode)")){
                    mode = "HM";
                }
            }

            if(config.oldRoomMsg() && event.getMessage().contains("Wave '") && event.getMessage().contains(" Mode) complete!<br>Duration: <col=ff0000>")){
                if(Text.removeTags(event.getMessage()).contains("(Hard Mode)")){
                    event.getMessageNode().setValue(event.getMessageNode().getValue().replace("(Hard Mode) complete!<br>", "complete! "));
                    timeFileStr.add(Text.removeTags(event.getMessageNode().getValue().replace("(Hard Mode) complete!<br>", "complete! ")));
                }else if(Text.removeTags(event.getMessage()).contains("(Entry Mode)")){
                    event.getMessageNode().setValue(event.getMessageNode().getValue().replace("(Entry Mode) complete!<br>", "complete! "));
                    timeFileStr.add(Text.removeTags(event.getMessageNode().getValue().replace("(Entry Mode) complete!<br>", "complete! ")));
                }else if(Text.removeTags(event.getMessage()).contains("(Normal Mode)")){
                    event.getMessageNode().setValue(event.getMessageNode().getValue().replace("(Normal Mode) complete!<br>", "complete! "));
                    timeFileStr.add(Text.removeTags(event.getMessageNode().getValue().replace("(Normal Mode) complete!<br>", "complete! ")));
                }
            }

            if (!messages.isEmpty()) {
                for (String m : messages) {
                    chatMessageManager.queue(QueuedMessage.builder()
                            .type(ChatMessageType.GAMEMESSAGE)
                            .runeLiteFormattedMessage(m)
                            .build());
                }
                messages.clear();
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (!tobInside) {
            return;
        }

        List<String> messages = new ArrayList<>(Collections.emptyList());

        NPC npc = event.getNpc();
        int npcId = npc.getId();

        switch (npcId) {
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10815:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10823:
            {
                if (maidenStartTick != -1 && !maiden70)
                {
                    maiden70 = true;
                    maiden70time = client.getTickCount() - maidenStartTick;
                    maidenProcTime = client.getTickCount();
                    phase("70%", maiden70time, false, MAIDEN, null);
                }
                break;
            }
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10816:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10824:
            {
                if (maidenStartTick != -1 && !maiden50)
                {
                    maiden50 = true;
                    maiden50time = client.getTickCount() - maidenStartTick;
                    maidenProcTime = client.getTickCount();
                    phase("50%", maiden50time, true, MAIDEN, null);
                    if ((maiden50time - maiden70time) * TICK_LENGTH < 10600)
                    {
                        flash = true;
                    }
                }
                break;
            }
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10817:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10825:
            {
                if (maidenStartTick != -1 && !maiden30)
                {
                    maiden30 = true;
                    maiden30time = client.getTickCount() - maidenStartTick;
                    maidenProcTime = client.getTickCount();
                    phase("30%", maiden30time, true, MAIDEN, null);
                    if ((maiden30time - maiden50time) * TICK_LENGTH < 10600)
                    {
                        flash = true;
                    }
                }
                break;
            }
            case NpcID.SOTETSEG_8388:
            case NpcID.SOTETSEG_10865:
            case NpcID.SOTETSEG_10868:
                if (soteStartTick == -1) {
                    soteStartTick = client.getTickCount();
                    resetTimer();
                    room.put(SOTETSEG, soteStartTick);
                }
                break;
            case NpcID.XARPUS_8339:
            case NpcID.XARPUS_10767:
            case NpcID.XARPUS_10771:
                xarpusStartTick = client.getTickCount();
                resetTimer();
                room.put(XARPUS, xarpusStartTick);

                break;
            case NpcID.XARPUS_8340:
            case NpcID.XARPUS_10768:
            case NpcID.XARPUS_10772:
                xarpusRecoveryTime = client.getTickCount() - xarpusStartTick;
                phase("Recovery", xarpusRecoveryTime, false, XARPUS, null);
                break;
            case NpcID.VERZIK_VITUR_8370:
            case NpcID.VERZIK_VITUR_10831:
            case NpcID.VERZIK_VITUR_10848:
                verzikStartTick = client.getTickCount();
                resetTimer();
                room.put(VERZIK, verzikStartTick);
                break;
            case NpcID.VERZIK_VITUR_8375:
            case NpcID.VERZIK_VITUR_10836:
            case NpcID.VERZIK_VITUR_10853:
                double personal = personalDamage.getOrDefault("Verzik Vitur", 0) ;
                double total = totalDamage.getOrDefault("Verzik Vitur", 0);
                double p3personal = personalDamage.getOrDefault("Verzik Vitur", 0) - (verzikP1personal + verzikP2personal);
                double p3total = totalDamage.getOrDefault("Verzik Vitur", 0) - (verzikP1total + verzikP2total);
                double p3healed = totalHealing.getOrDefault("Verzik Vitur", 0) - verzikP2healed;
                double healed = totalHealing.getOrDefault("Verzik Vitur", 0);
                double p3percent = (p3personal / p3total) * 100;
                double percent = (personal / total) * 100;

                if (p3personal > 0) {
                    if (config.dmgMsg()) {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("P3 Personal Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(p3personal) + " (" + DECIMAL_FORMAT.format(p3percent) + "%)")
                                        .build()
                        );
                    }
                }

                if (personal > 0)
                {
                    if (config.dmgMsg())
                    {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("Total Personal Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(personal) + " (" + DECIMAL_FORMAT.format(percent) + "%)")
                                        .build()
                        );
                    }
                }
                if (config.healMsg())
                {
                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("P3 Healed - ")
                                    .append(Color.RED, DMG_FORMAT.format(p3healed))
                                    .build()
                    );

                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("Total Healed - ")
                                    .append(Color.RED, DMG_FORMAT.format(healed))
                                    .build()
                    );
                }
                break;
        }
        if (!messages.isEmpty()) {
            for (String m : messages) {
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.GAMEMESSAGE)
                        .runeLiteFormattedMessage(m)
                        .build());
            }
            messages.clear();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        if (!tobInside)
        {
            return;
        }

        List<String> messages = new ArrayList<>(Collections.emptyList());

        NPC npc = event.getNpc();
        int npcId = npc.getId();
        if (npc.getName() != null && npc.getName().equals("Verzik Vitur"))
        {
            verziknpc = npc;
        }

        switch (npcId)
        {
            case NpcID.THE_MAIDEN_OF_SUGADINTI:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10814:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_10822:
                maidenStartTick = client.getTickCount();
                resetTimer();
                room.put(MAIDEN, maidenStartTick);
                break;
            case NullNpcID.NULL_8358:
            case NullNpcID.NULL_10790:
            case NullNpcID.NULL_10811:
                nyloStartTick = client.getTickCount();
                resetTimer();
                room.put(NYLO, nyloStartTick);
                break;
            case NpcID.NYLOCAS_VASILIAS:
            case NpcID.NYLOCAS_VASILIAS_10786:
            case NpcID.NYLOCAS_VASILIAS_10807:
                bossSpawnTime = client.getTickCount() - nyloStartTick;
                phase("Boss Spawn", bossSpawnTime, true, NYLO, null);
                break;
            case NpcID.VERZIK_VITUR_8371:
            case NpcID.VERZIK_VITUR_10832:
            case NpcID.VERZIK_VITUR_10849:
                verzikP1time = client.getTickCount() - verzikStartTick;
                verzikP1personal = personalDamage.getOrDefault("Verzik Vitur", 0);
                verzikP1total = totalDamage.getOrDefault("Verzik Vitur", 0);
                phase("P1", verzikP1time, false, VERZIK, null);

                double p1percent = (verzikP1personal / verzikP1total) * 100;
                messages.clear();
                if (verzikP1personal > 0) {
                    if (config.dmgMsg())
                    {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("P1 Personal Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(verzikP1personal) + " (" + DECIMAL_FORMAT.format(p1percent) + "%)")
                                        .build()
                        );
                    }
                }
                break;
            case NpcID.VERZIK_VITUR_8373:
            case NpcID.VERZIK_VITUR_10834:
            case NpcID.VERZIK_VITUR_10851:
                verzikP2time = client.getTickCount() - verzikStartTick;
                verzikP2personal = personalDamage.getOrDefault("Verzik Vitur", 0) - verzikP1personal;
                verzikP2total = totalDamage.getOrDefault("Verzik Vitur", 0) - verzikP1total;
                verzikP2healed = totalHealing.getOrDefault("Verzik Vitur", 0);
                phase.remove("Reds");
                phase("P2", verzikP2time, true, VERZIK, null);

                double p2percent = (verzikP2personal / verzikP2total) * 100;
                messages.clear();
                if (verzikP2personal > 0)
                {
                    if (config.dmgMsg())
                    {
                        messages.add(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.NORMAL)
                                        .append("P2 Personal Damage - ")
                                        .append(Color.RED, DMG_FORMAT.format(verzikP2personal) + " (" + DECIMAL_FORMAT.format(p2percent) + "%)")
                                        .build()
                        );
                    }
                }
                if (config.healMsg())
                {
                    messages.add(
                            new ChatMessageBuilder()
                                    .append(ChatColorType.NORMAL)
                                    .append("P2 Healed - ")
                                    .append(Color.RED, DMG_FORMAT.format(verzikP2healed))
                                    .build()
                    );
                }
                break;
        }

        if (!messages.isEmpty())
        {
            for (String m : messages)
            {
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.GAMEMESSAGE)
                        .runeLiteFormattedMessage(m)
                        .build());
            }
            messages.clear();
        }

        if (!NYLOCAS_IDS.contains(npcId) || prevRegion != NYLOCAS_REGION)
        {
            return;
        }

        currentNylos++;
        WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
        Point spawnLoc = new Point(worldPoint.getRegionX(), worldPoint.getRegionY());

        if (!NYLOCAS_VALID_SPAWNS.contains(spawnLoc))
        {
            return;
        }

        if (!waveThisTick)
        {
            nyloWave++;
            waveThisTick = true;
        }

        if (nyloWave == NYLOCAS_WAVES_TOTAL && !nyloWavesFinished)
        {
            waveTime = client.getTickCount() - nyloStartTick;
            nyloWavesFinished = true;
            phase("Waves", waveTime, false, NYLO, null);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (!tobInside)
        {
            return;
        }

        NPC npc = event.getNpc();
        int npcId = npc.getId();

        if (!NYLOCAS_IDS.contains(npcId) || prevRegion != NYLOCAS_REGION)
        {
            return;
        }

        currentNylos--;

        if (nyloWavesFinished && !nyloCleanupFinished && currentNylos == 0)
        {
            cleanupTime = client.getTickCount() - nyloStartTick;
            nyloCleanupFinished = true;
            phase("Cleanup", cleanupTime, true, NYLO, null);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!tobInside) {
            return;
        }

        if (waveThisTick) {
            waveThisTick = false;
        }

        if (verziknpc != null) {
            if (verziknpc.getAnimation() == 8117 && !verzikRedTimerFlag) {
                verzikRedCrabTime = client.getTickCount() - verzikStartTick;
                phase("Reds", verzikRedCrabTime, true, VERZIK, null);
                verzikRedTimerFlag = true;
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (tobInside) {
            boolean ingame_setting = client.getVarbitValue(11866) == 1;
            preciseTimers = config.preciseTimers() == SpoonTobStatsConfig.PreciseTimersSetting.TICKS
                    || (config.preciseTimers() == SpoonTobStatsConfig.PreciseTimersSetting.INGAME_SETTING && ingame_setting);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOADING) {
            return;
        }

        boolean prevInstance = instanced;
        instanced = client.isInInstancedRegion();
        if (prevInstance && !instanced) {
            resetAll();
            resetAllInfoBoxes();
            resetTimer();
        } else if (!prevInstance && instanced) {
            resetAll();
            resetAllInfoBoxes();
            resetTimer();
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (!tobInside) {
            return;
        }

        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) {
            return;
        }

        NPC npc = (NPC) actor;
        String npcName = npc.getName();
        if (npcName == null || !(BOSS_NAMES.contains(npcName))) {
            return;
        }

        npcName = Text.removeTags(npcName);
        Hitsplat hitsplat = event.getHitsplat();

        if (hitsplat.isMine()) {
            int myDmg = personalDamage.getOrDefault(npcName, 0);
            int totalDmg = totalDamage.getOrDefault(npcName, 0);
            myDmg += hitsplat.getAmount();
            totalDmg += hitsplat.getAmount();
            personalDamage.put(npcName, myDmg);
            totalDamage.put(npcName, totalDmg);
        } else if (hitsplat.isOthers()) {
            int totalDmg = totalDamage.getOrDefault(npcName, 0);
            totalDmg += hitsplat.getAmount();
            totalDamage.put(npcName, totalDmg);
        } else if (hitsplat.getHitsplatType() == Hitsplat.HitsplatType.HEAL) {
            int healed = totalHealing.getOrDefault(npcName, 0);
            healed += hitsplat.getAmount();
            totalHealing.put(npcName, healed);
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        Actor npc = event.getActor();
        if (!(npc instanceof NPC) || !tobInside) {
            return;
        }

        String overheadText = event.getOverheadText();
        String npcName = npc.getName();
        if (npcName != null && npcName.equals("Xarpus") && overheadText.equals("Screeeeech!")) {
            xarpusAcidTime = client.getTickCount() - xarpusStartTick;
            xarpusPreScreech = personalDamage.getOrDefault(npcName, 0);
            xarpusPreScreechTotal = totalDamage.getOrDefault(npcName, 0);
            phase("Acid", xarpusAcidTime, true, XARPUS, null);
        }
    }

    private SpoonTobStatsInfobox createInfoBox(int itemId, String room, String time, String percent, String damage, String splits, String healed) {
        BufferedImage image = itemManager.getImage(itemId);
        return new SpoonTobStatsInfobox(image, config, this, room, time, percent, damage, splits, healed);
    }

    public String formatTime(int ticks) {
        int millis = ticks * TICK_LENGTH;
        String hundredths = String.valueOf(millis % 1000).substring(0, 1);

        if (preciseTimers)
        {
            return String.format("%d:%02d.%s",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1),
                    hundredths);
        }
        else
        {
            if (hundredths.equals("6") || hundredths.equals("8"))
            {
                millis += 1000;
            }
            return String.format("%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }
    }

    public static String to_mmss_precise(int ticks) {
        int min = ticks / 100;
        int tmp = (ticks - min * 100) * 6;
        int sec = tmp / 10;
        int sec_tenth = tmp - sec * 10;
        String timeStr = min + (sec < 10 ? ":0" : ":") + sec + "." + sec_tenth;
        return timeStr;
    }

    public static String to_mmss(int ticks) {
        int m = ticks / 100;
        int s = (ticks - m * 100) * 6 / 10;
        String timeStr = m + (s < 10 ? ":0" : ":") + s;
        return timeStr;
    }

    private void phase(String name, int ticks, boolean splitPhase, String boss, ChatMessage event) {
        if (splitPhase && !phase.isEmpty()) {
            phaseSplit.put(name, ticks - phaseTime.get(phase.getLast()));
        }

        if (!name.equals(boss)) {
            phaseTime.put(name, ticks);
            phase.add(name);

            if (config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ACTIVE) {
                if (config.simpleMessage()) {
                    printTime(ticks, name, phaseSplit.getOrDefault(name, 0));
                } else {
                    printTime(ticks, boss + " - " + name, phaseSplit.getOrDefault(name, 0));
                }
            }
        } else {
            time.put(name, ticks);

            if ((config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ACTIVE || config.msgTiming() == SpoonTobStatsConfig.msgTimeMode.ROOM_END)
                    && !phase.isEmpty() && event != null) {
                String string = event.getMessage();
                String[] message = string.split("(?=</col>)", 2);
                String startMessage = message[0];
                String endMessage = message[1];
                event.getMessageNode().setValue(startMessage + "</col> (<col=ff0000>" + formatTime(ticks - phaseTime.get(phase.getLast())) + "</col>)" + endMessage);
                timeFileStr.add(Text.removeTags(startMessage + "</col> (<col=ff0000>" + formatTime(ticks - phaseTime.get(phase.getLast())) + "</col>)" + endMessage));
            }
        }
    }

    private void printTime(int ticks, String subject, int splitTicks) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.simpleMessage()){
            stringBuilder.append(subject).append(" - " ).append("<col=ff0000>").append(formatTime(ticks));
        } else {
            stringBuilder.append("Wave '").append(subject).append("' complete! Duration: <col=ff0000>").append(formatTime(ticks));
        }
        if (splitTicks > 0) {
            stringBuilder.append("</col> (<col=ff0000>").append(formatTime(splitTicks)).append("</col>)");
        }
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", stringBuilder.toString(), "", false);
        timeFileStr.add(Text.removeTags(stringBuilder.toString()));
    }

    private void resetMaiden() {
        maidenStartTick = -1;
        maiden70 = false;
        maiden70time = 0;
        maiden50 = false;
        maiden50time = 0;
        maiden30 = false;
        maiden30time = 0;
        personalDamage.remove("The Maiden of Sugadinti");
        totalDamage.remove("The Maiden of Sugadinti");
        totalHealing.remove("The Maiden of Sugadinti");
        maidenProcTime = 0;
    }

    private void resetBloat() {
        personalDamage.remove("Pestilent Bloat");
        totalDamage.remove("Pestilent Bloat");
        bloatStartTick = -1;
    }

    private void resetNylo() {
        nyloStartTick = -1;
        currentNylos = 0;
        nyloWavesFinished = false;
        nyloCleanupFinished = false;
        waveTime = 0;
        cleanupTime = 0;
        bossSpawnTime = 0;
        waveThisTick = false;
        nyloWave = 0;
        personalDamage.remove("Nylocas Vasilias");
        totalDamage.remove("Nylocas Vasilias");
    }

    private void resetSote() {
        soteStartTick = -1;
        sote66 = false;
        sote66time = 0;
        sote33 = false;
        sote33time = 0;
        personalDamage.remove("Sotetseg");
        totalDamage.remove("Sotetseg");
    }

    private void resetXarpus() {
        xarpusStartTick = -1;
        xarpusRecoveryTime = 0;
        xarpusAcidTime = 0;
        xarpusPreScreech = 0;
        xarpusPreScreechTotal = 0;
        personalDamage.remove("Xarpus");
        totalDamage.remove("Xarpus");
        totalHealing.remove("Xarpus");
    }

    private void resetVerzik() {
        verzikStartTick = -1;
        verzikP1time = 0;
        verzikP2time = 0;
        verzikP1personal = 0;
        verzikP1total = 0;
        verzikP2personal = 0;
        verzikP2total = 0;
        verzikP2healed = 0;
        personalDamage.clear();
        totalDamage.clear();
        totalHealing.clear();
        verzikRedCrabTime = 0;
        verzikRedTimerFlag = false;
    }

    private void resetTimer() {
        room.clear();
        time.clear();
        phase.clear();
        phaseTime.clear();
        phaseSplit.clear();
    }

    private void resetAll() {
        resetMaiden();
        resetBloat();
        resetNylo();
        resetSote();
        resetXarpus();
        resetVerzik();
        timeFileStr.clear();
        mode = "";
    }

    private void resetAllInfoBoxes() {
        infoBoxManager.removeInfoBox(maidenInfoBox);
        infoBoxManager.removeInfoBox(bloatInfoBox);
        infoBoxManager.removeInfoBox(nyloInfoBox);
        infoBoxManager.removeInfoBox(soteInfoBox);
        infoBoxManager.removeInfoBox(xarpusInfoBox);
        infoBoxManager.removeInfoBox(verzikInfoBox);
    }

    private void exportTimes() throws Exception {
        String fileName = TIMES_DIR + "\\" + client.getLocalPlayer().getName() + "_" + mode + "_TobTimes.txt";
        FileWriter writer = new FileWriter(fileName, true);
        try {
            for (String msg : timeFileStr){
                writer.write(msg + "\r\n");
            }
            writer.write("------------------------------------------------------------------------------------------------\r\n" +
                    "------------------------------------------------------------------------------------------------\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.close();
    }
}
