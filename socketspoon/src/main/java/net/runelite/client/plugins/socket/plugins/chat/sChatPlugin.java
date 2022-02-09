package net.runelite.client.plugins.socket.plugins.chat;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONException;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.util.ColorUtil;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "Socket - Chat",
        description = "Chat over socket",
        tags = {"Socket", "chat"}
)
@PluginDependency(SocketPlugin.class)
public class sChatPlugin extends Plugin implements KeyListener {
    private static final Logger log = LoggerFactory.getLogger(sChatPlugin.class);

    @Inject
    Client client;

    @Inject
    private KeyManager keyManager;

    @Inject
    sChatConfig config;

    @Inject
    private ClientThread clientThread;

    private boolean tradeActive = false;

    private boolean typing = false;

    private boolean lastTypingState = false;

    @Inject
    private EventBus eventBus;

    @Inject
    private ChatMessageManager chatMessageManager;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");

    SimpleDateFormat formatterr = new SimpleDateFormat("HH:mm");

    SimpleDateFormat formatterrr = new SimpleDateFormat("MM/dd");

    private HashMap<String, SentSocketMessage> sentMessages = new HashMap<>();

    @Getter
    @Setter
    private long time;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private boolean setOverhead;

    @Provides
    sChatConfig getConfig(ConfigManager configManager) {
        return (sChatConfig) configManager.getConfig(sChatConfig.class);
    }

    protected void startUp() throws Exception {
        keyManager.registerKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

    @Subscribe
    public void onVarClientStrChanged(VarClientStrChanged event) throws InterruptedException {
        removeHotkey();
    }

    private void removeHotkey() throws InterruptedException {
        String typedText = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
        if (typedText.length() > 0) {
            String subTypedText = typedText.substring(0, typedText.length() - 1);
            String x = KeyEvent.getKeyText(config.hotkey().getKeyCode());
            char a = (char) KeyEvent.getExtendedKeyCodeForChar(typedText.substring(typedText.length() - 1).toCharArray()[0]);
            char b = (char) config.hotkey().getKeyCode();
            typedText.substring(typedText.length() - 1);
            if (a == b)
                client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, subTypedText);
        }
    }

    @Subscribe
    private void onBeforeRender(BeforeRender event) {
        Widget chatbox = client.getWidget(WidgetInfo.CHATBOX_INPUT);
        if (chatbox != null && !chatbox.isHidden()) {
            if (!tradeActive && client.getVarcIntValue(41) == 6) {
                lastTypingState = typing;
                typing = true;
                tradeActive = true;
            } else if (tradeActive && client.getVarcIntValue(41) != 6) {
                typing = lastTypingState;
                tradeActive = false;
            }
            if (typing) {
                if (!chatbox.getText().startsWith("[SOCKET CHAT] ")) {
                    chatbox.setText("[SOCKET CHAT] " + chatbox.getText());
                }
            } else if (chatbox.getText().startsWith("[SOCKET CHAT] ")) {
                chatbox.setText(chatbox.getText().substring(13));
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (config.hotkey().matches(e)) {
            typing = !typing;
            clientThread.invokeLater(() -> {
                try {
                    removeHotkey();
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            });
        }
        if (e.getKeyCode() == 10) {
            String typedText = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
            if (typing) {
                if (typedText.startsWith("/")) {
                    if (!config.overrideSlash()) {
                        sendMessage(typedText);
                        client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, "");
                    }
                } else {
                    sendMessage(typedText);
                    client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, "");
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() == GameState.LOGGED_IN) {
            checkOverhead(true);
        }
    }

    private void sendMessage(String msg) {
        if (!msg.equals("")) {
            JSONArray data = new JSONArray();
            JSONObject jsonmsg = new JSONObject();
            jsonmsg.put("msg", " " + msg);
            String sender = (config.getNameFake().length() >= 1) ? config.getNameFake() : client.getLocalPlayer().getName();
            if (config.getIcon() != 0) {
                jsonmsg.put("sender", "<img=" + config.getIcon() + ">" + sender);
            } else {
                jsonmsg.put("sender", sender);
            }
            jsonmsg.put("senderreal", client.getLocalPlayer().getName());
            data.put(jsonmsg);
            JSONObject send = new JSONObject();
            send.put("sChat", data);
            eventBus.post(new SocketBroadcastPacket(send));

            if (config.singleText()) {
                typing = false;
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            String senderReal;
            ChatMessageType cmt;
            JSONObject payload = event.getPayload();

            if (!payload.has("sChat"))
                return;
            Date date = new Date();
            JSONArray data = payload.getJSONArray("sChat");
            JSONObject jsonmsg = data.getJSONObject(0);
            String sender = jsonmsg.getString("sender");
            String msg = jsonmsg.getString("msg");
            try {
                senderReal = jsonmsg.getString("senderreal");
            } catch (JSONException e) {
                senderReal = null;
            }

            if (config.overrideTradeButton()) {
                cmt = ChatMessageType.TRADE;
            } else {
                cmt = ChatMessageType.GAMEMESSAGE;
            }

            String dateTime = "";
            if (config.getDateStamp()) {
                if (config.getFreedomUnits()) {
                    dateTime = dateTime + formatterrr.format(date);
                } else {
                    dateTime = dateTime + formatter.format(date);
                }
            }

            if (config.getTimeStamp()) {
                if (!dateTime.equals("")) {
                    dateTime = dateTime + " | " + formatterr.format(date);
                } else {
                    dateTime = dateTime + formatterr.format(date);
                }
            }

            String dateTimeString = "[" + dateTime + "] ";
            String customMsg = "";
            if (!config.showSomeStupidShit().equals("")) {
                customMsg = "[" + config.showSomeStupidShit() + "] ";
            }

            if (!dateTime.equals("")) {
                client.addChatMessage(cmt, "", ColorUtil.prependColorTag(dateTimeString, config.getDateTimeColor()) + customMsg
                        + ColorUtil.prependColorTag(sender, config.getNameColor()) + ":" + ColorUtil.prependColorTag(msg, config.messageColor()), null, false);
            } else {
                client.addChatMessage(cmt, "", customMsg + ColorUtil.prependColorTag(sender, config.getNameColor()) + ":"
                        + ColorUtil.prependColorTag(msg, config.messageColor()), null, false);
            }

            sentMessages.put(senderReal, new SentSocketMessage(client.getTickCount(), msg, false));
            checkOverhead(false);

        } catch (Exception var8) {
            var8.printStackTrace();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    private void checkOverhead(boolean tickCheck) {
        for (Player p : client.getPlayers()) {
            String nameToCheck = p.getName();
            SentSocketMessage message = sentMessages.get(nameToCheck);
            if (message != null) {
                if (client.getTickCount() > message.getTime() + 5L || !config.overheadText()) {
                    if (p.getOverheadText() != null && p.getOverheadText().equals(message.getText())) {
                        p.setOverheadText(null);
                        sentMessages.remove(p.getName());
                    }
                    continue;
                }
                if (!message.isSetOverhead() && !tickCheck) {
                    if (config.hideLocalPlayerOverhead() && p == client.getLocalPlayer()) {
                        continue;
                    }
                    if (config.overheadText()) {
                        p.setOverheadText(message.getText());
                        message.setSetOverhead(true);
                    }
                }
            }
        }
    }

    private static class SentSocketMessage {
        @Getter
        @Setter
        private long time;

        @Getter
        @Setter
        private String text;

        @Getter
        @Setter
        private boolean setOverhead;

        public SentSocketMessage(long time, String text, boolean setOverhead) {
            this.time = time;
            this.text = text;
            this.setOverhead = setOverhead;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof SentSocketMessage))
                return false;
            SentSocketMessage other = (SentSocketMessage)o;
            if (!other.canEqual(this))
                return false;
            if (getTime() != other.getTime())
                return false;
            if (isSetOverhead() != other.isSetOverhead())
                return false;
            Object this$text = getText(), other$text = other.getText();
            return Objects.equals(this$text, other$text);
        }

        protected boolean canEqual(Object other) {
            return other instanceof SentSocketMessage;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            long $time = getTime();
            result = result * 59 + (int)($time >>> 32L ^ $time);
            result = result * 59 + (isSetOverhead() ? 79 : 97);
            Object $text = getText();
            return result * 59 + (($text == null) ? 43 : $text.hashCode());
        }

        public String toString() {
            return "sChatPlugin.SentSocketMessage(time=" + getTime() + ", text=" + getText() + ", setOverhead=" + isSetOverhead() + ")";
        }
    }
}
