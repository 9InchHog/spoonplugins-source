package net.runelite.client.plugins.gwdessencehider;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Gwd Essence",
        description = "Removes the new essence counter and replaces it with a better one",
        tags = {"combat", "spoon", "pve", "pvm", "bosses", "gwd"}
)
public class GwdEssenceHiderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GwdEssenceHiderOverlay overlay;

    public boolean gwdWidget;
    public int armaKc = 0;
    public int bandosKc = 0;
    public int saraKc = 0;
    public int zammyKc = 0;
    public int nexKc = 0;

    @Provides
    GwdEssenceHiderConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(GwdEssenceHiderConfig.class);
    }

    protected void startUp() throws Exception {
        gwdWidget = this.client.getWidget(26607621) != null;
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() throws Exception {
        gwdWidget = false;
        this.overlayManager.remove(this.overlay);
        if(this.client.getWidget(26607621) != null){
            Objects.requireNonNull(this.client.getWidget(26607621)).setHidden(false);
        }
    }

    public void reset() {
        armaKc = 0;
        bandosKc = 0;
        saraKc = 0;
        zammyKc = 0;
        nexKc = 0;
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == 406 && !gwdWidget) {
            gwdWidget = this.client.getWidget(26607621) != null;
            setKc();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (gwdWidget) {
            Widget widget = this.client.getWidget(26607621);
            if(widget != null){
                if (!widget.isHidden()) {
                    widget.setHidden(true);
                    setKc();
                }
            } else {
                gwdWidget = false;
                reset();
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event){
        if(gwdWidget){
            setKc();
        }
    }

    public void setKc(){
        armaKc = this.client.getVarbitValue(3973);
        bandosKc = this.client.getVarbitValue(3975);
        saraKc = this.client.getVarbitValue(3972);
        zammyKc = this.client.getVarbitValue(3976);
        //nexKc = this.client.getVarbitValue(3977);
    }
}
