package net.runelite.client.plugins.spoonvm.overlays;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.spoonvm.SpoonVMConfig;
import net.runelite.client.plugins.spoonvm.SpoonVMPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.AsyncBufferedImage;

public class VMPrayerOverlay extends Overlay {
    private final Client client;

    private final SpoonVMPlugin plugin;

    private final SpoonVMConfig config;

    private final ItemManager itemManager;

    private final PanelComponent panelComponent = new PanelComponent();

    public static final int DIMENSIONS = 150;

    @Inject
    public VMPrayerOverlay(Client client, SpoonVMPlugin plugin, SpoonVMConfig config, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Yuri Prayer Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if(plugin.isInVM() && this.config.drinkPrayer()) {
            this.panelComponent.getChildren().clear();
            double prayerLevel = this.client.getRealSkillLevel(Skill.PRAYER);
            int restoreCount = (int) Math.floor(prayerLevel / 4.0D) + 7;
            int afterDrink = this.client.getBoostedSkillLevel(Skill.PRAYER) + restoreCount;
            if (afterDrink <= (int) prayerLevel) {
                double doubleDimensions = 150.0D;
                AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(139);
                double multiplierX = doubleDimensions / asyncBufferedImage.getWidth();
                double multiplierY = doubleDimensions / asyncBufferedImage.getHeight();
                double multiplier = Math.min(multiplierX, multiplierY);
                int realX = (int) (multiplier * asyncBufferedImage.getWidth());
                int realY = (int) (multiplier * asyncBufferedImage.getHeight());
                int half = 75;
                BufferedImage image = new BufferedImage(150, 150, 2);
                Graphics2D g = image.createGraphics();
                long time = Math.abs(System.currentTimeMillis() % 2000L - 1000L);
                float opacity = (float) time / 1000.0F;
                g.setComposite(AlphaComposite.getInstance(3, opacity));
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawImage((Image) asyncBufferedImage, 10 + half - realX / 2, half - realY / 2, realX, realY, null);
                g.dispose();
                this.panelComponent.getChildren().add(new ImageComponent(image));
                return this.panelComponent.render(graphics);
            }
        }
        return null;
    }
}
