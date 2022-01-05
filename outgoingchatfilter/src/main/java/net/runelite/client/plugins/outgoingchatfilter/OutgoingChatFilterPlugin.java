package net.runelite.client.plugins.outgoingchatfilter;

import com.google.common.base.CharMatcher;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.vars.InputType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
@PluginDescriptor(
        name = "[b] Outgoing Chat filter",
        description = "Filters words for your message before it is send to the server",
        enabledByDefault = false
)
@Singleton
public class OutgoingChatFilterPlugin extends Plugin implements KeyListener {
  private static final Logger log = LoggerFactory.getLogger(OutgoingChatFilterPlugin.class);
  
  @Inject
  private Client client;
  
  @Inject
  private OutgoingChatFilterConfig config;
  
  @Inject
  private KeyManager keyManager;
  
  private final CharMatcher jagexPrintableCharMatcher = Text.JAGEX_PRINTABLE_CHAR_MATCHER;
  
  private static final ArrayList<Pattern> FILTERED_WORDS = new ArrayList<>();
  
  static final String CONFIG_GROUP = "OutgoingChatFilterConfig";
  
  @Provides
  OutgoingChatFilterConfig provideConfig(ConfigManager configManager) {
    return (OutgoingChatFilterConfig)configManager.getConfig(OutgoingChatFilterConfig.class);
  }
  
  protected void startUp() {
    this.keyManager.registerKeyListener(this);
    parseConfig(this.config.getWordsToFilter());
  }
  
  protected void shutDown() {
    this.keyManager.unregisterKeyListener(this);
  }
  
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 10)
      cleanupText(); 
  }
  
  @Subscribe
  public void onConfigChanged(ConfigChanged event) {
    if (event.getGroup().equals("OutgoingChatFilterConfig"))
      parseConfig(event.getNewValue()); 
  }
  
  private static void parseConfig(String text) {
    FILTERED_WORDS.clear();
    Text.fromCSV(text).forEach(word -> FILTERED_WORDS.add(Pattern.compile("(?<=^| )" + Pattern.quote(word.trim()) + "(?=$| )", 2)));
    FILTERED_WORDS.sort((m1, m2) -> Integer.compare(m2.pattern().length(), m1.pattern().length()));
  }
  
  private void cleanupText() {
    int inputType = this.client.getVar(VarClientInt.INPUT_TYPE);
    if (inputType == InputType.PRIVATE_MESSAGE.getType() || inputType == InputType.NONE.getType()) {
      VarClientStr var;
      if (inputType == InputType.PRIVATE_MESSAGE.getType()) {
        var = VarClientStr.INPUT_TEXT;
      } else {
        var = VarClientStr.CHATBOX_TYPED_TEXT;
      } 
      String text = this.client.getVar(var);
      if (text == null || "".equals(text))
        return; 
      String cleanedText = censorMessage(text);
      if (!cleanedText.equals(text))
        this.client.setVar(var, cleanedText); 
      log.debug("text:{}, censored:{}", text, cleanedText);
    } 
  }
  
  private String censorMessage(String message) {
    String strippedMessage = this.jagexPrintableCharMatcher.retainFrom(message).replace(' ', ' ');
    boolean filtered = false;
    for (Pattern pattern : FILTERED_WORDS) {
      Matcher m = pattern.matcher(strippedMessage);
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        m.appendReplacement(sb, StringUtils.repeat('*', m.group(0).length()));
        filtered = true;
      } 
      m.appendTail(sb);
      strippedMessage = sb.toString();
    } 
    return filtered ? strippedMessage : message;
  }
  
  public void keyReleased(KeyEvent e) {}
  
  public void keyTyped(KeyEvent e) {}
}
