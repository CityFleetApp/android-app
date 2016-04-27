package com.cityfleet.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by vika on 19.04.16.
 */
public class ChatRoomImageView extends ImageView {
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private Bitmap thirdBitmap;
    private Bitmap fourthBitmap;
    private BitmapShader bitmapShader;
    private Paint paint;

    public ChatRoomImageView(Context context) {
        super(context);
    }

    public ChatRoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (paint == null) {
            setup();
        }
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, paint);
    }


    public void setBitmaps(Bitmap firstBitmap, Bitmap secondBitmap, Bitmap thirdBitmap, Bitmap fourthBitmap) {
        this.firstBitmap = firstBitmap;
        this.secondBitmap = secondBitmap;
        this.thirdBitmap = thirdBitmap;
        this.fourthBitmap = fourthBitmap;
        paint = null;
    }

    public void clearBitmaps() {
        firstBitmap = null;
        secondBitmap = null;
        thirdBitmap = null;
        fourthBitmap = null;
    }

    public void setBitmap(Bitmap bitmap, int position) {
        switch (position) {
            case 0:
                firstBitmap = bitmap;
                break;
            case 1:
                secondBitmap = bitmap;
                break;
            case 2:
                thirdBitmap = bitmap;
                break;
            case 3:
                fourthBitmap = bitmap;
                break;
        }
        paint = null;
    }

    public void setup() {
        paint = new Paint();
        if (firstBitmap != null && secondBitmap == null && thirdBitmap == null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, getWidth(), getHeight());
            bitmapShader = new BitmapShader(cuttedFirstBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
            //    firstBitmap.recycle();
            // cuttedFirstBitmap.recycle();
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap == null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, getWidth() / 2, getHeight());
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, getWidth() / 2, getHeight());
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, getWidth() / 2, getHeight());
            Rect secondDstRect = new Rect();
            secondDstRect.set(getWidth() / 2, 0, getWidth(), getHeight());

            Bitmap resultBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
            bitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
//            firstBitmap.recycle();
//            secondBitmap.recycle();
//            cuttedFirstBitmap.recycle();
//            cuttedSecondBitmap.recycle();
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap != null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, getWidth() / 2, getHeight());
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, getWidth() / 2, getHeight() / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(thirdBitmap, getWidth() / 2, getHeight() / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, getWidth() / 2, getHeight());
            Rect secondDstRect = new Rect();
            secondDstRect.set(getWidth() / 2, 0, getWidth(), getHeight() / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(getWidth() / 2, getHeight() / 2, getWidth(), getHeight());

            Bitmap resultBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, paint);
            bitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
//            firstBitmap.recycle();
//            secondBitmap.recycle();
//            thirdBitmap.recycle();
//            cuttedFirstBitmap.recycle();
//            cuttedSecondBitmap.recycle();
//            cuttedThirdBitmap.recycle();
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap != null && fourthBitmap != null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, getWidth() / 2, getHeight() / 2);
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, getWidth() / 2, getHeight() / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(thirdBitmap, getWidth() / 2, getHeight() / 2);
            Bitmap cuttedFourthBitmap = ThumbnailUtils.extractThumbnail(fourthBitmap, getWidth() / 2, getHeight() / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, getWidth() / 2, getHeight() / 2);
            Rect secondDstRect = new Rect();
            secondDstRect.set(getWidth() / 2, 0, getWidth(), getHeight() / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(0, getHeight() / 2, getWidth() / 2, getHeight());
            Rect fourthDstRect = new Rect();
            fourthDstRect.set(getWidth() / 2, getHeight() / 2, getWidth(), getHeight());

            Bitmap resultBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, paint);
            resultBitmapCanvas.drawBitmap(cuttedFourthBitmap, null, fourthDstRect, paint);
            bitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
//            firstBitmap.recycle();
//            secondBitmap.recycle();
//            thirdBitmap.recycle();
//            fourthBitmap.recycle();
//            cuttedFirstBitmap.recycle();
//            cuttedSecondBitmap.recycle();
//            cuttedThirdBitmap.recycle();
//            cuttedFourthBitmap.recycle();
        } else {
            paint.setColor(Color.TRANSPARENT);
        }
    }
}
