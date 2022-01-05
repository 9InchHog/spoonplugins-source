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

package net.runelite.client.plugins.mouseovertext;

import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.*;
import net.runelite.client.plugins.keyremapping.KeyRemappingPlugin;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;

@Extension
@PluginDescriptor(
        name = "[S] MouseOverText",
        tags = {"mouse", "over", "text", "command"},
        enabledByDefault = false
)
@PluginDependency(KeyRemappingPlugin.class)
public class MouseOverTextPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private KeyRemappingPlugin keyRemappingPlugin;
    private static final String COMMAND = "::mouseovertext";
    private boolean loggedIn = false;
    private boolean commandExecuted = false;

    public MouseOverTextPlugin() {
    }

    protected void startUp() {
    }

    protected void shutDown() {
        this.reset();
    }

    private void reset() {
        this.loggedIn = false;
        this.commandExecuted = false;
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        switch(e.getGameState()) {
            case LOGGED_IN:
                this.loggedIn = true;
                break;
            case HOPPING:
            case LOGIN_SCREEN:
                this.reset();
        }

    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (this.client.getLocalPlayer() != null) {
            if (this.loggedIn && !this.commandExecuted) {
                this.clientThread.invokeLater(() -> {
                    this.client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, "::mouseovertext");
                });
                if (this.client.getVar(VarClientStr.CHATBOX_TYPED_TEXT).contains("::mouseovertext")) {
                    if (pluginManager.isPluginEnabled(keyRemappingPlugin))
                    {
                        try {
                            pluginManager.setPluginEnabled(keyRemappingPlugin, false);
                            pluginManager.stopPlugin(keyRemappingPlugin);
                        } catch (PluginInstantiationException ex) {
                            //log.error("error stopping plugin", ex);
                        }

                        Executors.newSingleThreadExecutor().submit(this::pressEnter);

                        try {
                            pluginManager.setPluginEnabled(keyRemappingPlugin, true);
                            pluginManager.startPlugin(keyRemappingPlugin);
                        } catch (PluginInstantiationException ex) {
                            //log.error("error starting plugin", ex);
                        }
                    }
                    else
                    {
                        Executors.newSingleThreadExecutor().submit(this::pressEnter);
                    }
                }
            }

        }
    }

    private void pressEnter() {
        KeyEvent keyPress = new KeyEvent(this.client.getCanvas(), 401, System.currentTimeMillis(), 0, 10);
        this.client.getCanvas().dispatchEvent(keyPress);
        KeyEvent keyRelease = new KeyEvent(this.client.getCanvas(), 402, System.currentTimeMillis(), 0, 10);
        this.client.getCanvas().dispatchEvent(keyRelease);
        KeyEvent keyTyped = new KeyEvent(this.client.getCanvas(), 400, System.currentTimeMillis(), 0, 10);
        this.client.getCanvas().dispatchEvent(keyTyped);
    }

    @Subscribe
    private void onCommandExecuted(CommandExecuted e) {
        if (e.getCommand().equalsIgnoreCase("mouseovertext")) {
            this.commandExecuted = true;
        }

    }
}
