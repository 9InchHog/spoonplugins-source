package net.runelite.client.plugins.gwdessencehider;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

class GwdEssenceHiderOverlay extends OverlayPanel {
	private final Client client;
	private final GwdEssenceHiderPlugin plugin;
    private final GwdEssenceHiderConfig config;

	@Inject
	private GwdEssenceHiderOverlay(Client client, GwdEssenceHiderPlugin plugin, GwdEssenceHiderConfig config) {
		this.client = client;
		this.plugin = plugin;
        this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
        if(plugin.gwdWidget){
            panelComponent.getChildren().clear();
            if(plugin.armaKc > 0){
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(config.textColor() ? config.armaColor() : config.defaultColor())
                        .left(config.godMode() == GwdEssenceHiderConfig.GodMode.FULL_NAME ? "Armadyl: " : "Arma: ")
                        .rightColor(config.textColor() ? config.armaColor() : config.defaultColor())
                        .right(String.valueOf(plugin.armaKc))
                        .build());
            }
            if(plugin.bandosKc > 0){
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(config.textColor() ? config.bandosColor() : config.defaultColor())
                        .left("Bandos: ")
                        .rightColor(config.textColor() ? config.bandosColor() : config.defaultColor())
                        .right(String.valueOf(plugin.bandosKc))
                        .build());
            }
            if(plugin.saraKc > 0){
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(config.textColor() ? config.saraColor() : config.defaultColor())
                        .left(config.godMode() == GwdEssenceHiderConfig.GodMode.FULL_NAME ? "Saradomin: " : "Sara: ")
                        .rightColor(config.textColor() ? config.saraColor() : config.defaultColor())
                        .right(String.valueOf(plugin.saraKc))
                        .build());
            }
            if(plugin.zammyKc > 0){
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(config.textColor() ? config.zammyColor() : config.defaultColor())
                        .left(config.godMode() == GwdEssenceHiderConfig.GodMode.FULL_NAME ? "Zamorak: " : "Zammy: ")
                        .rightColor(config.textColor() ? config.zammyColor() : config.defaultColor())
                        .right(String.valueOf(plugin.zammyKc))
                        .build());
            }
            if (plugin.nexKc > 0){
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(config.textColor() ? config.nexColor() : config.defaultColor())
                        .left(config.godMode() == GwdEssenceHiderConfig.GodMode.FULL_NAME ? "Ancient: " : "Nex: ")
                        .rightColor(config.textColor() ? config.nexColor() : config.defaultColor())
                        .right(String.valueOf(plugin.nexKc))
                        .build());
            }
            panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("Saradomin:   ") + 40, 0));
            return super.render(graphics);
        }
		return null;
	}
}
