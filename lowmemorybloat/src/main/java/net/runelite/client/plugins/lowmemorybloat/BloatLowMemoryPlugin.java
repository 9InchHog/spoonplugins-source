/*
 * Copyright (c) 2021 Caps Lock13
 * Copyright (c) 2021 BikkusLite
 * Copyright (c) 2022 SpoonLite
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
package net.runelite.client.plugins.lowmemorybloat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.lowmemory.LowMemoryPlugin;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@Slf4j
@PluginDescriptor(
        name = "[S] Low Detail",
        description = "Turns off at bloat",
        tags = {"memory", "usage", "ground", "decorations", "bloat"},
        enabledByDefault = false
)
@PluginDependency(LowMemoryPlugin.class)
public class BloatLowMemoryPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private LowMemoryPlugin lowMemoryPlugin;

    private static final Integer BLOAT_REGION = 13125;
    private boolean enabled;

    @Override
    protected void startUp()
    {
        try {
            pluginManager.setPluginEnabled(lowMemoryPlugin, true);
            pluginManager.startPlugin(lowMemoryPlugin);
            enabled = pluginManager.isPluginEnabled(lowMemoryPlugin);
        } catch (PluginInstantiationException ex) {
            log.error("error starting plugin", ex);
        }
    }

    @Override
    protected void shutDown()
    {
        try {
            pluginManager.setPluginEnabled(lowMemoryPlugin, false);
            pluginManager.stopPlugin(lowMemoryPlugin);
            enabled = pluginManager.isPluginEnabled(lowMemoryPlugin);
        } catch (PluginInstantiationException ex) {
            log.error("error stopping plugin", ex);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (enabled && inRoomRegion(BLOAT_REGION)) {
            try {
                pluginManager.setPluginEnabled(lowMemoryPlugin, false);
                pluginManager.stopPlugin(lowMemoryPlugin);
                enabled = pluginManager.isPluginEnabled(lowMemoryPlugin);
            } catch (PluginInstantiationException ex) {
                log.error("error stopping plugin", ex);
            }
        } else if (!enabled && !inRoomRegion(BLOAT_REGION)) {
            try {
                pluginManager.setPluginEnabled(lowMemoryPlugin, true);
                pluginManager.startPlugin(lowMemoryPlugin);
                enabled = pluginManager.isPluginEnabled(lowMemoryPlugin);
            } catch (PluginInstantiationException ex) {
                log.error("error starting plugin", ex);
            }
        }
    }

    public boolean inRoomRegion(Integer roomRegionId)
    {
        return ArrayUtils.contains(client.getMapRegions(), roomRegionId);
    }
}
