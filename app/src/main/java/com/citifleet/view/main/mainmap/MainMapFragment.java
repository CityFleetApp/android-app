package com.citifleet.view.main.mainmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.FriendNearby;
import com.citifleet.model.Report;
import com.citifleet.model.ReportType;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.citifleet.view.main.addfriends.AddFriendsFragment;
import com.citifleet.view.main.dashboard.DashboardFragment;
import com.citifleet.view.main.marketplace.MarketPlaceFragment;
import com.citifleet.view.main.notifications.NotificationsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult>, MainMapPresenter.MainMapView {
    public static final int REQUEST_LOCATION_UPDATES = 111;
    public static final int REQUEST_CHECK_SETTINGS = 222;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @Bind(R.id.dashboardBtn)
    TextView dashboardBtn;
    @Bind(R.id.marketplaceBtn)
    TextView marketplaceBtn;
    @Bind(R.id.reportBtn)
    TextView reportBtn;
    @Bind(R.id.notificationBtn)
    TextView notificationBtn;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String defaultErrorMes;
    @Bind(R.id.nearReportDialog)
    LinearLayout nearReportDialog;
    private NearbyReportDialogView nearbyReportDialogView;
    private GoogleMap map;
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation;
    protected LocationSettingsRequest locationSettingsRequest;
    private MainMapPresenter presenter;
    private Place selectedPlace;
    private List<Report> nearbyReportsList = new ArrayList<Report>();
    private List<FriendNearby> nearbyFriendsList = new ArrayList<FriendNearby>();
    private List<Marker> nearbyReportMarkersList = new ArrayList<Marker>();
    private List<Marker> nearbyFriendsMarkerList = new ArrayList<Marker>();
    private boolean needToAnimateZoomToLocation = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new MainMapPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
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
        selectButton(dashboardBtn);
        buildGoogleApiClient();
        nearbyReportDialogView = new NearbyReportDialogView(this, nearReportDialog);
        return view;
    }

    protected Location getCurrentLocation() {
        return currentLocation;
    }

    protected Report getReportForMarker(Marker marker) {
        int reportId = Integer.valueOf(marker.getTitle());
        for (Report report : nearbyReportsList) {
            if (report.getId() == reportId) {
                return report;
            }
        }
        return null;
    }

    protected void onReportConfirmDenyClicked(int reportId, boolean isConfirmed) {
        presenter.confirmDenyReport(reportId, isConfirmed);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setSmallestDisplacement(Constants.MAP_DISPLACEMENT);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                map.setMyLocationEnabled(true);
            } else {
                //TODO
            }
        }
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
        if (googleApiClient != null) {
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);

            if (googleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }

            googleApiClient.disconnect();
        }

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
        nearbyReportDialogView.onDestroy();
        map = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Report report = getReportForMarker(marker);
                if (!nearbyReportDialogView.isVisible() || (nearbyReportDialogView.isVisible() && nearbyReportDialogView.getSelectedReport() != report)) {
                    nearbyReportDialogView.show(report, currentLocation);
                } else {
                    nearbyReportDialogView.hide();
                }
                return true;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (nearbyReportDialogView.isVisible()) {
                    nearbyReportDialogView.hide();
                }
            }
        });
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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
        }
        ((BaseActivity) getActivity()).changeFragmentWithAnimation(new DashboardFragment(), true, R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down);
    }

    @OnClick(R.id.marketplaceBtn)
    void marketPlaceBtnClick() {
        if (!marketplaceBtn.isSelected()) {
            selectButton(marketplaceBtn);
            ((BaseActivity) getActivity()).changeFragment(new MarketPlaceFragment(), true);
        }
    }

    @OnClick(R.id.reportBtn)
    void reportBtnClick() {
        if (!reportBtn.isSelected()) {
            selectButton(reportBtn);
            FragmentManager fm = getChildFragmentManager();
            ReportDialogFragment fragment = (ReportDialogFragment) fm.findFragmentByTag(Constants.REPORT_DIALOG_TAG);
            if (fragment == null) {
                fragment = new ReportDialogFragment();
            }
            fragment.setTargetFragment(this, Constants.REPORT_TARGET_FRAGMENT);
            fragment.show(getChildFragmentManager(), Constants.REPORT_DIALOG_TAG);
        }
    }


    @OnClick(R.id.notificationBtn)
    void notificationBtnClick() {
        if (!notificationBtn.isSelected()) {
            selectButton(notificationBtn);
            ((BaseActivity) getActivity()).changeFragment(new NotificationsFragment(), true);
        }
    }

    @OnClick(R.id.directBtn)
    void onDirectBtnClick() {
        if (selectedPlace != null && currentLocation != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("http://maps.google.com/maps?saddr=").append(currentLocation.getLatitude()).append(",").append(currentLocation.getLongitude()).
                    append("&daddr=").append(selectedPlace.getLatLng().latitude).append(",").append(selectedPlace.getLatLng().longitude);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(sb.toString()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.addFriendBtn)
    void onAddFriendBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new AddFriendsFragment(), true);
    }

    @OnClick(R.id.gpsBtn)
    void onGpsBtnClick() {
        if (currentLocation != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.ZOOM_LEVEL));
        }
    }

    @OnClick(R.id.searchBtn)
    void onSearchBtnClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(MainMapFragment.class.getName(), e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(MainMapFragment.class.getName(), e.getMessage());
        }
    }

    @OnClick(R.id.menuBtn)
    void onMenuBtnClick() {
        ((BaseActivity) getActivity()).changeFragmentWithAnimation(new DashboardFragment(), true, R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down);
    }

    public void onReportDialogClosed() {
        selectButton(dashboardBtn);
    }

    public void onReportItemClick(int position) {
        double lat = currentLocation == null ? 0 : currentLocation.getLatitude();
        double longt = currentLocation == null ? 0 : currentLocation.getLongitude();
        presenter.sendReport(position + 1, lat, longt);
        selectButton(dashboardBtn);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                map.setMyLocationEnabled(true);
            }
        }
        checkLocationSettings();
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
        if (needToAnimateZoomToLocation) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.ZOOM_LEVEL));
            needToAnimateZoomToLocation = false;
        }
        presenter.loadReportsNearby(currentLocation.getLatitude(), currentLocation.getLongitude());
        presenter.loadFriendsNearby(currentLocation.getLatitude(), currentLocation.getLongitude());
        Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        selectedPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(selectedPlace.getLatLng())
                                .title(selectedPlace.getName().toString()));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace.getLatLng(), Constants.ZOOM_LEVEL));
                        break;
                    case PlaceAutocomplete.RESULT_ERROR:
                        Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                        Log.d(MainMapFragment.class.getName(), status.getStatusMessage());
                        break;
                }
                break;
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.d(MainMapFragment.class.getName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(MainMapFragment.class.getName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPostReportSuccess() {

    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReportsNearbyLoaded(List<Report> reportList) {
        nearbyReportsList.clear(); //TODO check difference
        nearbyReportsList.addAll(reportList);
        for (Marker marker : nearbyReportMarkersList) {
            marker.remove();
        }
        for (Report report : nearbyReportsList) {
            int iconResId = ReportType.values()[report.getReportType() - 1].getPinIconResId();
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(report.getLat(), report.getLng()))
                    .icon(BitmapDescriptorFactory.fromResource(iconResId))
                    .title(String.valueOf(report.getId())));
            nearbyReportMarkersList.add(marker);
        }
    }

    @Override
    public void onFriendsNearbyLoaded(List<FriendNearby> friendList) {
        nearbyFriendsList.clear(); //TODO check difference
        nearbyFriendsList.addAll(friendList);
        for (Marker marker : nearbyFriendsMarkerList) {
            marker.remove();
        }
        for (FriendNearby friendNearby : nearbyFriendsList) {
            // int iconResId = ReportType.values()[report.getReportType() - 1].getPinIconResId();
            View view = getActivity().getLayoutInflater().inflate(R.layout.friend_marker, null);
            TextView name = (TextView) view.findViewById(R.id.markerFriendName);
            name.setText(friendNearby.getFullName());
            view.setDrawingCacheEnabled(true);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(friendNearby.getLat(), friendNearby.getLng()))
                    .icon(BitmapDescriptorFactory.fromBitmap(b))
                    .title(String.valueOf(friendNearby.getId())));
            nearbyFriendsMarkerList.add(marker);
        }
    }
}
