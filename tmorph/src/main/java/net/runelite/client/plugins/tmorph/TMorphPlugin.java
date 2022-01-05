package net.runelite.client.plugins.tmorph;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] Tmorph",
		description = "Change the visual of a worn item to another item<br>Format: id,id -comment (if wanted)",
		enabledByDefault = false
)
@Slf4j
public class TMorphPlugin extends Plugin {
	static final String TMORPH_GROUP = "BLTMorph";
	private static final ImmutableMap<KitType, HashMap<Integer, Integer>> tmorphPairs =
			ImmutableMap.<KitType, HashMap<Integer, Integer>>builder()
			.put(KitType.HEAD, new HashMap<>())
			.put(KitType.CAPE, new HashMap<>())
			.put(KitType.AMULET, new HashMap<>())
			.put(KitType.WEAPON, new HashMap<>())
			.put(KitType.TORSO,	new HashMap<>())
			.put(KitType.SHIELD, new HashMap<>())
			.put(KitType.LEGS, new HashMap<>())
			.put(KitType.HANDS,	new HashMap<>())
			.put(KitType.BOOTS,	new HashMap<>()).build();

	private static final HashMap<KitType, Integer> originalItemIds = new HashMap<>();
	private static final HashMap<Integer, Integer> animationReplaces = new HashMap<>();
	private static final HashMap<Integer, Integer> graphicReplaces = new HashMap<>();
	private static final HashMap<Integer, Integer> poseAnimationReplaces = new HashMap<>();
	private static final HashMap<Integer, Integer> soundEffectReplaces = new HashMap<>();
	//private static final HashMap<Integer, Integer> inventoryTmorphs = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private TMorphConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Provides
	TMorphConfig provideConfig(ConfigManager configManager){
		return configManager.getConfig(TMorphConfig.class);
	}

	@Override
	protected void startUp() throws Exception{
		tmorphPairs.get(KitType.HEAD).clear();
		tmorphPairs.get(KitType.CAPE).clear();
		tmorphPairs.get(KitType.AMULET).clear();
		tmorphPairs.get(KitType.WEAPON).clear();
		tmorphPairs.get(KitType.TORSO).clear();
		tmorphPairs.get(KitType.SHIELD).clear();
		tmorphPairs.get(KitType.LEGS).clear();
		tmorphPairs.get(KitType.HANDS).clear();
		tmorphPairs.get(KitType.BOOTS).clear();
		animationReplaces.clear();
		graphicReplaces.clear();
		poseAnimationReplaces.clear();
		soundEffectReplaces.clear();
		parse(tmorphPairs.get(KitType.HEAD), config.getHeadSlotTMorphs());
		parse(tmorphPairs.get(KitType.CAPE), config.getCapeSlotTMorphs());
		parse(tmorphPairs.get(KitType.AMULET), config.getAmmySlotTMorphs());
		parse(tmorphPairs.get(KitType.WEAPON), config.getWeaponSlotTMorphs());
		parse(tmorphPairs.get(KitType.TORSO), config.getTorsoSlotTMorphs());
		parse(tmorphPairs.get(KitType.SHIELD), config.getShieldSlotTMorphs());
		parse(tmorphPairs.get(KitType.LEGS), config.getLegsSlotTMorphs());
		parse(tmorphPairs.get(KitType.HANDS), config.getGloveSlotTMorphs());
		parse(tmorphPairs.get(KitType.BOOTS), config.getBootSlotTMorphs());
		parse(animationReplaces, config.animationTmorphs());
		parse(graphicReplaces, config.graphicTmorphs());
		parse(poseAnimationReplaces, config.poseAnimationTmorphs());
		parse(soundEffectReplaces, config.soundEffectTmorphs());
		/*inventoryTmorphs.clear();
		parse(inventoryTmorphs, config.inventoryTmorphs());*/

	}

	@Override
	protected void shutDown() throws Exception{
		tmorphPairs.forEach((k, v) ->{
			restore(v, k, false);
		});

		clientThread.invokeLater(() ->{
			if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null){
				client.getLocalPlayer().setPoseAnimation(-1);
				client.getLocalPlayer().setAnimation(-1);
				client.getLocalPlayer().setAnimationFrame(0);
			}
		});
	}

	private void restore(final HashMap<Integer, Integer> map, final KitType finalType, boolean update){
		clientThread.invokeLater(() ->{
			if (client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null){
				var composition = client.getLocalPlayer().getPlayerComposition();
				if (originalItemIds.containsKey(finalType)){
					int replace;
					int current = composition.getEquipmentId(finalType);
					if (current >= 0){
						if (update && map.containsKey(originalItemIds.get(finalType) - 512) && map.get(originalItemIds.get(finalType) - 512) >= 0){
							replace = map.get(originalItemIds.get(finalType) - 512) + 512;
						}else {
							replace = originalItemIds.get(finalType);
						}
						composition.getEquipmentIds()[finalType.getIndex()] = replace;
						composition.setHash();
					}
				}
			}
		});
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged event){
		if (event.getGroup().equals(TMORPH_GROUP)){
			KitType type = null;
			switch (event.getKey()){
				case "headSlotTMorphs":
					type = KitType.HEAD;
					break;
				case "capeSlotTMorphs":
					type = KitType.CAPE;
					break;
				case "ammySlotTMorphs":
					type = KitType.AMULET;
					break;
				case "torsoSlotTMorphs":
					type = KitType.TORSO;
					break;
				case "legsSlotTMorphs":
					type = KitType.LEGS;
					break;
				case "gloveSlotTMorphs":
					type = KitType.HANDS;
					break;
				case "bootSlotTMorphs":
					type = KitType.BOOTS;
					break;
				case "weaponSlotTMorphs":
					type = KitType.WEAPON;
					break;
				case "shieldSlotTMorphs":
					type = KitType.SHIELD;
					break;
				case "animationTmorphs":
					animationReplaces.clear();
					parse(animationReplaces, event.getNewValue());
					return;
				case "graphicTmorphs":
					graphicReplaces.clear();
					parse(graphicReplaces, event.getNewValue());
					return;
				case "poseAnimationTmorphs":
					poseAnimationReplaces.clear();
					parse(poseAnimationReplaces, event.getNewValue());
					return;
				case "soundEffectTmorphs":
					soundEffectReplaces.clear();
					parse(soundEffectReplaces, event.getNewValue());
					return;
				/*case "inventoryTmorphs":
					ChatMessageBuilder message = (new ChatMessageBuilder()).append(Color.MAGENTA, "Re-log to load the inventory models!");
					chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.ITEM_EXAMINE).runeLiteFormattedMessage(message.build()).build());
					inventoryTmorphs.clear();
					parse(inventoryTmorphs, event.getNewValue());
					return;*/
			}

			if (type != null){
				var map = tmorphPairs.get(type);
				map.clear();
				parse(map, event.getNewValue());
				restore(map, type, true);
			}
		}
	}

	private static void parse(HashMap<Integer, Integer> map, String text){
		if (text != null){
			String[] lines = text.split("\\R");
			for (String line : lines){
				String[] values = line.split(",");
				if (values.length == 2 && !values[0].trim().equals("") && !values[1].trim().equals("")){
					try{
						int left = Integer.parseInt(values[0].trim());
						int right;
						if(values[1].contains("-")){
							if(!values[1].startsWith("-1")) {
								right = Integer.parseInt(values[1].substring(0, values[1].indexOf("-")).trim());
							}else {
								right = -1;
							}
						}else{
							right = Integer.parseInt(values[1].trim());
						}
						map.put(left, right);
					}catch (NumberFormatException ex){
						log.warn("Config entry could not be parsed, entry: {}", line);
					}
				}
			}
		}
	}

	@Subscribe
	protected void onGameStateChanged(GameStateChanged event){
		if (event.getGameState() == GameState.LOGGED_IN){
			setTmorphs();
		}
	}

	@Subscribe
	protected void onAnimationChanged(AnimationChanged event){
		if (event.getActor() == client.getLocalPlayer()){
			var animation = event.getActor().getAnimation();
			if (animationReplaces.containsKey(animation)){
				event.getActor().setAnimation(animationReplaces.get(animation));
				event.getActor().setAnimationFrame(0);
			}
		}
	}

	@Subscribe
    public void onGraphicChanged(GraphicChanged event) {
		if(event.getActor() != null && event.getActor() instanceof Player){
			var graphic = event.getActor().getGraphic();
			if (graphicReplaces.containsKey(graphic)){
				event.getActor().setGraphic(graphicReplaces.get(graphic));
			}
		}
    }

	@Subscribe
	protected void onSoundEffectPlayed(SoundEffectPlayed event){
		if (soundEffectReplaces.containsKey(event.getSoundId())){
			var soundEffect = event.getSoundId();
			if (soundEffect > 0){
				event.consume();
				clientThread.invokeLater(() -> client.playSoundEffect(soundEffectReplaces.get(soundEffect)));
			}
		}
	}

	/*@Subscribe
	protected void onGameTick(GameTick gameTick){
		setTmorphs();
	}*/

	@Subscribe
	protected void onClientTick(ClientTick event){
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null) {
			return;
		}

		setTmorphs();

		if (!config.shouldReplacePoseAnimationTmorphs()) {
			return;
		}

		var player = client.getLocalPlayer();
		var animation = player.getPoseAnimation();

		if (poseAnimationReplaces.containsKey(animation)) {
			player.setPoseAnimation(poseAnimationReplaces.get(animation));
		}

	}

	private void setTmorphs(){
		if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null) {
			var composition = client.getLocalPlayer().getPlayerComposition();
			var equipment = composition.getEquipmentIds();
			boolean changed = false;
			for (KitType key : tmorphPairs.keySet()) {
				var map = tmorphPairs.get(key);
				int current = composition.getEquipmentId(key);
				if (map.containsKey(current) && map.get(current) > 0) {
					originalItemIds.put(key, equipment[key.getIndex()]);
					equipment[key.getIndex()] = map.get(current) + 512;
					changed = true;
				}
			}

			if (changed) {
				client.getLocalPlayer().getPlayerComposition().setHash();
			}
		}

		/*for (Integer tmorph : inventoryTmorphs.keySet()) {
			ItemComposition itemComposition = client.getItemComposition(tmorph);
			itemComposition.setModelOverride(inventoryTmorphs.get(tmorph));
		}*/
	}
}
