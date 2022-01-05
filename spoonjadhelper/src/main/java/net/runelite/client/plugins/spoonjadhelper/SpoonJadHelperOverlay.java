package net.runelite.client.plugins.spoonjadhelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SpoonJadHelperOverlay extends OverlayPanel {
    private final Client client;

    private final SpoonJadHelperPlugin plugin;

    private final SpoonJadHelperConfig config;

    @Inject
    private SpoonJadHelperOverlay(Client client, SpoonJadHelperPlugin plugin, SpoonJadHelperConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if(config.jadChallengeSpawn() && plugin.challengeNum > 0 && plugin.spawnPoints.size() > 0 && this.client.isInInstancedRegion()){
            for(LocalPoint lp : plugin.spawnPoints){
                Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 5);
                renderPolygon(graphics, poly, config.jadChallengeSpawnColor(), config.jadChallengeSpawnOpacity());
            }
        }

        for(JadInfo jadInfo : plugin.jads){
			String textOverlay = Integer.toString(jadInfo.ticks);
            BufferedImage icon = null;
            if(jadInfo.color == Color.RED){
			    textOverlay = "BONK";
                icon = ImageUtil.loadImageResource(SpoonJadHelperPlugin.class, "bonk.png");
            }
			Point textLoc = jadInfo.jad.getCanvasTextLocation(graphics, textOverlay, 50);
			if(textLoc != null) {
				Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
				Font oldFont = graphics.getFont();
				graphics.setFont(new Font("Arial", Font.BOLD, this.config.sixJadSize()));
				OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
				OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, jadInfo.color);
				graphics.setFont(oldFont);

				if(icon != null && config.bonk()){
                    Point point = new Point(textLoc.getX() - icon.getWidth() / 2, textLoc.getY() - 10);
                    OverlayUtil.renderImageLocation(graphics, point, icon);
                }
			}
		}
        return super.render(graphics);
    }

    private static void renderPolygon(Graphics2D graphics, Shape polygon, Color color, int opacity){
        if (polygon != null){
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity));
            graphics.fill(polygon);
        }
    }
}
