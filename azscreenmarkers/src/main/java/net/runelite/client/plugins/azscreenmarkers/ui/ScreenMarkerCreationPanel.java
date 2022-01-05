package net.runelite.client.plugins.azscreenmarkers.ui;

import net.runelite.client.plugins.azscreenmarkers.azScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenMarkerCreationPanel extends JPanel {
    private final JShadowedLabel instructionsLabel = new JShadowedLabel();

    private final JLabel confirmLabel = new JLabel();

    private boolean lockedConfirm = true;

    private static final ImageIcon CONFIRM_ICON = new ImageIcon(ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "confirm_icon.png"));

    private static final ImageIcon CANCEL_ICON = new ImageIcon(ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "cancel_icon.png"));

    private static final ImageIcon CONFIRM_HOVER_ICON;

    private static final ImageIcon CONFIRM_LOCKED_ICON;

    private static final ImageIcon CANCEL_HOVER_ICON;

    static {
        BufferedImage confirmIcon = ImageUtil.bufferedImageFromImage(CONFIRM_ICON.getImage());
        CONFIRM_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(confirmIcon, 0.54F));
        CONFIRM_LOCKED_ICON = new ImageIcon(ImageUtil.grayscaleImage(confirmIcon));
        CANCEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(ImageUtil.bufferedImageFromImage(CANCEL_ICON.getImage()), 0.6F));
    }

    ScreenMarkerCreationPanel(final azScreenMarkerPlugin plugin) {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setLayout(new BorderLayout());
        this.instructionsLabel.setFont(FontManager.getRunescapeSmallFont());
        this.instructionsLabel.setForeground(Color.WHITE);
        JPanel actionsContainer = new JPanel(new GridLayout(1, 2, 8, 0));
        actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.confirmLabel.setIcon(CONFIRM_LOCKED_ICON);
        this.confirmLabel.setToolTipText("Confirm and save");
        this.confirmLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (!ScreenMarkerCreationPanel.this.lockedConfirm)
                    plugin.finishCreation(false);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerCreationPanel.this.confirmLabel.setIcon(ScreenMarkerCreationPanel.this.lockedConfirm ? ScreenMarkerCreationPanel.CONFIRM_LOCKED_ICON : ScreenMarkerCreationPanel.CONFIRM_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerCreationPanel.this.confirmLabel.setIcon(ScreenMarkerCreationPanel.this.lockedConfirm ? ScreenMarkerCreationPanel.CONFIRM_LOCKED_ICON : ScreenMarkerCreationPanel.CONFIRM_ICON);
            }
        });
        final JLabel cancelLabel = new JLabel(CANCEL_ICON);
        cancelLabel.setToolTipText("Cancel");
        cancelLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                plugin.finishCreation(true);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                cancelLabel.setIcon(ScreenMarkerCreationPanel.CANCEL_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                cancelLabel.setIcon(ScreenMarkerCreationPanel.CANCEL_ICON);
            }
        });
        actionsContainer.add(this.confirmLabel);
        actionsContainer.add(cancelLabel);
        add((Component)this.instructionsLabel, "Center");
        add(actionsContainer, "East");
    }

    public void unlockConfirm() {
        this.confirmLabel.setIcon(CONFIRM_ICON);
        this.lockedConfirm = false;
        this.instructionsLabel.setText("Confirm or cancel to finish.");
    }

    public void lockConfirm() {
        this.confirmLabel.setIcon(CONFIRM_LOCKED_ICON);
        this.lockedConfirm = true;
        this.instructionsLabel.setText("Drag in-game to draw");
    }
}
