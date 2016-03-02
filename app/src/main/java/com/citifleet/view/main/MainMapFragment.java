package com.citifleet.view.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    @Bind(R.id.dashboardBtn)    TextView        dashboardBtn;
    @Bind(R.id.marketplaceBtn)  TextView        marketplaceBtn;
    @Bind(R.id.reportBtn)       TextView        reportBtn;
    @Bind(R.id.notificationBtn) TextView        notificationBtn;
    private static              GoogleMap       map;
    protected                   GoogleApiClient googleApiClient;
    protected                   LocationRequest locationRequest;
    protected                   Location        currentLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        ButterKnife.bind(this, view);
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("mapFragment");
            if (mapFragment == null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mapFragment = SupportMapFragment.newInstance();
                fragmentTransaction.replace(R.id.mapFragment, mapFragment, "mapFragment").commit();
            }
            mapFragment.getMapAsync(this);
        }
        dashboardBtn.performClick();
        buildGoogleApiClient();
        return view;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onDestroyView() {
        map = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

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
        if (currentLocation != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.ZOOM_LEVEL));
        }
    }

    @OnClick(R.id.searchBtn)
    void onSearchBtnClick() {

    }

    @OnClick(R.id.menuBtn)
    void onMenuBtnClick() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(MainMapFragment.class.getName(), "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }
}
