package net.runelite.client.plugins.bingo;

import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BingoOverlay extends OverlayPanel {
    private BingoConfig config;

    private BingoPlugin plugin;

    @Inject
    private BingoOverlay(BingoPlugin plugin, BingoConfig config) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Bingo TeamCode Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YY");
        SimpleDateFormat formatterr = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatterrr = new SimpleDateFormat("MM/dd/YY");
        Date date = new Date();
        if (!config.getTeamCode().equals(""))
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(config.getTeamCode())
                    .color(config.getTextColour())
                    .build());
        if (config.getFreedomUnits()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(formatterrr.format(date))
                    .right(formatterr.format(date))
                    .leftColor(config.getTextColour())
                    .rightColor(config.getTextColour())
                    .build());
        } else {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(formatter.format(date))
                    .right(formatterr.format(date))
                    .leftColor(config.getTextColour())
                    .rightColor(config.getTextColour())
                    .build());
        }
        int width = graphics.getFontMetrics().stringWidth(formatter.format(date) + formatterr.format(date) + '\n');
        panelComponent.setPreferredSize(new Dimension(width, 0));
        return super.render(graphics);
    }
}
