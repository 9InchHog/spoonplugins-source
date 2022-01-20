package net.runelite.client.plugins.yuritheatre;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class YuriOverlay extends Overlay {
    public static final int MAIDEN_ID = 8363;

    public static final int MAIDEN_CRAB_ID = 8366;

    public static final int PROJECTILE_SOTETSEG_BOMB = 1604;

    public static final int PROJECTILE_VERZIK_LIGHTNING = 1585;

    public static final int PROJECTILE_VERZIK_PURPLE = 1586;

    public static final int PROJECTILE_VERZIK_BOMB = 1598;

    private final Client client;

    private final YuriPlugin plugin;

    private final YuriConfig config;

    private final ItemManager itemManager;

    @Inject
    public YuriOverlay(Client client, YuriPlugin plugin, YuriConfig config, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        Font oldFont = graphics.getFont();
        graphics.setFont(new Font("Arial", 1, 16));
        if (this.config.showMaidenCrabsDistance())
            drawMaiden(graphics);
        if (this.config.showNylcoasChangeTimer())
            drawNylocas(graphics);
        int[] arr = { 1604, 1585, 1598 };
        if (!this.config.showSotetsegBomb())
            arr[0] = -1;
        if (!this.config.showVerzikLightning())
            arr[1] = -1;
        if (!this.config.showVerzikBomb())
            arr[2] = -1;
        drawProjectiles(graphics, arr);
        if (this.config.queueLightningCycle())
            drawVerzik(graphics);
        if (this.config.showPurpleLanding())
            drawPurpleLanding(graphics);
        if (this.config.showSotetsegBombsLeft())
            drawSotetseg(graphics);
        if (this.config.showNylocasBarrageTiles())
            drawNylocasBarrage(graphics);
        if (this.config.showBloatDest())
            drawBloat(graphics);
        graphics.setFont(oldFont);
        return null;
    }

    public void drawBloat(Graphics2D graphics) {
        NPC bloat = this.plugin.getBloatBoss();
        if (bloat == null)
            return;
        int timer = this.plugin.getNextBloatTicks();
        if (this.config.showBloatAttack() && timer > 0) {
            Player p = this.client.getLocalPlayer();
            if (p != null) {
                LocalPoint localPoint = p.getLocalLocation();
                if (localPoint != null) {
                    String str = Integer.toString(timer);
                    Point loc = Perspective.getCanvasTextLocation(this.client, graphics, localPoint, str, p.getLogicalHeight() / 2);
                    if (loc != null)
                        OverlayUtil.renderTextLocation(graphics, loc, str, Color.WHITE);
                }
            }
        }
        int[] nextLoc = this.plugin.getNextBloatAttack();
        if (nextLoc == null)
            return;
        LocalPoint lp = bloat.getLocalLocation();
        if (lp == null)
            return;
        WorldPoint wp = WorldPoint.fromLocalInstance(this.client, lp);
        if (wp == null)
            return;
        int deltaX = nextLoc[0] - wp.getRegionX();
        int deltaY = nextLoc[1] - wp.getRegionY();
        LocalPoint nextLp = LocalPoint.fromScene(lp.getSceneX() + deltaX, lp.getSceneY() + deltaY);
        if (nextLp == null)
            return;
        Polygon poly = Perspective.getCanvasTileAreaPoly(this.client, nextLp, 5);
        if (poly == null)
            return;
        Color color = Color.WHITE;
        int strokeWidth = 2;
        int outlineAlpha = 255;
        int fillAlpha = 5;
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(poly);
        graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
        graphics.fill(poly);
    }

    public void drawSotetseg(Graphics2D graphics) {
        NPC sotetseg = null;
        for (NPC npc : this.client.getNpcs()) {
            if (npc.getName() != null && npc.getName().equals("Sotetseg")) {
                sotetseg = npc;
                break;
            }
        }
        if (sotetseg == null)
            return;
        if (Math.max(0, this.plugin.getSotetsegAttacksLeft()) == 0) {
            AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(560);
            if (asyncBufferedImage == null)
                return;
            Point loc = sotetseg.getCanvasImageLocation((BufferedImage)asyncBufferedImage, sotetseg.getModelHeight() / 4);
            if (loc != null)
                OverlayUtil.renderImageLocation(graphics, loc, (BufferedImage)asyncBufferedImage);
        } else {
            LocalPoint lp = sotetseg.getLocalLocation();
            if (lp == null)
                return;
            String str = Integer.toString(this.plugin.getSotetsegAttacksLeft());
            Point loc = Perspective.getCanvasTextLocation(this.client, graphics, lp, str, sotetseg.getModelHeight() / 4);
            if (loc != null)
                OverlayUtil.renderTextLocation(graphics, loc, str, Color.ORANGE);
        }
    }

    public void drawVerzik(Graphics2D graphics) {
        NPC verzik = null;
        for (NPC npc : this.client.getNpcs()) {
            if (npc.getId() == 8372) {
                verzik = npc;
                break;
            }
        }
        if (verzik == null) {
            this.plugin.setVerzikAttacksLeft(4);
            this.plugin.getVerzikRangedAttacks().clear();
            return;
        }
        if (this.plugin.getVerzikAttacksLeft() == 0) {
            AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(560);
            if (asyncBufferedImage == null)
                return;
            Point loc = verzik.getCanvasImageLocation((BufferedImage)asyncBufferedImage, verzik.getModelHeight() / 4);
            if (loc != null)
                OverlayUtil.renderImageLocation(graphics, loc, (BufferedImage)asyncBufferedImage);
        } else {
            LocalPoint lp = verzik.getLocalLocation();
            if (lp == null)
                return;
            String str = Integer.toString(this.plugin.getVerzikAttacksLeft());
            Point loc = Perspective.getCanvasTextLocation(this.client, graphics, lp, str, verzik.getModelHeight() / 4);
            if (loc != null)
                OverlayUtil.renderTextLocation(graphics, loc, str, Color.ORANGE);
        }
    }

    public void drawNylocas(Graphics2D graphics) {
        NPC npc = this.plugin.getNylocasBoss();
        if (npc == null)
            return;
        LocalPoint lp = npc.getLocalLocation();
        if (lp == null)
            return;
        String str = Integer.toString(this.plugin.getNylocasDelay());
        Point loc = Perspective.getCanvasTextLocation(this.client, graphics, lp, str, 0);
        if (loc != null)
            OverlayUtil.renderTextLocation(graphics, loc, str, Color.ORANGE);
    }

    public void drawNylocasBarrage(Graphics2D graphics) {
        boolean foundPillar = false;
        for (NPC npc : this.client.getNpcs()) {
            if (npc.getId() == 8358) {
                foundPillar = true;
                break;
            }
        }
        if (!foundPillar)
            return;
        NPC toHighlight = null;
        int totalCount = 1;
        for (NPC npc : this.client.getNpcs()) {
            String name = npc.getName();
            if (name == null)
                continue;
            if (!name.equals("Nylocas Hagios") && !name.equals("Nylocas Toxobolos") && !name.equals("Nylocas Ischyros"))
                continue;
            WorldPoint wp = npc.getWorldLocation();
            int count = 0;
            for (NPC target : this.client.getNpcs()) {
                String tName = target.getName();
                if (tName == null || !tName.equals("Nylocas Hagios"))
                    continue;
                WorldPoint tWp = target.getWorldLocation();
                int x_dist = Math.abs(tWp.getX() - wp.getX());
                int y_dist = Math.abs(tWp.getY() - wp.getY());
                if (x_dist <= 1 && y_dist <= 1)
                    count++;
            }
            if (count > totalCount) {
                totalCount = count;
                toHighlight = npc;
            }
        }
        if (toHighlight != null) {
            Shape poly = toHighlight.getConvexHull();
            if (poly != null) {
                Color color = Color.MAGENTA;
                int strokeWidth = 2;
                int outlineAlpha = 255;
                int fillAlpha = 30;
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                graphics.setStroke(new BasicStroke(strokeWidth));
                graphics.draw(poly);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                graphics.fill(poly);
            }
        }
    }

    private void drawMaiden(Graphics2D graphics) {
        NPC maiden = null;
        for (NPC npc : this.client.getNpcs()) {
            String name = npc.getName();
            if (name != null && name.equals("The Maiden of Sugadinti")) {
                maiden = npc;
                break;
            }
        }
        if (maiden == null)
            return;
        WorldPoint maidenWp = maiden.getWorldLocation();
        int maidenX = maidenWp.getX();
        NPCComposition maidenModel = maiden.getTransformedComposition();
        if (maidenModel != null)
            maidenX += maidenModel.getSize();
        for (NPC npc : this.client.getNpcs()) {
            String name = npc.getName();
            if (name != null && name.equals("Nylocas Matomenos")) {
                WorldPoint healerWp = npc.getWorldLocation();
                int healerX = healerWp.getX();
                int deltaX = Math.max(0, healerX - maidenX);
                String deltaXStr = Integer.toString(deltaX);
                Point drawPoint = npc.getCanvasTextLocation(graphics, deltaXStr, 0);
                OverlayUtil.renderTextLocation(graphics, drawPoint, deltaXStr, Color.WHITE);
            }
        }
    }

    private void drawPurpleLanding(Graphics2D graphics) {
        for (Projectile p : this.client.getProjectiles()) {
            int id = p.getId();
            if (id == 1586) {
                if (p.getRemainingCycles() <= 0)
                    continue;
                int totalTicks = p.getEndCycle() - p.getStartCycle();
                double deltaX = p.getVelocityX() * totalTicks;
                double deltaY = p.getVelocityY() * totalTicks;
                deltaX = Math.round(deltaX / 128.0D) * 128.0D;
                deltaY = Math.round(deltaY / 128.0D) * 128.0D;
                int newX = p.getX1() + (int)deltaX;
                int newY = p.getY1() + (int)deltaY;
                LocalPoint raw = new LocalPoint(newX, newY);
                WorldPoint world = WorldPoint.fromLocal(this.client, raw);
                LocalPoint real = LocalPoint.fromWorld(this.client, world);
                Polygon poly = Perspective.getCanvasTileAreaPoly(this.client, real, 3);
                if (poly == null)
                    continue;
                Color color = Color.RED;
                int strokeWidth = 2;
                int outlineAlpha = 255;
                int fillAlpha = 10;
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
                graphics.setStroke(new BasicStroke(strokeWidth));
                graphics.draw(poly);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
                graphics.fill(poly);
            }
        }
    }

    private void drawProjectiles(Graphics2D graphics, int[] arr) {
        Player p = this.client.getLocalPlayer();
        if (p == null)
            return;
        LocalPoint lp = p.getLocalLocation();
        if (lp == null)
            return;
        for (Projectile projectile : this.client.getProjectiles()) {
            int id = projectile.getId();
            for (int k : arr) {
                if (k != -1)
                    if (id == k) {
                        double millis = Math.max(0, projectile.getRemainingCycles());
                        double ticks = millis / 60.0D;
                        double round = Math.round(ticks * 10.0D) / 10.0D;
                        Color color = (round > 0.6D) ? Color.WHITE : Color.ORANGE;
                        String str = Double.toString(round);
                        Point loc = Perspective.getCanvasTextLocation(this.client, graphics, lp, str, 0);
                        if (loc != null)
                            OverlayUtil.renderTextLocation(graphics, loc, str, color);
                        break;
                    }
            }
        }
    }
}
