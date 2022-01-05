package net.runelite.client.plugins.bobigsplits;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class BigSplitsOverlay extends OverlayPanel
{
	private final Client client;
	private final BigSplitsPlugin plugin;
	private final BigSplitsConfig config;



	@Inject
	private BigSplitsOverlay(Client client, BigSplitsConfig config, BigSplitsPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;

		setPriority(OverlayPriority.HIGH);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);

	}


	@Override
	public Dimension render(Graphics2D graphics) {

		if (plugin.getSplitsMap().isEmpty()) {
			return null;
		}

		int textStyle = config.textStyle();

		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


		plugin.getSplitsMap().forEach((npc, ticks) -> {
			final Polygon poly = Perspective.getCanvasTileAreaPoly(client, npc.getLocalLocation(), 2);

			if (poly != null) {

				if (ticks == 1) {
					graphics.setStroke(new BasicStroke(this.config.tileStroke1()));
					graphics.setColor(config.TileColor1());
					graphics.draw(poly);
					graphics.setColor(new Color(config.TileColor1().getRed(), config.TileColor1().getGreen(), config.TileColor1().getBlue(), config.tileFill1()));
					graphics.fill(poly);
				}
				if (ticks == 2) {
					graphics.setStroke(new BasicStroke(this.config.tileStroke2()));
					graphics.setColor(config.TileColor2());
					graphics.draw(poly);
					graphics.setColor(new Color(config.TileColor2().getRed(), config.TileColor2().getGreen(), config.TileColor2().getBlue(), config.tileFill2()));
					graphics.fill(poly);
				}
				if (ticks >= 3) {
					graphics.setStroke(new BasicStroke(this.config.tileStroke()));
					graphics.setColor(config.tileColor());
					graphics.draw(poly);
					graphics.setColor(new Color(config.tileColor().getRed(), config.tileColor().getGreen(), config.tileColor().getBlue(), config.tileFill()));

					graphics.fill(poly);
				}
			}
		});



		if (!(config.tileFill() == 0 && config.tileFill2() == 0 && config.tileFill1() == 0)) {
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);		//ty to caps/jessie for this, aliasing text + fill looks horrible
		}
			plugin.getSplitsMap().forEach((npc, ticks) -> {

			final Point textLocation = Perspective.getCanvasTextLocation(client, graphics, npc.getLocalLocation(), "#", 0);

			if (textLocation != null)
			{

				if (ticks == 1)
				{
					graphics.setFont(new Font("Arial", textStyle, config.textSize1()));
					OverlayUtil.renderTextLocation(graphics, textLocation, Integer.toString(ticks), config.textColor1());
				}

				if (ticks == 2)
				{
					graphics.setFont(new Font("Arial", textStyle, config.textSize2()));
					OverlayUtil.renderTextLocation(graphics, textLocation, Integer.toString(ticks), config.textColor2());
				}

				if (ticks >= 3 && ticks < 10)	//if ticks have been set to 1000 and not updated by the animation start, then it wont render the ticks til splits
				{
					graphics.setFont(new Font("Arial", textStyle, config.textSize()));
					OverlayUtil.renderTextLocation(graphics, textLocation, Integer.toString(ticks), config.textColor());
				}
			}
		});

		 return null;
	}
}
