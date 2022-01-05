package net.runelite.client.plugins.socket.plugins.socketthieving;

import com.google.inject.Provides;

import java.util.*;
import java.util.function.ToIntFunction;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.socket.plugins.socketthieving.util.Raids1Util;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "Socket - Thieving",
        description = "De0's thieving plugin with the option of Socket! Works without socket too.",
        tags = {"xeric", "thieving", "chambers", "cox", "socket", "de0", "bats", "grubs"}
        )
public class SocketThievingPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BatChestsHighlight overlay;

    @Inject
    private SocketThievingDatabox grubOverlay;

    @Inject
    private ScavHighlight scavOverlay;

    @Inject
    private SocketThievingConfig config;

    @Inject
    private EventBus eventBus;

    public int roomtype;

    private int plane;

    private int base_x;

    private int base_y;

    int room_base_x;

    int room_base_y;

    int rot;

    int wind;

    byte soln;

    Set<Byte> not_solns;

    private int last_grubs;

    int num_grubs;

    GrubCollection gc_local;

    GrubCollection[] gc_others;

    int gc_others_count;

    Comparator<GrubCollection> comparator;

    int lastGrubs = 0;
    int teamTotalGrubs = 0;
    int socketGrubs = 0;
    double nonSocketGrubs = 0;
    ArrayList<String> socketPlayerNames = new ArrayList<>();
    int teamGrubsNeeded = 0;

    boolean hasCovid = false;

    boolean scavDead = false;

    boolean canDump = false;

    public SocketThievingPlugin() {
        this.roomtype = -1;
        this.soln = -1;
        this.not_solns = new HashSet<>();
        this.gc_others = new GrubCollection[99];
        this.gc_others_count = 0;
        this.comparator = Comparator.comparingInt(new ToIntFunction<GrubCollection>() {
            public int applyAsInt(SocketThievingPlugin.GrubCollection v) {
                if (v == SocketThievingPlugin.this.gc_local)
                    return -SocketThievingPlugin.this.num_grubs;
                return -v.num_with_grubs * SocketThievingPlugin.this.config.grubRate() / 100;
            }
        });
    }

    class GrubCollection {
        String displayname;

        int num_opened;

        int num_with_grubs;
    }

    @Provides
    SocketThievingConfig getConfig(ConfigManager configManager) {
        return (SocketThievingConfig) configManager.getConfig(SocketThievingConfig.class);
    }

    protected void startUp() throws Exception {
        if (this.client.getLocalPlayer() != null) {
            sendFlag(this.client.getLocalPlayer().getName() + " turned on Socket Thieving");
        }
    }

    protected void shutDown() throws Exception {
        reset();
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.grubOverlay);
        this.overlayManager.remove(this.scavOverlay);
        sendFlag(this.client.getLocalPlayer().getName() + " turned off Socket Thieving");
    }

    protected void reset() {
        this.not_solns.clear();
        this.gc_local = null;
        for (int i = 0; i < this.gc_others_count; i++)
            this.gc_others[i] = null;
        this.gc_others_count = 0;
        this.num_grubs = 0;
        this.last_grubs = 0;
        this.soln = -1;
        this.roomtype = -1;
        socketGrubs = 0;
        nonSocketGrubs = 0;
        this.teamTotalGrubs = 0;
        teamGrubsNeeded = 0;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (this.client.getVar(Varbits.IN_RAID) == 0) {
            if (this.roomtype != -1)
                try {
                    shutDown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            return;
        }
        int plane = this.client.getPlane();
        int base_x = this.client.getBaseX();
        int base_y = this.client.getBaseY();
        if (this.base_x != base_x || this.base_y != base_y || this.plane != plane) {
            this.base_x = base_x;
            this.base_y = base_y;
            this.plane = plane;
            searchForThieving();
        }
        WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
        int x = wp.getX() - this.client.getBaseX();
        int y = wp.getY() - this.client.getBaseY();
        int type = Raids1Util.getroom_type(this.client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
        if (type != this.roomtype) {
            if (type == 13) {
                this.overlayManager.add(this.overlay);
                this.overlayManager.add(this.grubOverlay);
                this.overlayManager.add(this.scavOverlay);
            } else if (this.roomtype == 13) {
                if (type == 3 && this.config.display4Prep()) {
                    this.overlayManager.remove(this.overlay);
                    this.overlayManager.remove(this.scavOverlay);
                } else {
                    this.overlayManager.remove(this.overlay);
                    this.overlayManager.remove(this.grubOverlay);
                    this.overlayManager.remove(this.scavOverlay);
                }
            } else if (type == 3 && this.config.display4Prep()) {
                this.overlayManager.add(this.grubOverlay);
            }else{
                this.overlayManager.remove(this.overlay);
                this.overlayManager.remove(this.grubOverlay);
                this.overlayManager.remove(this.scavOverlay);
            }
            this.roomtype = type;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned e) {
        int chestX, chestY;
        GameObject obj = e.getGameObject();
        if (obj.getId() != 29745 && obj.getId() != 29743 && obj.getId() != 29744)
            return;
        Point p = e.getTile().getSceneLocation();
        int x = p.getX();
        int y = p.getY();
        if (this.rot == 0) {
            chestX = x - this.room_base_x;
            chestY = y - this.room_base_y;
        } else if (this.rot == 1) {
            chestX = this.room_base_y - y;
            chestY = x - this.room_base_x;
        } else if (this.rot == 2) {
            chestX = this.room_base_x - x;
            chestY = this.room_base_y - y;
        } else {
            chestX = y - this.room_base_y;
            chestY = this.room_base_x - x;
        }
        byte chestno = coordToChestNo(chestX, chestY);
        boolean opened = false;
        boolean grub = false;
        if (obj.getId() == 29744 || obj.getId() == 29745) {
            byte notsoln = solve(chestno);
            if (notsoln != -1)
                this.not_solns.add(Byte.valueOf(notsoln));
            opened = true;
        }
        if (obj.getId() == 29745) {
            grub = true;
        } else if (obj.getId() == 29743 && this.soln == -1) {
            this.soln = solve(chestno);
        }
        if (opened) {
            int angle = obj.getOrientation().getAngle() >> 9;
            int px = x + ((angle == 1) ? -1 : ((angle == 3) ? 1 : 0));
            int py = y + ((angle == 0) ? -1 : ((angle == 2) ? 1 : 0));
            for (Player pl : this.client.getPlayers()) {
                WorldPoint wp = pl.getWorldLocation();
                int plx = wp.getX() - this.client.getBaseX();
                int ply = wp.getY() - this.client.getBaseY();
                if (plx == px && ply == py) {
                    if (grub && pl == this.client.getLocalPlayer()) {
                        add_grubs_local();
                        break;
                    }
                    if (grub) {
                        add_grubs_other(pl);
                        break;
                    }
                    add_empty(pl);
                    break;
                }
            }
        }
        int nonSocketGrubsFloor = (int) Math.floor(nonSocketGrubs);
        teamTotalGrubs = socketGrubs + nonSocketGrubsFloor;
        if(config.dumpMsg() && teamTotalGrubs >= teamGrubsNeeded) {
            if (!canDump) {
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>DUMP YOUR GRUBS!", "");
            }
            canDump = true;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned e) {
        GameObject obj = e.getGameObject();
        if (obj.getId() != 26209)
            return;
        WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
        int px = wp.getX() - this.client.getBaseX();
        int py = wp.getY() - this.client.getBaseY();
        int type = Raids1Util.getroom_type(this.client.getInstanceTemplateChunks()[plane][px / 8][py / 8]);
        if (config.thievingRateMsg() && obj.getId() == 26209 && type == 13) {
            if (!scavDead) {
                double rate = ((double) gc_local.num_with_grubs / (double) gc_local.num_opened) * 100;
                String rateStr = Double.toString(rate).substring(0, 4);
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Chest Success Rate: <col=ff0000>" + gc_local.num_with_grubs + "/" + gc_local.num_opened + " (" + rateStr + "%)", "");
            }
            scavDead = true;
        }
    }

    private void add_grubs_local() {
        GrubCollection gc = this.gc_local;
        if (gc == null) {
            gc = this.gc_local = new GrubCollection();
            gc.displayname = this.client.getLocalPlayer().getName();
        }
        int grubs = this.client.getItemContainer(InventoryID.INVENTORY)
                .count(20885);
        int delta = grubs - this.last_grubs;

        JSONObject data = new JSONObject();
        data.put("player", this.client.getLocalPlayer().getName());
        data.put("grubs", delta);
        JSONObject payload = new JSONObject();
        payload.put("socketgrubs", data);
        this.eventBus.post(new SocketBroadcastPacket(payload));
        socketGrubs += delta;

        this.num_grubs += delta;
        this.last_grubs = grubs;
        gc.num_opened++;
        gc.num_with_grubs++;
    }

    private void add_grubs_other(Player pl) {
        GrubCollection gc = null;
        int hash = pl.getName().hashCode();
        for (int i = 0; i < this.gc_others_count; i++) {
            if (hash == (this.gc_others[i]).displayname.hashCode()) {
                gc = this.gc_others[i];
                break;
            }
        }
        if (gc == null) {
            gc = this.gc_others[this.gc_others_count++] = new GrubCollection();
            gc.displayname = pl.getName();
        }
        gc.num_opened++;
        gc.num_with_grubs++;
        Arrays.sort(this.gc_others, 0, this.gc_others_count, this.comparator);

        if(!socketPlayerNames.contains(pl.getName().toLowerCase())){
            double rate = config.grubRate();
            nonSocketGrubs += rate / 100;
        }
    }

    private void add_empty(Player pl) {
        GrubCollection gc = this.gc_local;
        if (gc == null) {
            gc = this.gc_local = new GrubCollection();
            gc.displayname = this.client.getLocalPlayer().getName();
        }
        int hash = pl.getName().hashCode();
        if (hash != gc.displayname.hashCode()) {
            gc = null;
            for (int i = 0; i < this.gc_others_count; i++) {
                if (hash == (this.gc_others[i]).displayname.hashCode()) {
                    gc = this.gc_others[i];
                    break;
                }
            }
            if (gc == null) {
                gc = this.gc_others[this.gc_others_count++] = new GrubCollection();
                gc.displayname = pl.getName();
            }
        }
        gc.num_opened++;
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e) {
        if (e.getContainerId() == 93) {
            this.last_grubs = e.getItemContainer().count(20885);

            if (this.config.covidMsg()) {
                if (e.getItemContainer().count(20883) >= 3) {
                    if (!hasCovid) {
                        sendFlag(this.client.getLocalPlayer().getName() + " has contracted covid.");
                    }
                    hasCovid = true;
                }
            }
        }
    }

    private void searchForThieving() {
        int[][] templates = this.client.getInstanceTemplateChunks()[this.plane];
        for (int cx = 0; cx < 13; cx += 4) {
            for (int cy = 0; cy < 13; cy += 4) {
                int template = templates[cx][cy];
                int tx = template >> 14 & 0x3FF;
                int ty = template >> 3 & 0x7FF;
                if (Raids1Util.getroom_type(template) == 13) {
                    this.rot = Raids1Util.getroom_rot(template);
                    if (this.rot == 0) {
                        this.room_base_x = cx - (tx & 0x3) << 3;
                        this.room_base_y = cy - (ty & 0x3) << 3;
                    } else if (this.rot == 1) {
                        this.room_base_x = cx - (ty & 0x3) << 3;
                        this.room_base_y = cy + (tx & 0x3) << 3 | 0x7;
                    } else if (this.rot == 2) {
                        this.room_base_x = cx + (tx & 0x3) << 3 | 0x7;
                        this.room_base_y = cy + (ty & 0x3) << 3 | 0x7;
                    } else if (this.rot == 3) {
                        this.room_base_x = cx + (ty & 0x3) << 3 | 0x7;
                        this.room_base_y = cy - (tx & 0x3) << 3;
                    }
                    this.wind = Raids1Util.getroom_winding(template);
                }
            }
        }
    }

    private byte solve(byte poisonchestno) {
        byte[][] solns = BatData.CHEST_SOLNS[this.wind][this.rot];
        for (byte i = 0; i < solns.length; i = (byte) (i + 1)) {
            for (byte j = 0; j < (solns[i]).length; j = (byte) (j + 1)) {
                if (solns[i][j] == poisonchestno)
                    return i;
            }
        }
        return -1;
    }

    private byte coordToChestNo(int x, int y) {
        byte[][] locs = BatData.CHEST_LOCS[this.wind];
        byte i;
        for (i = 0; i < locs.length; i = (byte)(i + 1)) {
            if (locs[i][0] == x && locs[i][1] == y)
                return (byte)(i + 1);
        }
        return -1;
    }

    private void sendFlag(String msg) {
        JSONArray data = new JSONArray();
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", msg);
        data.put(jsonmsg);
        JSONObject send = new JSONObject();
        send.put("socketgrubsalt", data);
        this.eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (payload.has("socketgrubs")) {
                JSONObject data = payload.getJSONObject("socketgrubs");

                if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
                    int grubCount = data.getInt("grubs");
                    socketGrubs += grubCount;
                }
            } else if(payload.has("socketgrubsalt")){
                JSONArray data = payload.getJSONArray("socketgrubsalt");
                JSONObject jsonmsg = data.getJSONObject(0);
                String msg = jsonmsg.getString("msg");
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        boolean tempInRaid = client.getVar(Varbits.IN_RAID) == 1;

        // if the player's raid state has changed
        if (!tempInRaid) {
            reset();
            this.overlayManager.remove(this.overlay);
            this.overlayManager.remove(this.grubOverlay);
            this.overlayManager.remove(this.scavOverlay);
        }else {
            if (this.client.getVarbitValue(5424) == 1){
                teamGrubsNeeded = 30;
            } else{
                teamGrubsNeeded = (this.client.getVarbitValue(5424) * 16) - 1;
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        String text = Text.standardize(event.getMessageNode().getValue());
        if (text.contains("member (") || text.contains("members (")) {
                text = text.substring(text.indexOf(":") + 1).trim();
            this.socketPlayerNames.clear();
            byte b;
            int i;
            String[] arrayOfString;
            for (i = (arrayOfString = text.split(",")).length, b = 0; b < i; ) {
                String str = arrayOfString[b];
                str = str.trim();
                if (!"".equals(str))
                    this.socketPlayerNames.add(str.toLowerCase());
                b++;
            }
        }
        if (text.contains("any active socket server connections were closed")) {
            this.socketPlayerNames.clear();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if(this.client.getLocalPlayer() != null) {
            WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
            int x = wp.getX() - this.client.getBaseX();
            int y = wp.getY() - this.client.getBaseY();
            int type = Raids1Util.getroom_type(this.client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
            if (!config.display4Prep() && type != 13) {
                this.overlayManager.remove(this.grubOverlay);
            } else if (config.display4Prep() && type == 3) {
                this.overlayManager.add(this.grubOverlay);
            }
        }
    }
}

