package net.runelite.client.plugins.specinfobox;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "[S] Spec InfoBox",
        description = "Displays an InfoBox when you can spec based on a threshold",
        tags = {"special", "attack", "info", "box", "big mfkn tyler"},
        enabledByDefault = false
)
public class SpecIBPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private SpecIBConfig config;

    private static final int SPEC_VARP_ID = VarPlayer.SPECIAL_ATTACK_PERCENT.getId();

    @Provides
    SpecIBConfig provideConfig(ConfigManager configManager) {
        return (SpecIBConfig)configManager.getConfig(SpecIBConfig.class);
    }

    protected void startUp() {
        if (this.client.getGameState() == GameState.LOGGED_IN)
            postFakeVarbitChangedEvent();
    }

    protected void shutDown() {
        Objects.requireNonNull(SpecialAttackInfoBox.class);
        this.infoBoxManager.removeIf(SpecialAttackInfoBox.class::isInstance);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged e) {
        if (!e.getGroup().equals("specinfobox"))
            return;
        if (e.getKey().equals("specThreshold"))
            postFakeVarbitChangedEvent();
    }

    private void postFakeVarbitChangedEvent() {
        this.clientThread.invokeLater(() -> {
            VarbitChanged varbitChanged = new VarbitChanged();
            varbitChanged.setIndex(SPEC_VARP_ID);
            this.eventBus.post(varbitChanged);
        });
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged e) {
        int idx = e.getIndex();
        if (idx != SPEC_VARP_ID)
            return;
        int varp = this.client.getVarps()[idx];
        int threshold = this.config.specThreshold() * 10;
        Objects.requireNonNull(SpecialAttackInfoBox.class);
        this.infoBoxManager.removeIf(SpecialAttackInfoBox.class::isInstance);
        if (varp >= threshold) {
            BufferedImage specTransferImage = this.spriteManager.getSprite(558, 0);
            if (specTransferImage == null)
                return;
            this.infoBoxManager.addInfoBox(new SpecialAttackInfoBox(this, specTransferImage));
        }
    }
}
