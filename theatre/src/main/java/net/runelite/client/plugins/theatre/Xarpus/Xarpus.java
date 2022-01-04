/*
 * Copyright (c) 2021 BikkusLite
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.theatre.Xarpus;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.theatre.Room;
import net.runelite.client.plugins.theatre.TheatreConfig;
import net.runelite.client.plugins.theatre.TheatrePlugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.tuple.Pair;

public class Xarpus extends Room
{
	@Inject
	private Client client;

	@Inject
	private XarpusOverlay xarpusOverlay;

	@Inject
	private ExhumedPanel exhumedPanel;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private TheatrePlugin p; //DO NOT USE. Just here for the exhumedCounter constructor.

	@Inject
	protected Xarpus(TheatrePlugin plugin, TheatreConfig config)
	{
		super(plugin, config);
	}

	@Getter
	private boolean xarpusActive;
	private boolean xarpusStarted = false;

	@Getter
	private NPC xarpusNPC;

	@Getter
	private int instanceTimer = 0;

	@Getter
	private boolean isInstanceTimerRunning = false;
	private boolean nextInstance = true;

	@Getter
	private boolean exhumedSpawned = false;

	@Getter
	private final Map<Long, Pair<GroundObject, Integer>> xarpusExhumeds = new HashMap<>();

	@Getter
	private Counter exhumedCounter;

	private int exhumedCount;

	@Getter
	private int xarpusTicksUntilAttack;

	@Getter
	private boolean postScreech = false;

	private boolean xarpusStare;

	private static BufferedImage EXHUMED_COUNT_ICON;
	private static final int GROUNDOBJECT_ID_EXHUMED = 32743;

	private static BufferedImage HEALED_COUNT_ICON;
	@Getter
	private Counter xarpusHealedCounter;
	@Getter
	private int xarpusHealedAmount;
	@Getter
	private int xarpusHealSplatCount;

	@Getter
	private boolean isHM;
	private static final Set<Integer> XARPUS_HM_ID = ImmutableSet.of(10770, 10771, 10772, 10773);

	@Override
	public void init()
	{
		EXHUMED_COUNT_ICON = ImageUtil.resizeCanvas(ImageUtil.getResourceStreamFromClass(TheatrePlugin.class, "1067-POISON.png"), 26, 26);
		HEALED_COUNT_ICON = ImageUtil.resizeCanvas(ImageUtil.getResourceStreamFromClass(TheatrePlugin.class, "healsplat.png"), 26, 26);
	}

	@Override
	public void load()
	{
		overlayManager.add(xarpusOverlay);
	}

	@Override
	public void unload()
	{
		overlayManager.remove(xarpusOverlay);
		overlayManager.remove(exhumedPanel);

		infoBoxManager.removeInfoBox(exhumedCounter);

		exhumedCounter = null;

		infoBoxManager.removeInfoBox(xarpusHealedCounter);
		xarpusHealedCounter = null;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		switch (npc.getId())
		{
			case NpcID.XARPUS:
			case NpcID.XARPUS_8339:
			case NpcID.XARPUS_8340:
			case NpcID.XARPUS_8341:
			case 10766:
			case 10767:
			case 10768:
			case 10769:
			case 10770:
			case 10771:
			case 10772:
			case 10773:
				isHM = XARPUS_HM_ID.contains(npc.getId());
				xarpusActive = true;
				xarpusNPC = npc;
				xarpusStare = false;
				xarpusTicksUntilAttack = 9;
				exhumedSpawned = false;
				postScreech = false;
				xarpusHealedAmount = 0;
				xarpusHealSplatCount = 0;
				break;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		switch (npc.getId())
		{
			case NpcID.XARPUS:
			case NpcID.XARPUS_8339:
			case NpcID.XARPUS_8340:
			case NpcID.XARPUS_8341:
			case 10766:
			case 10767:
			case 10768:
			case 10769:
			case 10770:
			case 10771:
			case 10772:
			case 10773:
				isHM = false;
				xarpusActive = false;
				xarpusNPC = null;
				xarpusStare = false;
				xarpusTicksUntilAttack = 9;
				xarpusExhumeds.clear();
				infoBoxManager.removeInfoBox(exhumedCounter);
				exhumedCounter = null;
				isInstanceTimerRunning = false;
				exhumedSpawned = false;
				postScreech = false;
				exhumedCount = -1;
				removeCounter();
				break;
		}
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		if (xarpusActive)
		{
			GroundObject o = event.getGroundObject();
			if (o.getId() == GROUNDOBJECT_ID_EXHUMED)
			{
				long hash = o.getHash();
				if (xarpusExhumeds.containsKey(hash))
				{
					return;
				}
				exhumedSpawned = true;

				if (exhumedCounter == null)
				{
					switch (TheatrePlugin.partySize)
					{
						case 5:
							exhumedCount = isHM ? 24 : 18;
							break;
						case 4:
							exhumedCount = isHM ? 20 : 15;
							break;
						case 3:
							exhumedCount = isHM ? 16 : 12;
							break;
						case 2:
							exhumedCount = isHM ? 13 : 9;
							break;
						default:
							exhumedCount = isHM ? 9 : 7;
					}

					exhumedCounter = new Counter(EXHUMED_COUNT_ICON, p, exhumedCount - 1);
					if (config.xarpusExhumedCount())
					{
						infoBoxManager.addInfoBox(exhumedCounter);
					}
					if (config.xarpusExhumedCountOverlay())
					{
						overlayManager.add(exhumedPanel);
					}
				}
				else
				{

					exhumedCounter.setCount(exhumedCounter.getCount() - 1);
				}

				xarpusExhumeds.put(hash, Pair.of(o, isHM ? 9 : 11));
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!xarpusStarted && inRoomRegion(TheatrePlugin.XARPUS_REGION) && client.getVarbitValue(client.getVarps(), 6447) == 2 && (client.getVar(Varbits.MULTICOMBAT_AREA) == 1))
		{
			xarpusStarted = true;
			isInstanceTimerRunning = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (xarpusActive)
		{
			if (!xarpusExhumeds.isEmpty())
			{
				xarpusExhumeds.replaceAll((k, v) -> Pair.of(v.getLeft(), v.getRight() - 1));
				xarpusExhumeds.values().removeIf((p) -> p.getRight() <= 0);
			}

			if (xarpusNPC.getOverheadText() != null && !xarpusStare)
			{
				xarpusStare = true;
				xarpusTicksUntilAttack = 9;
			}

			if (xarpusStare)
			{
				xarpusTicksUntilAttack--;
				if (xarpusTicksUntilAttack <= 0)
				{
					if (!postScreech)
					{
						postScreech = true;
					}
					xarpusTicksUntilAttack = 8;
				}
			}
			else if (xarpusNPC.getId() == NpcID.XARPUS_8340 || xarpusNPC.getId() == 10768 || xarpusNPC.getId() == 10772)
			{
				xarpusTicksUntilAttack--;
				if (xarpusTicksUntilAttack <= 0)
				{
					xarpusTicksUntilAttack = 4;
				}
			}

		}

		if (isInstanceTimerRunning)
		{
			instanceTimer = (instanceTimer + 1) % 4;
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}
		List<Player> players = client.getPlayers();
		for (Player player : players)
		{
			if (player.getWorldLocation() != null)
			{
				WorldPoint wpPlayer = player.getWorldLocation();
				LocalPoint lpPlayer = LocalPoint.fromWorld(client, wpPlayer.getX(), wpPlayer.getY());

				WorldPoint wpChest = WorldPoint.fromRegion(player.getWorldLocation().getRegionID(), 17, 5, player.getWorldLocation().getPlane());
				LocalPoint lpChest = LocalPoint.fromWorld(client, wpChest.getX(), wpChest.getY());
				if (lpChest != null)
				{
					Point point = new Point(lpChest.getSceneX() - lpPlayer.getSceneX(), lpChest.getSceneY() - lpPlayer.getSceneY());

					if (isInSotetsegRegion() && point.getY() == 1 && (point.getX() == 1 || point.getX() == 2 || point.getX() == 3) && nextInstance)
					{
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Xarpus instance timer started", "");
						instanceTimer = 2;
						isInstanceTimerRunning = true;
						nextInstance = false;
					}
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			nextInstance = true;
		}
	}

	boolean isInSotetsegRegion()
	{
		return inRoomRegion(TheatrePlugin.SOTETSEG_REGION_OVERWORLD) || inRoomRegion(TheatrePlugin.SOTETSEG_REGION_UNDERWORLD);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged npcDefinitionChanged)
	{
		if (xarpusActive)
		{
			NPC npc = npcDefinitionChanged.getNpc();
			if (npc.getId() == NpcID.XARPUS_8340 || npc.getId() == 10768 || npc.getId() == 10772)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Times Healed: <col=ff0000>" + xarpusHealSplatCount, "");
				infoBoxManager.removeInfoBox(exhumedCounter);
				exhumedCounter = null;
				overlayManager.remove(exhumedPanel);
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied e)
	{
		if (e.getActor() instanceof NPC)
		{
			Actor npc = e.getActor();
			Hitsplat.HitsplatType type = e.getHitsplat().getHitsplatType();
			if (npc == xarpusNPC && type == Hitsplat.HitsplatType.HEAL)
			{
				xarpusHealedAmount += e.getHitsplat().getAmount();
				addCounter();
				updateCounter();
				++xarpusHealSplatCount;
			}
		}
	}

	private void updateCounter()
	{
		if (xarpusHealedCounter != null)
		{
			xarpusHealedCounter.setCount(xarpusHealedAmount);
		}
	}

	private void addCounter()
	{
		if (config.xarpusHealingCount() && xarpusHealedCounter == null)
		{
			xarpusHealedCounter = new Counter(HEALED_COUNT_ICON, plugin, xarpusHealedAmount);
			xarpusHealedCounter.setTooltip("Xarpus Heals");
			infoBoxManager.addInfoBox(xarpusHealedCounter);
		}
	}

	private void removeCounter()
	{
		if (xarpusHealedCounter != null)
		{
			infoBoxManager.removeInfoBox(xarpusHealedCounter);
			xarpusHealedAmount = 0;
			xarpusHealSplatCount = 0;
			xarpusHealedCounter = null;
		}
	}
}
