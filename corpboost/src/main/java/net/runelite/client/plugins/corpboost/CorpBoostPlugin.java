package net.runelite.client.plugins.corpboost;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
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
    private final List<WorldPoint> cannonSpotPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> stunSpearPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> xferSpearPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> stunHealerPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> xferHealerPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> stunDwhPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> xferDwhPoints = new ArrayList<>();

    @Getter
    private final List<WorldPoint> customerPoints = new ArrayList<>();

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
    int coreStunTick = -1;

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
        customerPoints.clear();
        stunSpearPoints.clear();
        xferSpearPoints.clear();
        stunHealerPoints.clear();
        xferHealerPoints.clear();
        stunDwhPoints.clear();
        xferDwhPoints.clear();
        core = null;
        corp = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if(!config.coreArrow() && core != null){
            client.clearHintArrow();
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
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
        {
            return;
        }

        if (config.hidePlayersTillBossSpawn()) {
            if (!temporaryHide && withinBounds(2964, 2970, 4379, 4387)) {
                TemporarilyHidePlayers(true, config.hidePlayersDuration());
            }
        }

        cannonSpotPoints.clear();
        for (WorldPoint spot : CorpCannonSpots.getCorpCannonSpots())
        {
            if (WorldPoint.isInScene(client, spot.getX(), spot.getY()))
            {
                cannonSpotPoints.add(spot);
            }
        }

        customerPoints.clear();
        for (WorldPoint customerSpot : CustomerSpot.getCustomerSpots())
        {
            if (customerPoints.contains(client.getLocalPlayer().getWorldLocation())){
                customerPoints.clear();
            }else if (WorldPoint.isInScene(client, customerSpot.getX(), customerSpot.getY())) {
                customerPoints.add(customerSpot);
            }
        }

        stunSpearPoints.clear();
        for (WorldPoint stunSpearSpot : StunSpearSpots.getStunSpearSpots())
        {
            if (WorldPoint.isInScene(client, stunSpearSpot.getX(), stunSpearSpot.getY()))
            {
                stunSpearPoints.add(stunSpearSpot);
            }
        }

        xferSpearPoints.clear();
        for (WorldPoint xferSpearSpot : XferSpearSpots.getXferSpearSpots())
        {
            if (WorldPoint.isInScene(client, xferSpearSpot.getX(), xferSpearSpot.getY()))
            {
                xferSpearPoints.add(xferSpearSpot);
            }
        }

        stunHealerPoints.clear();
        for (WorldPoint stunHealerSpot : StunHealerSpots.getStunHealerSpots())
        {
            if (WorldPoint.isInScene(client, stunHealerSpot.getX(), stunHealerSpot.getY()))
            {
                stunHealerPoints.add(stunHealerSpot);
            }
        }

        xferHealerPoints.clear();
        for (WorldPoint xferHealerSpot : XferHealerSpots.getXferHealerSpots())
        {
            if (WorldPoint.isInScene(client, xferHealerSpot.getX(), xferHealerSpot.getY()))
            {
                xferHealerPoints.add(xferHealerSpot);
            }
        }

        stunDwhPoints.clear();
        for (WorldPoint stunDwhSpot : StunDwhSpots.getStunDwhSpots())
        {
            if (stunDwhPoints.contains(client.getLocalPlayer().getWorldLocation())){
                stunDwhPoints.clear();
            } else if (WorldPoint.isInScene(client, stunDwhSpot.getX(), stunDwhSpot.getY())) {
                stunDwhPoints.add(stunDwhSpot);
            }
        }

        xferDwhPoints.clear();
        for (WorldPoint xferDwhSpot : XferDwhSpots.getXferDwhSpots())
        {
            if (xferDwhPoints.contains(client.getLocalPlayer().getWorldLocation())){
                xferDwhPoints.clear();
            }else if (WorldPoint.isInScene(client, xferDwhSpot.getX(), xferDwhSpot.getY())) {
                xferDwhPoints.add(xferDwhSpot);
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();

        Player localPlayer = client.getLocalPlayer();
        if (gameObject.getId() == CANNON_BASE && !cannonPlaced && localPlayer != null)
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
        if (event.getNpc().getId() == NpcID.CORPOREAL_BEAST){
            corp = event.getNpc();
        }

        if (event.getNpc().getId() == NpcID.DARK_ENERGY_CORE){
            core = event.getNpc();
            coreStunTick = -1;
            if (config.coreArrow()) {
                client.setHintArrow(event.getNpc());
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (event.getNpc().getId() == NpcID.CORPOREAL_BEAST){
            corp = null;
            coreStunTick = -1;
        }

        if (event.getNpc().getId() == NpcID.DARK_ENERGY_CORE && event.getNpc().isDead()){
            core = null;
            coreStunTick = -1;
            if (config.coreArrow()) {
                client.clearHintArrow();
            }
        }
    }

    @Subscribe
    public void onActorDeath (ActorDeath event) {
        if (event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            if (npc.getId() == NpcID.CORPOREAL_BEAST || npc.getId() == NpcID.DARK_ENERGY_CORE) {
                coreStunTick = -1;
            }
        }
    }

    private void swapMenuEntry(int index, MenuEntry menuEntry) {
        String option = Text.standardize(menuEntry.getOption(), true).toLowerCase();
        String target = Text.standardize(menuEntry.getTarget(), true).toLowerCase();

        if (config.bpCore() && client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null &&
                option.contains("attack") && target.equals("dark energy core")){
            int weapon = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);
            int helm = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.HEAD);

            if (weapon == ItemID.TOXIC_BLOWPIPE && (helm == ItemID.SERPENTINE_HELM || helm == ItemID.TANZANITE_HELM || helm == ItemID.MAGMA_HELM)){
                client.setMenuEntries(new MenuEntry[]{menuEntry});
            }
        }
    }

    int lastRangeXP = -1;

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (statChanged.getSkill() == Skill.RANGED) {
            int rangeXP = client.getSkillExperience(Skill.RANGED);
            if (lastRangeXP == -1) {
                lastRangeXP = rangeXP;
                return;
            }
            int diff = rangeXP - lastRangeXP;
            lastRangeXP = rangeXP;
            if (diff > 0)
                HandleCoreCheck(client.getLocalPlayer());
        }
    }

    void HandleCoreCheck(Player player) {
        if (player != null && player.getInteracting() != null) {
            NPC npc = (NPC) player.getInteracting();
            if (npc != null && npc.getId() == NpcID.DARK_ENERGY_CORE) {
                if (player.getAnimation() == 5061) {
                    coreStunTick = client.getTickCount();
                }
            }
        }
    }

    @Subscribe
    public void onFakeXpDrop(FakeXpDrop fakeXpDrop) {
        if (fakeXpDrop.getSkill() == Skill.RANGED) {
            int diff = fakeXpDrop.getXp();
            if (diff > 0)
                HandleCoreCheck(client.getLocalPlayer());
        }
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
            return;
        MenuEntry[] menuEntries = client.getMenuEntries();
        int idx = 0;
        optionIndexes.clear();
        for (MenuEntry entry : menuEntries) {
            String option = Text.removeTags(entry.getOption()).toLowerCase();
            optionIndexes.put(option, idx++);
        }
        idx = 0;
        for (MenuEntry entry : menuEntries)
            swapMenuEntry(idx++, entry);
    }

    @Subscribe
    public void onBeforeRender(BeforeRender event) {
        if (config.hideBlack())
            HideWidgets(71, new int[] { 0 });
        if (config.hideMore())
            HideWidgets(71, new int[] { 1, 2, 3, 4 });
        if (config.hideOps())
            HideWidgets(370, new int[] { 17, 18 });
    }

    public void HideWidgets(int groupId, int[] childIds) {
        for (int childId : childIds) {
            Widget widget = client.getWidget(groupId, childId);
            if (widget != null && !widget.isSelfHidden())
                widget.setHidden(true);
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (config.hidePlayersTillBossSpawn() && client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null) {
            String sourceName = (event.getSource() != null && event.getSource().getName() != null) ? event.getSource().getName().toLowerCase() : "";
            String targetName = (event.getTarget() != null && event.getTarget().getName() != null) ? event.getTarget().getName().toLowerCase() : "";
            if (!sourceName.equals(client.getLocalPlayer().getName().toLowerCase()))
                return;
            if (targetName.equals("corporeal beast")) {
                TemporarilyHidePlayers(false, -1);
            }
        }
    }

    int unhideFailSafeTick = -1;
    public boolean temporaryHide = false;

    public void TemporarilyHidePlayers(boolean hiding, int failSafeTicks) {
        temporaryHide = hiding;
        if (hiding) {
            unhideFailSafeTick = client.getTickCount() + failSafeTicks;
            hideShit(true);
        } else {
            hideShit(false);
            unhideFailSafeTick = -1;
        }
    }

    public void hideShit (boolean hide) {
        if (hide) {
            client.setIsHidingEntities(true);
            client.setOthersHidden(true);
            client.setOthersHidden2D(true);
            client.setFriendsHidden(true);
            client.setFriendsChatMembersHidden(true);
            client.setClanChatMembersHidden(true);
        } else {
            client.setOthersHidden(false);
            client.setOthersHidden2D(false);
            client.setFriendsHidden(false);
            client.setFriendsChatMembersHidden(false);
            client.setClanChatMembersHidden(false);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (unhideFailSafeTick != -1) {
            if (client.getTickCount() >= unhideFailSafeTick) {
                unhideFailSafeTick = -1;
                TemporarilyHidePlayers(false, -1);
            }
        }

        if (coreStunTick == -1 && core != null)
            for (Player player : client.getPlayers()) {
                if (player == null)
                    continue;
                if (player == client.getLocalPlayer())
                    continue;
                HandleCoreCheck(player);
            }
    }

    public boolean withinBounds(int x1, int x2, int y1, int y2) {
        if (client.getLocalPlayer() == null)
            return false;
        if (client.getLocalPlayer().getLocalLocation() == null)
            return false;
        WorldPoint point = client.getLocalPlayer().getWorldLocation();
        return (point.getX() >= x1 && point.getX() <= x2 && point.getY() >= y1 && point.getY() <= y2);
    }
}
