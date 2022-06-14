package net.runelite.client.plugins.cursed;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import java.awt.*;
import java.io.BufferedInputStream;
import java.util.*;
import javax.inject.Inject;
import javax.sound.sampled.*;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Cursed",
        description = " I will see too it that this only gets worse",
        tags = {"spoon", "wtf", "help"},
        enabledByDefault = false
)

public class CursedPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private CursedConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CursedOverlay overlay;

    @Inject
    private CursedBelowWidgetsOverlay bwOverlay;

    public Set<Integer> gameObjects = null;

    public int pulseOpacity = 0;
    public String pulseOpacityUpOrDown = "";

    public ArrayList<Color> raveProjectiles = new ArrayList<>();

    public int psychedelicRed = 30;
    public String psychedelicRedUpDown = "down";
    public int psychedelicGreen = 240;
    public String psychedelicGreenUpDown = "up";
    public int psychedelicBlue = 185;
    public String psychedelicBlueUpDown = "down";

    public int skinwalkerDelay = 6;

    public int catJamFrame = 0;
    private static Clip clip;
    public boolean playCatJam = false;
    public int catJamTimeLeft = 0;
    public float catJamOpacity = 0;

    public int clippyTicks = 0;

    public int gtaTicks = 0;
    public float gtaOpacity = 0;

    @Provides
    CursedConfig provideConfig(ConfigManager configManager) {
        return (CursedConfig) configManager.getConfig(CursedConfig.class);
    }

    @Override
    protected void startUp(){
        reset();
        overlayManager.add(overlay);
        overlayManager.add(bwOverlay);
    }

    @Override
    protected void shutDown(){
        reset();
        if(config.magicTrick()){
            clientThread.invokeLater(() -> client.setGameState(GameState.LOADING));
        }
        overlayManager.remove(overlay);
        overlayManager.remove(bwOverlay);
    }

    private void reset(){
        gameObjects = null;
        pulseOpacity = 0;
        pulseOpacityUpOrDown = "";
        raveProjectiles.clear();
        psychedelicRed = 30;
        psychedelicRedUpDown = "down";
        psychedelicGreen = 230;
        psychedelicGreenUpDown = "up";
        psychedelicBlue = 185;
        psychedelicBlueUpDown = "down";
        skinwalkerDelay = 6;
        catJamFrame = 0;
        catJamTimeLeft = 0;
        playCatJam = false;
        catJamOpacity = 0;
        clippyTicks = 0;
        gtaTicks = 0;
        gtaOpacity = 0;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("SpoonCursed")) {
            if (event.getKey().equals("magicTrick")) {
                if (!config.magicTrick()) {
                    clientThread.invokeLater(() -> client.setGameState(GameState.LOADING));
                } else {
                    removeGameObjectsFromScene(gameObjects, client.getPlane(), true);
                }
            } else if (event.getKey().equals("catJamVolume")) {
                if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (config.catJamVolume() / 2 - 45));
                    }
                }
            } else if (event.getKey().equals("catJam")) {
                if(config.catJam()) {
                    try {
                        AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(CursedPlugin.class.getResourceAsStream("THX.wav")));
                        AudioFormat format = stream.getFormat();
                        DataLine.Info info = new DataLine.Info(Clip.class, format);
                        clip = (Clip)AudioSystem.getLine(info);
                        clip.open(stream);
                        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                        if (control != null)
                            control.setValue((float)(config.catJamVolume() / 2 - 45));
                        playCatJam = true;
                        catJamTimeLeft = 35;
                        catJamOpacity = 0;
                        clip.setFramePosition(0);
                        clip.start();
                    } catch (Exception var6) {
                        clip = null;
                    }
                } else {
                    playCatJam = false;
                    catJamTimeLeft = 0;
                    catJamOpacity = 0;
                    clip.stop();
                }
            } else if (event.getKey().equals("diabloBrewsVolume")) {
                if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (config.diabloBrewsVolume() / 2 - 45));
                    }
                }
            } else if (event.getKey().equals("coxDepressionVolume")) {
                if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (config.coxDepressionVolume() / 2 - 45));
                    }
                }
            } else if (event.getKey().equals("gtaCaVolume")) {
                if (clip != null) {
                    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (control != null) {
                        control.setValue((float) (config.gtaCaVolume() / 2 - 45));
                    }
                }
            }
        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event){
        if(event.getActor().getName() != null && client.getLocalPlayer() != null) {
            if (event.getActor().getName().equals(client.getLocalPlayer().getName())) {
                if (config.bigDie()) {
                    client.playSoundEffect(3892, 10);
                }
                clippyTicks = 8;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if(config.gameTicks()){
            client.playSoundEffect(3892, 20);
        }

        if(config.raveProjectiles()){
            raveProjectiles.clear();
            for(Projectile p : client.getProjectiles()) {
                raveProjectiles.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
            }
        }

        if(config.skinwalkers()){
            skinwalkerDelay--;
            if(skinwalkerDelay <= 0){
                if (client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null) {
                    client.getLocalPlayer().getPlayerComposition().setTransformedNpcId(new Random().nextInt(15000));
                }
                skinwalkerDelay = 6;
            }
        }

        if(config.catJam() && config.catJamVolume() > 0) {
            if (catJamTimeLeft > 0) {
                catJamTimeLeft--;
                if(catJamTimeLeft <= 0) {
                    playCatJam = false;
                    catJamOpacity = 0;
                }
            }
        }

        if (clippyTicks > 0) {
            clippyTicks--;
        }

        if (gtaTicks > 0) {
            gtaTicks--;
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned obj) {
        if(config.magicTrick()){
            Random rand = new Random();
            int rng = rand.nextInt(2);
            if(rng == 1) {
                gameObjects = ImmutableSet.of(obj.getGameObject().getId());
                removeGameObjectsFromScene(gameObjects, client.getPlane(), false);
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        String target = Text.removeTags(event.getTarget()).toLowerCase();
        String option = Text.removeTags(event.getOption()).toLowerCase();
        if ((config.swapEssenceRunning() && target.contains("stamina potion") && !option.contains("drink")) || config.why()) {
            client.setMenuEntries(Arrays.copyOf(client.getMenuEntries(), client.getMenuEntries().length - 1));
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (client.getGameState() == GameState.LOGGED_IN) {
            catJamFrame++;
            if(catJamFrame >= 157){
                catJamFrame = 0;
            }

            if(playCatJam && catJamOpacity < 1f) {
                catJamOpacity += .001f;
            }

            if(config.clientTicks()){
                client.playSoundEffect(3892, 20);
            }

            if(config.pulsingPlayers()){
                if(pulseOpacity <= 0){
                    pulseOpacityUpOrDown = "up";
                }else if(pulseOpacity >= 255){
                    pulseOpacityUpOrDown = "down";
                }

                if(pulseOpacityUpOrDown.equals("up")){
                    pulseOpacity += 4;
                    if(pulseOpacity > 255){
                        pulseOpacity = 255;
                    }
                }else if(pulseOpacityUpOrDown.equals("down")){
                    pulseOpacity -= 4;
                    if(pulseOpacity < 0){
                        pulseOpacity = 0;
                    }
                }
            }

            if(config.psychedelicNpcs()){
                if(psychedelicRed <= 0){
                    psychedelicRedUpDown = "up";
                }else if(psychedelicRed >= 255){
                    psychedelicRedUpDown = "down";
                }

                if(psychedelicGreen <= 0){
                    psychedelicGreenUpDown = "up";
                }else if(psychedelicGreen >= 255){
                    psychedelicGreenUpDown = "down";
                }

                if(psychedelicBlue <= 0){
                    psychedelicBlueUpDown = "up";
                }else if(psychedelicBlue >= 255){
                    psychedelicBlueUpDown = "down";
                }

                if(psychedelicRedUpDown.equals("up")){
                    psychedelicRed++;
                }else if(psychedelicRedUpDown.equals("down")){
                    psychedelicRed--;
                }

                if(psychedelicGreenUpDown.equals("up")){
                    psychedelicGreen++;
                }else if(psychedelicGreenUpDown.equals("down")){
                    psychedelicGreen--;
                }

                if(psychedelicBlueUpDown.equals("up")){
                    psychedelicBlue++;
                }else if(psychedelicBlueUpDown.equals("down")){
                    psychedelicBlue--;
                }
            }

            if(gtaTicks > 0) {
                if (gtaTicks > 6) {
                    gtaOpacity += .010f;
                } else {
                    gtaOpacity -= .010f;
                }

                if (gtaOpacity > 1f) {
                    gtaOpacity = 1f;
                } else if (gtaOpacity < 0) {
                    gtaOpacity = 0;
                }
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        String msg = Text.removeTags(event.getMessage());
        if (event.getMessage().contains("You drink some of the foul liquid.") && config.diabloBrews()) {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(CursedPlugin.class.getResourceAsStream("DiabloPotion.wav")));
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clip = (Clip)AudioSystem.getLine(info);
                clip.open(stream);
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (control != null)
                    control.setValue((float)(config.diabloBrewsVolume() / 2 - 45));
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception var6) {
                clip = null;
            }
        } else if (client.getVarbitValue(Varbits.IN_RAID) == 1 && (msg.contains(" - Dexterous prayer scroll") || msg.contains(" - Arcane prayer scroll"))
                && config.coxDepression()) {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(CursedPlugin.class.getResourceAsStream("emotionalDamage.wav")));
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clip = (Clip)AudioSystem.getLine(info);
                clip.open(stream);
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (control != null)
                    control.setValue((float)(config.coxDepressionVolume() / 2 - 45));
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception var6) {
                clip = null;
            }
        } else if (event.getMessageNode().getType() == ChatMessageType.GAMEMESSAGE && (event.getMessage().contains("New item added to your collection log:")
                || (event.getMessage().contains("Congratulations, you've completed a") && event.getMessage().contains(" combat task:"))) && config.gtaCa()) {
            gtaTicks = 12;
            gtaOpacity = 0;
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(CursedPlugin.class.getResourceAsStream("MissionPassed.wav")));
                AudioFormat format = stream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clip = (Clip)AudioSystem.getLine(info);
                clip.open(stream);
                FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (control != null)
                    control.setValue((float)(config.gtaCaVolume() / 2 - 45));
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception var6) {
                clip = null;
            }
        }
    }

    @Subscribe
    private void onSoundEffectPlayed(SoundEffectPlayed event) {
        if (event.getSoundId() == 2401 && config.diabloBrews()) {
            event.consume();
        }
    }

    public void removeGameObjectsFromScene(Set<Integer> objectIDs, int plane, boolean onToggle) {
        Scene scene = client.getScene();
        Tile[][] tiles = scene.getTiles()[plane];
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                Tile tile = tiles[x][y];
                if (tile != null) {
                    Objects.requireNonNull(scene);
                    if(onToggle){
                        Arrays.<GameObject>stream(tile.getGameObjects()).filter(obj -> (obj != null) && new Random().nextInt(2) == 1).findFirst().ifPresent(scene::removeGameObject);
                    }else {
                        Arrays.<GameObject>stream(tile.getGameObjects()).filter(obj -> (obj != null && objectIDs.contains(obj.getId()))).findFirst().ifPresent(scene::removeGameObject);
                    }
                }
            }
        }
    }
}
