package com.citifleet.view.main.marketplace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.citifleet.R;
import com.citifleet.model.UserImages;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by vika on 30.03.16.
 */
public class RentSaleGalleryPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> images;
    private String picassoTag;

    public RentSaleGalleryPagerAdapter(Context context, List<String> images, String picassoTag) {
        this.images = images;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.picassoTag = picassoTag;
    }

    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.rent_sale_image_item, container, false);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.carImage);
        final ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        Picasso.with(context).load(images.get(position)).tag(picassoTag).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}