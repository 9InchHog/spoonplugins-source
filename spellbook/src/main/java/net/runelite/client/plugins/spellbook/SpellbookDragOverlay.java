package net.runelite.client.plugins.spellbook;

import net.runelite.api.Client;
import net.runelite.api.SpritePixels;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class SpellbookDragOverlay extends Overlay
{
    private final SpellbookPlugin plugin;
    private final Client client;

    @Inject
    private SpellbookDragOverlay(final SpellbookPlugin plugin, final Client client)
    {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.TOOLTIP);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(final Graphics2D g)
    {
        if (!plugin.isDragging())
        {
            return null;
        }

        final net.runelite.api.Point mouseCanvasPosition = client.getMouseCanvasPosition();
        final net.runelite.api.Point draggingLocation = plugin.getDraggingLocation();
        final SpritePixels sprite = plugin.getDraggingWidget().getSprite();
        final Point drawPos = new Point(mouseCanvasPosition.getX() - draggingLocation.getX(), mouseCanvasPosition.getY() - draggingLocation.getY());

        if (sprite != null)
        {
            sprite.drawAt(drawPos.x, drawPos.y);
        }

        return null;
    }
}