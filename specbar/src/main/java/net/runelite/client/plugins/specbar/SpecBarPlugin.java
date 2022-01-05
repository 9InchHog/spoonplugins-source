package net.runelite.client.plugins.specbar;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Spec Bar",
        description = "Shows the spec bar on weapons that do not have one",
        tags = {"special", "spec-bar", "special attack"},
        enabledByDefault = true
)
public class SpecBarPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    public SpecBarConfig config;

    @Provides
    SpecBarConfig getConfig(ConfigManager configManager) {
        return (SpecBarConfig)configManager.getConfig(SpecBarConfig.class);
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        int specBarWidgetId = this.config.specbarid();
        Widget specbarWidget = this.client.getWidget(593, specBarWidgetId);
        if (specbarWidget != null)
            specbarWidget.setHidden(false);
    }
}