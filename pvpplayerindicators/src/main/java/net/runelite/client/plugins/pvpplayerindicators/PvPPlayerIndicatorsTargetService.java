package net.runelite.client.plugins.pvpplayerindicators;

import java.awt.Color;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.plugins.pvpplayerindicators.utils.PvpUtil;

@Singleton
public class PvPPlayerIndicatorsTargetService {
    private final Client client;
    private final PvPPlayerIndicatorsConfig config;
    private final PvPPlayerIndicatorsPlugin plugin;

    @Inject
    private PvPPlayerIndicatorsTargetService(Client client, PvPPlayerIndicatorsPlugin plugin, PvPPlayerIndicatorsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    public void forEachPlayer(BiConsumer<Player, Color> consumer) {
        if (config.highlightTargets() == PvPPlayerIndicatorsConfig.TargetHighlightMode.OFF)
            return;
        Player localPlayer = this.client.getLocalPlayer();
        for (Player player : this.client.getPlayers()) {
            if (player == null || player.getName() == null)
                continue;
            if (PvpUtil.isAttackable(this.client, player) && !this.client.isFriended(player.getName(), false) &&
                    !player.isFriendsChatMember() && !player.getName().equals(localPlayer.getName())) {
                consumer.accept(player, config.getTargetColor());
            }
        }
    }
}
