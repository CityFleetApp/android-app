package com.citifleet.view.main.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.UserImages;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 15.03.16.
 */
public class GalleryImageFragment extends BaseFragment {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.title)
    TextView title;
    private List<UserImages> imagesList;
    private int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        ButterKnife.bind(this, view);
        imagesList = Parcels.unwrap(getArguments().getParcelable(Constants.IMAGES_LIST_TAG));
        position = getArguments().getInt(Constants.IMAGES_SELECTED_TAG);
        pager.setAdapter(new GalleryPagerAdapter(getContext(), imagesList));
        pager.setCurrentItem(position, false);
        title.setText(getString(R.string.gallery_title, position + 1, imagesList.size()));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title.setText(getString(R.string.gallery_title, position + 1, imagesList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
