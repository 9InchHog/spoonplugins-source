package net.runelite.client.plugins.infernoretard;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.openosrs.client.util.WeaponMap;
import com.openosrs.client.util.WeaponStyle;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.pf4j.Extension;

import java.util.Set;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Inferno Wheelchair",
        enabledByDefault = false,
        description = "For the disabled",
        tags = {"inferno", "wheelchair", "spoon", "zuk"}
)
@Slf4j
public class InfernoRetardPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private InfernoRetardConfig config;

    private static final int INFERNO_REGION = 9043;
    private WeaponStyle weaponStyle;
    private boolean skipTickCheck = false;
    private boolean brewedDown = false;

    protected static final Set<String> INFERNO_NPC = ImmutableSet.of(
            "Jal-Nib", "Jal-MejRah", "Jal-Ak", "Jal-AkRek-Xil", "Jal-AkRek-Mej", "Jal-AkRek-Ket", "Jal-ImKot", "Jal-Xil", "Jal-Zek",
            "JalTok-Jad", "Yt-HurKot", "TzKal-Zuk", "Jal-MejJak"
    );

    protected static final Set<Integer> TRIDENT_IDS = ImmutableSet.of(
            ItemID.SANGUINESTI_STAFF, ItemID.HOLY_SANGUINESTI_STAFF, ItemID.TRIDENT_OF_THE_SEAS_E, ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.TRIDENT_OF_THE_SEAS_FULL, ItemID.TRIDENT_OF_THE_SWAMP_E, ItemID.TRIDENT_OF_THE_SWAMP
    );

    @Provides
    InfernoRetardConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(InfernoRetardConfig.class);
    }

    private boolean isInInferno()
    {
        return ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (isInInferno()) {
            if (skipTickCheck) {
                skipTickCheck = false;
            } else {
                if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null) {
                    return;
                }
                int equippedWeapon = ObjectUtils.defaultIfNull(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON), -1);
                weaponStyle = WeaponMap.StyleMap.get(equippedWeapon);
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (isInInferno()) {
            int magicLvl = client.getBoostedSkillLevel(Skill.MAGIC);
            int autoCast = client.getVar(Varbits.AUTO_CAST_SPELL);
            int castThreshold = 1;
            switch (autoCast) {
                case 46: //Ice barrage
                    castThreshold = 94;
                    break;
                case 45: //Blood barrage
                    castThreshold = 92;
                    break;
                case 44: //Shadow barrage
                    castThreshold = 88;
                    break;
                case 43: //Smoke barrage
                    castThreshold = 86;
                    break;
                case 42: //Ice blitz
                    castThreshold = 82;
                    break;
                case 41: //Blood blitz
                    castThreshold = 80;
                    break;
                case 40: //Shadow blitz
                    castThreshold = 76;
                    break;
                case 39: //Smoke blitz
                    castThreshold = 74;
                    break;
                case 38: //Ice burst
                    castThreshold = 70;
                    break;
                case 37: //Blood burst
                    castThreshold = 68;
                    break;
                case 36: //Shadow burst
                    castThreshold = 64;
                    break;
                case 35: //Smoke burst
                    castThreshold = 62;
                    break;
                case 34: //Ice rush
                    castThreshold = 58;
                    break;
                case 33: //Blood rush
                    castThreshold = 56;
                    break;
                case 32: //Shadow rush
                    castThreshold = 52;
                    break;
                case 31: //Smoke rush
                    castThreshold = 50;
                    break;
            }
            brewedDown = magicLvl < castThreshold;
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (isInInferno()) {
            String target = Text.removeTags(event.getMenuTarget(), true);
            if (event.getMenuAction() == MenuAction.ITEM_SECOND_OPTION) {
                WeaponStyle newStyle = WeaponMap.StyleMap.get(event.getId());
                if (newStyle != null) {
                    skipTickCheck = true;
                    weaponStyle = newStyle;
                }
            } else if (config.consumeClick()) {
                if (INFERNO_NPC.contains(target) && event.getMenuAction() == MenuAction.NPC_SECOND_OPTION && weaponStyle != null
                        && client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null) {
                    int feet = -1;
                    switch (weaponStyle) {
                        case MAGIC:
                            if (config.antibop() && (!client.getSpellSelected() && client.getVarbitValue(275) == 0
                                    && !TRIDENT_IDS.contains(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON)))
                                    || (client.getVarbitValue(275) == 1 && brewedDown)) {
                                event.consume();
                            }
                            break;
                        case MELEE:
                            if (config.antikick() && feet == (client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON))) {
                                event.consume();
                            }
                            break;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (isInInferno() && !config.consumeClick()) {
            String target = Text.removeTags(event.getTarget(), true);

            if (INFERNO_NPC.contains(target) && event.getType() == MenuAction.NPC_SECOND_OPTION.getId() && weaponStyle != null
                    && client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null) {
                int feet = -1;
                switch (weaponStyle) {
                    case MAGIC:
                        if (config.antibop() && (!client.getSpellSelected() && client.getVarbitValue(275) == 0
                                && !TRIDENT_IDS.contains(client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON)))
                                || (client.getVarbitValue(275) == 1 && brewedDown)) {
                                client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                            }
                        break;
                    case MELEE:
                        if (config.antikick() && feet == (client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON))) {
                            client.setMenuOptionCount(client.getMenuOptionCount() - 1);
                        }
                        break;
                }
            }
        }
    }
}
