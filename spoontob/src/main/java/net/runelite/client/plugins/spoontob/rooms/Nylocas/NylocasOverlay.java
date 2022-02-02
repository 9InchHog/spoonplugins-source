package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import com.google.common.base.Strings;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.spoontob.RoomOverlay;
import net.runelite.client.plugins.spoontob.SpoonTobConfig;
import net.runelite.client.plugins.spoontob.SpoonTobPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;

public class NylocasOverlay extends RoomOverlay {
    @Inject
    private Nylocas nylocas;

    @Inject
    private SpoonTobPlugin plugin;

    @Inject
    protected NylocasOverlay(SpoonTobConfig config) {
        super(config);
    }

    public Dimension render(Graphics2D graphics) {
        if(nylocas.isInNyloRegion() && client.getLocalPlayer() != null){
            Player player = client.getLocalPlayer();
            Point point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 60);
            if (nylocas.isInstanceTimerRunning() && config.entryInstanceTimer() == SpoonTobConfig.instancerTimerMode.OVERHEAD && point != null) {
                renderTextLocation(graphics, String.valueOf(nylocas.getInstanceTimer()), Color.CYAN, point);
            }

            if (config.showPhaseChange() != SpoonTobConfig.nyloBossPhaseChange.OFF && nylocas.getBossChangeTicks() > 0) {
                drawNylocas(graphics);
            }

            if (nylocas.isNyloActive()) {
                if (config.nyloPillars()) {
                    Color c;
                    HashMap<NPC, Integer> npcMap = nylocas.getNylocasPillars();
                    for (NPC npc : npcMap.keySet()) {
                        int health = npcMap.get(npc);
                        String healthStr = health + "%";
                        WorldPoint p = npc.getWorldLocation();
                        LocalPoint lp = LocalPoint.fromWorld(client, p.getX() + 1, p.getY() + 1);
                        if (config.oldHpThreshold()) {
                            c = plugin.oldHitpointsColor(health);
                        } else {
                            c = plugin.calculateHitpointsColor(health);
                        }
                        if (lp != null) {
                            Point canvasPoint = Perspective.localToCanvas(client, lp, client.getPlane(), 65);
                            if (config.fontStyle()) {
                                renderTextLocation(graphics, healthStr, c, canvasPoint);
                            } else {
                                renderSteroidsTextLocation(graphics, healthStr, 13, Font.BOLD, c, canvasPoint);
                            }
                        }
                    }
                }

                if (config.showNylocasExplosions() != SpoonTobConfig.ExplosionWarning.OFF || config.getHighlightMageNylo() || config.getHighlightMeleeNylo()
                        || config.getHighlightRangeNylo() || config.nyloAggressiveOverlay()) {
                    HashMap<NPC, Integer> npcMap = nylocas.getNylocasNpcs();
				    int meleeIndex = 0;
                    int rangeIndex = 0;
                    int mageIndex = 0;

                    for (NPC npc : npcMap.keySet()) {
                        String name = npc.getName();
                        LocalPoint lp = npc.getLocalLocation();

                        if(!npc.isDead()){
                            if (config.nyloAggressiveOverlay() && nylocas.getAggressiveNylocas().contains(npc) && lp != null) {
                                Polygon poly = getCanvasTileAreaPoly(client, lp, npc.getComposition().getSize(), -25);
                                renderPoly(graphics, Color.RED, poly, config.nyloTileWidth());
                            }

                            int ticksLeft = npcMap.get(npc);
                            if (ticksLeft > -1 && ticksLeft <= config.nyloExplosionDisplayTicks()) {
                                int ticksAlive = ticksLeft;
                                if (config.nyloTimeAliveCountStyle() == SpoonTobConfig.nylotimealive.COUNTUP)
                                {
                                    ticksAlive = 52 - ticksLeft;
                                }

                                Point textLocation = npc.getCanvasTextLocation(graphics, String.valueOf(ticksAlive), 60);
                                if ((config.showNylocasExplosions() == SpoonTobConfig.ExplosionWarning.BOTH || config.showNylocasExplosions() == SpoonTobConfig.ExplosionWarning.TILE)
                                        && ticksLeft <= 6 && lp != null && npc.getComposition() != null) {
                                    if(config.nyloExplosionType() == SpoonTobConfig.nyloExplosionType.TILE){
                                        renderPoly(graphics, Color.YELLOW, getCanvasTileAreaPoly(client, lp, npc.getComposition().getSize(), -15), config.nyloTileWidth());
                                    } else {
                                        renderPoly(graphics, Color.YELLOW, getCanvasTileAreaPoly(client, lp, npc.getComposition().getSize() + 4, 0), config.nyloTileWidth());
                                    }
                                }

                                if(textLocation != null){
                                    if ((config.showNylocasExplosions() == SpoonTobConfig.ExplosionWarning.BOTH || config.showNylocasExplosions() == SpoonTobConfig.ExplosionWarning.TICKS)) {
                                        if ((ticksAlive >= 44 && config.nyloTimeAliveCountStyle() == SpoonTobConfig.nylotimealive.COUNTUP)
                                                || (ticksAlive <= 8 && config.nyloTimeAliveCountStyle() == SpoonTobConfig.nylotimealive.COUNTDOWN)) {
                                            if (config.fontStyle()){
                                                renderTextLocation(graphics, String.valueOf(ticksAlive), Color.RED, textLocation);
                                            }else {
                                                renderSteroidsTextLocation(graphics, String.valueOf(ticksAlive), 13, Font.BOLD, Color.RED, textLocation);
                                            }
                                        } else {
                                            if (config.fontStyle()){
                                                renderTextLocation(graphics, String.valueOf(ticksAlive), Color.WHITE, textLocation);
                                            } else {
                                                renderSteroidsTextLocation(graphics, String.valueOf(ticksAlive), 13, Font.BOLD, Color.WHITE, textLocation);
                                            }
                                        }
                                    }
                                }
                            }


                            Color nyloColor = Color.WHITE;
                            if(name != null && lp != null) {
                                if (config.getHighlightMeleeNylo() && name.equals("Nylocas Ischyros")) {
                                    if (config.raveNylo()) {
                                        nyloColor = nylocas.meleeNyloRaveColors.get(meleeIndex);
                                        meleeIndex++;
                                    } else {
                                        nyloColor = new Color(255, 188, 188);
                                    }
                                } else if (config.getHighlightRangeNylo() && name.equals("Nylocas Toxobolos")) {
                                    if (config.raveNylo()) {
                                        nyloColor = nylocas.rangeNyloRaveColors.get(rangeIndex);
                                        rangeIndex++;
                                    } else {
                                        nyloColor = Color.GREEN;
                                    }
                                } else if (config.getHighlightMageNylo() && name.equals("Nylocas Hagios")) {
                                    if (config.raveNylo()) {
                                        nyloColor = nylocas.mageNyloRaveColors.get(mageIndex);
                                        mageIndex++;
                                    } else {
                                        nyloColor = Color.CYAN;
                                    }
                                }

                                if (nyloColor != Color.WHITE) {
                                    renderPoly(graphics, nyloColor, Perspective.getCanvasTileAreaPoly(client, lp, npc.getComposition().getSize()), config.nyloTileWidth());
                                }
                            }
                        }
                    }
                }

                if((config.waveSpawnTimer() == SpoonTobConfig.waveSpawnTimerMode.OVERLAY || config.waveSpawnTimer() == SpoonTobConfig.waveSpawnTimerMode.BOTH)
                        && client.getLocalPlayer() != null && nylocas.nyloWave < 31 && nylocas.waveSpawnTicks > -1) {
                    String text = String.valueOf(nylocas.waveSpawnTicks);
                    LocalPoint eastLp = LocalPoint.fromWorld(client, WorldPoint.fromRegion(client.getLocalPlayer().getWorldLocation().getRegionID(), 42, 25, client.getLocalPlayer().getWorldLocation().getPlane()));
                    LocalPoint westLp = LocalPoint.fromWorld(client, WorldPoint.fromRegion(client.getLocalPlayer().getWorldLocation().getRegionID(), 5, 25, client.getLocalPlayer().getWorldLocation().getPlane()));
                    LocalPoint southLp = LocalPoint.fromWorld(client, WorldPoint.fromRegion(client.getLocalPlayer().getWorldLocation().getRegionID(), 24, 6, client.getLocalPlayer().getWorldLocation().getPlane()));

                    Color color = config.waveSpawnTimerColor();
                    if (nylocas.stalledWave) {
                        color = Color.RED;
                    }

                    if (config.fontStyle()) {
                        if (eastLp != null)
                            renderTextLocation(graphics, text, color, Perspective.getCanvasTextLocation(client, graphics, eastLp, text, 0));
                        if (westLp != null)
                            renderTextLocation(graphics, text, color, Perspective.getCanvasTextLocation(client, graphics, westLp, text, 0));
                        if (southLp != null)
                            renderTextLocation(graphics, text, color, Perspective.getCanvasTextLocation(client, graphics, southLp, text, 0));
                    } else {
                        if (eastLp != null)
                            renderSteroidsTextLocation(graphics, text, 14, 1, color, Perspective.getCanvasTextLocation(client, graphics, eastLp, text, 0));
                        if (westLp != null)
                            renderSteroidsTextLocation(graphics, text, 14, 1, color, Perspective.getCanvasTextLocation(client, graphics, westLp, text, 0));
                        if (southLp != null)
                            renderSteroidsTextLocation(graphics, text, 14, 1, color, Perspective.getCanvasTextLocation(client, graphics, southLp, text, 0));
                    }
                }
            }

            if (config.showBigSplits()) {
                nylocas.getSplitsMap().forEach((npc, ticks) -> {
                    Polygon poly = Perspective.getCanvasTileAreaPoly(client, npc.getLocalLocation(), 2);
                    if (poly != null)
                    {
                        renderPolygon(graphics, poly, config.bigsColor());
                    }

                    Point textLocation = Perspective.getCanvasTextLocation(client, graphics, npc.getLocalLocation(), "#", 0);
                    if (textLocation != null) {
                        if (config.fontStyle()) {
                            renderTextLocation(graphics, Integer.toString(ticks), Color.WHITE, textLocation);
                        } else {
                            renderTextLocation(graphics, textLocation, Integer.toString(ticks), Color.WHITE, 13, Font.BOLD);
                        }
                    }
                });
            }

        }
        return null;
    }

    public void drawNylocas(Graphics2D graphics) {
		NPC npc = null;
		if(nylocas.minibossAlive && nylocas.nyloMiniboss != null && config.showPhaseChange() == SpoonTobConfig.nyloBossPhaseChange.BOTH){
			npc = nylocas.nyloMiniboss;
		}else if(nylocas.getNylocasBoss() != null){
			npc = nylocas.getNylocasBoss();
		}
		
        if (npc != null){
			LocalPoint lp = npc.getLocalLocation();
			if (lp != null) {
				String str = Integer.toString(nylocas.getBossChangeTicks());
				Point loc = Perspective.getCanvasTextLocation(client, graphics, lp, str, 0);
				if (loc != null) {
					if (config.fontStyle()){
						renderTextLocation(graphics, str, Color.WHITE, loc);
					} else {
						renderSteroidsTextLocation(graphics, str, 14, Font.BOLD, Color.WHITE, loc);
					}
				}
			}
		}
    }

    protected void renderTextLocation(Graphics2D graphics, @Nullable Point txtLoc, @Nullable String text, @Nonnull Color color , int fontSize, int fontStyle) {
        if (txtLoc != null && !Strings.isNullOrEmpty(text)) {
            graphics.setFont(new Font("Arial", fontStyle, fontSize));
            int x = txtLoc.getX();
            int y = txtLoc.getY();
            graphics.setColor(Color.BLACK);
            graphics.drawString(text, x, y + 1);
            graphics.drawString(text, x, y - 1);
            graphics.drawString(text, x + 1, y);
            graphics.drawString(text, x - 1, y);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
            graphics.drawString(text, x, y);
        }
    }

    protected void renderPolygon(Graphics2D graphics, @Nullable Shape polygon, @Nonnull Color color) {
        if (polygon != null) {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
            graphics.fill(polygon);
        }
    }
}