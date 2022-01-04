/*
 * THIS PLUGIN WAS WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI BUT SHUFFLED BY A KANGAROO WITH THUMBS.
 * The plugin and it's refactoring was intended for xKylee's Externals but I'm sure if you're reading this, you're probably planning to yoink..
 * or you're just genuinely curious. If you're trying to yoink, it doesn't surprise me.. just don't claim it as your own. Cheers.
 */

package net.runelite.client.plugins.theatre.Maiden;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.util.Text;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.theatre.Room;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.TheatrePlugin;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Maiden extends Room
{
	@Inject
	private Client client;

	@Inject
	private MaidenOverlay maidenOverlay;

	@Inject
	private ThresholdOverlay thresholdOverlay;

	@Inject
	private MaidenMaxHitTooltip maidenMaxHitTooltip;

	@Inject
	private MaidenMaxHitOverlay maidenMaxHitOverlay;

	@Inject
	protected Maiden(TheatrePlugin plugin, TheatreConfig config)
	{
		super(plugin, config);
		maxHit = 36.5D;
	}

	@Getter
	private boolean maidenActive;

	@Getter
	private NPC maidenNPC;

	@Getter
	private List<NPC> maidenSpawns = new ArrayList<>();

	@Getter
	private Map<NPC, Pair<Integer, Integer>> maidenReds = new HashMap<>();

	@Getter
	private List<WorldPoint> maidenBloodSplatters = new ArrayList<>();

	@Getter
	private List<WorldPoint> maidenBloodSpawnLocations = new ArrayList<>();

	@Getter
	private List<WorldPoint> maidenBloodSpawnTrailingLocations = new ArrayList<>();

	@Getter
	private int ticksUntilAttack = 0;
	private int lastAnimationID = -1;
	private int ticksLastAttack = 0;

	private static final int GRAPHICSOBJECT_ID_MAIDEN = 1579;

	@Getter
	private int newMaidenHp = -1;
	@Getter
	private int newMaidenThresholdHp = -1;
	@Getter
	private short realMaidenHp = -1;
	@Getter
	private short thresholdHp = -1;
	@Getter
	private double maxHit;
	private short timesMaidenHealed = 0;
	private short amountMaidenHealed = 0;
	public final DecimalFormat df1 = new DecimalFormat("#0.0");
	private final Consumer<Double> setThreshold = (percent) -> {
		thresholdHp = (short)((int)Math.floor((double)getMaidenBaseHpIndex() * percent));
	};

	@Getter
	private final Map<Integer, MatomenosDetails> matomenos = new HashMap<>();

	private static final Map<Integer, Integer> GRAPHICS_MAP = ImmutableMap.of(181, 8, 180, 16, 179, 24, 369, 33, 367, 25);

	@Override
	public void load()
	{
		overlayManager.add(maidenOverlay);
		overlayManager.add(thresholdOverlay);
		overlayManager.add(maidenMaxHitOverlay);
		addIfTrue(maidenMaxHitTooltip, config::maidenMaxHit);
	}

	@Override
	public void unload()
	{
		overlayManager.remove(maidenOverlay);
		overlayManager.remove(thresholdOverlay);
		overlayManager.remove(maidenMaxHitOverlay);
		overlayManager.removeIf(MaidenMaxHitTooltip.class::isInstance);

		maidenActive = false;
		maidenBloodSplatters.clear();
		maidenSpawns.clear();
		maidenReds.clear();
		maidenBloodSpawnLocations.clear();
		maidenBloodSpawnTrailingLocations.clear();
		newMaidenHp = -1;
		newMaidenThresholdHp = -1;
		timesMaidenHealed = 0;
		amountMaidenHealed = 0;
		realMaidenHp = -1;
		thresholdHp = -1;
		maxHit = 36.5D;
		matomenos.clear();
	}

	void updateMaidenMaxHit()
	{
		maxHit += 3.5D;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e)
	{
		if (maidenActive && e.getActor() instanceof NPC)
		{
			NPC npc = (NPC)e.getActor();
			if (isNpcFromName(npc, "Nylocas Matomenos") && npc.getAnimation() == 8097)
			{
				MatomenosDetails details = matomenos.get(npc.getIndex());
				if (!details.getMatomenosNpc().isDead() && details.calculateDistanceTo(getMaidenNPC()) == 0)
				{

					updateMaidenMaxHit();
				}
			}

		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		switch (npc.getId())
		{
			case NpcID.THE_MAIDEN_OF_SUGADINTI:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8364:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8365:
			case 10814:
			case 10815:
			case 10816:
			case 10817:
			case 10818:
			case 10819:
			case 10822:
			case 10823:
			case 10824:
			case 10825:
			case 10826:
			case 10827:
				ticksUntilAttack = 10;
				ticksLastAttack = client.getTickCount() - 1;
				maidenActive = true;
				maidenNPC = npc;
				if (realMaidenHp < 0) {
					realMaidenHp = getMaidenBaseHpIndex();
				}

				setThreshold.accept(0.7D);
				break;
			case NpcID.BLOOD_SPAWN:
			case 10821:
			case 10829:
				maidenSpawns.add(npc);
				break;
			case NpcID.NYLOCAS_MATOMENOS:
			case 10820:
			case 10828:
				maidenReds.putIfAbsent(npc, new MutablePair<>(npc.getHealthRatio(), npc.getHealthScale()));
				matomenos.put(npc.getIndex(), new MatomenosDetails(client, npc, "0"));
				break;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		switch (npc.getId())
		{
			case NpcID.THE_MAIDEN_OF_SUGADINTI:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8364:
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8365:
			case 10814:
			case 10815:
			case 10816:
			case 10817:
			case 10818:
			case 10819:
			case 10822:
			case 10823:
			case 10824:
			case 10825:
			case 10826:
			case 10827:
				ticksUntilAttack = 0;
				ticksLastAttack = 0;
				maidenActive = false;
				maidenSpawns.clear();
				maidenNPC = null;
				newMaidenHp = -1;
				newMaidenThresholdHp = -1;
				timesMaidenHealed = 0;
				amountMaidenHealed = 0;
				realMaidenHp = -1;
				thresholdHp = -1;
				maxHit = 36.5D;
				break;
			case NpcID.BLOOD_SPAWN:
			case 10821:
			case 10829:
				maidenSpawns.remove(npc);
				break;
			case NpcID.NYLOCAS_MATOMENOS:
			case 10820:
			case 10828:
				maidenReds.remove(npc);
				matomenos.remove(npc.getIndex());
				break;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!maidenActive)
		{
			return;
		}

		if (maidenNPC != null)
		{
			ticksUntilAttack--;
			if (lastAnimationID == -1 && maidenNPC.getAnimation() != lastAnimationID)
			{
				//ticksUntilAttack = 10;
				ticksUntilAttack = client.getTickCount() - ticksLastAttack;
				ticksLastAttack = client.getTickCount();
			}
			lastAnimationID = maidenNPC.getAnimation();
		}

		maidenBloodSplatters.clear();
		client.getGraphicsObjects().stream().filter(o -> o.getId() == GRAPHICSOBJECT_ID_MAIDEN).
			forEach(o -> maidenBloodSplatters.add(WorldPoint.fromLocal(client, o.getLocation())));

		maidenBloodSpawnTrailingLocations.clear();
		maidenBloodSpawnTrailingLocations.addAll(maidenBloodSpawnLocations);
		maidenBloodSpawnLocations.clear();

		maidenSpawns.forEach(s -> maidenBloodSpawnLocations.add(s.getWorldLocation()));

		if (!matomenos.isEmpty())
		{
			matomenos.values().forEach(MatomenosDetails::decrementFrozenTicks);
		}
	}

	Color maidenSpecialWarningColor()
	{
		Color col = Color.GREEN;
		if (maidenNPC == null || maidenNPC.getInteracting() == null)
		{
			return col;
		}

		if (maidenNPC.getInteracting().getName().equals(client.getLocalPlayer().getName()))
		{
			return Color.ORANGE;
		}

		return col;
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcDefinitionChanged)
	{
		int npcId = npcDefinitionChanged.getNpc().getId();
		switch(npcId)
		{
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
				setThreshold.accept(0.5D);
				break;
			case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
				setThreshold.accept(0.3D);
		}

	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied e)
	{
		if (maidenActive)
		{
			if (e.getActor() instanceof NPC)
			{
				NPC npc = (NPC)e.getActor();
				if (npc == maidenNPC)
				{
					Hitsplat.HitsplatType type = e.getHitsplat().getHitsplatType();
					switch(type)
					{
						case HEAL:
							realMaidenHp = (short)(realMaidenHp + e.getHitsplat().getAmount());
							++timesMaidenHealed;
							amountMaidenHealed = (short)(amountMaidenHealed + e.getHitsplat().getAmount());
							break;
						case DAMAGE_ME:
						case DAMAGE_OTHER:
							realMaidenHp = (short)(realMaidenHp - e.getHitsplat().getAmount());
					}
				}

			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded e)
	{
		if (!maidenActive)
		{
			return;
		}

		if (config.maidenRedsHealthMenu())
		{
			if (e.getTarget().contains("Nylocas Matomenos") && (e.getType() == MenuAction.NPC_SECOND_OPTION.getId() || e.getType() == MenuAction.SPELL_CAST_ON_NPC.getId()))
			{
				NPC npc = client.getCachedNPCs()[e.getIdentifier()];
				if (npc == null || npc.getName() == null)
				{
					return;
				}

				if (maidenReds.containsKey(npc))
				{
					Pair<Integer, Integer> hp = maidenReds.get(npc);
					float nyloHp = (float) hp.getLeft() / (float) hp.getRight() * 100.0F;
					String strippedTarget = stripLevel(stripColor(e.getTarget()));
					String newTarget = ColorUtil.prependColorTag(strippedTarget + "(" + df1.format(nyloHp) + ")", maidenOverlay.percentageToColor(nyloHp));
					MenuEntry[] entries = client.getMenuEntries();
					entries[entries.length - 1].setTarget(newTarget);
					client.setMenuEntries(entries);
				}
			}

		}

		if (config.maidenBloodSpawnsMES())
		{
			if (e.getTarget().contains("Blood spawn") && e.getTarget().contains("Ice B") && e.getType() == MenuAction.SPELL_CAST_ON_NPC.getId())
			{
				client.setMenuOptionCount(client.getMenuOptionCount() - 1);
			}
		}
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged e)
	{
		if (isMaidenActive() && e.getActor() instanceof NPC)
		{
			NPC npc = (NPC)e.getActor();
			MatomenosDetails details = matomenos.getOrDefault(npc.getIndex(), null);
			if (details != null)
			{
				details.setFrozenTicks(GRAPHICS_MAP.getOrDefault(npc.getGraphic(), -1));
			}
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened menu)
	{
		if (!config.maidenRedsHealthMenu() || !maidenActive)
		{
			return;
		}

		// Filter all entries with Examine
		client.setMenuEntries(Arrays.stream(menu.getMenuEntries()).filter(s -> !s.getOption().equals("Examine")).toArray(MenuEntry[]::new));
	}

	private short getMaidenBaseHpIndex()
	{
		switch(TheatrePlugin.partySize)
		{
			case 4:
				return 3062;
			case 5:
				return 3500;
			default:
				return 2625;
		}
	}


	public static boolean isNylocasMatomenos(NPC npc)
	{
		return npc.getName() != null && npc.getName().equalsIgnoreCase("nylocas matomenos");
	}

	private static String stripColor(String str)
	{
		return str.replaceAll("(<col=[0-9a-f]+>|</col>)", "");
	}

	private static String stripLevel(String str)
	{
		return str.replaceAll("\\(level-[0-9]+\\)", "");
	}
}
