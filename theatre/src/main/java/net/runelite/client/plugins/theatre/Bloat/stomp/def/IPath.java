//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Bloat.stomp.def;

import net.runelite.client.plugins.theatre.util.coords.SafespotLine;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public interface IPath {
    int[] NW_OFFSETS = new int[]{-1, 0, 0, 1};
    int[] NE_OFFSETS = new int[]{1, 0, 0, 1};
    int[] SW_OFFSETS = new int[]{-1, 0, 0, -1};
    int[] SE_OFFSETS = new int[]{1, 0, 0, -1};
    int SIDE_MAX_OFFSET = 4;
    int SIDE_MIN_OFFSET = 2;

    default List<Pair<SafespotLine[], int[]>> getCornerSafespots(SafespotLine[][][] bloatGrid) {
        return null;
    }

    default SafespotLine[] getSideSafespotLines(SafespotLine[][][] bloatGrid) {
        return null;
    }

    default boolean areOffsetsNegative() {
        return false;
    }

    default boolean shouldOffsetX() {
        return false;
    }

    default boolean shouldOffsetY() {
        return false;
    }

    default List<Integer> getSideOffsets(boolean sideMin) {
        return Arrays.asList(sideMin ? (this.areOffsetsNegative() ? -4 : 4) : (this.areOffsetsNegative() ? -2 : 2), sideMin ? (this.areOffsetsNegative() ? -2 : 2) : (this.areOffsetsNegative() ? -4 : 4));
    }

    static SafespotLine[] getNWCornerLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][1][0], bloatGrid[1][1][0]};
    }

    static SafespotLine[] getNECornerLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][0][0], bloatGrid[1][1][1]};
    }

    static SafespotLine[] getSWCornerLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][1][1], bloatGrid[1][0][0]};
    }

    static SafespotLine[] getSECornerLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][0][1], bloatGrid[1][0][1]};
    }

    static SafespotLine[] getNorthLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[1][0][0], bloatGrid[1][0][1]};
    }

    static SafespotLine[] getEastLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][1][0], bloatGrid[0][1][1]};
    }

    static SafespotLine[] getSouthLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[1][1][1], bloatGrid[1][1][0]};
    }

    static SafespotLine[] getWestLines(SafespotLine[][][] bloatGrid) {
        return new SafespotLine[]{bloatGrid[0][0][1], bloatGrid[0][0][0]};
    }
}
