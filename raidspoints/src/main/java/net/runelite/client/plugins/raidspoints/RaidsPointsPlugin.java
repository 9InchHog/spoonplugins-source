package net.runelite.client.plugins.raidspoints;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
@PluginDescriptor(
        name = "[S] Raids Points",
        description = "Show Points information for the Chambers of Xeric raid - yoinked from Steroid",
        tags = {"combat", "raid", "overlay", "pve", "pvm", "bosses"}
)
public class RaidsPointsPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(RaidsPointsPlugin.class);

    private static final String RAID_START_MESSAGE = "The raid has begun!";

    private static final String KC_MESSAGE = "Your completed Chambers of Xeric count is:";

    private static final String KC_MESSAGECM = "Your completed Chambers of Xeric Challenge Mode count is:";

    private static final String RAID_COMPLETE_MESSAGE = "Congratulations - your raid is complete!";

    private static final String RAID_COMPLETE_MESSAGE2 = "Congratulations - your raid is complete! Duration:";

    private static final Pattern RAIDS_DURATION_PATTERN = Pattern.compile("Duration: (?<duration>[0-9:]+)");

    @Inject
    private SkillIconManager iconManager;

    @Inject
    private Client client;

    @Inject
    private RaidsPointsConfig config;

    private PointsPanel pointsPanel;

    private NavigationButton navButton2;

    @Inject
    private ClientToolbar clientToolbar;

    data raidToAdd = new data();

    int ticks = 0;

    @Provides
    RaidsPointsConfig provideConfig(ConfigManager configManager) {
        return (RaidsPointsConfig)configManager.getConfig(RaidsPointsConfig.class);
    }

    protected void startUp() throws Exception {
        this.ticks = 0;
        this.pointsPanel = new PointsPanel(this, this.config, this.client, this.iconManager);
        this.pointsPanel.init();
        if (this.config.soloPanel()) {
            this.pointsPanel.setSolo(true);
            this.pointsPanel.updateSolo();
        } else {
            this.pointsPanel.setSolo(false);
            this.pointsPanel.update();
        }
        BufferedImage iconPoint = ImageUtil.loadImageResource(getClass(), "cox.png");
        this.navButton2 = NavigationButton.builder().tooltip("CoX Points").icon(iconPoint).priority(9).panel(this.pointsPanel).build();
        if (this.config.ptsPanel()) {
            this.clientToolbar.addNavigation(this.navButton2);
        }
    }

    protected void shutDown() throws Exception {
        this.clientToolbar.removeNavigation(this.navButton2);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("raidspoints") && this.config.ptsPanel()) {
            this.clientToolbar.addNavigation(this.navButton2);
            if (this.config.soloPanel()) {
                this.pointsPanel.updateSolo();
                this.pointsPanel.setSolo(true);
            } else {
                this.pointsPanel.update();
                this.pointsPanel.setSolo(false);
            }
            this.pointsPanel.revalidate();
        } else if (event.getGroup().equals("raidspoints") && !this.config.ptsPanel()){
            this.clientToolbar.removeNavigation(this.navButton2);
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        this.ticks++;
        if (this.ticks > 20) {
            if (this.config.soloPanel()) {
                this.pointsPanel.updateSolo();
            } else {
                this.pointsPanel.update();
            }
            this.ticks = 0;
        }
        this.pointsPanel.updateTime();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        String text = Text.removeTags(event.getMessage());
        if (this.raidToAdd != null)
            this.raidToAdd.cm = false;
        if (text.startsWith("Your completed Chambers of Xeric count is:")) {
            PointsPanel var10000 = this.pointsPanel;
            PointsPanel var10001 = this.pointsPanel;
            ((data)PointsPanel.raids.get(PointsPanel.raids.size() - 1)).kc = Integer.parseInt(text.replaceAll("\\D+", ""));
            var10000 = this.pointsPanel;
            var10001 = this.pointsPanel;
            ((data)PointsPanel.raids.get(PointsPanel.raids.size() - 1)).cm = false;
            if (this.config.soloPanel()) {
                this.pointsPanel.updateSolo();
            } else {
                this.pointsPanel.update();
            }
        }
        if (text.startsWith("Your completed Chambers of Xeric Challenge Mode count is:")) {
            PointsPanel var10000 = this.pointsPanel;
            PointsPanel var10001 = this.pointsPanel;
            ((data)PointsPanel.raids.get(PointsPanel.raids.size() - 1)).kc = Integer.parseInt(text.replaceAll("\\D+", ""));
            var10000 = this.pointsPanel;
            var10001 = this.pointsPanel;
            PointsPanel.raids.get(PointsPanel.raids.size() - 1).cm = true;
            if (this.config.soloPanel()) {
                this.pointsPanel.updateSolo();
            } else {
                this.pointsPanel.update();
            }
        }
        if (this.client.getVar(Varbits.IN_RAID) == 1 && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION) {
            String message = Text.removeTags(event.getMessage());
            if (message.startsWith("The raid has begun!"))
                this.raidToAdd.start = new Date();
            if (message.startsWith("Congratulations - your raid is complete!"))
                this.raidToAdd.finish = new Date();
            if (message.startsWith("Congratulations - your raid is complete!")) {
                Matcher matcher2 = RAIDS_DURATION_PATTERN.matcher(message);
                if (matcher2.find())
                    parseTime(matcher2);
            }
        }
    }

    private static int timeStringToSeconds(String timeString) {
        String[] s = timeString.split(":");
        if (s.length == 2)
            return Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
        return (s.length == 3) ? (Integer.parseInt(s[0]) * 60 * 60 + Integer.parseInt(s[1]) * 60 + Integer.parseInt(s[2])) : Integer.parseInt(timeString);
    }

    private void parseTime(Matcher matcher) {
        int seconds = timeStringToSeconds(matcher.group("duration"));
        this.raidToAdd.timeTaken = seconds;
        this.raidToAdd.personal = this.client.getVar(Varbits.PERSONAL_POINTS);
        this.raidToAdd.total = this.client.getVar(Varbits.TOTAL_POINTS);
        this.raidToAdd.hr = (int)(this.raidToAdd.personal / this.raidToAdd.timeTaken * 3600.0F);
        if (this.config.ptsPanel()) {
            log.info("RAID TIME: {}", seconds);
            PointsPanel var10000 = this.pointsPanel;
            PointsPanel.raids.add(this.raidToAdd);
            if (!this.pointsPanel.timer.started)
                this.pointsPanel.timer.start(this.raidToAdd.timeTaken);
            if (this.config.soloPanel()) {
                this.pointsPanel.updateSolo();
            } else {
                this.pointsPanel.update();
            }
            this.raidToAdd = new data();
        }
    }
}
