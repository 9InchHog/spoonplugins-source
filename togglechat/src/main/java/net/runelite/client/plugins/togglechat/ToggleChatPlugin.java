package net.runelite.client.plugins.togglechat;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.ClientTick;
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
@Slf4j
@PluginDescriptor(
        name = "[S] Toggle Chat",
        description = "Hotkey to toggle chat",
        tags = {"hotkey", "toggle", "chat"},
        enabledByDefault = false
)
public class ToggleChatPlugin extends Plugin implements KeyListener {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ToggleChatConfig config;

    @Provides
    ToggleChatConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ToggleChatConfig.class);
    }

    @Override
    protected void startUp() {
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown() {
        keyManager.unregisterKeyListener(this);
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        boolean hidden = client.getVarcIntValue(41) == 1337;
        if (hidden && config.removeBlueTabs()) {
            client.getVarcMap().put(44, 0);
            client.getVarcMap().put(45, 0);
            client.getVarcMap().put(46, 0);
            client.getVarcMap().put(438, 0);
            client.getVarcMap().put(47, 0);
            client.getVarcMap().put(48, 0);
        }
    }

    private void removeHotkey() throws InterruptedException {
        String typedText = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
        if(typedText.length() > 0) {
            String subTypedText = typedText.substring(0, typedText.length() - 1);
            String x = KeyEvent.getKeyText(config.hotKey().getKeyCode());
            char a = (char) KeyEvent.getExtendedKeyCodeForChar(typedText.substring(typedText.length() - 1).toCharArray()[0]);
            char b = (char) config.hotKey().getKeyCode();
            String y = typedText.substring(typedText.length() - 1);
            if (a == b) {
                client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, subTypedText);
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (config.hotKey().matches(e)) {
            clientThread.invokeLater(() -> {
                try {
                    removeHotkey();
                    client.runScript(175, 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}


