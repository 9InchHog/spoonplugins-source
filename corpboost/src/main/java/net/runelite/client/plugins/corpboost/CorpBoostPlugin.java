package net.runelite.client.plugins.corpboost;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.corpboost.spots.*;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.runelite.api.ObjectID.CANNON_BASE;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Corp Additions",
        description = "Extra plugins for corp",
        tags = {"sigil", "stun", "corp", "core", "spear", "tbow", "healer", "boost", "booster", "cannon"}
)

public class CorpBoostPlugin extends Plugin
{
    @Getter
    private boolean cannonPlaced;

    @Getter
    private WorldPoint cannonPosition;

    @Getter
    private int cannonWorld = -1;

    @Getter
    private GameObject cannon;

    @Getter
    private List<WorldPoint> cannonSpotPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> spearHealerPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> spearAltPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> tbowHealerPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> stunnerPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> dwhAltPoints = new ArrayList<>();

    @Getter
    private List<WorldPoint> dwhAltPoints2 = new ArrayList<>();

    @Getter
    private List<WorldPoint> customerPoints = new ArrayList<>();

    @Inject
    private ItemManager itemManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private Notifier notifier;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CorpBoostOverlay overlay;

    @Inject
    private CorpCannonOverlay cannonSpotOverlay;

    @Inject
    private CorpBoostConfig config;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private final ArrayListMultimap<String, Integer> optionIndexes = ArrayListMultimap.create();

    public NPC corp = null;
    public NPC core = null;

    @Provides
    CorpBoostConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CorpBoostConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(cannonSpotOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        cannonSpotOverlay.setHidden(true);
        overlayManager.remove(overlay);
        overlayManager.remove(cannonSpotOverlay);
        cannonPlaced = false;
        cannonWorld = -1;
        cannonPosition = null;
        cannonSpotPoints.clear();
        spearHealerPoints.clear();
        spearAltPoints.clear();
        tbowHealerPoints.clear();
        customerPoints.clear();
        dwhAltPoints.clear();
        dwhAltPoints2.clear();
        stunnerPoints.clear();
        core = null;
        corp = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(!config.coreArrow() && core != null){
            this.client.clearHintArrow();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY))
        {
            return;
        }

        boolean hasBase = false;
        boolean hasStand = false;
        boolean hasBarrels = false;
        boolean hasFurnace = false;
        boolean hasAll = false;

        if (!cannonPlaced)
        {
            for (Item item : event.getItemContainer().getItems())
            {
                if (item == null)
                {
                    continue;
                }

                switch (item.getId())
                {
                    case ItemID.CANNON_BASE:
                        hasBase = true;
                        break;
                    case ItemID.CANNON_STAND:
                        hasStand = true;
                        break;
                    case ItemID.CANNON_BARRELS:
                        hasBarrels = true;
                        break;
                    case ItemID.CANNON_FURNACE:
                        hasFurnace = true;
                        break;
                }

                if (hasBase && hasStand && hasBarrels && hasFurnace)
                {
                    hasAll = true;
                    break;
                }
            }
        }

        cannonSpotOverlay.setHidden(!hasAll);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        cannonSpotPoints.clear();
        for (WorldPoint spot : CorpCannonSpots.getCorpCannonSpots())
        {
            if (WorldPoint.isInScene(client, spot.getX(), spot.getY()))
            {
                cannonSpotPoints.add(spot);
            }
        }

        spearHealerPoints.clear();
        for (WorldPoint healerSpot : SpearHealerSpots.getSpearHealerSpots())
        {
            if (WorldPoint.isInScene(client, healerSpot.getX(), healerSpot.getY()))
            {
                spearHealerPoints.add(healerSpot);
            }
        }

        spearAltPoints.clear();
        for (WorldPoint spearSpot : SpearAltSpots.getSpearAltSpots())
        {
            if (WorldPoint.isInScene(client, spearSpot.getX(), spearSpot.getY()))
            {
                spearAltPoints.add(spearSpot);
            }
        }

        tbowHealerPoints.clear();
        for (WorldPoint bowSpot : TbowHealerSpots.getTbowHealerSpots())
        {
            if (WorldPoint.isInScene(client, bowSpot.getX(), bowSpot.getY()))
            {
                tbowHealerPoints.add(bowSpot);
            }
        }

        customerPoints.clear();
        for (WorldPoint customerSpot : CustomerSpot.getCustomerSpots())
        {
            if (client.getLocalPlayer().getWorldLocation().equals(customerPoints)){
                customerPoints.clear();
            }else if (WorldPoint.isInScene(client, customerSpot.getX(), customerSpot.getY())) {
                customerPoints.add(customerSpot);
            }
        }

        dwhAltPoints.clear();
        for (WorldPoint dwhAltSpot : DwhAltSpots.getDwhAltSpots())
        {
            if (client.getLocalPlayer().getWorldLocation().equals(dwhAltPoints)){
                dwhAltPoints.clear();
            }else if (WorldPoint.isInScene(client, dwhAltSpot.getX(), dwhAltSpot.getY())) {
                dwhAltPoints.add(dwhAltSpot);
            }
        }

        dwhAltPoints2.clear();
        for (WorldPoint dwhAltSpot2 : DwhAltSpots2.getDwhAltSpots2())
        {
            if (client.getLocalPlayer().getWorldLocation().equals(dwhAltPoints2)){
                dwhAltPoints2.clear();
            }else if (WorldPoint.isInScene(client, dwhAltSpot2.getX(), dwhAltSpot2.getY())) {
                dwhAltPoints2.add(dwhAltSpot2);
            }
        }

        stunnerPoints.clear();
        for (WorldPoint stunnerSpot : StunnerSpot.getStunnerSpots())
        {
            if (client.getLocalPlayer().getWorldLocation().equals(stunnerPoints)){
                stunnerPoints.clear();
            }else if (WorldPoint.isInScene(client, stunnerSpot.getX(), stunnerSpot.getY())) {
                stunnerPoints.add(stunnerSpot);
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();

        Player localPlayer = client.getLocalPlayer();
        if (gameObject.getId() == CANNON_BASE && !cannonPlaced)
        {
            if (localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2
                    && localPlayer.getAnimation() == AnimationID.BURYING_BONES)
            {
                cannonPosition = gameObject.getWorldLocation();
                cannonWorld = client.getWorld();
                cannon = gameObject;
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
        {
            return;
        }

        if (event.getMessage().equals("You add the furnace."))
        {
            cannonPlaced = true;

        }

        if (event.getMessage().contains("You pick up the cannon")
                || event.getMessage().contains("Your cannon has decayed. Speak to Nulodion to get a new one!"))
        {
            cannonPlaced = false;
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if(event.getNpc().getId() == 319){
            corp = event.getNpc();
        }

        if(event.getNpc().getId() == 320){
            core = event.getNpc();
            if(config.coreArrow()) {
                this.client.setHintArrow(event.getNpc());
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if(event.getNpc().getId() == 319){
            corp = null;
        }

        if(event.getNpc().getId() == 320 && event.getNpc().isDead()){
            core = null;
            if(config.coreArrow()) {
                this.client.clearHintArrow();
            }
        }
    }

    private void swapMenuEntry(int index, MenuEntry menuEntry) {
        String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
        String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

        if(config.bpCore() && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getPlayerComposition() != null &&
                option.contains("attack") && target.equals("dark energy core  (level-75)")){
            int weapon = Objects.requireNonNull(this.client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
            int helm = Objects.requireNonNull(this.client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.HEAD);

            if (weapon == 12926 && (helm == 12931 || helm == 13197 || helm == 13199)){
                this.client.setMenuEntries(new MenuEntry[]{menuEntry});
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (this.client.getGameState() != GameState.LOGGED_IN || this.client.isMenuOpen())
            return;
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        int idx = 0;
        this.optionIndexes.clear();
        for (MenuEntry entry : menuEntries) {
            String option = Text.removeTags(entry.getOption()).toLowerCase();
            this.optionIndexes.put(option, Integer.valueOf(idx++));
        }
        idx = 0;
        for (MenuEntry entry : menuEntries)
            swapMenuEntry(idx++, entry);
    }
}
