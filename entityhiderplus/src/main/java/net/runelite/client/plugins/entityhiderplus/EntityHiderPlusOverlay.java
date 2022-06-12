package net.runelite.client.plugins.entityhiderplus;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

class EntityHiderPlusOverlay extends Overlay {
    private final Client client;
    private final EntityHiderPlusConfig config;
    private final EntityHiderPlusPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    EntityHiderPlusOverlay(Client client, EntityHiderPlusConfig config, EntityHiderPlusPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (NPC npc : this.client.getNpcs()) {
            if (npc.isDead() && config.higlightDead() && (npc.getId() < 9434 || npc.getId() > 9445)) {
                if (npc.getName() != null) {
                    if (!plugin.blacklistName.contains(npc.getName().toLowerCase()) && !plugin.blacklistID.contains(npc.getId())) {
                        for (String str : plugin.hideNPCsOnDeathName) {
                            if (str.contains("*") && ((str.startsWith("*") && str.endsWith("*") && npc.getName().toLowerCase().contains(str.replace("*", "")))
                                    || (str.startsWith("*") && npc.getName().toLowerCase().endsWith(str.replace("*", ""))) || npc.getName().toLowerCase().startsWith(str.replace("*", "")))){
                                return null;
                            }
                        }
                        modelOutlineRenderer.drawOutline(npc, config.highlightDeadThiCC(), config.highlightDeadColor(), config.highlightDeadFeather());
                    }
                } else {
                    if (!plugin.hideNPCsOnDeathID.contains(npc.getId())) {
                        modelOutlineRenderer.drawOutline(npc, config.highlightDeadThiCC(), config.highlightDeadColor(), config.highlightDeadFeather());
                    }
                }
            }
        }
        return null;
    }
}
