package net.runelite.client.plugins.alchemicalhydra;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.IndexDataBase;
import net.runelite.api.Prayer;
import net.runelite.api.SpritePixels;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;

@Singleton
class HydraOverlay extends Overlay {
	static final int IMGSIZE = 36;

	private final HydraPlugin plugin;
	private final Client client;
	private final SpriteManager spriteManager;
	private final PanelComponent panelComponent = new PanelComponent();

	private BufferedImage stunImg;

	@Setter(AccessLevel.PACKAGE)
	private Color safeCol;

	@Setter(AccessLevel.PACKAGE)
	private Color medCol;

	@Setter(AccessLevel.PACKAGE)
	private Color badCol;

	@Setter(AccessLevel.PACKAGE)
	@Getter
	private int stunTicks;

	private HydraConfig config;

	@Inject
	HydraOverlay(final HydraPlugin plugin, final Client client, final SpriteManager spriteManager, final HydraConfig config) {
		this.config = config;
		this.plugin = plugin;
		this.client = client;
		this.spriteManager = spriteManager;
		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		panelComponent.setOrientation(ComponentOrientation.VERTICAL);
	}

	@Override
	public Dimension render(Graphics2D graphics2D) {
		final Hydra hydra = plugin.getHydra();
		panelComponent.getChildren().clear();

		if (hydra != null) {
			if (stunTicks > 0) {
				addStunOverlay();
			}

			if (config.counting()) {
				addSpecOverlay(hydra);
				addPrayOverlay(hydra);
			}

			panelComponent.setPreferredSize(new Dimension(40, 0));
			panelComponent.setBorder(new Rectangle(0, 0, 0, 0));
		}
		return panelComponent.render(graphics2D);
	}

	private void addStunOverlay() {
		final InfoBoxComponent stunComponent = new InfoBoxComponent();

		stunComponent.setBackgroundColor(badCol);
		stunComponent.setImage(getStunImg());
		stunComponent.setText("        " + stunTicks);
		stunComponent.setPreferredSize(new Dimension(40, 40));

		panelComponent.getChildren().add(stunComponent);
	}

	private void addSpecOverlay(final Hydra hydra) {
		final HydraPhase phase = hydra.getPhase();
		final int nextSpec = hydra.getNextSpecialRelative();

		if (nextSpec > 3) {
			return;
		}
		final InfoBoxComponent specComponent = new InfoBoxComponent();

		if (nextSpec == 0) {
			specComponent.setBackgroundColor(badCol);
		} else if (nextSpec == 1) {
			specComponent.setBackgroundColor(medCol);
		}

		specComponent.setImage(phase.getSpecImage(spriteManager));
		specComponent.setText("        " + nextSpec); // hacky way to not have to figure out how to move text
		specComponent.setPreferredSize(new Dimension(40, 40));

		panelComponent.getChildren().add(specComponent);
	}

	private void addPrayOverlay(final Hydra hydra) {
		final Prayer nextPrayer = hydra.getNextAttack().getPrayer();
		final int nextSwitch = hydra.getNextSwitch();

		InfoBoxComponent prayComponent = new InfoBoxComponent();

		if (nextSwitch == 1) {
			prayComponent.setBackgroundColor(client.isPrayerActive(nextPrayer) ? medCol : badCol);
		} else {
			prayComponent.setBackgroundColor(client.isPrayerActive(nextPrayer) ? safeCol : badCol);
		}

		prayComponent.setImage(hydra.getNextAttack().getImage(spriteManager));
		prayComponent.setText("        " + nextSwitch);
		prayComponent.setColor(Color.white);
		prayComponent.setPreferredSize(new Dimension(40, 40));

		panelComponent.getChildren().add(prayComponent);
	}

	private BufferedImage getStunImg() {
		if (stunImg == null) {
			stunImg = createStunImage(client);
		}
		return stunImg;
	}

	private static BufferedImage createStunImage(Client client) {
		final SpritePixels root = getSprite(client, 1788);
		if (root == null) {
			return null;
		}
		return root.toBufferedImage();
	}

	private static SpritePixels getSprite(Client client, int id) {
		final IndexDataBase spriteDb = client.getIndexSprites();
		if (spriteDb != null) {
			final SpritePixels[] sprites = client.getSprites(spriteDb, id, 0);
			if (sprites != null) {
				return sprites[0];
			}
		}
		return null;
	}
}
