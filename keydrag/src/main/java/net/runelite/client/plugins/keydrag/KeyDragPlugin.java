package net.runelite.client.plugins.keydrag;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
public class KeyDragPlugin extends Plugin implements KeyListener {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Client client;

    @Inject
    private KeyDragConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private KeyDragOverlay overlay;

    private static final int DEFAULT_DELAY = 5;
    public boolean toggleDrag = true;
    private boolean ctrlHeld;

    @Provides
    KeyDragConfig getConfig(ConfigManager configManager) {
        return (KeyDragConfig) configManager.getConfig(KeyDragConfig.class);
    }

    protected void startUp() throws Exception {
        toggleDrag = true;
        setDragDelay();
        keyManager.registerKeyListener(this);
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        toggleDrag = false;
        resetDragDelay();
        keyManager.unregisterKeyListener(this);
        overlayManager.remove(overlay);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL && config.disableOnCtrl()) {
            resetDragDelay();
            toggleDrag = false;
        }
        ctrlHeld = true;
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

    private void setBankDragDelay(int delay) {
        Widget bankItemContainer = this.client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        Widget bankInventoryItemsContainer = this.client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER);
        Widget bankDepositContainer = this.client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER);
        Widget coxPrivateChest = this.client.getWidget(17760262);

        if (bankItemContainer != null) {
            Widget[] items = bankItemContainer.getDynamicChildren();
            for (Widget item : items)
                item.setDragDeadTime(delay);
        }
        if (bankInventoryItemsContainer != null) {
            Widget[] items = bankInventoryItemsContainer.getDynamicChildren();
            for (Widget item : items)
                item.setDragDeadTime(delay);
        }
        if (bankDepositContainer != null) {
            Widget[] items = bankDepositContainer.getDynamicChildren();
            for (Widget item : items)
                item.setDragDeadTime(delay);
        }
        if (coxPrivateChest != null) {
            Widget[] items = coxPrivateChest.getDynamicChildren();
            for (Widget item : items){
                if (item.getDragDeadTime() != delay) {
                    item.setDragDeadTime(delay);
                }
            }
        }
    }

    private void setDragDelay() {
        client.setInventoryDragDelay(config.dragDelay());
        setBankDragDelay(config.dragDelay());
    }

    private void resetDragDelay() {
        client.setInventoryDragDelay(DEFAULT_DELAY);
        setBankDragDelay(DEFAULT_DELAY);
    }

    @Subscribe
    private void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == WidgetInfo.BANK_ITEM_CONTAINER.getGroupId() || event.getGroupId() == WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getGroupId()
                || event.getGroupId() == WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER.getGroupId() || event.getGroupId() == 271) {
            if (toggleDrag) {
                setBankDragDelay(config.dragDelay());
            } else {
                setBankDragDelay(DEFAULT_DELAY);
            }
        }
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event){
        if (!event.isFocused()){
            if (ctrlHeld){
                setDragDelay();
                ctrlHeld = false;
                toggleDrag = true;
            }
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event) {
        if (this.client.getVar(Varbits.IN_RAID) == 1) {
            if (event.getContainerId() == 583) {
                if (toggleDrag) {
                    setBankDragDelay(this.config.dragDelay());
                } else {
                    setBankDragDelay(DEFAULT_DELAY);
                }
            }
        }
    }
}
