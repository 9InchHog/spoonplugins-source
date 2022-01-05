package net.runelite.client.plugins.spoonkilltimers;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

public class SpoonKillTimersOverlay extends OverlayPanel {
    private final Client client;

    private final SpoonKillTimersPlugin plugin;

    private final SpoonKillTimersConfig config;

    @Inject
    private SpoonKillTimersOverlay(Client client, SpoonKillTimersPlugin plugin, SpoonKillTimersConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.setPriority(OverlayPriority.HIGH);
        this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.getChildren().clear();
        if (this.plugin.box != null && this.plugin.timer != null && this.config.timerMode() == SpoonKillTimersConfig.timerMode.PANEL) {
            String str;
            if(this.plugin.timer.ticks < 0){
                str = this.plugin.tommss(0);
            }else{
                str = this.plugin.tommss(this.plugin.timer.ticks);
            }

            this.panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(this.plugin.timer.name) + 55, 0));
            this.panelComponent.getChildren().add(LineComponent.builder()
                    .left(this.plugin.timer.name)
                    .right(str)
                    .build());
        }
        return super.render(graphics);
    }
}
