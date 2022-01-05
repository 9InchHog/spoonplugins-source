package net.runelite.client.plugins.spoontempoross;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class SpoonTemporossOverlay extends Overlay {
    private final Client client;

    private final SpoonTemporossPlugin plugin;

    private final SpoonTemporossConfig config;

    @Inject
    private SpoonTemporossOverlay(Client client, SpoonTemporossPlugin plugin, SpoonTemporossConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (this.config.highlightFish()){
            for(NPC npc : this.client.getNpcs()){
                if(npc.getId() == 10565 || npc.getId() == 10568){
                    Polygon tilepoly = npc.getCanvasTilePoly();
                    if (tilepoly == null)
                        continue;
                    graphics.setStroke(new BasicStroke(config.tileThiCC()));
                    graphics.setColor(new Color(config.fishColor().getRed(), config.fishColor().getGreen(), config.fishColor().getBlue(), 255));
                    graphics.drawPolygon(tilepoly);
                    graphics.setColor(new Color(config.fishColor().getRed(), config.fishColor().getGreen(), config.fishColor().getBlue(), 0));
                    graphics.fillPolygon(tilepoly);
                }
            }
        }

        if (this.config.jumpingFish()){
            for(NPC npc : this.client.getNpcs()){
                if(npc.getId() == 10569){
                    Polygon tilepoly = npc.getCanvasTilePoly();
                    if (tilepoly == null)
                        continue;
                    graphics.setStroke(new BasicStroke(config.tileThiCC()));
                    graphics.setColor(new Color(config.jumpingFishColor().getRed(), config.jumpingFishColor().getGreen(), config.jumpingFishColor().getBlue(), 255));
                    graphics.drawPolygon(tilepoly);
                    graphics.setColor(new Color(config.jumpingFishColor().getRed(), config.jumpingFishColor().getGreen(), config.jumpingFishColor().getBlue(), 0));
                    graphics.fillPolygon(tilepoly);
                }
            }
        }

        if(config.highlightTotem() && plugin.waveComing){
            for(GameObject obj : plugin.tetherList) {
                Shape clickbox = obj.getClickbox();
                if (clickbox != null) {
                    Color fillColor = new Color(config.totemColor().getRed(), config.totemColor().getGreen(), config.totemColor().getBlue(), 50);
                    OverlayUtil.renderHoverableArea(graphics, clickbox, client.getMouseCanvasPosition(), fillColor, config.totemColor(), config.totemColor().darker());
                }
            }
        }

        if(config.highlightPool()){
            for(NPC npc : this.client.getNpcs()){
                if(npc.getId() == 10571) {
                    NPCComposition comp = npc.getComposition();
                    Polygon tilepoly = Perspective.getCanvasTileAreaPoly(client, npc.getLocalLocation(), comp.getSize());
                    if (tilepoly == null)
                        continue;
                    graphics.setStroke(new BasicStroke(config.tileThiCC()));
                    graphics.setColor(new Color(config.poolColor().getRed(), config.poolColor().getGreen(), config.poolColor().getBlue(), 255));
                    graphics.drawPolygon(tilepoly);
                    graphics.setColor(new Color(config.poolColor().getRed(), config.poolColor().getGreen(), config.poolColor().getBlue(), 0));
                    graphics.fillPolygon(tilepoly);
                }
            }
        }

        if(config.highlightShrine() && plugin.hasRawFish){
            for(GameObject obj : plugin.shrineList) {
                Shape clickbox = obj.getClickbox();
                if (clickbox != null) {
                    Color fillColor = new Color(config.shrineColor().getRed(), config.shrineColor().getGreen(), config.shrineColor().getBlue(), 50);
                    OverlayUtil.renderHoverableArea(graphics, clickbox, client.getMouseCanvasPosition(), fillColor, config.shrineColor(), config.shrineColor().darker());
                }
            }
        }

        if (this.config.highlightAmmoCrate() && (plugin.hasCookedFish || plugin.hasRawFish)){
            for(NPC npc : this.client.getNpcs()){
                if(npc.getName() != null && npc.getName().toLowerCase().contains("ammunition crate")){
                    Shape poly = npc.getConvexHull();
                    graphics.setStroke(new BasicStroke(config.tileThiCC()));
                    graphics.setColor(new Color(config.ammoCrateColor().getRed(), config.ammoCrateColor().getGreen(), config.ammoCrateColor().getBlue(), 255));
                    graphics.draw(poly);
                    graphics.setColor(new Color(config.ammoCrateColor().getRed(), config.ammoCrateColor().getGreen(), config.ammoCrateColor().getBlue(), 0));
                    graphics.fill(poly);
                }
            }
        }

        if(config.highlightFire()){
            for(GameObject obj : plugin.fireList) {
                Shape clickbox = obj.getClickbox();
                if (clickbox != null) {
                    Color fillColor = new Color(config.fireColor().getRed(), config.fireColor().getGreen(), config.fireColor().getBlue(), 50);
                    OverlayUtil.renderHoverableArea(graphics, clickbox, client.getMouseCanvasPosition(), fillColor, config.fireColor(), config.fireColor().darker());
                }
            }
        }

        if(config.highlightCloud()){
            for(GameObject obj : plugin.cloudList) {
                Shape clickbox = obj.getClickbox();
                if (clickbox != null) {
                    Color fillColor = new Color(config.cloudColor().getRed(), config.cloudColor().getGreen(), config.cloudColor().getBlue(), 0);
                    OverlayUtil.renderHoverableArea(graphics, clickbox, client.getMouseCanvasPosition(), fillColor, config.cloudColor(), config.cloudColor().darker());
                }
            }
        }

        if(config.highlightRepair()){
            for(GameObject obj : plugin.repairList) {
                Shape clickbox = obj.getClickbox();
                if (clickbox != null) {
                    Color fillColor = new Color(config.repairColor().getRed(), config.repairColor().getGreen(), config.repairColor().getBlue(), 50);
                    OverlayUtil.renderHoverableArea(graphics, clickbox, client.getMouseCanvasPosition(), fillColor, config.repairColor(), config.repairColor().darker());
                }
            }
        }

        if(config.vulnTicks() && plugin.temporossVulnerable){
            for(NPC npc : this.client.getNpcs()){
                if(npc.getId() == 10570) {
                    String textOverlay = Integer.toString(plugin.vulnTicks);
                    Point textLoc = npc.getCanvasTextLocation(graphics, textOverlay, 50);
                    Font oldFont = graphics.getFont();
                    graphics.setFont(new Font("Arial", 1, 15));
                    Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                    OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
                    OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, config.vulnColor());
                    graphics.setFont(oldFont);
                }
            }
        }

        if(config.fireTicks() && plugin.cloudList.size() > 0){
            for(GameObject obj : plugin.cloudList){
                String textOverlay = Integer.toString(plugin.cloudTicks);
                Point textLoc = obj.getCanvasTextLocation(graphics, textOverlay, 50);
                Font oldFont = graphics.getFont();
                graphics.setFont(new Font("Arial", 1, 15));
                Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
                OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
                OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, config.fireTicksColor());
                graphics.setFont(oldFont);
            }
        }
        return null;
    }
}
