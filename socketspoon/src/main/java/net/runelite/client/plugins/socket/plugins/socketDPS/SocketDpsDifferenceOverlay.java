package net.runelite.client.plugins.socket.plugins.socketDPS;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.ws.PartyService;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class SocketDpsDifferenceOverlay extends OverlayPanel {
    static final OverlayMenuEntry RESET_ENTRY = new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset", "DPS counter");
    private final SocketDpsCounterPlugin plugin;
    private final SocketDpsConfig config;
    private final Client client;

    @Inject
    SocketDpsDifferenceOverlay(SocketDpsCounterPlugin socketDpsCounterPlugin, SocketDpsConfig socketDpsConfig, PartyService partyService, Client client) {
        super(socketDpsCounterPlugin);
        this.plugin = socketDpsCounterPlugin;
        this.config = socketDpsConfig;
        this.client = client;
        getMenuEntries().add(RESET_ENTRY);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.showDifference() && client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null) {
            this.panelComponent.getChildren().clear();
            AtomicReference<String> highlightedPlayer = new AtomicReference<>("");
            int difference = 0;

            if (!this.plugin.getMembers().isEmpty()) {
                Map<String, Integer> dpsMembers = this.plugin.getMembers();
                dpsMembers.forEach((k, v) -> {
                    if (config.boostedPlayerName().equalsIgnoreCase(k.toLowerCase())) {
                        highlightedPlayer.set(k);
                    }
                });

                int personalDmg = dpsMembers.get(client.getLocalPlayer().getName()) != null ? dpsMembers.get(client.getLocalPlayer().getName()) : 0;
                int boostedDmg = dpsMembers.get(highlightedPlayer.toString()) != null ? dpsMembers.get(highlightedPlayer.toString()) : 0;
                difference = boostedDmg - personalDmg;
                Color color;
                if(config.isMain()) {
                    color = Color.WHITE;
                } else if (difference < config.lateWarning()) {
                    color = Color.RED;
                } else if (difference < config.earlyWarning()) {
                    color = Color.ORANGE;
                } else {
                    color = Color.GREEN;
                }

                this.panelComponent.getChildren().add(TitleComponent.builder()
                        .color(color)
                        .text(QuantityFormatter.formatNumber(difference))
                        .build());
            } else {
                this.panelComponent.getChildren().add(TitleComponent.builder()
                        .color(config.isMain() ? Color.WHITE : Color.RED)
                        .text(String.valueOf(0))
                        .build());
            }
            this.panelComponent.setPreferredSize(new Dimension(55, 0));

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
