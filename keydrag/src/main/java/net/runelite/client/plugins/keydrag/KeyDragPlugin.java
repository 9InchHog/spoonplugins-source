package net.runelite.client.plugins.keydrag;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.Varbits;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Anti Drag",
        description = "Prevent dragging an item for a specified delay",
        tags = {"antidrag", "delay", "inventory", "items", "hotkey", "keybind", "spoon"},
        conflicts = "Anti Drag"
)
@Slf4j
public class KeyDragPlugin extends Plugin implements KeyListener {
    static final String CONFIG_GROUP = "keydrag";

    private static final int DEFAULT_DELAY = 5;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyDragConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private KeyDragOverlay overlay;

    public boolean toggleDrag = true;
    private boolean ctrlHeld;

    @Provides
    KeyDragConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(KeyDragConfig.class);
    }

    protected void startUp() throws Exception {
        toggleDrag = true;
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            clientThread.invokeLater(this::setDragDelay);
        }
        keyManager.registerKeyListener(this);
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        toggleDrag = false;
        clientThread.invoke(this::resetDragDelay);
        keyManager.unregisterKeyListener(this);
        overlayManager.remove(overlay);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            ctrlHeld = true;
            if (config.disableOnCtrl()) {
                resetDragDelay();
                toggleDrag = false;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            if (config.disableOnCtrl()){
                toggleDrag = true;
                setDragDelay();
            }
            ctrlHeld = false;
        } else if (config.hotkey().matches(e)) {
            if (toggleDrag) {
                toggleDrag = false;
                resetDragDelay();
            } else {
                toggleDrag = true;
                setDragDelay();
            }
        }
    }

    private boolean isOverriding()
    {
        return toggleDrag && !ctrlHeld;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals(CONFIG_GROUP))
        {
            if (event.getKey().equals("disableOnCtrl"))
            {
                if (!config.disableOnCtrl() && ctrlHeld) {
                    clientThread.invoke(this::setDragDelay);
                    ctrlHeld = false;
                }
            }
            else if (event.getKey().equals("dragDelay") && toggleDrag) {
                clientThread.invoke(this::setDragDelay);
            }
        }
    }

    @Subscribe
    public void onFocusChanged(FocusChanged focusChanged)
    {
        if (!focusChanged.isFocused() && ctrlHeld)
        {
            toggleDrag = true;
            ctrlHeld = false;
            clientThread.invoke(this::setDragDelay);
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        if (!isOverriding())
        {
            return;
        }

        if (widgetLoaded.getGroupId() == WidgetID.BANK_GROUP_ID ||
                widgetLoaded.getGroupId() == WidgetID.BANK_INVENTORY_GROUP_ID ||
                widgetLoaded.getGroupId() == WidgetID.DEPOSIT_BOX_GROUP_ID)
        {
            setBankDragDelay(config.dragDelay());
        }
        else if (widgetLoaded.getGroupId() == WidgetID.INVENTORY_GROUP_ID)
        {
            setInvDragDelay(config.dragDelay());
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
        if (this.client.getVarbitValue(Varbits.IN_RAID) == 1) {
            if (event.getContainerId() == 583) {
                if (toggleDrag) {
                    setBankDragDelay(config.dragDelay());
                } else {
                    setBankDragDelay(DEFAULT_DELAY);
                }
            }
        }
    }

    @Subscribe
    private void onScriptPostFired(ScriptPostFired ev)
    {
        if (ev.getScriptId() == ScriptID.INVENTORY_DRAWITEM)
        {
            Widget inv = client.getWidget(WidgetInfo.INVENTORY);
            final int delay = config.dragDelay();
            boolean overriding = isOverriding();
            for (Widget child : inv.getDynamicChildren())
            {
                // disable [clientscript,inventory_antidrag_update] listener
                child.setOnMouseRepeatListener((Object[]) null);
                if (overriding)
                {
                    child.setDragDeadTime(delay);
                }
            }
        }
    }

    private static void applyDragDelay(Widget widget, int delay)
    {
        if (widget != null)
        {
            for (Widget item : widget.getDynamicChildren())
            {
                item.setDragDeadTime(delay);
            }
        }
    }

    private void setBankDragDelay(int delay) {
        final Widget bankItemContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        final Widget bankInventoryItemsContainer = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
        final Widget bankDepositContainer = client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER);
        final Widget coxPrivateChest = client.getWidget(17760262);

        applyDragDelay(bankItemContainer, delay);
        applyDragDelay(bankInventoryItemsContainer, delay);
        applyDragDelay(bankDepositContainer, delay);
        applyDragDelay(coxPrivateChest, delay);
    }

    private void setInvDragDelay(int delay)
    {
        final Widget inventory = client.getWidget(WidgetInfo.INVENTORY);
        applyDragDelay(inventory, delay);
    }

    private void setDragDelay()
    {
        final int delay = config.dragDelay();
        log.debug("Set delay to {}", delay);
        client.setInventoryDragDelay(delay);
        setInvDragDelay(delay);
        setBankDragDelay(delay);
    }

    private void resetDragDelay()
    {
        log.debug("Reset delay to {}", DEFAULT_DELAY);
        client.setInventoryDragDelay(DEFAULT_DELAY);
        setInvDragDelay(DEFAULT_DELAY);
        setBankDragDelay(DEFAULT_DELAY);
    }
}
