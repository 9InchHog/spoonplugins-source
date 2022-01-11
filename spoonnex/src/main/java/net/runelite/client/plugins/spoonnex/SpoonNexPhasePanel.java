package net.runelite.client.plugins.spoonnex;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class SpoonNexPhasePanel extends OverlayPanel{
    private SpoonNexPlugin plugin;

    private SpoonNexConfig config;

    private Client client;

    @Inject
    public SpoonNexPhasePanel(SpoonNexPlugin plugin, SpoonNexConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }
    
    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if(config.killTimer() == SpoonNexConfig.KillTimerMode.PANEL && (plugin.nex != null || plugin.timerTicksLeft > 0) && plugin.startTick > -1){
            if(plugin.timerTicksLeft > 0) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time:")
                    .rightColor(Color.GREEN)
                    .right(plugin.ticksToTime(plugin.p5Tick - plugin.startTick))
                    .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time:")
                    .right(plugin.ticksToTime(client.getTickCount() - plugin.startTick))
                    .build());
            }

            String phaseText = "";
            String timeText = "";
            if(plugin.p1Tick > -1) {
                phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P1:" : "Smoke:";
                timeText = plugin.ticksToTime(plugin.p1Tick - plugin.startTick);
                if (config.showMinionSplit() && plugin.p1Boss > -1)
                    timeText += " (" + plugin.ticksToTime(plugin.p1Boss - plugin.startTick) + ")";
                createPanelComponent(phaseText, timeText);

                if(plugin.p2Tick > -1) {
                    phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P2:" : "Shadow:";
                    timeText = plugin.ticksToTime(plugin.p2Tick - plugin.p1Tick);
                    if (config.showMinionSplit() && plugin.p2Boss > -1)
                        timeText += " (" + plugin.ticksToTime(plugin.p2Boss - plugin.p1Tick) + ")";
                    createPanelComponent(phaseText, timeText);

                    if(plugin.p3Tick > -1) {
                        phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P3:" : "Blood:";
                        timeText = plugin.ticksToTime(plugin.p3Tick - plugin.p2Tick);
                        if (config.showMinionSplit() && plugin.p3Boss > -1)
                            timeText += " (" + plugin.ticksToTime(plugin.p3Boss - plugin.p2Tick) + ")";
                        createPanelComponent(phaseText, timeText);

                        if(plugin.p4Tick > -1) {
                            phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P4:" : "Ice:";
                            timeText = plugin.ticksToTime(plugin.p4Tick - plugin.p3Tick);
                            if (config.showMinionSplit() && plugin.p4Boss > -1)
                                timeText += " (" + plugin.ticksToTime(plugin.p4Boss - plugin.p3Tick) + ")";
                            createPanelComponent(phaseText, timeText);

                            if(plugin.p5Tick > -1) {
                                phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P5:" : "Zaros:";
                                timeText = plugin.ticksToTime(plugin.p5Tick - plugin.p4Tick);
                                createPanelComponent(phaseText, timeText);
                            } else {
                                phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P5:" : "Zaros:";
                                timeText = plugin.ticksToTime(client.getTickCount() - plugin.p4Tick);
                                createPanelComponent(phaseText, timeText);
                            }
                        } else {
                            phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P4:" : "Ice:";
                            timeText = plugin.ticksToTime(client.getTickCount() - plugin.p3Tick);
                            if (config.showMinionSplit() && plugin.p4Boss > -1)
                                timeText += " (" + plugin.ticksToTime(plugin.p4Boss - plugin.p3Tick) + ")";
                            createPanelComponent(phaseText, timeText);
                        }
                    } else {
                        phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P3:" : "Blood:";
                        timeText = plugin.ticksToTime(client.getTickCount() - plugin.p2Tick);
                        if (config.showMinionSplit() && plugin.p3Boss > -1)
                            timeText += " (" + plugin.ticksToTime(plugin.p3Boss - plugin.p2Tick) + ")";
                        createPanelComponent(phaseText, timeText);
                    }
                } else {
                    phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P2:" : "Shadow:";
                    timeText = plugin.ticksToTime(client.getTickCount() - plugin.p1Tick);
                    if (config.showMinionSplit() && plugin.p2Boss > -1)
                        timeText += " (" + plugin.ticksToTime(plugin.p2Boss - plugin.p1Tick) + ")";
                    createPanelComponent(phaseText, timeText);
                }
            } else {
                phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P1:" : "Smoke:";
                timeText = plugin.ticksToTime(client.getTickCount() - plugin.startTick);
                if (config.showMinionSplit() && plugin.p1Boss > -1)
                    timeText += " (" + plugin.ticksToTime(plugin.p1Boss - plugin.startTick) + ")";
                createPanelComponent(phaseText, timeText);
            }
        }
        return super.render(graphics);
    }

    public void createPanelComponent(String phaseText, String timeText) {
        this.panelComponent.getChildren().add(LineComponent.builder()
            .left(phaseText)
            .right(timeText)
            .build());
    }
}
