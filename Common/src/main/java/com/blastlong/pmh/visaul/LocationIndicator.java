package com.blastlong.pmh.visaul;

import com.blastlong.pmh.data.object.LocationObject;

public class LocationIndicator {

    private boolean isIndicating = false;
    private LocationObject locationObject;

    public void indicateLocation(LocationObject locationObject) {
        isIndicating = true;
        this.locationObject = locationObject;
    }

    public void stopIndicating() {
        isIndicating = false;
        this.locationObject = null;
    }

    public boolean isIndicating() {
        return isIndicating;
    }

    public LocationObject getLocationObject() {
        return locationObject;
    }
}
