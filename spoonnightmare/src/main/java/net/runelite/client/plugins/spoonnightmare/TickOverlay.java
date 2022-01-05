package net.runelite.client.plugins.spoonnightmare;

import com.google.common.base.Strings;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class TickOverlay extends Overlay {
    private final Client client;

    private final SpoonNightmarePlugin plugin;

    private final SpoonNightmareConfig config;

    @Inject
    private TickOverlay(Client client, SpoonNightmarePlugin plugin, SpoonNightmareConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private void renderNightmareTicks(Graphics2D graphics) {
        if (plugin.getTicksUntilAttack() > 0 || plugin.getEventTicks() > 0) {
            NPC npc = plugin.getNightmareNpc();
            Color color = config.tickCounterColor();
            if (plugin.getTicksUntilAttack() > 2 && config.matchStyleColor() && config.tickCounter()){
                if(plugin.correctPray.equalsIgnoreCase("melee") && plugin.getNightmareNpc().getAnimation() == 8594){
                    color = Color.RED;
                }else if(plugin.correctPray.equalsIgnoreCase("magic") && plugin.getNightmareNpc().getAnimation() == 8595){
                    color = Color.CYAN;
                }else if(plugin.correctPray.equalsIgnoreCase("missiles") && plugin.getNightmareNpc().getAnimation() == 8596){
                    color = Color.GREEN;
                }
            }
            String nmTicks = "";
            String attTicks = Integer.toString(plugin.getTicksUntilAttack());
            String eventTicks = Integer.toString(plugin.getEventTicks());
            if (config.tickCounter() && config.eventTickCounter()) {
                if (plugin.getTicksUntilAttack() > 0) {
                    if (plugin.getEventTicks() > 0) {
                        nmTicks = attTicks + " : " + eventTicks;
                    } else {
                        nmTicks = attTicks;
                    }
                } else if (plugin.getTicksUntilAttack() < 0 && plugin.getEventTicks() > 0) {
                    nmTicks = eventTicks;
                }
            } else if (config.tickCounter() && plugin.getTicksUntilAttack() > 0) {
                nmTicks = attTicks;
            } else if (config.eventTickCounter() && plugin.getEventTicks() > 0) {
                nmTicks = eventTicks;
            }
            Point p = npc.getCanvasTextLocation(graphics, nmTicks, 0);
            renderTextLocation(graphics, p, nmTicks, color, config.tickCounterSize(), config.txtOutline());
        }
    }

    private static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color, int size, boolean outline) {
        if (Strings.isNullOrEmpty(text))
            return;
        if (txtLoc == null)
            return;
        int x = txtLoc.getX() - 5;
        int y = txtLoc.getY() + 5;
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", 1, size));
        if (outline) {
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

    public Dimension render(Graphics2D graphics) {
        if ((plugin.isActiveFight() || plugin.getNightmareNpc() != null) && (
                config.tickCounter() || config.eventTickCounter()))
            renderNightmareTicks(graphics);
        return null;
    }
}
