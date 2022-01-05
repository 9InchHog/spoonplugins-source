package net.runelite.client.plugins.soulwars;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;

public class AvatarOverlay extends OverlayPanel {
    private final SoulWarsPlugin plugin;
    private final SoulWarsConfig config;
    private final Client client;

    @Inject
    public AvatarOverlay(SoulWarsPlugin plugin, SoulWarsConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();

        if(config.showAvatarDamage() && plugin.avatarDamage > 0 && plugin.isInSW() && this.client.getLocalPlayer() != null && this.client.getLocalPlayer().getTeam() > 0) {
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Avatar DMG:")
                    .right(String.valueOf(plugin.avatarDamage))
                    .build());
        }
        return super.render(graphics);
    }
}
