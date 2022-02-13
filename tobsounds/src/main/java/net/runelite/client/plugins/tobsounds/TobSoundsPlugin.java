package net.runelite.client.plugins.tobsounds;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] ToB Sounds",
        tags = {"bloat", "tob", "windows"},
        enabledByDefault = false
)
public class TobSoundsPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(TobSoundsPlugin.class);

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private TobSoundsConstants constants;

    @Inject
    private TobSoundsConfig config;

    @Provides
    TobSoundsConfig providesConfig(ConfigManager configManager) {
        return (TobSoundsConfig)configManager.getConfig(TobSoundsConfig.class);
    }

    private static Clip clip;
    private boolean bloatDown = false;
    private int stompDelay = 32;

    protected void startUp() {

    }

    protected void shutDown() {

    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(bloatDown){
            stompDelay--;
            if(stompDelay <= 7){
                stompDelay = 32;
                bloatDown = false;

                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("BloatStomp.wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip)AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float)(this.config.soundVolume() / 3 - 35));
                    }
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            String name = npc.getName();
            if (name != null && name.equalsIgnoreCase("pestilent bloat")) {
                if (npc.getAnimation() == 8082 && this.config.bloatSounds()) {
                    bloatDown = true;
                    try {
                        AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("BloatShutdown.wav")));
                        AudioFormat format = stream.getFormat();
                        DataLine.Info info = new DataLine.Info(Clip.class, format);
                        clip = (Clip) AudioSystem.getLine(info);
                        clip.open(stream);
                        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        if (control != null) {
                            control.setValue((float) (this.config.soundVolume() / 2 - 45));
                        }
                        clip.setFramePosition(0);
                        clip.start();
                    } catch (Exception var6) {
                        clip = null;
                    }
                }
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event){
        String message = Text.standardize(event.getMessageNode().getValue());
        if(config.lootSounds()) {
            if (message.contains("found something special: justiciar")) {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("shitloot.wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (this.config.soundVolume() / 2 - 45));
                    }
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            } else if (message.contains("found something special: scythe of vitur")) {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("beans.wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (this.config.soundVolume() / 2 - 45));
                    }
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            }
        }
    }

    @Subscribe
    private void onInteractingChanged(InteractingChanged event) {
        if (config.tankGay() && event.getSource().getName() != null && event.getSource().getName().equalsIgnoreCase("verzik vitur")) {
            if(event.getSource().getInteracting() != null && event.getSource().getInteracting().getName() != null && this.client.getLocalPlayer() != null &&
                    event.getSource().getInteracting().getName().equalsIgnoreCase(this.client.getLocalPlayer().getName())) {
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("wayabovegay.wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (this.config.soundVolume() / 2 - 45));
                    }
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            }
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (event.getActor() instanceof Player) {
            if (client.getVar(Varbits.THEATRE_OF_BLOOD)==2){ // 1=In Party, 2=Inside/Spectator, 3=Dead Spectating
                try {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(TobSoundsPlugin.class.getResourceAsStream("horsey.wav")));
                    AudioFormat format = stream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (this.config.soundVolume() / 2 - 45));
                    }
                    clip.setFramePosition(0);
                    clip.start();
                } catch (Exception var6) {
                    clip = null;
                }
            }
        }
    }
}

