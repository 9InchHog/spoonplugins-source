package net.runelite.client.plugins.azscreenmarkers;

import java.awt.Color;

public class ScreenMarker {
    private long id;

    private String name;

    private int borderThickness;

    private Color color;

    private Color fill;

    private boolean visible;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ScreenMarker))
            return false;
        ScreenMarker other = (ScreenMarker)o;
        if (!other.canEqual(this))
            return false;
        if (getId() != other.getId())
            return false;
        if (getBorderThickness() != other.getBorderThickness())
            return false;
        if (isVisible() != other.isVisible())
            return false;
        Object this$name = getName(), other$name = other.getName();
        if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
            return false;
        Object this$color = getColor(), other$color = other.getColor();
        if ((this$color == null) ? (other$color != null) : !this$color.equals(other$color))
            return false;
        Object this$fill = getFill(), other$fill = other.getFill();
        return !((this$fill == null) ? (other$fill != null) : !this$fill.equals(other$fill));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScreenMarker;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = getId();
        result = result * 59 + (int)($id >>> 32L ^ $id);
        result = result * 59 + getBorderThickness();
        result = result * 59 + (isVisible() ? 79 : 97);
        Object $name = getName();
        result = result * 59 + (($name == null) ? 43 : $name.hashCode());
        Object $color = getColor();
        result = result * 59 + (($color == null) ? 43 : $color.hashCode());
        Object $fill = getFill();
        return result * 59 + (($fill == null) ? 43 : $fill.hashCode());
    }

    public String toString() {
        return "ScreenMarker(id=" + getId() + ", name=" + getName() + ", borderThickness=" + getBorderThickness() + ", color=" + getColor() + ", fill=" + getFill() + ", visible=" + isVisible() + ")";
    }

    public ScreenMarker() {}

    public ScreenMarker(long id, String name, int borderThickness, Color color, Color fill, boolean visible) {
        this.id = id;
        this.name = name;
        this.borderThickness = borderThickness;
        this.color = color;
        this.fill = fill;
        this.visible = visible;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getBorderThickness() {
        return this.borderThickness;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getFill() {
        return this.fill;
    }

    public boolean isVisible() {
        return this.visible;
    }
}
