package net.runelite.client.plugins.coxadditions.utils;

import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.BackgroundComponent;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.infobox.InfoBox;

@Setter
public class OrbInfoBox implements LayoutableRenderableEntity
{
    private static final int SEPARATOR = 3;
    private static final int DEFAULT_SIZE = 32;

    @Getter
    private String tooltip;

    @Getter
    private final Rectangle bounds = new Rectangle();

    private Point preferredLocation = new Point();
    private Dimension preferredSize = new Dimension(DEFAULT_SIZE, DEFAULT_SIZE);
    private String text;
    private Color color = Color.WHITE;
    private boolean outline;
    private Color backgroundColor = ComponentConstants.STANDARD_BACKGROUND_COLOR;
    private BufferedImage image;
    @Getter
    private InfoBox infoBox;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (image == null)
        {
            return new Dimension();
        }

        graphics.setFont(getSize() < DEFAULT_SIZE ? FontManager.getRunescapeSmallFont() : FontManager.getRunescapeFont());

        final int baseX = preferredLocation.x;
        final int baseY = preferredLocation.y;

        // Calculate dimensions
        final FontMetrics metrics = graphics.getFontMetrics();
        final int size = getSize();
        final Rectangle bounds = new Rectangle(baseX, baseY, size, size);

        // Render background
        final BackgroundComponent backgroundComponent = new BackgroundComponent();
        backgroundComponent.setBackgroundColor(backgroundColor);
        backgroundComponent.setRectangle(bounds);
        backgroundComponent.render(graphics);

        // Render image
        graphics.drawImage(
                image,
                baseX + (size - image.getWidth(null)) / 2,
                baseY + (size - image.getHeight(null)) / 2,
                null);

        // Render caption
        if (!Strings.isNullOrEmpty(text))
        {
            final TextComponent textComponent = new TextComponent();
            textComponent.setColor(color);
            textComponent.setOutline(outline);
            textComponent.setText(text);
            textComponent.setPosition(new Point(baseX + ((size - metrics.stringWidth(text) - 5)), baseY + size - SEPARATOR));
            textComponent.render(graphics);
        }

        this.bounds.setBounds(bounds);
        return bounds.getSize();
    }

    private int getSize()
    {
        return Math.max(preferredSize.width, preferredSize.height);
    }
}
