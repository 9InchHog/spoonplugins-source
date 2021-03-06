/*
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.spoonslayer;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

class SpoonSlayerOverlay extends WidgetItemOverlay
{
    private final static Set<Integer> SLAYER_JEWELRY = ImmutableSet.of(
            ItemID.SLAYER_RING_1,
            ItemID.SLAYER_RING_2,
            ItemID.SLAYER_RING_3,
            ItemID.SLAYER_RING_4,
            ItemID.SLAYER_RING_5,
            ItemID.SLAYER_RING_6,
            ItemID.SLAYER_RING_7,
            ItemID.SLAYER_RING_8
    );

    private final static Set<Integer> ALL_SLAYER_ITEMS = ImmutableSet.of(
            ItemID.SLAYER_HELMET,
            ItemID.SLAYER_HELMET_I,
            ItemID.SLAYER_HELMET_I_25177,
            ItemID.BLACK_SLAYER_HELMET,
            ItemID.BLACK_SLAYER_HELMET_I,
            ItemID.BLACK_SLAYER_HELMET_I_25179,
            ItemID.GREEN_SLAYER_HELMET,
            ItemID.GREEN_SLAYER_HELMET_I,
            ItemID.GREEN_SLAYER_HELMET_I_25181,
            ItemID.PURPLE_SLAYER_HELMET,
            ItemID.PURPLE_SLAYER_HELMET_I,
            ItemID.PURPLE_SLAYER_HELMET_I_25185,
            ItemID.RED_SLAYER_HELMET,
            ItemID.RED_SLAYER_HELMET_I,
            ItemID.RED_SLAYER_HELMET_I_25183,
            ItemID.TURQUOISE_SLAYER_HELMET,
            ItemID.TURQUOISE_SLAYER_HELMET_I,
            ItemID.TURQUOISE_SLAYER_HELMET_I_25187,
            ItemID.TWISTED_SLAYER_HELMET,
            ItemID.TWISTED_SLAYER_HELMET_I,
            ItemID.TWISTED_SLAYER_HELMET_I_25191,
            ItemID.HYDRA_SLAYER_HELMET,
            ItemID.HYDRA_SLAYER_HELMET_I,
            ItemID.HYDRA_SLAYER_HELMET_I_25189,
            ItemID.TZTOK_SLAYER_HELMET,
            ItemID.TZTOK_SLAYER_HELMET_I,
            ItemID.TZTOK_SLAYER_HELMET_I_25902,
            ItemID.VAMPYRIC_SLAYER_HELMET,
            ItemID.VAMPYRIC_SLAYER_HELMET_I,
            ItemID.VAMPYRIC_SLAYER_HELMET_I_25908,
            ItemID.TZKAL_SLAYER_HELMET,
            ItemID.TZKAL_SLAYER_HELMET_I,
            ItemID.TZKAL_SLAYER_HELMET_I_25914,
            ItemID.SLAYER_RING_ETERNAL,
            ItemID.ENCHANTED_GEM,
            ItemID.ETERNAL_GEM,
            ItemID.SLAYER_RING_1,
            ItemID.SLAYER_RING_2,
            ItemID.SLAYER_RING_3,
            ItemID.SLAYER_RING_4,
            ItemID.SLAYER_RING_5,
            ItemID.SLAYER_RING_6,
            ItemID.SLAYER_RING_7,
            ItemID.SLAYER_RING_8
    );

    private final SpoonSlayerConfig config;
    private final SpoonSlayerPlugin plugin;

    @Inject
    private SpoonSlayerOverlay(SpoonSlayerPlugin plugin, SpoonSlayerConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
        showOnEquipment();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (!ALL_SLAYER_ITEMS.contains(itemId))
        {
            return;
        }

        if (!config.showItemOverlay())
        {
            return;
        }

        int amount = plugin.getAmount();
        if (amount <= 0)
        {
            return;
        }

        graphics.setFont(FontManager.getRunescapeSmallFont());

        final Rectangle bounds = widgetItem.getCanvasBounds();
        final TextComponent textComponent = new TextComponent();
        textComponent.setText(String.valueOf(amount));

        // Draw the counter in the bottom left for equipment, and top left for jewelry
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y - 1 + (SLAYER_JEWELRY.contains(itemId)
                ? bounds.height
                : graphics.getFontMetrics().getHeight())));
        textComponent.render(graphics);
    }
}
