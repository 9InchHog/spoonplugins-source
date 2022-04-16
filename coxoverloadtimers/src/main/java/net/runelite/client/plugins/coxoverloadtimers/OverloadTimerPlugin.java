package net.runelite.client.plugins.coxoverloadtimers;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;

@Extension
@PluginDescriptor(
        name = "[S] CoX Overload Timers",
        description = "Custom CoX Overload Timer with extra features",
        tags = {"combat", "items", "potions", "cox", "overload", "timer/timers", "big mfkn tyler"},
        enabledByDefault = false
)
public class OverloadTimerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject
    private ItemManager itemManager;
    @Inject
    private OverloadTimerConfig config;
    private int lastRaidVarbit = -1;

    public OverloadTimerPlugin() {
    }

    @Provides
    OverloadTimerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OverloadTimerConfig.class);
    }

    protected void startUp() {
    }

    protected void shutDown() {
        this.removeOverloadTimer();
        this.lastRaidVarbit = -1;
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged e) {
        int raidVarbit = this.getRaidVarbitValue();
        if (this.lastRaidVarbit != raidVarbit) {
            this.removeOverloadTimer();
            this.lastRaidVarbit = raidVarbit;
        }

    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        if (e.getType() == ChatMessageType.SPAM || e.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = Text.removeTags(e.getMessage()).toLowerCase();
            if (message.startsWith("you drink some of your") && message.contains("overload") && this.getRaidVarbitValue() == 1) {
                this.createOverloadTimer();
            }

        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath e) {
        if (e.getActor() == this.client.getLocalPlayer()) {
            this.removeOverloadTimer();
        }

    }

    private void createOverloadTimer() {
        this.removeOverloadTimer();
        OverloadTimer overloadTimer = new OverloadTimer(this, this.config, this.itemManager.getImage(20996), Duration.ofMinutes(5L));
        this.infoBoxManager.addInfoBox(overloadTimer);
    }

    private void removeOverloadTimer() {
        this.infoBoxManager.removeIf((box) -> {
            return box instanceof OverloadTimer;
        });
    }

    private int getRaidVarbitValue() {
        return this.client.getVarbitValue(Varbits.IN_RAID);
    }
}
