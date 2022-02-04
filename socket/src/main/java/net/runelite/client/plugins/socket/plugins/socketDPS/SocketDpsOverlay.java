package net.runelite.client.plugins.socket.plugins.socketDPS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.ws.PartyService;

class SocketDpsOverlay extends OverlayPanel {
    static final OverlayMenuEntry RESET_ENTRY = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "DPS counter");
    private final SocketDpsCounterPlugin plugin;
    private final SocketDpsConfig config;
    private final Client client;

    @Inject
    SocketDpsOverlay(SocketDpsCounterPlugin socketDpsCounterPlugin, SocketDpsConfig socketDpsConfig, PartyService partyService, Client client) {
        super(socketDpsCounterPlugin);
        this.plugin = socketDpsCounterPlugin;
        this.config = socketDpsConfig;
        this.client = client;
        getMenuEntries().add(RESET_ENTRY);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.displayOverlay() && !plugin.getMembers().isEmpty() && client.getLocalPlayer() != null) {
            Map<String, Integer> dpsMembers = plugin.getMembers();
            panelComponent.getChildren().clear();
            int tot = 0;
            String localName = client.getLocalPlayer().getName();
            if (dpsMembers.containsKey("Total")) {
                tot = dpsMembers.get("Total");
                dpsMembers.remove("Total");
            }

            int maxWidth = 129;
            dpsMembers.forEach((k, v) -> {
                String right = QuantityFormatter.formatNumber(v);
                if (k.equalsIgnoreCase(client.getLocalPlayer().getName())) {
                    Color color = config.highlightSelf() ? Color.GREEN : Color.WHITE;
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(color)
                            .left(k)
                            .rightColor(color)
                            .right(right)
                            .build());
                } else if (config.highlightOtherPlayer() && plugin.getHighlights().contains(k.toLowerCase())) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(config.getHighlightColor())
                            .left(k)
                            .rightColor(config.getHighlightColor())
                            .right(right)
                            .build());
                } else {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left(k)
                            .right(right)
                            .build());
                }
            });

            panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));
            dpsMembers.put("Total", tot);
            if (localName != null && dpsMembers.containsKey(localName) && tot > dpsMembers.get(localName) && config.showTotal()) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.RED)
                        .left("Total")
                        .rightColor(Color.RED)
                        .right(dpsMembers.get("Total").toString())
                        .build());
            }

            if (config.backgroundStyle() == SocketDpsConfig.backgroundMode.HIDE) {
                panelComponent.setBackgroundColor(null);
            } else if (config.backgroundStyle() == SocketDpsConfig.backgroundMode.STANDARD) {
                panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
            } else if (config.backgroundStyle() == SocketDpsConfig.backgroundMode.CUSTOM) {
                panelComponent.setBackgroundColor(new Color(config.backgroundColor().getRed(), config.backgroundColor().getGreen(), config.backgroundColor().getBlue(), config.backgroundColor().getAlpha()));
            }
            return panelComponent.render(graphics);
        }
        return null;
    }
}
