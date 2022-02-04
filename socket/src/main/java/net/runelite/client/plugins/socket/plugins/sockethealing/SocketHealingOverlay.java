package net.runelite.client.plugins.socket.plugins.sockethealing;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

public class SocketHealingOverlay extends OverlayPanel {
    private final Client client;

    private final SocketHealingPlugin plugin;

    private final SocketHealingConfig config;

    private ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private SocketHealingOverlay(Client client, SocketHealingPlugin plugin, SocketHealingConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(config.setHighestPriority() ? OverlayLayer.ABOVE_WIDGETS : OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        if (config.healingFontType() == SocketHealingConfig.SocketFontType.CUSTOM) {
            graphics.setFont(new Font(FontManager.getRunescapeFont().toString(), Font.BOLD, config.fontSize()));
        } else if (config.healingFontType() == SocketHealingConfig.SocketFontType.REGULAR) {
            graphics.setFont(FontManager.getRunescapeFont());
        } else if (config.healingFontType() == SocketHealingConfig.SocketFontType.BOLD) {
            graphics.setFont(FontManager.getRunescapeBoldFont());
        } else if (config.healingFontType() == SocketHealingConfig.SocketFontType.SMALL) {
            graphics.setFont(FontManager.getRunescapeSmallFont());
        }

        ArrayList<LocalPoint> playerPoints = new ArrayList<>();
        for (Player p : client.getPlayers()) {
            if(p.getName() != null && plugin.getPartyMembers().containsKey(p.getName())) {
                String playerName = p.getName();
                SocketHealingPlayer player = plugin.getPartyMembers().get(playerName);
                int health = player.getHealth();
                Color highlightColor = Color.WHITE;
                Color textColor;

                if (health > config.greenZone())
                    if (config.separateOpactiy()) {
                        highlightColor = config.greenZoneColor();
                    } else {
                        highlightColor = new Color(config.greenZoneColor().getRed(), config.greenZoneColor().getGreen(), config.greenZoneColor().getBlue(), config.opacity());
                    }
                    textColor = config.greenZoneColor();
                if (health <= config.greenZone() && health > config.orangeZone()) {
                    if (config.separateOpactiy()) {
                        highlightColor = config.orangeZoneColor();
                    } else {
                        highlightColor = new Color(config.orangeZoneColor().getRed(), config.orangeZoneColor().getGreen(), config.orangeZoneColor().getBlue(), config.opacity());
                    }
                    textColor = config.orangeZoneColor();
                } else if (health <= config.orangeZone()) {
                    if (config.separateOpactiy()) {
                        highlightColor = config.redZoneColor();
                    } else {
                        highlightColor = new Color(config.redZoneColor().getRed(), config.redZoneColor().getGreen(), config.redZoneColor().getBlue(), config.opacity());
                    }
                    textColor = config.redZoneColor();
                }

                if ((config.displayHealth() || (!config.hpPlayerNames().equals("") && plugin.playerNames.contains(playerName.toLowerCase())))
                        && (!config.dontShowHp() || health < config.dontShowHpThreshold() || config.dontShowHpMode() == SocketHealingConfig.DontShowHpMode.OUTLINE)) {
                    String text = "";
                    if (config.showName()) {
                        text = playerName + " - " + health;
                    } else {
                        text = String.valueOf(health);
                    }
                    int offsetHp = 0;
                    for (LocalPoint lp : playerPoints) {
                        if (lp.getX() == p.getLocalLocation().getX() && lp.getY() == p.getLocalLocation().getY()) {
                            offsetHp++;
                        }
                    }
                    int xOffset = config.getIndicatorXOffset();
                    int yOffset = config.getIndicatorYOffset();
                    Point point = p.getCanvasTextLocation(graphics, text, config.getIndicatorZOffset());

                    if (point != null) {
                        point = new Point(point.getX() + xOffset, point.getY() - yOffset);
                        if (offsetHp != 0) {
                            int x = point.getX();
                            int y = point.getY() - (15 * offsetHp);
                            point = new Point(x, y);
                        }
                        OverlayUtil.renderTextLocation(graphics, point, text, textColor);
                    }
                    playerPoints.add(p.getLocalLocation());
                }

                if (config.highlightedPlayerNames().toLowerCase().contains(playerName.toLowerCase())
                        && (!config.dontShowHp() || health < config.dontShowHpThreshold() || config.dontShowHpMode() == SocketHealingConfig.DontShowHpMode.TEXT)) {
                    if (config.highlightOutline()) {
                        modelOutlineRenderer.drawOutline(p, config.hpThiCC(), highlightColor, config.glow());
                    } else if (config.highlightHull()) {
                        Shape poly = p.getConvexHull();
                        if (poly != null) {
                            graphics.setColor(highlightColor);
                            graphics.setStroke(new BasicStroke(config.hpThiCC()));
                            graphics.draw(poly);
                            graphics.setColor(new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), 0));
                            graphics.fill(poly);
                        }
                    }
                }
            }
        }
        return null;
    }
}
