package net.runelite.client.plugins.spoonscenereloader;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Scene Reloader",
        description = "Reloads the scene with a hotkey, made by De0"
)
public class SpoonSceneReloader extends Plugin implements KeyListener {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ConfigManager cm;

    @Inject
    private SpoonSceneReloaderConfig config;

    @Provides
    SpoonSceneReloaderConfig getConfig(ConfigManager configManager) {
        return (SpoonSceneReloaderConfig)configManager.getConfig(SpoonSceneReloaderConfig.class);
    }

    boolean inRaid;

    protected void startUp() throws Exception {
        this.keyManager.registerKeyListener(this);
        this.cm.unsetConfiguration("raids", "dummy");
    }

    protected void shutDown() throws Exception {
        this.keyManager.unregisterKeyListener(this);
        inRaid = false;
    }

    public void keyTyped(KeyEvent e) {}

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {}

    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.isShiftDown() && e.getKeyChar() == '\022') {
            this.clientThread.invoke(new Runnable() {
                public void run() {
                    if (SpoonSceneReloader.this.client.getGameState() == GameState.LOGGED_IN)
                        SpoonSceneReloader.this.cm.setConfiguration("raids", "dummy", String.valueOf(SpoonSceneReloader.this.cm.getConfiguration("raids", "dummy")) + "0");
                }
            });
        } //else if (e.isControlDown() && e.getKeyChar() == '\022') {
        else if (config.hotkey().matches(e)) {
            if (config.raidsOnly() && inRaid) {
                reload();
            } else if (!config.raidsOnly()){
                reload();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void reload() {
        this.clientThread.invoke(new Runnable() {
            public void run() {
                if (SpoonSceneReloader.this.client.getGameState() == GameState.LOGGED_IN)
                    SpoonSceneReloader.this.client.setGameState(GameState.CONNECTION_LOST);
            }
        });
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        inRaid = this.client.getVar(Varbits.IN_RAID) == 1;
    }
}
