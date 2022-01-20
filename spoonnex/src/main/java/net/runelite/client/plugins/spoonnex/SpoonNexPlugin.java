package net.runelite.client.plugins.spoonnex;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.util.*;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] Nex",
	description = "Nihilism intensifies",
	tags = {"Nex", "gwd", "Spoon", "Torva", "Happy birthday rusher", "Ancient"},
	enabledByDefault = false
)
@Slf4j
@Singleton
public class SpoonNexPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private SpoonNexConfig config;

	@Inject
	private SpoonNexOverlay overlay;

	@Inject
	private SpoonNexPanelOverlay panelOverlay;

	@Inject
	private SpoonNexPhasePanel phasePanel;

	@Inject
	private SpoonNexPrayerBox prayerOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
    private InfoBoxManager infoBoxManager;

	@Inject
    private ItemManager itemManager;

	private static final int[] nexRegions = { 11345, 11601, 11857 };
	public ArrayList<Integer> nexIds = new ArrayList<Integer> (Arrays.asList(11278, 11279, 11280, 11281, 11282));

	public Nex nex = null;
	public NPC activeMage = null;
	public ArrayList<GameObject> gameObjects = new ArrayList<>();
	public Map<String, Integer> covidList = new HashMap<>();
	public boolean sacrificeTarget = false;
	private static Clip nexAudio;
	public int startTick = -1;
	public int p1Tick = -1;
	public int p1Boss = -1;
	public int p2Tick = -1;
	public int p2Boss = -1;
	public int p3Tick = -1;
	public int p3Boss = -1;
	public int p4Tick = -1;
	public int p4Boss = -1;
	public int p5Tick = -1;
	public int timerTicksLeft = 0;
	private SpoonNexTimerBox timerBox;

	public ArrayList<Color> raveObjects = new ArrayList<>();
	public ArrayList<Color> forWhyColors = new ArrayList<>();
	private static Clip clip;
	public int ratJamFrame = 1;
	public int ratJamTicks = 0;
	public Point ratJamPoint = null;
	public ArrayList<Color> raveRunway = new ArrayList<Color>();

	@Provides
	SpoonNexConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SpoonNexConfig.class);
	}

	@Override
	protected void startUp() {
		reset();
		overlayManager.add(overlay);
		overlayManager.add(panelOverlay);
		overlayManager.add(prayerOverlay);
		overlayManager.add(phasePanel);
	}

	@Override
	protected void shutDown() {
		reset();
		overlayManager.remove(overlay);
		overlayManager.remove(panelOverlay);
		overlayManager.remove(prayerOverlay);
		overlayManager.remove(phasePanel);
		infoBoxManager.removeInfoBox(timerBox);
	}

	private void reset() {
		nex = null;
		activeMage = null;
		raveObjects.clear();
		forWhyColors.clear();
		gameObjects.clear();
		client.clearHintArrow();
		covidList.clear();
		sacrificeTarget = false;
		raveRunway.clear();
		if(timerTicksLeft == 0)
			infoBoxManager.removeInfoBox(timerBox);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("SpoonNex")) {
			if(event.getKey().equals("noEscapeVolume")) {
				if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (this.config.audioVolume() / 2 - 45));
                    }
                }
			} else if(event.getKey().equals("killTimer")) {
				if (config.killTimer() == SpoonNexConfig.KillTimerMode.INFOBOX && nex != null && startTick > -1) {
                    BufferedImage image = itemManager.getImage(26348);
					timerBox = new SpoonNexTimerBox(image, config, this, client);
					infoBoxManager.addInfoBox(timerBox);
                } else {
					infoBoxManager.removeInfoBox(timerBox);
				}
			}
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event) {
		if(nexIds.contains(event.getNpc().getId())) {
			nex = new Nex(event.getNpc());
			timerTicksLeft = 0;
		}
	}

	@Subscribe
	private void onNpcChanged(NpcChanged event) {
		if(nex != null && nex.npc != null && event.getOld() != null && nex.npc.getId() == event.getOld().getId()) {
			nex.npc = event.getNpc();
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (nex != null && nex.npc != null && nex.npc.getName() != null && event.getNpc().getName() != null && nex.npc.getName().equals(event.getNpc().getName())) {
			reset();
		}
	}

	@Subscribe
	private void onActorDeath(ActorDeath event) {
		if (activeMage != null && event.getActor() instanceof NPC && activeMage == event.getActor()) {
			activeMage = null;
			client.clearHintArrow();
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() instanceof NPC && event.getActor().getName() != null && event.getActor().getName().equals("Nex")) {
			 if (event.getActor().getAnimation() == 9189 || event.getActor().getAnimation() == 9188 || event.getActor().getAnimation() == 9180) {
				nex.attacksTilSpecial--;
			}
		}
	}

	@Subscribe
	private void onOverheadTextChanged(OverheadTextChanged event) {
		if (event.getActor() instanceof Player && event.getActor().getOverheadText().equals("*Cough*") && nex != null){
			if (config.olmPTSD()) {
				event.getActor().setOverheadText(new Random().nextInt(2) == 0 ? "Burn with me!" : "I will burn with you!");
			}
			covidList.remove(event.getActor().getName());
			covidList.put(event.getActor().getName(), 5);
		} else if (event.getActor().getName() != null && event.getActor().getName().equals("Nex") && event.getOverheadText().contains("Taste my wrath!")
				&& client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null && client.getLocalPlayer().getName().equals("Null God")) {
			event.getActor().setOverheadText("Allahuakbar! *Click*");
		} else if (config.olmPTSD() && event.getActor().getName() != null && event.getActor().getName().equals("Nex") && event.getOverheadText().contains("Let the virus flow through you!")) {
			event.getActor().setOverheadText("Let the burn flow through you!");
		}
	}

	@Subscribe
	private void onGameTick(GameTick tick) {
		if (nex != null) {
			if (nex.specialTicksLeft > 0) {
				nex.specialTicksLeft--;
				if (nex.specialTicksLeft == 0) {
					nex.currentSpecial = "";
					sacrificeTarget = false;
				}
			}

			if (nex.invulnerableTicks > 0)
				nex.invulnerableTicks--;

			raveObjects.clear();
			for(GameObject obj : gameObjects){
				raveObjects.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}

			forWhyColors.clear();
			for(int i=0; i<7; i++){
				forWhyColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}

			if (ratJamTicks > 0) {
				ratJamTicks--;
			} else {
				if (new Random().nextInt(300) == 0) {
					ratJamTicks = 2;
					ratJamPoint = new Point(new Random().nextInt(client.getCanvasWidth()), new Random().nextInt(client.getCanvasHeight()));
				}
			}

			for (String name : covidList.keySet()) {
				int ticks = covidList.get(name) - 1;
				covidList.replace(name, covidList.get(name), ticks);
			}
			covidList.entrySet().removeIf(entry -> entry.getValue() == 0);

			raveRunway.clear();
			for(int i=0; i<30; i++){
				raveRunway.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
			}
		}

		if(timerTicksLeft > 0) {
			timerTicksLeft--;
			if(timerTicksLeft == 0) {
				startTick = -1;
				p1Tick = -1;
				p1Boss = -1;
				p2Tick = -1;
				p2Boss = -1;
				p3Tick = -1;
				p3Boss = -1;
				p4Tick = -1;
				p4Boss = -1;
				p5Tick = -1;
				infoBoxManager.removeInfoBox(timerBox);
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event) {
		if (this.client.getGameState() == GameState.LOGGED_IN){
			ratJamFrame++;
			if (ratJamFrame >= 35) {
				ratJamFrame = 1;
			}
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == 42944 || event.getGameObject().getId() == 42942) {
			gameObjects.add(event.getGameObject());
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event) {
		if (event.getGameObject().getId() == 42944 || event.getGameObject().getId() == 42942) {
			gameObjects.remove(event.getGameObject());
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event) {
		String text = event.getMessage();
		String playAudio = "";

		if (text.contains("Nex: <col=9090ff>") || text.contains("Nex: <col=0000ff>")) {
			if (text.contains("AT LAST!")) {
				playAudio = "atLast.wav";
			} else if ((text.contains("Fumus") || text.contains("Umbra") || text.contains("Cruor") || text.contains("Glacies")) && text.contains(", don't fail me!")) {
				for(NPC npc : this.client.getNpcs()) {
					if ((npc.getId() == 11283 && text.contains("Fumus, don't fail me!")) || (npc.getId() == 11284 && text.contains("Umbra, don't fail me!"))
							|| (npc.getId() == 11285 && text.contains("Cruor, don't fail me!")) || (npc.getId() == 11286 && text.contains("Glacies, don't fail me!"))) {
						client.setHintArrow(npc);
						activeMage = npc;
						break;
					}
				}

				if (text.contains("Fumus")) {
					playAudio = "fumusDontFail.wav";
					p1Boss = client.getTickCount();
					if(config.phaseChatMessages() && config.showMinionSplit()) {
						String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P1" : "Smoke Phase";
						this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Boss Complete! Duration: <col=ff0000>" + ticksToTime(p1Boss - startTick) + "</col>", null);
					}
				} else if (text.contains("Umbra")) {
					playAudio = "umbraDontFail.wav";
					p2Boss = client.getTickCount();
					if(config.phaseChatMessages() && config.showMinionSplit()) {
						String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P2" : "Shadow Phase";
						this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Boss Complete! Duration: <col=ff0000>" + ticksToTime(p2Boss - p1Tick) + "</col>", null);
					}
				} else if (text.contains("Cruor")) {
					playAudio = "cruorDontFail.wav";
					p3Boss = client.getTickCount();
					if(config.phaseChatMessages() && config.showMinionSplit()) {
						String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P3" : "Blood Phase";
						this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Boss Complete! Duration: <col=ff0000>" + ticksToTime(p3Boss - p2Tick) + "</col>", null);
					}
				} else if (text.contains("Glacies")) {
					playAudio = "glaciesDontFail.wav";
					p4Boss = client.getTickCount();
					if(config.phaseChatMessages() && config.showMinionSplit()) {
						String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P4" : "Ice Phase";
						this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Boss Complete! Duration: <col=ff0000>" + ticksToTime(p4Boss - p3Tick) + "</col>", null);
					}
				}
			} else if(text.contains("Fumus!")){
				playAudio = "fumus.wav";
			} else if(text.contains("Umbra!")){
				playAudio = "umbra.wav";
			} else if(text.contains("Cruor!")){
				playAudio = "cruor.wav";
			} else if(text.contains("Glacies!")){
				playAudio = "glacies.wav";
			} else if (text.contains("Let the virus flow through you!")) {
				nex.currentSpecial = "virus";
				nex.nextSpecial = "no escape";
				nex.attacksTilSpecial = 5;
				playAudio = "virus.wav";
			} else if (text.contains("There is...")) {
				nex.currentSpecial = "no escape";
				nex.nextSpecial = "virus";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 7;
				playAudio = config.noEscape() == SpoonNexConfig.NoEscapeMode.NEX ? "thereIs.wav" : "backInNam.wav";
			}  else if (text.contains("NO ESCAPE!")) {
				if(config.noEscape() == SpoonNexConfig.NoEscapeMode.NEX)
					playAudio = "noEscape.wav";
			} else if (text.contains("Embrace darkness!")) {
				nex.currentSpecial = "darkness";
				nex.nextSpecial = "shadows";
				nex.attacksTilSpecial = 5;
				playAudio = "darkness.wav";
			} else if (text.contains("Fear the shadow!")) {
				nex.currentSpecial = "shadows";
				nex.nextSpecial = "darkness";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 5;
				playAudio = "fearTheShadows.wav";
			} else if (text.contains("I demand a blood sacrifice!")) {
				nex.currentSpecial = "sacrifice";
				nex.nextSpecial = "siphon";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 7;
				playAudio = "bloodSacrifice.wav";
			} else if (text.contains("A siphon will solve this!")) {
				nex.currentSpecial = "siphon";
				nex.nextSpecial = "sacrifice";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 9;
				playAudio = "siphon.wav";
			} else if (text.contains("Contain this!")) {
				nex.currentSpecial = "contain";
				nex.nextSpecial = "ice prison";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 6;
				playAudio = "containThis.wav";
			} else if (text.contains("Die now, in a prison of ice!")) {
				nex.currentSpecial = "ice prison";
				nex.nextSpecial = "contain";
				nex.attacksTilSpecial = 5;
				nex.specialTicksLeft = 14;
				playAudio = "icePrison.wav";
			} else if (text.contains("Taste my wrath!")) {
				nex.currentSpecial = "wrath";
				playAudio = "wrath.wav";
				p5Tick = client.getTickCount();
				timerTicksLeft = 35;
				if(config.phaseChatMessages()) {
					String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P5" : "Zaros Phase";
					String msgText = "Nex " + phaseText + " Complete! Duration: <col=ff0000>" + ticksToTime(p5Tick - p4Tick) + "</col><br>Overall Duration: <col=ff0000>" + ticksToTime(p5Tick - startTick) + "</col>";
					this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", msgText, null);
				}
				if(client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null && client.getLocalPlayer().getName().equals("Null God"))
					event.setMessage("<col=ff0000>Allahuakbar! *Click*</col>");
			} else if (text.contains("Fill my soul with smoke!")) {
				nex.phase = 1;
				activeMage = null;
				nex.attacksTilSpecial = 0;
				nex.invulnerableTicks = 6;
				playAudio = "fillMySoul.wav";
				if(startTick == -1) {
					startTick = client.getTickCount();
					if(config.killTimer() == SpoonNexConfig.KillTimerMode.INFOBOX) {
						BufferedImage image = itemManager.getImage(26348);
						timerBox = new SpoonNexTimerBox(image, config, this, client);
						infoBoxManager.addInfoBox(timerBox);
					}
				}
			} else if (text.contains("Darken my shadow!")) {
				nex.phase = 2;
				activeMage = null;
				nex.attacksTilSpecial = 0;
				nex.invulnerableTicks = 6;
				playAudio = "darkenMyShadow.wav";
				p1Tick = client.getTickCount();
				if(config.phaseChatMessages()) {
					String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P1" : "Smoke Phase";
					this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Complete! Duration: <col=ff0000>" + ticksToTime(p1Tick - startTick) + "</col>", null);
				}
			} else if (text.contains("Flood my lungs with blood!")) {
				nex.phase = 3;
				activeMage = null;
				nex.attacksTilSpecial = 0;
				nex.invulnerableTicks = 6;
				playAudio = "floodMyLungs.wav";
				p2Tick = client.getTickCount();
				if(config.phaseChatMessages()) {
					String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P2" : "Shadow Phase";
					this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Complete! Duration: <col=ff0000>" + ticksToTime(p2Tick - p1Tick) + "</col>", null);
				}
			} else if (text.contains("Infuse me with the power of ice!")) {
				nex.phase = 4;
				activeMage = null;
				nex.attacksTilSpecial = 0;
				nex.invulnerableTicks = 6;
				playAudio = "infuseWithIce.wav";
				p3Tick = client.getTickCount();
				if(config.phaseChatMessages()) {
					String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P3" : "Blood Phase";
					this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Complete! Duration: <col=ff0000>" + ticksToTime(p3Tick - p2Tick) + "</col>", null);
				}
			} else if (text.contains("NOW, THE POWER OF ZAROS!")) {
				nex.phase = 5;
				activeMage = null;
				nex.invulnerableTicks = 6;
				playAudio = "zaros.wav";
				p4Tick = client.getTickCount();
				if(config.phaseChatMessages()) {
					String phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P4" : "Ice Phase";
					this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", "Nex " + phaseText + " Complete! Duration: <col=ff0000>" + ticksToTime(p4Tick - p3Tick) + "</col>", null);
				}
			}

			if (!playAudio.equals("") && config.audio() && config.audioVolume() > 0) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(SpoonNexPlugin.class.getResourceAsStream(playAudio)));
					AudioFormat format = stream.getFormat();
					DataLine.Info info = new DataLine.Info(Clip.class, format);
					nexAudio = (Clip) AudioSystem.getLine(info);
					nexAudio.open(stream);
					FloatControl control = (FloatControl) nexAudio.getControl(FloatControl.Type.MASTER_GAIN);
					if (control != null) {
						control.setValue((float) (config.audioVolume() / 2 - 45));
					}
					nexAudio.setFramePosition(0);
					nexAudio.start();
				} catch (Exception var6) {
					nexAudio = null;
				}
				if(nexAudio != null) {
					nexAudio.setFramePosition(0);
					nexAudio.start();
				}
			}
		} else {
			if (text.contains("Nex has marked you for a blood sacrifice! RUN!")) {
				nex.specialTicksLeft = 7;
				sacrificeTarget = true;
			}
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event) {
		if(config.nexWheelchair() && event.getMenuOption().contains("Attack") && event.getMenuTarget().contains("Nex") && nex != null && nex.invulnerableTicks > 2) {
			event.consume();
		}
	}

	public String ticksToTime(int ticks) {
        int min = ticks / 100;
        int tmp = (ticks - min * 100) * 6;
        int sec = tmp / 10;
        int sec_tenth = tmp - sec * 10;
		return config.usePrecise() ? min + (sec < 10 ? ":0" : ":") + sec + "." + sec_tenth : min + (sec < 10 ? ":0" : ":") + sec;
    }
}
