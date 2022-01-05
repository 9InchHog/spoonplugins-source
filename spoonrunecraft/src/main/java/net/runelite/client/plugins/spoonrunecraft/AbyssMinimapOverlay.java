package net.runelite.client.plugins.spoonrunecraft;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.AsyncBufferedImage;

class AbyssMinimapOverlay extends Overlay {
	private static final Dimension IMAGE_SIZE = new Dimension(15, 14);

	private final Map<AbyssRifts, BufferedImage> abyssIcons = new HashMap<>();

	private final Client client;

	private final sRunecraftPlugin plugin;

	private final sRunecraftConfig config;

	private final ItemManager itemManager;

	@Inject
	AbyssMinimapOverlay(Client client, sRunecraftPlugin plugin, sRunecraftConfig config, ItemManager itemManager) {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.itemManager = itemManager;
	}

	public Dimension render(Graphics2D graphics) {
		if (!this.config.showRifts())
			return null;
		for (DecorativeObject object : this.plugin.getAbyssObjects()) {
			AbyssRifts rift = AbyssRifts.getRift(object.getId());
			if (rift == null)
				continue;
			BufferedImage image = getImage(rift);
			Point miniMapImage = Perspective.getMiniMapImageLocation(this.client, object.getLocalLocation(), image);
			if (miniMapImage != null)
				graphics.drawImage(image, miniMapImage.getX(), miniMapImage.getY(), (ImageObserver)null);
		}
		return null;
	}

	private BufferedImage getImage(AbyssRifts rift) {
		BufferedImage image = this.abyssIcons.get(rift);
		if (image != null)
			return image;
		AsyncBufferedImage asyncBufferedImage = this.itemManager.getImage(rift.getItemId());
		BufferedImage resizedImage = new BufferedImage(IMAGE_SIZE.width, IMAGE_SIZE.height, 2);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage((Image)asyncBufferedImage, 0, 0, IMAGE_SIZE.width, IMAGE_SIZE.height, null);
		g.dispose();
		this.abyssIcons.put(rift, resizedImage);
		return resizedImage;
	}
}
