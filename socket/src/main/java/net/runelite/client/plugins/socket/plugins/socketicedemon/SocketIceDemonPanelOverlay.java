package net.runelite.client.plugins.socket.plugins.socketicedemon;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;

public class SocketIceDemonPanelOverlay extends OverlayPanel {
    private SocketIceDemonPlugin plugin;

    private SocketIceDemonConfig config;

    private Client client;

    @Inject
    public SocketIceDemonPanelOverlay(SocketIceDemonPlugin plugin, SocketIceDemonConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();

        if(config.showTeamKindling()) {
            if (plugin.teamTotalKindlingCut >= plugin.teamKindlingNeeded) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Cut: ")
                        .rightColor(Color.GREEN)
                        .right(plugin.teamTotalKindlingCut + "/" + plugin.teamKindlingNeeded)
                        .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Cut: ")
                        .right(plugin.teamTotalKindlingCut + "/" + plugin.teamKindlingNeeded)
                        .build());
            }

            if (plugin.teamTotalKindlingLit >= plugin.teamKindlingNeeded) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Lit: ")
                        .rightColor(Color.GREEN)
                        .right(plugin.teamTotalKindlingLit + "/" + plugin.teamKindlingNeeded)
                        .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Lit: ")
                        .right(plugin.teamTotalKindlingLit + "/" + plugin.teamKindlingNeeded)
                        .build());
            }
        }else {
            if (plugin.teamTotalKindlingCut >= plugin.teamKindlingNeeded) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Cut: ")
                        .rightColor(Color.GREEN)
                        .right(String.valueOf(plugin.teamTotalKindlingCut))
                        .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Cut: ")
                        .right(String.valueOf(plugin.teamTotalKindlingCut))
                        .build());
            }

            if (plugin.teamTotalKindlingLit >= plugin.teamKindlingNeeded) {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Lit: ")
                        .rightColor(Color.GREEN)
                        .right(String.valueOf(plugin.teamTotalKindlingLit))
                        .build());
            } else {
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("Lit: ")
                        .right(String.valueOf(plugin.teamTotalKindlingLit))
                        .build());
            }
        }

        if(config.showNames()){
            for(String name : plugin.playerNameList){
                int index = plugin.playerNameList.indexOf(name);
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .leftColor(Color.WHITE)
                        .left("    " + name + ":")
                        .right(String.valueOf(plugin.playerKindlingList.get(index)))
                        .build());
            }
        }

        this.panelComponent.getChildren().add(LineComponent.builder()
                .leftColor(Color.WHITE)
                .left("Braziers: ")
                .right(String.valueOf(plugin.litBraziers))
                .build());

        return super.render(graphics);
    }
}
