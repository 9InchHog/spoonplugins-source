package net.runelite.client.plugins.coxfloorsplits;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class CoxFloorSplitsOverlay extends OverlayPanel
{
    private static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");
    private static final DecimalFormat POINTS_PERCENT_FORMAT = new DecimalFormat(" (##0.00%)");
    private static final DecimalFormat UNIQUE_FORMAT = new DecimalFormat("#0.00%");

    private Client client;
    private CoxFloorSplitsPlugin plugin;
    private CoxFloorSplitsConfig config;
    private TooltipManager tooltipManager;

    @Inject
    private CoxFloorSplitsOverlay(Client client,
                                  CoxFloorSplitsPlugin plugin,
                                  CoxFloorSplitsConfig config,
                                  TooltipManager tooltipManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.tooltipManager = tooltipManager;
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInRaidChambers()) {
            return null;
        }

        int totalPoints = client.getVar(Varbits.TOTAL_POINTS);
        int personalPoints = client.getVar(Varbits.PERSONAL_POINTS);
        int partySize = client.getVar(Varbits.RAID_PARTY_SIZE);
        FontMetrics metrics = graphics.getFontMetrics();

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total:")
                .right(POINTS_FORMAT.format(totalPoints))
                .build());
        String personalPointsString = POINTS_FORMAT.format(personalPoints);

        if (partySize > 1) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(client.getLocalPlayer().getName() + ":")
                    .right(personalPointsString)
                    .build());
        }

        if (config.raidsTimer()) {
            if(!plugin.olmTime.equals("")){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time:")
                        .rightColor(Color.GREEN)
                        .right(plugin.getTime())
                        .build());
            }else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Time:")
                        .right(plugin.getTime())
                        .build());
            }
        }

        if(!config.showFloorSplits()) {
            if (config.displayOlm() && plugin.lowerTime != -1 && plugin.olmTime.equals("")) {
                int seconds = (int) Math.floor(client.getVarbitValue(6386) * .6);
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .left("Olm: ")
                        .right(plugin.secondsToTime(seconds - plugin.olmStart))
                        .build());
            }
        }else {
            if(plugin.upperTime != -1){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Floor 1: ")
                        .right(this.plugin.upperFloorTime)
                        .build());
            }

            if(plugin.middleTime != -1){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Floor 2: ")
                        .right(this.plugin.middleFloorTime)
                        .build());
            }

            if(plugin.lowerTime != -1){
                if(plugin.middleTime != -1){
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Floor 3: ")
                            .right(this.plugin.lowerFloorTime)
                            .build());
                }else {
                    this.panelComponent.getChildren().add(LineComponent.builder()
                            .left("Floor 2: ")
                            .right(this.plugin.lowerFloorTime)
                            .build());
                }
            }

            if(config.displayOlm() && plugin.lowerTime != -1 && plugin.olmTime.equals("")) {
                int seconds = (int) Math.floor(client.getVarbitValue(6386) * .6);
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .left("Olm: ")
                        .right(plugin.secondsToTime(seconds - plugin.olmStart))
                        .build());
            }

            if(!plugin.olmTime.equals("")){
                this.panelComponent.getChildren().add(LineComponent.builder()
                        .left("Olm: ")
                        .right(this.plugin.olmTime)
                        .build());
            }
        }

        final Rectangle bounds = this.getBounds();
        if (bounds.getX() > 0)
        {
            final Point mousePosition = client.getMouseCanvasPosition();

            if (bounds.contains(mousePosition.getX(), mousePosition.getY()))
            {
                String tooltip = plugin.getTooltip();

                if (tooltip != null && !config.showFloorSplits())
                {
                    tooltipManager.add(new Tooltip(tooltip));
                }
            }
        }
        return super.render(graphics);
    }
}
