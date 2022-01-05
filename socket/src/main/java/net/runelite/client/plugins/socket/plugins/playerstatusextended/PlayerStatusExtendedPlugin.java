package net.runelite.client.plugins.socket.plugins.playerstatusextended;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.util.ColorUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Socket - Player Status Extended",
        description = "Socket extension for displaying player status to members in your party.",
        tags = {"socket"},
        enabledByDefault = false
)
@PluginDependency(SocketPlugin.class)
public class PlayerStatusExtendedPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private PlayerStatusExtendedConfig config;

    @Inject
    private EventBus eventBus;

    private ArrayList<String> exemptPlayer = new ArrayList<>();

    @Provides
    PlayerStatusExtendedConfig getConfig(ConfigManager configManager) {
        return (PlayerStatusExtendedConfig)configManager.getConfig(PlayerStatusExtendedConfig.class);
    }

    protected void startUp() throws Exception {
        exemptPlayer.clear();
        exemptPlayer = new ArrayList<>();
        if (config.ePlayers() != null && config.ePlayers().length() > 0) {
            String[] sp0 = config.ePlayers().split(",");
            for (String sp1 : sp0) {
                if (sp1 != null) {
                    String sp2 = sp1.trim();
                    if (sp2.length() != 0)
                        exemptPlayer.add(sp2.toLowerCase());
                }
            }
        }
    }

    protected void shutDown() throws Exception {
        exemptPlayer.clear();
        exemptPlayer = new ArrayList<>();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("playerstatusextendedconfig")) {
            if (event.getKey().equals("exemptPl")){
                exemptPlayer.clear();
                if (config.ePlayers() != null && config.ePlayers().length() > 0) {
                    String[] sp0 = config.ePlayers().split(",");
                    for (String sp1 : sp0) {
                        if (sp1 != null) {
                            String sp2 = sp1.trim();
                            if (sp2.length() != 0)
                                exemptPlayer.add(sp2.toLowerCase());
                            System.out.println("Config Changed: " + exemptPlayer);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (!payload.has("sLeech")) {
                return;
            }

            JSONArray data = payload.getJSONArray("sLeech");
            JSONObject jsonmsg = data.getJSONObject(0);
            String sender = jsonmsg.getString("sender");
            if (exemptPlayer.contains(sender.toLowerCase())) {
                return;
            }

            String mapRegion = jsonmsg.getString("mapregion");
            int[] mapRegions = regionsFromString(mapRegion);
            boolean inTob = inRegion(mapRegions, 12613, 13125, 13123, 12612, 12611, 13122);
            boolean inCox = (jsonmsg.getInt("raidbit") == 1);

            if (config.where() == PlayerStatusExtendedConfig.Where.TOB && !inTob) {
                return;
            }

            if (config.where() == PlayerStatusExtendedConfig.Where.COX && !inCox) {
                return;
            }

            if (config.where() == PlayerStatusExtendedConfig.Where.TOB_AND_COX && !inCox && !inTob) {
                return;
            }

            String msg = jsonmsg.getString("print");
            String finalS = ColorUtil.prependColorTag(msg, config.col());
            if (config.lvlOnly() && !finalS.contains("str")) {
                return;
            }

            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", finalS, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] regionsFromString(String s) {
        String s1 = s.substring(1).replaceAll("]", "");
        String[] s2 = s1.split(",");
        List<Integer> o = new ArrayList<>();
        for (String s3 : s2)
            o.add(Integer.valueOf(s3.trim()));
        return o.stream().mapToInt(i -> i).toArray();
    }

    private boolean inRegion(int[] realR, int... regions) {
        if (realR != null) {
            for (int i : realR) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        }
        return false;
    }
}
