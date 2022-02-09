package net.runelite.client.plugins.socket.plugins.playerstatus;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.socket.plugins.playerstatus.gametimer.GameIndicator;
import net.runelite.client.plugins.socket.plugins.playerstatus.gametimer.GameTimer;
import net.runelite.client.plugins.socket.plugins.playerstatus.marker.AbstractMarker;
import net.runelite.client.plugins.socket.plugins.playerstatus.marker.IndicatorMarker;
import net.runelite.client.plugins.socket.plugins.playerstatus.marker.TimerMarker;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerStatusOverlay extends Overlay {

    private final Client client;
    private final PlayerStatusPlugin plugin;
    private final PlayerStatusConfig config;

    private final ItemManager itemManager;
    private final SpriteManager spriteManager;

    @Inject
    public PlayerStatusOverlay(Client client, PlayerStatusPlugin plugin, PlayerStatusConfig config, ItemManager itemManager, SpriteManager spriteManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        this.itemManager = itemManager;
        this.spriteManager = spriteManager;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private boolean ignoreMarker(AbstractMarker marker) {
        if (marker == null)
            return true;

        if (marker instanceof IndicatorMarker) {
            GameIndicator indicator = ((IndicatorMarker) marker).getIndicator();
            switch (indicator) {
                case VENGEANCE_ACTIVE:
                    return (config.showVengeanceActive() == PlayerStatusConfig.vengeMode.OFF);
                case SPEC_XFER:
                    return (config.showSpecXfer() == PlayerStatusConfig.xferIconMode.OFF);
                default:
                    return true;
            }
        } else if (marker instanceof TimerMarker) {
            GameTimer timer = ((TimerMarker) marker).getTimer();
            switch (timer) {
                case VENGEANCE:
                    return !config.showVengeanceCooldown();
                case IMBUED_HEART:
                    return !config.showImbuedHeart();
                case OVERLOAD:
                case OVERLOAD_RAID:
                    return !config.showOverload();
                case PRAYER_ENHANCE:
                    return !config.showPrayerEnhance();
                case STAMINA:
                    return !config.showStamina();
                case DIVINE_SCB:
                case DIVINE_ATTACK:
                case DIVINE_STRENGTH:
                case DIVINE_BASTION:
                case DIVINE_RANGE:
                    return !config.showDivines();
                default:
                    return true;
            }
        }

        return true;
    }

    private List<AbstractMarker> renderPlayer(Graphics graphics, Player p, List<AbstractMarker> markers) {
        List<AbstractMarker> toRemove = new ArrayList<>();

        int size = config.getIndicatorSize();
        int margin = config.getIndicatorPadding();
        graphics.setFont(new Font("SansSerif", Font.BOLD, (int) (0.75d * size)));

        Point base = Perspective.localToCanvas(client, p.getLocalLocation(), client.getPlane(), p.getLogicalHeight());
        int zOffset = 0;
        int xOffset = config.getIndicatorXOffset() - (size / 2);

        for (AbstractMarker marker : markers) {
            if (ignoreMarker(marker))
                continue;

            if (marker instanceof TimerMarker) {
                TimerMarker timer = (TimerMarker) marker;
                long elapsedTime = System.currentTimeMillis() - timer.getStartTime();
                double timeRemaining = timer.getTimer().getDuration().toMillis() - elapsedTime;
                if (timeRemaining < 0)
                    toRemove.add(marker);
                else {
                    BufferedImage icon = timer.getImage(size);
                    graphics.drawImage(icon, base.getX() + xOffset, base.getY() + zOffset, null);
                    zOffset += size;

                    int xDelta = icon.getWidth() + margin; // +5 for padding
                    String text;
                    if (timeRemaining > (100 * 1000))
                        text = String.format("%d", (long) (timeRemaining / 1000));
                    else
                        text = String.format("%.1f", timeRemaining / 1000);

                    graphics.setColor(Color.BLACK);
                    graphics.drawString(text, base.getX() + xOffset + xDelta + 1, base.getY() + zOffset);

                    graphics.setColor(Color.WHITE);
                    graphics.drawString(text, base.getX() + xOffset + xDelta, base.getY() + zOffset);
                    zOffset += margin;
                }
            } else if (marker instanceof IndicatorMarker) {
                IndicatorMarker timer = (IndicatorMarker) marker;
                BufferedImage icon = timer.getImage(size);
                graphics.drawImage(icon, base.getX() + xOffset, base.getY() + zOffset, null);
                zOffset += (size + margin);
            }
        }

        return toRemove;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Map<String, List<AbstractMarker>> effects = plugin.getStatusEffects();
        Player p = client.getLocalPlayer();

        List<AbstractMarker> localMarkers = effects.get(null);
        if (localMarkers != null) {
            List<AbstractMarker> toRemove = renderPlayer(graphics, p, localMarkers);

            if (!toRemove.isEmpty()) {
                synchronized (effects) {
                    for (AbstractMarker marker : toRemove)
                        localMarkers.remove(marker);

                    if (localMarkers.isEmpty())
                        effects.remove(null);
                }
            }
        }

        for (Player t : client.getPlayers()) {
            if(config.showSpecXfer() != PlayerStatusConfig.xferIconMode.OFF) {
                for (Map.Entry<String, PlayerStatus> entry : plugin.getPartyStatus().entrySet()) {
                    String name = entry.getKey();
                    PlayerStatus status = entry.getValue();
                    if (name.equals(t.getName())) {
                        System.out.println(plugin.playerNames.size());
                        if(config.showSpecXfer() == PlayerStatusConfig.xferIconMode.ALL ||
                                (config.showSpecXfer() == PlayerStatusConfig.xferIconMode.LIST && plugin.playerNames.contains(t.getName().toLowerCase()))) {
                            int size = config.getIndicatorSize();
                            int margin = config.getIndicatorPadding();
                            graphics.setFont(new Font("SansSerif", Font.BOLD, (int) (0.75d * size)));
                            Point base = Perspective.localToCanvas(client, t.getLocalLocation(), client.getPlane(), t.getLogicalHeight());
                            int zOffset = 0;
                            int xOffset = config.getIndicatorXOffset() - (size / 2);
                            BufferedImage icon = spriteManager.getSprite(SpriteID.SPELL_ENERGY_TRANSFER, 0);
                            zOffset += size;

                            int xDelta = icon.getWidth() + margin; // +5 for padding
                            String text = status.getSpecial() + "%";

                            if(status.getSpecial() <= config.specThreshold()) {
                                graphics.setColor(Color.BLACK);
                                graphics.drawString(text, base.getX() + xOffset + xDelta + 1, base.getY() + zOffset);

                                if (status.getSpecial() > 0) {
                                    graphics.setColor(Color.YELLOW);
                                } else {
                                    graphics.setColor(Color.RED);
                                }
                                graphics.drawString(text, base.getX() + xOffset + xDelta, base.getY() + zOffset);
                                zOffset += margin;
                            }
                        }
                    }
                }
            }

            if (p != t) {
                if(plugin.noSocketVenged.contains(t.getName()) && config.showVengeanceActive() == PlayerStatusConfig.vengeMode.ALL) {
                    Point base = Perspective.localToCanvas(client, t.getLocalLocation(), t.getWorldLocation().getPlane(), t.getLogicalHeight());
                    if(base != null) {
                        int size = config.getIndicatorSize();
                        IndicatorMarker marker = new IndicatorMarker(GameIndicator.VENGEANCE_ACTIVE);
                        marker.setBaseImage(spriteManager.getSprite(GameIndicator.VENGEANCE_ACTIVE.getImageId(), 0));
                        BufferedImage icon = marker.getImage(size);
                        graphics.drawImage(icon, base.getX() + config.getIndicatorXOffset() - (size / 2), base.getY(), null);
                    }
                }

                List<AbstractMarker> markers = effects.get(t.getName());
                if (markers != null) {
                    List<AbstractMarker> toRemove = renderPlayer(graphics, t, markers);

                    if (!toRemove.isEmpty()) {
                        synchronized (markers) {
                            for (AbstractMarker marker : toRemove)
                                markers.remove(marker);

                            if (markers.isEmpty())
                                effects.remove(t.getName());
                        }
                    }
                }
            }
        }
        return null;
    }
}
