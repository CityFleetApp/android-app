package com.citifleet.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    private int frameSize;
    private static final int FRAME_ALPHA = 43;

    public CircleTransform(int frameSize) {
        this.frameSize = frameSize;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);


        float bigRadius = size / 2f;
        float smallRadius = size / 2f - 2 * frameSize;


        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAlpha(FRAME_ALPHA);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(frameSize);
        canvas.drawCircle(bigRadius, bigRadius, smallRadius + frameSize / 2, paint);

        paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        canvas.drawCircle(bigRadius, bigRadius, smallRadius, paint);
        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "(circle):frame_size=" + frameSize;
    }
}