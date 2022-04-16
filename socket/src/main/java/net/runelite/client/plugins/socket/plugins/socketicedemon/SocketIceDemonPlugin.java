package net.runelite.client.plugins.socket.plugins.socketicedemon;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.socket.plugins.socketicedemon.util.Raids1Util;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;

@Extension
@PluginDescriptor(
        name = "Socket - Ice Demon",
        description = "Socket ice demon",
        tags = {"xeric", "iceDemon", "chambers", "cox", "socket"}
        )
@PluginDependency(SocketPlugin.class)
public class SocketIceDemonPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketIceDemonOverlay overlay;

    @Inject
    private SocketIceDemonPanelOverlay panelOverlay;

    @Inject
    private SocketIceDemonConfig config;

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

    int lastKindling = 0;
    int teamTotalKindlingCut = 0;
	int teamTotalKindlingLit = 0;
    int teamKindlingNeeded = 0;
    boolean dumpKindling = false;
	boolean allKindlingLit = false;
	int litBraziers = 0;
	boolean cuttingTree = false;
	boolean lightingBrazier = false;
	ArrayList<GameObject> unlitBrazierList = new ArrayList<>();
	NPC iceDemon = null;
	int iceDemonActivateTicks = 10;
	boolean iceDemonActive = false;
	int deadTree = 29764;
	boolean treeKilled = false;
	ArrayList<Integer> chopAnimationList = new ArrayList<>(Arrays.asList(879, 877, 875, 873, 871, 869, 867, 8303, 2846, 2117, 7264, 8324, 8778, 24));

	ArrayList<String> playerNameList = new ArrayList<>();
	ArrayList<Integer> playerKindlingList = new ArrayList<>();
	boolean dumpedIntoLit = false;
	boolean litMessage = false;

    public SocketIceDemonPlugin() {
        this.roomtype = -1;
    }

	private boolean mirrorMode;

    @Provides
    SocketIceDemonConfig getConfig(ConfigManager configManager) {
        return (SocketIceDemonConfig) configManager.getConfig(SocketIceDemonConfig.class);
    }

    protected void startUp() throws Exception {
		reset();
    }

    protected void shutDown() throws Exception {
        reset();
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.panelOverlay);
    }

    protected void reset() {
        this.roomtype = -1;
        lastKindling = 0;
		teamTotalKindlingCut = 0;
		teamTotalKindlingLit = 0;
		teamKindlingNeeded = 0;
		dumpKindling = false;
		allKindlingLit = false;
		litBraziers = 0;
		cuttingTree = false;
		lightingBrazier = false;
        unlitBrazierList.clear();
		iceDemon = null;
		iceDemonActivateTicks = 10;
		iceDemonActive = false;
		treeKilled = false;
		playerNameList.clear();
		playerKindlingList.clear();
		dumpedIntoLit = false;
		litMessage = false;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (this.client.getVarbitValue(Varbits.IN_RAID) == 0) {
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
            searchForIceDemon();
        }
        WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
        int x = wp.getX() - this.client.getBaseX();
        int y = wp.getY() - this.client.getBaseY();
        int type = Raids1Util.getroom_type(this.client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
        if (type != this.roomtype) {
            if (type == 12) {
                this.overlayManager.add(this.overlay);
                this.overlayManager.add(this.panelOverlay);
            } else if (this.roomtype == 12) {
                if (type == 2 && this.config.display4Scav()) {
                    this.overlayManager.remove(this.overlay);
                } else {
                    this.overlayManager.remove(this.panelOverlay);
                    this.overlayManager.remove(this.overlay);
                }
            } else if (type == 2 && this.config.display4Scav()) {
                this.overlayManager.add(this.panelOverlay);
            }else{
                this.overlayManager.remove(this.panelOverlay);
                this.overlayManager.remove(this.overlay);
            }
            this.roomtype = type;
        }
		
		if(iceDemonActive){
			iceDemonActivateTicks--;
			if(iceDemonActivateTicks <= 0){
				iceDemonActive = false;
				iceDemonActivateTicks = 10;
				iceDemon = null;
			}
		}
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned e) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(e.getGameObject().getId() == 29748){
				litBraziers++;
				if(litBraziers > 4){
					litBraziers = 4;
				}
			}else if(e.getGameObject().getId() == 29747){
				if(unlitBrazierList.size() < 4) {
					if(e.getGameObject().getPlane() == this.client.getPlane()) {
						unlitBrazierList.add(e.getGameObject());
					}
				}
			}else if(e.getGameObject().getId() == deadTree){
				LocalPoint treeLP = LocalPoint.fromWorld(client, e.getGameObject().getWorldLocation());
				LocalPoint playerLP = LocalPoint.fromWorld(client, this.client.getLocalPlayer().getWorldLocation());
				if(treeLP != null && playerLP != null) {
					if (e.getGameObject().getLocalLocation().distanceTo(this.client.getLocalPlayer().getLocalLocation()) == 128) {
						cuttingTree = true;
						treeKilled = true;
					}
				}
			}
		}
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned e) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(e.getGameObject().getId() == 29748){
				litBraziers--;
				if(litBraziers < 0){
					litBraziers = 0;
				}
			}else if(e.getGameObject().getId() == 29747){
				if(e.getGameObject().getPlane() == this.client.getPlane()) {
					unlitBrazierList.remove(e.getGameObject());
				}
			}
		}
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(event.getGraphicsObject().getId() == 188){
				iceDemonActive = true;
				iceDemonActivateTicks = 10;
			}
		}
    }

	@Subscribe
    private void onNpcSpawned(NpcSpawned event) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(event.getNpc().getId() == 7584){
				iceDemon = event.getNpc();
			}
		}
    }
	
	@Subscribe
    private void onNpcDespawned(NpcDespawned event) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(event.getNpc().getId() == 7584){
				iceDemon = null;
			}
		}
    }
	
	@Subscribe
    public void onAnimationChanged(AnimationChanged e) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if(e.getActor().getName() != null && this.client.getLocalPlayer() != null) {
				if (e.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
					if (chopAnimationList.contains(e.getActor().getAnimation())) {
						cuttingTree = true;
					} else if (e.getActor().getAnimation() == 3687 || e.getActor().getAnimation() == 832 && this.roomtype == 12) {
						if(e.getActor().getAnimation() == 832){
							dumpedIntoLit = true;
						}
						lightingBrazier = true;
					} else {
						cuttingTree = false;
					}
				}
			}
		}
    }
	
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			if (e.getContainerId() == 93) {
				int currentKindling = e.getItemContainer().count(20799);
				if(cuttingTree){
					if(currentKindling > lastKindling){
						int diff = currentKindling - lastKindling;
						JSONObject data = new JSONObject();
						data.put("player", this.client.getLocalPlayer().getName());
						data.put("kindling", diff);
						JSONObject payload = new JSONObject();
						payload.put("socketicecut", data);
						this.eventBus.post(new SocketBroadcastPacket(payload));
						
						teamTotalKindlingCut += diff;
						if(teamTotalKindlingCut >= teamKindlingNeeded && config.dumpMsg()){
							if (!dumpKindling) {
								this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>DUMP YOUR KINDLING!", "");
							}
							dumpKindling = true;
						}

						if(!playerNameList.contains(this.client.getLocalPlayer().getName())) {
							playerNameList.add(this.client.getLocalPlayer().getName());
							playerKindlingList.add(0);
						}
						int index = playerNameList.indexOf(this.client.getLocalPlayer().getName());
						playerKindlingList.set(index, playerKindlingList.get(index) + diff);
					}
					
					if(treeKilled){
						treeKilled = false;
						cuttingTree = false;
					}
				}else if(lightingBrazier){
					if(currentKindling == 0) {
						JSONObject data = new JSONObject();
						data.put("player", this.client.getLocalPlayer().getName());
						data.put("kindling", lastKindling);
						JSONObject payload = new JSONObject();
						payload.put("socketicelight", data);
						this.eventBus.post(new SocketBroadcastPacket(payload));

						if(this.client.getVarbitValue(5424) == 1) {
							if(lastKindling <= 17) {
								this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000> Are you daft, cunt?!", "");
							}
						}else {
							if(dumpedIntoLit) {
								if (litBraziers < 4) {
									sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " dumped " + lastKindling + " kindling into a lit brazier");
									litMessage = true;
								}
							}

							if (!litMessage && lastKindling <= 17) {
								sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " only dumped " + lastKindling + " kindling");
							}
							litMessage = false;
							dumpedIntoLit = false;
						}

						teamTotalKindlingLit += lastKindling;
						if(config.dumpMsg() && teamTotalKindlingLit >= teamKindlingNeeded) {
							if (!allKindlingLit) {
								this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>KINDLING DUMPED.... Ice Demon starting!", "");
							}
							allKindlingLit = true;
						}
					}
					lightingBrazier = false;
				}
				lastKindling = currentKindling;
			}
		}
    }

    private void searchForIceDemon() {
        int[][] templates = this.client.getInstanceTemplateChunks()[this.plane];
        for (int cx = 0; cx < 12; cx += 4) {
            for (int cy = 0; cy < 12; cy += 4) {
                int template = templates[cx][cy];
                int tx = template >> 14 & 0x3FF;
                int ty = template >> 3 & 0x7FF;
                if (Raids1Util.getroom_type(template) == 12) {
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

    private void sendFlag(String msg) {
        JSONArray data = new JSONArray();
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", msg);
        data.put(jsonmsg);
        JSONObject send = new JSONObject();
        send.put("socketicealt", data);
        this.eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
		if(client.getVarbitValue(Varbits.IN_RAID) == 1){
			try {
				JSONObject payload = event.getPayload();
				if (payload.has("socketicecut")) {
					JSONObject data = payload.getJSONObject("socketicecut");
					String name = data.getString("player");
					if(!name.equals(this.client.getLocalPlayer().getName())){
						if(!playerNameList.contains(name)) {
							playerNameList.add(name);
							playerKindlingList.add(0);
						}
						int kindlingCount = data.getInt("kindling");
						teamTotalKindlingCut += kindlingCount;

						int index = playerNameList.indexOf(name);
						playerKindlingList.set(index, playerKindlingList.get(index) + kindlingCount);

						if (teamTotalKindlingCut >= teamKindlingNeeded && config.dumpMsg()) {
							if (!dumpKindling) {
								this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>DUMP YOUR KINDLING!", "");
							}
							dumpKindling = true;
						}
					}
				}else if (payload.has("socketicelight")) {
					JSONObject data = payload.getJSONObject("socketicelight");

					if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
						int kindlingCount = data.getInt("kindling");
						teamTotalKindlingLit += kindlingCount;

						if(config.dumpMsg() && teamTotalKindlingLit >= teamKindlingNeeded) {
							if (!allKindlingLit) {
								this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>KINDLING DUMPED.... Ice Demon starting!", "");
							}
							allKindlingLit = true;
						}
					}
				}else if(payload.has("socketicealt")){
					JSONArray data = payload.getJSONArray("socketicealt");
					JSONObject jsonmsg = data.getJSONObject(0);
					String msg = jsonmsg.getString("msg");
					this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, null);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }


    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        boolean tempInRaid = client.getVarbitValue(Varbits.IN_RAID) == 1;

        // if the player's raid state has changed
        if (!tempInRaid) {
            reset();
            this.overlayManager.remove(this.panelOverlay);
            this.overlayManager.remove(this.overlay);
        }else {
            if (this.client.getVarbitValue(5424) == 1){
                teamKindlingNeeded = 48;
            }else if (this.client.getVarbitValue(5424) >= 5){
				teamKindlingNeeded = (this.client.getVarbitValue(5424) - 1) * 18;
			}else {
                teamKindlingNeeded = 36 + (this.client.getVarbitValue(5424) * 12);
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if(this.client.getLocalPlayer() != null) {
            WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
            int x = wp.getX() - this.client.getBaseX();
            int y = wp.getY() - this.client.getBaseY();
            int type = Raids1Util.getroom_type(this.client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
            if (!config.display4Scav() && type != 12) {
                this.overlayManager.remove(this.panelOverlay);
                this.overlayManager.remove(this.overlay);
            } else if (config.display4Scav() && type == 2) {
                this.overlayManager.add(this.panelOverlay);
                this.overlayManager.add(this.overlay);
            }
        }
    }

	/*@Subscribe
	private void onClientTick(ClientTick event) {
		if (client.isMirrored() && !mirrorMode) {
			overlay.setLayer(OverlayLayer.AFTER_MIRROR);
			panelOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			mirrorMode = true;
		}
	}*/

}

