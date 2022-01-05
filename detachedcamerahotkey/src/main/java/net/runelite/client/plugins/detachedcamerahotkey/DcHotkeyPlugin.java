package net.runelite.client.plugins.detachedcamerahotkey;

import net.runelite.client.plugins.PluginDescriptor;
import com.google.inject.Provides;
import java.util.function.Supplier;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "[S] Detached Camera Hotkey",
        description = "Hotkey to enable/disable the detached camera. Not made by me",
        tags = {"hotkey", "detached", "camera", "big mfkn tyler"},
        enabledByDefault = false
)
public class DcHotkeyPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private KeyManager keyManager;

    @Inject
    private DcHotkeyConfig config;

    @Provides
    DcHotkeyConfig provideConfig(ConfigManager cm) {
        return cm.getConfig(DcHotkeyConfig.class);
    }

    private boolean toggled = false;

    private final HotkeyListener masterSwitch = new HotkeyListener(() -> config.getDCHotkey()) {
        public void hotkeyPressed() {
            toggled = !toggled;
            client.setOculusOrbState(toggled ? 1 : 0);
            if (toggled)
                client.setOculusOrbNormalSpeed(config.getDCSpeed());
        }
    };

    protected void startUp() {
        keyManager.registerKeyListener(masterSwitch);
    }

    protected void shutDown() {
        keyManager.unregisterKeyListener(masterSwitch);
        toggled = false;
        client.setOculusOrbState(0);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("detachedcamerahotkey")) {
            if (e.getKey().equals("dcSpeed") && toggled) {
                client.setOculusOrbNormalSpeed(config.getDCSpeed());
            }
        }
    }
}
