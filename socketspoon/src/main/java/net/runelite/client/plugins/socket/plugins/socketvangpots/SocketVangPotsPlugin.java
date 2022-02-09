package net.runelite.client.plugins.socket.plugins.socketvangpots;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

import static net.runelite.client.plugins.socket.plugins.socketvangpots.CoxUtil.*;

@Extension
@PluginDescriptor(
        name = "Socket - Vanguard Pots",
        description = "Lets the prepper know how many Overloads were dropped at Vanguards",
        tags = {"cox", "chambers", "xeric", "spoon", "spoonlite", "overload", "raid"}
)
@PluginDependency(SocketPlugin.class)
public class SocketVangPotsPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private SocketVangPotsConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketVangsOverlayPanel overlay;

    public int overloadsDropped = 0;

    public boolean inRoom = false;
    private int roomtype = -1;
    private int plane, base_x, base_y;
    int room_base_x, room_base_y;
    int rot, wind;

    @Provides
    SocketVangPotsConfig provideConfig(ConfigManager configManager) {
        return (SocketVangPotsConfig) configManager.getConfig(SocketVangPotsConfig.class);
    }

    public SocketVangPotsPlugin() {
    }

    protected void startUp() throws Exception {
        reset();
    }

    protected void shutDown() throws Exception {
        reset();
    }

    protected void reset() {
        overloadsDropped = 0;
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        if(event.getNpc().getName() != null && event.getNpc().getName().equalsIgnoreCase("vanguard") && client.getVar(Varbits.IN_RAID) == 1 && client.getLocalPlayer() != null){
            for(ItemStack item : event.getItems()){
                if(item.getId() == ItemID.OVERLOAD_4_20996){
                    overloadsDropped++;
                    sendFlag("<col=ff0000>" + client.getLocalPlayer().getName() + " got an Overload");
                    JSONObject data = new JSONObject();
                    data.put("player", client.getLocalPlayer().getName());
                    JSONObject payload = new JSONObject();
                    payload.put("socketvangpots", data);
                    eventBus.post(new SocketBroadcastPacket(payload));
                }
            }
        }
    }

    private void sendFlag(String msg) {
        JSONArray data = new JSONArray();
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", msg);
        data.put(jsonmsg);
        JSONObject send = new JSONObject();
        send.put("socketvangpotsmsg", data);
        eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (payload.has("socketvangpots")) {
                JSONObject data = payload.getJSONObject("socketvangpots");
                if(!data.getString("player").equals(client.getLocalPlayer().getName())){
                    overloadsDropped++;
                }
            } else if(payload.has("socketvangpotsmsg") && config.showChatMessage()){
                JSONArray data = payload.getJSONArray("socketvangpotsmsg");
                JSONObject jsonmsg = data.getJSONObject(0);
                String msg = jsonmsg.getString("msg");
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (client.getVar(Varbits.IN_RAID) != 1) {
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (client.getVar(Varbits.IN_RAID) == 0) {
            // player has left the raid
            if (roomtype != -1)
                try {
                    shutDown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            return;
        }
        int plane = client.getPlane();
        int base_x = client.getBaseX();
        int base_y = client.getBaseY();
        if (this.base_x != base_x || this.base_y != base_y || this.plane != plane) {
            // scene was reloaded
            this.base_x = base_x;
            this.base_y = base_y;
            this.plane = plane;
            searchForVanguards();
        }
        WorldPoint wp = client.getLocalPlayer().getWorldLocation();
        int x = wp.getX() - client.getBaseX();
        int y = wp.getY() - client.getBaseY();
        int type = CoxUtil.getroom_type(client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
        if (type != roomtype) {
            if (type == VANGUARDS || type == FARMING) {
                // player has entered vanguards/prep rooms
                overlayManager.add(overlay);
            } else if (roomtype == VANGUARDS || roomtype == FARMING) {
                // player has left vanguards/prep rooms
                overlayManager.remove(overlay);
            }
            roomtype = type;
        }
    }

    private void searchForVanguards() {
        int[][] templates = client.getInstanceTemplateChunks()[plane];
        for (int cx = 0; cx < 13; cx += 4) {
            for (int cy = 0; cy < 13; cy += 4) {
                int template = templates[cx][cy];
                // PP_XXXXXXXXXX_YYYYYYYYYYY_RR0
                int tx = template >> 14 & 0x3FF;
                int ty = template >> 3 & 0x7FF;
                if (CoxUtil.getroom_type(template) == VANGUARDS) {
                    rot = CoxUtil.room_rot(template);
                    if (rot == 0) {
                        room_base_x = (cx - (tx & 0x3)) << 3;
                        room_base_y = (cy - (ty & 0x3)) << 3;
                    } else if (rot == 1) {
                        room_base_x = (cx - (ty & 0x3)) << 3;
                        room_base_y = (cy + (tx & 0x3)) << 3 | 7;
                    } else if (rot == 2) {
                        room_base_x = (cx + (tx & 0x3)) << 3 | 7;
                        room_base_y = (cy + (ty & 0x3)) << 3 | 7;
                    } else if (rot == 3) {
                        room_base_x = (cx + (ty & 0x3)) << 3 | 7;
                        room_base_y = (cy - (tx & 0x3)) << 3;
                    }

                    wind = CoxUtil.room_winding(template);
                }
            }
        }
    }
}