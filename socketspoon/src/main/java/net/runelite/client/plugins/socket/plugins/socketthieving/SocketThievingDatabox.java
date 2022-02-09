package net.runelite.client.plugins.socket.plugins.socketthieving;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

import net.runelite.api.Client;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class SocketThievingDatabox extends OverlayPanel {
    private SocketThievingPlugin plugin;

    private SocketThievingConfig config;

    private Client client;

    @Inject
    public SocketThievingDatabox(SocketThievingPlugin plugin, SocketThievingConfig config, Client client) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        int myindex;
        if (plugin.gc_local == null || plugin.gc_local.num_opened == 0) {
            myindex = -1;
        } else {
            myindex = Arrays.binarySearch(plugin.gc_others, 0, plugin.gc_others_count,
                    plugin.gc_local, plugin.comparator);
            if (myindex < 0)
                myindex = -myindex - 1;
        }
        int sum_grubs = plugin.num_grubs;
        for (int i = 0; i < plugin.gc_others_count; i++)
            sum_grubs += (plugin.gc_others[i]).num_with_grubs * config.grubRate() / 100;
        List<LayoutableRenderableEntity> elems = panelComponent.getChildren();
        elems.clear();
        elems.add(LineComponent.builder()
                .leftColor(Color.WHITE)
                .left("Grub count: ")
                .right(plugin.socketGrubs + " (" + (int) Math.floor(plugin.nonSocketGrubs) + ")")
                .build());

        if(plugin.roomtype == 13){
            if (config.displayMinGrubs()){
                if (plugin.teamTotalGrubs >= plugin.teamGrubsNeeded) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Dump Amt: ")
                            .rightColor(Color.GREEN)
                            .right(plugin.teamTotalGrubs + "/" + plugin.teamGrubsNeeded)
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Dump Amt: ")
                            .right(plugin.teamTotalGrubs + "/" + plugin.teamGrubsNeeded)
                            .build());
                }
            }else{
                if (plugin.teamTotalGrubs >= plugin.teamGrubsNeeded) {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Dump Amt: ")
                            .rightColor(Color.GREEN)
                            .right(String.valueOf(plugin.teamTotalGrubs))
                            .build());
                } else {
                    elems.add(LineComponent.builder()
                            .leftColor(Color.WHITE)
                            .left("Dump Amt: ")
                            .right(String.valueOf(plugin.teamTotalGrubs))
                            .build());
                }
            }
        }

        for (int j = 0; j < plugin.gc_others_count; j++) {
            if (j == myindex) {
                add_gc_line(elems, plugin.gc_local, true);
            }
            if(plugin.socketPlayerNames.contains(plugin.gc_others[j].displayname.toLowerCase())){
                add_gc_line(elems, plugin.gc_others[j], true);
            }else{
                add_gc_line(elems, plugin.gc_others[j], false);
            }
        }
        if (myindex == plugin.gc_others_count)
            add_gc_line(elems, plugin.gc_local, true);
        return super.render(graphics);
    }

    private void add_gc_line(List<LayoutableRenderableEntity> elems, SocketThievingPlugin.GrubCollection gc, boolean socket) {
        if(socket){
            elems.add(LineComponent.builder()
                    .left("\u2713 " + gc.displayname)
                    .right(gc.num_with_grubs + "/" + gc.num_opened).build());
        }else{
            elems.add(LineComponent.builder()
                    .left("\u2717 " + gc.displayname)
                    .right(gc.num_with_grubs + "/" + gc.num_opened).build());
        }
    }
}
