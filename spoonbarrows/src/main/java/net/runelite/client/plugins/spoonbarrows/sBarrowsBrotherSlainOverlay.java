package net.runelite.client.plugins.spoonbarrows;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class sBarrowsBrotherSlainOverlay extends OverlayPanel {
	private final Client client;

	private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");

	@Inject
	private sBarrowsBrotherSlainOverlay(sBarrowsPlugin plugin, Client client) {
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Barrows overlay"));
	}

	public Dimension render(Graphics2D graphics) {
		// Only render the brothers slain overlay if the vanilla interface is loaded
		final Widget barrowsBrothers = client.getWidget(WidgetInfo.BARROWS_BROTHERS);
		if (barrowsBrothers == null)
		{
			return null;
		}

		for (sBarrowsBrothers brother : sBarrowsBrothers.values())
		{
			final boolean brotherSlain = client.getVar(brother.getKilledVarbit()) > 0;
			String slain = brotherSlain ? "\u2713" : "\u2717";
			panelComponent.getChildren().add(LineComponent.builder()
					.left(brother.getName())
					.right(slain)
					.rightFont(FontManager.getDefaultFont())
					.rightColor(brotherSlain ? Color.GREEN : Color.RED)
					.build());
		}

		final int rewardPotential = rewardPotential();
		panelComponent.getChildren().add(LineComponent.builder()
				.left("Potential")
				.right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
				.rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
				.build());

		return super.render(graphics);
	}

	/**
	 * Compute the barrows reward potential. Potential rewards are based off of the amount of
	 * potential.
	 * <p>
	 * The reward potential thresholds are as follows:
	 * Mind rune - 381
	 * Chaos rune - 506
	 * Death rune - 631
	 * Blood rune - 756
	 * Bolt rack - 881
	 * Half key - 1006
	 * Dragon med - 1012
	 *
	 * @return potential, 0-1012 inclusive
	 * @see <a href="https://twitter.com/jagexkieren/status/705428283509366785?lang=en">source</a>
	 */
	private int rewardPotential()
	{
		// this is from [proc,barrows_overlay_reward]
		int brothers = client.getVar(Varbits.BARROWS_KILLED_AHRIM)
				+ client.getVar(Varbits.BARROWS_KILLED_DHAROK)
				+ client.getVar(Varbits.BARROWS_KILLED_GUTHAN)
				+ client.getVar(Varbits.BARROWS_KILLED_KARIL)
				+ client.getVar(Varbits.BARROWS_KILLED_TORAG)
				+ client.getVar(Varbits.BARROWS_KILLED_VERAC);
		return client.getVar(Varbits.BARROWS_REWARD_POTENTIAL) + brothers * 2;
	}
}
