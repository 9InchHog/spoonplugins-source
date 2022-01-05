package net.runelite.client.plugins.spoonkilltimers;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class KillTimerInfoBox extends InfoBox {
    private SpoonKillTimersPlugin plugin;

    private Client client;

    public KillTimerInfoBox(BufferedImage img, Client client, SpoonKillTimersPlugin plugin) {
        super(img, plugin);
        this.plugin = plugin;
        this.client = client;
    }

    public String getText() {
        String str;
        if(this.plugin.timer.ticks < 0){
            str = this.plugin.tommss(0);
        }else{
            str = this.plugin.tommss(this.plugin.timer.ticks);
        }
        return str;
    }

    public Color getTextColor() {
        return Color.WHITE;
    }

    public String getTooltip() {
        return "Elapsed " + this.plugin.timer.name + " time: " + this.plugin.tommss(this.plugin.timer.ticks);
    }
}
