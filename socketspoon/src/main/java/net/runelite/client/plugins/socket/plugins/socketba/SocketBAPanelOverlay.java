package net.runelite.client.plugins.socket.plugins.socketba;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class SocketBAPanelOverlay extends OverlayPanel {
    private SocketBAPlugin plugin;

    private SocketBAConfig config;

    private Client client;

    @Inject
    public SocketBAPanelOverlay(SocketBAPlugin plugin, SocketBAConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
		if (config.roleInfobox()  && !plugin.roleDone && this.client.getVar(Varbits.IN_GAME_BA) == 1) {
			String displayCall = "";
			Color color = Color.WHITE;
			switch (plugin.role) {
				case "Attacker":
					if (plugin.attCall.toLowerCase().contains("aggressive")) {
						displayCall = "Agg/Blunt/Earth";
						color = Color.GREEN;
					} else if (plugin.attCall.toLowerCase().contains("accurate")) {
						displayCall = "Acc/Field/Water";
						color = Color.CYAN;
					} else if (plugin.attCall.toLowerCase().contains("controlled")) {
						displayCall = "Ctrl/Bullet/Wind";
					} else if (plugin.attCall.toLowerCase().contains("defensive")) {
						displayCall = "Def/Barbed/Fire";
						color = Color.RED;
					}
					break;
				case "Defender":
					displayCall = plugin.defCall;
					break;
				case "Healer":
					if (plugin.healCall.toLowerCase().contains("tofu")) {
						displayCall = "Tofu";
					} else if (plugin.healCall.toLowerCase().contains("worms")) {
						displayCall = "Worms";
					} else if (plugin.healCall.toLowerCase().contains("meat")) {
						displayCall = "Schmeat";
					}
					break;
				case "Collector":
					displayCall = plugin.colCall;
					break;
			}

			if (!displayCall.equals("")) {
				this.panelComponent.getChildren().add(TitleComponent.builder()
						.color(color)
						.text(displayCall)
						.build());

				this.panelComponent.setPreferredSize(new Dimension(23 + (displayCall.length() * 5), 24));
			}
		}
        return super.render(graphics);
    }
}
