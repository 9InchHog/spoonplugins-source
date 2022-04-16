package net.runelite.client.plugins.socket.plugins.socketplanks;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
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
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "Socket - Planks",
        description = "Aint letting these bastards get away with shit",
        tags = {"cox"}
)
@PluginDependency(SocketPlugin.class)
public class SocketPlanksPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private SocketPlanksOverlay overlay;

    @Inject
    private SocketPlanksOverlayPanel overlayPanel;

    public boolean planksDropped = false;
    public boolean planksPickedUp = false;
    public int mostPlanks = 0;
    public int plankCount = 0;
    public boolean chestBuilt = false;
    public int planksDroppedTime = -1;
    public int planksPickedUpTime = -1;
    public int chestBuiltTime = -1;
    public String planksPickedUpTimeStr = "";
    public String chestBuiltTimeStr = "";
    public int splitTimerDelay = 8;
    public String nameGotPlanks = "";
    public WorldPoint planksDroppedTile = null;

    private boolean mirrorMode;

    @Provides
    SocketPlanksConfig provideConfig(ConfigManager configManager) {
        return (SocketPlanksConfig) configManager.getConfig(SocketPlanksConfig.class);
    }

    public SocketPlanksPlugin() {
    }

    protected void startUp() throws Exception {
        reset();
        overlayManager.add(overlay);
        overlayManager.add(overlayPanel);
    }

    protected void shutDown() throws Exception {
        reset();
        overlayManager.remove(overlay);
        overlayManager.remove(overlayPanel);
    }

    protected void reset() {
        planksDropped = false;
        planksPickedUp = false;
        plankCount = 0;
        mostPlanks = 0;
        chestBuilt = false;
        planksDroppedTime = -1;
        planksPickedUpTime = -1;
        chestBuiltTime = -1;
        planksPickedUpTimeStr = "";
        chestBuiltTimeStr = "";
        splitTimerDelay = 8;
        nameGotPlanks = "";
        planksDroppedTile = null;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if(splitTimerDelay > 0 && chestBuilt){
            splitTimerDelay--;
        }
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        if(event.getNpc().getName() != null && event.getNpc().getName().equals("Scavenger beast") && this.client.getVarbitValue(Varbits.IN_RAID) == 1){
            for(ItemStack item : event.getItems()){
                if(item.getId() == ItemID.MALLIGNUM_ROOT_PLANK){
                    if(!planksDropped){
                        planksDropped = true;
                        planksDroppedTime = timeToSeconds(getTime());
                        planksDroppedTile = WorldPoint.fromLocal(client, item.getLocation());
						nameGotPlanks = this.client.getLocalPlayer().getName();
						
                        sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " got planks");
                        JSONObject data = new JSONObject();
                        data.put("player", this.client.getLocalPlayer().getName());
                        data.put("time", planksDroppedTime);
                        data.put("x", planksDroppedTile.getX());
                        data.put("y", planksDroppedTile.getY());
                        data.put("plane", planksDroppedTile.getPlane());
                        JSONObject payload = new JSONObject();
                        payload.put("socketplanksdropped", data);
                        this.eventBus.post(new SocketBroadcastPacket(payload));
                    }
                }
            }
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
        if (this.client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (event.getContainerId() == 93) {
                plankCount = event.getItemContainer().count(ItemID.MALLIGNUM_ROOT_PLANK);
                if(plankCount > mostPlanks && plankCount >= 2 && !planksPickedUp){
                    int time = timeToSeconds(getTime()) - planksDroppedTime;
                    if(time > 15){
                        sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " took " +
                                time + " seconds to pick up the fucking planks");
                    }else {
                        sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " picked up planks");
                    }
                    planksPickedUp = true;
                    planksPickedUpTime = timeToSeconds(getTime());
                    planksPickedUpTimeStr = secondsToTime(planksPickedUpTime - planksDroppedTime);
                    nameGotPlanks = "";

                    JSONObject data = new JSONObject();
                    data.put("player", this.client.getLocalPlayer().getName());
                    data.put("time", planksPickedUpTime);
                    data.put("timeStr", planksPickedUpTimeStr);
                    JSONObject payload = new JSONObject();
                    payload.put("socketplankspickedup", data);
                    this.eventBus.post(new SocketBroadcastPacket(payload));
                }
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if(this.client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (event.getActor().getName() != null && event.getActor().getName().equals(this.client.getLocalPlayer().getName()) &&
                    (event.getActor().getAnimation() == 3676 || event.getActor().getAnimation() == 7049)) {
                if(!chestBuilt) {
                    int time = timeToSeconds(getTime()) - planksPickedUpTime;
                    if(time > 15){
                        sendFlag("<col=ff0000>Holy shit... " + this.client.getLocalPlayer().getName() + " took " +
                                time + " seconds to build the fucking chest");
                    }else {
                        sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " built the chest");
                    }
                    chestBuilt = true;
                    chestBuiltTime = timeToSeconds(getTime());
                    chestBuiltTimeStr = secondsToTime(chestBuiltTime - planksPickedUpTime);
					String totalTime = secondsToTime(chestBuiltTime - planksDroppedTime);
					String msg = "Total Time: <col=ff0000>" + totalTime + "</col> Picked Up: <col=ff0000>" + planksPickedUpTimeStr +
									"</col> Chest Built: <col=ff0000>" + chestBuiltTimeStr;
					this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
					
                    JSONObject data = new JSONObject();
                    data.put("player", this.client.getLocalPlayer().getName());
                    data.put("time", chestBuiltTime);
                    data.put("timeStr", chestBuiltTimeStr);
                    JSONObject payload = new JSONObject();
                    payload.put("socketplanksbuilt", data);
                    this.eventBus.post(new SocketBroadcastPacket(payload));
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
        send.put("socketplanksmsg", data);
        this.eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (payload.has("socketplanksdropped")) {
                planksDropped = true;
                JSONObject data = payload.getJSONObject("socketplanksdropped");

                if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
					nameGotPlanks = data.getString("player");
                    planksDroppedTime = data.getInt("time");
                    int x = data.getInt("x");
                    int y = data.getInt("y");
                    int plane = data.getInt("plane");
                    planksDroppedTile = new WorldPoint(x, y, plane);
                }
            } else if (payload.has("socketplankspickedup")) {
                planksPickedUp = true;
                JSONObject data = payload.getJSONObject("socketplankspickedup");

                if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
                    planksPickedUpTime = data.getInt("time");
                    planksPickedUpTimeStr = data.getString("timeStr");
                    nameGotPlanks = "";
                }
            } else if (payload.has("socketplanksbuilt")) {
				if(!chestBuilt){
					JSONObject data = payload.getJSONObject("socketplanksbuilt");

					if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
						chestBuiltTime = data.getInt("time");
						chestBuiltTimeStr = data.getString("timeStr");
						String totalTime = secondsToTime(chestBuiltTime - planksDroppedTime);
						String msg = "Total Time: <col=ff0000>" + totalTime + "</col> Picked Up: <col=ff0000>" + planksPickedUpTimeStr +
									"</col> Chest Built: <col=ff0000>" + chestBuiltTimeStr;
						this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
					}
				}
				chestBuilt = true;
            } else if(payload.has("socketplanksmsg")){
                JSONArray data = payload.getJSONArray("socketplanksmsg");
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
        if (client.getVarbitValue(Varbits.IN_RAID) != 1) {
            reset();
        }
    }

    public String getTime()
    {
        int seconds = (int) Math.floor(client.getVarbitValue(6386) * .6);
        return secondsToTime(seconds);
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

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            overlayPanel.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlayPanel);
            overlayManager.add(overlayPanel);
            mirrorMode = true;
        }
    }*/
}