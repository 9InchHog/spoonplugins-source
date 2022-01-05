package net.runelite.client.plugins.socket;

/**
 * Enum message types for broadcasting a message to the user's chatbox.
 * SocketLog identifies a color prefix to use in each scenario.
 */
public enum SocketLog {

    INFO("<col=008000>"), ERROR("<col=b4281e>");

    private String prefix;

    SocketLog(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Retrieves the hex tag to use for the specific message type.
     *
     * @return String hex tag.
     */
    public String getPrefix() {
        return this.prefix;
    }
}