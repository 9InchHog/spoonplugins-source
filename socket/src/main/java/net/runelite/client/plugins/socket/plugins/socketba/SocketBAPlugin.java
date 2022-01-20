package net.runelite.client.plugins.socket.plugins.socketba;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
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
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@PluginDescriptor(
        name = "Socket - Barbarian Assault",
        description = "Socket BA",
        tags = {"ba", "barb assault", "spoon", "spoonlite"},
		enabledByDefault = false
)
public class SocketBAPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SocketBAOverlay overlay;

    @Inject
    private SocketBAPanelOverlay panelOverlay;

	@Inject
	private SocketBAItemOverlay itemOverlay;

    @Inject
    private SocketBAConfig config;

    @Inject
    private EventBus eventBus;

	private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

	public NPC queen = null;
	public String role = "";
	public String otherRole = "";
	public String attCall = "";
	public String defCall = "";
	public String colCall = "";
	public String healCall = "";
	public boolean roleDone = false;
	public boolean fightersDead = false;
	public boolean rangersDead = false;
	public ArrayList<String> hornList = new ArrayList<String>(Arrays.asList("attacker horn", "healer horn", "defender horn", "collector horn"));
	public int arrowEquiped = 0;
	private int attackStyleVarbit = -1;
	public int equippedWeaponTypeVarbit = -1;
	public AttackStyle attackStyle;
	public String roleWidgetText = "";
	public Map<WorldPoint, Integer> eggMap = new HashMap<>();
	public ArrayList<GameObject> vendingMachines = new ArrayList<>();
	public Map<GameObject, Color> eggHoppers = new HashMap<>();
	public Map<NPC, Color> cannons = new HashMap<>();
	public double cannonWidth = 1;
	public boolean cannonWidthUp = true;
	public Map<GroundObject, Color> discoTiles = new HashMap<>();

    public SocketBAPlugin() {
       
    }

    @Provides
    SocketBAConfig getConfig(ConfigManager configManager) {
        return (SocketBAConfig) configManager.getConfig(SocketBAConfig.class);
    }

    protected void startUp() throws Exception {
		reset();
		attCall = "";
		colCall = "";
		healCall = "";
		defCall = "";
		roleWidgetText = "";
		this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.panelOverlay);
		this.overlayManager.add(this.itemOverlay);
		vendingMachines.clear();
		cannons.clear();
		eggHoppers.clear();
		discoTiles.clear();
    }

    protected void shutDown() throws Exception {
        reset();
		attCall = "";
		colCall = "";
		healCall = "";
		defCall = "";
		roleWidgetText = "";
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.panelOverlay);
		this.overlayManager.remove(this.itemOverlay);
		attackStyleVarbit = -1;
		equippedWeaponTypeVarbit = -1;
		vendingMachines.clear();
		cannons.clear();
		eggHoppers.clear();
    }

    protected void reset() {
		role = "";
		otherRole = "";
		roleDone = false;
		fightersDead = false;
		rangersDead = false;
		arrowEquiped = 0;
		roleWidgetText = "";
		eggMap.clear();
    }

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		Widget hpWidget = client.getWidget(WidgetInfo.BA_HEAL_TEAMMATES.getGroupId(), WidgetInfo.BA_HEAL_TEAMMATES.getChildId());
		if(hpWidget != null) {
			hpWidget.setHidden(config.hideHpOverlay());
		}
	}

	@Subscribe
	private void onItemSpawned(ItemSpawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			int id = event.getItem().getId();
			if(id == 10534 || id == 10531 || id == 10532 || id == 10533) {
				for (int i=0; i<event.getItem().getQuantity(); i++) {
					eggMap.put(event.getTile().getWorldLocation(), id);
				}
			}
		}
	}

	@Subscribe
	private void onItemDespawned(ItemDespawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			eggMap.remove(event.getTile().getWorldLocation(), event.getItem().getId());
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			int id = event.getGameObject().getId();
			if(id == 20241 || id == 20242 || id == 20243) {
				vendingMachines.removeIf(obj -> obj.getId() == id);
				vendingMachines.add(event.getGameObject());
			} else if(id == 20267) {
				eggHoppers.put(event.getGameObject(), Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			int id = event.getGameObject().getId();
			if(id == 20241 || id == 20242 || id == 20243) {
				vendingMachines.remove(event.getGameObject());
			} else if(id == 20267) {
				eggHoppers.remove(event.getGameObject());
			}
		}
	}

	@Subscribe
	private void onGroundObjectSpawned(GroundObjectSpawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			int id = event.getGroundObject().getId();
			if(id >= 20136  && id <= 20147) {
				discoTiles.put(event.getGroundObject(), Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
		}
	}

	@Subscribe
	private void onGroundObjectDespawned(GroundObjectDespawned event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			int id = event.getGroundObject().getId();
			if(id >= 20136  && id <= 20147) {
				discoTiles.remove(event.getGroundObject());
			}
		}
	}

	@Subscribe
    private void onGameTick(GameTick event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			for (Map.Entry<GameObject, Color> entry : eggHoppers.entrySet()) {
				entry.setValue(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
			for (Map.Entry<NPC, Color> entry : cannons.entrySet()) {
				entry.setValue(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
			for (Map.Entry<GroundObject, Color> entry : discoTiles.entrySet()) {
				entry.setValue(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}

			if(!role.equals("") && !otherRole.equals("") && this.client.getLocalPlayer() != null) {
				Widget otherWidget = null;
				Widget roleWidget = null;
				if(role.equals("Attacker")){
					otherWidget = client.getWidget(31784970);
					roleWidget = client.getWidget(31784967);
				}else if(role.equals("Defender")){
					otherWidget = client.getWidget(31916041);
					roleWidget = client.getWidget(31916039);
				}else if(role.equals("Healer")){
					otherWidget = client.getWidget(31981577);
					roleWidget = client.getWidget(31981575);
				}else if(role.equals("Collector")){
					otherWidget = client.getWidget(31850505);
					roleWidget = client.getWidget(31850503);
				}

				if(roleWidget != null) {
					if(!roleWidget.getText().equals(roleWidgetText) && !roleWidget.getText().equals("- - -")) {
						roleWidgetText = roleWidget.getText();
						if(role.equals("Attacker")){
							if (roleWidgetText.contains("Aggressive")) {
								attCall = "Aggressive/Blunt/Earth";
							} else if (roleWidgetText.contains("Accurate")) {
								attCall = "Accurate/Field/Water";
							} else if (roleWidgetText.contains("Controlled")) {
								attCall = "Controlled/Bullet/Wind";
							} else if (roleWidgetText.contains("Defensive")) {
								attCall = "Defensive/Barbed/Fire";
							}
						}else if(role.equals("Defender")){
							defCall = roleWidgetText;
						}else if(role.equals("Healer")){
							healCall = roleWidgetText;
						}else if(role.equals("Collector")){
							colCall = roleWidgetText;
						}
					}
				}

				if(otherWidget != null){
					String otherCall = "";
					if(role.equals("Attacker") && !colCall.equalsIgnoreCase(otherWidget.getText())){
						colCall = otherWidget.getText();
						otherCall = colCall;
					}else if(role.equals("Defender") && !healCall.equalsIgnoreCase(otherWidget.getText())){
						healCall = otherWidget.getText();
						otherCall = healCall;
					}else if(role.equals("Healer") && !defCall.equalsIgnoreCase(otherWidget.getText())){
						defCall = otherWidget.getText();
						otherCall = defCall;
					}else if(role.equals("Collector") && !attCall.equalsIgnoreCase(otherWidget.getText())){
						attCall = otherWidget.getText();
						otherCall = attCall;
					}

					if(!otherCall.equals("")) {
						JSONObject data = new JSONObject();
						data.put("player", this.client.getLocalPlayer().getName());
						data.put("role", otherRole);
						data.put("call", otherCall);
						JSONObject payload = new JSONObject();
						payload.put("socketbarole", data);
						this.eventBus.post(new SocketBroadcastPacket(payload));
					}
				}
			}
		}
    }

	@Subscribe
    private void onNpcSpawned(NpcSpawned event) {
    	if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			if (event.getNpc().getId() == 5775) {
				queen = event.getNpc();
			} else if (event.getNpc().getId() == 1655) {
				cannons.put(event.getNpc(), Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
		}
    }

	@Subscribe
    private void onNpcDespawned(NpcDespawned event) {
    	if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			if (event.getNpc().getId() == 5775) {
				queen = null;
			} else if (event.getNpc().getId() == 1655) {
				cannons.remove(event.getNpc());
			}
		}
    }

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1 && event.getMenuOption().equalsIgnoreCase("wield")
				&& (event.getId() == 22227 || event.getId() == 22228 || event.getId() == 22229 || event.getId() == 22230)){
			arrowEquiped = event.getId();
		}
	}

    private void sendFlag(String msg) {
        JSONArray data = new JSONArray();
        JSONObject jsonmsg = new JSONObject();
        jsonmsg.put("msg", msg);
        data.put(jsonmsg);
        JSONObject send = new JSONObject();
        send.put("socketbaalt", data);
        this.eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
		if(this.client.getLocalPlayer() != null){
			try {
				JSONObject payload = event.getPayload();
				if (payload.has("socketbarole")) {
					JSONObject data = payload.getJSONObject("socketbarole");

					if(!data.getString("player").equals(this.client.getLocalPlayer().getName())){
						if(data.getString("role").equals("Attacker")){
							attCall = data.getString("call");
							if(attCall.contains("Defensive")) {
								Objects.requireNonNull(client.getWidget(31784967)).setText("Defensive");
							}else if(attCall.contains("Aggressive")) {
								Objects.requireNonNull(client.getWidget(31784967)).setText("Aggressive");
							}else if(attCall.contains("Accurate")) {
								Objects.requireNonNull(client.getWidget(31784967)).setText("Accurate");
							}else {
								Objects.requireNonNull(client.getWidget(31784967)).setText("Controlled");
							}
						}else if(data.getString("role").equals("Defender")){
							defCall = data.getString("call");
							Objects.requireNonNull(client.getWidget(31916039)).setText(defCall);
						}else if(data.getString("role").equals("Healer")){
							healCall = data.getString("call");
							Objects.requireNonNull(client.getWidget(31981575)).setText(healCall);
						}else if(data.getString("role").equals("Collector")){
							colCall = data.getString("call");
							Objects.requireNonNull(client.getWidget(31850503)).setText(colCall);
						}
					}
				}else if(payload.has("socketbaalt") && config.bmMessages()){
					JSONArray data = payload.getJSONArray("socketbaalt");
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
    public void onWidgetLoaded(WidgetLoaded event) {
		if(event.getGroupId() == WidgetID.BA_ATTACKER_GROUP_ID){
			reset();
			role = "Attacker";
			otherRole = "Collector";
			colCall = "";
		}else if(event.getGroupId() == WidgetID.BA_DEFENDER_GROUP_ID){
			reset();
			role = "Defender";
			otherRole = "Healer";
			healCall = "";
		}else if(event.getGroupId() == WidgetID.BA_HEALER_GROUP_ID){
			reset();
			role = "Healer";
			otherRole = "Defender";
			defCall = "";
		}else if(event.getGroupId() == WidgetID.BA_COLLECTOR_GROUP_ID){
			reset();
			role = "Collector";
			otherRole = "Attacker";
			attCall = "";
		}

		Widget hpWidget = client.getWidget(WidgetInfo.BA_HEAL_TEAMMATES.getGroupId(), WidgetInfo.BA_HEAL_TEAMMATES.getChildId());
		if(hpWidget != null) {
			hpWidget.setHidden(config.hideHpOverlay());
		}
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
		if(event.getType() == ChatMessageType.GAMEMESSAGE && this.client.getVar(Varbits.IN_GAME_BA) == 1 && this.client.getLocalPlayer() != null){
			String msg = Text.removeTags(event.getMessage());
			if((msg.toLowerCase().contains("all of the penance runners have been killed!") && role.equals("Defender"))
					|| (msg.toLowerCase().contains("all of the penance healers have been killed!") && role.equals("Healer"))){
				roleDone = true;
			}else if(msg.toLowerCase().contains("all of the penance fighters have been killed!") && role.equals("Attack")){
				fightersDead = true;
				if(rangersDead){
					roleDone = true;
				}
			}else if(msg.toLowerCase().contains("all of the penance rangers have been killed!") && role.equals("Attack")){
				rangersDead = true;
				if(fightersDead){
					roleDone = true;
				}
			}else if(msg.equalsIgnoreCase("the egg exploded.")){
				int rng = new Random().nextInt(3);
				if(rng == 0) {
					sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " picked up the wrong egg.");
				}else if(rng == 1) {
					sendFlag("<col=ff0000>... Really? Just click the right egg. " + this.client.getLocalPlayer().getName() + " has got no hands");
				}else {
					sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " is colorblind");
				}
			}else if(msg.equalsIgnoreCase("that's the wrong type of poisoned food to use! penalty!")){
				int rng = new Random().nextInt(3);
				if(rng == 0) {
					sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " used the wrong poisoned food.");
				}else if(rng == 1) {
					sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " has room temp IQ");
				}else {
					sendFlag("<col=ff0000>Either they are greedy with the ticks.... or " + this.client.getLocalPlayer().getName() + " can't tell whats" + healCall.replace("Pois. ", ""));
				}
			}
		}
    }

	@Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1 && this.client.getLocalPlayer() != null){
			if (role.equals("Attacker") && event.getHitsplat().getAmount() == 0 && event.getActor() instanceof NPC && event.getHitsplat().isMine() && event.getActor().getName() != null
					&& event.getActor().getName().contains("Penance ") && this.client.getLocalPlayer().getAnimation() != 7511) {
				int rng = new Random().nextInt(3);
				if(rng == 0) {
					sendFlag("<col=ff0000>" + this.client.getLocalPlayer().getName() + " is being a brainlet");
				}else if(rng == 1) {
					sendFlag("<col=ff0000>Hehe point go brrrrrrrrrrrrrrrrrrr  -" + this.client.getLocalPlayer().getName());
				}else {
					sendFlag("<col=ff0000>Just kick him now. " + this.client.getLocalPlayer().getName() + " is bing chillin");
				}
			}
		}
    }

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {
		int currentAttackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
		int currentEquippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);

		if (attackStyleVarbit != currentAttackStyleVarbit || equippedWeaponTypeVarbit != currentEquippedWeaponTypeVarbit) {
			attackStyleVarbit = currentAttackStyleVarbit;
			equippedWeaponTypeVarbit = currentEquippedWeaponTypeVarbit;
			updateAttackStyle(equippedWeaponTypeVarbit, attackStyleVarbit);
		}
	}

	private void updateAttackStyle(int equippedWeaponType, int attackStyleIndex) {
		AttackStyle[] attackStyles = WeaponType.getWeaponType(equippedWeaponType).getAttackStyles();
		if (attackStyleIndex < attackStyles.length) {
			attackStyle = attackStyles[attackStyleIndex];
			if (attackStyle == null) {
				attackStyle = AttackStyle.OTHER;
			}
		}
	}

	@Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (this.client.getGameState() != GameState.LOGGED_IN || this.client.isMenuOpen())
            return;

		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			if(cannonWidthUp) {
				cannonWidth += .02;
				if(cannonWidth >= 20) {
					cannonWidthUp = false;
				}
			} else {
				cannonWidth -= .02;
				if(cannonWidth <= 1) {
					cannonWidthUp = true;
				}
			}

			MenuEntry[] menuEntries = this.client.getMenuEntries();
			int idx = 0;
			this.optionIndexes.clear();
			for (MenuEntry entry : menuEntries) {
				String option = Text.removeTags(entry.getOption()).toLowerCase();
				this.optionIndexes.put(option, idx++);
			}
			idx = 0;
			for (MenuEntry entry : menuEntries)
				swapMenuEntry(idx++, entry);

			client.setMenuEntries(updateMenuEntries(client.getMenuEntries()));
		}
    }
	
	private void swapMenuEntry(int index, MenuEntry menuEntry) {
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

		if (this.config.leftClickHorn() && option.contains("tell-") && hornList.contains(target)) {
			String newSwap = "";
			if (role.equalsIgnoreCase("attacker")) {
				newSwap = "tell-" + colCall.substring(0, colCall.indexOf(" egg")).toLowerCase();
			} else if (role.equalsIgnoreCase("healer")) {
				newSwap = "tell-" + defCall.toLowerCase();
			} else if (role.equalsIgnoreCase("defender")) {
				newSwap = "tell-" + healCall.replace("Pois. ", "").toLowerCase();
			} else if (role.equalsIgnoreCase("collector")) {
				newSwap = "tell-" + attCall.substring(0, attCall.indexOf("/")).toLowerCase();
			}
			swap(newSwap, option, target, index, false);
		}
	}

    private void swap(String optionA, String optionB, String target, int index, boolean strict) {
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int thisIndex = findIndex(menuEntries, index, optionB, target, strict);
        int optionIdx = findIndex(menuEntries, thisIndex, optionA, target, strict);
        if (thisIndex >= 0 && optionIdx >= 0)
            swap(this.optionIndexes, menuEntries, optionIdx, thisIndex);
    }

    private int findIndex(MenuEntry[] entries, int limit, String option, String target, boolean strict) {
        if (strict) {
            List<Integer> indexes = this.optionIndexes.get(option);
            for (int i = indexes.size() - 1; i >= 0; i--) {
                int idx = indexes.get(i);
                MenuEntry entry = entries[idx];
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (idx <= limit && entryTarget.equals(target))
                    return idx;
            }
        } else {
            for (int i = limit; i >= 0; i--) {
                MenuEntry entry = entries[i];
                String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
                String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
                    return i;
            }
        }
        return -1;
    }

    private void swap(ArrayListMultimap<String, Integer> optionIndexes, MenuEntry[] entries, int index1, int index2) {
        MenuEntry entry = entries[index1];
        entries[index1] = entries[index2];
        entries[index2] = entry;
        this.client.setMenuEntries(entries);
        optionIndexes.clear();
        int idx = 0;
        for (MenuEntry menuEntry : entries) {
            String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
            optionIndexes.put(option, idx++);
        }
    }

	private MenuEntry[] updateMenuEntries(MenuEntry[] menuEntries) {
		return Arrays.stream(menuEntries)
				.filter(filterMenuEntries).sorted((o1, o2) -> 0)
				.toArray(MenuEntry[]::new);
	}

	private final Predicate<MenuEntry> filterMenuEntries = entry -> {
		int id = entry.getIdentifier();
		String option = Text.standardize(entry.getOption(), true).toLowerCase();
		String target = Text.standardize(entry.getTarget(), true).toLowerCase();
		int type = entry.getType().getId();

		if (this.config.leftClickEggs() && type >= 18 && type <= 22 && (id == 10531 || id == 10532 || id == 10533 || id == 10534)) {
			if(!role.equals("Collector")
					|| ((colCall.equals("Green egg") && (id == 10532 || id == 10533))
					|| (colCall.equals("Red egg") && (id == 10531 || id == 10533))
					|| (colCall.equals("Blue egg") && (id == 10531 || id == 10532)))){
				return false;
			}
		}

		if(config.hideAttack() && (target.contains("penance fighter") || target.contains("penance ranger"))){
			if(option.contains("attack")) {
				WeaponType wepType =  WeaponType.getWeaponType(equippedWeaponTypeVarbit);
				System.out.println("Arrow: " + arrowEquiped);
				System.out.println("wepType: " + wepType);
				System.out.println("attCall: " + attCall);
				System.out.println("Style: " + attackStyle.getName());
				if (!role.equals("Attacker")){
					System.out.println("Hide Not attack\n");
					return false;
				} else if(wepType == WeaponType.TYPE_3 || wepType == WeaponType.TYPE_5 || wepType == WeaponType.TYPE_7 || wepType == WeaponType.TYPE_19) {
					if((arrowEquiped == 22227 && !attCall.contains("Controlled"))
							|| (arrowEquiped == 22228 && !attCall.contains("Accurate"))
							|| (arrowEquiped == 22229 && !attCall.contains("Aggressive"))
							|| (arrowEquiped == 22230 && !attCall.contains("Defensive"))) {
						System.out.println("Hide Wrong arrow\n");
						return false;
					}
				} else if (!attCall.contains(attackStyle.getName())) {
					System.out.println("Hide wrong melee\n");
					return false;
				}
			}else if(option.contains("cast ") && target.contains(" -> ")){
				if (!role.equals("Attacker") || (option.contains(" wind ") && !attCall.contains("Controlled")) || (option.contains(" water ") && !attCall.contains("Accurate"))
						|| (option.contains(" earth ") && !attCall.contains("Aggressive")) || (option.contains(" fire ") && !attCall.contains("Defensive"))) {
					return false;
				}
			}
		}

		if(config.hideAttack() && option.contains("attack") && target.contains("penance queen")){
			return false;
		}

		if(config.removeUseFood() && option.contains("use") && (target.contains("poisoned ") && (target.contains(" meat ->") || target.contains(" tofu ->") || target.contains(" worms ->")))
				&& !target.contains("penance healer") && role.equals("Healer")) {
			return false;
		}

		if(config.highlightVendingMachine() && (option.contains("stock-up") || option.contains("take-") || option.contains("convert"))
				&& (target.contains(" item machine") || target.contains("collector converter"))) {
			if((role.equals("Attacker") && !target.contains("attacker item machine")) || (role.equals("Defender") && !target.contains("defender item machine"))
					|| (role.equals("Healer") && !target.contains("healer item machine")) || (!role.equals("Collector") && target.contains("collector converter"))) {
				return false;
			}
		}
		return true;
	};
}

