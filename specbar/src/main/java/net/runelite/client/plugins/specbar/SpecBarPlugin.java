package net.runelite.client.plugins.specbar;

import com.google.inject.Provides;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Spec Bar",
        description = "Shows the spec bar instantly when you equip a spec weapon",
        tags = {"special", "spec-bar", "special attack"},
        enabledByDefault = false
)
public class SpecBarPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    public SpecBarConfig config;

    private WeaponStyle weaponStyle;
    private boolean skipTickCheck = false;

    @Provides
    SpecBarConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SpecBarConfig.class);
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        final int specBarWidgetId = config.specbarid();
        Widget specbarWidget = client.getWidget(WidgetID.COMBAT_GROUP_ID, specBarWidgetId);
        if (specbarWidget != null && weaponStyle == WeaponStyle.SPEC)
        {
            specbarWidget.setHidden(false);
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