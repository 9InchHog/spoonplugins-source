package net.runelite.client.plugins.spoontempoross;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Tempoross",
        description = "All-in-one plugin for the Tempoross.",
        tags = {"Tempoross"},
        enabledByDefault = false
)
public class SpoonTemporossPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SpoonTemporossOverlay overlay;

    @Inject
    private SpoonTemporossConfig config;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    public ArrayList<GameObject> tetherList = new ArrayList<>();
    public ArrayList<GameObject> repairList = new ArrayList<>();
    public ArrayList<GameObject> shrineList = new ArrayList<>();
    public ArrayList<GameObject> cloudList = new ArrayList<>();
    public ArrayList<GameObject> fireList = new ArrayList<>();

    public boolean hasCookedFish = false;
    public boolean hasRawFish = false;
    public boolean waveComing = false;
    public boolean temporossVulnerable = false;

    public int vulnTicks = 12;
    public int cloudTicks = 17;

    public int fishCount = 0;
    public FishInfoBox box = null;

    private boolean mirrorMode;

    @Provides
    SpoonTemporossConfig getConfig(ConfigManager configManager) {
        return (SpoonTemporossConfig)configManager.getConfig(SpoonTemporossConfig.class);
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
        tetherList.clear();
        repairList.clear();
        shrineList.clear();
        cloudList.clear();
        fireList.clear();

        hasCookedFish = false;
        hasRawFish = false;
        waveComing = false;
        temporossVulnerable = false;

        vulnTicks = 12;
        cloudTicks = 17;

        fishCount = 0;
        infoBoxManager.removeInfoBox(box);
        box = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(event.getKey().equals("displayFishCount")){
            if(config.displayFishCount()) {
                box = new FishInfoBox(itemManager.getImage(ItemID.RAW_HARPOONFISH), this.client, this);
                this.infoBoxManager.addInfoBox(box);
            }else {
                this.infoBoxManager.removeInfoBox(box);
                box = null;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if(config.vulnTicks()) {
            if (temporossVulnerable) {
                vulnTicks--;
                if (vulnTicks <= 0) {
                    temporossVulnerable = false;
                    vulnTicks = 12;
                }
            }
        }

        if(config.fireTicks()) {
            if(cloudList.size() > 0){
                cloudTicks--;
                if (cloudTicks <= 0) {
                    cloudTicks = 17;
                }
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState currentState = event.getGameState();
        if (currentState.equals(GameState.CONNECTION_LOST) || currentState.equals(GameState.HOPPING) || currentState.equals(GameState.LOGGING_IN)) {
            reset();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (this.client.getVarbitValue(6719) == 2) {
            reset();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc().getId() == 10571){
            temporossVulnerable = false;
            vulnTicks = 12;
        }else if(event.getNpc().getId() == 10569){
            this.client.setHintArrow(event.getNpc());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if(event.getNpc().getId() == 10569){
            this.client.clearHintArrow();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (event.getGameObject().getId() == 41236){
            shrineList.add(event.getGameObject());
        }else if (event.getGameObject().getId() == 41352 || event.getGameObject().getId() == 41353 || event.getGameObject().getId() == 41354 || event.getGameObject().getId() == 41355){
            tetherList.add(event.getGameObject());
        }else if (event.getGameObject().getId() == 40996 || event.getGameObject().getId() == 40997 || event.getGameObject().getId() == 41010 || event.getGameObject().getId() == 41011){
            repairList.add(event.getGameObject());
        }else if (event.getGameObject().getId() == 41006){
            cloudList.add(event.getGameObject());
        }else if (event.getGameObject().getId() == 41005){
            fireList.add(event.getGameObject());
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (event.getGameObject().getId() == 41236){
            shrineList.remove(event.getGameObject());
        }else if (event.getGameObject().getId() == 41352 || event.getGameObject().getId() == 41353 || event.getGameObject().getId() == 41354 || event.getGameObject().getId() == 41355){
            tetherList.remove(event.getGameObject());
        }else if (event.getGameObject().getId() == 40996 || event.getGameObject().getId() == 40997 || event.getGameObject().getId() == 41010 || event.getGameObject().getId() == 41011){
            repairList.remove(event.getGameObject());
        }else if (event.getGameObject().getId() == 41006){
            cloudList.remove(event.getGameObject());
            if(cloudList.size() == 0){
                cloudTicks = 17;
            }
        }else if (event.getGameObject().getId() == 41005){
            fireList.remove(event.getGameObject());
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if(event.getContainerId() == InventoryID.INVENTORY.getId()){
            fishCount = 0;
            if(event.getItemContainer().count(25565) > 0){
                fishCount += event.getItemContainer().count(25565);
                hasCookedFish = true;
            }else {
                hasCookedFish = false;
            }

            if(event.getItemContainer().count(25564) > 0){
                fishCount += event.getItemContainer().count(25564);
                hasRawFish = true;
            }else {
                hasRawFish = false;
            }

            if(event.getItemContainer().count(25566) > 0){
                fishCount += event.getItemContainer().count(25566);
            }
            
            if(this.config.displayFishCount()){
                if (box != null) {
                    this.infoBoxManager.removeInfoBox(box);
                }

                if(fishCount > 0) {
                    box = new FishInfoBox(itemManager.getImage(ItemID.RAW_HARPOONFISH), this.client, this);
                    this.infoBoxManager.addInfoBox(box);
                }
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String msg = Text.removeTags(event.getMessage());
        if(msg.equals("A colossal wave closes in...")){
            waveComing = true;
        }else if(msg.equals("...the rope keeps you securely upright as the wave washes over you.") || msg.equals("...the wave slams into you, knocking you to the ground.")){
            waveComing = false;
        }else if(msg.equals("Tempoross is vulnerable!")){
            temporossVulnerable = true;
        }else if(msg.equals("Tempoross has been subdued!")){
            reset();
        }
    }

    /*@Subscribe
    private void onClientTick(ClientTick event) {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            mirrorMode = true;
        }
    }*/
}
