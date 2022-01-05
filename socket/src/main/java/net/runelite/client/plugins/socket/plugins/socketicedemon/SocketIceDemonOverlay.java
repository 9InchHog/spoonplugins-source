package net.runelite.client.plugins.socket.plugins.socketicedemon;

import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

import java.awt.*;

public class SocketIceDemonOverlay extends OverlayPanel {
	private SocketIceDemonPlugin plugin;

	private SocketIceDemonConfig config;

	private Client client;

	@Inject
	public SocketIceDemonOverlay(SocketIceDemonPlugin plugin, SocketIceDemonConfig config, Client client) {
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics) {
		if (config.highlightUnlitBrazier()) {
			if(plugin.unlitBrazierList.size() > 0 && plugin.roomtype == 12 && !plugin.iceDemonActive){
				renderTileObjects(graphics);
			}
		}

		if (config.iceDemonSpawnTicks() && plugin.iceDemon != null && plugin.iceDemonActivateTicks > 0 && plugin.iceDemonActive && plugin.roomtype == 12) {
			String text = String.valueOf(plugin.iceDemonActivateTicks);
			Point textLoc = plugin.iceDemon.getCanvasTextLocation(graphics, text, 50);
			Font oldFont = graphics.getFont();
			graphics.setFont(FontManager.getRunescapeBoldFont());
			Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
			OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, textLoc, text, Color.RED);
			graphics.setFont(oldFont);
		}

		if(config.iceDemonHp() && plugin.iceDemon != null && !plugin.iceDemonActive && plugin.roomtype == 12){
			String str;
			Font oldFont = graphics.getFont();
			graphics.setFont(FontManager.getRunescapeBoldFont());
			Color textColor = Color.WHITE;
			float floatRatio = 0;

			if (this.client.getVarbitValue(5424) == 1) {
				floatRatio = ((float) plugin.iceDemon.getHealthRatio() / (float) plugin.iceDemon.getHealthScale()) * 100;
				if(floatRatio > 75){
					textColor = Color.GREEN;
				}else if(floatRatio > 25){
					textColor = Color.YELLOW;
				}else {
					textColor = Color.RED;
				}
				String text = Float.toString(floatRatio);
				str = text.substring(0, text.indexOf(".")) + "%";
			} else {
				if(plugin.iceDemon.getHealthRatio() > 75){
					textColor = Color.GREEN;
				}else if(plugin.iceDemon.getHealthRatio() > 25){
					textColor = Color.YELLOW;
				}else {
					textColor = Color.RED;
				}
				str = plugin.iceDemon.getHealthRatio() + "%";
			}
			Point point = plugin.iceDemon.getCanvasTextLocation(graphics, str, plugin.iceDemon.getLogicalHeight());
			if (point == null)
				return null;
			point = new Point(point.getX(), point.getY() + 20);
			OverlayUtil.renderTextLocation(graphics, point, str, textColor);
			graphics.setFont(oldFont);
		}
		return super.render(graphics);
	}

	private void renderTileObjects(Graphics2D graphics) {
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();

		int z = client.getPlane();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[z][x][y];

				if (tile == null) {
					continue;
				}

				Player player = client.getLocalPlayer();
				if (player == null) {
					continue;
				}

				renderGameObjects(graphics, tile, player);
			}
		}
	}

	private void renderGameObjects(Graphics2D graphics, Tile tile, Player player) {
		GameObject[] gameObjects = tile.getGameObjects();
		if (gameObjects != null) {
			for (GameObject gameObject : gameObjects) {
				if (gameObject != null && gameObject.getSceneMinLocation().equals(tile.getSceneLocation())) {
					if(gameObject.getId() == 29747) {
						renderTileObject(graphics, gameObject, player, config.highlightBrazierColor());
					}
				}
			}
		}
	}

	private void renderTileObject(Graphics2D graphics, TileObject tileObject, Player player, Color color) {
		if (tileObject != null) {
			if (player.getLocalLocation().distanceTo(tileObject.getLocalLocation()) <= 2400) {
				Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), config.highlightBrazierOpacity());
				OverlayUtil.renderHoverableArea(graphics, tileObject.getClickbox(), client.getMouseCanvasPosition(), fillColor, color, color.darker());
			}
		}
	}
}