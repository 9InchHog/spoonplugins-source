package net.runelite.client.plugins.azscreenmarkers;

import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

import java.awt.*;

public class ScreenMarkerRenderable implements LayoutableRenderableEntity {
    private Point preferredLocation;

    private Dimension preferredSize;

    private int borderThickness;

    private Color color;

    private Color fill;

    private Stroke stroke;

    Point getPreferredLocation() {
        return this.preferredLocation;
    }

    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    Dimension getPreferredSize() {
        return this.preferredSize;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    int getBorderThickness() {
        return this.borderThickness;
    }

    void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    Color getColor() {
        return this.color;
    }

    void setColor(Color color) {
        this.color = color;
    }

    Color getFill() {
        return this.fill;
    }

    void setFill(Color fill) {
        this.fill = fill;
    }

    Stroke getStroke() {
        return this.stroke;
    }

    void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    private final Rectangle bounds = new Rectangle();

    public Rectangle getBounds() {
        return this.bounds;
    }

    public Dimension render(Graphics2D graphics) {
        int thickness = this.borderThickness;
        int width = this.preferredSize.width;
        int height = this.preferredSize.height;
        graphics.setColor(this.fill);
        graphics.fillRect(thickness, thickness, width - thickness * 2, height - thickness * 2);
        int offset = thickness / 2;
        graphics.setColor(this.color);
        graphics.setStroke(this.stroke);
        graphics.drawRect(offset, offset, width - thickness, height - thickness);
        this.bounds.setSize(this.preferredSize);
        return this.preferredSize;
    }
}
