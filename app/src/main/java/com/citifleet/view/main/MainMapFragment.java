package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMapFragment extends Fragment implements OnMapReadyCallback {
    @Bind(R.id.dashboardBtn)    TextView  dashboardBtn;
    @Bind(R.id.marketplaceBtn)  TextView  marketplaceBtn;
    @Bind(R.id.reportBtn)       TextView  reportBtn;
    @Bind(R.id.notificationBtn) TextView  notificationBtn;
    private static              GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        ButterKnife.bind(this, view);
        if (map == null) {
            ((MapFragment) getActivity().getFragmentManager()
                    .findFragmentById(R.id.mapView)).getMapAsync(this);
        }
        dashboardBtn.performClick();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null && getActivity()!=null) {
            android.app.Fragment mapFragment = getActivity().getFragmentManager().findFragmentById(R.id.mapView);
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
            map = null;
            mapFragment = null;
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //TODO runtime location permission!!!
    }

    private void selectButton(View view) {
        dashboardBtn.setSelected(false);
        marketplaceBtn.setSelected(false);
        reportBtn.setSelected(false);
        notificationBtn.setSelected(false);
        view.setSelected(true);
    }

    @OnClick(R.id.dashboardBtn)
    void dashboardBtnClick() {
        if (!dashboardBtn.isSelected()) {
            selectButton(dashboardBtn);
            //TODO
        }
    }

    @OnClick(R.id.marketplaceBtn)
    void marketPlaceBtnClick() {
        if (!marketplaceBtn.isSelected()) {
            selectButton(marketplaceBtn);
            //TODO
        }
    }

    @OnClick(R.id.reportBtn)
    void reportBtnClick() {
        if (!reportBtn.isSelected()) {
            selectButton(reportBtn);
            //TODO
        }
    }

    @OnClick(R.id.notificationBtn)
    void notificationBtnClick() {
        if (!notificationBtn.isSelected()) {
            selectButton(notificationBtn);
            //TODO
        }
    }

    @OnClick(R.id.directBtn)
    void onDirectBtnClick() {

    }

    @OnClick(R.id.addFriendBtn)
    void onAddFriendBtnClick() {

    }

    @OnClick(R.id.gpsBtn)
    void onGpsBtnClick() {

    }

    @OnClick(R.id.searchBtn)
    void onSearchBtnClick() {

    }

    @OnClick(R.id.menuBtn)
    void onMenuBtnClick() {

    }
}
