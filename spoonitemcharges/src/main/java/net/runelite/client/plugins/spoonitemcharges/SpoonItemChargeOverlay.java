package net.runelite.client.plugins.spoonitemcharges;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

class SpoonItemChargeOverlay extends WidgetItemOverlay {
	private final SpoonItemChargePlugin itemChargePlugin;

	private final SpoonItemChargeConfig config;

	@Inject
	SpoonItemChargeOverlay(SpoonItemChargePlugin itemChargePlugin, SpoonItemChargeConfig config) {
		this.itemChargePlugin = itemChargePlugin;
		this.config = config;
		showOnInventory();
		showOnEquipment();
	}

	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
		int charges;
		SpoonItemWithConfig itemWithConfig = SpoonItemWithConfig.findItem(itemId);
		if (itemWithConfig != null) {
			if (!itemWithConfig.getType().getEnabled().test(this.config))
				return;
			charges = this.itemChargePlugin.getItemCharges(itemWithConfig.getConfigKey());
		} else {
			SpoonItemWithCharge chargeItem = SpoonItemWithCharge.findItem(itemId);
			if (chargeItem == null)
				return;
			SpoonItemChargeType type = chargeItem.getType();
			if (!type.getEnabled().test(this.config))
				return;
			charges = chargeItem.getCharges();
		}
		if (config.hideDosePotionCharges() && charges >= config.potionCharges()) {
			SpoonItemWithCharge chargeItem = SpoonItemWithCharge.findItem(itemId);
			if (chargeItem != null) {
				SpoonItemChargeType type = chargeItem.getType();
				if (type == SpoonItemChargeType.POTION || type == SpoonItemChargeType.DIVINE_POTION
						|| type == SpoonItemChargeType.GUTHIX_REST || type == SpoonItemChargeType.COX_POTION) {
					return;
				}
			}
		}
		graphics.setFont(FontManager.getRunescapeSmallFont());
		Rectangle bounds = widgetItem.getCanvasBounds();
		TextComponent textComponent = new TextComponent();
		textComponent.setPosition(new Point(bounds.x - 1, bounds.y + 15));
		textComponent.setText((charges < 0) ? "?" : String.valueOf(charges));
		textComponent.setColor(this.itemChargePlugin.getColor(charges));
		textComponent.render(graphics);
	}
}
