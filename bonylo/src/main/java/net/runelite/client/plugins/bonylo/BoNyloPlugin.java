package net.runelite.client.plugins.bonylo;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

@Extension
@PluginDescriptor(
        name = "[Bo] Nylo",
        description = "Nylo extras - coded by Boak",
        tags = {"tob", "bonylo", "nylo", "big", "Boak"}
)
@PluginDependency(SpoonTobPlugin.class)
public class BoNyloPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(BoNyloPlugin.class);

    private String credit = "CODED BY BOAK";

    private String credit2 = "BIG THANKS TO LOSING_TICKS, CAPS and BLUELITE DEV COMMUNITY";

    private ArrayList<NyloContainer> bigNylos = new ArrayList<>();

    public ArrayList<NyloContainer> getBigNylos() {
        return bigNylos;
    }

    private ArrayList<NPC> test = new ArrayList<>();

    public ArrayList<NPC> getTest() {
        return test;
    }

    private ArrayList<NPC> test2 = new ArrayList<>();

    public ArrayList<NPC> getTest2() {
        return test2;
    }

    private ArrayList<NPC> nyloBoss = new ArrayList<>();

    private boolean pillarsSpawned;

    private int wave;

    private int lastWaveSpawnedTick;

    private int pillars;

    private int rangeSplits;

    private int mageSplits;

    private int meleeSplits;

    private int totalSplits;

    private int rangeBoss;

    private int mageBoss;

    private int meleeBoss;

    private int preRangeSplits;

    private int preMeleeSplits;

    private int preMageSplits;

    private int postRangeSplits;

    private int postMeleeSplits;

    private int postMageSplits;

    private int cleanupRangeSplits;

    private int cleanupMeleeSplits;

    private int cleanupMageSplits;

    public ArrayList<NPC> getNyloBoss() {
        return nyloBoss;
    }

    public int getWave() {
        return this.wave;
    }

    private int lastBossId = 1;

    private String splitMessage;

    private String splitMessage2;

    private String bossMessage;

    private boolean bossAlive = false;

    private int debugCounter = 0;

    public void setDebugCounter(int debugCounter) {
        this.debugCounter = debugCounter;
    }

    public int getDebugCounter() {
        return debugCounter;
    }

    private Color testShadowColor = Color.BLACK;

    public Color getTestShadowColor() {
        return testShadowColor;
    }

    private boolean inNylo = false;

    public boolean isInNylo() {
        return inNylo;
    }

    private boolean inNyloPrev = false;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BoNyloOverlay overlay;

    @Inject
    private BoNyloTileOverlay tileOverlay;

    @Inject
    private BoNyloConfig config;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private SpoonTobConfig spoonTobConfig;

    public boolean isInNyloPrev() {
        return inNyloPrev;
    }

    public boolean mirrorMode;

    @Provides
    BoNyloConfig provideConfig(ConfigManager configManager) {
        return (BoNyloConfig)configManager.getConfig(BoNyloConfig.class);
    }

    protected void startUp() throws Exception {
        spoonTobConfig = configManager.getConfig(SpoonTobConfig.class);
        if (config.enableDebug()) {
            overlayManager.add(overlay);
            overlayManager.add(tileOverlay);
        }
        if(config.useSpoonTob()) {
            config.setEnableBoxRange(spoonTobConfig.getHighlightRangeNylo());
            config.setEnableBoxMage(spoonTobConfig.getHighlightMageNylo());
            config.setEnableBoxMelee(spoonTobConfig.getHighlightMeleeNylo());
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("bonylo")) {
            if (event.getKey().equals("enableDebug")) {
                if (config.enableDebug()) {
                    overlayManager.add(overlay);
                    overlayManager.add(tileOverlay);
                } else {
                    overlayManager.remove(overlay);
                    overlayManager.remove(tileOverlay);
                }
            } else if(event.getKey().equals("useSpoonTob") && config.useSpoonTob()) {
                config.setEnableBoxRange(spoonTobConfig.getHighlightRangeNylo());
                config.setEnableBoxMage(spoonTobConfig.getHighlightMageNylo());
                config.setEnableBoxMelee(spoonTobConfig.getHighlightMeleeNylo());
            }
        } else if (config.useSpoonTob() && event.getGroup().equals("spoontob")) {
            if(event.getKey().equals("highlightRange")) {
                config.setEnableBoxRange(spoonTobConfig.getHighlightRangeNylo());
            }else if(event.getKey().equals("highlightMage")) {
                config.setEnableBoxMage(spoonTobConfig.getHighlightMageNylo());
            }else if(event.getKey().equals("highlightMelee")) {
                config.setEnableBoxMelee(spoonTobConfig.getHighlightMeleeNylo());
            }
        }
    }

    public void start() {
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
    }

    protected void shutDown() {
        stop();
    }

    public void stop() {
        if (!config.enableDebug()) {
            overlayManager.remove(overlay);
            overlayManager.remove(tileOverlay);
        }
        bigNylos.clear();
        overlay.nyloGrouped.clear();
        overlay.guardsGrouped.clear();
        test2.clear();
        pillars = 0;
        overlay.nyloGrouped.clear();
        wave = 0;
        pillarsSpawned = false;
        resetNyloSplits();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event
                .getGameState() == GameState.HOPPING) {
            bigNylos.clear();
            overlay.nyloGrouped.clear();
            overlay.guardsGrouped.clear();
            test2.clear();
        }
    }

    public class NyloContainer {
        private NPC npc;
        private boolean isAlive;
        private String spawn;
        private int ID;
        private int ticksAlive;
        private int tickSpawned;
        private String originalName;
        private String size;
        private int waveSpawned;
        private String t25;
        private String t27;
        private int tickDied;
        private String t26Style;
        private Color textColor;
        private Color t26color;
        private Color currentColor;
        private int lastId;
        private int index;
        public NPC getNpc() {
            return npc;
        }
        public void setAlive(boolean isAlive) {
            this.isAlive = isAlive;
        }
        public boolean isAlive() {
            return isAlive;
        }
        public String getSpawn() {
            return spawn;
        }
        public int getID() {
            return ID;
        }
        public int getTicksAlive() {
            return ticksAlive;
        }
        public String getSize() {
            return size;
        }
        public int getWaveSpawned() {
            return waveSpawned;
        }
        public String getT25() {
            return t25;
        }
        public void setT25(String t25) {
            this.t25 = t25;
        }
        public String getT27() {
            return t27;
        }
        public void setT27(String t27) {
            this.t27 = t27;
        }
        public int getTickDied() {
            return tickDied;
        }
        public void setTickDied(int tickDied) {
            this.tickDied = tickDied;
        }
        public String getT26Style() {
            return t26Style;
        }
        public Color getTextColor() {
            return textColor;
        }
        public Color getT26color() {
            return t26color;
        }
        public Color getCurrentColor() {
            return currentColor;
        }
        public void isDead() {
            isAlive = false;
        }

        public void incrementTicks() {
            ticksAlive++;
            if (ticksAlive > 26)
                t26color = Color.BLACK;
            if (ticksAlive >= 46)
                t26color = new Color(150, 85, 0);
            if (lastId != npc.getId()) {
                styleCheck(npc.getId());
                lastId = npc.getId();
            }
        }

        public void styleCheck(int id) {
            switch (id) {
                case 8342:
                case 8345:
                case 8348:
                case 8351:
                case 10774:
                case 10777:
                case 10780:
                case 10783:
                case 10791:
                case 10794:
                case 10797:
                case 10800:
                    currentColor = Color.WHITE;
                    return;
                case 8343:
                case 8346:
                case 8349:
                case 8352:
                case 10775:
                case 10778:
                case 10781:
                case 10784:
                case 10792:
                case 10795:
                case 10798:
                case 10801:
                    currentColor = Color.GREEN;
                    return;
                case 8344:
                case 8347:
                case 8350:
                case 8353:
                case 10776:
                case 10779:
                case 10782:
                case 10785:
                case 10793:
                case 10796:
                case 10799:
                case 10802:
                    currentColor = Color.CYAN;
                    return;
            }
            currentColor = Color.BLACK;
            isAlive = false;
        }

        public NyloContainer(NPC npc, String position, int id) {
            this.npc = npc;
            ticksAlive = 0;
            isAlive = true;
            spawn = position;
            ID = id;
            tickSpawned = BoNyloPlugin.this.client.getTickCount();
            waveSpawned = BoNyloPlugin.this.wave;
            lastId = id;
            index = npc.getIndex();
            t25 = "notset";
            t27 = "notset";
            originalName = npc.getName();
            styleCheck(id);
            if (npc.getComposition().getSize() == 1) {
                size = "small";
                if ("EAST".equals(position))
                    spawn = "EAST NORTH";
            } else {
                size = "big";
                if ("EAST".equals(position))
                    spawn = "EAST BIG";
            }
            BoNyloPlugin.log.debug("wave: " + waveSpawned + " spawn: " + spawn + " spawn: " + spawn);
            if ((BoNyloPlugin.this.wave == 18 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 18 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 18 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 21 && spawn == "WEST SOUTH") || (BoNyloPlugin.this.wave == 21 && spawn == "EAST SOUTH") || (BoNyloPlugin.this.wave == 22 && spawn == "SOUTH WEST") || (BoNyloPlugin.this.wave == 22 && spawn == "SOUTH EAST") || (BoNyloPlugin.this.wave == 23 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 24 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 27 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 28 && spawn == "SOUTH WEST") || (BoNyloPlugin.this.wave == 28 && spawn == "SOUTH EAST") || (BoNyloPlugin.this.wave == 29 && spawn == "EAST NORTH") || (BoNyloPlugin.this.wave == 30 && spawn == "SOUTH WEST") || (BoNyloPlugin.this.wave == 30 && spawn == "SOUTH EAST") || (BoNyloPlugin.this.wave == 31 && spawn == "WEST SOUTH")) {
                t26Style = "mage";
                t26color = new Color(0, 0, 255);
                BoNyloPlugin.log.debug("assign mage " + spawn + " " + waveSpawned);
            } else if ((BoNyloPlugin.this.wave == 20 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 20 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 20 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 21 && spawn == "SOUTH EAST") || (BoNyloPlugin.this.wave == 21 && spawn == "SOUTH WEST") || (BoNyloPlugin.this.wave == 22 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 22 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 23 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 23 && spawn == "WEST NORTH") || (BoNyloPlugin.this.wave == 26 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 28 && spawn == "WEST NORTH") || (BoNyloPlugin.this.wave == 28 && spawn == "EAST SOUTH") || (BoNyloPlugin.this.wave == 29 && spawn == "WEST NORTH") || (BoNyloPlugin.this.wave == 31 && spawn == "EAST NORTH") || (BoNyloPlugin.this.wave == 31 && spawn == "WEST NORTH")) {
                t26Style = "range";
                t26color = new Color(0, 137, 0);
                BoNyloPlugin.log.debug("assign range " + spawn + " " + waveSpawned);
            } else if ((BoNyloPlugin.this.wave == 17 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 17 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 17 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 19 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 19 && spawn == "SOUTH BIG") || (BoNyloPlugin.this.wave == 19 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 21 && spawn == "EAST NORTH") || (BoNyloPlugin.this.wave == 21 && spawn == "WEST NORTH") || (BoNyloPlugin.this.wave == 23 && spawn == "WEST SOUTH") || (BoNyloPlugin.this.wave == 25 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 27 && spawn == "EAST BIG") || (BoNyloPlugin.this.wave == 28 && spawn == "EAST NORTH") || (BoNyloPlugin.this.wave == 28 && spawn == "WEST SOUTH") || (BoNyloPlugin.this.wave == 29 && spawn == "EAST SOUTH") || (BoNyloPlugin.this.wave == 29 && spawn == "WEST SOUTH") || (BoNyloPlugin.this.wave == 30 && spawn == "WEST BIG") || (BoNyloPlugin.this.wave == 31 && spawn == "EAST SOUTH") || (BoNyloPlugin.this.wave == 31 && spawn == "SOUTH WEST") || (BoNyloPlugin.this.wave == 31 && spawn == "SOUTH EAST")) {
                t26Style = "melee";
                t26color = new Color(150, 0, 0);
                BoNyloPlugin.log.debug("assign melee " + spawn + " " + waveSpawned);
            } else {
                t26Style = "none";
                t26color = Color.BLACK;
                BoNyloPlugin.log.debug("assign melee " + spawn);
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e) {
        NPC npc = e.getNpc();
        String name = npc.getName();
        if (name == null)
            return;
        int id = npc.getId();
        if (id == 3269 || id == 3271 || id == 3272)
            test2.add(npc);
        if (!inNylo)
            return;
        if (id == 8355 || id == 8356 || id == 8357 || id == 10808 || id == 10809 || id == 10810 || id == 10787 || id == 10788 || id == 10789) {
            bossAlive = true;
            nyloBoss.add(npc);
            meleeBoss = 0;
            mageBoss = 0;
            rangeBoss = 0;
            lastBossId = 999;
            if (config.enableGroupAfterSpawn())
                sendSplitsMessage();
        }
        if ((id == 8358 || id == 10811 || id == 10790) && !pillarsSpawned) {
            wave = 0;
            pillarsSpawned = true;
            bigNylos.clear();
            overlay.nyloGrouped.clear();
            pillars = 4;
            resetNyloSplits();
        }
        if (id == 8342 || id == 8348 || id == 8345 || id == 8351 || id == 8343 || id == 8349 || id == 8346 || id == 8352 || id == 8344 || id == 8350 || id == 8347 || id == 8353 || id == 10791 || id == 10792 || id == 10793 || id == 10797 || id == 10798 || id == 10799 || id == 10794 || id == 10795 || id == 10796 || id == 10800 || id == 10801 || id == 10802 || id == 10774 || id == 10777 || id == 10780 || id == 10783 || id == 10775 || id == 10778 || id == 10781 || id == 10784 || id == 10776 || id == 10779 || id == 10782 || id == 10785) {
            int x = WorldPoint.fromLocalInstance(client, npc.getLocalLocation()).getRegionX();
            int y = WorldPoint.fromLocalInstance(client, npc.getLocalLocation()).getRegionY();
            if ((x == 17 && y == 25) || (x == 17 && y == 24) || (x == 31 && y == 9) || (x == 32 && y == 9) || (x == 46 && y == 24) || (x == 46 && y == 25) || (x == 47 && y == 25) || (x == 32 && y == 10) || (x == 18 && y == 25)) {
                if (lastWaveSpawnedTick != client.getTickCount()) {
                    wave++;
                    lastWaveSpawnedTick = client.getTickCount();
                }
            } else {
                log.debug("bn: split");
                if (wave <= 19) {
                    if (name.contains("Nylocas Hagios")) {
                        mageSplits++;
                        preMageSplits++;
                    }
                    if (name.contains("Nylocas Toxobolos")) {
                        rangeSplits++;
                        preRangeSplits++;
                    }
                    if (name.contains("Nylocas Ischyros")) {
                        meleeSplits++;
                        preMeleeSplits++;
                    }
                }
                if (config.trackCleanupSplits()) {
                    if (wave >= 20 && wave < 29) {
                        if (name.contains("Nylocas Hagios")) {
                            mageSplits++;
                            postMageSplits++;
                        }
                        if (name.contains("Nylocas Toxobolos")) {
                            rangeSplits++;
                            postRangeSplits++;
                        }
                        if (name.contains("Nylocas Ischyros")) {
                            meleeSplits++;
                            postMeleeSplits++;
                        }
                    }
                    if (wave >= 29) {
                        if (name.contains("Nylocas Hagios")) {
                            mageSplits++;
                            cleanupMageSplits++;
                        }
                        if (name.contains("Nylocas Toxobolos")) {
                            rangeSplits++;
                            cleanupRangeSplits++;
                        }
                        if (name.contains("Nylocas Ischyros")) {
                            meleeSplits++;
                            cleanupMeleeSplits++;
                        }
                    }
                } else {
                    if (wave >= 20) {
                        if (name.contains("Nylocas Hagios")) {
                            mageSplits++;
                            postMageSplits++;
                        }
                        if (name.contains("Nylocas Toxobolos")) {
                            rangeSplits++;
                            postRangeSplits++;
                        }
                        if (name.contains("Nylocas Ischyros")) {
                            meleeSplits++;
                            postMeleeSplits++;
                        }
                    }
                }
            }
            if (npc.getName() == null)
                return;
            if (!pillarsSpawned)
                return;
            if (x == 17 && y == 25) {
                bigNylos.add(new NyloContainer(npc, "WEST NORTH", id));
                log.debug("" + wave + " detected west north");
            } else if (x == 17 && y == 24) {
                bigNylos.add(new NyloContainer(npc, "WEST SOUTH", id));
                log.debug("" + wave + " detected west south");
            } else if (x == 31 && y == 9) {
                bigNylos.add(new NyloContainer(npc, "SOUTH WEST", id));
                log.debug("" + wave + " detected south west");
            } else if (x == 32 && y == 9) {
                bigNylos.add(new NyloContainer(npc, "SOUTH EAST", id));
                log.debug("" + wave + " detected south east");
            } else if (x == 46 && y == 24) {
                bigNylos.add(new NyloContainer(npc, "EAST SOUTH", id));
                log.debug("" + wave + " detected east south");
            } else if (x == 46 && y == 25) {
                bigNylos.add(new NyloContainer(npc, "EAST", id));
                log.debug("" + wave + " detected east");
            } else if (x == 18 && y == 25) {
                bigNylos.add(new NyloContainer(npc, "WEST BIG", id));
                log.debug("" + wave + " detected west BIG");
            } else if (x == 32 && y == 10) {
                bigNylos.add(new NyloContainer(npc, "SOUTH BIG", id));
                log.debug("" + wave + " detected south big");
            } else if (x == 47 && y == 25) {
                bigNylos.add(new NyloContainer(npc, "EAST BIG 30", id));
                log.debug("" + wave + " detected east big 30");
            } else {
                bigNylos.add(new NyloContainer(npc, "split", id));
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        debugCounter++;
        if (debugCounter >= 100)
            debugCounter = 1;
        if (client.getTickCount() % 5 == 0)
            if (inRegion(13122)) {
                inNylo = true;
            } else {
                inNylo = false;
            }
        if (inNylo && !inNyloPrev) {
            start();
            message("BNP: in Nylo = true");
        }
        if (!inNylo && inNyloPrev) {
            stop();
            message("BNP: in Nylo = false");
        }
        inNyloPrev = inNylo;
        if (!inNylo)
            return;
        /*if (bossAlive) {
            for (NPC npc : nyloBoss) {
                int id = npc.getId();
                if (lastBossId != id) {
                    lastBossId = id;
                    switch (id) {
                        case 8355:
                        case 10808:
                            meleeBoss++;
                        case 8356:
                        case 10809:
                            mageBoss++;
                        case 8357:
                        case 10810:
                            rangeBoss++;
                    }
                }
            }
        }*/
        for (NyloContainer nylo : bigNylos) {
            if (nylo.isAlive) {
                nylo.incrementTicks();
                if (nylo.ticksAlive == 52 || nylo.getNpc().isDead()) {
                    nylo.setAlive(false);
                    nylo.setTickDied(nylo.ticksAlive);
                }
            }
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event){
        NPC npc = event.getNpc();
        int id = npc.getId();
        if (id == 8355 || id == 10787 || id == 10808) {
            meleeBoss++;
        } else if (id == 8356 || id == 10788 || id == 10809) {
            mageBoss++;
        } else if (id == 8357 || id == 10789 || id == 10810) {
            rangeBoss++;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned ee) {
        NPC npc = ee.getNpc();
        int id = npc.getId();
        String name = npc.getName();
        if (name == null)
            return;
        if ((id == 3269 || id == 3721 || id == 3272) &&
                test2.contains(npc))
            test2.remove(npc);
        if (!inNylo)
            return;
        if (id == 8355 || id == 8356 || id == 8357 || id == 10808 || id == 10809 || id == 10810 || id == 10787 || id == 10788 || id == 10789) {
            if (!config.enableGroupAfterSpawn())
                sendSplitsMessage();
            sendBossMessage();
            nyloBoss.clear();
            bossAlive = false;
            resetNyloSplits();
        }
    }

    public void resetNyloSplits() {
        totalSplits = 0;
        rangeSplits = meleeSplits = mageSplits = totalSplits = 0;
        preRangeSplits = preMeleeSplits = preMageSplits = postRangeSplits = postMeleeSplits = postMageSplits = cleanupRangeSplits = cleanupMeleeSplits = cleanupMageSplits = 0;
    }

    public void sendSplitsMessage() {
        if (!config.enableSplitsMessage())
            return;
        totalSplits = mageSplits + rangeSplits + meleeSplits;
        if (rangeSplits <= 20 && totalSplits == 86) {
            splitMessage = "<col=EF1020> You got fucked on range splits lmao";
        } else {
            splitMessage = "";
        }
        if (mageSplits >= 38 && totalSplits == 86) {
            splitMessage2 = "<col=EF1020> It's fucking Hogwarts in here";
        } else {
            splitMessage2 = "";
        }
        if (config.enablePrePostSplits()) {
            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, " ", "Pre cap splits: <col=000000>[<col=0000FF>" + preMageSplits + "<col=000000>] <col=000000>[<col=126100>" + preRangeSplits + "<col=000000>] <col=000000>[<col=EF1020>" + preMeleeSplits + "<col=000000>] </col>Post cap splits: <col=000000>[<col=0000FF>" + postMageSplits + "<col=000000>] <col=000000>[<col=126100>" + postRangeSplits + "<col=000000>] <col=000000>[<col=EF1020>" + postMeleeSplits + "<col=000000>]  ", null);
            if (config.trackCleanupSplits()) {
                client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, " ", "Cleanup splits: <col=000000>[<col=0000FF>" + cleanupMageSplits + "<col=000000>] <col=000000>[<col=126100>" + cleanupRangeSplits + "<col=000000>] <col=000000>[<col=EF1020>" + cleanupMeleeSplits + "<col=000000>]  ", null);
            }
        }
        client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, " ", "Total splits: <col=000000>[<col=0000FF>" + mageSplits + "<col=000000>] <col=000000>[<col=126100>" + rangeSplits + "<col=000000>] <col=000000>[<col=EF1020>" + meleeSplits + "<col=000000>] " + splitMessage + " " + splitMessage2, null);
    }

    public void sendBossMessage() {
        if (config.enableSplitsMessage()) {
            int totalBoss = meleeBoss + rangeBoss + mageBoss;
            if (mageBoss == 0 && totalSplits == 86) {
                bossMessage = "<col=EF1020> GOD BOSS!!!!";
            } else if (mageBoss >= totalBoss / 2 && mageBoss > 2 && totalSplits == 86) {
                bossMessage = "<col=EF1020> Harry Potter looking-ass boss";
            } else {
                bossMessage = "";
            }
            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, " ", "Boss rotation: <col=000000>[<col=0000FF>" + mageBoss
                    + "<col=000000>] <col=000000>[<col=126100>" + rangeBoss
                    + "<col=000000>] <col=000000>[<col=EF1020>" + meleeBoss + "<col=000000>] " + bossMessage, null);
        }
    }

    private void SocketDeathIntegration(int passedIndex) {
        for (NyloContainer nylo : bigNylos) {
            if (passedIndex == nylo.index)
                nylo.setAlive(false);
        }
    }

    public void message(String str) {
        if (config.debugmsgs())
            client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, " ", str, null);
    }

    public boolean inRegion(int... regions) {
        if (client.getMapRegions() != null)
            for (int i : client.getMapRegions()) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        return false;
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            tileOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(tileOverlay);
            overlayManager.add(tileOverlay);
            mirrorMode = true;
        }
    }*/
}
