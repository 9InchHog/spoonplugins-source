package net.runelite.client.plugins.coxadditions.overlays;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class OlmHpPanelOverlay extends OverlayPanel {
	private final Client client;

	private final CoxAdditionsPlugin plugin;

	private final CoxAdditionsConfig config;

	@Inject
	private OlmHpPanelOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.olmHandsHealth() == CoxAdditionsConfig.olmHandsHealthMode.INFOBOX && (plugin.mageHand != null || plugin.meleeHand != null)) {
			this.panelComponent.getChildren().clear();
			NPC mageHand = plugin.mageHand;
			NPC meleeHand = plugin.meleeHand;
			this.panelComponent.getChildren().add(TitleComponent.builder()
					.color(Color.WHITE)
					.text("Olm Hands HP")
					.build());

			if (this.client.getVarbitValue(5424) == 1) {
				if (mageHand != null && plugin.mageHandHp >= 0) {
					String mageText = String.valueOf(plugin.mageHandHp);
					Color mageColor = Color.WHITE;
					if (plugin.mageHandHp < 100) {
						mageColor = Color.RED;
					}
					this.panelComponent.getChildren().add(LineComponent.builder()
							.leftColor(Color.CYAN)
							.left("Mage Hand:")
							.rightColor(mageColor)
							.right(mageText)
							.build());
				}
				if (meleeHand != null && plugin.meleeHandHp >= 0) {
					String meleeText = String.valueOf(plugin.meleeHandHp);
					Color meleeColor = Color.WHITE;
					if (plugin.meleeHandHp < 100) {
						meleeColor = Color.RED;
					}
					this.panelComponent.getChildren().add(LineComponent.builder()
							.leftColor(Color.RED)
							.left("Melee Hand:")
							.rightColor(meleeColor)
							.right(meleeText)
							.build());
				}
			} else {
				if (mageHand != null) {
					Color mageColor = Color.WHITE;
					String mageText = "";
					if (mageHand.getHealthRatio() > 0 || (plugin.mageHandLastRatio != 0 && plugin.mageHandLastHealthScale != 0)) {
						if (mageHand.getHealthRatio() > 0) {
							plugin.mageHandLastRatio = mageHand.getHealthRatio();
							plugin.mageHandLastHealthScale = mageHand.getHealthScale();
						}

						float floatRatioMage = ((float) plugin.mageHandLastRatio / (float) plugin.mageHandLastHealthScale * 100);
						if (floatRatioMage <= 15) {
							mageColor = Color.RED;
						}
						mageText = Float.toString(floatRatioMage);
						mageText = mageText.substring(0, mageText.indexOf("."));
					}
					this.panelComponent.getChildren().add(LineComponent.builder()
							.leftColor(Color.CYAN)
							.left("Mage Hand:")
							.rightColor(mageColor)
							.right(mageText + "%")
							.build());
				}
				if (meleeHand != null) {
					Color meleeColor = Color.WHITE;
					String meleeText = "";
					if (meleeHand.getHealthRatio() > 0 || (plugin.meleeHandLastRatio != 0 && plugin.meleeHandLastHealthScale != 0)) {
						if (plugin.meleeHand.getHealthRatio() > 0) {
							plugin.meleeHandLastRatio = meleeHand.getHealthRatio();
							plugin.meleeHandLastHealthScale = meleeHand.getHealthScale();
						}

						float floatRatioMelee = ((float) plugin.meleeHandLastRatio / (float) plugin.meleeHandLastHealthScale * 100);
						if (floatRatioMelee <= 15) {
							meleeColor = Color.RED;
						}
						meleeText = Float.toString(floatRatioMelee);
						meleeText = meleeText.substring(0, meleeText.indexOf("."));
					}
					this.panelComponent.getChildren().add(LineComponent.builder()
							.leftColor(Color.RED)
							.left("Melee Hand:")
							.rightColor(meleeColor)
							.right(meleeText + "%")
							.build());
				}
			}
		}
		return super.render(graphics);
	}
}

