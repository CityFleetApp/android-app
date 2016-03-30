package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.GeneralGood;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 30.03.16.
 */
public class GeneralGoodDetailFragment extends BaseFragment {
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
    private GeneralGood generalGood;
    private RentSaleGalleryPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_good_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.marketplace);
        generalGood = Parcels.unwrap(getArguments().getParcelable(Constants.GENERAL_GOODS_TAG));
        adapter = new RentSaleGalleryPagerAdapter(getContext(), generalGood.getPhotos(), GeneralGoodDetailFragment.class.getName());
        goodsImagePager.setAdapter(adapter);
        goodsPrice.setText(generalGood.getPrice());
        goodsTitle.setText(generalGood.getItem());
        typeLbl.setText(generalGood.getCondition());
        detailsText.setText(generalGood.getDescription());
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getContext()).cancelTag(GeneralGoodDetailFragment.class.getName());
        ButterKnife.unbind(this);
    }
}