package net.runelite.client.plugins.socket.plugins.socketba;

import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.*;

import java.awt.*;
import java.util.Locale;

public class SocketBAOverlay extends OverlayPanel {
	private SocketBAPlugin plugin;

	private SocketBAConfig config;

	private Client client;

	@Inject
	public SocketBAOverlay(SocketBAPlugin plugin, SocketBAConfig config, Client client) {
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGHEST);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	public Dimension render(Graphics2D graphics) {
		if(this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			if (config.highlightRoleNpcs()) {
				for(NPC npc : this.client.getNpcs()) {
					NPCComposition npcComposition = npc.getComposition();
					if(npc.getName() != null && npc.getName().toLowerCase().contains("penance") && npcComposition != null) {
						String name = npc.getName().toLowerCase();
						if((plugin.role.equals("Attacker") && (name.contains("fighter") || name.contains("ranger")))
								|| (plugin.role.equals("Defender") && name.contains("runner"))
								|| (plugin.role.equals("Healer") && name.contains("healer"))) {
							int size = npcComposition.getSize();
							LocalPoint lp = npc.getLocalLocation();
							if (lp != null) {
								Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
								if (tilePoly != null) {
									Color outlineColor = new Color(config.highlightRoleNpcsColor().getRed(), config.highlightRoleNpcsColor().getGreen(), config.highlightRoleNpcsColor().getBlue(), 255);
									Color fillColor = config.highlightRoleNpcsColor();
									this.renderPoly(graphics, outlineColor, fillColor, tilePoly, 1);
								}
							}
						}
					}
				}
			}

			//this.client.getWidget(38862851) == Combat level widget
			if (config.meleeStyleHighlight() && plugin.equippedWeaponTypeVarbit != -1 && plugin.role.equals("Attacker")){
				AttackStyle[] styleList = WeaponType.getWeaponType(plugin.equippedWeaponTypeVarbit).getAttackStyles();
				if (styleList != null) {
					int index = 1;
					for (AttackStyle aStyle : styleList) {
						if (aStyle != null) {
							if (plugin.attCall.contains(aStyle.getName())) {
								Widget widget;
								if (index == 1) {
									widget = this.client.getWidget(38862852);
								} else if (index == 2) {
									widget = this.client.getWidget(38862856);
								} else if (index == 3) {
									widget = this.client.getWidget(38862860);
								} else {
									widget = this.client.getWidget(38862864);
								}

								if (widget != null && !widget.isHidden()) {
									drawBox(graphics, widget.getCanvasLocation().getX(), widget.getCanvasLocation().getY(), widget.getBounds().height, widget.getBounds().width);
								}
							}
						}
						index++;
					}
				}
			}

			if (config.highlightEggs() && plugin.role.equals("Collector") && plugin.eggMap.size() > 0) {
				plugin.eggMap.forEach((wp, id) -> {
					LocalPoint point = LocalPoint.fromWorld(this.client, wp);
					if(point != null) {
						Polygon poly = Perspective.getCanvasTilePoly(this.client, point);
						Color color = Color.WHITE;
						if(id == 10534){
							color = Color.YELLOW;
						}else if(plugin.colCall.equals("Green egg") && id == 10531){
							color = Color.GREEN;
						}else if(plugin.colCall.equals("Red egg") && id == 10532){
							color = Color.RED;
						}else if(plugin.colCall.equals("Blue egg") && id == 10533){
							color = Color.BLUE;
						}

						if(color != Color.WHITE) {
							graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
							graphics.drawPolygon(poly);
						}
					}
				});
			}

			if (config.highlightVendingMachine()) {
				for(GameObject obj : plugin.vendingMachines) {
					if(obj.getConvexHull() != null && ((plugin.role.equals("Attacker") && obj.getId() == 20241) || (plugin.role.equals("Defender") && obj.getId() == 20242)
							|| (plugin.role.equals("Healer") && obj.getId() == 20243))) {
						graphics.setColor(config.vendingMachineColor());
						graphics.draw(obj.getConvexHull());
						graphics.setColor(new Color(config.vendingMachineColor().getRed(), config.vendingMachineColor().getGreen(), config.vendingMachineColor().getBlue(), config.vendingMachineOpacity()));
						graphics.fill(obj.getConvexHull());
					}
				}
			}

			if(config.cannonHelper()) {
				plugin.cannons.forEach((n,c) -> {
					if(n != null && n.getConvexHull() != null) {
						graphics.setColor(c);
						graphics.setStroke(new BasicStroke((float) plugin.cannonWidth));
						graphics.draw(n.getConvexHull());
					}
				});

				plugin.cannons.forEach((o,c) -> {
					if(o != null && o.getConvexHull() != null) {
						graphics.setColor(c);
						graphics.setStroke(new BasicStroke((float) plugin.cannonWidth));
						graphics.draw(o.getConvexHull());
					}
				});
			}

			if(config.discoQueen() && plugin.queen != null) {
				plugin.discoTiles.forEach((o,c) -> {
					if(o != null) {
						Color fillColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
						renderPoly(graphics, c, fillColor, o.getCanvasTilePoly(), 2);
					}
				});
			}
		}
		return super.render(graphics);
	}

	private void drawBox(Graphics2D graphics, int startX, int startY, int height, int width) {
		graphics.setColor(config.meleeStyleHighlightColor());
		graphics.setStroke(new BasicStroke(2));
		graphics.drawLine(startX, startY, startX + width, startY);
		graphics.drawLine(startX + width, startY, startX + width, startY + height);
		graphics.drawLine(startX + width, startY + height, startX, startY + height);
		graphics.drawLine(startX, startY + height, startX, startY);
	}

	private void renderPoly(Graphics2D graphics, Color outlineColor, Color fillColor, Shape polygon, double width) {
		if (polygon != null) {
			graphics.setColor(outlineColor);
			graphics.setStroke(new BasicStroke((float) width));
			graphics.draw(polygon);
			graphics.setColor(fillColor);
			graphics.fill(polygon);
		}
	}
}