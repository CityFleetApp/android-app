package com.cityfleet.view.main.marketplace;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cityfleet.R;
import com.cityfleet.model.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.List;

/**
 * Created by vika on 30.03.16.
 */
public class RentSaleGalleryPagerAdapter extends PagerAdapter implements IconPagerAdapter {
    int iconResId = R.drawable.bullet_inactive;
    int activeIconResId = R.drawable.bullet_active;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Photo> images;
    private String picassoTag;
    int currentItem = 0;

    public RentSaleGalleryPagerAdapter(Context context, List<Photo> images, String picassoTag) {
        this.images = images;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.picassoTag = picassoTag;
    }

    @Override
    public int getIconResId(int i) {
        if (i == currentItem)
            return activeIconResId;

        return iconResId;
    }

    public void setCurrentPosition(int i) {
        currentItem = i;
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
        Picasso.with(context).load(images.get(position).getUrl()).tag(picassoTag).error(R.drawable.painting_big).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
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