package net.runelite.client.plugins.ratjam;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
	name = "Rat Jam",
	description = "Ask Boris to explain why",
	tags = {"spoon", "boris"},
	enabledByDefault = false
)
public class PlayerStatusMemePlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerStatusMemeOverlay overlay;

	public int ratJamFrame = 1;

	@Override
	protected void startUp(){
		this.overlayManager.add(this.overlay);
	}

	@Override
	protected void shutDown(){
		this.overlayManager.remove(this.overlay);
	}

	@Subscribe
	public void onClientTick(ClientTick event) {
		if (this.client.getGameState() == GameState.LOGGED_IN){
			ratJamFrame++;
			if (ratJamFrame >= 35) {
				ratJamFrame = 1;
			}
		}
	}
}
