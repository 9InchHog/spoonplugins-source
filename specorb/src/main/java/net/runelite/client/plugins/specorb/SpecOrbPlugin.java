package net.runelite.client.plugins.specorb;

import javax.inject.Inject;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

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

    @Inject
    public SpecOrbConfig config;

    private final Point invalidMouseLocation = new Point(-1, -1);
    private WeaponStyle weaponStyle;
    private boolean skipTickCheck = false;

    @Provides
    SpecOrbConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SpecOrbConfig.class);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (event.getMenuAction() == MenuAction.CC_OP && event.getParam1() == WidgetInfo.MINIMAP_SPEC_CLICKBOX.getGroupId())
        {
            client.setMenuOptionCount(client.getMenuOptionCount() - 1);
        }
    }

    @Subscribe
    private void onClientTick(ClientTick event)
    {
        Widget specOrb = client.getWidget(WidgetInfo.MINIMAP_SPEC_CLICKBOX);
        Point mousePosition = client.getMouseCanvasPosition();

        if (specOrb == null || mousePosition.equals(invalidMouseLocation))
        {
            return;
        }

        if (config.hideNormalWeapons() && weaponStyle != WeaponStyle.SPEC)
        {
            return;
        }

        if (specOrb.getBounds().contains(mousePosition.getX(), mousePosition.getY()))
        {
            client.insertMenuItem("Use <col=00ff00>Special Attack</col>", "", MenuAction.CC_OP.getId(), 1, -1, WidgetInfo.COMBAT_SPECIAL_ATTACK_CLICKBOX.getId(), false);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.isItemOp() && event.getItemOp() == 2)
        {
            WeaponStyle newStyle = WeaponMap.StyleMap.get(event.getItemId());
            if (newStyle != null)
            {
                skipTickCheck = true;
                weaponStyle = newStyle;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (skipTickCheck)
        {
            skipTickCheck = false;
        }
        else
        {
            if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null)
            {
                return;
            }

            int equippedWeapon = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON), -1);
            weaponStyle = WeaponMap.StyleMap.get(equippedWeapon);
        }
    }
}
