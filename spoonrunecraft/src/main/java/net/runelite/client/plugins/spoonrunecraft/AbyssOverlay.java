package net.runelite.client.plugins.spoonrunecraft;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class AbyssOverlay extends Overlay
{
	private final Client client;
	private final sRunecraftPlugin plugin;
	private final sRunecraftConfig config;

	@Inject
	AbyssOverlay(Client client, sRunecraftPlugin plugin, sRunecraftConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Set<DecorativeObject> abyssObjects = plugin.getAbyssObjects();
		if (abyssObjects.isEmpty() || !config.showRifts() || !config.showClickBox())
		{
			return null;
		}

		for (DecorativeObject object : abyssObjects)
		{
			renderRift(graphics, object);
		}

		return null;
	}

	private void renderRift(Graphics2D graphics, DecorativeObject object)
	{
		AbyssRifts rift = AbyssRifts.getRift(object.getId());
		if (rift == null || !rift.getConfigEnabled().test(config))
		{
			return;
		}

		Point mousePosition = client.getMouseCanvasPosition();
		Shape objectClickbox = object.getClickbox();
		if (objectClickbox != null)
		{
			if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY()))
			{
				graphics.setColor(Color.MAGENTA.darker());
			}
			else
			{
				graphics.setColor(Color.MAGENTA);
			}
			graphics.draw(objectClickbox);
			graphics.setColor(new Color(255, 0, 255, 20));
			graphics.fill(objectClickbox);
		}
	}
}