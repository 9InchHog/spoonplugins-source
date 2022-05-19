package net.runelite.client.plugins.socket.plugins.socketping;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Objects;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;

public class RadialWheelWidget {
    @Getter
    @Setter
    private Polygon poly;
    @Getter
    @Setter
    private Point location = new Point(0, 0);
    @Getter
    @Setter
    private boolean selected = false;
    @Getter
    @Setter
    private Consumer<Point> updateFunction;
    @Getter
    @Setter
    private Runnable function;
    @Getter
    @Setter
    private Color color;
    @Getter
    @Setter
    private BufferedImage bufferedImage;
    @Getter
    @Setter
    private Point pictureLocationOffset;

    public RadialWheelWidget(int outer_size, int inner_size, double offset) {
        color = Color.white;
        pictureLocationOffset = new Point(0, 0);
        int x_multiplier = 1;
        int y_multiplier = 1;
        if ((!(offset > 0.5D) || !(offset < 1.0D)) && (!(offset > 1.5D) || !(offset < 2.0D))) {
            x_multiplier = -1;
        } else {
            y_multiplier = -1;
        }

        int stepsize = 10;
        int[] _xs = new int[stepsize * 2];
        int[] _ys = new int[stepsize * 2];

        int i;
        double _y;
        double _x;
        int x;
        int y;
        for(i = 0; i < stepsize; ++i) {
            _y = (double)outer_size * Math.sin((double)i / (double)(stepsize - 1) * 3.141592653589793D / 2.0D + offset * 3.141592653589793D);
            _x = (double)outer_size * Math.cos((double)i / (double)(stepsize - 1) * 3.141592653589793D / 2.0D + offset * 3.141592653589793D);
            x = (int)_x;
            y = (int)_y;
            _xs[i] = x;
            _ys[i] = y;
        }

        for(i = 0; i < stepsize; ++i) {
            _y = (double)(y_multiplier * inner_size) * Math.sin((double)i / (double)(stepsize - 1) * 3.141592653589793D / 2.0D + offset * 3.141592653589793D);
            _x = (double)(x_multiplier * inner_size) * Math.cos((double)i / (double)(stepsize - 1) * 3.141592653589793D / 2.0D + offset * 3.141592653589793D);
            x = (int)_x;
            y = (int)_y;
            _xs[i + stepsize] = x;
            _ys[i + stepsize] = y;
        }

        poly = new Polygon(_xs, _ys, _xs.length);
    }

    public boolean isContainedIn(Point point) {
        Point point1 = new Point(point.getX() - location.getX(), point.getY() - location.getY());
        return poly.contains(new java.awt.Point(point1.getX(), point1.getY()));
    }

    public void updateLocation(Point point) {
        updateFunction.accept(point);
    }

    public void draw(Graphics2D graphics) {
        Polygon polygon = new Polygon(poly.xpoints.clone(), poly.ypoints.clone(), poly.npoints);
        polygon.translate(location.getX(), location.getY());
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(2.0F));
        graphics.draw(polygon);
        if (selected) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 125));
        } else {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        }

        graphics.fill(polygon);
        int x = pictureLocationOffset.getX() - bufferedImage.getWidth() / 2 + location.getX();
        int y = pictureLocationOffset.getY() - bufferedImage.getHeight() / 2 + location.getY();
        graphics.drawImage(bufferedImage, x, y, null);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RadialWheelWidget))
            return false;
        RadialWheelWidget other = (RadialWheelWidget)o;
        if (!other.canEqual(this))
            return false;
        Object this$poly = getPoly(), other$poly = other.getPoly();
        if (!Objects.equals(this$poly, other$poly))
            return false;
        Object this$location = getLocation(), other$location = other.getLocation();
        if (!Objects.equals(this$location, other$location))
            return false;
        if (isSelected() != other.isSelected())
            return false;
        Object this$updateFunction = getUpdateFunction(), other$updateFunction = other.getUpdateFunction();
        if (!Objects.equals(this$updateFunction, other$updateFunction))
            return false;
        Object this$function = getFunction(), other$function = other.getFunction();
        if (!Objects.equals(this$function, other$function))
            return false;
        Object this$color = getColor(), other$color = other.getColor();
        if (!Objects.equals(this$color, other$color))
            return false;
        Object this$bufferedImage = getBufferedImage(), other$bufferedImage = other.getBufferedImage();
        if (!Objects.equals(this$bufferedImage, other$bufferedImage))
            return false;
        Object this$pictureLocationOffset = getPictureLocationOffset(), other$pictureLocationOffset = other.getPictureLocationOffset();
        return Objects.equals(this$pictureLocationOffset, other$pictureLocationOffset);
    }

    protected boolean canEqual(Object other) {
        return other instanceof RadialWheelWidget;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $poly = getPoly();
        result = result * 59 + (($poly == null) ? 43 : $poly.hashCode());
        Object $location = getLocation();
        result = result * 59 + (($location == null) ? 43 : $location.hashCode());
        result = result * 59 + (isSelected() ? 79 : 97);
        Object $updateFunction = getUpdateFunction();
        result = result * 59 + (($updateFunction == null) ? 43 : $updateFunction.hashCode());
        Object $function = getFunction();
        result = result * 59 + (($function == null) ? 43 : $function.hashCode());
        Object $color = getColor();
        result = result * 59 + (($color == null) ? 43 : $color.hashCode());
        Object $bufferedImage = getBufferedImage();
        result = result * 59 + (($bufferedImage == null) ? 43 : $bufferedImage.hashCode());
        Object $pictureLocationOffset = getPictureLocationOffset();
        return result * 59 + (($pictureLocationOffset == null) ? 43 : $pictureLocationOffset.hashCode());
    }

    public String toString() {
        Polygon var10000 = getPoly();
        return "RadialWheelWidget(poly=" + var10000 + ", location=" + getLocation() + ", selected=" + isSelected() + ", updateFunction=" + getUpdateFunction() + ", function=" + getFunction() + ", color=" + getColor() + ", bufferedImage=" + getBufferedImage() + ", pictureLocationOffset=" + getPictureLocationOffset() + ")";
    }
}
