package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.GeneralGood;
import com.cityfleet.model.Photo;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.profile.ProfileFragment;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.IconPageIndicator;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 30.03.16.
 */
public class GeneralGoodDetailFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.goodsImagePager)
    ViewPager goodsImagePager;
    @Bind(R.id.goodsPrice)
    TextView goodsPrice;
    @Bind(R.id.goodsTitle)
    TextView goodsTitle;
    @Bind(R.id.typeLbl)
    TextView typeLbl;
    @Bind(R.id.detailsText)
    TextView detailsText;
    @Bind(R.id.authorLbl)
    TextView authorLbl;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;
    @Bind(R.id.pageIndicator)
    IconPageIndicator pageIndicator;
    private GeneralGood generalGood;
    private RentSaleGalleryPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_good_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.marketplace);
        closeBtn.setVisibility(View.VISIBLE);
        generalGood = Parcels.unwrap(getArguments().getParcelable(Constants.GENERAL_GOODS_TAG));
        List<Photo> photoList = generalGood.getPhotos();
        if (photoList.isEmpty()) {
            photoList.add(new Photo());
            photoList.get(0).setUrl("error");
        }
        adapter = new RentSaleGalleryPagerAdapter(getContext(), photoList, GeneralGoodDetailFragment.class.getName());
        goodsImagePager.setAdapter(adapter);
        if (photoList.size() > 1) {
            pageIndicator.setViewPager(goodsImagePager);
            goodsImagePager.addOnPageChangeListener(this);
        }
        goodsPrice.setText(getString(R.string.dollar_price, generalGood.getPrice()));
        goodsTitle.setText(generalGood.getItem());
        typeLbl.setText(generalGood.getCondition());
        detailsText.setText(generalGood.getDescription());
        authorLbl.setText(generalGood.getOwnerName());
        return view;
    }

    @OnClick(R.id.authorLbl)
    void onAuthorLblClicked() {
        ((BaseActivity) getActivity()).changeFragment(ProfileFragment.getInstanceForFriend(generalGood.getOwner(), getContext()), true);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.closeBtn)
    void onCloseBtnClicked() {
        ((BaseActivity) getActivity()).goToTop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getContext()).cancelTag(GeneralGoodDetailFragment.class.getName());
        ButterKnife.unbind(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RentSaleGalleryPagerAdapter) goodsImagePager.getAdapter()).setCurrentPosition(position);
        pageIndicator.setCurrentItem(position);
        pageIndicator.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
