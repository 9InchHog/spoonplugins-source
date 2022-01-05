package net.runelite.client.plugins.yuritheatre;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.AsyncBufferedImage;

public class YuriPrayerOverlay extends Overlay {
    private final Client client;

    private final YuriPlugin plugin;

    private final YuriConfig config;

    private final ItemManager itemManager;

    private final PanelComponent panelComponent = new PanelComponent();

    public static final int DIMENSIONS = 150;

    @Inject
    public YuriPrayerOverlay(Client client, YuriPlugin plugin, YuriConfig config, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        setPosition(OverlayPosition.TOP_CENTER);
        setPriority(OverlayPriority.HIGH);
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Yuri Prayer Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if (!this.config.showPrayerDrink())
            return null;
        double prayerLevel = this.client.getRealSkillLevel(Skill.PRAYER);
        int restoreCount = (int)Math.floor(prayerLevel / 4.0D) + 7;
        int afterDrink = this.client.getBoostedSkillLevel(Skill.PRAYER) + restoreCount;
        if (afterDrink > (int)prayerLevel)
            return null;
        double doubleDimensions = 150.0D;
        AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(139);
        double multiplierX = doubleDimensions / asyncBufferedImage.getWidth();
        double multiplierY = doubleDimensions / asyncBufferedImage.getHeight();
        double multiplier = Math.min(multiplierX, multiplierY);
        int realX = (int)(multiplier * asyncBufferedImage.getWidth());
        int realY = (int)(multiplier * asyncBufferedImage.getHeight());
        int half = 75;
        BufferedImage image = new BufferedImage(150, 150, 2);
        Graphics2D g = image.createGraphics();
        long time = Math.abs(System.currentTimeMillis() % 2000L - 1000L);
        float opacity = (float)time / 1000.0F;
        g.setComposite(AlphaComposite.getInstance(3, opacity));
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage((Image)asyncBufferedImage, 10 + half - realX / 2, half - realY / 2, realX, realY, null);
        g.dispose();
        this.panelComponent.getChildren().add(new ImageComponent(image));
        return this.panelComponent.render(graphics);
    }
}
