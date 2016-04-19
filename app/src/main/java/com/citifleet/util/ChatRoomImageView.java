package com.citifleet.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
        Paint paint = new Paint();
        if (firstBitmap != null && secondBitmap == null && thirdBitmap == null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(cuttedFirstBitmap, 0, 0, paint);
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap == null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, canvas.getWidth() / 2, canvas.getHeight());
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, canvas.getWidth() / 2, canvas.getHeight());
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, canvas.getWidth() / 2, canvas.getHeight());
            Rect secondDstRect = new Rect();
            secondDstRect.set(canvas.getWidth() / 2, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            canvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap != null && fourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, canvas.getWidth() / 2, canvas.getHeight());
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(thirdBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, canvas.getWidth() / 2, canvas.getHeight());
            Rect secondDstRect = new Rect();
            secondDstRect.set(canvas.getWidth() / 2, 0, canvas.getWidth(), canvas.getHeight() / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            canvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
            canvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, paint);
        } else if (firstBitmap != null && secondBitmap != null && thirdBitmap != null && fourthBitmap != null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(firstBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(secondBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(thirdBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Bitmap cuttedFourthBitmap = ThumbnailUtils.extractThumbnail(fourthBitmap, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, canvas.getWidth() / 2, canvas.getHeight() / 2);
            Rect secondDstRect = new Rect();
            secondDstRect.set(canvas.getWidth() / 2, 0, canvas.getWidth(), canvas.getHeight() / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(0, canvas.getHeight() / 2, canvas.getWidth() / 2, canvas.getHeight());
            Rect fourthDstRect = new Rect();
            fourthDstRect.set(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, paint);
            canvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, paint);
            canvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, paint);
            canvas.drawBitmap(cuttedFourthBitmap, null, fourthDstRect, paint);
        }
    }

    public void addBitmap(Bitmap bitmap) {
        if (firstBitmap == null) {
            firstBitmap = bitmap;
        } else if (secondBitmap == null) {
            secondBitmap = bitmap;
        } else if (thirdBitmap == null) {
            thirdBitmap = bitmap;
        } else if (fourthBitmap == null) {
            fourthBitmap = bitmap;
        }
        invalidate();
    }
}
