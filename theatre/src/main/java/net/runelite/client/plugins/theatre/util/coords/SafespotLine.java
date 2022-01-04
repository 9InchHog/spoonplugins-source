//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.util.coords;

import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public final class SafespotLine {
    private final Coordinates coordA;
    private final Coordinates coordB;
    private final int sigX;
    private final int sigY;

    public SafespotLine(Coordinates coordA, Coordinates coordB, int sigX, int sigY) {
        this.coordA = coordA;
        this.coordB = coordB;
        this.sigX = Integer.signum(sigX);
        this.sigY = Integer.signum(sigY);
    }

    @Nullable
    public Point getTranslatedPointA(@Nonnull Client client) {
        return translate(client, this.coordA, this.sigX, this.sigY);
    }

    @Nullable
    public Point getTranslatedPointB(@Nonnull Client client) {
        return translate(client, this.coordB, this.sigX, this.sigY);
    }

    public SafespotLine offset(UnaryOperator<Coordinates> offsetFunc) {
        return offsetFunc == null ? this : this.offset(offsetFunc, offsetFunc);
    }

    public SafespotLine offset(UnaryOperator<Coordinates> offsetFuncA, UnaryOperator<Coordinates> offsetFuncB) {
        return new SafespotLine(offsetFuncA == null ? this.coordA : offsetFuncA.apply(this.coordA), offsetFuncB == null ? this.coordB : offsetFuncB.apply(this.coordB), this.sigX, this.sigY);
    }

    @Nullable
    private static Point translate(@Nonnull Client client, @Nonnull Coordinates coords, int sigX, int sigY) {
        Player player = client.getLocalPlayer();
        if (player == null) {
            return null;
        } else {
            int regionID = player.getWorldLocation().getRegionID();
            int plane = client.getPlane();
            LocalPoint local = LocalPoint.fromWorld(client, WorldPoint.fromRegion(regionID, coords.getX(), coords.getY(), plane));
            if (local == null) {
                return null;
            } else {
                int x = local.getX() + getTileOffset(sigX);
                int y = local.getY() + getTileOffset(sigY);
                return Perspective.localToCanvas(client, x, y, client.getTileHeights()[plane][x >> 7][y >> 7]);
            }
        }
    }

    private static int getTileOffset(int sig) {
        if (sig == 0) {
            return 0;
        } else {
            boolean off = true;
            return sig > 0 ? 64 : -64;
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof SafespotLine)) {
            return false;
        } else {
            return this.coordA.equals(((SafespotLine)other).coordA) && this.coordB.equals(((SafespotLine)other).coordB);
        }
    }

    public int hashCode() {
        return Objects.hash(this.coordA, this.coordB, this.sigX, this.sigY);
    }

    public Coordinates getCoordA() {
        return this.coordA;
    }

    public Coordinates getCoordB() {
        return this.coordB;
    }
}
