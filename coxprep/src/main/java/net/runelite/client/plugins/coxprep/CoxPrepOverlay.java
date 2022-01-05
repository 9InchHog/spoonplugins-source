package net.runelite.client.plugins.coxprep;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class CoxPrepOverlay extends OverlayPanel {
    private Client client;
    private CoxPrepPlugin plugin;
    private CoxPrepConfig config;

    @Inject
    private CoxPrepOverlay(Client client, CoxPrepPlugin plugin, CoxPrepConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<LayoutableRenderableEntity> elems = this.panelComponent.getChildren();
        if (plugin.roomtype == 3) {
            if((config.brews() + config.revites() + config.enhances()) != 0) {
                if (plugin.totalBuchus >= (config.brews() + config.revites() + config.enhances())) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Buchu: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.totalBuchus + "/" + (config.brews() + config.revites() + config.enhances()))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Buchu: ")
                            .right(plugin.totalBuchus + "/" + (config.brews() + config.revites() + config.enhances()))
                            .build());
                }

                if(config.showPots()) {
                    if (config.brews() > 0) {
                        if (plugin.totalBrews >= config.brews()) {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Brews: ")
                                    .rightColor(Color.GREEN)
                                    .right(plugin.totalBrews + "/" + config.brews())
                                    .build());
                        } else {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Brews: ")
                                    .right(plugin.totalBrews + "/" + config.brews())
                                    .build());
                        }
                    }

                    if (config.revites() > 0) {
                        if (plugin.totalRevites >= config.revites()) {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Revites: ")
                                    .rightColor(Color.GREEN)
                                    .right(plugin.totalRevites + "/" + config.revites())
                                    .build());
                        } else {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Revites: ")
                                    .right(plugin.totalRevites + "/" + config.revites())
                                    .build());
                        }
                    }

                    if (config.enhances() > 0) {
                        if (plugin.totalEnhances >= config.enhances()) {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Enhances: ")
                                    .rightColor(Color.GREEN)
                                    .right(plugin.totalEnhances + "/" + config.enhances())
                                    .build());
                        } else {
                            elems.add(LineComponent.builder()
                                    .leftColor(Color.WHITE)
                                    .left("    Enhances: ")
                                    .right(plugin.totalEnhances + "/" + config.enhances())
                                    .build());
                        }
                    }
                }
            }

            if(config.overloads() != 0) {
                if (plugin.totalGolpar >= (config.overloads() * 3)) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Golpar: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.totalGolpar + "/" + (config.overloads() * 3))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Golpar: ")
                            .right(plugin.totalGolpar + "/" + (config.overloads() * 3))
                            .build());
                }

                if(config.showPots()) {
                    if (plugin.totalElders >= (config.overloads() * 3)) {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Elders: ")
                                .rightColor(Color.GREEN)
                                .right(plugin.totalElders + "/" + (config.overloads() * 3))
                                .build());
                    } else {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Elders: ")
                                .right(plugin.totalElders + "/" + (config.overloads() * 3))
                                .build());
                    }

                    if (plugin.totalTwisteds >= (config.overloads() * 3)) {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Twiseds: ")
                                .rightColor(Color.GREEN)
                                .right(plugin.totalTwisteds + "/" + (config.overloads() * 3))
                                .build());
                    } else {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Twisteds: ")
                                .right(plugin.totalTwisteds + "/" + (config.overloads() * 3))
                                .build());
                    }

                    if (plugin.totalKodais >= (config.overloads() * 3)) {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Kodais: ")
                                .rightColor(Color.GREEN)
                                .right(plugin.totalKodais + "/" + (config.overloads() * 3))
                                .build());
                    } else {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Kodais: ")
                                .right(plugin.totalKodais + "/" + (config.overloads() * 3))
                                .build());
                    }
                }

                if (plugin.totalNox >= config.overloads()) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Noxifer: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.totalNox + "/" + config.overloads())
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Noxifer: ")
                            .right(plugin.totalNox + "/" + config.overloads())
                            .build());
                }

                if(config.showPots()) {
                    if (plugin.totalOverloads >= config.overloads()) {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Overloads: ")
                                .rightColor(Color.GREEN)
                                .right(plugin.totalOverloads + "/" + config.overloads())
                                .build());
                    } else {
                        elems.add(LineComponent.builder()
                                .leftColor(Color.WHITE)
                                .left("    Overloads: ")
                                .right(plugin.totalOverloads + "/" + config.overloads())
                                .build());
                    }
                }
            }
        }

        if (plugin.roomtype == 2 && config.showSecondaries()) {
            if(config.brews() != 0 || config.overloads() != 0) {
                if (plugin.pickedJuice >= (config.brews() + config.overloads())) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Juice: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.pickedJuice + "/" + (config.brews() + config.overloads()))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Juice: ")
                            .right(plugin.pickedJuice + "/" + (config.brews() + config.overloads()))
                            .build());
                }
            }

            if(config.revites() != 0 || config.overloads() != 0) {
                if (plugin.pickedShrooms >= (config.revites() + config.overloads())) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Shrooms: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.pickedShrooms + "/" + (config.revites() + config.overloads()))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Shrooms: ")
                            .right(plugin.pickedShrooms + "/" + (config.revites() + config.overloads()))
                            .build());
                }
            }

            if(config.enhances() != 0 || config.overloads() != 0) {
                if (plugin.pickedCicely >= (config.enhances() + config.overloads())) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Cicely: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.pickedCicely + "/" + (config.enhances() + config.overloads()))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Cicely: ")
                            .right(plugin.pickedCicely + "/" + (config.enhances() + config.overloads()))
                            .build());
                }
            }
        }
        return super.render(graphics);
    }
}
