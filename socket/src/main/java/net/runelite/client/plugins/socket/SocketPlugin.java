package net.runelite.client.plugins.socket;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.hash.AES256;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.*;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static net.runelite.api.NpcID.*;

@Slf4j
@Extension
@PluginDescriptor(
        name = "Socket",
        description = "Socket connection for broadcasting messages across clients.",
        tags = {"socket", "server", "discord", "connection", "broadcast"},
        enabledByDefault = false
)

public class SocketPlugin extends Plugin {

    // Config version changes between updates, hence we use a global variable.
    public static final String CONFIG_VERSION = "Socket Plugin v2.1.0";

    // To help users who decide to use weak passwords.
    public static final String PASSWORD_SALT = "$P@_/gKR`y:mv)6K";

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private Client client;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private EventBus eventBus;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private ClientThread clientThread;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private SocketConfig config;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private InfoBoxManager infoBoxManager;

    @Provides
    SocketConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketConfig.class);
    }

    // This variables controls the next UNIX epoch time to establish the next connection.
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private long nextConnection;

    // This variables controls the current active connection.
    public SocketConnection connection = null;

    private SocketInfobox connectionIB = null;
    final BufferedImage icon_Connected = ImageUtil.loadImageResource(getClass(), "icon_Connected.png");
    final BufferedImage icon_Disconnected = ImageUtil.loadImageResource(getClass(), "icon_Disconnected.png");
    final BufferedImage icon_Ready = ImageUtil.loadImageResource(getClass(), "icon_Ready.png");
    public String connectionState = "";

    public static SocketPlugin instance = null;

    //Player status extended
    private DeferredCheck deferredCheck;

    @Override
    protected void startUp()
    {
        instance = this;

        infoBoxManager.removeInfoBox(connectionIB);

        this.nextConnection = 0L;

        eventBus.register(SocketReceivePacket.class);
        eventBus.register(SocketBroadcastPacket.class);

        eventBus.register(SocketPlayerJoin.class);
        eventBus.register(SocketPlayerLeave.class);

        eventBus.register(SocketStartup.class);
        eventBus.register(SocketShutdown.class);

        eventBus.post(new SocketStartup());
        connectionState = "";
    }

    @Override
    protected void shutDown()
    {
        instance = null;

        infoBoxManager.removeInfoBox(connectionIB);

        eventBus.post(new SocketShutdown());

        eventBus.unregister(SocketReceivePacket.class);
        eventBus.unregister(SocketBroadcastPacket.class);

        eventBus.unregister(SocketPlayerJoin.class);
        eventBus.unregister(SocketPlayerLeave.class);

        eventBus.unregister(SocketStartup.class);
        eventBus.unregister(SocketShutdown.class);

        if (connection != null)
            connection.terminate(true);

        connectionState = "";
    }

    private SocketInfobox createInfoBox(BufferedImage image, String status) {
        return new SocketInfobox(image, config, this, status);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Attempt connecting, or re-establishing connection to the socket server, only when the user is logged in.
        if (client.getGameState() == GameState.LOGGED_IN) {
            statCheckOnGameTick(); //Player status extended

            if (connection != null) { // If an connection is already being established, ignore.
                if (config.infobox()) {
                    if (!connection.getState().toString().equals(connectionState)) {
                        connectionState = connection.getState().toString();
                        infoBoxManager.removeInfoBox(connectionIB);

                        switch (connection.getState()) {
                            case DISCONNECTED:
                            case TERMINATED:
                                connectionIB = createInfoBox(icon_Disconnected, "Disconnected");
                                break;
                            case CONNECTING:
                                connectionIB = createInfoBox(icon_Ready, "Connecting...");
                                break;
                            case CONNECTED:
                                connectionIB = createInfoBox(icon_Connected, "Connected");
                                break;
                        }
                        infoBoxManager.addInfoBox(connectionIB);
                    }
                }

                SocketState state = connection.getState();
                if (state == SocketState.CONNECTING || state == SocketState.CONNECTED)
                    return;
            }

            if (System.currentTimeMillis() >= nextConnection) { // Create a new connection.
                nextConnection = System.currentTimeMillis() + 30000L;
                connection = new SocketConnection(this, client.getLocalPlayer().getName());
                new Thread(connection).start(); // Handler blocks, so run it on a separate thread.
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("infobox")) {
            if (config.infobox()) {
                infoBoxManager.addInfoBox(connectionIB);
            } else {
                infoBoxManager.removeInfoBox(connectionIB);
            }
        }

        if (config.disableChatMessages())
        {
            return;
        }

        // Notify the user to restart the plugin when the config changes.
        if (event.getGroup().equals(CONFIG_VERSION) && !event.getKey().equals("infobox"))
            clientThread.invoke(() -> client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=b4281e>Configuration changed. Please restart the plugin to see updates.", null));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        // Terminate all connections to the socket server when the user logs out.
        if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            if (connection != null)
            {
                connection.terminate(false);
            }
        }
    }

    @Subscribe
    public void onSocketBroadcastPacket(SocketBroadcastPacket packet)
    {
        try
        {
            // Handles the packets that alternative plugins broadcasts.
            if (connection == null || connection.getState() != SocketState.CONNECTED)
                return;

            String data = packet.getPayload().toString();
            log.debug("Deploying packet from client: {}", data);

            String secret = config.getPassword() + PASSWORD_SALT;

            JSONObject payload = new JSONObject();
            payload.put("header", SocketPacket.BROADCAST);
            payload.put("payload", AES256.encrypt(secret, data)); // Payload is now an encrypted string.

            PrintWriter outputStream = connection.getOutputStream();
            synchronized (outputStream)
            {
                outputStream.println(payload.toString());
            }
        } catch (Exception e) { // Oh no, something went wrong!
            e.printStackTrace();
            log.error("An error has occurred while trying to broadcast a packet.", e);
        }
    }

    //------------------------------------------------------------//
    // Player status extended
    //------------------------------------------------------------//
    private void statCheckOnGameTick() {
        if (client == null || client.getLocalPlayer() == null)
            return;
        if (deferredCheck != null && client.getTickCount() == deferredCheck.getTick()) {
            checkStats();
            deferredCheck = null;
        }
    }

    private boolean isInRegion(int regionID) {
        List<Integer> regions = Arrays.asList(12631, 13125, 13122, 13123, 13379, 12612, 12611, 12867);
        return regions.contains(regionID);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        onCheckAnimationChanged(event);
    }

    protected static final Set<Integer> VERZIK_P2_IDS = ImmutableSet.of(
            VERZIK_VITUR_8372, VERZIK_VITUR_10833, VERZIK_VITUR_10850
    );

    private boolean ignoredNPCs(NPC target) {
        if (target != null && target.getName() != null) {
            return !VERZIK_P2_IDS.contains(target.getId());
        }
        return false;
    }

    private boolean otherShitBow(int i) {
        int[] e = { 861, 12788, 22550, 22547 };
        for (int i2 : e) {
            if (i2 == i)
                return true;
        }
        return false;
    }

    private void checkStats() {
        int anim = deferredCheck.getAnim();
        int hammerBop = 401;
        int godBop = 7045;
        int bow = 426;
        int clawSpec = 7514;
        int clawBop = 393;
        int whip = 1658;
        int chalyBop = 440;
        int chalySpec = 1203;
        int scy = 8056;
        int bggsSpec = 7643;
        int bggsSpec2 = 7642;
        int hammerSpec = 1378;
        int del = 1100;
        int lanceSmack = 8290;
        int lancePoke = 8288;
        int[] hits = { lancePoke, lanceSmack, clawSpec, clawBop, whip, chalySpec, scy, bggsSpec, bggsSpec2, hammerSpec };
        for (int i : hits) {
            if (anim == i) {
                int lvl = client.getBoostedSkillLevel(Skill.STRENGTH);
                boolean piety = deferredCheck.isPiety();
                boolean is118 = lvl >= 118;
                if (!piety || !is118) {
                    String s = "attacked";
                    if (i == clawSpec) {
                        s = "claw speced";
                    } else if (i == chalySpec) {
                        s = "chally speced";
                    } else if (i == bggsSpec || i == bggsSpec2) {
                        s = "bgs speced";
                    } else if (i == hammerSpec) {
                        s = "hammer speced";
                    }

                    String s2 = "";
                    if (!piety) {
                        if (!is118) {
                            s2 = " with " + lvl + " strength and without piety.";
                        } else {
                            s2 = " without piety.";
                        }
                    } else {
                        s2 = " with " + lvl + " strength.";
                    }

                    flagMesOut("You " + s + s2);
                }
                break;
            }
        }
    }

    public static int getCurrentRegionID(Client client) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return -1;
        } else {
            WorldPoint wp = WorldPoint.fromLocalInstance(client, localPlayer.getLocalLocation());
            return wp == null ? -1 : wp.getRegionID();
        }
    }

    private void onCheckAnimationChanged(AnimationChanged event) {
        if (event != null) {
            if (event.getActor() instanceof Player) {
                Player p = (Player) event.getActor();
                if (p == null) {
                    return;
                }

                int anim = p.getAnimation();
                if (p.getPlayerComposition() == null) {
                    return;
                }

                int wep = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);
                int hammerBop = 401;
                int godBop = 7045;
                int bow = 426;
                int lanceSmack = 8290;
                int lancePoke = 8288;
                int clawSpec = 7514;
                int clawBop = 393;
                int whip = 1658;
                int chalyBop = 440;
                int chalySpec = 1203;
                int scy = 8056;
                int bggsSspec = 7643;
                int hammerSpec = 1378;
                int trident = 1167;
                int surge = 7855;
                Actor interacting = p.getInteracting();
                NPC target = null;
                if (p.getInteracting() != null && p.getInteracting() instanceof NPC) {
                    target = (NPC)interacting;
                }

                if (p.equals(client.getLocalPlayer()) && anim != 0 && anim != -1) {
                    if (!ignoredNPCs(target)) {
                        int style = client.getVar(VarPlayer.ATTACK_STYLE);
                        if (anim == scy) {
                            String b = "";
                            if (style == 0) {
                                b = "accurate";
                            } else if (style == 2) {
                                b = "crush";
                            } else if (style == 3) {
                                b = "defensive";
                            }

                            if (!b.equals("")) {
                                if (isInRegion(getCurrentRegionID(client))) {
                                    flagMesOut("You scythed on " + b + ".");
                                } else if (!b.equals("crush")) {
                                    flagMesOut("You scythed on " + b + ".");
                                }
                            }
                        } else if (anim == bow && !otherShitBow(wep) && !client.isPrayerActive(Prayer.RIGOUR)) {
                            flagMesOut("You bowed without rigour active.");
                        } else if (anim == hammerBop && wep == 13576) {
                            flagMesOut("You hammer bopped.");
                        } else if (anim == godBop) {
                            flagMesOut("You godsword bopped.");
                        } else if (anim == chalyBop) {
                            flagMesOut("You chaly poked.");
                        }
                    }

                    deferredCheck = new DeferredCheck(client.getTickCount(), anim, wep, client.isPrayerActive(Prayer.PIETY));
                }
            }
        }
    }

    private void flagMesOut(String mes) {
        if (client != null && client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null) {
            String finalS = mes.toLowerCase().replaceAll("you ", client.getLocalPlayer().getName() + " ");
            JSONArray data = new JSONArray();
            JSONObject json$ = new JSONObject();
            json$.put("print", finalS);
            json$.put("sender", client.getLocalPlayer().getName());
            int[] mapRegions = (client.getMapRegions() == null) ? new int[0] : client.getMapRegions();
            json$.put("mapregion", Arrays.toString(mapRegions));
            json$.put("raidbit", client.getVar(Varbits.IN_RAID));
            data.put(json$);
            JSONObject send = new JSONObject();
            send.put("sLeech", data);
            eventBus.post(new SocketBroadcastPacket(send));
        }
    }

    public static class DeferredCheck {
        @Getter
        @Setter
        private int tick;
        @Getter
        @Setter
        private int anim;
        @Getter
        @Setter
        private int wep;
        @Getter
        @Setter
        private boolean piety;

        public DeferredCheck(int tick, int anim, int wep, boolean piety) {
            this.tick = tick;
            this.anim = anim;
            this.wep = wep;
            this.piety = piety;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof DeferredCheck))
                return false;
            DeferredCheck other = (DeferredCheck)o;
            return other.canEqual(this) && (getTick() == other.getTick()
                    && (getAnim() == other.getAnim() && (getWep() == other.getWep() && (isPiety() == other.isPiety()))));
        }

        protected boolean canEqual(Object other) {
            return other instanceof DeferredCheck;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + getTick();
            result = result * 59 + getAnim();
            result = result * 59 + getWep();
            return result * 59 + (isPiety() ? 79 : 97);
        }

        public String toString() {
            return "SocketPlugin.DeferredCheck(tick=" + getTick() + ", anim=" + getAnim() + ", wep=" + getWep() + ", piety=" + isPiety() + ")";
        }
    }
}
