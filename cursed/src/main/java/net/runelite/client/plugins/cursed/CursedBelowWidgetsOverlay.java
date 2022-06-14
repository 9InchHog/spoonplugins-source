package net.runelite.client.plugins.cursed;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CursedBelowWidgetsOverlay extends OverlayPanel {
	private final Client client;
	private final CursedPlugin plugin;
	private final CursedConfig config;

	@Inject
	private CursedBelowWidgetsOverlay(final Client client, final CursedPlugin plugin, final CursedConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (config.immersiveHp() && client.getBoostedSkillLevel(Skill.HITPOINTS) < client.getRealSkillLevel(Skill.HITPOINTS)) {
			float hpPercent = 1 - (float)client.getBoostedSkillLevel(Skill.HITPOINTS) / (float) client.getRealSkillLevel(Skill.HITPOINTS);
			if (hpPercent > .5) {
				Point base = Perspective.localToCanvas(client, client.getLocalPlayer().getLocalLocation(), client.getPlane(), client.getLocalPlayer().getLogicalHeight() / 2 - 10);
				if (base != null) {
					BufferedImage icon = ImageUtil.loadImageResource(CursedPlugin.class, "codScreenBlood.png");
					if (icon != null) {
						Composite oldComposite = graphics.getComposite();
						graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hpPercent / 2));
						graphics.drawImage(icon, 0, 0, client.getCanvasWidth(), client.getCanvasHeight(), null);
						graphics.setComposite(oldComposite);
					}
				}
			}
		}
		return super.render(graphics);
	}
}
