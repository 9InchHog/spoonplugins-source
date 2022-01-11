package net.runelite.client.plugins.spoonnex;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class SpoonNexTimerBox extends InfoBox {
    private final SpoonNexConfig config;
    private final SpoonNexPlugin plugin;
    private final Client client;

    SpoonNexTimerBox(BufferedImage image, SpoonNexConfig config, SpoonNexPlugin plugin, Client client) {
        super(image, plugin);
        this.config = config;
        this.plugin = plugin;
        this.client = client;
        setPriority(InfoBoxPriority.LOW);
    }

    @Override
    public String getText() {
        return plugin.timerTicksLeft > 0 ? plugin.ticksToTime(plugin.p5Tick - plugin.startTick) : plugin.ticksToTime(client.getTickCount() - plugin.startTick);
    }

    @Override
    public Color getTextColor() {
        return plugin.timerTicksLeft > 0 ? Color.GREEN : Color.WHITE;
    }

    @Override
    public String getTooltip() {
        StringBuilder sb = new StringBuilder();
        String phaseText = "";
        String bossText = "";
        if(plugin.p1Tick > -1) {
            phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P1: " : "Smoke: ";
            if (config.showMinionSplit() && plugin.p1Boss > -1)
                bossText = " (" + plugin.ticksToTime(plugin.p1Boss - plugin.startTick) + ")";
            sb.append(phaseText).append(plugin.ticksToTime(plugin.p1Tick - plugin.startTick)).append(bossText).append("</br>");

            if(plugin.p2Tick > -1) {
                phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P2: " : "Shadow: ";
                if (config.showMinionSplit() && plugin.p2Boss > -1)
                    bossText = " (" + plugin.ticksToTime(plugin.p2Boss - plugin.p1Tick) + ")";
                sb.append(phaseText).append(plugin.ticksToTime(plugin.p2Tick - plugin.p1Tick)).append(bossText).append("</br>");

                if(plugin.p3Tick > -1) {
                    phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P3: " : "Blood: ";
                    if (config.showMinionSplit() && plugin.p3Boss > -1)
                        bossText = " (" + plugin.ticksToTime(plugin.p3Boss - plugin.p2Tick) + ")";
                    sb.append(phaseText).append(plugin.ticksToTime(plugin.p3Tick - plugin.p2Tick)).append(bossText).append("</br>");

                    if(plugin.p4Tick > -1) {
                        phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P4: " : "Ice: ";
                        if (config.showMinionSplit() && plugin.p4Boss > -1)
                            bossText = " (" + plugin.ticksToTime(plugin.p4Boss - plugin.p3Tick) + ")";
                        sb.append(phaseText).append(plugin.ticksToTime(plugin.p4Tick - plugin.p3Tick)).append(bossText).append("</br>");

                        if(plugin.p5Tick > -1) {
                            phaseText = config.phaseNameType() == SpoonNexConfig.PhaseNameTypeMode.NUMBER ? "P5: " : "Zaros: ";
                            sb.append(phaseText).append(plugin.ticksToTime(plugin.p5Tick - plugin.p4Tick)).append("</br>");
                        } 
                    } 
                } 
            }
        } 
        return sb.toString();
    }

    @Override
    public boolean render() {
        return config.killTimer() == SpoonNexConfig.KillTimerMode.INFOBOX;
    }
}
