package com.cityfleet.view.main.chat;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by vika on 18.05.16.
 */
public class ChatImageDetailFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.image)
    ImageViewTouch image;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_image_gallery, container, false);
        ButterKnife.bind(this, view);
        imageUrl = getArguments().getString(Constants.IMAGE_URL_TAG);
        title.setText(R.string.chatting);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        progressBar.setVisibility(View.VISIBLE);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                    image.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(getContext()).load(imageUrl).into(target);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    public static ChatImageDetailFragment getInstance(String imageUrl) {
        ChatImageDetailFragment fragment = new ChatImageDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.IMAGE_URL_TAG, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
