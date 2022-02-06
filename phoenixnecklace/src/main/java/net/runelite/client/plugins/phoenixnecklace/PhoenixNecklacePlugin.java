package net.runelite.client.plugins.phoenixnecklace;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Phoenix Necklace",
        description = "Shows an infobox when you are not wearing a phoenix necklace",
        tags = {"pneck", "bloat", "overlay", "corp", "boost", "kq"},
        enabledByDefault = false
)
public class PhoenixNecklacePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private PhoenixNecklaceConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PhoenixNecklaceOverlay pneckOverlay;

    @Inject
    private RockCakeOverlay cakeOverlay;

    public int currentHp = 0;
    public int maxHp = 0;
    public int cakeDamage = 0;
    public boolean pneckEquipped = false;
    public boolean inRegion = false;
    //private static Clip clip;

    private boolean mirrorMode;

    @Provides
    PhoenixNecklaceConfig provideConfig(ConfigManager configManager) {
        return (PhoenixNecklaceConfig)configManager.getConfig(PhoenixNecklaceConfig.class);
    }

    protected void startUp() {
        reset();
        this.overlayManager.add(pneckOverlay);
        this.overlayManager.add(cakeOverlay);

        /*try
        {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(PhoenixNecklacePlugin.class.getResourceAsStream("3924.wav")));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control != null)
            {
                control.setValue(20.0F * (float)Math.log10((float)config.volume() / 100.0F));
            }
        }
        catch (Exception var6)
        {
            clip = null;
        }*/

    }

    protected void shutDown() {
        reset();
        this.overlayManager.remove(pneckOverlay);
        this.overlayManager.remove(cakeOverlay);
    }

    protected void reset() {
        currentHp = 0;
        maxHp = 0;
        cakeDamage = 0;
        pneckEquipped = false;
        inRegion = false;
        //clip = null;
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            currentHp = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
            maxHp = this.client.getRealSkillLevel(Skill.HITPOINTS);
            cakeDamage = ((int) Math.floor((double) currentHp * .1)) + 1;
            int amuletSlot = Objects.requireNonNull(this.client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.AMULET);
            pneckEquipped = amuletSlot == 11090;

            if (this.client.isInInstancedRegion()){
                inRegion = checkRegionId(true);
            } else{
                inRegion = checkRegionId(false);
            }
        }
    }

    public boolean checkRegionId(boolean isInstance) {
        if (config.showEverywhere()){
            return true;
        }
        if (isInstance) {
            int id = WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID();
            if (config.showTob()){
                if (id == Regions.BLOAT.getRegionId()){
                    return true;
                }
            }
        } else {
            int id = this.client.getLocalPlayer().getWorldLocation().getRegionID();

            if (config.showWild()){
                if (this.client.getVar(Varbits.IN_WILDERNESS) == 1 ){
                    return true;
                }
            }

            if (config.showCorp()){
                if (id == Regions.CORP.getRegionId()){
                    return true;
                }
            }

            if (config.showKq()){
                if (id == Regions.KQ.getRegionId()){
                    return true;
                }
            }

            if(config.showSpooder()){
                if (id == Regions.SIRACHNIS.getRegionId()){
                    return true;
                }
            }
        }
        return false;
    }

    @Subscribe
    private void onChatMessage (ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM) {
            String message = Text.standardize(event.getMessageNode().getValue());
            if (message.contains("your phoenix necklace heals you, but is destroyed in the process.")) {
                /*if (config.sound() && clip != null) {
                    clip.setFramePosition(0);
                    clip.start();
                }*/
                if (config.sound()) {
                    client.playSoundEffect(3924, this.config.volume());
                }
            }
        }
    }

    /*@Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("phoenixnecklace")) {
            if (event.getKey().equals("volume") && clip != null) {
                FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                if (control != null) {
                    control.setValue(20.0F * (float) Math.log10((float) config.volume() / 100.0F));
                }
            }
        }
    }*/

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            pneckOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(pneckOverlay);
            overlayManager.add(pneckOverlay);
            mirrorMode = true;
        }
    }*/

    public enum Regions {
        MAIDEN(12613),
        BLOAT(13125),
        NYLOCAS(13122),
        SOTETSEG(13123),
        SOTETSEG_MAZE(13379),
        XARPUS(12612),
        VERZIK(12611),
        KQ(13972),
        CORP(11844),
        SIRACHNIS(7322);

        private final int regionId;

        Regions(int regionId) {
            this.regionId = regionId;
        }

        public int getRegionId() {
            return this.regionId;
        }
    }
}
