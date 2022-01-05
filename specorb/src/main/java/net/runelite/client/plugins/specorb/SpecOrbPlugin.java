package net.runelite.client.plugins.specorb;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "Spec Orb",
        enabledByDefault = false,
        description = "Make the special attack orb work everywhere with all weapons"
)
public class SpecOrbPlugin extends Plugin
{
    @Inject
    private Client client;

    private final Point invalidMouseLocation = new Point(-1, -1);


    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (event.getMenuAction() == MenuAction.CC_OP && event.getParam1() == 160)
        {
            client.setMenuOptionCount(client.getMenuOptionCount() - 1);
        }
    }

    @Subscribe
    private void onClientTick(ClientTick event)
    {
        Widget specOrb = client.getWidget(160, 30);
        Point mousePosition = client.getMouseCanvasPosition();

        if (specOrb == null || mousePosition.equals(invalidMouseLocation))
        {
            return;
        }

        if (specOrb.getBounds().contains(mousePosition.getX(), mousePosition.getY()))
        {
            client.insertMenuItem("Use <col=00ff00>Special Attack</col>", "", MenuAction.CC_OP.getId(), 1, -1, WidgetInfo.COMBAT_SPECIAL_ATTACK_CLICKBOX.getId(), false);
        }
    }
}
