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
import com.cityfleet.model.Car;
import com.cityfleet.model.Photo;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

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
    @Bind(R.id.detailsText)
    TextView detailsText;
    @Bind(R.id.authorLbl)
    TextView authorLbl;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;
    private Car car;
    private RentSaleGalleryPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rent_sale_item_fragment, container, false);
        ButterKnife.bind(this, view);
        closeBtn.setVisibility(View.VISIBLE);
        title.setText(R.string.marketplace);
        car = Parcels.unwrap(getArguments().getParcelable(Constants.CAR_RENT_SALE_TAG));
        List<Photo> photoList = car.getPhotos();
        if (photoList.isEmpty()) {
            photoList.add(new Photo());
            photoList.get(0).setUrl("error");
        }
        adapter = new RentSaleGalleryPagerAdapter(getContext(), car.getPhotos(), RentSaleItemFragment.class.getName());
        carImagePager.setAdapter(adapter);
        carPrice.setText(getString(R.string.dollar_price, car.getPrice()));
        carTitle.setText(car.getYear() + " " + car.getMake() + " " + car.getModel());
        colorLbl.setText(car.getColor());
        fuelLbl.setText(car.getFuel());
        seatsLbl.setText(String.valueOf(car.getSeats()));
        modelLbl.setText(car.getType());
        detailsText.setText(car.getDescription());
        authorLbl.setText(car.getOwnerName());
        return view;
    }

    @OnClick(R.id.authorLbl)
    void onAuthorLblClicked() {
        ((BaseActivity) getActivity()).changeFragment(ProfileFragment.getInstanceForFriend(car.getOwner()), true);
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
        Picasso.with(getContext()).cancelTag(RentSaleItemFragment.class.getName());
        ButterKnife.unbind(this);
    }
}
