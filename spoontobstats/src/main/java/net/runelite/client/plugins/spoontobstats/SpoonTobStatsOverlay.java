package net.runelite.client.plugins.spoontobstats;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonTobStatsOverlay extends OverlayPanel {
    private final Client client;
    private final SpoonTobStatsPlugin plugin;
    private final SpoonTobStatsConfig config;

    @Inject
    private SpoonTobStatsOverlay(Client client, SpoonTobStatsPlugin plugin, SpoonTobStatsConfig config) {
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) { {
            if (config.timerOverlay() && plugin.isTobInside()) {
                if (!config.fontName().equals("")) {
                    graphics.setFont(new Font(config.fontName(), config.fontWeight().weight, config.fontSize()));
                }

                LineComponent lineComponent = null;
                panelComponent.getChildren().clear();

                for (String room : plugin.getRoom().keySet()) {
                    lineComponent = LineComponent.builder().left(room).build(); //Room name

                    if (plugin.getTime().get(room) != null) {
                        lineComponent.setRightColor(Color.GREEN);
                        String time = plugin.formatTime(plugin.getTime().get(room));
                        if (plugin.getPhaseSplit().get(room) != null) {
                            lineComponent.setRight("(" + plugin.formatTime(plugin.getPhaseSplit().get(room)) + ") " + time); //Room time with splits
                        } else {
                            lineComponent.setRight(time); //Room time
                        }
                    } else {
                        String current = plugin.formatTime(client.getTickCount() - plugin.getRoom().get(room));
                        if (!plugin.getPhase().isEmpty()) {
                            String phase = plugin.getPhase().getLast();
                            if (plugin.getPhaseSplit().get(phase) != null) {
                                lineComponent.setRight("(" + plugin.formatTime(plugin.getPhaseSplit().get(phase)) + ") " + current);
                            } else {
                                lineComponent.setRight("(" + plugin.formatTime(plugin.getPhaseTime().get(phase)) + ") " + current);
                            }
                        } else {
                            lineComponent.setRight(current);
                        }
                    }
                }
                if (lineComponent != null) {
                    panelComponent.getChildren().add(lineComponent);
                }

                if (!config.simpleOverlay()) {
                    for (String phase : plugin.getPhase()) {
                        String phaseTime = plugin.formatTime(plugin.getPhaseTime().get(phase));
                        if (plugin.getPhaseSplit().get(phase) != null) {
                            panelComponent.getChildren().add(LineComponent.builder().left(phase).right(phaseTime + " (" + plugin.formatTime(plugin.getPhaseSplit().get(phase)) + ")").build());
                        } else {
                            panelComponent.getChildren().add(LineComponent.builder().left(phase).right(phaseTime).build());
                        }
                    }
                }
                return panelComponent.render(graphics);
            }
            return null;
        }
    }
}
