/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.pvpplayerindicators;

import com.google.inject.Provides;
import com.openosrs.client.util.PvPUtil;
import lombok.Value;
import net.runelite.api.*;
import net.runelite.api.clan.ClanTitle;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.FriendsChatRank.UNRANKED;
import static net.runelite.api.MenuAction.*;

@Extension
@PluginDescriptor(
		name = "<html><font color=#25c550>[S] Player Indicators",
		description = "Highlight players on-screen and/or on the minimap",
		tags = {"highlight", "minimap", "overlay", "players"},
		conflicts = "Player Indicators"
)
public class PvPPlayerIndicatorsPlugin extends Plugin
{
	private static final String TRADING_WITH_TEXT = "Trading with: ";

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PvPPlayerIndicatorsConfig config;

	@Inject
	private PvPPlayerIndicatorsOverlay playerIndicatorsOverlay;

	@Inject
	private PvPPlayerIndicatorsTileOverlay playerIndicatorsTileOverlay;

	@Inject
	private PvPPlayerIndicatorsTrueTile playerIndicatorsTrueTile;

	@Inject
	private PvPPlayerIndicatorsHullOverlay playerIndicatorsHullOverlay;

	@Inject
	private PvPPlayerIndicatorsMinimapOverlay playerIndicatorsMinimapOverlay;

	@Inject
	private PvPTargetHighlightOverlay targetHighlightOverlay;

	@Inject
	private PvPPlayerIndicatorsService playerIndicatorsService;

	@Inject
	private Client client;

	@Inject
	private ChatIconManager chatIconManager;

	@Inject
	private ClientThread clientThread;

	private boolean mirrorMode;

	@Provides
	PvPPlayerIndicatorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PvPPlayerIndicatorsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(playerIndicatorsOverlay);
		overlayManager.add(playerIndicatorsTileOverlay);
		overlayManager.add(playerIndicatorsTrueTile);
		overlayManager.add(playerIndicatorsHullOverlay);
		overlayManager.add(playerIndicatorsMinimapOverlay);
		overlayManager.add(targetHighlightOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(playerIndicatorsOverlay);
		overlayManager.remove(playerIndicatorsTileOverlay);
		overlayManager.remove(playerIndicatorsTrueTile);
		overlayManager.remove(playerIndicatorsHullOverlay);
		overlayManager.remove(playerIndicatorsMinimapOverlay);
		overlayManager.remove(targetHighlightOverlay);
	}

	private LocalPoint GELocation = null;

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == 10063)
			GELocation = event.getTile().getLocalLocation();
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event) {
		if (config.playerAlertSound()) {
			Player player = event.getPlayer();
			if (!player.getName().equalsIgnoreCase(client.getLocalPlayer().getName()) &&
					PvPUtil.isAttackable(client, player) && !player.isFriendsChatMember() && !player.isFriend() &&
					PvPUtil.isAttackable(client, client.getLocalPlayer())) {
				if (GELocation != null) {
					LocalPoint playerloc = client.getLocalPlayer().getLocalLocation();
					if (playerloc.distanceTo(GELocation) < 3000) {
						System.out.println(playerloc.distanceTo(GELocation));
						return;
					}
				}
				client.playSoundEffect(3924, config.playerAlertSoundVolume());
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		/*if (client.isMirrored() && !mirrorMode) {
			targetHighlightOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(targetHighlightOverlay);
			overlayManager.add(targetHighlightOverlay);
			playerIndicatorsOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(playerIndicatorsOverlay);
			overlayManager.add(playerIndicatorsOverlay);
			mirrorMode = true;
		}*/

		if (client.isMenuOpen())
		{
			return;
		}

		MenuEntry[] menuEntries = client.getMenuEntries();
		boolean modified = false;

		for (MenuEntry entry : menuEntries)
		{
			int type = entry.getType().getId();

			if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET)
			{
				type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
			}

			if (type == MenuAction.WALK.getId() || type == WIDGET_TARGET_ON_PLAYER.getId()
					|| type == MenuAction.ITEM_USE_ON_PLAYER.getId() || type == MenuAction.PLAYER_FIRST_OPTION.getId()
					|| type == MenuAction.PLAYER_SECOND_OPTION.getId() || type == MenuAction.PLAYER_THIRD_OPTION.getId()
					|| type == MenuAction.PLAYER_FOURTH_OPTION.getId() || type == MenuAction.PLAYER_FIFTH_OPTION.getId()
					|| type == MenuAction.PLAYER_SIXTH_OPTION.getId() || type == MenuAction.PLAYER_SEVENTH_OPTION.getId()
					|| type == MenuAction.PLAYER_EIGTH_OPTION.getId() || type == MenuAction.RUNELITE_PLAYER.getId())
			{
				Player[] players = client.getCachedPlayers();
				Player player = null;

				int identifier = entry.getIdentifier();

				// 'Walk here' identifiers are offset by 1 because the default
				// identifier for this option is 0, which is also a player index.
				if (type == MenuAction.WALK.getId())
				{
					identifier--;
				}

				if (identifier >= 0 && identifier < players.length)
				{
					player = players[identifier];
				}

				if (player != null) {
					Decorations decorations = getDecorations(player);
					if (decorations != null) {
						String oldTarget = entry.getTarget();
						String newTarget = decorateTarget(oldTarget, decorations);
						entry.setTarget(newTarget);
						modified = true;
					}
				}
			}
		}
		if (modified)
			client.setMenuEntries(menuEntries);
	}

	private Decorations getDecorations(Player player)
	{
		int image = -1;
		Color color = null;

		if (config.highlightFriends() && client.isFriended(player.getName(), false))
		{
			color = config.getFriendColor();
		}
		else if (config.drawFriendsChatMemberNames() && player.isFriendsChatMember())
		{
			color = config.getFriendsChatMemberColor();

			FriendsChatRank rank = playerIndicatorsService.getFriendsChatRank(player);
			if (rank != UNRANKED)
			{
				image = chatIconManager.getIconNumber(rank);
			}
		}
		else if (config.highlightTeamMembers() && player.getTeam() > 0 && client.getLocalPlayer().getTeam() == player.getTeam())
		{
			color = config.getTeamMemberColor();
		}
		else if (player.isClanMember() && config.highlightClanMembers())
		{
			color = config.getClanMemberColor();

			if (config.showClanChatRanks())
			{
				ClanTitle clanTitle = playerIndicatorsService.getClanTitle(player);
				if (clanTitle != null)
				{
					image = chatIconManager.getIconNumber(clanTitle);
				}
			}
		}
		else if (config.highlightOthers() && !player.isFriendsChatMember() && !player.isClanMember())
		{
			color = config.getOthersColor();
		}
		else if (PvPUtil.isAttackable(client, player) && !player.isFriendsChatMember() && !player.isFriend())
		{
			color = config.getTargetColor();
		}

		if (image == -1 && color == null)
		{
			return null;
		}

		return new Decorations(image, color);
	}

	private String decorateTarget(String oldTarget, Decorations decorations)
	{
		String newTarget = oldTarget;

		if (decorations.getColor() != null && config.colorPlayerMenu())
		{
			// strip out existing <col...
			int idx = oldTarget.indexOf('>');
			if (idx != -1)
			{
				newTarget = oldTarget.substring(idx + 1);
			}

			newTarget = ColorUtil.prependColorTag(newTarget, decorations.getColor());
		}

		if (decorations.getImage() != -1 && config.showFriendsChatRanks())
		{
			newTarget = "<img=" + decorations.getImage() + ">" + newTarget;
		}

		return newTarget;
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.TRADE_MAIN_INIT)
		{
			clientThread.invokeLater(() ->
			{
				Widget tradeTitle = client.getWidget(WidgetInfo.TRADE_WINDOW_HEADER);
				String header = tradeTitle.getText();
				String playerName = header.substring(TRADING_WITH_TEXT.length());

				Player targetPlayer = findPlayer(playerName);
				if (targetPlayer == null)
				{
					return;
				}

				Decorations playerColor = getDecorations(targetPlayer);
				if (playerColor != null)
				{
					tradeTitle.setText(TRADING_WITH_TEXT + ColorUtil.wrapWithColorTag(playerName, playerColor.color));
				}
			});
		}
	}

	private Player findPlayer(String name)
	{
		for (Player player : client.getPlayers())
		{
			if (player.getName().equals(name))
			{
				return player;
			}
		}
		return null;
	}

	@Value
	private static class Decorations
	{
		int image;
		Color color;
	}
}