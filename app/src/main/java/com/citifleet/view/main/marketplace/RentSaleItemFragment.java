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
import com.citifleet.model.Car;
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
public class RentSaleItemFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.carImagePager)
    ViewPager carImagePager;
    @Bind(R.id.carPrice)
    TextView carPrice;
    @Bind(R.id.carTitle)
    TextView carTitle;
    @Bind(R.id.colorLbl)
    TextView colorLbl;
    @Bind(R.id.fuelLbl)
    TextView fuelLbl;
    @Bind(R.id.modelLbl)
    TextView modelLbl;
    @Bind(R.id.seatsLbl)
    TextView seatsLbl;
    @Bind(R.id.typeLbl)
    TextView typeLbl;
    @Bind(R.id.detailsText)
    TextView detailsText;
    private Car car;
    private RentSaleGalleryPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_sale_item_fragment, container, false);
        ButterKnife.bind(this, view);

        title.setText(R.string.marketplace);
        car = Parcels.unwrap(getArguments().getParcelable(Constants.CAR_RENT_SALE_TAG));
        adapter = new RentSaleGalleryPagerAdapter(getContext(), car.getPhotos(), RentSaleItemFragment.class.getName());
        carImagePager.setAdapter(adapter);
        carPrice.setText(car.getPrice());
        carTitle.setText(car.getYear() + " " + car.getMake() + " " + car.getModel());
        colorLbl.setText(car.getColor());
        fuelLbl.setText(car.getFuel());
        seatsLbl.setText(String.valueOf(car.getSeats()));
        typeLbl.setText(car.getType());
        modelLbl.setText(car.getModel());
        detailsText.setText(car.getDescription());
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getContext()).cancelTag(RentSaleItemFragment.class.getName());
        ButterKnife.unbind(this);
    }
}