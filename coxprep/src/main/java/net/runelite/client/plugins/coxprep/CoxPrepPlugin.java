package net.runelite.client.plugins.coxprep;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Cox Prep",
        description = "Track prep shit woo yay",
        tags = {"xeric", "prep", "chambers", "cox", "buchu", "golpar", "nox", "seeds"}
)

public class CoxPrepPlugin extends Plugin{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CoxPrepConfig config;

    @Inject
    private CoxPrepOverlay overlay;

    public int roomtype;

    private int plane;

    private int base_x;

    private int base_y;

    int room_base_x;

    int room_base_y;

    int rot;

    int wind;

    //Herbs
    public static final int GRIMY_NOXIFER = 20901;
    public static final int NOXIFER = 20902;
    public static final int GRIMY_GOLPAR = 20904;
    public static final int GOLPAR = 20905;
    public static final int GRIMY_BUCHU_LEAF = 20907;
    public static final int BUCHU_LEAF = 20908;

    //2nds
    public static final int STINKHORN_MUSHROOM = 20910;
    public static final int ENDARKENED_JUICE = 20911;
    public static final int CICELY = 20912;

    public int totalBuchus = 0;
    public int totalGolpar = 0;
    public int totalNox = 0;
    public int inventoryBuchus = 0;
    public int inventoryGolpar = 0;
    public int inventoryNox = 0;
    public boolean pickedHerb = false;
    public int totalBrews = 0;
    public int totalRevites = 0;
    public int totalEnhances = 0;
    public int totalElders = 0;
    public int totalTwisteds = 0;
    public int totalKodais = 0;
    public int totalOverloads = 0;
    public int inventoryBrews = 0;
    public int inventoryRevites = 0;
    public int inventoryEnhances = 0;
    public int inventoryElders = 0;
    public int inventoryTwisteds = 0;
    public int inventoryKodais = 0;
    public int inventoryOverloads = 0;
    public boolean potMade = false;

    public int pickedJuice = 0;
    public int pickedShrooms = 0;
    public int pickedCicely = 0;

    @Provides
    CoxPrepConfig getConfig(ConfigManager configManager) {
        return (CoxPrepConfig) configManager.getConfig(CoxPrepConfig.class);
    }

    @Override
    protected void startUp()
    {
        reset();
        this.overlayManager.add(this.overlay);
    }

    @Override
    protected void shutDown()
    {
        reset();
        this.overlayManager.remove(this.overlay);
    }

    protected void reset() {
        this.roomtype = -1;
        totalBuchus = 0;
        totalGolpar = 0;
        totalNox = 0;
        pickedJuice = 0;
        pickedShrooms = 0;
        pickedCicely = 0;
        inventoryBuchus = 0;
        inventoryGolpar = 0;
        inventoryNox = 0;
        pickedHerb = false;
        totalBrews = 0;
        totalRevites = 0;
        totalEnhances = 0;
        totalElders = 0;
        totalTwisteds = 0;
        totalKodais = 0;
        totalOverloads = 0;
        inventoryBrews = 0;
        inventoryRevites = 0;
        inventoryEnhances = 0;
        inventoryElders = 0;
        inventoryTwisteds = 0;
        inventoryKodais = 0;
        inventoryOverloads = 0;
        potMade = false;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (this.client.getVarbitValue(Varbits.IN_RAID) == 0) {
            if (this.roomtype != -1)
                try {
                    shutDown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            return;
        }
        int plane = this.client.getPlane();
        int base_x = this.client.getBaseX();
        int base_y = this.client.getBaseY();
        if (this.base_x != base_x || this.base_y != base_y || this.plane != plane) {
            this.base_x = base_x;
            this.base_y = base_y;
            this.plane = plane;
        }
        WorldPoint wp = this.client.getLocalPlayer().getWorldLocation();
        int x = wp.getX() - this.client.getBaseX();
        int y = wp.getY() - this.client.getBaseY();
        int type = CoxPrepUtils.getroom_type(this.client.getInstanceTemplateChunks()[plane][x / 8][y / 8]);
        if (type != this.roomtype) {
            if (type == 3 || type == 2) {
                this.overlayManager.add(this.overlay);
            } else {
                this.overlayManager.remove(this.overlay);
            }
            this.roomtype = type;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if(e.getActor().getName() != null && this.client.getLocalPlayer() != null) {
            if (e.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
                if (e.getActor().getAnimation() == 2282) {
                    pickedHerb = true;
                }else {
                    pickedHerb = false;
                }

                if (e.getActor().getAnimation() == 363) {
                    potMade = true;
                }else {
                    potMade = false;
                }
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e) {
        if(client.getVarbitValue(Varbits.IN_RAID) == 1) {
            int nox = e.getItemContainer().count(20901) + e.getItemContainer().count(20902);
            int golpar = e.getItemContainer().count(20904) + e.getItemContainer().count(20905);
            int buchus = e.getItemContainer().count(20907) + e.getItemContainer().count(20908);
            int brews = e.getItemContainer().count(20984);
            int revites = e.getItemContainer().count(20960);
            int enhances = e.getItemContainer().count(20972);
            int elders = e.getItemContainer().count(20924);
            int twisteds = e.getItemContainer().count(20936);
            int kodais = e.getItemContainer().count(20948);
            int overloads = e.getItemContainer().count(20996);

            if(e.getContainerId() == 93) {
                if(pickedHerb) {
                    if(nox > inventoryNox){
                        totalNox++;
                    }
                    if(golpar > inventoryGolpar){
                        totalGolpar++;
                    }
                    if(buchus > inventoryBuchus){
                        totalBuchus++;
                    }
                }

                if(potMade) {
                    if(brews > inventoryBrews){
                        totalBrews++;
                    }
                    if(revites > inventoryRevites){
                        totalRevites++;
                    }
                    if(enhances > inventoryEnhances){
                        totalEnhances++;
                    }
                    if(elders > inventoryElders){
                        totalElders++;
                    }
                    if(twisteds > inventoryTwisteds){
                        totalTwisteds++;
                    }
                    if(kodais > inventoryKodais){
                        totalKodais++;
                    }
                    if(overloads > inventoryOverloads){
                        totalOverloads++;
                    }
                }

                inventoryNox = nox;
                inventoryGolpar = golpar;
                inventoryBuchus = buchus;
                inventoryBrews = brews;
                inventoryRevites = revites;
                inventoryEnhances = enhances;
                inventoryElders = elders;
                inventoryTwisteds = twisteds;
                inventoryKodais = kodais;
                inventoryOverloads = overloads;
                pickedJuice = e.getItemContainer().count(20911);
                pickedShrooms = e.getItemContainer().count(20910);
                pickedCicely = e.getItemContainer().count(20912);
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        boolean tempInRaid = client.getVarbitValue(Varbits.IN_RAID) == 1;

        // if the player's raid state has changed
        if (!tempInRaid) {
            reset();
            this.overlayManager.remove(this.overlay);
        }
    }
}
