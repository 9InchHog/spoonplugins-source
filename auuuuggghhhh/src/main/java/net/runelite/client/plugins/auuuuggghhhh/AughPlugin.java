package net.runelite.client.plugins.auuuuggghhhh;

import com.google.inject.Provides;
import java.io.BufferedInputStream;
import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "[S] AAAAUUUUUGGGHHHHH",
        description = "AAAAAUUUUUUUUUGGGGGGGGHHHHHHHHHHHH",
        tags = {"Turael, AUUUUUUUUUUUUUGHHHHHHHHH", "augh"},
        enabledByDefault = false
)
public class AughPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private AughConfig config;

    private String interactingName;

    private final boolean playClip = false;

    private Clip clip;

    FloatControl control;

    float volume = 25.0F;

    @Provides
    AughConfig provideConfig(ConfigManager configManager) {
        return (AughConfig)configManager.getConfig(AughConfig.class);
    }

    public void startUp() {
        reset();
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("augh.wav")));
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);
            control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (control != null) {
                volume = 20.0F * (float)Math.log10((config.volume() / 100.0F));
                control.setValue(volume);
            }
        } catch (Exception e) {
            e.printStackTrace();
            clip = null;
        }
    }

    public void shutDown() {
        reset();
    }

    private void reset() {
        interactingName = "";
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getLocalPlayer().getInteracting() != null)
            interactingName = client.getLocalPlayer().getInteracting().getName();
        Widget taskScreen = client.getWidget(231, 6);
        if (interactingName.equals("Steve") && taskScreen != null) {
            String taskText = taskScreen.getText();
            if ((taskText.contains("Your") || taskText.contains("You're")) && !taskText.toLowerCase().contains("tzhaar")) {
                String replacementText = "AAAAAAAAUUUUUUUUGGGGGHHHHHHHHHHHH";
                taskScreen.setText(replacementText + "!!!");
                if (clip != null) {
                    clip.setFramePosition(0);
                    clip.start();
                }
            }
        } else if (interactingName.equals("Turael") && taskScreen != null) {
            String taskText = taskScreen.getText();
            String replacementText = "";
            if ((taskText.contains("Your") || taskText.contains("You're")) && taskText.contains("monkeys") && config.monkeys().length() > 0) {
                replacementText = taskText.replace("monkeys", config.monkeys());
            } else if ((taskText.contains("Your") || taskText.contains("You're")) && taskText.contains("rats") && config.rats().length() > 0) {
                replacementText = taskText.replace("rats", config.rats());
            } else if ((taskText.contains("Your") || taskText.contains("You're")) && taskText.contains("dwarves") && config.dwarves().length() > 0) {
                replacementText = taskText.replace("dwarves", config.dwarves());
            } else if ((taskText.contains("Your") || taskText.contains("You're")) && taskText.contains("dogs") && config.dogs().length() > 0) {
                replacementText = taskText.replace("dogs", config.dogs());
            }
            if (!replacementText.equals(""))
                taskScreen.setText(replacementText);
        }
    }
}
