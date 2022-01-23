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
        if (this.config.displayOverlay() && !this.plugin.getMembers().isEmpty() && this.client.getLocalPlayer() != null) {
            Map<String, Integer> dpsMembers = this.plugin.getMembers();
            this.panelComponent.getChildren().clear();
            int tot = 0;
            String localName = this.client.getLocalPlayer().getName();
            if (dpsMembers.containsKey("Total")) {
                tot = dpsMembers.get("Total");
                dpsMembers.remove("Total");
            }

            int maxWidth = 129;
            dpsMembers.forEach((k, v) -> {
                String right = QuantityFormatter.formatNumber(v);
                if (k.equalsIgnoreCase(this.client.getLocalPlayer().getName())) {
                    Color color = this.config.highlightSelf() ? Color.GREEN : Color.WHITE;
                    this.panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(color)
                            .left(k)
                            .rightColor(color)
                            .right(right)
                            .build());
                } else if (this.config.highlightOtherPlayer() && this.plugin.getHighlights().contains(k.toLowerCase())) {
                    this.panelComponent.getChildren().add(LineComponent.builder()
                            .leftColor(this.config.getHighlightColor())
                            .left(k)
                            .rightColor(this.config.getHighlightColor())
                            .right(right)
                            .build());
                } else {
                    this.panelComponent.getChildren().add(LineComponent.builder()
                            .left(k)
                            .right(right)
                            .build());
                }
            });

            this.panelComponent.setPreferredSize(new Dimension(maxWidth + 10, 0));
            dpsMembers.put("Total", tot);
            if (localName != null && dpsMembers.containsKey(localName) && tot > dpsMembers.get(localName) && this.config.showTotal()) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.RED)
                        .left("Total")
                        .rightColor(Color.RED)
                        .right(dpsMembers.get("Total").toString())
                        .build());
            }

            if (this.config.backgroundStyle() == SocketDpsConfig.backgroundMode.HIDE) {
                panelComponent.setBackgroundColor(null);
            } else if (this.config.backgroundStyle() == SocketDpsConfig.backgroundMode.STANDARD) {
                panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
            } else if (this.config.backgroundStyle() == SocketDpsConfig.backgroundMode.CUSTOM) {
                panelComponent.setBackgroundColor(new Color(config.backgroundColor().getRed(), config.backgroundColor().getGreen(), config.backgroundColor().getBlue(), config.backgroundColor().getAlpha()));
            }
            return this.panelComponent.render(graphics);
        }
        return null;
    }
}
