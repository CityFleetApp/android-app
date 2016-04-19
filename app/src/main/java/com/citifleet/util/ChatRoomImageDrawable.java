package com.citifleet.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by vika on 19.04.16.
 */
public class ChatRoomImageDrawable extends ImageView {
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private Bitmap thirdBitmap;
    private Bitmap fourthBitmap;


    public ChatRoomImageDrawable(Context context) {
        super(context);
        init();
    }

    public ChatRoomImageDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

//TODO
//    public ChatRoomImageDrawable(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setFirstBitmap(Bitmap firstBitmap) {
        this.firstBitmap = firstBitmap;
    }

    public void setSecondBitmap(Bitmap secondBitmap) {
        this.secondBitmap = secondBitmap;
    }

    public void setThirdBitmap(Bitmap thirdBitmap) {
        this.thirdBitmap = thirdBitmap;
    }

    public void setFourthBitmap(Bitmap fourthBitmap) {
        this.fourthBitmap = fourthBitmap;
    }
}
