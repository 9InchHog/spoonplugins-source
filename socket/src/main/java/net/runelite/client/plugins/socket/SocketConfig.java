package net.runelite.client.plugins.socket;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

import static net.runelite.client.plugins.socket.SocketPlugin.CONFIG_VERSION;

@ConfigGroup(CONFIG_VERSION)
public interface SocketConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "getHost",
            name = "Server Host Address",
            description = "The host address of the server to connect to."
    )
    default Server getServerAddress() {
        return Server.FOREIGNER;
    }

    @ConfigItem(
            position = 1,
            keyName = "customServerAddress",
            name = "Custom Host Address",
            description = "The host address of the server to connect to."
    )
    default String customServerAddress() {
        return "socket.kthisiscvpv.com";
    }

    @ConfigItem(
            position = 2,
            keyName = "getPort",
            name = "Server Port Number",
            description = "The port number of the server to connect to."
    )
    default int getServerPort() {
        return 26388;
    }

    @ConfigItem(
            position = 3,
            keyName = "getPassword",
            name = "Shared Password",
            description = "Used to encrypt and decrypt data sent to the server."
    )
    default String getPassword() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @ConfigItem(
            position = 99,
            keyName = "disableChatMessages",
            name = "Disable Chat Messages",
            description = "Disable chat messages."
    )
    default boolean disableChatMessages() {return false;}

    @ConfigItem(
            position = 100,
            keyName = "infobox",
            name = "Show Connection Infobox",
            description = "Displays an infobox to show if you are connected or disconnected."
    )
    default boolean infobox() {return false;}

    enum Server {
        FOREIGNER("American"),
        AUS("AUS"),
        CUSTOM("Custom");

        private final String name;
        public String toString() {
            return this.name;
        }
        public String getName() {
            return this.name;
        }
        Server(String name) {
            this.name = name;
        }
    }
}
