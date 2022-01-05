package net.runelite.client.plugins.vmswimshamer;

import com.google.inject.Provides;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.util.ImageCapture;
import net.runelite.client.util.ImageUploadStyle;
import org.pf4j.Extension;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Swim Shamer",
        description = "Uploads a screenshot to discord whenever someone swims or dies in Volcanic Mine",
        tags = {"death", "swim", "fish", "shame", "vm", "volcanic", "discord", "discord", "webhook"},
        loadWhenOutdated = true,
        enabledByDefault = false
)
public class SwimShamerPlugin extends Plugin{

    @Inject
    private Client client;

    @Inject
    private ImageCapture imageCapture;

    @Inject
    private DrawManager drawManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private SwimShamerConfig config;

    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;

    @Provides
    SwimShamerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SwimShamerConfig.class);
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath)
    {
        Actor actor = actorDeath.getActor();
        if (actor instanceof Player)
        {
            Player player = (Player) actor;
            if (player != client.getLocalPlayer() && isInVM())
            {
                takeScreenshot("Death of " + player.getName(), "Wall of Shame");
            }
            else {
                System.out.println("[DEBUG] Not in Vm sorry.");
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (actor instanceof Player) {
            Player player = (Player) actor;
            if (player != client.getLocalPlayer() && isInVM()) {
                if (event.getActor().getAnimation() == 1950) {
                    takeScreenshot("Swim " + event.getActor().getName(), "Wall of Shame");
                }
            }
        }
    }

    public boolean isInVM()
    {
        if(this.client.getLocalPlayer() != null) {
            return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                    WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
        }else {
            return false;
        }
    }

    private void takeScreenshot(String fileName, String subDir)
    {
        Consumer<Image> imageCallback = (img) ->
        {
            executor.submit(() -> {
                try {
                    takeScreenshot(fileName, subDir, img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        };
        drawManager.requestNextFrameListener(imageCallback);
    }

    private void takeScreenshot(String fileName, String subDir, Image image) throws IOException
    {
        BufferedImage screenshot = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = screenshot.getGraphics();
        int gameOffsetX = 0;
        int gameOffsetY = 0;
        graphics.drawImage(image, gameOffsetX, gameOffsetY, null);
        imageCapture.takeScreenshot(screenshot, fileName, subDir, false, ImageUploadStyle.NEITHER);
        ByteArrayOutputStream screenshotOutput = new ByteArrayOutputStream();
        ImageIO.write(screenshot, "png", screenshotOutput);

        if (config.webhookEnabled() && !config.webhookLink().equals(""))
        {
            DiscordWebhook FileSender = new DiscordWebhook();
            FileSender.SendWebhook(screenshotOutput, fileName, config.webhookLink());
        }
    }
}
