package net.runelite.client.plugins.azscreenmarkers.ui;

import net.runelite.client.plugins.azscreenmarkers.ScreenMarkerOverlay;
import net.runelite.client.plugins.azscreenmarkers.azScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenMarkerPluginPanel extends PluginPanel {
    private static final ImageIcon ADD_ICON;

    private static final ImageIcon ADD_HOVER_ICON;

    private static final Color DEFAULT_BORDER_COLOR = Color.GREEN;

    private static final Color DEFAULT_FILL_COLOR = new Color(0, 255, 0, 0);

    private static final int DEFAULT_BORDER_THICKNESS = 3;

    private final JLabel addMarker = new JLabel(ADD_ICON);

    private final JLabel title = new JLabel();

    private final PluginErrorPanel noMarkersPanel = new PluginErrorPanel();

    private final JPanel markerView = new JPanel(new GridBagLayout());

    private final azScreenMarkerPlugin plugin;

    private Color selectedColor = DEFAULT_BORDER_COLOR;

    public Color getSelectedColor() {
        return this.selectedColor;
    }

    private Color selectedFillColor = DEFAULT_FILL_COLOR;

    public Color getSelectedFillColor() {
        return this.selectedFillColor;
    }

    private int selectedBorderThickness = 3;

    private ScreenMarkerCreationPanel creationPanel;

    public int getSelectedBorderThickness() {
        return this.selectedBorderThickness;
    }

    public ScreenMarkerCreationPanel getCreationPanel() {
        return this.creationPanel;
    }

    static {
        BufferedImage addIcon = ImageUtil.loadImageResource(azScreenMarkerPlugin.class, "add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53F));
    }

    public ScreenMarkerPluginPanel(azScreenMarkerPlugin screenMarkerPlugin) {
        this.plugin = screenMarkerPlugin;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));
        this.title.setText("Screen Markers");
        this.title.setForeground(Color.WHITE);
        northPanel.add(this.title, "West");
        northPanel.add(this.addMarker, "East");
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.markerView.setBackground(ColorScheme.DARK_GRAY_COLOR);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = 2;
        constraints.weightx = 1.0D;
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.noMarkersPanel.setContent("Screen Markers", "Highlight a region on your screen.");
        this.noMarkersPanel.setVisible(false);
        this.markerView.add((Component)this.noMarkersPanel, constraints);
        constraints.gridy++;
        this.creationPanel = new ScreenMarkerCreationPanel(this.plugin);
        this.creationPanel.setVisible(false);
        this.markerView.add(this.creationPanel, constraints);
        constraints.gridy++;
        this.addMarker.setToolTipText("Add new screen marker");
        this.addMarker.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                ScreenMarkerPluginPanel.this.setCreation(true);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                ScreenMarkerPluginPanel.this.addMarker.setIcon(ScreenMarkerPluginPanel.ADD_HOVER_ICON);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                ScreenMarkerPluginPanel.this.addMarker.setIcon(ScreenMarkerPluginPanel.ADD_ICON);
            }
        });
        centerPanel.add(this.markerView, "Center");
        add(northPanel, "North");
        add(centerPanel, "Center");
    }

    public void rebuild() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = 2;
        constraints.weightx = 1.0D;
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.markerView.removeAll();
        for (ScreenMarkerOverlay marker : this.plugin.getScreenMarkers()) {
            this.markerView.add(new ScreenMarkerPanel(this.plugin, marker), constraints);
            constraints.gridy++;
            this.markerView.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
            constraints.gridy++;
        }
        boolean empty = (constraints.gridy == 0);
        this.noMarkersPanel.setVisible(empty);
        this.title.setVisible(!empty);
        this.markerView.add((Component)this.noMarkersPanel, constraints);
        constraints.gridy++;
        this.markerView.add(this.creationPanel, constraints);
        constraints.gridy++;
        repaint();
        revalidate();
    }

    public void setCreation(boolean on) {
        if (on) {
            this.noMarkersPanel.setVisible(false);
            this.title.setVisible(true);
        } else {
            boolean empty = this.plugin.getScreenMarkers().isEmpty();
            this.noMarkersPanel.setVisible(empty);
            this.title.setVisible(!empty);
        }
        this.creationPanel.setVisible(on);
        this.addMarker.setVisible(!on);
        if (on) {
            this.creationPanel.lockConfirm();
            this.plugin.setMouseListenerEnabled(true);
            this.plugin.setCreatingScreenMarker(true);
        }
    }
}
