package net.runelite.client.plugins.coxoverloadtimers;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class OverloadTimer extends Timer {
    private final OverloadTimerConfig config;

    OverloadTimer(Plugin plugin, OverloadTimerConfig config, BufferedImage img, Duration totalDuration) {
        super(totalDuration.toMillis(), ChronoUnit.MILLIS, img, plugin);
        this.config = config;
        this.setPriority(InfoBoxPriority.MED);
    }

    public String getName() {
        return "CoX Overload";
    }

    public String getTooltip() {
        Duration delta = this.getTimeLeft();
        long seconds = delta.getSeconds();
        StringBuilder tooltip = (new StringBuilder()).append(ColorUtil.wrapWithColorTag("CoX Overload Timer", Color.CYAN)).append("</br>");
        tooltip.append("Time Left: ");
        String formattedTime = String.format("%d:%02d", seconds % 3600L / 60L, seconds % 60L);
        tooltip.append(ColorUtil.wrapWithColorTag(formattedTime, Color.YELLOW)).append("</br>");
        tooltip.append("Next Interval In: ");
        int mod = this.getModulo(seconds);
        tooltip.append(mod == 0 ? ColorUtil.wrapWithColorTag("Now", this.config.ovl15SecondModuloColor()) : ColorUtil.wrapWithColorTag(mod + "s", Color.PINK));
        return tooltip.toString();
    }

    public String getText() {
        long millis = this.getTimeLeft().toMillis();
        int seconds = (int)(millis / 1000L);
        switch(this.config.ovlUnitOfTime()) {
            case SECONDS:
                return Integer.toString(seconds);
            case GAME_TICKS:
                return Long.toString(millis / 600L);
            default:
                return String.format("%d:%02d", seconds % 3600 / 60, seconds % 60);
        }
    }

    public Color getTextColor() {
        long seconds = this.getTimeLeft().getSeconds();
        int mod = this.getModulo(seconds);
        if (this.config.ovl15SecondModulo() && mod == 0) {
            return this.config.ovl15SecondModuloColor();
        } else if (this.config.ovlPrewarn() && mod <= this.config.ovlPrewarnGap()) {
            return this.config.ovlPrewarnColor();
        } else {
            return (double)seconds < (double)this.getDuration().getSeconds() * 0.1D ? Color.RED.brighter() : Color.WHITE;
        }
    }

    private Duration getTimeLeft() {
        return Duration.between(Instant.now(), this.getEndTime());
    }

    private int getModulo(long seconds) {
        return Math.floorMod(seconds, 15);
    }
}
