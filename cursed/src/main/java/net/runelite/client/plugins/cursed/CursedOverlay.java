package net.runelite.client.plugins.cursed;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CursedOverlay extends OverlayPanel {
	private final Client client;
	private final CursedPlugin plugin;
	private final CursedConfig config;

	@Inject
	private CursedOverlay(final Client client, final CursedPlugin plugin, final CursedConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if(this.client.getLocalPlayer() != null && config.catJam() && plugin.playCatJam) {
			Point base = Perspective.localToCanvas(this.client, this.client.getLocalPlayer().getLocalLocation(), this.client.getPlane(), this.client.getLocalPlayer().getLogicalHeight() / 2 - 10);
			if (base != null) {
				String pngName;
				if(plugin.catJamFrame > 99) {
					pngName = "frame_" + plugin.catJamFrame + "_delay-0.03s.png";
				} else if (plugin.catJamFrame > 10) {
					pngName = "frame_0" + plugin.catJamFrame + "_delay-0.03s.png";
				} else {
					pngName = "frame_00" + plugin.catJamFrame + "_delay-0.03s.png";
				}

				BufferedImage icon = ImageUtil.loadImageResource(CursedPlugin.class, pngName);
				if (icon != null) {
					Composite oldComposite = graphics.getComposite();
					graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, plugin.catJamOpacity));
					graphics.drawImage(icon, 0, 0, this.client.getCanvasWidth(), this.client.getCanvasHeight(), null);
					graphics.setComposite(oldComposite);
				}
			}
		}

		if(config.npcEpilepsy()){
			for(NPC npc : this.client.getNpcs()){
				NPCComposition npcComposition = npc.getTransformedComposition();
				int size = 0;
				if(npcComposition != null) {
					size = npcComposition.getSize();
				}
				LocalPoint lp = npc.getLocalLocation();
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);
				ArrayList<Color> colorList = new ArrayList<>(Arrays.asList(Color.blue, Color.red, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.orange));
				Random rand = new Random();
				int rng = rand.nextInt(6);
				Color color = colorList.get(rng);
				renderPoly(graphics, color, tilePoly);
			}
		}

		if(config.raveProjectiles()){
			int index = 0;
			for (Projectile p : client.getProjectiles()) {
				int x = (int) p.getX();
				int y = (int) p.getY();
				LocalPoint lp = new LocalPoint(x, y);
				Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
				if (poly != null) {
					try {
						renderPoly(graphics, plugin.raveProjectiles.get(index), poly);
					} catch (Exception ex) {
						renderPoly(graphics, Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F), poly);
					}
				}
				index++;
			}
		}

		if(config.pulsingPlayers()){
			for(Player player : this.client.getPlayers()){
				Shape box = player.getConvexHull();
				if (box != null) {
					Color color = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), plugin.pulseOpacity);
					graphics.setColor(color);
					graphics.fill(box);
				}
			}
		}

		if(config.psychedelicNpcs()){
			for(NPC npc : this.client.getNpcs()){
				Shape box = npc.getConvexHull();
				if (box != null) {
					Color color = new Color(plugin.psychedelicRed, plugin.psychedelicGreen, plugin.psychedelicBlue, 150);
					graphics.setColor(color);
					graphics.fill(box);
				}
			}
		}

		if (client.getLocalPlayer() != null && plugin.clippyTicks > 0) {
			BufferedImage icon = ImageUtil.loadImageResource(CursedPlugin.class, "Clippy.png");
			if (icon != null) {
				graphics.drawImage(icon, (client.getCanvasWidth() - 210) / 2, (client.getCanvasHeight() - 250) / 2, 210, 250, null);
			}
		}

		if (client.getLocalPlayer() != null && plugin.gtaTicks > 0) {
			BufferedImage icon = ImageUtil.loadImageResource(CursedPlugin.class, "MissionPassed.png");
			if (icon != null) {
				Composite oldComposite = graphics.getComposite();
				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, plugin.gtaOpacity));
				graphics.drawImage(icon, (client.getCanvasWidth() - 300) / 2, ((client.getCanvasHeight() - 100) / 2) - 100, 300, 100, null);
				graphics.setComposite(oldComposite);
			}
		}

		if (config.immersiveHp() && client.getBoostedSkillLevel(Skill.HITPOINTS) < client.getRealSkillLevel(Skill.HITPOINTS)) {
			float hpPercent = 1 - (float)client.getBoostedSkillLevel(Skill.HITPOINTS) / (float) client.getRealSkillLevel(Skill.HITPOINTS);
			if (hpPercent > .5) {
				Point base = Perspective.localToCanvas(this.client, this.client.getLocalPlayer().getLocalLocation(), this.client.getPlane(), this.client.getLocalPlayer().getLogicalHeight() / 2 - 10);
				if (base != null) {
					BufferedImage icon = ImageUtil.loadImageResource(CursedPlugin.class, "codScreenBlood.png");
					if (icon != null) {
						Composite oldComposite = graphics.getComposite();
						graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hpPercent / 2));
						graphics.drawImage(icon, 0, 0, this.client.getCanvasWidth(), this.client.getCanvasHeight(), null);
						graphics.setComposite(oldComposite);
					}
				}
			}
		}
		return super.render(graphics);
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon){
		if (polygon != null){
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			graphics.fill(polygon);
		}
	}
}
