package net.runelite.client.plugins.toggleoverlays;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayRenderer;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@Extension
@Slf4j
@PluginDescriptor(
        name = "[S] Toggle Overlays",
        description = "Hotkey toggle overlays",
        tags = {"hotkey", "toggle", "overlays", "hide"},
        enabledByDefault = false
)
public class ToggleOverlaysPlugin extends Plugin implements KeyListener
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;

    @Inject
    private OverlayRenderer overlayRenderer;

    @Inject
    private ToggleOverlaysConfig config;

    private boolean hidden;

    @Provides
    ToggleOverlaysConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ToggleOverlaysConfig.class);
    }

    @Override
    protected void startUp()
    {
        hidden = true;
        overlayRenderer.setShouldRender(true);
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown()
    {
        hidden = true;
        overlayRenderer.setShouldRender(true);
        keyManager.unregisterKeyListener(this);
    }

    private void toggleWidgets(boolean state)
    {
        overlayRenderer.setShouldRender(state);
    }

    private void removeHotkey() throws InterruptedException
    {
        String typedText = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
        if(typedText.length() > 0)
        {
            String subTypedText = typedText.substring(0, typedText.length() - 1);
            String x = KeyEvent.getKeyText(config.hotKey().getKeyCode());
            char a = (char) KeyEvent.getExtendedKeyCodeForChar(typedText.substring(typedText.length() - 1).toCharArray()[0]);
            char b = (char) config.hotKey().getKeyCode();
            String y = typedText.substring(typedText.length() - 1);
            if (a == b)
            {
                client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, subTypedText);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.hotKey().matches(e))
        {
            hidden = !hidden;
            toggleWidgets(hidden);
            clientThread.invokeLater(() ->
            {
                try
                {
                    removeHotkey();
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }

            });
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
}

