package net.runelite.client.plugins.spoonvm.overlays;

import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.spoonvm.SpoonVMConfig;
import net.runelite.client.plugins.spoonvm.SpoonVMObjects;
import net.runelite.client.plugins.spoonvm.SpoonVMPlugin;
import net.runelite.client.plugins.spoonvm.utils.Constants;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class RockRespawnOverlay extends Overlay {
    private final Client client;
    private final SpoonVMPlugin plugin;
    private final Constants constants;
    private final SpoonVMConfig config;
    private final ItemManager itemManager;
    private final PanelComponent panelComponent = new PanelComponent();

    protected static final BiFunction<Integer, Integer, Color> rgbMod;

    @Inject
    private RockRespawnOverlay(Client client, SpoonVMPlugin plugin, Constants constants, SpoonVMConfig config, ItemManager itemManager) {
        this.client = client;
        this.plugin = plugin;
        this.constants = constants;
        this.config = config;
        this.itemManager = itemManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }
    static {
        rgbMod = ((max, current) -> new Color(255 * (max - current) / max, 255 * current / max, 0));
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(this.config.vmFontType().getFont());
        if (config.rockRespawn() != SpoonVMConfig.RockRespawnStyle.OFF && plugin.rocks.size() > 0) {
            for(SpoonVMObjects vmObj : plugin.rocks) {
                Polygon poly = vmObj.obj.getCanvasTilePoly();
                if (config.rockRespawn() == SpoonVMConfig.RockRespawnStyle.BOTH) {
                    OverlayUtil.renderPolygon(graphics, poly, rgbMod.apply(25, vmObj.ticks));
                    net.runelite.api.Point textLocation = vmObj.obj.getCanvasTextLocation(graphics, Integer.toString(vmObj.ticks), 0);
                    Objects.requireNonNull(this.config);
                    renderTextLocation(graphics, textLocation, Integer.toString(vmObj.ticks), Color.WHITE, this.config::txtOutline);
                } else if (config.rockRespawn() == SpoonVMConfig.RockRespawnStyle.TILE) {
                    OverlayUtil.renderPolygon(graphics, poly, rgbMod.apply(25, vmObj.ticks));
                } else if (config.rockRespawn() == SpoonVMConfig.RockRespawnStyle.TEXT) {
                    net.runelite.api.Point textLocation = vmObj.obj.getCanvasTextLocation(graphics, Integer.toString(vmObj.ticks), 0);
                    Objects.requireNonNull(this.config);
                    renderTextLocation(graphics, textLocation, Integer.toString(vmObj.ticks), Color.WHITE, this.config::txtOutline);
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
