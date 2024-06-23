package com.blastlong.pmh.data.object;

public class LocationObject {
    public boolean isAvailable;
    public float x;
    public float y;
    public float z;

    // Constructor
    public LocationObject(boolean isAvailable, float x, float y, float z) {
        this.isAvailable = isAvailable;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // toString method
    @Override
    public String toString() {
        return String.format("[ x:%.1f  y:%.1f z:%.1f ]", x, y, z);
    }
}

