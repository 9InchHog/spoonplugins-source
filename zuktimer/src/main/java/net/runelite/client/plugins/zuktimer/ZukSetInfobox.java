package net.runelite.client.plugins.zuktimer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class ZukSetInfobox extends InfoBox
{
	private static final long SPAWN_DURATION = 210; // 3 minutes 30 seconds
	private static final long SPAWN_DURATION_INCREMENT = 105; // 1 minute 45 seconds
	private static final long SPAWN_DURATION_WARNING = 120; // 2 minutes before next respawn
	private static final long SPAWN_DURATION_DANGER = 30; // 30 seconds before next respawn

	private long timeRemaining;
	private long startTime;

	@Getter(AccessLevel.PACKAGE)
	private boolean running;

	ZukSetInfobox(final BufferedImage image, final ZukTimerPlugin plugin)
	{
		super(image, plugin);
		setPriority(InfoBoxPriority.HIGH);
		running = false;
		timeRemaining = SPAWN_DURATION;
	}

	void run()
	{
		startTime = Instant.now().getEpochSecond();
		running = true;
	}

	void reset()
	{
		running = false;
		timeRemaining = SPAWN_DURATION;
	}

	void pause()
	{
		if (!running)
		{
			return;
		}

		running = false;

		long timeElapsed = Instant.now().getEpochSecond() - startTime;

		timeRemaining = Math.max(0, timeRemaining - timeElapsed);

		timeRemaining += SPAWN_DURATION_INCREMENT;
	}

	@Override
	public String getText()
	{
		final long seconds = running
			? Math.max(0, timeRemaining - (Instant.now().getEpochSecond() - startTime))
			: timeRemaining;

		final long minutes = seconds % 3600 / 60;
		final long secs = seconds % 60;

		return String.format("%02d:%02d", minutes, secs);
	}

	@Override
	public Color getTextColor()
	{
		final long seconds = running
			? Math.max(0, timeRemaining - (Instant.now().getEpochSecond() - startTime))
			: timeRemaining;

		return seconds <= SPAWN_DURATION_DANGER ?
			Color.RED : seconds <= SPAWN_DURATION_WARNING ?
			Color.ORANGE : Color.GREEN;
	}

	@Override
	public boolean render()
	{
		return true;
	}

	@Override
	public boolean cull()
	{
		return false;
	}
}
