package net.runelite.client.plugins.dnightmare;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

import java.awt.*;

public class NightmareInfoBox extends InfoBox {
    private DeoNightmarePlugin plugin;

    private Client client;

    public NightmareInfoBox(Client client, DeoNightmarePlugin plugin) {
        super(ImageUtil.loadImageResource(NightmareInfoBox.class, "nightmare.png"), plugin);
        this.plugin = plugin;
        this.client = client;
    }

    public String getText() {
        String str;
        if (plugin.phase_splits[0] != -1) {
            str = MiscUtil.to_mmss(plugin.phase_splits[0]);
        } else {
            str = MiscUtil.to_mmss(client.getTickCount() - plugin.fight_timer);
        }
        if (plugin.is_timer_dirty())
            str = str + "*";
        return str;
    }

    public Color getTextColor() {
        return (plugin.phase_splits[0] == -1) ? Color.WHITE : Color.GREEN;
    }

    public String getTooltip() {
        StringBuilder builder = new StringBuilder();
        builder.append("Elapsed nightmare time: ");
        if (plugin.phase_splits[0] != -1) {
            builder.append(plugin.to_mmss(plugin.phase_splits[0]));
        } else {
            builder.append(plugin.to_mmss(client.getTickCount() - plugin.fight_timer));
        }
        if (plugin.phase_splits[1] != -1) {
            builder.append("</br>First phase: ");
            builder.append(plugin.to_mmss(plugin.phase_splits[1]));
        }
        if (plugin.phase_splits[2] != -1) {
            builder.append("</br>Second phase: ");
            builder.append(plugin.to_mmss(plugin.phase_splits[2]));
        }
        if (plugin.phase_splits[3] != -1) {
            builder.append("</br>Third phase: ");
            builder.append(plugin.to_mmss(plugin.phase_splits[3]));
        }
        if (plugin.phase_splits[4] != -1) {
            builder.append("</br>Fourth phase: ");
            builder.append(plugin.to_mmss(plugin.phase_splits[4]));
        }
        if (plugin.phase_splits[5] != -1) {
            builder.append("</br>Final phase: ");
            builder.append(plugin.to_mmss(plugin.phase_splits[5]));
        }
        return builder.toString();
    }
}
