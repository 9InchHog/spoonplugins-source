//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Bloat.stomp;

import net.runelite.client.plugins.theatre.Bloat.stomp.def.BloatPath;
import net.runelite.client.plugins.theatre.Bloat.stomp.def.BloatRotation;
import net.runelite.client.plugins.theatre.Bloat.stomp.def.DistanceInfo;
import net.runelite.client.plugins.theatre.util.coords.Coordinates;
import net.runelite.client.plugins.theatre.util.coords.SafespotLine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class BloatSafespot {
    private static final SafespotLine[][][] grid = new SafespotLine[][][]{{{new SafespotLine(new Coordinates(29, 39), new Coordinates(29, 34), -1, 1), new SafespotLine(new Coordinates(29, 29), new Coordinates(29, 24), -1, -1)}, {new SafespotLine(new Coordinates(34, 39), new Coordinates(34, 34), 1, 1), new SafespotLine(new Coordinates(34, 29), new Coordinates(34, 24), 1, -1)}}, {{new SafespotLine(new Coordinates(24, 34), new Coordinates(29, 34), -1, 1), new SafespotLine(new Coordinates(34, 34), new Coordinates(39, 34), 1, 1)}, {new SafespotLine(new Coordinates(28, 29), new Coordinates(23, 29), 1, -1), new SafespotLine(new Coordinates(34, 29), new Coordinates(39, 29), 1, -1)}}};
    private final BloatPath bloatPath;
    private final BloatRotation clockRotation;
    private final DistanceInfo distanceInfo;

    public BloatSafespot(Pair<BloatPath, BloatRotation> pair, Supplier<Integer> distance) {
        this.bloatPath = pair.getLeft();
        this.clockRotation = pair.getRight();
        this.distanceInfo = new DistanceInfo(distance.get());
    }

    public List<SafespotLine> getSafespotLines() {
        return this.distanceInfo.isCorner() ? this.getCornerSafespots() : this.getSideSafespots();
    }

    private List<SafespotLine> getCornerSafespots() {
        if (this.bloatPath == BloatPath.UNKNOWN) {
            return Collections.emptyList();
        } else {
            Pair<SafespotLine[], int[]> lop = this.bloatPath.getCornerSafespots(grid).get(this.distanceInfo.getCornerIndex(this.clockRotation.isClockwise()));
            SafespotLine[] safespotLines = lop.getLeft();
            if (!this.distanceInfo.shouldModifyCorner(this.bloatPath)) {
                return Arrays.asList(safespotLines);
            } else {
                byte bit = (byte)(this.bloatPath != BloatPath.N_PATH && this.bloatPath != BloatPath.S_PATH ? 0 : 1);
                boolean isCol = bit == 0;
                int[] offsets = lop.getRight();
                return Arrays.asList(safespotLines[bit].offset((c) -> {
                    return c.dx(isCol ? offsets[0] : offsets[2]).dy(isCol ? offsets[1] : offsets[3]);
                }), safespotLines[(3 + bit) % 2]);
            }
        }
    }

    private List<SafespotLine> getSideSafespots() {
        if (this.bloatPath == BloatPath.UNKNOWN) {
            return Collections.emptyList();
        } else {
            SafespotLine[] safespotLines = this.bloatPath.getSideSafespotLines(grid);
            if (!this.clockRotation.isClockwise()) {
                ArrayUtils.reverse(safespotLines);
            }

            List<Integer> offsets = this.bloatPath.getSideOffsets(this.distanceInfo.isSideMin());
            return Arrays.asList(safespotLines[0].offset((c) -> {
                return c.dx(this.bloatPath.shouldOffsetX() ? offsets.get(0) : 0).dy(this.bloatPath.shouldOffsetY() ? offsets.get(0) : 0);
            }), safespotLines[1].offset((c) -> {
                return c.dx(this.bloatPath.shouldOffsetX() ? offsets.get(1) : 0).dy(this.bloatPath.shouldOffsetY() ? offsets.get(1) : 0);
            }));
        }
    }
}
