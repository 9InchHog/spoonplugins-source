package net.runelite.client.plugins.spoonjadhelper;

import com.google.inject.Provides;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Jad Helper",
        description = "Help",
        tags = {"six jad", "spoon", "jad", "fight caves", "inferno"},
        enabledByDefault = false
)
public class SpoonJadHelperPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SpoonJadHelperOverlay overlay;

    @Inject
    private SpoonJadHelperConfig config;
	
	public ArrayList<JadInfo> jads = new ArrayList<JadInfo>();
    public int challengeNum = -1;
    public ArrayList<LocalPoint> spawnPoints = new ArrayList<LocalPoint>();

    private boolean mirrorMode;

    @Provides
    SpoonJadHelperConfig getConfig(ConfigManager configManager) {
        return (SpoonJadHelperConfig)configManager.getConfig(SpoonJadHelperConfig.class);
    }

    protected void startUp() {
        reset();
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() {
        reset();
        this.overlayManager.remove(this.overlay);
    }

    private void reset() {
		jads.clear();
        challengeNum = -1;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        for(int i=jads.size()-1; i>=0; i--) {
            jads.get(i).ticks--;
            if(jads.get(i).ticks == 0) {
                jads.remove(i);
			}
		}
    }
	
	@Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();

        if (actor.getName() != null && actor instanceof NPC) {
            NPC npc = (NPC) actor;
            if (actor.getName().equalsIgnoreCase("jaltok-jad")) {
                if (actor.getAnimation() == 7592) {
                    jads.add(new JadInfo(npc, 4, Color.CYAN));
                } else if(actor.getAnimation() == 7593){
                    jads.add(new JadInfo(npc, 4, Color.GREEN));
                } else if (actor.getAnimation() == 7590) {
                    jads.add(new JadInfo(npc, 2, Color.RED));
                }
            }else if (actor.getName().equalsIgnoreCase("tztok-jad")) {
                if (actor.getAnimation() == 2656) {
                    jads.add(new JadInfo(npc, 4, Color.CYAN));
                } else if(actor.getAnimation() == 2652){
                    jads.add(new JadInfo(npc, 4, Color.GREEN));
                } else if (actor.getAnimation() == 2655) {
                    jads.add(new JadInfo(npc, 2, Color.RED));
                }
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if(event.getNpc().getId() == 10623 && challengeNum > 0){
            challengeNum = -1;
            spawnPoints.clear();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if(event.getType() == ChatMessageType.GAMEMESSAGE){
            String msg = Text.removeTags(event.getMessage());
            if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's First Challenge.")){
                challengeNum = 1;
                spawnPoints.add(new LocalPoint(7104, 7104));
            }else if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's Second Challenge.")){
                challengeNum = 2;
                spawnPoints.add(new LocalPoint(7104, 7104));
                spawnPoints.add(new LocalPoint(7104, 5184));
            }else if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's Third Challenge.")){
                challengeNum = 3;
                spawnPoints.add(new LocalPoint(6208, 5696));
                spawnPoints.add(new LocalPoint(7104, 7104));
                spawnPoints.add(new LocalPoint(8000, 5696));
            }else if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's Fourth Challenge.")){
                challengeNum = 4;
                spawnPoints.add(new LocalPoint(6464, 5440));
                spawnPoints.add(new LocalPoint(6464, 6848));
                spawnPoints.add(new LocalPoint(7744, 5440));
                spawnPoints.add(new LocalPoint(7744, 6848));
            }else if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's Fifth Challenge.")){
                challengeNum = 5;
                spawnPoints.add(new LocalPoint(6208, 6336));
                spawnPoints.add(new LocalPoint(6592, 5440));
                spawnPoints.add(new LocalPoint(7104, 7104));
                spawnPoints.add(new LocalPoint(8000, 6336));
                spawnPoints.add(new LocalPoint(7616, 5440));
            }else if(msg.equalsIgnoreCase("You enter the Inferno for TzHaar-Ket-Rak's Sixth Challenge.")){
                challengeNum = 6;
                spawnPoints.add(new LocalPoint(6208, 5696));
                spawnPoints.add(new LocalPoint(6208, 6592));
                spawnPoints.add(new LocalPoint(7104, 5184));
                spawnPoints.add(new LocalPoint(7104, 7104));
                spawnPoints.add(new LocalPoint(8000, 6592));
                spawnPoints.add(new LocalPoint(8000, 5696));
            }
        }   
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            mirrorMode = true;
        }
    }*/
}
