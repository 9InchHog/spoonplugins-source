package net.runelite.client.plugins.socket.plugins.playerindicatorsextended;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.clan.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.plugins.playerindicators.PlayerNameLocation;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

public class PlayerIndicatorsExtendedOverlay extends Overlay {
    private final Client client;

    private final PlayerIndicatorsExtendedPlugin plugin;

    private final PlayerIndicatorsExtendedConfig config;

    private final ChatIconManager chatIconManager;

    @Inject
    ConfigManager configManager;

    @Inject
    private PlayerIndicatorsExtendedOverlay(Client client, PlayerIndicatorsExtendedPlugin plugin, PlayerIndicatorsExtendedConfig config, ChatIconManager chatIconManager) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.chatIconManager = chatIconManager;
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.DYNAMIC);
    }

    public Dimension render(Graphics2D graphics)
    {
        for (Actor actor : this.plugin.getPlayers())
        {
            Player p = (Player) actor;
            int zOffset = actor.getLogicalHeight() + 40;
            String name = Text.sanitize(actor.getName());
            Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);

            if (textLocation == null)
            {
                return null;
            }

            BufferedImage rankImage = null;
            if (p.isFriendsChatMember() && configManager.getConfiguration("playerindicators", "drawClanMemberNames").equals("true") && configManager.getConfiguration("playerindicators", "clanMenuIcons").equals("true"))
            {
                final FriendsChatRank rank = getFriendsChatRank(actor);

                if (rank != FriendsChatRank.UNRANKED)
                {
                    rankImage = chatIconManager.getRankImage(rank);
                }
            }
            else if (p.isClanMember() && configManager.getConfiguration("playerindicators", "drawClanChatMemberNames").equals("true") && configManager.getConfiguration("playerindicators", "clanchatMenuIcons").equals("true"))
            {
                ClanTitle clanTitle = getClanTitle(actor);
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
                imageTextMargin = imageWidth / 2;
                imageNegativeMargin = imageWidth / 2;

                final int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
                final Point imageLocation = new Point(textLocation.getX() - imageNegativeMargin - 1, textLocation.getY() - textHeight / 2 - rankImage.getHeight() / 2);
                OverlayUtil.renderImageLocation(graphics, imageLocation, rankImage);

                // move text
                textLocation = new Point(textLocation.getX() + imageTextMargin, textLocation.getY());
            }
            OverlayUtil.renderTextLocation(graphics, textLocation, name, this.config.nameColor());
        }
        return null;
    }

    ClanTitle getClanTitle(Actor player)
    {
        ClanChannel clanChannel = client.getClanChannel();
        ClanSettings clanSettings = client.getClanSettings();
        if (clanChannel == null || clanSettings == null)
        {
            return null;
        }

        ClanChannelMember member = clanChannel.findMember(player.getName());
        if (member == null)
        {
            return null;
        }

        ClanRank rank = member.getRank();
        return clanSettings.titleForRank(rank);
    }

    FriendsChatRank getFriendsChatRank(Actor actor)
    {
        final FriendsChatManager friendsChatManager = client.getFriendsChatManager();
        if (friendsChatManager == null)
        {
            return FriendsChatRank.UNRANKED;
        }

        FriendsChatMember friendsChatMember = friendsChatManager.findByName(actor.getName());
        return friendsChatMember != null ? friendsChatMember.getRank() : FriendsChatRank.UNRANKED;
    }
}
