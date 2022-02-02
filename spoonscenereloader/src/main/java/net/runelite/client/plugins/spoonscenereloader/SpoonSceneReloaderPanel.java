package net.runelite.client.plugins.spoonscenereloader;

import java.awt.BorderLayout;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

public class SpoonSceneReloaderPanel extends PluginPanel {
    private Client client;
    private ClientThread clientThread;
    private ChatMessageManager chatMessageManager;
    private SpoonSceneReloaderConfig config;

    @Inject
    SpoonSceneReloaderPanel(Client client, ClientThread clientThread, ChatMessageManager chatMessageManager, SpoonSceneReloaderConfig config)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.chatMessageManager = chatMessageManager;
        this.config = config;
    }

    void init()
    {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel reloadContainer = new JPanel();
        reloadContainer.setLayout(new BorderLayout());
        reloadContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JButton button = new JButton("Reload Scene");

        JPanel reloadFrame = new JPanel();
        button.addActionListener(e ->
        {
            clientThread.invoke(() ->
            {
                if (client.getGameState() == GameState.LOGGED_IN) {
                    boolean isInRaid = client.getVar(Varbits.IN_RAID) == 1;
                    if (config.raidsOnly()) {
                        if (isInRaid) {
                            client.setGameState(GameState.CONNECTION_LOST);
                        } else {
                            String chatMessage = new ChatMessageBuilder()
                                    .append(ChatColorType.HIGHLIGHT)
                                    .append("You aren't in a raid, you fucking moron...")
                                    .build();

                            chatMessageManager.queue(QueuedMessage.builder()
                                    .type(ChatMessageType.CONSOLE)
                                    .runeLiteFormattedMessage(chatMessage)
                                    .build());
                        }
                    } else {
                        client.setGameState(GameState.CONNECTION_LOST);
                    }
                }
            });
        });
        reloadFrame.add(button);
        reloadContainer.add(reloadFrame, BorderLayout.CENTER);

        JLabel reloadMessage = new JLabel("<html><center><h3>Scene Reloader</h3>Reloading the scene will cause your client to disconnect temporarily.<br>" +
                "You can also use the hotkey to reload the scene.</center></html>");
        add(reloadMessage, BorderLayout.PAGE_START);
        add(reloadContainer, BorderLayout.CENTER);
    }
}
