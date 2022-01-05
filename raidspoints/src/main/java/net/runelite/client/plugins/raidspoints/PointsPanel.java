package net.runelite.client.plugins.raidspoints;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PointsPanel extends PluginPanel {
    private final Map<data, XpInfoBox> infoBoxes = new HashMap<>();

    private final JLabel overallPointsGained = new JLabel(XpInfoBox.htmlLabel("Personal Points: ", 0));

    private final JLabel overallPointsPerHour = new JLabel(XpInfoBox.htmlLabel("Per Hour: ", 0));

    private final JLabel teamPointsGained = new JLabel(XpInfoBox.htmlLabel("Team Points: ", 0));

    private final JLabel teamPointsPerHour = new JLabel(XpInfoBox.htmlLabel("Per Hour: ", 0));

    private final JLabel raidsCompleted = new JLabel(XpInfoBox.htmlLabel("Raids Done: ", 0));

    private final JLabel averageTime = new JLabel(XpInfoBox.htmlLabel("Avg. Raid Time: ", "00:00:00"));

    private final JLabel averagePoints = new JLabel(XpInfoBox.htmlLabel("Avg. Points: ", 0));

    private final JLabel averagePointsTeam = new JLabel(XpInfoBox.htmlLabel("Avg. Points: ", 0));

    private final JLabel elapsedTime = new JLabel(XpInfoBox.htmlLabel("Elapsed Time: ", "00:00:00"));

    private final JLabel blankline1 = new JLabel("<html><br></html>");

    private final JLabel blankline = new JLabel("<html><br></html>");

    final JMenuItem pausePopup = new JMenuItem("Pause Timer");

    final JButton pauseButton = new JButton("Pause Timer");

    private final JLabel blankline2 = new JLabel("<html><br></html>");

    public Date finalTime = new Date();

    public Date tempTime = new Date();

    public boolean paused = false;

    final JPanel overallInfo = new JPanel();

    private final JPanel overallPanel = new JPanel();

    public static ArrayList<data> raids = new ArrayList<>();

    static final JPanel infoBoxPanel = new JPanel();

    public boolean soloMode;

    @Inject
    private static RaidsPointsPlugin raidsPlugin;

    @Inject
    private RaidsPointsConfig raidsConfig;

    @Inject
    private static Client client;

    @Inject
    private static SkillIconManager iconManager;

    timerSpecial timer = new timerSpecial();

    ActionListener action = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            PointsPanel.this.updateTime();
        }
    };

    @Provides
    RaidsPointsConfig provideConfig(ConfigManager configManager) {
        return (RaidsPointsConfig)configManager.getConfig(RaidsPointsConfig.class);
    }

    PointsPanel(RaidsPointsPlugin raidsPlugin, RaidsPointsConfig raidsConfig, Client client, SkillIconManager iconManager) {
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());
        JPanel layoutPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layoutPanel, 1);
        layoutPanel.setLayout(boxLayout);
        add(layoutPanel, "North");
        this.overallPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.overallPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.overallPanel.setLayout(new BorderLayout());
        this.overallPanel.setVisible(true);
        JMenuItem reset = new JMenuItem("Reset Tracker");
        reset.addActionListener(e -> resetAll(true));
        this.pausePopup.addActionListener(e -> pauseTime());
        this.pauseButton.addActionListener(e -> pauseTime());
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(reset);
        popupMenu.add(this.pausePopup);
        this.overallPanel.setComponentPopupMenu(popupMenu);
        BufferedImage image = ImageUtil.loadImageResource(getClass(), "cox.png");
        ImageIcon i = new ImageIcon(image);
        JLabel overallIcon = new JLabel(i);
        this.overallInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.overallInfo.setLayout(new GridLayout(12, 1));
        this.overallInfo.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.pauseButton.setFont(FontManager.getRunescapeSmallFont());
        this.overallInfo.add(this.pauseButton);
        this.overallPointsGained.setFont(FontManager.getRunescapeSmallFont());
        this.overallPointsPerHour.setFont(FontManager.getRunescapeSmallFont());
        this.teamPointsGained.setFont(FontManager.getRunescapeSmallFont());
        this.raidsCompleted.setFont(FontManager.getRunescapeSmallFont());
        this.averagePoints.setFont(FontManager.getRunescapeSmallFont());
        this.averageTime.setFont(FontManager.getRunescapeSmallFont());
        this.teamPointsPerHour.setFont(FontManager.getRunescapeSmallFont());
        this.blankline1.setFont(FontManager.getRunescapeSmallFont());
        this.blankline.setFont(FontManager.getRunescapeSmallFont());
        this.blankline2.setFont(FontManager.getRunescapeSmallFont());
        this.averagePointsTeam.setFont(FontManager.getRunescapeSmallFont());
        this.elapsedTime.setFont(FontManager.getRunescapeSmallFont());
        this.overallInfo.add(this.teamPointsGained);
        this.overallInfo.add(this.teamPointsPerHour);
        this.overallInfo.add(this.averagePointsTeam);
        this.overallInfo.add(this.blankline1);
        this.overallInfo.add(this.overallPointsGained);
        this.overallInfo.add(this.overallPointsPerHour);
        this.overallInfo.add(this.averagePoints);
        this.overallInfo.add(this.blankline2);
        this.overallInfo.add(this.raidsCompleted);
        this.overallInfo.add(this.averageTime);
        this.overallInfo.add(this.elapsedTime);
        this.overallPanel.add(overallIcon, "West");
        this.overallPanel.add(this.overallInfo, "Center");
        infoBoxPanel.setLayout(new BoxLayout(infoBoxPanel, 1));
        layoutPanel.add(this.overallPanel);
        layoutPanel.add(infoBoxPanel);
        try {
            for (data d : raids)
                infoBoxPanel.add(new XpInfoBox(raidsPlugin, raidsConfig, client, infoBoxPanel, d, iconManager));
        } catch (IOException iOException) {}
    }

    XpInfoBox test(data d) {
        try {
            XpInfoBox x = new XpInfoBox(raidsPlugin, this.raidsConfig, client, infoBoxPanel, d, iconManager);
            return x;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    SoloBox testS(data d) {
        try {
            SoloBox x = new SoloBox(raidsPlugin, this.raidsConfig, client, infoBoxPanel, d, iconManager);
            return x;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void init() {}

    void update() {
        int totalPoints = 0;
        int totalTeamPoints = 0;
        float teamperhour = 0.0F;
        infoBoxPanel.removeAll();
        Object startTime = null;
        Object finishTime = null;
        int totalTime = 0;
        float difference = 0.0F;
        int raidsDone = 0;
        for (data d : raids) {
            System.out.println(d.raid);
            SwingUtilities.invokeLater(() -> infoBoxPanel.add(test(d)));
            totalPoints += d.personal;
            totalTeamPoints += d.total;
            raidsDone++;
            totalTime += d.timeTaken;
        }
        if (this.timer.getElapsedTime() > 0L) {
            difference = (float)this.timer.getElapsedTime();
            teamperhour = totalTeamPoints / difference * 3600.0F;
            difference = totalPoints / difference * 3600.0F;
        }
        this.overallInfo.removeAll();
        this.overallInfo.setLayout(new GridLayout(12, 1));
        this.teamPointsPerHour.setVisible(true);
        this.teamPointsGained.setVisible(true);
        this.overallInfo.add(this.pauseButton);
        this.overallInfo.add(this.teamPointsGained);
        this.overallInfo.add(this.teamPointsPerHour);
        this.overallInfo.add(this.averagePointsTeam);
        this.overallInfo.add(this.blankline1);
        this.overallInfo.add(this.overallPointsGained);
        this.overallInfo.add(this.overallPointsPerHour);
        this.overallInfo.add(this.averagePoints);
        this.overallInfo.add(this.blankline2);
        this.overallInfo.add(this.raidsCompleted);
        this.overallInfo.add(this.averageTime);
        this.overallInfo.add(this.elapsedTime);
        this.overallPointsGained.setText(XpInfoBox.htmlLabel("Personal Points: ", totalPoints));
        this.overallPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", (int)difference));
        this.teamPointsGained.setText(XpInfoBox.htmlLabel("Team Points: ", totalTeamPoints));
        this.teamPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", (int)teamperhour));
        this.raidsCompleted.setText(XpInfoBox.htmlLabel("Raids Done: ", raidsDone));
        if (raidsDone > 0) {
            this.elapsedTime.setText(XpInfoBox.htmlLabel("Elapsed Time: ", formatSeconds((int)this.timer.getElapsedTime())));
            this.averageTime.setText(XpInfoBox.htmlLabel("Avg. Raid Time: ", formatSeconds(totalTime / raidsDone)));
            this.averagePoints.setText(XpInfoBox.htmlLabel("Avg. Points: ", totalPoints / raidsDone));
            this.averagePointsTeam.setText(XpInfoBox.htmlLabel("Avg. Points: ", totalTeamPoints / raidsDone));
        } else {
            this.elapsedTime.setText(XpInfoBox.htmlLabel("Elapsed Time: ", "00:00:00"));
            this.averageTime.setText(XpInfoBox.htmlLabel("Avg. Raid Time: ", "00:00:00"));
            this.averagePoints.setText(XpInfoBox.htmlLabel("Avg. Points: ", 0));
            this.averagePointsTeam.setText(XpInfoBox.htmlLabel("Avg. Points: ", 0));
        }
        infoBoxPanel.revalidate();
    }

    void updateSolo() {
        int totalPoints = 0;
        int totalTeamPoints = 0;
        float teamperhour = 0.0F;
        infoBoxPanel.removeAll();
        Date startTime = null;
        Date finishTime = null;
        int totalTime = 0;
        float difference = 0.0F;
        int raidsDone = 0;
        if (raids.size() > 0) {
            startTime = ((data)raids.get(0)).start;
            finishTime = ((data)raids.get(raids.size() - 1)).finish;
            difference = (float)getDateDiff(startTime, finishTime, TimeUnit.SECONDS);
        }
        for (data d : raids) {
            SwingUtilities.invokeLater(() -> infoBoxPanel.add(testS(d)));
            totalPoints += d.personal;
            totalTeamPoints += d.total;
            raidsDone++;
            totalTime += d.timeTaken;
        }
        if (this.timer.getElapsedTime() > 0L) {
            difference = (float)this.timer.getElapsedTime();
            teamperhour = totalTeamPoints / difference * 3600.0F;
            difference = totalPoints / difference * 3600.0F;
        }
        this.overallInfo.removeAll();
        this.overallInfo.setLayout(new GridLayout(7, 1));
        this.overallInfo.add(this.pauseButton);
        this.overallInfo.add(this.overallPointsGained);
        this.overallInfo.add(this.overallPointsPerHour);
        this.overallInfo.add(this.blankline);
        this.overallInfo.add(this.raidsCompleted);
        this.overallInfo.add(this.averageTime);
        this.overallInfo.add(this.elapsedTime);
        this.overallPointsGained.setText(XpInfoBox.htmlLabel("Personal Points: ", totalPoints));
        this.overallPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", (int)difference));
        this.teamPointsGained.setText(XpInfoBox.htmlLabel("Team Points: ", totalTeamPoints));
        this.teamPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", (int)teamperhour));
        this.raidsCompleted.setText(XpInfoBox.htmlLabel("Raids Done: ", raidsDone));
        if (raidsDone > 0) {
            this.elapsedTime.setText(XpInfoBox.htmlLabel("Elapsed Time: ", formatSeconds((int)this.timer.getElapsedTime())));
            this.averageTime.setText(XpInfoBox.htmlLabel("Avg. Raid Time: ", formatSeconds(totalTime / raidsDone)));
            this.averagePoints.setText(XpInfoBox.htmlLabel("Avg. Points: ", totalPoints / raidsDone));
            this.averagePointsTeam.setText(XpInfoBox.htmlLabel("Avg. Points: ", totalTeamPoints / raidsDone));
        } else {
            this.elapsedTime.setText(XpInfoBox.htmlLabel("Elapsed Time: ", "00:00:00"));
            this.averageTime.setText(XpInfoBox.htmlLabel("Avg. Raid Time: ", "00:00:00"));
            this.averagePoints.setText(XpInfoBox.htmlLabel("Avg. Points: ", 0));
            this.averagePointsTeam.setText(XpInfoBox.htmlLabel("Avg. Points: ", 0));
        }
        this.teamPointsPerHour.setVisible(false);
        this.teamPointsGained.setVisible(false);
        infoBoxPanel.revalidate();
    }

    void unpauseTime() {
        if (!this.timer.started)
            return;
        this.timer.unpause();
        this.pausePopup.setText("Pause Timer");
        this.pauseButton.setText("Pause Timer");
        for (ActionListener d : this.pausePopup.getActionListeners())
            this.pausePopup.removeActionListener(d);
        for (ActionListener d : this.pauseButton.getActionListeners())
            this.pauseButton.removeActionListener(d);
        this.pausePopup.addActionListener(e -> pauseTime());
        this.pauseButton.addActionListener(e -> pauseTime());
    }

    public void updateTime() {
        this.finalTime = new Date();
        int totalPoints = 0;
        int totalTeamPoints = 0;
        int raidsDone = 0;
        int totalTime = 0;
        if (raids.size() > 0) {
            for (data d : raids) {
                totalPoints += d.personal;
                totalTeamPoints += d.total;
                raidsDone++;
                totalTime += d.timeTaken;
            }
            if (this.timer.getElapsedTime() > 0L) {
                int difference = (int)this.timer.getElapsedTime();
                int teamperhour = (int)(totalTeamPoints / difference * 3600.0F);
                difference = (int)(totalPoints / difference * 3600.0F);
                this.overallPointsGained.setText(XpInfoBox.htmlLabel("Personal Points: ", totalPoints));
                this.overallPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", difference));
                this.teamPointsGained.setText(XpInfoBox.htmlLabel("Team Points: ", totalTeamPoints));
                this.teamPointsPerHour.setText(XpInfoBox.htmlLabel("Per Hour: ", teamperhour));
            }
            this.elapsedTime.setText(XpInfoBox.htmlLabel("Elapsed Time: ", formatSeconds((int)this.timer.getElapsedTime())));
            revalidate();
        }
    }

    void pauseTime() {
        if (!this.timer.started)
            return;
        this.timer.pause();
        this.pausePopup.setText("Resume Timer");
        this.pauseButton.setText("Resume Timer");
        for (ActionListener d : this.pausePopup.getActionListeners())
            this.pausePopup.removeActionListener(d);
        for (ActionListener d : this.pauseButton.getActionListeners())
            this.pauseButton.removeActionListener(d);
        this.pausePopup.addActionListener(e -> unpauseTime());
        this.pauseButton.addActionListener(e -> unpauseTime());
    }

    void resetAll(boolean solo) {
        this.timer = new timerSpecial();
        this.pausePopup.setText("Pause Timer");
        this.pauseButton.setText("Pause Timer");
        for (ActionListener d : this.pausePopup.getActionListeners())
            this.pausePopup.removeActionListener(d);
        for (ActionListener d : this.pauseButton.getActionListeners())
            this.pauseButton.removeActionListener(d);
        this.pausePopup.addActionListener(e -> pauseTime());
        this.pauseButton.addActionListener(e -> pauseTime());
        raids.clear();
        if (this.soloMode) {
            updateSolo();
        } else {
            update();
        }
    }

    void setSolo(boolean solo) {
        this.soloMode = solo;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        Object formattedTime = "";
        if (hours < 10)
            formattedTime = (String)formattedTime + "0";
        formattedTime = (String)formattedTime + hours + ":";
        if (minutes < 10)
            formattedTime = (String)formattedTime + "0";
        formattedTime = (String)formattedTime + minutes + ":";
        if (seconds < 10)
            formattedTime = (String)formattedTime + "0";
        formattedTime = (String)formattedTime + seconds;
        return (String)formattedTime;
    }
}
