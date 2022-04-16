package net.runelite.client.plugins.spoonbarrows;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

class sBarrowsOverlay extends Overlay {
	private static final int MAX_DISTANCE = 2350;

	private final Client client;

	private final sBarrowsPlugin plugin;

	private final sBarrowsConfig config;

	@Inject
	private sBarrowsOverlay(Client client, sBarrowsPlugin plugin, sBarrowsConfig config) {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		Widget puzzleAnswer = this.plugin.getPuzzleAnswer();
		Player local = this.client.getLocalPlayer();
		Color npcColor = getMinimapDotColor(1);
		Color playerColor = getMinimapDotColor(2);

		if (!this.plugin.getWalls().isEmpty() && this.client.getPlane() == 0) {
			graphics.setColor(npcColor);
			List<NPC> npcs = this.client.getNpcs();
			for (NPC npc : npcs) {
				NPCComposition composition = npc.getComposition();
				if (composition != null && !composition.isMinimapVisible())
					continue;
				Point minimapLocation = npc.getMinimapLocation();
				if (minimapLocation != null)
					graphics.fillOval(minimapLocation.getX(), minimapLocation.getY(), 4, 4);
			}
			graphics.setColor(playerColor);
			List<Player> players = this.client.getPlayers();
			for (Player player : players) {
				if (player == local)
					continue;
				Point minimapLocation = player.getMinimapLocation();
				if (minimapLocation != null)
					graphics.fillOval(minimapLocation.getX(), minimapLocation.getY(), 4, 4);
			}
			renderObjects(graphics, local);
			graphics.setColor(playerColor);
			graphics.fillRect(local.getMinimapLocation().getX(), local.getMinimapLocation().getY(), 3, 3);
		}

		if (plugin.isBarrowsLoaded() && config.showBrotherLoc()) {
			renderBarrowsBrothers(graphics);
		}

		if (puzzleAnswer != null && this.config.showPuzzleAnswer() && !puzzleAnswer.isHidden()) {
			Rectangle answerRect = puzzleAnswer.getBounds();
			graphics.setColor(Color.GREEN);
			graphics.draw(answerRect);
		}
		return null;
	}

	private void renderBarrowsBrothers(Graphics2D graphics)
	{
		for (sBarrowsBrothers brother : sBarrowsBrothers.values())
		{
			LocalPoint localLocation = LocalPoint.fromWorld(client, brother.getLocation());
			if (localLocation == null)
			{
				continue;
			}

			String brotherLetter = Character.toString(brother.getName().charAt(0));
			Point miniMapLocation = Perspective.getCanvasTextMiniMapLocation(client, graphics, localLocation, brotherLetter);

			if (miniMapLocation != null)
			{
				graphics.setColor(Color.black);
				graphics.drawString(brotherLetter, miniMapLocation.getX() + 1, miniMapLocation.getY() + 1);

				if (client.getVarbitValue(brother.getKilledVarbit()) > 0)
				{
					graphics.setColor(config.deadBrotherLocColor());
				}
				else
				{
					graphics.setColor(config.brotherLocColor());
				}

				graphics.drawString(brotherLetter, miniMapLocation.getX(), miniMapLocation.getY());
			}
		}
	}


	private void renderObjects(Graphics2D graphics, Player localPlayer) {
		LocalPoint localLocation = localPlayer.getLocalLocation();
		for (WallObject wall : this.plugin.getWalls()) {
			LocalPoint location = wall.getLocalLocation();
			if (localLocation.distanceTo(location) <= 2350)
				renderWalls(graphics, wall);
		}
		for (GameObject ladder : this.plugin.getLadders()) {
			LocalPoint location = ladder.getLocalLocation();
			if (localLocation.distanceTo(location) <= 2350)
				renderLadders(graphics, ladder);
		}
	}

	private void renderWalls(Graphics2D graphics, WallObject wall) {
		Point minimapLocation = wall.getMinimapLocation();
		if (minimapLocation == null)
			return;
		ObjectComposition objectComp = this.client.getObjectDefinition(wall.getId());
		ObjectComposition impostor = (objectComp.getImpostorIds() != null) ? objectComp.getImpostor() : null;
		if (impostor != null && impostor.getActions()[0] != null) {
			graphics.setColor(Color.green);
		} else {
			graphics.setColor(Color.gray);
		}
		graphics.fillRect(minimapLocation.getX(), minimapLocation.getY(), 3, 3);
	}

	private Color getMinimapDotColor(int typeIndex) {
		int pixel = this.client.getMapDots()[typeIndex].getPixels()[1];
		return new Color(pixel);
	}

	private void renderLadders(Graphics2D graphics, GameObject ladder) {
		Point minimapLocation = ladder.getMinimapLocation();
		if (minimapLocation == null)
			return;
		ObjectComposition objectComp = this.client.getObjectDefinition(ladder.getId());
		if (objectComp.getImpostorIds() != null && objectComp.getImpostor() != null) {
			graphics.setColor(Color.orange);
			graphics.fillRect(minimapLocation.getX(), minimapLocation.getY(), 6, 6);
		}
	}
}
