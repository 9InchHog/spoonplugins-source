package net.runelite.client.plugins.zuktimer;

import com.openosrs.client.game.NPCManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] Zuk Set Timer",
	enabledByDefault = false,
	description = "Displays an infobox of when the next set is going to spawn at zuk",
	tags = {"zuk", "inferno", "timer", "set"}
)
@Slf4j
public class ZukTimerPlugin extends Plugin
{
	private static final int INFERNO_REGION = 9043;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private NPCManager npcManager;

	@Getter(AccessLevel.PACKAGE)
	private boolean finalPhase = false;

	@Getter(AccessLevel.PACKAGE)
	private NPC zukShield = null;
	private NPC zuk = null;

	@Getter(AccessLevel.PACKAGE)
	private long lastTick;

	private ZukSetInfobox spawnTimerInfoBox;

	@Override
	protected void startUp()
	{
	}

	@Override
	protected void shutDown()
	{
		infoBoxManager.removeInfoBox(spawnTimerInfoBox);
	}

	@Subscribe
	private void onGameTick(GameTick event)
	{
		if (!isInInferno())
		{
			return;
		}

		lastTick = System.currentTimeMillis();

		calculateSpawnTimerInfobox();
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		if (!isInInferno())
		{
			return;
		}

		final int npcId = event.getNpc().getId();

		if (npcId == NpcID.ANCESTRAL_GLYPH)
		{
			zukShield = event.getNpc();
			return;
		}

		switch (npcId)
		{
			case NpcID.JALZEK:
			case NpcID.JALZEK_7703:
				if (zuk != null && spawnTimerInfoBox != null)
				{
					spawnTimerInfoBox.reset();
					spawnTimerInfoBox.run();
				}
				break;
			case NpcID.TZKALZUK:
				finalPhase = false;
				log.debug("[INFERNO] Zuk spawn detected, not in final phase");

				zuk = event.getNpc();

				if (spawnTimerInfoBox != null)
				{
					infoBoxManager.removeInfoBox(spawnTimerInfoBox);
				}

				spawnTimerInfoBox = new ZukSetInfobox(itemManager.getImage(ItemID.TZREKZUK), this);
				infoBoxManager.addInfoBox(spawnTimerInfoBox);

				break;
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event)
	{
		if (!isInInferno())
		{
			return;
		}

		int npcId = event.getNpc().getId();

		switch (npcId)
		{
			case NpcID.ANCESTRAL_GLYPH:
				zukShield = null;
				return;
			case NpcID.TZKALZUK:
				zuk = null;

				if (spawnTimerInfoBox != null)
				{
					infoBoxManager.removeInfoBox(spawnTimerInfoBox);
				}

				spawnTimerInfoBox = null;
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!isInInferno())
		{
			zukShield = null;
			zuk = null;

			if (spawnTimerInfoBox != null)
			{
				infoBoxManager.removeInfoBox(spawnTimerInfoBox);
			}

			spawnTimerInfoBox = null;
		}
	}

	private boolean isInInferno()
	{
		return ArrayUtils.contains(client.getMapRegions(), INFERNO_REGION);
	}

	private void calculateSpawnTimerInfobox()
	{
		if (zuk == null || finalPhase || spawnTimerInfoBox == null)
		{
			return;
		}

		final int pauseHp = 600;
		final int resumeHp = 480;

		int hp = calculateNpcHp(zuk.getHealthRatio(), zuk.getHealthScale(), npcManager.getHealth(zuk.getId()));

		if (hp <= 0)
		{
			return;
		}

		if (spawnTimerInfoBox.isRunning())
		{
			if (hp >= resumeHp && hp < pauseHp)
			{
				spawnTimerInfoBox.pause();
			}
		}
		else
		{
			if (hp < resumeHp)
			{
				spawnTimerInfoBox.run();
			}
		}
	}

	private static int calculateNpcHp(int ratio, int health, int maxHp)
	{
		// See OpponentInfo Plugin
		// Copyright (c) 2016-2018, Adam <Adam@sigterm.info>
		// Copyright (c) 2018, Jordan Atwood <jordan.atwood423@gmail.com>

		if (ratio < 0 || health <= 0 || maxHp == -1)
		{
			return -1;
		}

		int exactHealth = 0;

		if (ratio > 0)
		{
			int minHealth = 1;
			int maxHealth;

			if (health > 1)
			{
				if (ratio > 1)
				{
					minHealth = (maxHp * (ratio - 1) + health - 2) / (health - 1);
				}

				maxHealth = (maxHp * ratio - 1) / (health - 1);

				if (maxHealth > maxHp)
				{
					maxHealth = maxHp;
				}
			}
			else
			{
				maxHealth = maxHp;
			}

			exactHealth = (minHealth + maxHealth + 1) / 2;
		}

		return exactHealth;
	}
}