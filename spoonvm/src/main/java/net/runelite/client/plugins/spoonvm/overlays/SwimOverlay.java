package net.runelite.client.plugins.spoonvm.overlays;

import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.spoonvm.SpoonVMConfig;
import net.runelite.client.plugins.spoonvm.SpoonVMObjects;
import net.runelite.client.plugins.spoonvm.SpoonVMPlugin;
import net.runelite.client.plugins.spoonvm.utils.Constants;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.AsyncBufferedImage;

import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.function.Supplier;
import net.runelite.api.Point;

public class SwimOverlay extends Overlay {
    private final Client client;
    private final SpoonVMPlugin plugin;
    private final Constants constants;
    private final SpoonVMConfig config;
    private final ItemManager itemManager;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private SwimOverlay(Client client, SpoonVMPlugin plugin, Constants constants, SpoonVMConfig config, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.constants = constants;
        this.config = config;
        this.itemManager = itemManager;
        setPosition(OverlayPosition.TOP_CENTER);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.swimWarning() != SpoonVMConfig.SwimWarningStyle.OFF && plugin.platforms.size() > 0 && this.client.getLocalPlayer() != null) {
            for(SpoonVMObjects vmObj : plugin.platforms) {
                LocalPoint playerLp = this.client.getLocalPlayer().getLocalLocation();
                LocalPoint objLp = vmObj.obj.getLocalLocation();
                if (playerLp.getX() == objLp.getX() && playerLp.getY() == objLp.getY()) {
                    this.panelComponent.getChildren().clear();

                    if (config.swimWarning() == SpoonVMConfig.SwimWarningStyle.OVERLAY) {
                        double doubleDimensions = 150.0D;
                        AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(21655);
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
                    } else if (config.swimWarning() == SpoonVMConfig.SwimWarningStyle.TEXT) {
                        String text = "Run Nigga Run";
                        int textWidth = graphics.getFontMetrics().stringWidth("Run Nigga Run");
                        int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
                        int width = (int) this.client.getRealDimensions().getWidth();
                        this.panelComponent.getChildren().add(TitleComponent.builder().text("Run Nigga Run").color(Color.RED).build());
                        java.awt.Point jpoint = new java.awt.Point(width / 2 - textWidth, textHeight + 75);
                    } else if (config.swimWarning() == SpoonVMConfig.SwimWarningStyle.BOTH) {
                        double doubleDimensions = 150.0D;
                        AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(21655);
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
                        String text = "Run Nigga Run";
                        int textWidth = graphics.getFontMetrics().stringWidth("Run Nigga Run");
                        int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
                        int width = (int) this.client.getRealDimensions().getWidth();
                        this.panelComponent.getChildren().add(TitleComponent.builder().text("Run Nigga Run").color(Color.RED).build());
                        java.awt.Point jpoint = new java.awt.Point(width / 2 - textWidth, textHeight + 75);
                    }
                    this.panelComponent.render(graphics);
                }
            }
        }
        return null;
    }

    protected static void renderTextLocation(Graphics2D graphics, @Nullable Point txtLoc, @Nullable String text, @Nonnull Color color, Supplier<Boolean> outline) {
        if (txtLoc == null || Strings.isNullOrEmpty(text))
            return;
        int x = txtLoc.getX();
        int y = txtLoc.getY();
        graphics.setColor(Color.BLACK);
        if (outline.get()) {
            graphics.drawString(text, x, y + 1);
            graphics.drawString(text, x, y - 1);
            graphics.drawString(text, x + 1, y);
            graphics.drawString(text, x - 1, y);
        } else {
            graphics.drawString(text, x + 1, y + 1);
        }
        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }
}