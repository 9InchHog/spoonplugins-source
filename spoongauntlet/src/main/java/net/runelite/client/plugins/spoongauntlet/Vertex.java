package net.runelite.client.plugins.spoongauntlet;

import net.runelite.api.Perspective;

public final class Vertex {
    private final int x;
    private final int y;
    private final int z;

    public Vertex rotate(int orientation) {
        orientation = (orientation + 1024) % 2048;
        if (orientation == 0) {
            return this;
        } else {
            int sin = Perspective.SINE[orientation];
            int cos = Perspective.COSINE[orientation];
            return new Vertex(this.x * cos + this.z * sin >> 16, this.y, this.z * cos - this.x * sin >> 16);
        }
    }

    public Vertex(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Vertex)) {
            return false;
        } else {
            Vertex other = (Vertex)o;
            if (this.getX() != other.getX()) {
                return false;
            } else if (this.getY() != other.getY()) {
                return false;
            } else {
                return this.getZ() == other.getZ();
            }
        }
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + getX();
        result = result * 59 + getY();
        return result * 59 + getZ();
    }

    public String toString() {
        return "Vertex(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ")";
    }
}
