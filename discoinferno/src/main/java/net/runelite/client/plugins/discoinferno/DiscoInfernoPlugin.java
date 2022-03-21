package net.runelite.client.plugins.discoinferno;

import com.google.inject.Provides;

import java.awt.*;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.sound.sampled.*;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Disco Inferno",
        description = "Help",
        tags = {"spoon", "trammps"},
        enabledByDefault = false
)
public class DiscoInfernoPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private DiscoInfernoConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DiscoInfernoOverlay overlay;

    public boolean entered = false;
    public int enterDelay = 5;
    public int sceneX = 0;
    public int sceneY = 0;

    private static final int INFERNO_REGION = 9043;
    private static Clip clip;
    public ArrayList<Color> discoColors = new ArrayList<Color>();
    public int discoDelay = 2;

    @Provides
    DiscoInfernoConfig getConfig(ConfigManager configManager) {
        return (DiscoInfernoConfig)configManager.getConfig(DiscoInfernoConfig.class);
    }

    protected void startUp() {
        if(isInInferno()){
            if(config.trammps()){
                loadSound();
            }
        }
        this.overlayManager.add(overlay);
    }

    protected void shutDown() {
        if(clip != null && clip.isRunning()){
            clip.stop();
        }
        this.overlayManager.remove(overlay);
    }

    protected void reset() {
        discoColors.clear();
        discoDelay = 2;
        entered = false;
        sceneX = 0;
        sceneY = 0;
    }

    public boolean isInInferno() {
        return ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION);
    }

    public void loadSound() {
        try {
            BufferedInputStream buffStream = new BufferedInputStream(DiscoInfernoPlugin.class.getResourceAsStream("Disco_Inferno.wav"));
            AudioInputStream stream = AudioSystem.getAudioInputStream(buffStream);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            int intVol = this.config.volume() - 50;
            float floatVol = (float) intVol;

            if(floatVol < -50.0f){
                floatVol = -50.0f;
            }else if(floatVol > 6.0f){
                floatVol = 6.0f;
            }
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(floatVol);
            clip.loop(10000);
        } catch (Exception e) {
            e.printStackTrace();
            clip = null;
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if(enterDelay > 0){
            enterDelay--;
        }

        if(isInInferno()){
            if(enterDelay <= 0){
                entered = true;
            }
        }else {
            reset();
        }

        if(config.trammps()) {
            if (isInInferno()) {
                if (clip != null) {
                    if (!clip.isRunning()) {
                        loadSound();
                    }
                } else {
                    loadSound();
                }
            } else {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
            }
        }

        if(config.boogie()){
            if(discoColors.size() > 0){
                discoDelay--;
                if(discoDelay <= 0){
                    discoColors.clear();
                }
            }
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if(clip != null){
            if(clip.isRunning()){
                int intVol = this.config.volume() - 50;
                float floatVol = (float) intVol;

                if(floatVol < -50.0f){
                    floatVol = -50.0f;
                }else if(floatVol > 6.0f){
                    floatVol = 6.0f;
                }
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(floatVol);
            }
        }

        if(event.getKey().equals("trammps")){
            if(!config.trammps()){
                if(clip != null){
                    if(clip.isRunning()){
                        clip.stop();
                    }
                }
            }else{
                if(clip != null){
                    if(!clip.isRunning()){
                        clip.loop(10000);
                    }
                }
            }
        }
    }
}
