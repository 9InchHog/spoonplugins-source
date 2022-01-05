package net.runelite.client.plugins.spoonkilltimers;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NullItemID;
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
import java.util.ArrayList;
import java.util.Arrays;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Kill Timers",
        description = "Timers for different bosses",
        tags = {"boss", "spoon", "timer"}
        )
public class SpoonKillTimersPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private SpoonKillTimersConfig config;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SpoonKillTimersOverlay overlay;

    public ArrayList<String> bossNames = new ArrayList<>(Arrays.asList("hespori", "zulrah", "vorkath", "alchemical hydra", "fragment of seren", "glough", "galvek", "dusk"));

    public KillTimerInfoBox box = null;

    public KillTimer timer = null;

    public int despawnDelay = 0;
	
    public SpoonKillTimersPlugin() {
       
    }

    @Provides
    SpoonKillTimersConfig getConfig(ConfigManager configManager) {
        return (SpoonKillTimersConfig) configManager.getConfig(SpoonKillTimersConfig.class);
    }

    protected void startUp() throws Exception {
		reset();
		this.overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        reset();
        this.overlayManager.remove(overlay);
    }

    protected void reset() {
        infoBoxManager.removeInfoBox(box);
        box = null;
        timer = null;
        despawnDelay = 0;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if(event.getKey().equals("timerMode") && box != null) {
            if(config.timerMode() == SpoonKillTimersConfig.timerMode.PANEL) {
                infoBoxManager.removeInfoBox(box);
            }else {
                infoBoxManager.addInfoBox(box);
            }
        }
    }

	@Subscribe
    private void onGameTick(GameTick event) {
		if(timer != null) {
            timer.ticks++;
		}

		if(despawnDelay > 0){
		    despawnDelay--;
		    if(despawnDelay == 0){
		        boolean keepBox = false;
                for(NPC npc : this.client.getNpcs()){
                    if(npc.getName() != null && (npc.getName().equalsIgnoreCase("fragment of seren") || npc.getName().equalsIgnoreCase("glough")
                        || npc.getName().equalsIgnoreCase("galvek"))){
                        keepBox = true;
                        break;
                    }
                }

                if(!keepBox){
                    reset();
                }
            }
        }
    }

	@Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if(event.getNpc().getName() != null && bossNames.contains(event.getNpc().getName().toLowerCase())) {
            if(box != null && !event.getNpc().getName().equalsIgnoreCase("fragment of seren") && !event.getNpc().getName().equalsIgnoreCase("glough")
                && !event.getNpc().getName().equalsIgnoreCase("galvek")){
                reset();
            }

            if (event.getNpc().getName().equalsIgnoreCase("hespori")) {
                timer = new KillTimer("Hespori", 3);
                this.box = new KillTimerInfoBox(itemManager.getImage(NullItemID.NULL_23044), this.client, this);
                if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                    this.infoBoxManager.addInfoBox(this.box);
                }
            } else if (event.getNpc().getName().equalsIgnoreCase("zulrah")) {
                timer = new KillTimer("Zulrah", -5);
                this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.PET_SNAKELING_12940), this.client, this);
                if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                    this.infoBoxManager.addInfoBox(this.box);
                }
            } else if (event.getNpc().getName().equalsIgnoreCase("alchemical hydra") && this.client.isInInstancedRegion()) {
                timer = new KillTimer("Alchemical Hydra", 0);
                this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.ALCHEMICAL_HYDRA_HEAD), this.client, this);
                if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                    this.infoBoxManager.addInfoBox(this.box);
                }
            } else if (event.getNpc().getName().equalsIgnoreCase("dusk")) {
                timer = new KillTimer("Grotesque Guardians", 0);
                this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.NOON), this.client, this);
                if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                    this.infoBoxManager.addInfoBox(this.box);
                }
            }else if (event.getNpc().getName().equalsIgnoreCase("fragment of seren")) {
                if(box == null) {
                    timer = new KillTimer("Seren", 0);
                    this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.SEREN_HALO), this.client, this);
                    if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                        this.infoBoxManager.addInfoBox(this.box);
                    }
                }
            } else if (event.getNpc().getName().equalsIgnoreCase("glough")) {
                if(box == null) {
                    timer = new KillTimer("Glough", -2);
                    this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.GNOME_CHILD_MASK), this.client, this);
                    if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                        this.infoBoxManager.addInfoBox(this.box);
                    }
                }
            } else if (event.getNpc().getName().equalsIgnoreCase("galvek")) {
                if(box == null) {
                    timer = new KillTimer("Galvek", 0);
                    this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.DRACONIC_VISAGE), this.client, this);
                    if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                        this.infoBoxManager.addInfoBox(this.box);
                    }
                }
            }
        }
    }

    @Subscribe
    private void onNpcChanged(NpcChanged event) {
        if(event.getNpc().getName() != null && bossNames.contains(event.getNpc().getName().toLowerCase())) {
            if (event.getNpc().getName().equalsIgnoreCase("vorkath") && event.getNpc().getId() == 8061) {
                if (box != null) {
                    reset();
                }
                timer = new KillTimer("Vorkath", 0);
                this.box = new KillTimerInfoBox(itemManager.getImage(ItemID.VORKI), this.client, this);
                if(this.config.timerMode() == SpoonKillTimersConfig.timerMode.INFOBOX){
                    this.infoBoxManager.addInfoBox(this.box);
                }
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        if(event.getNpc().getName() != null && bossNames.contains(event.getNpc().getName().toLowerCase())) {
            if(event.getNpc().getName().equalsIgnoreCase("fragment of seren") || event.getNpc().getName().equalsIgnoreCase("glough")
                || event.getNpc().getName().equalsIgnoreCase("galvek")){
                despawnDelay = 4;
            }else {
                reset();
            }
        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event) {
        if(event.getActor().getName() != null && event.getActor() instanceof NPC && bossNames.contains(event.getActor().getName().toLowerCase())) {
            if(!event.getActor().getName().equalsIgnoreCase("alchemical hydra") && !event.getActor().getName().equalsIgnoreCase("dusk")) {
                reset();
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if(event.getActor().getName() != null && event.getActor() instanceof NPC && event.getActor().getName().equalsIgnoreCase("dusk")) {
            if(event.getActor().getAnimation() == 7803) {
                reset();
            }
        }
    }

    String tommss(int ticks) {
        if (ticks % 5 == 1 || ticks % 5 == 3)
            ticks++;
        int m = ticks / 100;
        int s = (ticks - m * 100) * 60 / 100;
        return String.valueOf(m) + ((s < 10) ? ":0" : ":") + s;
    }
}

