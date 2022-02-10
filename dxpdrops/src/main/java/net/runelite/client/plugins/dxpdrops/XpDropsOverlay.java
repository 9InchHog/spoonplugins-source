package net.runelite.client.plugins.dxpdrops;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.experiencedrop.XpDropConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class XpDropsOverlay extends Overlay {
    private final Client client;
    private final XpDropsPlugin plugin;
    private final XpDropsConfig config;
    private final XpDropConfig xpDropConfig;
    private BufferedImage[] stat_icons;
    private BufferedImage fake_icon;
    private static final int NUM_STATS = 23;
    private static final int STAT_FAKE = 31;
    private static final int MELEE_ON = 100666422;
    private static final int RANGE_ON = 22282240;
    private static final int MAGE_ON = 145227776;
    private static final int MIN_SEP = 24;
    private static final int QUEUE_SIZE = 10;
    private final int[] drop_offset = new int[QUEUE_SIZE];
    private final int[] drop_value = new int[QUEUE_SIZE];
    private final int[] drop_stats = new int[QUEUE_SIZE];
    private final int[] drop_praymode = new int[QUEUE_SIZE];
    private int[] stat_display_order;

    @Inject
    public XpDropsOverlay(Client client, XpDropsPlugin plugin, XpDropsConfig config, ConfigManager configManager) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_RIGHT);
        this.setResizable(true);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.xpDropConfig = configManager.getConfig(XpDropConfig.class);
    }

    public Dimension render(Graphics2D g) {
        if (fake_icon == null) {
            init();
        }

        if (plugin.font == null) {
            plugin.load_settings();
        }

        Widget xpWidget = client.getWidget(122, 0);
        if (xpWidget == null) {
            return null;
        }
        update();
        int h = Math.max(100, getBounds().height);
        int x0 = 0;
        int last_y = -MIN_SEP;

        for (int i = 0; i < QUEUE_SIZE; ++i) {
            int y = ++drop_offset[i] * plugin.speed / 2;

            if (y < last_y + MIN_SEP) {
                y = last_y + MIN_SEP;
            }

            last_y = y;
            int heightLimit = h + (50 / plugin.speed);
            if (drop_stats[i] != 0 && drop_offset[i] < heightLimit) {
                draw_drop(g, i, x0, h - y);
            }
        }

        return new Dimension(37, h);
    }

    private void draw_drop(Graphics2D g, int i, int x0, int y0) {
        int xoff = 0;
        String xpDrop = Integer.toString(drop_value[i]);
        g.setFont(plugin.font);
        FontRenderContext fontRenderCtx = g.getFontRenderContext();
        Rectangle2D bounds = plugin.font.getStringBounds(xpDrop, fontRenderCtx);
        int xpDrop_Bounds = (int) bounds.getWidth();
        int xRight = x0 + 37;
        if (!xpDropConfig.hideSkillIcons()) {
            int j;
            if (config.showFakeXpIcon() && (drop_stats[i] & Integer.MIN_VALUE) != 0) {
                xoff += fake_icon.getWidth() + 3;
                j = y0 - 17 + (MIN_SEP - fake_icon.getHeight()) / 2;
                g.drawImage(fake_icon, xRight - xpDrop_Bounds - xoff, j, null);
            }

            for (j = 22; j >= 0; --j) {
                int st = stat_display_order[j];
                if ((drop_stats[i] & 1 << st) != 0) {
                    xoff += stat_icons[st].getWidth() + 3;
                    int y = y0 - 17 + (MIN_SEP - stat_icons[st].getHeight()) / 2;
                    g.drawImage(stat_icons[st], xRight - xpDrop_Bounds - xoff, y, null);
                }
            }
        }

        if (config.antiAlias()) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        g.setColor(Color.BLACK);
        g.drawString(Integer.toString(drop_value[i]), xRight - xpDrop_Bounds + 1, y0 + 1);
        Color c;
        switch (drop_praymode[i]) {
            case 0:
                c = plugin.color;
                break;
            case 1:
                c = xpDropConfig.getMeleePrayerColor();
                break;
            case 2:
                c = xpDropConfig.getRangePrayerColor();
                break;
            default:
                c = xpDropConfig.getMagePrayerColor();
        }

        g.setColor(c);
        g.drawString(Integer.toString(drop_value[i]), xRight - xpDrop_Bounds, y0);
    }

    private void update() {
        int[] gain = new int[plugin.curr_xp.length];

        int varp;
        for(varp = 0; varp < plugin.curr_xp.length; ++varp) {
            if (plugin.prev_xp[varp] > 0) {
                gain[varp] = plugin.fake_xp[varp] + plugin.curr_xp[varp] - plugin.prev_xp[varp];
            }
        }

        varp = plugin.varps[83];
        int praymode = 0;
        if ((varp & 0x1540000) != 0) {
            if (gain[4] > 0) {
                praymode = 2;
            }
        } else if ((varp & 0x8A80000) != 0) {
            if (gain[6] > 0) {
                praymode = 3;
            }
        } else if ((varp & 0x6000C36) != 0 && (gain[0] > 0 || gain[2] > 0 || gain[1] > 0)) {
            praymode = 1;
        }

        int sum_gain = 0;
        int statset = 0;
        byte b;
        int j;
        int[] arrayOfInt1;
        for (j = (arrayOfInt1 = stat_display_order).length, b = 0; b < j; ) {
            int st = arrayOfInt1[b];
            if (gain[st] > 0) {
                int stat = 1 << st;
                if (plugin.fake_xp[st] > 0)
                    stat |= Integer.MIN_VALUE;
                if (!plugin.group) {
                    queue_drop(gain[st], stat, praymode);
                } else {
                    statset |= stat;
                }
                sum_gain += gain[st];
            }
            plugin.prev_xp[st] = plugin.curr_xp[st];
            plugin.fake_xp[st] = 0;
            b++;
        }
        if (this.plugin.group && sum_gain > 0)
            queue_drop(sum_gain, statset, praymode);
    }

    private void queue_drop(int value, int stats, int praymode) {
        for (int i = QUEUE_SIZE-1; i > 0; --i) {
            drop_offset[i] = drop_offset[i - 1];
            drop_value[i] = drop_value[i - 1];
            drop_stats[i] = drop_stats[i - 1];
            drop_praymode[i] = drop_praymode[i - 1];
        }

        drop_offset[0] = 0;
        drop_value[0] = value;
        drop_stats[0] = stats;
        drop_praymode[0] = praymode;
    }

    private void init() {
        stat_display_order = new int[]{10, 0, 2, 4, 6, 1, 3, 5, 16, 15, 17, 12, 20, 14, 13, 7, 11, 8, 9, 18, 19, 22, 21};
        stat_icons = new BufferedImage[NUM_STATS];
        load_staticon(0, 197);
        load_staticon(1, 199);
        load_staticon(2, 198);
        load_staticon(3, 203);
        load_staticon(4, 200);
        load_staticon(5, 201);
        load_staticon(6, 202);
        load_staticon(7, 212);
        load_staticon(8, 214);
        load_staticon(9, 208);
        load_staticon(10, 211);
        load_staticon(11, 213);
        load_staticon(12, 207);
        load_staticon(13, 210);
        load_staticon(14, 209);
        load_staticon(15, 205);
        load_staticon(16, 204);
        load_staticon(17, 206);
        load_staticon(18, 216);
        load_staticon(19, 217);
        load_staticon(20, 215);
        load_staticon(21, 220);
        load_staticon(22, 221);
        fake_icon = Objects.requireNonNull(client.getSprites(client.getIndexSprites(), 423, 0))[11].toBufferedImage();
    }

    private void load_staticon(int stat, int icon) {
        stat_icons[stat] = Objects.requireNonNull(client.getSprites(client.getIndexSprites(), icon, 0))[0].toBufferedImage();
    }
}