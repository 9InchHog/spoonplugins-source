package net.runelite.client.plugins.tzhaartimers;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;


import static net.runelite.api.ItemID.FIRE_CAPE;
import static net.runelite.api.ItemID.INFERNAL_CAPE;

@Extension
@PluginDescriptor(
        name = "[b] Tzhaar Timers",
        description = "Display elapsed time in the Fight Caves and Inferno",
        tags = {"inferno", "fight", "caves", "cape", "timer", "tzhaar"}
)
@Slf4j
public class TzhaarTimersPlugin extends Plugin
{
    private static final String START_MESSAGE = "Wave: 1";
    private static final String WAVE_MESSAGE = "Wave:";
    private static final String DEFEATED_MESSAGE = "You have been defeated!";
    private static final String INFERNO_COMPLETE_MESSAGE = "Your TzKal-Zuk kill count is:";
    private static final String FIGHT_CAVES_COMPLETE_MESSAGE = "Your TzTok-Jad kill count is:";
    private static final String INFERNO_PAUSED_MESSAGE = "The Inferno has been paused. You may now log out.";
    private static final String FIGHT_CAVE_PAUSED_MESSAGE = "The Fight Cave has been paused. You may now log out.";
    private static final String WAVE9 = "Wave: 9";
    private static final String WAVE18 = "Wave: 18";
    private static final String WAVE25 = "Wave: 25";
    private static final String WAVE35 = "Wave: 35";
    private static final String WAVE42 = "Wave: 42";
    private static final String WAVE50 = "Wave: 50";
    private static final String WAVE57 = "Wave: 57";
    private static final String WAVE60 = "Wave: 60";
    private static final String WAVE63 = "Wave: 63";
    private static final String WAVE66 = "Wave: 66";
    private static final String WAVE67 = "Wave: 67";
    private static final String WAVE68 = "Wave: 68";
    private static final String WAVE69 = "Wave: 69";

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private Client client;

    @Inject
    private TzhaarTimersConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    @Getter
    private TzhaarTimers timer;

    private Instant startTime;

    private Instant originalTime;

    private Instant lastTime;

    private boolean started;

    private boolean loggingIn;

    private LocalTime time;

    @Provides
    TzhaarTimersConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(TzhaarTimersConfig.class);
    }

    @Override
    protected void shutDown() throws Exception
    {
        removeTimer();
        resetConfig();
        startTime = null;
        originalTime = null;

        lastTime = null;
        started = false;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("tzhaartimers"))
        {
            return;
        }

        if (event.getKey().equals("tzhaarTimers"))
        {
            updateInfoBoxState();
            return;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case LOGGED_IN:
                if (loggingIn)
                {
                    loggingIn = false;
                    loadConfig();
                    resetConfig();
                }
                break;
            case LOGGING_IN:
                loggingIn = true;
                break;
            case LOADING:
                if (!loggingIn)
                {
                    updateInfoBoxState();
                }
                break;
            case HOPPING:
                loggingIn = true;
            case LOGIN_SCREEN:
                removeTimer();
                saveConfig();
                break;
            default:
                break;
        }
    }
    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (config.tzhaarTimers() && (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM))
        {
            String message = Text.removeTags(event.getMessage());
            Instant now = Instant.now();
            if (!started && message.contains(START_MESSAGE))
            {
                started = true;

                now = now.minus(6, ChronoUnit.SECONDS);
                if (checkInFightCaves())
                {
                    createTimer(FIRE_CAPE, now, null);
                }
                if (checkInInferno())
                {
                    createTimer(INFERNAL_CAPE, now, null);
                }
                startTime = now;
                originalTime = now;
                return;
            }
            if (started)
            {
                if (message.contains(WAVE_MESSAGE))
                {

                    if (lastTime != null)
                    {
                        startTime = startTime.plus(Duration.between(startTime, now)).minus(Duration.between(startTime, lastTime));
                        lastTime = null;
                    }
                    if (checkInFightCaves())
                    {
                        infoBoxManager.removeInfoBox(timer);
                        createTimer(FIRE_CAPE, startTime, lastTime);
                    }
                    if (checkInInferno())
                    {
                        infoBoxManager.removeInfoBox(timer);
                        createTimer(INFERNAL_CAPE, startTime, lastTime);
                    }
                }
                if (message.contains(FIGHT_CAVE_PAUSED_MESSAGE) || message.contains(INFERNO_PAUSED_MESSAGE))
                {
                    if (checkInFightCaves())
                    {
                        infoBoxManager.removeInfoBox(timer);
                        createTimer(FIRE_CAPE, startTime, now);
                    }
                    if (checkInInferno())
                    {
                        infoBoxManager.removeInfoBox(timer);
                        createTimer(INFERNAL_CAPE, startTime, now);
                    }
                    lastTime = now;
                }

                if (message.contains(WAVE9) || message.contains(WAVE18) || message.contains(WAVE25) ||
                        message.contains(WAVE35) || message.contains(WAVE42) || message.contains(WAVE50) ||
                        message.contains(WAVE57) || message.contains(WAVE60) || message.contains(WAVE63)
                        || message.contains(WAVE66) || message.contains(WAVE67) || message.contains(WAVE68) ||
                        message.contains(WAVE69))
                {
                    printMessage();

                }

                if (message.contains(DEFEATED_MESSAGE) || message.contains(INFERNO_COMPLETE_MESSAGE) || message.contains(FIGHT_CAVES_COMPLETE_MESSAGE))
                {
                    removeTimer();
                    resetConfig();
                    startTime = null;
                    lastTime = null;
                    started = false;
                }


            }
        }
    }

    private void printMessage() {
        if (originalTime == null) {
            return;
        }
        Instant now = Instant.now();
        time = LocalTime.ofSecondOfDay(now.getEpochSecond() - this.originalTime.getEpochSecond());

        if (time.getHour() > 0)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Wave Split: </col=ff0000>" + time.format(DateTimeFormatter.ofPattern("HH:mm")), null, false);
        }
        else {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Wave Split: </col=ff0000>" + time.format(DateTimeFormatter.ofPattern("mm:ss")), null, false);
        }
    }

    private void updateInfoBoxState()
    {
        if (timer == null)
        {
            return;
        }

        if ((!checkInFightCaves() && !checkInInferno()) || !config.tzhaarTimers())
        {
            removeTimer();
            resetConfig();
            startTime = null;
            lastTime = null;
            started = false;
        }
    }

    private boolean checkInFightCaves()
    {
        return client.getMapRegions() != null && Arrays.stream(client.getMapRegions())
                .filter(x -> x == 9551)
                .toArray().length > 0;
    }

    private boolean checkInInferno()
    {
        return client.getMapRegions() != null && Arrays.stream(client.getMapRegions())
                .filter(x -> x == 9043)
                .toArray().length > 0;
    }

    private void removeTimer()
    {
        infoBoxManager.removeInfoBox(timer);
        timer = null;
    }

    private void createTimer(int id, Instant time, Instant lTime)
    {
        timer = new TzhaarTimers(itemManager.getImage(id), this, time, lTime);
        infoBoxManager.addInfoBox(timer);
    }

    private void loadConfig()
    {
        startTime = configManager.getConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_TIME, Instant.class);
        Boolean temp = configManager.getConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_STARTED, Boolean.class);
        if (temp != null)
        {
            started = temp;
        }
        lastTime = configManager.getConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_LASTTIME, Instant.class);

    }

    private void resetConfig()
    {
        configManager.unsetConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_TIME);
        configManager.unsetConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_STARTED);
        configManager.unsetConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_LASTTIME);
    }

    private void saveConfig()
    {
        if (startTime != null)
        {
            resetConfig();
            if (lastTime == null)
            {
                lastTime = Instant.now();
            }
            configManager.setConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_TIME, startTime);
            configManager.setConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_STARTED, started);
            configManager.setConfiguration(TzhaarTimersConfig.CONFIG_GROUP, TzhaarTimersConfig.CONFIG_LASTTIME, lastTime);
            startTime = null;
            lastTime = null;
            started = false;
            originalTime = null;
        }
    }
}