package net.runelite.client.plugins.socket.plugins.socketdefence;

import java.awt.Color;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import javax.inject.Inject;

@ToString
public class DefenceInfoBox extends InfoBox {
    @Inject
    private final SocketDefenceConfig config;

    @Getter
    @Setter
    private long count;

    public DefenceInfoBox(BufferedImage image, Plugin plugin, long count, SocketDefenceConfig config) {
        super(image, plugin);
        this.count = count;
        this.config = config;
    }

    @Override
    public String getText() {
        return Long.toString(getCount());
    }

    @Override
    public Color getTextColor() {
        if (count == 0) {
            return Color.GREEN;
        } else if (count >= 1 && count <= this.config.lowDef()) {
            return Color.YELLOW;
        } else{
            return Color.WHITE;
        }
    }
}