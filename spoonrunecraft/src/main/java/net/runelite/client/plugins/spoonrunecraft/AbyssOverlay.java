package net.runelite.client.plugins.spoonrunecraft;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.AsyncBufferedImage;

class AbyssOverlay extends Overlay {
	@Inject
	AbyssOverlay(Client client, sRunecraftPlugin plugin, sRunecraftConfig config) {
		this.rifts = new HashSet<>();
		this.abyssIcons = new HashMap<>();
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	public Dimension render(Graphics2D graphics) {
		if (this.config.showRifts())
			for (DecorativeObject object : this.plugin.getAbyssObjects())
				renderRifts(graphics, object);
		if (this.config.hightlightDarkMage())
			highlightDarkMage(graphics);
		return null;
	}

	private void highlightDarkMage(Graphics2D graphics) {
		if (!this.plugin.isDegradedPouchInInventory())
			return;
		NPC darkMage = this.plugin.getDarkMage();
		if (darkMage == null)
			return;
		Polygon tilePoly = darkMage.getCanvasTilePoly();
		if (tilePoly == null)
			return;
		OverlayUtil.renderPolygon(graphics, tilePoly, Color.green);
	}

	private void renderRifts(Graphics2D graphics, DecorativeObject object) {
		AbyssRifts rift = AbyssRifts.getRift(object.getId());
		if (rift == null || !this.rifts.contains(rift))
			return;
		if (this.config.showClickBox()) {
			Point mousePosition = this.client.getMouseCanvasPosition();
			Shape objectClickbox = object.getClickbox();
			if (objectClickbox != null) {
				if (objectClickbox.contains(mousePosition.getX(), mousePosition.getY())) {
					graphics.setColor(Color.MAGENTA.darker());
				} else {
					graphics.setColor(Color.MAGENTA);
				}
				graphics.draw(objectClickbox);
				graphics.setColor(new Color(255, 0, 255, 20));
				graphics.fill(objectClickbox);
			}
		}
		BufferedImage image = getImage(rift);
		Point miniMapImage = Perspective.getMiniMapImageLocation(this.client, object.getLocalLocation(), image);
		if (miniMapImage != null)
			graphics.drawImage(image, miniMapImage.getX(), miniMapImage.getY(), (ImageObserver)null);
	}

	public BufferedImage getImage(AbyssRifts rift) {
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

	public void updateConfig() {
		this.rifts.clear();
		if (this.config.showAir())
			this.rifts.add(AbyssRifts.AIR_RIFT);
		if (this.config.showBlood())
			this.rifts.add(AbyssRifts.BLOOD_RIFT);
		if (this.config.showBody())
			this.rifts.add(AbyssRifts.BODY_RIFT);
		if (this.config.showChaos())
			this.rifts.add(AbyssRifts.CHAOS_RIFT);
		if (this.config.showCosmic())
			this.rifts.add(AbyssRifts.COSMIC_RIFT);
		if (this.config.showDeath())
			this.rifts.add(AbyssRifts.DEATH_RIFT);
		if (this.config.showEarth())
			this.rifts.add(AbyssRifts.EARTH_RIFT);
		if (this.config.showFire())
			this.rifts.add(AbyssRifts.FIRE_RIFT);
		if (this.config.showLaw())
			this.rifts.add(AbyssRifts.LAW_RIFT);
		if (this.config.showMind())
			this.rifts.add(AbyssRifts.MIND_RIFT);
		if (this.config.showNature())
			this.rifts.add(AbyssRifts.NATURE_RIFT);
		if (this.config.showSoul())
			this.rifts.add(AbyssRifts.SOUL_RIFT);
		if (this.config.showWater())
			this.rifts.add(AbyssRifts.WATER_RIFT);
	}

	private static final Dimension IMAGE_SIZE = new Dimension(15, 14);

	private final Set<AbyssRifts> rifts;

	private final Map<AbyssRifts, BufferedImage> abyssIcons;

	private final Client client;

	private final sRunecraftPlugin plugin;

	private final sRunecraftConfig config;

	@Inject
	private ItemManager itemManager;
}
