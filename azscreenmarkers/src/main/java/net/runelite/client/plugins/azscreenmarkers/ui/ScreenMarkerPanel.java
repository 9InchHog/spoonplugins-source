package net.runelite.client.plugins.azscreenmarkers.ui;

import net.runelite.client.plugins.azscreenmarkers.ScreenMarkerOverlay;
import net.runelite.client.plugins.azscreenmarkers.azScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class ScreenMarkerPanel extends JPanel {
    private static final int DEFAULT_FILL_OPACITY = 75;

    private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));

    private static final ImageIcon BORDER_COLOR_ICON;

    private static final ImageIcon BORDER_COLOR_HOVER_ICON;

    private static final ImageIcon NO_BORDER_COLOR_ICON;

    private static final ImageIcon NO_BORDER_COLOR_HOVER_ICON;

    private static final ImageIcon FILL_COLOR_ICON;

    private static final ImageIcon FILL_COLOR_HOVER_ICON;

    private static final ImageIcon NO_FILL_COLOR_ICON;

    private static final ImageIcon NO_FILL_COLOR_HOVER_ICON;

    private static final ImageIcon FULL_OPACITY_ICON;

    private static final ImageIcon FULL_OPACITY_HOVER_ICON;

    private static final ImageIcon NO_OPACITY_ICON;

    private static final ImageIcon NO_OPACITY_HOVER_ICON;

    private static final ImageIcon VISIBLE_ICON;

    private static final ImageIcon VISIBLE_HOVER_ICON;

    private static final ImageIcon INVISIBLE_ICON;

    private static final ImageIcon INVISIBLE_HOVER_ICON;

    private static final ImageIcon DELETE_ICON;

    private static final ImageIcon DELETE_HOVER_ICON;

    private final azScreenMarkerPlugin plugin;

    private final ScreenMarkerOverlay marker;

    private final JLabel borderColorIndicator = new JLabel();

    private final JLabel fillColorIndicator = new JLabel();

    private final JLabel opacityIndicator = new JLabel();

    private final JLabel visibilityLabel = new JLabel();

    private final JLabel deleteLabel = new JLabel();

    private final FlatTextField nameInput = new FlatTextField();

    private final JLabel save = new JLabel("Save");

    private final JLabel cancel = new JLabel("Cancel");

    private final JLabel rename = new JLabel("Rename");

    private final SpinnerModel spinnerModel = new SpinnerNumberModel(5, 0, 2147483647, 1);

    private final JSpinner thicknessSpinner = new JSpinner(this.spinnerModel);

    private boolean visible;

    static {
        BufferedImage borderImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "border_color_icon.png");
        BufferedImage borderImgHover = ImageUtil.luminanceOffset(borderImg, -150);
        BORDER_COLOR_ICON = new ImageIcon(borderImg);
        BORDER_COLOR_HOVER_ICON = new ImageIcon(borderImgHover);
        NO_BORDER_COLOR_ICON = new ImageIcon(borderImgHover);
        NO_BORDER_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(borderImgHover, -100));
        BufferedImage fillImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "fill_color_icon.png");
        BufferedImage fillImgHover = ImageUtil.luminanceOffset(fillImg, -150);
        FILL_COLOR_ICON = new ImageIcon(fillImg);
        FILL_COLOR_HOVER_ICON = new ImageIcon(fillImgHover);
        NO_FILL_COLOR_ICON = new ImageIcon(fillImgHover);
        NO_FILL_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(fillImgHover, -100));
        BufferedImage opacityImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "opacity_icon.png");
        BufferedImage opacityImgHover = ImageUtil.luminanceOffset(opacityImg, -150);
        FULL_OPACITY_ICON = new ImageIcon(opacityImg);
        FULL_OPACITY_HOVER_ICON = new ImageIcon(opacityImgHover);
        NO_OPACITY_ICON = new ImageIcon(opacityImgHover);
        NO_OPACITY_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(opacityImgHover, -100));
        BufferedImage visibleImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "visible_icon.png");
        VISIBLE_ICON = new ImageIcon(visibleImg);
        VISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(visibleImg, -100));
        BufferedImage invisibleImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "invisible_icon.png");
        INVISIBLE_ICON = new ImageIcon(invisibleImg);
        INVISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(invisibleImg, -100));
        BufferedImage deleteImg = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "delete_icon.png");
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));
    }

    ScreenMarkerPanel(final azScreenMarkerPlugin plugin, final ScreenMarkerOverlay marker) {
        this.plugin = plugin;
        this.marker = marker;
        this.visible = marker.getMarker().isVisible();
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel nameWrapper = new JPanel(new BorderLayout());
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(NAME_BOTTOM_BORDER);
        JPanel nameActions = new JPanel(new BorderLayout(3, 0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.save.setVisible(false);
        this.save.setFont(FontManager.getRunescapeSmallFont());
        this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
        this.save.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            }
        });
        this.cancel.setVisible(false);
        this.cancel.setFont(FontManager.getRunescapeSmallFont());
        this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
        this.cancel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            }
        });
        this.rename.setFont(FontManager.getRunescapeSmallFont());
        this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
        this.rename.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.nameInput.setEditable(true);
                ScreenMarkerPanel.this.updateNameActions(true);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            }
        });
        nameActions.add(this.save, "East");
        nameActions.add(this.cancel, "West");
        nameActions.add(this.rename, "Center");
        this.nameInput.setText(marker.getMarker().getName());
        this.nameInput.setBorder(null);
        this.nameInput.setEditable(false);
        this.nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.nameInput.setPreferredSize(new Dimension(0, 24));
        this.nameInput.getTextField().setForeground(Color.WHITE);
        this.nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        this.nameInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    ScreenMarkerPanel.this.save();
                } else if (e.getKeyCode() == 27) {
                    ScreenMarkerPanel.this.cancel();
                }
            }
        });
        this.nameInput.getTextField().addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.preview(true);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.preview(false);
            }
        });
        nameWrapper.add((Component)this.nameInput, "Center");
        nameWrapper.add(nameActions, "East");
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JPanel leftActions = new JPanel(new FlowLayout(0, 8, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.borderColorIndicator.setToolTipText("Edit border color");
        this.borderColorIndicator.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.openBorderColorPicker();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.borderColorIndicator.setIcon((marker.getMarker().getBorderThickness() == 0) ? ScreenMarkerPanel.NO_BORDER_COLOR_HOVER_ICON : ScreenMarkerPanel.BORDER_COLOR_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.borderColorIndicator.setIcon((marker.getMarker().getBorderThickness() == 0) ? ScreenMarkerPanel.NO_BORDER_COLOR_ICON : ScreenMarkerPanel.BORDER_COLOR_ICON);
            }
        });
        this.fillColorIndicator.setToolTipText("Edit fill color");
        this.fillColorIndicator.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.openFillColorPicker();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.fillColorIndicator.setIcon((marker.getMarker().getFill().getAlpha() == 0) ? ScreenMarkerPanel.NO_FILL_COLOR_HOVER_ICON : ScreenMarkerPanel.FILL_COLOR_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.fillColorIndicator.setIcon((marker.getMarker().getFill().getAlpha() == 0) ? ScreenMarkerPanel.NO_FILL_COLOR_ICON : ScreenMarkerPanel.FILL_COLOR_ICON);
            }
        });
        this.thicknessSpinner.setValue(Integer.valueOf(marker.getMarker().getBorderThickness()));
        this.thicknessSpinner.setPreferredSize(new Dimension(50, 20));
        this.thicknessSpinner.addChangeListener(ce -> updateThickness(true));
        this.opacityIndicator.setToolTipText("Toggle background transparency");
        this.opacityIndicator.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                Color fill = marker.getMarker().getFill();
                if (fill.getAlpha() == 0) {
                    marker.getMarker().setFill(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 75));
                } else {
                    marker.getMarker().setFill(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 0));
                }
                ScreenMarkerPanel.this.updateFill();
                plugin.updateConfig();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.opacityIndicator.setIcon((marker.getMarker().getFill().getAlpha() == 0) ? ScreenMarkerPanel.NO_OPACITY_HOVER_ICON : ScreenMarkerPanel.FULL_OPACITY_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.opacityIndicator.setIcon((marker.getMarker().getFill().getAlpha() == 0) ? ScreenMarkerPanel.NO_OPACITY_ICON : ScreenMarkerPanel.FULL_OPACITY_ICON);
            }
        });
        leftActions.add(this.borderColorIndicator);
        leftActions.add(this.fillColorIndicator);
        leftActions.add(this.opacityIndicator);
        leftActions.add(this.thicknessSpinner);
        JPanel rightActions = new JPanel(new FlowLayout(2, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.visibilityLabel.setToolTipText(this.visible ? "Hide screen marker" : "Show screen marker");
        this.visibilityLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.toggle(!ScreenMarkerPanel.this.visible);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.visibilityLabel.setIcon(ScreenMarkerPanel.this.visible ? ScreenMarkerPanel.VISIBLE_HOVER_ICON : ScreenMarkerPanel.INVISIBLE_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.updateVisibility();
            }
        });
        this.deleteLabel.setIcon(DELETE_ICON);
        this.deleteLabel.setToolTipText("Delete screen marker");
        this.deleteLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                int confirm = JOptionPane.showConfirmDialog(ScreenMarkerPanel.this, "Are you sure you want to permanently delete this screen marker?", "Warning", 2);
                if (confirm == 0)
                    plugin.deleteMarker(marker);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.deleteLabel.setIcon(ScreenMarkerPanel.DELETE_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPanel.this.deleteLabel.setIcon(ScreenMarkerPanel.DELETE_ICON);
            }
        });
        rightActions.add(this.visibilityLabel);
        rightActions.add(this.deleteLabel);
        bottomContainer.add(leftActions, "West");
        bottomContainer.add(rightActions, "East");
        add(nameWrapper, "North");
        add(bottomContainer, "Center");
        updateVisibility();
        updateFill();
        updateBorder();
        updateBorder();
    }

    private void preview(boolean on) {
        if (this.visible)
            return;
        this.marker.getMarker().setVisible(on);
    }

    private void toggle(boolean on) {
        this.visible = on;
        this.marker.getMarker().setVisible(this.visible);
        this.plugin.updateConfig();
        updateVisibility();
    }

    private void save() {
        this.marker.getMarker().setName(this.nameInput.getText());
        this.plugin.updateConfig();
        this.nameInput.setEditable(false);
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void cancel() {
        this.nameInput.setEditable(false);
        this.nameInput.setText(this.marker.getMarker().getName());
        updateNameActions(false);
        requestFocusInWindow();
    }

    private void updateNameActions(boolean saveAndCancel) {
        this.save.setVisible(saveAndCancel);
        this.cancel.setVisible(saveAndCancel);
        this.rename.setVisible(!saveAndCancel);
        if (saveAndCancel) {
            this.nameInput.getTextField().requestFocusInWindow();
            this.nameInput.getTextField().selectAll();
        }
    }

    private void updateThickness(boolean save) {
        this.marker.getMarker().setBorderThickness(((Integer)this.thicknessSpinner.getValue()).intValue());
        updateBorder();
        if (save)
            this.plugin.updateConfig();
    }

    private void updateVisibility() {
        this.visibilityLabel.setIcon(this.visible ? VISIBLE_ICON : INVISIBLE_ICON);
    }

    private void updateFill() {
        boolean isFullyTransparent = (this.marker.getMarker().getFill().getAlpha() == 0);
        if (isFullyTransparent) {
            this.fillColorIndicator.setBorder((Border)null);
        } else {
            Color color = this.marker.getMarker().getFill();
            Color fullColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
            this.fillColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, fullColor));
        }
        this.fillColorIndicator.setIcon(isFullyTransparent ? NO_FILL_COLOR_ICON : FILL_COLOR_ICON);
        this.opacityIndicator.setIcon(isFullyTransparent ? NO_OPACITY_ICON : FULL_OPACITY_ICON);
    }

    private void updateBorder() {
        if (this.marker.getMarker().getBorderThickness() == 0) {
            this.borderColorIndicator.setBorder((Border)null);
        } else {
            Color color = this.marker.getMarker().getColor();
            this.borderColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
        }
        this.borderColorIndicator.setIcon((this.marker.getMarker().getBorderThickness() == 0) ? NO_BORDER_COLOR_ICON : BORDER_COLOR_ICON);
    }

    private void openFillColorPicker() {
        RuneliteColorPicker colorPicker = this.plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this), this.marker
                        .getMarker().getFill(), this.marker
                        .getMarker().getName() + " Fill", false);
        colorPicker.setLocation(getLocationOnScreen());
        colorPicker.setOnColorChange(c -> {
            this.marker.getMarker().setFill(c);
            updateFill();
        });
        colorPicker.setOnClose(c -> this.plugin.updateConfig());
        colorPicker.setVisible(true);
    }

    private void openBorderColorPicker() {
        RuneliteColorPicker colorPicker = this.plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this), this.marker
                        .getMarker().getColor(), this.marker
                        .getMarker().getName() + " Border", false);
        colorPicker.setLocation(getLocationOnScreen());
        colorPicker.setOnColorChange(c -> {
            this.marker.getMarker().setColor(c);
            updateBorder();
        });
        colorPicker.setOnClose(c -> this.plugin.updateConfig());
        colorPicker.setVisible(true);
    }
}
