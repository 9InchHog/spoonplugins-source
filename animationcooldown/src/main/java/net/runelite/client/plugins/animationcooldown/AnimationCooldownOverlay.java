package net.runelite.client.plugins.animationcooldown;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Point;
import net.runelite.api.kit.KitType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.http.api.item.ItemStats;

import java.awt.*;

public class AnimationCooldownOverlay extends Overlay {
    private final Client client;
    private final AnimationCooldownPlugin plugin;
    private final AnimationCooldownConfig config;
    @Inject
    private ItemManager itemManager;
    private final Color GREEN = new Color(0, 200, 83);
    private final Color BLUE = new Color(0, 184, 212);

    @Inject
    AnimationCooldownOverlay(Client client, AnimationCooldownPlugin plugin, AnimationCooldownConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.setPriority(OverlayPriority.HIGH);
    }

    public Dimension render(Graphics2D graphics) {
        Font prevFont = graphics.getFont();
        graphics.setFont(this.config.animationCooldownFontType().getFont());

        if (this.plugin.getPlayerListMultiMap().size() > 0 && (!config.raidsOnly() || plugin.inRaid || plugin.enforceRegion())) {
            this.plugin.getPlayerListMultiMap().forEach((player, pair) -> {
                String ticks = pair.getRight() >= 0 ? Integer.toString(pair.getRight()) : Integer.toString(pair.getRight() * -1);
                Color color = pair.getRight() > 0 ? this.config.tickCounterColor() : this.config.lostTicksColor();
                Point textLocation = player.getCanvasTextLocation(graphics, "", this.config.animationCooldownOffset());
                if (textLocation != null) {
                    OverlayUtil.renderTextLocation(graphics, textLocation, ticks, color);
                }

            });
        }

        if (this.config.animDebug()) {
            this.renderDebugOverlay(graphics);
        }

        graphics.setFont(prevFont);
        return null;
    }

    private void renderDebugOverlay(Graphics2D graphics) {
        this.client.getPlayers().forEach((player) -> {
            PlayerComposition composition = player.getPlayerComposition();
            int weapon = composition.getEquipmentId(KitType.WEAPON);
            ItemStats itemStats = this.getItemStats(weapon);
            String text = this.getPlayerDebugString(player.getName(), weapon, player.getAnimation(), itemStats != null ? itemStats.getEquipment().getAspeed() : 0);
            OverlayUtil.renderActorOverlay(graphics, player, text, player == this.client.getLocalPlayer() ? this.BLUE : this.GREEN);
        });
    }

    private ItemStats getItemStats(int weaponId) {
        return this.itemManager.getItemStats(weaponId, false);
    }

    private String getPlayerDebugString(String name, int weapon, int animation, int ticks) {
        return name + " (Weapon ID: " + weapon + ") (Animation: " + animation + ") (Ticks: " + ticks + ")";
    }
}
