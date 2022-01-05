package net.runelite.client.plugins.spellbook;

import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseWheelListener;

import javax.inject.Singleton;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

@Singleton
class SpellbookMouseListener extends MouseAdapter implements MouseWheelListener
{
    private final SpellbookPlugin plugin;

    SpellbookMouseListener(final SpellbookPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public MouseEvent mouseClicked(final MouseEvent event)
    {
        if (plugin.isNotOnSpellWidget())
        {
            return event;
        }

        if (SwingUtilities.isMiddleMouseButton(event))
        {
            plugin.resetSize();
        }

        event.consume();
        return event;
    }

    @Override
    public MouseEvent mousePressed(final MouseEvent event)
    {
        if (SwingUtilities.isRightMouseButton(event))
        {
            plugin.resetLocation();
            return event;
        }
        else if (SwingUtilities.isLeftMouseButton(event) && !plugin.isNotOnSpellWidget() && !plugin.isDragging())
        {
            plugin.startDragging(event.getPoint());
            event.consume();
        }

        return event;
    }

    @Override
    public MouseEvent mouseReleased(final MouseEvent event)
    {
        if (!SwingUtilities.isLeftMouseButton(event) || !plugin.isDragging())
        {
            return event;
        }

        plugin.completeDragging(event.getPoint());

        event.consume();
        return event;
    }

    @Override
    public MouseWheelEvent mouseWheelMoved(final MouseWheelEvent event)
    {
        if (plugin.isNotOnSpellWidget())
        {
            return event;
        }

        final int direction = event.getWheelRotation();

        if (direction > 0)
        {
            plugin.increaseSize();
        }
        else
        {
            plugin.decreaseSize();
        }

        event.consume();
        return event;
    }
}