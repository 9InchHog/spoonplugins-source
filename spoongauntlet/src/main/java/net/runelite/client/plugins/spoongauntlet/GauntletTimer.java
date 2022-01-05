package net.runelite.client.plugins.spoongauntlet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class GauntletTimer extends Overlay {
    private final Client client;

    private final SpoonGauntletPlugin plugin;

    private final SpoonGauntletConfig config;

    private final PanelComponent panelComponent = new PanelComponent();

    public enum RaidState {
        UNKNOWN, IN_RAID, IN_BOSS;
    }

    public long timeRaidStart = -1L;

    public long timeBossEnter = -1L;

    public RaidState currentState = RaidState.UNKNOWN;

    public void resetStates() {
        this.timeRaidStart = -1L;
        this.timeBossEnter = -1L;
        this.currentState = RaidState.UNKNOWN;
    }

    public void initStates() {
        this.timeRaidStart = -1L;
        this.timeBossEnter = -1L;
        if (GauntletUtils.inRaid(this.client)) {
            this.currentState = RaidState.IN_RAID;
            if (GauntletUtils.inBoss(this.client))
                this.currentState = RaidState.IN_BOSS;
        } else {
            this.currentState = RaidState.UNKNOWN;
        }
    }

    private String calculateElapsedTime(long epochA, long epochB) {
        long max = Math.max(epochA, epochB);
        long min = Math.min(epochA, epochB);
        long elapsedEpoch = max - min;
        long seconds = elapsedEpoch / 1000L;
        long minutes = seconds / 60L;
        seconds %= 60L;
        if (seconds == 0L)
            return minutes + ":00";
        if (seconds < 10L)
            return minutes + ":0" + seconds;
        return minutes + ":" + seconds;
    }

    public void checkStates(boolean checkVarps) {
        Player p = this.client.getLocalPlayer();
        if (p == null || !this.plugin.completeStartup)
            return;
        if (checkVarps) {
            if (this.currentState == RaidState.UNKNOWN) {
                if (GauntletUtils.inRaid(this.client) && p.getHealthRatio() != 0)
                    if (!GauntletUtils.inBoss(this.client)) {
                        this.currentState = RaidState.IN_RAID;
                        this.timeRaidStart = System.currentTimeMillis();
                    } else {
                        this.currentState = RaidState.IN_RAID;
                        this.timeRaidStart = this.timeBossEnter = System.currentTimeMillis();
                    }
            } else if (this.currentState == RaidState.IN_RAID) {
                if (GauntletUtils.inRaid(this.client)) {
                    if (GauntletUtils.inBoss(this.client)) {
                        printPrepTime();
                        this.currentState = RaidState.IN_BOSS;
                        this.timeBossEnter = System.currentTimeMillis();
                    }
                } else {
                    printPrepTime();
                    resetStates();
                }
            } else if (this.currentState == RaidState.IN_BOSS && (
                    !GauntletUtils.inBoss(this.client) || !GauntletUtils.inRaid(this.client))) {
                resetStates();
            }
        } else if (this.currentState == RaidState.IN_BOSS &&
                p.getHealthRatio() == 0) {
            printBossTime();
            resetStates();
        }
    }

    private void printPrepTime() {
        if (!this.config.displayTimerChat() || this.timeRaidStart == -1L)
            return;
        String elapsedTime = calculateElapsedTime(System.currentTimeMillis(), this.timeRaidStart);
    }

    private void printBossTime() {
        if (!this.config.displayTimerChat() || this.timeRaidStart == -1L || this.timeBossEnter == -1L)
            return;
        String elapsedBossTime = calculateElapsedTime(System.currentTimeMillis(), this.timeBossEnter);
        String elapsedPrepTime = calculateElapsedTime(this.timeRaidStart, this.timeBossEnter);
        String elapsedTotalTime = calculateElapsedTime(System.currentTimeMillis(), this.timeRaidStart);
        Widget transparentChatbox = this.client.getWidget(WidgetInfo.CHATBOX_TRANSPARENT_BACKGROUND);
        if (transparentChatbox.getChildren() != null && (transparentChatbox.getChildren()).length > 1) {
            this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Challenge duration: <col=ff0000>" + elapsedTotalTime + "<col=ffffff>.", null);
            this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Preparation time: <col=ff0000>" + elapsedPrepTime + "<col=ffffff>. Player death time: <col=ff0000>" + elapsedBossTime + "<col=ffffff>.", null);
        } else {
            this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Challenge duration: <col=ff0000>" + elapsedTotalTime + "<col=000000>.", null);
            this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Preparation time: <col=ff0000>" + elapsedPrepTime + "<col=000000>. Player death time: <col=ff0000>" + elapsedBossTime + "<col=000000>.", null);
        }
    }

    @Inject
    public GauntletTimer(Client client, SpoonGauntletPlugin plugin, SpoonGauntletConfig config) {
        super(plugin);
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Gauntlet Timer Overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.currentState == RaidState.UNKNOWN)
            return null;
        this.panelComponent.getChildren().clear();
        this.panelComponent.getChildren().add(TitleComponent.builder().text("Gauntlet Timer").color(Color.WHITE).build());
        if (this.timeRaidStart == -1L) {
            this.panelComponent.getChildren().add(LineComponent.builder().left("Inactive").right("0:00").build());
        } else {
            String elapsedPrepTime, elapsedBossTime, elapsedTotalTime = calculateElapsedTime(System.currentTimeMillis(), this.timeRaidStart);
            if (this.currentState == RaidState.IN_RAID) {
                elapsedPrepTime = calculateElapsedTime(this.timeRaidStart, System.currentTimeMillis());
                elapsedBossTime = "0:00";
            } else {
                elapsedPrepTime = calculateElapsedTime(this.timeRaidStart, this.timeBossEnter);
                elapsedBossTime = calculateElapsedTime(System.currentTimeMillis(), this.timeBossEnter);
            }
            this.panelComponent.getChildren().add(LineComponent.builder().left("Preparation").right(elapsedPrepTime).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Boss Fight").right(elapsedBossTime).build());
            this.panelComponent.getChildren().add(LineComponent.builder().left("Total Time").right(elapsedTotalTime).build());
        }
        return this.panelComponent.render(graphics);
    }
}
