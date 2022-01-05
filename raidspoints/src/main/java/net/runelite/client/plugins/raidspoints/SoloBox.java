package net.runelite.client.plugins.raidspoints;

import net.runelite.api.Client;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

class SoloBox extends JPanel {
    private static final DecimalFormat TWO_DECIMAL_FORMAT = new DecimalFormat("0.00");

    static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");

    private static final String HTML_TOOL_TIP_TEMPLATE = "<html>%s %s done<br/>%s %s/hr<br/>%s till goal lvl</html>";

    private static final String HTML_LABEL_TEMPLATE = "<html><body style='color:%s'>%s<span style='color:white'>%s</span></body></html>";

    private final JPanel panel;

    private final data raid;

    private final JPanel container = new JPanel();

    private final JPanel headerPanel = new JPanel();

    private final JPanel statsPanel = new JPanel();

    private final JLabel personalPoints = new JLabel();

    private final JLabel hourlyPoints = new JLabel();

    private final JLabel totalPoints = new JLabel();

    private final JLabel raidTime = new JLabel();

    private final JLabel layout = new JLabel();

    private final JLabel teamHour = new JLabel();

    private final JLabel killCount = new JLabel();

    private final JMenuItem pauseSkill = new JMenuItem("Pause");

    private RaidsPointsConfig raidsConfig;

    private boolean paused = false;

    SoloBox(RaidsPointsPlugin raidsPlugin, RaidsPointsConfig raidsConfig, Client client, JPanel panel, data raid, SkillIconManager iconManager) throws IOException {
        this.raidsConfig = raidsConfig;
        this.panel = panel;
        this.raid = raid;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 0, 0));
        this.container.setLayout(new BorderLayout());
        this.container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        BufferedImage image = ImageUtil.getResourceStreamFromClass(getClass(), "cox.png");
        ImageIcon i = new ImageIcon(image);
        JLabel skillIcon = new JLabel(i);
        skillIcon.setHorizontalAlignment(0);
        skillIcon.setVerticalAlignment(0);
        skillIcon.setPreferredSize(new Dimension(35, 35));
        this.headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.headerPanel.setLayout(new BorderLayout());
        this.statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.statsPanel.setBorder(new EmptyBorder(9, 2, 9, 2));
        this.statsPanel.setLayout((LayoutManager)new DynamicGridLayout(2, 2));
        this.personalPoints.setFont(FontManager.getRunescapeSmallFont());
        this.personalPoints.setText("Personal: " + POINTS_FORMAT.format(raid.personal));
        this.hourlyPoints.setFont(FontManager.getRunescapeSmallFont());
        this.hourlyPoints.setText("Points/Hr: " + POINTS_FORMAT.format(raid.hr));
        this.totalPoints.setFont(FontManager.getRunescapeSmallFont());
        this.totalPoints.setText("Total: " + POINTS_FORMAT.format(raid.total));
        this.teamHour.setFont(FontManager.getRunescapeSmallFont());
        float var10002 = raid.total / raid.timeTaken;
        this.teamHour.setText("Points/Hr: " + POINTS_FORMAT.format((var10002 * 3600.0F)));
        this.killCount.setFont(FontManager.getRunescapeSmallFont());
        this.killCount.setText("KC: " + raid.kc);
        this.raidTime.setFont(FontManager.getRunescapeSmallFont());
        this.raidTime.setText("Time: " + formatSeconds(raid.timeTaken));
        this.statsPanel.add(this.personalPoints);
        this.statsPanel.add(this.hourlyPoints);
        this.statsPanel.add(this.killCount);
        this.statsPanel.add(this.raidTime);
        this.headerPanel.add(skillIcon, "West");
        this.headerPanel.add(this.statsPanel, "Center");
        JPanel progressWrapper = new JPanel();
        progressWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        progressWrapper.setLayout(new BorderLayout());
        progressWrapper.setBorder(new EmptyBorder(0, 7, 7, 7));
        this.layout.setFont(FontManager.getRunescapeSmallFont());
        if (raid.cm) {
            this.layout.setText("<html><p>Challenge Mode</p></html>");
        } else {
            this.layout.setText("<html><p>" + raid.raid + "</p></html>");
        }
        progressWrapper.add(this.layout, "North");
        this.container.add(this.headerPanel, "North");
        this.container.add(progressWrapper, "South");
        add(this.container, "North");
    }

    public static String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        String formattedTime = "";
        if (hours < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + hours + ":";
        if (minutes < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + minutes + ":";
        if (seconds < 10)
            formattedTime = formattedTime + "0";
        formattedTime = formattedTime + seconds;
        return formattedTime;
    }

    data getRaid() {
        return this.raid;
    }
}
