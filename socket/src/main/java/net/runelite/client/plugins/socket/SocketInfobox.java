package net.runelite.client.plugins.socket;

import lombok.Getter;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class SocketInfobox extends InfoBox {

    private String status;
    private final SocketConfig config;
    private final SocketPlugin plugin;


    SocketInfobox(BufferedImage image, SocketConfig config, SocketPlugin plugin, String status) {
        super(image, plugin);
        this.config = config;
        this.plugin = plugin;
        this.status = status;
        setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText()
    {
        return "";
    }

    @Override
    public Color getTextColor() {
        Color color = Color.WHITE;
        switch (plugin.connection.getState()) {
            case CONNECTED:
                color = Color.GREEN;
                break;
            case CONNECTING:
                color = Color.YELLOW;
                break;
            case TERMINATED:
            case DISCONNECTED:
                color = Color.RED;
                break;
        }
         return color;
    }

    @Override
    public String getTooltip() {
        Color color = getTextColor();
        switch (plugin.connection.getState()) {
            case CONNECTED:
                status = ColorUtil.wrapWithColorTag("Connected", color);
                break;
            case CONNECTING:
                status = ColorUtil.wrapWithColorTag("Connecting...", color);
                break;
            case TERMINATED:
            case DISCONNECTED:
                status = ColorUtil.wrapWithColorTag("Disconnected", color);
                break;
        }
        return status;
    }

    @Override
    public boolean render() {
        return config.infobox();
    }
}
