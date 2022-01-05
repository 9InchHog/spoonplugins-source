package net.runelite.client.plugins.coxfloorsplits;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Cox Floor Splits"
)
public class CoxFloorSplitsPlugin extends Plugin
{
    private static final Pattern LEVEL_COMPLETE_REGEX = Pattern.compile("(.+) level complete! Duration: ([0-9:]+)");
    private static final Pattern RAID_COMPLETE_REGEX = Pattern.compile("Congratulations - your raid is complete!");

    private static final int RAID_TIMER_VARBIT = 6386;
    private static final int RAID_STATE_VARBIT = 5425;
    private static final int RAID_BANK_REGION = 4919;
    private static final int RAID_POINT_WIDGET_SCRIPT = 1510;

    @Inject
    private Client client;

    @Inject
    private CoxFloorSplitsOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    @Getter
    private boolean inRaidChambers;

    public int raidState;
    public int timerVarb;
    public int upperTime = -1;
    public int middleTime = -1;
    public int lowerTime = -1;
    public int raidTime = -1;
    public String upperFloorTime = "";
    public String middleFloorTime = "";
    public String lowerFloorTime = "";
    public String olmTime = "";
    public int olmStart = -1;

    @Getter
    private String tooltip;

    @Provides
    CoxFloorSplitsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CoxFloorSplitsConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);

        clientThread.invoke(() -> setHidden(true));
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);

        clientThread.invoke(() -> setHidden(false));
        reset();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        inRaidChambers = client.getVar(Varbits.IN_RAID) == 1;
        if(!inRaidChambers){
            reset();
        }
        raidState = client.getVarbitValue(RAID_STATE_VARBIT);
        timerVarb = client.getVarbitValue(RAID_TIMER_VARBIT);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event)
    {
        if (event.getScriptId() != RAID_POINT_WIDGET_SCRIPT || !inRaidChambers)
        {
            return;
        }

        Widget widget = client.getWidget(WidgetInfo.RAIDS_POINTS_INFOBOX);

        if (widget == null || widget.isHidden())
        {
            return;
        }

        widget.setHidden(true);
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (timerVarb > 0 && raidState < 5)
        {
            //mimic the script when the widget is hidden
            client.runScript(2289, 0, 0, 0);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (inRaidChambers && event.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION)
        {
            String message = Text.removeTags(event.getMessage());
            Matcher matcher;

            matcher = LEVEL_COMPLETE_REGEX.matcher(message);
            if (matcher.find())
            {
                String floor = matcher.group(1);
                int time = timeToSeconds(matcher.group(2));
                if (floor.equals("Upper"))
                {
                    upperTime = time;
                }
                else if (floor.equals("Middle"))
                {
                    middleTime = time;
                }
                else if (floor.equals("Lower"))
                {
                    lowerTime = time;
                    olmStart = (int) Math.floor(client.getVarbitValue(6386) * .6);
                }
                updateTooltip();
            }

            if (event.getMessage().contains("Congratulations - your raid is complete!"))
            {
                raidTime = timeToSeconds(getTime());
                updateTooltip();
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        // lazy way to reset
        if (event.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null && client.getVar(Varbits.IN_RAID) != 1)
        {
            reset();
        }
    }

    private void setHidden(boolean shouldHide)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        inRaidChambers = client.getVar(Varbits.IN_RAID) == 1;

        if (!inRaidChambers)
        {
            return;
        }

        final Widget widget = client.getWidget(WidgetInfo.RAIDS_POINTS_INFOBOX);
        if (widget != null)
        {
            widget.setHidden(shouldHide);
        }
    }

    public void reset()
    {
        upperTime = -1;
        middleTime = -1;
        lowerTime = -1;
        raidTime = -1;
        tooltip = null;
        upperFloorTime = "";
        middleFloorTime = "";
        lowerFloorTime = "";
        olmTime = "";
    }

    private int timeToSeconds(String s)
    {
        int seconds = -1;
        String[] split = s.split(":");
        if (split.length == 2)
        {
            seconds = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        }
        if (split.length == 3)
        {
            seconds = Integer.parseInt(split[0]) * 3600 + Integer.parseInt(split[1]) * 60 + Integer.parseInt(split[2]);
        }
        return seconds;
    }

    public String secondsToTime(int seconds)
    {
        StringBuilder builder = new StringBuilder();
        if (seconds >= 3600)
        {
            builder.append((int)Math.floor(seconds / 3600) + ":");
        }
        seconds %= 3600;
        if (builder.toString().equals(""))
        {
            builder.append((int)Math.floor(seconds / 60));
        }
        else
        {
            builder.append(StringUtils.leftPad(String.valueOf((int)Math.floor(seconds / 60)), 2, '0'));
        }
        builder.append(":");
        seconds %= 60;
        builder.append(StringUtils.leftPad(String.valueOf(seconds), 2, '0'));
        return builder.toString();
    }

    private void updateTooltip()
    {
        StringBuilder builder = new StringBuilder();
        if (upperTime == -1)
        {
            tooltip = null;
            return;
        }
        builder.append("Upper level: " + secondsToTime(upperTime));
        upperFloorTime = secondsToTime(upperTime);

        if (middleTime == -1)
        {
            if (lowerTime == -1)
            {
                tooltip = builder.toString();
                return;
            }
            else
            {
                builder.append("</br>Lower level: " + secondsToTime(lowerTime - upperTime));
                lowerFloorTime = secondsToTime(lowerTime - upperTime);
            }
        }
        else
        {
            builder.append("</br>Middle level: " + secondsToTime(middleTime - upperTime));
            middleFloorTime = secondsToTime(middleTime - upperTime);
            if (lowerTime == -1)
            {
                tooltip = builder.toString();
                return;
            }
            else
            {
                builder.append("</br>Lower level: " + secondsToTime(lowerTime - middleTime));
                lowerFloorTime = secondsToTime(lowerTime - middleTime);
            }
        }
        if (raidTime == -1)
        {
            tooltip = builder.toString();
            return;
        }
        builder.append("</br>Olm: " + secondsToTime(raidTime - lowerTime));
        olmTime = secondsToTime(raidTime - lowerTime);
        tooltip = builder.toString();
    }

    String getTime()
    {
        int seconds = (int) Math.floor(client.getVarbitValue(RAID_TIMER_VARBIT) * .6);
        return secondsToTime(seconds);
    }
}