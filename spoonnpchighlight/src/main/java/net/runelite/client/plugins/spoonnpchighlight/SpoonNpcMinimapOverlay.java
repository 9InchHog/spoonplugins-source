package net.runelite.client.plugins.spoonnpchighlight;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

public class SpoonNpcMinimapOverlay extends Overlay {
    private final Client client;

    private final SpoonNpcHighlightPlugin plugin;

    private final SpoonNpcHighlightConfig config;

    @Inject
    private SpoonNpcMinimapOverlay(Client client, SpoonNpcHighlightPlugin plugin, SpoonNpcHighlightConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for(NPC npc : this.client.getNpcs()) {
            if(npc.getName() != null && config.npcMinimapMode() != SpoonNpcHighlightConfig.npcMinimapMode.OFF) {
                String name = npc.getName().toLowerCase();
                ArrayList<ArrayList<String>> allLists = new ArrayList<ArrayList<String>>(
                        Arrays.asList(plugin.tileNames, plugin.trueTileNames, plugin.swTileNames, plugin.hullNames, plugin.areaNames, plugin.outlineNames, plugin.turboNames)
                );
                for (ArrayList<String> strList : allLists) {
                    for (String str : strList) {
                        if (str.equalsIgnoreCase(name) || (str.contains("*")
                                && ((str.startsWith("*") && str.endsWith("*") && name.contains(str.replace("*", "")))
                                || (str.startsWith("*") && name.endsWith(str.replace("*", ""))) || name.startsWith(str.replace("*", ""))))) {
                            NPCComposition npcComposition = npc.getTransformedComposition();
                            if (npcComposition != null && npcComposition.isInteractible()) {
                                Point minimapLocation = npc.getMinimapLocation();
                                if (minimapLocation != null) {
                                    if (config.npcMinimapMode() == SpoonNpcHighlightConfig.npcMinimapMode.DOT || config.npcMinimapMode() == SpoonNpcHighlightConfig.npcMinimapMode.BOTH) {
                                        OverlayUtil.renderMinimapLocation(graphics, minimapLocation, config.highlightColor());
                                    }

                                    if (config.npcMinimapMode() == SpoonNpcHighlightConfig.npcMinimapMode.NAME || config.npcMinimapMode() == SpoonNpcHighlightConfig.npcMinimapMode.BOTH) {
                                        OverlayUtil.renderTextLocation(graphics, minimapLocation, name, config.highlightColor());
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }
}
