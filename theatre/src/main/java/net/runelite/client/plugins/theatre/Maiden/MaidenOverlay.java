/*
 * THIS PLUGIN WAS WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI BUT SHUFFLED BY A KANGAROO WITH THUMBS.
 * The plugin and it's refactoring was intended for xKylee's Externals but I'm sure if you're reading this, you're probably planning to yoink..
 * or you're just genuinely curious. If you're trying to yoink, it doesn't surprise me.. just don't claim it as your own. Cheers.
 */

package net.runelite.client.plugins.theatre.Maiden;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.function.Consumer;
import javax.inject.Inject;
import com.google.common.collect.ArrayListMultimap;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.theatre.RoomOverlay;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class MaidenOverlay extends RoomOverlay
{
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");

	@Inject
	private Maiden maiden;

	@Inject
	private Client client;

	@Inject
	private ModelOutlineRenderer outliner;

	@Inject
	protected MaidenOverlay(TheatreConfig config)
	{
		super(config);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (maiden.isMaidenActive())
		{
			if (config.maidenBlood())
			{
				for (WorldPoint point : maiden.getMaidenBloodSplatters())
				{
					drawTile(graphics, point, new Color(0, 150, 200), 2, 150, 10);
				}
			}

			if (config.maidenSpawns())
			{
				for (WorldPoint point : maiden.getMaidenBloodSpawnLocations())
				{
					drawTile(graphics, point, new Color(0, 150, 200), 2, 180, 20);
				}
				for (WorldPoint point : maiden.getMaidenBloodSpawnTrailingLocations())
				{
					drawTile(graphics, point, new Color(0, 150, 200), 1, 120, 10);
				}
			}

			if (config.maidenTickCounter() && maiden.getMaidenNPC() != null && !maiden.getMaidenNPC().isDead())
			{
				String text = String.valueOf(maiden.getTicksUntilAttack());
				if (maiden.getTicksUntilAttack() > 10 || maiden.getTicksUntilAttack() < 0)
				{
					text = "??";
				}
				Point canvasPoint = maiden.getMaidenNPC().getCanvasTextLocation(graphics, text, 15);

				if (canvasPoint != null)
				{
					Color col = maiden.maidenSpecialWarningColor();
					renderTextLocation(graphics, text, col, canvasPoint);
				}
			}

			if (config.maidenRedsHealth() || config.maidenRedsDistance() || config.maidenRedsFreezeTimers() || config.maidenRedsSpawnIndicators() != TheatreConfig.RenderingTypes.OFF)
			{
				//displayNyloHpOverlayGrouped(graphics);
				if (config.maidenRedsHealth())
				{
					displayNyloHpOverlayB(graphics);
				}
				displayNyloOverlay(graphics);

				NPC[] reds = maiden.getMaidenReds().keySet().toArray(new NPC[0]);
				for (NPC npc : reds)
				{
					if (npc.getName() != null && npc.getHealthScale() > 0 && npc.getHealthRatio() < 100)
					{
						Pair<Integer, Integer> newVal = new MutablePair<>(npc.getHealthRatio(), npc.getHealthScale());
						if (maiden.getMaidenReds().containsKey(npc))
						{
							maiden.getMaidenReds().put(npc, newVal);
						}
					}
				}
			}
		}
		return null;
	}

	private void displayNyloHpOverlayGrouped(Graphics2D graphics)
	{
		ArrayListMultimap<Point, NPC> nyloGrouped = ArrayListMultimap.create();
		maiden.getMaidenReds().forEach((nylo, hp) -> {
			Point point = new Point(nylo.getWorldLocation().getX(), nylo.getWorldLocation().getY());
			if (!nylo.isDead())
			{
				nyloGrouped.put(point, nylo);
				if (nylo.getName() != null && nylo.getHealthScale() > 0 && nylo.getHealthRatio() < 100 && maiden.getMaidenReds().containsKey(nylo))
				{
					maiden.getMaidenReds().put(nylo, new MutablePair<>(nylo.getHealthRatio(), nylo.getHealthScale()));
				}
			}

		});

		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (Point point : nyloGrouped.keys())
		{
			int zOffset = 0;

			for (Iterator<NPC> iterator = nyloGrouped.get(point).iterator(); iterator.hasNext(); zOffset += fontMetrics.getHeight())
			{
				NPC nyloNPC = iterator.next();
				drawNyloHpOverlay(graphics, nyloNPC, zOffset);
			}
		}

	}

	private void drawNyloHpOverlay(Graphics2D graphics, NPC nyloNPC, int zOffset)
	{
		int healthScale = nyloNPC.getHealthScale();
		int healthRatio = nyloNPC.getHealthRatio();
		if (nyloNPC.getName() != null && nyloNPC.getHealthScale() > 0)
		{
			healthScale = nyloNPC.getHealthScale();
			healthRatio = Math.min(healthRatio, nyloNPC.getHealthRatio());
		}

		float nyloHp = ((float) healthRatio / (float) healthScale) * 100.0F;
		String text = getNyloString(nyloNPC);
		Point textLocation = nyloNPC.getCanvasTextLocation(graphics, text, 0);
		if (!nyloNPC.isDead() && textLocation != null)
		{
			textLocation = new Point(textLocation.getX(), textLocation.getY() - zOffset);
			Color color = percentageToColor(nyloHp);
			renderTextLocation(graphics, text, color, textLocation);
		}
	}

	private String getNyloString(NPC nyloNPC)
	{
		String string = "";
		if (config.maidenRedsHealth())
		{
			int v_health = nyloNPC.getHealthScale();
			int v_healthRation = nyloNPC.getHealthRatio();
			if (nyloNPC.getName() != null && nyloNPC.getHealthScale() > 0)
			{
				v_health = nyloNPC.getHealthScale();
				v_healthRation = Math.min(v_healthRation, nyloNPC.getHealthRatio());
			}
			float percentage = ((float) v_healthRation / (float) v_health) * 100f;
			string = (String.valueOf(DECIMAL_FORMAT.format(percentage)));
		}

		if (config.maidenRedsHealth() && config.maidenRedsDistance())
		{
			string += " - ";
		}

		if (config.maidenRedsDistance())
		{
			final int maidenX = maiden.getMaidenNPC().getWorldLocation().getX() + maiden.getMaidenNPC().getTransformedComposition().getSize();

			int deltaX = Math.max(0, nyloNPC.getWorldLocation().getX() - maidenX);
			string += deltaX;
		}

		return string;
	}

	Color percentageToColor(float percentage)
	{
		percentage = Math.max(Math.min(100.0F, percentage), 0.0F);
		double rMod = 130.0D * percentage / 100.0D;
		double gMod = 235.0D * percentage / 100.0D;
		double bMod = 125.0D * percentage / 100.0D;
		return new Color((int) Math.min(255.0D, 255.0D - rMod), Math.min(255, (int)(20.0D + gMod)), Math.min(255, (int)(0.0D + bMod)));
	}

	private void displayNyloOverlay(Graphics2D graphics) {
		this.client.getNpcs().stream().filter(Maiden::isNylocasMatomenos).forEach(this.buildNyloOverlay(graphics));
	}

	private int getMaidenX() {
		NPC maidenNpc = maiden.getMaidenNPC();
		WorldPoint maidenWp = maidenNpc.getWorldLocation();
		int maidenWpX = maidenWp.getX();
		NPCComposition composition = maidenNpc.getTransformedComposition();
		if (composition != null) {
			maidenWpX += composition.getSize();
		}

		return maidenWpX;
	}

	@NotNull
	private Consumer<NPC> buildNyloOverlay(Graphics2D graphics)
	{
		return (npc) -> {
			if (npc.isDead())
			{
				return;
			}

			if (config.maidenRedsDistance() && npc.getPoseAnimation() != 8096)
			{
				int distance = Math.max(0, npc.getWorldLocation().getX() - getMaidenX());
				Point textLocation = npc.getCanvasTextLocation(graphics, "", 150);
				if (distance != 0)
				{
					renderTextLocation(graphics, Integer.toString(distance), Color.WHITE, textLocation);
				}
			}

			if (config.maidenRedsFreezeTimers() && maiden.getMatomenos().get(npc.getIndex()).getFrozenTicksOptStr().isPresent() && npc.getPoseAnimation() == 8096)
			{
				String ft = maiden.getMatomenos().get(npc.getIndex()).getFrozenTicksOptStr().get();
				Point textLocation = npc.getCanvasTextLocation(graphics, ft, 150);
				renderTextLocation(graphics, ft, Color.CYAN, textLocation);
			}

			if (config.maidenRedsSpawnIndicators() != TheatreConfig.RenderingTypes.OFF)
			{
				Pair<String, Boolean> identifier = maiden.getMatomenos().get(npc.getIndex()).getIdentifier();
				if (identifier != null && identifier.getValue())
				{
					Color color = config.maidenScuffedColor();
					switch (config.maidenRedsSpawnIndicators())
					{
						case TILE:
							Polygon tileAreaPoly = getCanvasTileAreaPoly(client, npc.getLocalLocation(), 2, false);
							renderPoly(graphics, color, tileAreaPoly);
							break;
						case HULL:
							OverlayUtil.renderPolygon(graphics, npc.getConvexHull(), color);
							break;
						case OUTLINE:
							outliner.drawOutline(npc, 1, color, 0);
							break;
					}
				}
			}
		};
	}

	private void displayNyloHpOverlayB(Graphics2D graphics) {
		ArrayListMultimap<Point, NyloNPC> nyloGrouped = ArrayListMultimap.create();
		maiden.getMaidenReds().forEach((nylo, hp) -> {
			Point point = new Point(nylo.getWorldLocation().getX(), nylo.getWorldLocation().getY());
			NyloNPC nyloNPC = new NyloNPC(nylo, (Integer)hp.getLeft(), (Integer)hp.getRight());
			if (!nylo.isDead()) {
				nyloGrouped.put(point, nyloNPC);
				if (nylo.getName() != null && nylo.getHealthScale() > 0 && nylo.getHealthRatio() < 100 && maiden.getMaidenReds().containsKey(nylo)) {
					maiden.getMaidenReds().put(nylo, new MutablePair<>(nylo.getHealthRatio(), nylo.getHealthScale()));
				}
			}

		});
		FontMetrics fontMetrics = graphics.getFontMetrics();

		for (Point point : nyloGrouped.keys())
		{
			int zOffset = 0;

			for (Iterator<NyloNPC> var7 = nyloGrouped.get(point).iterator(); var7.hasNext(); zOffset += fontMetrics.getHeight())
			{
				NyloNPC nyloNPC = var7.next();
				this.drawNyloHpOverlayB(graphics, nyloNPC, zOffset);
			}
		}

	}

	private void drawNyloHpOverlayB(Graphics2D graphics, NyloNPC nyloNPC, int zOffset) {
		int healthScale = nyloNPC.getHealthScale();
		int healthRatio = nyloNPC.getHealthRatio();
		NPC npc = nyloNPC.getNpc();
		if (npc.getName() != null && npc.getHealthScale() > 0) {
			healthScale = npc.getHealthScale();
			healthRatio = Math.min(healthRatio, npc.getHealthRatio());
		}

		float nyloHp = (float)healthRatio / (float)healthScale * 100.0F;
		String text = String.valueOf(maiden.df1.format((double)nyloHp));
		Point textLocation = nyloNPC.getNpc().getCanvasTextLocation(graphics, text, 0);
		if (!npc.isDead() && textLocation != null) {
			textLocation = new Point(textLocation.getX(), textLocation.getY() - zOffset);
			Color color = percentageToColor(nyloHp);
			this.renderTextLocation(graphics, text, color, textLocation);
		}

	}

	static class NyloNPC {
		private NPC npc;
		private int healthRatio;
		private int healthScale;

		public NyloNPC(NPC npc, int healthRatio, int healthScale) {
			this.npc = npc;
			this.healthRatio = healthRatio;
			this.healthScale = healthScale;
		}

		public NPC getNpc() {
			return this.npc;
		}

		public int getHealthRatio() {
			return this.healthRatio;
		}

		public int getHealthScale() {
			return this.healthScale;
		}

		public void setNpc(NPC npc) {
			this.npc = npc;
		}

		public void setHealthRatio(int healthRatio) {
			this.healthRatio = healthRatio;
		}

		public void setHealthScale(int healthScale) {
			this.healthScale = healthScale;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (!(o instanceof NyloNPC)) {
				return false;
			} else {
				NyloNPC other = (NyloNPC)o;
				if (!other.canEqual(this)) {
					return false;
				} else {
					label31: {
						NPC this$npc = this.getNpc();
						NPC other$npc = other.getNpc();
						if (this$npc == null) {
							if (other$npc == null) {
								break label31;
							}
						} else if (this$npc.equals(other$npc)) {
							break label31;
						}

						return false;
					}

					if (this.getHealthRatio() != other.getHealthRatio()) {
						return false;
					} else {
						return this.getHealthScale() == other.getHealthScale();
					}
				}
			}
		}

		protected boolean canEqual(NyloNPC other) {
			return other instanceof NyloNPC;
		}

		public String toString() {
			NPC var10000 = this.getNpc();
			return "MaidenOverlay.NyloNPC(npc=" + var10000 + ", healthRatio=" + this.getHealthRatio() + ", healthScale=" + this.getHealthScale() + ")";
		}
	}
}
