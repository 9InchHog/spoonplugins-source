//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.theatre.Bloat.stomp.def;

import net.runelite.client.plugins.theatre.util.coords.SafespotLine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public enum BloatPath implements IPath {
    N_PATH {
        public List<Pair<SafespotLine[], int[]>> getCornerSafespots(SafespotLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getSECornerLines(bloatGrid), SE_OFFSETS), new ImmutablePair<SafespotLine[], int[]>(IPath.getSWCornerLines(bloatGrid), SW_OFFSETS));
        }

        public SafespotLine[] getSideSafespotLines(SafespotLine[][][] bloatGrid) {
            return IPath.getNorthLines(bloatGrid);
        }

        public boolean areOffsetsNegative() {
            return true;
        }

        public boolean shouldOffsetY() {
            return true;
        }
    },
    E_PATH {
        public List<Pair<SafespotLine[], int[]>> getCornerSafespots(SafespotLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getSWCornerLines(bloatGrid), SW_OFFSETS), new ImmutablePair<SafespotLine[], int[]>(IPath.getNWCornerLines(bloatGrid), NW_OFFSETS));
        }

        public SafespotLine[] getSideSafespotLines(SafespotLine[][][] bloatGrid) {
            return IPath.getEastLines(bloatGrid);
        }

        public boolean areOffsetsNegative() {
            return true;
        }

        public boolean shouldOffsetX() {
            return true;
        }
    },
    S_PATH {
        public List<Pair<SafespotLine[], int[]>> getCornerSafespots(SafespotLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getNWCornerLines(bloatGrid), NW_OFFSETS), new ImmutablePair<SafespotLine[], int[]>(IPath.getNECornerLines(bloatGrid), NE_OFFSETS));
        }

        public SafespotLine[] getSideSafespotLines(SafespotLine[][][] bloatGrid) {
            return IPath.getSouthLines(bloatGrid);
        }

        public boolean shouldOffsetY() {
            return true;
        }
    },
    W_PATH {
        public List<Pair<SafespotLine[], int[]>> getCornerSafespots(SafespotLine[][][] bloatGrid) {
            return Arrays.asList(new ImmutablePair<>(IPath.getNECornerLines(bloatGrid), NE_OFFSETS), new ImmutablePair<SafespotLine[], int[]>(IPath.getSECornerLines(bloatGrid), SE_OFFSETS));
        }

        public SafespotLine[] getSideSafespotLines(SafespotLine[][][] bloatGrid) {
            return IPath.getWestLines(bloatGrid);
        }

        public boolean shouldOffsetX() {
            return true;
        }
    },
    UNKNOWN;

    BloatPath() {
    }
}
