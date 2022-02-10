package net.runelite.client.plugins.dxpdrops;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

@Extension
@PluginDescriptor(
        name = "[D] XP Drops",
        description = "De0's XP Drops",
        tags = {"de0", "xp", "drops"}
)
public class XpDropsPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private XpDropsOverlay overlay;

    @Inject
    private XpDropsConfig config;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    int[] varps;

    int[] curr_xp;

    int[] prev_xp;

    int[] fake_xp;

    boolean group;

    int speed;

    int size;

    Font font;

    int pos;

    Color color;

    @Provides
    XpDropsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(XpDropsConfig.class);
    }

    protected void startUp() throws Exception {
        varps = findServerVarpsField();
        if (varps == null) {
            varps = client.getVarps();
        }
        curr_xp = client.getSkillExperiences();
        prev_xp = new int[curr_xp.length];
        fake_xp = new int[26];
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        font = null;
    }

    @Subscribe
    public void onFakeXpDrop(FakeXpDrop e) {
        if (e.getXp() < 20000000)
            fake_xp[e.getSkill().ordinal()] = fake_xp[e.getSkill().ordinal()] + e.getXp();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.HOPPING ||
                e.getGameState() == GameState.LOGIN_SCREEN)
            Arrays.fill(prev_xp, 0);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired e) {
        if (e.getScriptId() == 1004) {
            Widget w = client.getWidget(122, 17);
            if (w != null)
                w.setHidden(true);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        if (!e.getGroup().equals("xpdrops"))
            return;
        if (e.getKey().equals("customFontName") ||
                e.getKey().equals("customFontSize"))
            load_font();
        if (e.getKey().equals("inGameSettings") ||
                e.getKey().equals("speed")) {
            clientThread.invoke(this::load_settings);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged e) {
        if (e.getIndex() == 1227)
            load_settings();
    }

    void load_settings() {
        int pos_setting = client.getVarbitValue(4692);
        int size_setting = client.getVarbitValue(4693);
        int color_setting = client.getVarbitValue(4695);
        int group_setting = client.getVarbitValue(4696);
        int speed_setting = client.getVarbitValue(4722);
        pos = pos_setting;
        size = size_setting;
        if(config.inGameSettings()) {
            speed = (speed_setting == 0) ? 3 : ((speed_setting == 1) ? 2 : 4);
        }else {
            speed = config.speed();
        }
        group = (group_setting != 0);
        color = new Color(client.getEnum(1169).getIntValue(color_setting));
        load_font();
    }

    void load_font() {
        if ("".equals(config.customFontName())) {
            if (size == 0) {
                font = FontManager.getRunescapeSmallFont();
            } else if (size == 1) {
                font = FontManager.getRunescapeFont();
            } else {
                font = FontManager.getRunescapeBoldFont();
            }
        } else {
            font = new Font(config.customFontName(), 0,
                    config.customFontSize());
        }
    }

    private int[] findServerVarpsField() {
        try {
            Field classes = ClassLoader.class.getDeclaredField("classes");
            classes.setAccessible(true);
            ClassLoader ccl = client.getClass().getClassLoader();
            for (Class<?> c : (Iterable<Class<?>>)classes.get(ccl)) {
                byte b;
                int i;
                Field[] arrayOfField;
                for (i = (arrayOfField = c.getDeclaredFields()).length, b = 0; b < i; ) {
                    Field f = arrayOfField[b];
                    if (f.getType() == int[].class && (
                            f.getModifiers() & 0x8) != 0) {
                        f.setAccessible(true);
                        int[] value = (int[])f.get(null);
                        if (value != null && value.length == 4000 &&
                                value != client.getVarps())
                            return value;
                    }
                    b++;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
