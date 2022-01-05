/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.runelite.api.*;
import net.runelite.api.clan.ClanTitle;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.hiscore.HiscoreEndpoint;
import net.runelite.client.hiscore.HiscoreManager;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Singleton
public class PvPPlayerIndicatorsOverlay extends Overlay
{
	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 40;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final PvPPlayerIndicatorsService playerIndicatorsService;
	private final PvPPlayerIndicatorsConfig config;
	private final ChatIconManager chatIconManager;
	private final HiscoreManager hiscoreManager;

	private final BufferedImage agilityIcon = ImageUtil.loadImageResource(PvPPlayerIndicatorsPlugin.class, "agility.png");

	private final BufferedImage noAgilityIcon = ImageUtil.loadImageResource(PvPPlayerIndicatorsPlugin.class, "no-agility.png");

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private PvPPlayerIndicatorsOverlay(PvPPlayerIndicatorsConfig config, PvPPlayerIndicatorsService playerIndicatorsService,
									   ChatIconManager chatIconManager, HiscoreManager hiscoreManager)
	{
		this.config = config;
		this.playerIndicatorsService = playerIndicatorsService;
		this.chatIconManager = chatIconManager;
		this.hiscoreManager = hiscoreManager;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		playerIndicatorsService.forEachPlayer((player, color) -> renderPlayerOverlay(graphics, player, color));
		return null;
	}

	private void renderPlayerOverlay(Graphics2D graphics, Player actor, Color color)
	{
		final PvPPlayerNameLocation drawPlayerNamesConfig = config.playerNamePosition();
		if (drawPlayerNamesConfig == PvPPlayerNameLocation.DISABLED)
		{
			return;
		}

		final int zOffset;
		switch (drawPlayerNamesConfig)
		{
			case MODEL_CENTER:
			case MODEL_RIGHT:
				zOffset = actor.getLogicalHeight() / 2;
				break;
			default:
				zOffset = actor.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		}

		String name = Text.sanitize(actor.getName());
		Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);

		if (drawPlayerNamesConfig == PvPPlayerNameLocation.MODEL_RIGHT)
		{
			textLocation = actor.getCanvasTextLocation(graphics, "", zOffset);

			if (textLocation == null)
			{
				return;
			}

			textLocation = new Point(textLocation.getX() + ACTOR_HORIZONTAL_TEXT_MARGIN, textLocation.getY());
		}

		if (textLocation == null)
		{
			return;
		}

		//different

		BufferedImage rankImage = null;
		if (actor.isFriendsChatMember() && config.drawFriendsChatMemberNames() && config.showFriendsChatRanks())
		{
			final FriendsChatRank rank = playerIndicatorsService.getFriendsChatRank(actor);

			if (rank != FriendsChatRank.UNRANKED)
			{
				rankImage = chatIconManager.getRankImage(rank);
			}
		}
		else if (actor.isClanMember() && config.highlightClanMembers() && config.showClanChatRanks())
		{
			ClanTitle clanTitle = playerIndicatorsService.getClanTitle(actor);
			if (clanTitle != null)
			{
				rankImage = chatIconManager.getRankImage(clanTitle);
			}
		}

		if (rankImage != null)
		{
			final int imageWidth = rankImage.getWidth();
			final int imageTextMargin;
			final int imageNegativeMargin;

			if (drawPlayerNamesConfig == PvPPlayerNameLocation.MODEL_RIGHT)
			{
				imageTextMargin = imageWidth;
				imageNegativeMargin = 0;
			}
			else
			{
				imageTextMargin = imageWidth / 2;
				imageNegativeMargin = imageWidth / 2;
			}

			final int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
			final Point imageLocation = new Point(textLocation.getX() - imageNegativeMargin - 1, textLocation.getY() - textHeight / 2 - rankImage.getHeight() / 2);
			OverlayUtil.renderImageLocation(graphics, imageLocation, rankImage);

			// move text
			textLocation = new Point(textLocation.getX() + imageTextMargin, textLocation.getY());
		}

		if (this.config.showCombatLevel())
			name = name + " (" + actor.getCombatLevel() + ")";
		if (config.showAgilityLevel() && checkWildy()) {
			HiscoreResult hiscoreResult = this.hiscoreManager.lookupAsync(actor.getName(), HiscoreEndpoint.NORMAL);
			if (hiscoreResult != null) {
				int level = hiscoreResult.getSkill(HiscoreSkill.AGILITY).getLevel();
				if (config.agilityFormat() == PvPPlayerIndicatorsConfig.AgilityFormats.ICONS) {
					int width = graphics.getFontMetrics().stringWidth(name);
					int height = graphics.getFontMetrics().getHeight();
					if (level >= config.agilityFirstThreshold())
						OverlayUtil.renderImageLocation(graphics, new Point(textLocation
										.getX() + 5 + width, textLocation
										.getY() - height),
								ImageUtil.resizeImage(this.agilityIcon, height, height));
					if (level >= config.agilitySecondThreshold())
						OverlayUtil.renderImageLocation(graphics, new Point(textLocation
										.getX() + this.agilityIcon.getWidth() + width, textLocation
										.getY() - height),
								ImageUtil.resizeImage(this.agilityIcon, height, height));
					if (level < config.agilityFirstThreshold())
						OverlayUtil.renderImageLocation(graphics, new Point(textLocation
										.getX() + 5 + width, textLocation
										.getY() - height),
								ImageUtil.resizeImage(this.noAgilityIcon, height, height));
				} else {
					name = name + " " + level;
					int width = graphics.getFontMetrics().stringWidth(name);
					int height = graphics.getFontMetrics().getHeight();
					OverlayUtil.renderImageLocation(graphics, new Point(textLocation
									.getX() + 5 + width, textLocation
									.getY() - height),
							ImageUtil.resizeImage(this.agilityIcon, height, height));
				}
			}
		}
		OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
	}

	private boolean checkWildy() {
		return (this.client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isPvpWorld(this.client.getWorldType()));
	}
}