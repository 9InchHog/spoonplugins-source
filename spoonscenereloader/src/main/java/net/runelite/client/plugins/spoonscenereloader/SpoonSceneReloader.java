package net.runelite.client.plugins.spoonscenereloader;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

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
    private SpoonSceneReloaderConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    private SpoonSceneReloaderPanel panel;
    private NavigationButton navButton;
    private boolean buttonAttatched;

    private static final int RAIDS_LOBBY_REGION = 4919;

    @Provides
    SpoonSceneReloaderConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpoonSceneReloaderConfig.class);
    }

    boolean inRaid;

    protected void startUp() throws Exception {
        keyManager.registerKeyListener(this);
        CreatePanel();
    }

    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(this);
        inRaid = false;

        buttonAttatched = false;
        clientToolbar.removeNavigation(navButton);
    }

    public void CreatePanel() {
        buttonAttatched = false;
        clientToolbar.removeNavigation(navButton);

        panel = injector.getInstance(SpoonSceneReloaderPanel.class);
        panel.init();

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "instancereloadhelper.png");

        navButton = NavigationButton.builder()
                .tooltip("Scene Reloader")
                .icon(icon)
                .priority(config.panelPriority())
                .panel(panel)
                .build();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("scenereloader")) {
            if (event.getKey().equals("panelPriority")) {
                CreatePanel();
            }
        }
    }

    public void keyTyped(KeyEvent e) {}

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGIN_SCREEN)
        {
            SwingUtilities.invokeLater(() ->
            {
                clientToolbar.removeNavigation(navButton);
                buttonAttatched = false;
            });
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        boolean isInRaid = client.getVar(Varbits.IN_RAID) == 1;
        boolean inRaidLobby = (client.getLocalPlayer().getWorldLocation().getRegionID() == RAIDS_LOBBY_REGION);
        boolean inParty = client.getVar(VarPlayer.IN_RAID_PARTY) != -1;
        boolean raidsOnly = isInRaid | inRaidLobby | inParty;

        if (config.showPanel() != SpoonSceneReloaderConfig.panelMode.OFF) {
            if (raidsOnly != buttonAttatched && config.showPanel() == SpoonSceneReloaderConfig.panelMode.RAIDS) {
                SwingUtilities.invokeLater(() -> {
                    if (raidsOnly) {
                        clientToolbar.addNavigation(navButton);
                    } else {
                        clientToolbar.removeNavigation(navButton);
                    }
                });
                buttonAttatched = raidsOnly;
            } else if (config.showPanel() == SpoonSceneReloaderConfig.panelMode.LOGGED_IN) {
                if (!buttonAttatched) {
                    SwingUtilities.invokeLater(() -> clientToolbar.addNavigation(navButton));
                    buttonAttatched = true;
                }
            }
        } else if (buttonAttatched) {
            SwingUtilities.invokeLater(() -> clientToolbar.removeNavigation(navButton));
            buttonAttatched = false;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() && e.isShiftDown() && e.getKeyChar() == '\022') {
            reload();
        }
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
        clientThread.invoke(() -> {
            if (client.getGameState() == GameState.LOGGED_IN) {
                client.setGameState(GameState.CONNECTION_LOST);
            }
        });
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        inRaid = client.getVar(Varbits.IN_RAID) == 1;
    }
}
