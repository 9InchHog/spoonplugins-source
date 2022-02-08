/*
 * Copyright (c) 2021 BikkusLite
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.killswitch;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.OPRSExternalPluginManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

@Extension
@Slf4j
@PluginDescriptor(
        name = "[S] Killswitch",
        description = "Toggles your enabled external plugins",
        tags = {"hotkey", "toggle", "external", "plugins", "killswitch"},
        enabledByDefault = false
)
public class KillswitchPlugin extends Plugin implements KeyListener
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private OPRSExternalPluginManager oprsExternalPluginManager;

    @Inject
    private KillswitchConfig config;

    private boolean hidden;

    ArrayList<Plugin> enabledPlugins = new ArrayList<>();

    @Provides
    KillswitchConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(KillswitchConfig.class);
    }

    @Override
    protected void startUp()
    {
        keyManager.registerKeyListener(this);
        Map<String, Map<String, String>> pluginsInfoMap = oprsExternalPluginManager.getPluginsInfoMap();

        for (Plugin plugins : pluginManager.getPlugins())
        {
            if (!pluginsInfoMap.containsKey(plugins.getClass().getSimpleName()))
            {
                continue;
            }
            if (plugins.getName().contains("Killswitch"))
            {
                continue;
            }
            if (pluginManager.isPluginEnabled(plugins))
            {
                enabledPlugins.add(plugins);
            }
        }
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(this);
        toggleWidgets(false);
        hidden = false;
        enabledPlugins.clear();
    }

    private void toggleWidgets(boolean state)
    {
        for (Plugin plugins : enabledPlugins)
        {
            if (state)
            {
                if (pluginManager.isPluginEnabled(plugins))
                {
                    try
                    {
                        pluginManager.setPluginEnabled(plugins, false);
                        pluginManager.stopPlugin(plugins);
                    }
                    catch (PluginInstantiationException ex)
                    {
                        log.error("error stopping plugin", ex);
                    }
                }
            }
            else
            {
                if (!pluginManager.isPluginEnabled(plugins))
                {
                    try
                    {
                        pluginManager.setPluginEnabled(plugins, true);
                        pluginManager.startPlugin(plugins);
                    }
                    catch (PluginInstantiationException ex)
                    {
                        log.error("error starting plugin", ex);
                    }
                }
            }
        }

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
