package net.runelite.client.plugins.spoonnex;

import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class FollowPlayer {
	public FollowPlayer(Player player) {
		this.player = player;
		this.currentLoc = player.getWorldLocation();
		this.prevLoc = null;
		this.prevUnderNex = false;
	}
	public Player player;
	public WorldPoint currentLoc;
	public WorldPoint prevLoc;
	public boolean prevUnderNex;
}
