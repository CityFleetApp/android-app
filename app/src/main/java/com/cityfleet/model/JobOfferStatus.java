package com.cityfleet.model;

import com.cityfleet.R;

/**
 * Created by vika on 29.03.16.
 */
public enum JobOfferStatus {
    AVAILABLE(R.drawable.green_rectangle_rounded_corners), REQUESTED(R.drawable.orange_rectangle_rounded_corners),
    COVERED(R.drawable.orange_rectangle_rounded_corners),  AWARDED(R.drawable.red_rectangle_rounded_corners);
    private int drawableRes;

    JobOfferStatus(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public int getDrawableRes() {
        return drawableRes;
    }
}
