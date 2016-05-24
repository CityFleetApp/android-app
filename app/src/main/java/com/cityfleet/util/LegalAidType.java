package com.cityfleet.util;

import com.cityfleet.R;

/**
 * Created by vika on 17.03.16.
 */
public enum LegalAidType {
    DMV_LAWYERS(R.drawable.lawyer_selected), TLC_LAWYERS(R.drawable.lawyer_selected), ACCOUNTANTS(R.drawable.calc_selected), BROKERS(R.drawable.carsafe_selected);

    int iconRes;

    LegalAidType(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getIconRes() {
        return iconRes;
    }
}
