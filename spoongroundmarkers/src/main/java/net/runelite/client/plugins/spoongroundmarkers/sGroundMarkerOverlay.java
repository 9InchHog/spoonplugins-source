package net.runelite.client.plugins.spoongroundmarkers;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;

@Singleton
public class sGroundMarkerOverlay extends Overlay {
	private final Client client;
	private final sGroundMarkerPlugin plugin;
	private final sGroundMarkerConfig config;

	@Inject
	private sGroundMarkerOverlay(final Client client, final sGroundMarkerPlugin plugin, final sGroundMarkerConfig config) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		List<sGroundMarkerWorldPoint> points = plugin.getPoints();
		for (sGroundMarkerWorldPoint groundMarkerWorldPoint : points) {
			drawTile(graphics, groundMarkerWorldPoint);
		}
		return null;
	}

	private void drawTile(Graphics2D graphics, sGroundMarkerWorldPoint groundMarkerWorldPoint) {
		WorldPoint point = groundMarkerWorldPoint.getWorldPoint();
		if (point.getPlane() == client.getPlane()) {
			LocalPoint lp = LocalPoint.fromWorld(client, point);
			if (lp != null) {
				Polygon poly = Perspective.getCanvasTilePoly(client, lp);
				if (poly != null) {
					Color color = config.markerColor();
					switch (groundMarkerWorldPoint.getGroundMarkerPoint().getGroup()) {
						case 2:
							color = config.markerColor2();
							break;
						case 3:
							color = config.markerColor3();
							break;
						case 4:
							color = config.markerColor4();
							break;
						case 5:
							color = config.markerColor5();
							break;
						case 6:
							color = config.markerColor6();
							break;
						case 7:
							color = config.markerColor7();
							break;
						case 8:
							color = config.markerColor8();
							break;
						case 9:
							color = config.markerColor9();
							break;
						case 10:
							color = config.markerColor10();
							break;
						case 11:
							color = config.markerColor11();
							break;
						case 12:
							color = config.markerColor12();
					}
					renderPolygon(graphics, poly, color);

					if(groundMarkerWorldPoint.getGroundMarkerPoint().getLabel() != null){
						if(!config.labelGroupColor()){
							color = config.labelColor();
						}
						String text = groundMarkerWorldPoint.getGroundMarkerPoint().getLabel();
						Point textLoc = Perspective.getCanvasTextLocation(client, graphics, lp, text, 0);
						Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, text, color);
					}
				}
			}
		}
	}

	public void renderPolygon(Graphics2D graphics, Shape poly, Color color) {
		int cRed = color.getRed();
		int cGreen = color.getGreen();
		int cBlue = color.getBlue();
		Color noOutline = new Color(cRed, cGreen, cBlue, 0);
		Color noOutlineFill = new Color(cRed, cGreen, cBlue, config.opacity());

		if (config.tileThiCC() > 0) {
			graphics.setColor(color);
			final Stroke originalStroke = graphics.getStroke();
			graphics.setStroke(new BasicStroke((float) config.tileThiCC()));
			graphics.draw(poly);
			if (config.tileFill()){
				graphics.setColor(noOutlineFill);
			} else {
				graphics.setColor(new Color(0, 0, 0, config.opacity()));
			}
			graphics.fill(poly);
			graphics.setStroke(originalStroke);
		} else {
			graphics.setColor(noOutline);
			final Stroke originalStroke2 = graphics.getStroke();
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(poly);
			if (config.tileFill()){
				graphics.setColor(noOutlineFill);
			} else {
				graphics.setColor(new Color(0, 0, 0, config.opacity()));
			}
			graphics.fill(poly);
			graphics.setStroke(originalStroke2);
		}
	}
}