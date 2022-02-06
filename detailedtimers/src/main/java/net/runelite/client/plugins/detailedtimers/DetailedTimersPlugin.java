package net.runelite.client.plugins.detailedtimers;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "cAnalytics",
        description = "Detailed timers/info for TOB - No auth",
        tags = {"timer", "tob"},
        enabledByDefault = false
)
public class DetailedTimersPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(DetailedTimersPlugin.class);

    @Inject
    private DetailedTimersConfig config;

    @Inject
    private Client client;

    room currentRoom;

    enum room {
        MAIDEN, BLOAT, NYLOCAS, SOTETSEG, XARPUS, VERZIK, NONE;
    }

    private final int MAIDEN_FIRST = 8360;

    private final int MAIDEN_DEAD = 8365;

    private final int MAIDEN_SECOND = 8361;

    private final int MAIDEN_THIRD = 8362;

    private final int MAIDEN_LAST = 8363;

    private final int BLOAT = 8359;

    private final int NYLO_BOSS_MELEE = 8355;

    private final int NYLO_BOSS_MAGE = 8356;

    private final int NYLO_BOSS_RANGE = 8357;

    private final int SOTETSEG = 8388;

    private final int XARPUS_PRIOR = 8338;

    private final int XARPUS_NEXT = 8339;

    private final int SOTETSEG_NULL = 8387;

    private final int XARPUS = 8340;

    private final int XARPUS_DEATH = 8341;

    private final int VERZIK_P0 = 8369;

    private final int VERZIK_P1_TRANSITION = 8371;

    private final int VERZIK_P1 = 8370;

    private final int VERZIK_P2 = 8372;

    private final int VERZIK_P2_TRANSITION = 8373;

    private final int VERZIK_P3 = 8374;

    private final int VERZIK_DEATH = 8375;

    private final int MAIDEN_REGION = 12613;

    private int BLOAT_REGION = 13125;

    private int NYLO_REGION = 13122;

    private int SOTETSEG_REGION = 13123;

    private int XARPUS_REGION = 12612;

    private int VERZIK_REGION = 12611;

    @Inject
    ClientThread clientThread;

    @Inject
    private InfoBoxManager infoBoxManager;

    private NPC maidenNPC;

    private NPC bloatNPC;

    private NPC nyloNPC;

    private NPC soteNPC;

    private NPC xarpusNPC;

    private NPC verzikNPC;

    private int maidenID = -1;

    private int bloatID = -1;

    private int nyloID = -1;

    private int soteID = -1;

    private int xarpusID = -1;

    private int verzikID = -1;

    private MaidenTimer maiden;

    private BloatTimer bloat;

    private NyloTimer nylo;

    private SotetsegTimer sote;

    private XarpusTimer xarpus;

    private VerzikTimer verzik;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SkillIconManager skillIconManager;

    public NyloTimer getNylo() {
        return nylo;
    }

    private int maidenLeaked = 0;

    public int getMaidenLeaked() {
        return maidenLeaked;
    }

    private int temp = 0;

    private boolean maidenActive = false;

    private ArrayList<NyloCrab> nylos;

    public boolean isMaidenActive() {
        return maidenActive;
    }

    public ArrayList<NyloCrab> getNylos() {
        return nylos;
    }

    public static int partySize = 0;

    private ArrayList<String> xarpusPlayers;

    private ArrayList<Integer> xarpusPlayerTick;

    private ArrayList<String> activePlayers;

    private ArrayList<NyloCrab> bigNylos;

    private boolean authenticated = false;

    public ArrayList<Integer> HELMS = new ArrayList<>(Arrays.asList(
            ItemID.SERPENTINE_HELM, ItemID.TANZANITE_HELM, ItemID.MAGMA_HELM, ItemID.NEITIZNOT_FACEGUARD, ItemID.INQUISITORS_GREAT_HELM, ItemID.TORVA_FULL_HELM));
    public ArrayList<Integer> TORSOS = new ArrayList<>(Arrays.asList(
            ItemID.BANDOS_CHESTPLATE, ItemID.FIGHTER_TORSO, ItemID.FIGHTER_TORSO_L, ItemID.INQUISITORS_HAUBERK, ItemID.TORVA_PLATEBODY));
    public ArrayList<Integer> SPEC_WEAPONS = new ArrayList<>(Arrays.asList(
            ItemID.DRAGON_WARHAMMER, ItemID.DRAGON_WARHAMMER_20785));
    public ArrayList<Integer> DEFENDERS = new ArrayList<>(Arrays.asList(
            ItemID.AVERNIC_DEFENDER, ItemID.AVERNIC_DEFENDER_L));
    public ArrayList<Integer> BOOTS = new ArrayList<>(List.of(
            ItemID.PRIMORDIAL_BOOTS));
    public ArrayList<Integer> GLOVES = new ArrayList<>(List.of(
            ItemID.FEROCIOUS_GLOVES));
    public ArrayList<Integer> CAPES = new ArrayList<>(Arrays.asList(
            ItemID.INFERNAL_CAPE, ItemID.INFERNAL_CAPE_23622, ItemID.INFERNAL_CAPE_21297, ItemID.INFERNAL_CAPE_L,
            ItemID.INFERNAL_MAX_CAPE, ItemID.INFERNAL_MAX_CAPE_21285, ItemID.INFERNAL_MAX_CAPE_L));
    public ArrayList<Integer> AMULETS = new ArrayList<>(Arrays.asList(
            ItemID.AMULET_OF_TORTURE, ItemID.AMULET_OF_TORTURE_OR));

    @Provides
    DetailedTimersConfig provideConfig(ConfigManager configManager) {
        return (DetailedTimersConfig)configManager.getConfig(DetailedTimersConfig.class);
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String privateKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, getPrivateKey(privateKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, publicKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data, String base64PublicKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPublicKey(base64PublicKey));
    }

    protected void startUp() throws Exception {
        maiden = new MaidenTimer(client);
        bloat = new BloatTimer(client);
        nylo = new NyloTimer(client);
        sote = new SotetsegTimer(client);
        xarpus = new XarpusTimer(client);
        verzik = new VerzikTimer(client);
        currentRoom = room.NONE;
        crabs = new ArrayList<>();
        partySize = -1;
        nylos = new ArrayList<>();
        xarpusPlayers = new ArrayList<>();
        xarpusPlayerTick = new ArrayList<>();
        activePlayers = new ArrayList<>();
        bigNylos = new ArrayList<>();
        if (config.showMaidenMax())
            infoBoxManager.addInfoBox(new MaidenDamageBox(ImageUtil.loadImageResource(getClass(), "maxhit.png"), this));
    }

    int startTick = 0;

    private ArrayList<MaidenCrab> crabs;

    protected void shutDown() throws Exception {
        infoBoxManager.removeIf(t -> t instanceof MaidenDamageBox);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (bloatInstStart != -1)
            bloatInstStart++;
        updateRoom();
        if (currentRoom == room.MAIDEN) {
            updateMaiden();
        } else if (currentRoom == room.BLOAT) {
            updateBloat();
        } else if (currentRoom == room.NYLOCAS) {
            updateNylo();
        } else if (currentRoom == room.SOTETSEG) {
            updateSotetseg();
        } else if (currentRoom == room.XARPUS) {
            updateXarpus();
        } else if (currentRoom == room.VERZIK) {
            updateVerzik();
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        switch (event.getNpc().getId()) {
            case 8365:
                maiden.end();
                maidenActive = false;
                maidenLeaked = 0;
                break;
            case 8359:
                bloat.end();
                break;
            case 8355:
            case 8356:
            case 8357:
                nylo.end();
                startTick = 0;
                break;
            case 8342:
            case 8343:
            case 8344:
                nylos.removeIf(p -> p.npc.equals(event.getNpc()));
                break;
            case 8345:
            case 8346:
            case 8347:
            case 8351:
            case 8352:
            case 8353:
                if (config.showBigsAlive()) {
                    String msg = "";
                    Optional<NyloCrab> o = bigNylos.stream().filter(n -> n.npc.equals(event.getNpc())).findFirst();
                    if (o.isPresent()) {
                        if (event.getNpc().getId() == 8345 || event.getNpc().getId() == 8351) {
                            msg = msg + "Melee";
                        } else if (event.getNpc().getId() == 8346 || event.getNpc().getId() == 8352) {
                            msg = msg + "Mage";
                        } else if (event.getNpc().getId() == 8347 || event.getNpc().getId() == 8353) {
                            msg = msg + "Range";
                        }
                        msg = msg + " big died after being alive for " + ((NyloCrab)o.get()).ticksAlive + " ticks";
                        if (event.getNpc().getId() != ((NyloCrab)o.get()).startingID && ((NyloCrab)o.get()).ticksAlive > 20) {
                            msg = msg + " (switched from ";
                            if (((NyloCrab)o.get()).startingID == 8345 || ((NyloCrab)o.get()).startingID == 8351) {
                                msg = msg + "melee";
                            } else if (((NyloCrab)o.get()).startingID == 8346 || ((NyloCrab)o.get()).startingID == 8352) {
                                msg = msg + "mage";
                            } else if (((NyloCrab)o.get()).startingID == 8347 || ((NyloCrab)o.get()).startingID == 8353) {
                                msg = msg + "range";
                            }
                            msg = msg + ")";
                        }
                        sendChatMessage(msg);
                    }
                }
                bigNylos.removeIf(p -> p.npc.equals(event.getNpc()));
            case 8387:
            case 8388:
                if (client.getLocalPlayer().getWorldLocation().getPlane() != 3) {
                    sote.end();
                    break;
                }
                if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_1) {
                    sote.procFirstMaze();
                    break;
                }
                if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_2)
                    sote.procSecondMaze();
                break;
            case 8340:
            case 8341:
                xarpus.end();
                break;
            case 8375:
                verzik.endP3();
                verzik.end();
                break;
            case 8366:
                if (maiden.isActive())
                    maidenNylosDespawned(event.getNpc());
                break;
        }
    }

    private void maidenNylosDespawned(NPC npc) {
        if (crabs.size() == 0)
            return;
        ArrayList<MaidenCrab> toRemove = new ArrayList<>();
        for (MaidenCrab c : crabs) {
            if (c.getIndex() == npc.getIndex()) {
                if (c.getHP() != 0) {
                    String leak = c.getName() + " leaked with: " + (int)(c.getHP() / 30.0D * 100.0D) + "% hp";
                    maidenLeaked++;
                    if (config.showLeakedCrabs())
                        sendChatMessage(leak);
                }
                toRemove.add(c);
            }
        }
        for (MaidenCrab c : toRemove)
            crabs.remove(c);
    }

    public static String time(int ticks) {
        String timeStr = "";
        double seconds = ticks * 0.6D;
        int minutes = ((int)seconds - (int)seconds % 60) / 60;
        int onlySeconds = (int)seconds - 60 * minutes;
        String secondsString = String.format("%.1f", new Object[] { Double.valueOf(ticks * 0.6D) });
        if (minutes != 0)
            timeStr = timeStr + minutes + ":";
        if (onlySeconds < 10)
            timeStr = timeStr + "0";
        timeStr = timeStr + onlySeconds;
        if ((StringUtils.split(secondsString, ".")).length == 2) {
            String[] subStr = StringUtils.split(secondsString, ".");
            timeStr = timeStr + "." + subStr[1];
        } else {
            timeStr = timeStr + ".0";
        }
        if (minutes == 0)
            timeStr = timeStr + "s";
        return timeStr;
    }

    private boolean scuffedFlag = false;

    private int scuffTick = 0;

    private void addCrab(String name, boolean scuffed, NPC npc) {
        if (!scuffedFlag)
            if (scuffed) {
                scuffedFlag = true;
                scuffTick = client.getTickCount();
            }
        crabs.add(new MaidenCrab(name, scuffed, npc));
    }

    private void maidenNylosSpawned(NPC npc) {
        int x = npc.getWorldLocation().getRegionX();
        int y = npc.getWorldLocation().getRegionY();
        String proc = "";
        if (maidenNPC.getId() == 8361)
            proc = " 70";
        if (maidenNPC.getId() == 8362)
            proc = " 50";
        if (maidenNPC.getId() == 8363)
            proc = " 30";
        if (x == 21 && y == 40)
            addCrab("N1" + proc, false, npc);
        if (x == 22 && y == 41)
            addCrab("N1" + proc, true, npc);
        if (x == 25 && y == 40)
            addCrab("N2" + proc, false, npc);
        if (x == 26 && y == 41)
            addCrab("N2" + proc, true, npc);
        if (x == 29 && y == 40)
            addCrab("N3" + proc, false, npc);
        if (x == 30 && y == 41)
            addCrab("N3" + proc, true, npc);
        if (x == 33 && y == 40)
            addCrab("N4 (1)" + proc, false, npc);
        if (x == 34 && y == 41)
            addCrab("N4 (1)" + proc, true, npc);
        if (x == 33 && y == 38)
            addCrab("N4 (2)" + proc, false, npc);
        if (x == 34 && y == 39)
            addCrab("N4 (2)" + proc, true, npc);
        if (x == 21 && y == 20)
            addCrab("S1" + proc, false, npc);
        if (x == 22 && y == 19)
            addCrab("S1" + proc, true, npc);
        if (x == 25 && y == 20)
            addCrab("S2" + proc, false, npc);
        if (x == 26 && y == 19)
            addCrab("S2" + proc, true, npc);
        if (x == 29 && y == 20)
            addCrab("S3" + proc, false, npc);
        if (x == 30 && y == 19)
            addCrab("S3" + proc, true, npc);
        if (x == 33 && y == 20)
            addCrab("S4 (1)" + proc, false, npc);
        if (x == 34 && y == 19)
            addCrab("S4 (1)" + proc, true, npc);
        if (x == 33 && y == 22)
            addCrab("S4 (2)" + proc, false, npc);
        if (x == 34 && y == 20)
            addCrab("S4 (2)" + proc, true, npc);
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        NPC npc = event.getNpc();
        if (npc.getId() != 8355)
            if (npc.getId() != 8357)
                if (npc.getId() == 8356);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        switch (event.getNpc().getId()) {
            case 8360:
            case 8361:
            case 8362:
            case 8363:
                maidenSpawn(event.getNpc());
                break;
            case 8359:
                bloatNPC = event.getNpc();
                break;
            case 8342:
            case 8343:
            case 8344:
                nylos.add(new NyloCrab(event.getNpc()));
                break;
            case 8345:
            case 8346:
            case 8347:
            case 8351:
            case 8352:
            case 8353:
                bigNylos.add(new NyloCrab(event.getNpc()));
                break;
            case 8355:
            case 8356:
            case 8357:
                nyloNPC = event.getNpc();
                nylo.startBoss();
                startTick = client.getTickCount();
                break;
            case 8387:
            case 8388:
                soteNPC = event.getNpc();
                break;
            case 8338:
            case 8339:
            case 8340:
            case 8341:
                xarpusNPC = event.getNpc();
                break;
            case 8369:
            case 8370:
            case 8371:
            case 8372:
            case 8374:
            case 8375:
                verzikNPC = event.getNpc();
                break;
            case 8366:
                if (maiden.isActive())
                    maidenNylosSpawned(event.getNpc());
                break;
        }
    }

    private void maidenSpawn(NPC npc) {
        maidenNPC = npc;
        maiden.start();
        maidenActive = true;
    }

    private boolean bloatInst = false;

    private static String timeT(int t) {
        return String.format("%.1f", new Object[] { Double.valueOf(t * 0.6D) }) + "s";
    }

    private void updateMaiden() {
        if (scuffedFlag)
            if (client.getTickCount() > scuffTick) {
                scuffedFlag = false;
                if (config.showScuffedSpawns())
                    sendChatMessage("Scuffed Spawns!");
            }
        for (MaidenCrab c : crabs)
            c.updateHP();
        if (crossedLine(12613, new Point(24, 5), new Point(25, 5), true))
            if (bloatInstStart == -1) {
                bloatInstStart = 0;
                bloatInst = true;
            }
        if (maidenNPC != null) {
            if (maiden.isActive() && maidenNPC.getHealthRatio() == 0 && !maiden.isDead())
                maiden.kill();
            if (maidenNPC.getId() != maidenID) {
                if (maidenID != -1)
                    if (maidenID == 8360) {
                        maiden.proc70();
                    } else if (maidenID == 8361) {
                        maiden.proc50();
                    } else if (maidenID == 8362) {
                        maiden.proc30();
                    }
                maidenID = maidenNPC.getId();
            }
        }
    }

    private boolean crossedLine(int region, Point start, Point end, boolean vertical) {
        if (inRegion(region))
            for (Player p : client.getPlayers()) {
                WorldPoint wp = p.getWorldLocation();
                if (vertical) {
                    for (int j = start.getY(); j < end.getY() + 1; j++) {
                        if (wp.getRegionY() == j && wp.getRegionX() == start.getX())
                            return true;
                    }
                    continue;
                }
                for (int i = start.getX(); i < end.getX() + 1; i++) {
                    if (wp.getRegionX() == i && wp.getRegionY() == start.getY())
                        return true;
                }
            }
        return false;
    }

    private int lastX = -1;

    private int lastY = -1;

    private int dir = -1;

    private int lastDir = -1;

    private boolean bloatStart = false;

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC08H7l4mWyTtogBXEFf9PIccDyDKJOxe91WMHycbrQpuV7FkwbTaUbH5lqDe6NBqRxoopuHBkHbzUSYT02ZPrpALKOPXrIshoP2cAWRZAXHVLBq1cOrGFAYfMJDfYY5gg6iJaCMRccBKALYZr84G3VUVE8vweb/IhToT2J2W/dxQIDAQAB";

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN)
            loadAuthentication();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("detailedTimers"))
            loadAuthentication();
    }

    private void loadAuthentication() {
        try {
            String name = config.getAuthenticationKey();
            String decString = decrypt(name, publicKey);
            if (decString.toLowerCase().equals(Objects.requireNonNull(Objects.requireNonNull(client.getLocalPlayer()).getName()).toLowerCase())) {
                if (!authenticated) {
                    authenticated = true;
                    if (client.getGameState() == GameState.LOGGED_IN)
                        sendChatMessage("Authentication Successful");
                }
            } else {
                authenticated = true;
            }
        } catch (Exception e) {
            authenticated = true;
        }
    }

    private boolean nameListCheck(String msg) {
        if (msg.contains(Objects.<CharSequence>requireNonNull(Objects.<Player>requireNonNull(client.getLocalPlayer()).getName())))
            return true;
        for (String s : activePlayers) {
            if (msg.contains(s))
                return true;
        }
        return false;
    }

    private boolean checkAuth(String msg) {
        return (authenticated || nameListCheck(msg));
    }

    private void sendAuthMessage(String msg) {
        if (checkAuth(msg))
            sendChatMessage(msg);
    }

    private void sendChatMessage(String msg) {
        clientThread.invoke(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null));
    }

    private int bloatDir(int x, int y) {
        if (x == 24 && y > lastY)
            return 0;
        if (y == 35 && x > lastX)
            return 0;
        if (x == 35 && y < lastY)
            return 0;
        if (y == 24 && x < lastX)
            return 0;
        if (x == lastX && y == lastY)
            return lastDir;
        return 1;
    }

    public static int bloatInstStart = 0;

    private void updateBloat() {
        if (bloatNPC != null) {
            int bloatX = bloatNPC.getWorldLocation().getRegionX();
            int bloatY = bloatNPC.getWorldLocation().getRegionY();
            dir = bloatDir(bloatX, bloatY);
            if (lastDir != -1)
                if (dir != lastDir);
            lastDir = dir;
            bloatStart = true;
            lastX = bloatX;
            lastY = bloatY;
        } else {
            bloatStart = false;
            lastX = -1;
            lastY = -1;
            lastDir = -1;
            bloatInstStart = -1;
        }
        if (bloatNPC != null)
            if (bloat.isActive() && bloatNPC.getHealthRatio() == 0 && !bloat.isDead())
                bloat.kill();
        if (!bloat.isFinished() && !bloat.isActive() && crossedLine(BLOAT_REGION, new Point(39, 30), new Point(39, 33), true)) {
            bloat.start();
            bloat.walk();
        }
    }

    private void updateNylo() {
        if (nyloNPC != null)
            if (nylo.isActive() && nyloNPC.getHealthRatio() == 0 && !nylo.isDead())
                nylo.kill();
        if (!nylo.isFinished() && !nylo.isActive() && crossedLine(NYLO_REGION, new Point(23, 30), new Point(24, 30), false))
            nylo.start();
        for (NyloCrab c : nylos) {
            if (c.npc.getHealthRatio() == 0);
            c.ticksAlive++;
        }
        for (NyloCrab c : bigNylos)
            c.ticksAlive++;
    }

    private void updateSotetseg() {
        if (soteNPC != null)
            if (sote.isActive() && soteNPC.getHealthRatio() == 0 && !sote.isDead())
                sote.kill();
        if (!sote.isFinished() && !sote.isActive() && crossedLine(SOTETSEG_REGION, new Point(14, 20), new Point(17, 20), false))
            sote.start();
        if (sote.isActive())
            if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_1) {
                if (soteNPC.getId() == 8387)
                    sote.procFirstMaze();
            } else if (sote.state == SotetsegTimer.SotetsegRoomState.MAZE_1) {
                if (soteNPC.getId() == 8388)
                    sote.endFirstMaze();
            } else if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_2) {
                if (soteNPC.getId() == 8387)
                    sote.procSecondMaze();
            } else if (sote.state == SotetsegTimer.SotetsegRoomState.MAZE_2) {
                if (soteNPC.getId() == 8388)
                    sote.endFirstMaze();
            } else if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_3) {
                if (soteNPC.getId() == 8388)
                    sote.endSecondMaze();
            }
    }

    private void tryAddPlayer(String name) {
        if (!xarpusPlayers.contains(name)) {
            xarpusPlayers.add(name);
            xarpusPlayerTick.add(-1);
        }
    }

    private void updateXarpus() {
        if (xarpusNPC != null)
            if (xarpus.isActive() && xarpusNPC.getHealthRatio() == 0 && !xarpus.isDead())
                xarpus.kill();
        if (!xarpus.isFinished() && !xarpus.isActive() && crossedLine(XARPUS_REGION, new Point(25, 12), new Point(27, 12), false)) {
            xarpusPlayers.clear();
            xarpusPlayerTick.clear();
            xarpus.start();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (bloat.isActive()) {
            bloatAnimationUpdate(event);
        } else if (nylo.isActive()) {
            if (nyloAnimationUpdate(event))
                return;
        } else if (maiden.isActive()) {
            if (maidenAnimationUpdate(event))
                return;
        } else if (sote.isActive()) {
            sotetsegAnimationUpdate(event);
        } else if (xarpus.isActive()) {
            int animationID = event.getActor().getAnimation();
            if (animationID == 8056) {
                tryAddPlayer(event.getActor().getName());
                int pIndex = xarpusPlayers.indexOf(event.getActor().getName());
                if (pIndex != -1) {
                    int pLastTick = (Integer) xarpusPlayerTick.get(pIndex);
                    if (pLastTick != -1 && pLastTick != client.getTickCount()) {
                        int ticksLost = client.getTickCount() - pLastTick;
                        String msgModifier = "";
                        if (ticksLost == 1) {
                            msgModifier = msgModifier + "a tick";
                        } else {
                            msgModifier = msgModifier + ticksLost + " ticks";
                        }
                        String msg = event.getActor().getName() + " lost " + msgModifier;
                        sendAuthMessage(msg);
                    }
                    xarpusPlayerTick.set(pIndex, client.getTickCount() + 5);
                }
            } else if (animationID == 1378 || animationID == 7642 || animationID == 7643) {
                int pIndex = xarpusPlayers.indexOf(event.getActor().getName());
                if (pIndex != -1) {
                    int pLastTick = xarpusPlayerTick.get(pIndex);
                    if (pLastTick != -1 && pLastTick != client.getTickCount()) {
                        String msg = event.getActor().getName() + " lost a tick";
                        sendAuthMessage(msg);
                    }
                    xarpusPlayerTick.set(pIndex, client.getTickCount() + 6);
                }
            } else if (animationID == 7514) {
                int pIndex = xarpusPlayers.indexOf(event.getActor().getName());
                if (pIndex != -1) {
                    int pLastTick = xarpusPlayerTick.get(pIndex);
                    if (pLastTick != -1 && pLastTick != client.getTickCount()) {
                        String msg = event.getActor().getName() + " lost a tick";
                        sendAuthMessage(msg);
                    }
                    xarpusPlayerTick.set(pIndex, client.getTickCount() + 4);
                }
            }
        }
            genericAnimationUpdate(event);
    }

    private void genericAnimationUpdate(AnimationChanged event) {
        if (client.getVar(Varbits.THEATRE_OF_BLOOD) == 2) {
            Player p;
            if (event.getActor() instanceof Player) {
                p = (Player) event.getActor();
            } else {
                return;
            }
            try {
                int weapon = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);
                if (event.getActor().getAnimation() == 401) {
                    if (weapon == 13576) {
                        String msg = event.getActor().getName() + " hammer bopped (bad rng)";
                        if (config.showHammerBop())
                            sendAuthMessage(msg);
                    }
                } else if (event.getActor().getAnimation() == 440) {
                    if (weapon == 23987) {
                        String msg = event.getActor().getName() + " chally poked";
                        if (config.showChallyPoke())
                            sendAuthMessage(msg);
                    }
                } else if (event.getActor().getAnimation() == 7045) {
                    String msg = event.getActor().getName() + " swung BGS without speccing";
                    if (config.showBGSWhack())
                        sendAuthMessage(msg);
                } else if (event.getActor().getAnimation() == 414) {
                    if (weapon == 21006) {
                        String msg = p.getName() + " kodai bopped";
                        if (config.showKodaiBop())
                            sendAuthMessage(msg);
                    }
                } else if (event.getActor().getAnimation() == 8056) {
                    if (event.getActor() instanceof Player) {
                        if (p.getPlayerComposition().getEquipmentId(KitType.WEAPON) == 22486 || p.getPlayerComposition().getEquipmentId(KitType.WEAPON) == 25741
                                || p.getPlayerComposition().getEquipmentId(KitType.WEAPON) == 25738) {
                            String msg = event.getActor().getName() + " is using an uncharged scythe";
                            if (config.showUnchargedScythe())
                                sendAuthMessage(msg);
                        }
                        int helm = p.getPlayerComposition().getEquipmentId(KitType.HEAD);
                        if (helm == 12929 || helm == 13196 || helm == 13198) {
                            String msg = event.getActor().getName() + " is using an uncharged serp";
                            if (config.showUnchargedSerp())
                                sendAuthMessage(msg);
                        }
                        int amulet = p.getPlayerComposition().getEquipmentId(KitType.AMULET);
                        if (amulet == 24780) {
                            String msg = event.getActor().getName() + " is scything with a blood fury....";
                            if (config.showBloodFury())
                                sendAuthMessage(msg);
                        }
                    }
                } else if (event.getActor().getAnimation() == 1378) {
                    if (!checkEquipment(p.getPlayerComposition())) {
                        String statusMsg = p.getName() + " specced wearing wrong: " + lastEquipmentCheck;
                        if (config.showMissingSwitches())
                            sendAuthMessage(statusMsg);
                    }
                }
            } catch (Exception exception) {
            }
        }
    }

    private void sotetsegAnimationUpdate(AnimationChanged event) {
        if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_2 || sote.state == SotetsegTimer.SotetsegRoomState.PHASE_3) {
            if (event.getActor().getAnimation() == 1378) {
                String msg = event.getActor().getName() + " DWH specced ";
                if (sote.getTicksSinceLastMaze() > 1 && sote.getTicksSinceLastMaze() != 6) {
                    if (sote.getTicksSinceLastMaze() < 6) {
                        String msgs = (sote.getTicksSinceLastMaze() == 2) ? "" : "s";
                        msg = msg + (sote.getTicksSinceLastMaze() - 1) + " tick" + msgs + " late";
                    } else {
                        String msgs = (sote.getTicksSinceLastMaze() == 7) ? "" : "s";
                        msg = msg + (sote.getTicksSinceLastMaze() - 6) + " tick" + msgs + " late (backup)";
                    }
                } else if (sote.getTicksSinceLastMaze() == 1 || sote.getTicksSinceLastMaze() == 6) {
                    msg = msg + " on tick";
                    if (sote.getTicksSinceLastMaze() == 6)
                        msg = msg + " (backup)";
                }
                String finalMsg = msg;
                if (config.showSoteSpecs())
                    sendAuthMessage(finalMsg);
                if (event.getActor() instanceof Player) {
                    Player p = (Player)event.getActor();
                    if (!checkEquipment(p.getPlayerComposition())) {
                        String statusMsg = p.getName() + " specced wearing wrong: " + lastEquipmentCheck;
                        if (config.showMissingSwitches())
                            sendAuthMessage(statusMsg);
                    }
                }
            }
        } else if (sote.state == SotetsegTimer.SotetsegRoomState.PHASE_1) {
            if (event.getActor().getAnimation() == 1378) {
                String msg = event.getActor().getName() + " DWH specced ";
                if (sote.getTicksSinceStart() > 10) {
                    String msgs = (sote.getTicksSinceStart() == 11) ? "" : "s";
                    msg = msg + (sote.getTicksSinceStart() - 10) + " tick" + msgs + " late";
                } else if (sote.getTicksSinceStart() == 10) {
                    msg = msg + " on tick";
                }
                String finalMsg = msg;
                if (config.showSoteSpecs())
                    sendAuthMessage(finalMsg);
                if (event.getActor() instanceof Player) {
                    Player p = (Player)event.getActor();
                    if (!checkEquipment(p.getPlayerComposition())) {
                        String statusMsg = p.getName() + " specced wearing wrong: " + lastEquipmentCheck;
                        if (config.showMissingSwitches())
                            sendAuthMessage(statusMsg);
                    }
                }
            }
        }
    }

    private boolean maidenAnimationUpdate(AnimationChanged event) {
        if (event.getActor().getAnimation() == 1979) {
            String nylostr = "";
            for (MaidenCrab m : crabs) {
                Actor crabTarget = event.getActor().getInteracting();
                if (crabTarget == null)
                    return true;
                NPC targetCrab = (NPC)event.getActor().getInteracting();
                if (targetCrab.getIndex() == m.getIndex())
                    nylostr = m.getName();
            }
            if (nylostr.contains("N3") && !nylostr.contains("30")) {
                String msg = event.getActor().getName() + " cast barrage on N3 instead of S3";
                if (config.show3Cast())
                    sendAuthMessage(msg);
            }
        } else if (event.getActor().getAnimation() == 7618) {
            Actor cT = event.getActor().getInteracting();
            if (cT != null) {
                int x = Math.abs(cT.getWorldLocation().getRegionX() - event.getActor().getWorldLocation().getRegionX());
                int y = Math.abs(cT.getWorldLocation().getRegionY() - event.getActor().getWorldLocation().getRegionY());
                if (Math.max(x, y) > 6) {
                    String msg = event.getActor().getName() + " chinned from " + Math.max(x, y) + " tiles away";
                    if (config.showChinning())
                        sendAuthMessage(msg);
                } else if (Math.max(x, y) < 4) {
                    String msg = event.getActor().getName() + " chinned from " + Math.max(x, y) + " tiles away";
                    if (config.showChinning())
                        sendAuthMessage(msg);
                }
            }
        } else if (event.getActor().getAnimation() == 1378) {
            if (maiden.getActiveTicks() > 7) {
                String msg = event.getActor().getName() + " DWH specced ";
                if (maiden.getActiveTicks() > 10) {
                    String msgs = (maiden.getActiveTicks() == 11) ? "" : "s";
                    msg = msg + (maiden.getActiveTicks() - 10) + " tick" + msgs + " late";
                } else if (maiden.getActiveTicks() == 10) {
                    msg = msg + "on tick";
                } else if (maiden.getActiveTicks() < 10) {
                    msg = msg + (10 - maiden.getActiveTicks()) + " tick early";
                }
                String finalMsg = msg;
                if (config.showMaidenSpecs())
                    sendAuthMessage(finalMsg);
                if (event.getActor() instanceof Player) {
                    Player p = (Player)event.getActor();
                    if (!checkEquipment(p.getPlayerComposition())) {
                        String statusMsg = p.getName() + " specced wearing wrong: " + lastEquipmentCheck;
                        if (config.showMissingSwitches())
                            sendAuthMessage(statusMsg);
                    }
                }
            }
        } else if (event.getActor().getAnimation() == 7642 || event.getActor().getAnimation() == 7643) {
            if (maiden.getActiveTicks() > 7 && maiden.getActiveTicks() < 17) {
                String msg = event.getActor().getName() + " BGS specced ";
                if (maiden.getActiveTicks() > 11) {
                    String msgs = (maiden.getActiveTicks() == 12) ? "" : "s";
                    msg = msg + (maiden.getActiveTicks() - 11) + " tick" + msgs + " late";
                } else if (maiden.getActiveTicks() == 11) {
                    msg = msg + "on tick";
                } else if (maiden.getActiveTicks() < 11) {
                    msg = msg + (11 - maiden.getActiveTicks()) + " tick early";
                }
                String finalMsg = msg;
                if (config.showMaidenSpecs())
                    sendAuthMessage(finalMsg);
            }
        }
        return false;
    }

    private boolean nyloAnimationUpdate(AnimationChanged event) {
        int anim = event.getActor().getAnimation();
        String msg = event.getActor().getName() + " ";
        if (anim == 8056) {
            msg = msg + "scythe";
        } else if (anim == 5061) {
            msg = msg + "bp";
        } else if (anim == 1167) {
            msg = msg + "sang";
        } else if (anim == 7514) {
            msg = msg + "claw spec";
        } else if (anim == 1203) {
            msg = msg + "chally";
        } else if (anim == 426) {
            msg = msg + "tbow";
        } else {
            return true;
        }
        msg = msg + " on tick " + (client.getTickCount() - startTick);
        return false;
    }

    private void bloatAnimationUpdate(AnimationChanged event) {
        if (event.getActor() != null && event.getActor().getName() != null)
            if (event.getActor().getName().equals("Pestilent Bloat"))
                if (event.getActor().getAnimation() == 8082) {
                    bloat.stopWalk();
                } else if (event.getActor().getAnimation() == -1) {
                    bloat.walk();
                }
    }

    private String lastEquipmentCheck = "";

    boolean checkEquipment(PlayerComposition comp) {
        String equipmentStatus = "";
        boolean flag = true;
        int helmet = comp.getEquipmentId(KitType.HEAD);
        if (!HELMS.contains(helmet)) {
            equipmentStatus = equipmentStatus + "Helm, ";
            flag = false;
        }
        int torso = comp.getEquipmentId(KitType.TORSO);
        if (!TORSOS.contains(torso)) {
            equipmentStatus = equipmentStatus + "Torso, ";
            flag = false;
        }
        int weapon = comp.getEquipmentId(KitType.WEAPON);
        if (SPEC_WEAPONS.contains(weapon)) {
            int shield = comp.getEquipmentId(KitType.SHIELD);
            if (!DEFENDERS.contains(shield)) {
                equipmentStatus = equipmentStatus + "Defender, ";
                flag = false;
            }
        }
        int boots = comp.getEquipmentId(KitType.BOOTS);
        if (!BOOTS.contains(boots)) {
            equipmentStatus = equipmentStatus + "Boots, ";
            flag = false;
        }
        int gloves = comp.getEquipmentId(KitType.HANDS);
        if (!GLOVES.contains(gloves)) {
            equipmentStatus = equipmentStatus + "Gloves, ";
            flag = false;
        }
        int cape = comp.getEquipmentId(KitType.CAPE);
        if (!CAPES.contains(cape)) {
            equipmentStatus = equipmentStatus + "Cape, ";
            flag = false;
        }
        int amulet = comp.getEquipmentId(KitType.AMULET);
        if (!AMULETS.contains(amulet)) {
            equipmentStatus = equipmentStatus + "Amulet, ";
            flag = false;
        }
        if (equipmentStatus.length() > 2)
            lastEquipmentCheck = equipmentStatus.substring(0, equipmentStatus.length() - 2);
        return flag;
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (maiden.isActive()) {
            Actor targ = event.getTarget();
            if (targ != null) {
                NPC targNPC;
                try {
                    targNPC = (NPC)targ;
                } catch (ClassCastException e) {
                    return;
                }
                String nylostr = "";
                for (MaidenCrab m : crabs) {
                    if (targNPC.getIndex() == m.getIndex())
                        nylostr = m.getName();
                }
                if (event.getSource() != null)
                    if (event.getSource().getAnimation() == 1979) {
                        if (nylostr.contains("N3") && !nylostr.contains("30")) {
                            String msg = event.getSource().getName() + " cast barrage on N3 instead of S3";
                            if (config.show3Cast())
                                sendAuthMessage(msg);
                        }
                    } else if (event.getSource().getAnimation() == 7618) {
                        Actor cT = event.getTarget();
                        int x = Math.abs(cT.getWorldLocation().getRegionX() - event.getSource().getWorldLocation().getRegionX());
                        int y = Math.abs(cT.getWorldLocation().getRegionY() - event.getSource().getWorldLocation().getRegionY());
                        if (Math.max(x, y) > 6) {
                            String msg = event.getSource().getName() + " chinned from " + Math.max(x, y) + " tiles away";
                            if (config.showChinning())
                                sendAuthMessage(msg);
                        } else if (Math.max(x, y) < 4) {
                            String msg = event.getSource().getName() + " chinned from " + Math.max(x, y) + " tiles away";
                            if (config.showChinning())
                                sendAuthMessage(msg);
                        }
                    }
            }
        }
    }

    /*@Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        hitsplatApplied.getActor().getName();
    }*/

    private void updateVerzik() {
        if (verzikNPC != null) {
            if (verzik.isActive() && verzikNPC.getHealthRatio() == 0 && !verzik.isDead())
                if (verzikNPC.getId() == 8370) {
                    verzik.killP1();
                } else if (verzikNPC.getId() == 8372) {
                    verzik.killP2();
                } else if (verzikNPC.getId() == 8374) {
                    verzik.kill();
                }
            if (verzikNPC.getId() != verzikID) {
                if (verzikID != -1) {
                    if (verzikID == 8369)
                        verzik.start();
                    if (verzikID == 8370) {
                        verzik.endP1();
                    } else if (verzikID == 8372) {
                        verzik.endP2();
                    }
                }
                verzikID = verzikNPC.getId();
            }
        }
    }

    private void updateRoom() {
        room previous = currentRoom;
        if (inRegion(12613)) {
            if (previous != room.MAIDEN) {
                currentRoom = room.MAIDEN;
                enteredMaiden(previous);
            }
        } else if (inRegion(BLOAT_REGION)) {
            if (previous != room.BLOAT) {
                currentRoom = room.BLOAT;
                enteredBloat(previous);
            }
        } else if (inRegion(NYLO_REGION)) {
            if (previous != room.NYLOCAS) {
                currentRoom = room.NYLOCAS;
                enteredNylo(previous);
            }
        } else if (inRegion(SOTETSEG_REGION)) {
            if (previous != room.SOTETSEG) {
                currentRoom = room.SOTETSEG;
                enteredSote(previous);
            }
        } else if (inRegion(XARPUS_REGION)) {
            if (previous != room.XARPUS) {
                currentRoom = room.XARPUS;
                enteredXarpus(previous);
            }
        } else if (inRegion(VERZIK_REGION)) {
            if (previous != room.VERZIK) {
                currentRoom = room.VERZIK;
                enteredVerzik(previous);
            }
        } else if (previous != room.NONE) {
            currentRoom = room.NONE;
            leftRaid(previous);
        }
    }

    private void enteredMaiden(room old) {
        maiden.reset();
    }

    private void enteredBloat(room old) {
        if (maiden.isActive())
            maiden.reset();
        bloat.reset();
    }

    private void enteredNylo(room old) {
        bloat.reset();
        nylo.reset();
    }

    private void enteredSote(room old) {
        nylo.reset();
        sote.reset();
    }

    private void enteredXarpus(room old) {
        sote.reset();
        xarpus.reset();
    }

    private void enteredVerzik(room old) {
        xarpus.reset();
        verzik.reset();
    }

    private void leftRaid(room old) {
        bloatInstStart = 0;
        maiden.reset();
        bloat.reset();
        nylo.reset();
        sote.reset();
        xarpus.reset();
        verzik.reset();
    }

    private void enteredRaid() {}

    private boolean inRegion(int... regions) {
        if (client.getMapRegions() != null)
            for (int i : client.getMapRegions()) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        return false;
    }
}
