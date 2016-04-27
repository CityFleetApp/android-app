package com.cityfleet.view.main.benefits;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BenefitsImageView extends ImageView {

    private static final double ratio = (double) 0.7;

    public BenefitsImageView(Context context) {
        super(context);
    }

    public BenefitsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        double height = getMeasuredWidth() * ratio;
        setMeasuredDimension(getMeasuredWidth(), (int) height);
    }
}
