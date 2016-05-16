package com.cityfleet.view.main.mainmap;

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

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.FriendNearby;
import com.cityfleet.model.Report;
import com.cityfleet.model.ReportType;
import com.cityfleet.util.Constants;
import com.cityfleet.util.NewReportAddedEvent;
import com.cityfleet.util.ReportDeletedEvent;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.addfriends.AddFriendsFragment;
import com.cityfleet.view.main.chat.ChatActivity;
import com.cityfleet.view.main.dashboard.DashboardFragment;
import com.cityfleet.view.main.marketplace.MarketPlaceFragment;
import com.cityfleet.view.main.notifications.NotificationsFragment;
import com.cityfleet.view.main.profile.ProfileFragment;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult>, MainMapPresenter.MainMapView {
    public static final int REQUEST_LOCATION_UPDATES = 111;
    public static final int REQUEST_CHECK_SETTINGS = 222;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 333;
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
    @Bind(R.id.nearFriendDialog)
    LinearLayout nearFriendDialog;
    private NearbyReportDialogView nearbyReportDialogView;
    private NearbyFriendMapDialogView nearbyFriendMapDialogView;
    private GoogleMap map;
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation;
    private Location selectedForReportPosition;
    protected LocationSettingsRequest locationSettingsRequest;
    private MainMapPresenter presenter;
    private Place selectedPlace;
    private Set<Report> nearbyReportsList = new HashSet<Report>();
    private Set<FriendNearby> nearbyFriendsList = new HashSet<FriendNearby>();
    private List<Marker> nearbyReportMarkersList = new ArrayList<Marker>();
    private List<Marker> nearbyFriendsMarkerList = new ArrayList<Marker>();
    private boolean needToAnimateZoomToLocation = true;
    private Marker selectedFriendMarker = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_map_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new MainMapPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
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
        nearbyFriendMapDialogView = new NearbyFriendMapDialogView(this, nearFriendDialog);

        return view;
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(NewReportAddedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        boolean needToAddReport = true;
        for (Report report : nearbyReportsList) {
            if (report.equals(event.getReport())) {
                needToAddReport = false;
            }
        }
        if (needToAddReport) {
            addReportMarkerToMap(event.getReport());
            nearbyReportsList.add(event.getReport());
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ReportDeletedEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        boolean isRemoved = nearbyReportsList.remove(event.getReport());
        if (isRemoved) {
            for (Marker marker : nearbyReportMarkersList) {
                if (marker.getSnippet().equals(event.getReport().getId())) {
                    marker.remove();
                    break;
                }
            }
        }
    }


    private void addReportMarkerToMap(Report report) {
        int iconResId = ReportType.values()[report.getReportType() - 1].getPinIconResId();
        LatLng markerLocation = new LatLng(report.getLat(), report.getLng());
        if (checkIfMarkerOnLocationExists(markerLocation)) {
            markerLocation = getRandomCoordinate(markerLocation);
        }
        Marker marker = map.addMarker(new MarkerOptions()
                .position(markerLocation)
                .icon(BitmapDescriptorFactory.fromResource(iconResId))
                .title(String.valueOf(report.getId()))
                .snippet("report"));
        nearbyReportMarkersList.add(marker);
    }

    private boolean checkIfMarkerOnLocationExists(LatLng markerLocation) {
        boolean locationExists = false;
        for (Marker m : nearbyReportMarkersList) {
            if (m.getPosition().equals(markerLocation)) {
                locationExists = true;
                break;
            }
        }
        if (!locationExists) {
            for (Marker m : nearbyFriendsMarkerList) {
                if (m.getPosition().equals(markerLocation)) {
                    locationExists = true;
                    break;
                }
            }
        }
        return locationExists;
    }

    private LatLng getRandomCoordinate(LatLng latLng) {
        Random random = new Random();
        // Convert radius from meters to degrees
        double radiusInDegrees = Constants.RANDOM_RADIUS_FOR_MARKER / Constants.METERS_IN_DEGREE;
        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);
        // Adjust the x-coordinate for the shrinking of the east-west distances
        double newX = x / Math.cos(latLng.latitude);
        double foundLongitude = newX + latLng.longitude;
        double foundLatitude = y + latLng.latitude;
        return new LatLng(foundLatitude, foundLongitude);
    }

    private Report getReportForMarker(Marker marker) {
        int reportId = Integer.valueOf(marker.getTitle());
        for (Report report : nearbyReportsList) {
            if (report.getId() == reportId) {
                return report;
            }
        }
        return null;
    }

    private FriendNearby getFriendForMarker(Marker marker) {
        int friendId = Integer.valueOf(marker.getTitle());
        for (FriendNearby friendNearby : nearbyFriendsList) {
            if (friendNearby.getId() == friendId) {
                return friendNearby;
            }
        }
        return null;
    }

    protected void onReportConfirmDenyClicked(int reportId, boolean isConfirmed) {
        presenter.confirmDenyReport(reportId, isConfirmed);
    }

    protected void onChatFriend(int friendId) {
        presenter.createChatRoomWithFriend(friendId);
    }

    protected void onProfileFriendOpen(int friendId) {
        unselectFriendMarker();
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.FRIEND_PROFILE_EXTRA, true);
        args.putInt(Constants.FRIEND_ID_EXTRA, friendId);
        profileFragment.setArguments(args);
        ((BaseActivity) getActivity()).changeFragment(profileFragment, true);
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
        //  locationRequest.setSmallestDisplacement(Constants.MAP_DISPLACEMENT);
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        nearbyReportDialogView.onDestroy();
        nearbyFriendMapDialogView.onDestroy();
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
        map.setOnMarkerClickListener(markerClickListener);
        map.setOnMapClickListener(mapClickListener);
        map.setOnMapLongClickListener(mapLongClickListener);
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
            showReportDialog();
        }
    }

    private void showReportDialog() {
        FragmentManager fm = getChildFragmentManager();
        ReportDialogFragment fragment = (ReportDialogFragment) fm.findFragmentByTag(Constants.REPORT_DIALOG_TAG);
        if (fragment == null) {
            fragment = new ReportDialogFragment();
        }
        fragment.setTargetFragment(this, Constants.REPORT_TARGET_FRAGMENT);
        fragment.show(getChildFragmentManager(), Constants.REPORT_DIALOG_TAG);
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
        double lat = 0, longt = 0;
        if (selectedForReportPosition != null) {
            lat = selectedForReportPosition.getLatitude();
            longt = selectedForReportPosition.getLongitude();
            selectedForReportPosition = null;
        } else {
            lat = currentLocation.getLatitude();
            longt = currentLocation.getLongitude();
        }
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
        //Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    startLocationUpdates();
                }
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(selectedPlace.getLatLng())
                            .title(selectedPlace.getName().toString()));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace.getLatLng(), Constants.ZOOM_LEVEL));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    Log.d(MainMapFragment.class.getName(), status.getStatusMessage());
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
        //delete markers
        Iterator<Report> i = nearbyReportsList.iterator();
        while (i.hasNext()) {
            Report report = i.next();
            if (!reportList.contains(report)) {
                i.remove();
                deleteMarkerWithReportId(report.getId());
            }
        }

        //add new markers
        for (Report report : reportList) {
            boolean isAdded = nearbyReportsList.add(report);
            if (isAdded) {
                addReportMarkerToMap(report);
            }
        }
    }

    private void deleteMarkerWithReportId(int reportIdToDelete) {
        Iterator<Marker> i = nearbyReportMarkersList.iterator();
        while (i.hasNext()) {
            Marker marker = i.next();
            int reportId = Integer.valueOf(marker.getTitle());
            if (reportId == reportIdToDelete) {
                marker.remove();
                i.remove();
            }
        }
    }

    private void deleteMarkerWithFriendId(int friendIdToDelete) {
        Iterator<Marker> i = nearbyFriendsMarkerList.iterator();
        while (i.hasNext()) {
            Marker marker = i.next();
            int reportId = Integer.valueOf(marker.getTitle());
            if (reportId == friendIdToDelete) {
                marker.remove();
                i.remove();
            }
        }
    }

    private Bitmap getImageForFriendSelected(String friendName) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.friend_marker, null);
        TextView name = (TextView) view.findViewById(R.id.markerFriendName);
        name.setText(friendName);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        if (view != null && view.getDrawingCache() != null) {
            Bitmap drawingCache = view.getDrawingCache();
            Bitmap b = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
            drawingCache.recycle();
            return b;
        }
        return null;
    }

    private void addFriendMarkerToMap(FriendNearby friendNearby) {
        if (map != null) {
            LatLng markerLocation = new LatLng(friendNearby.getLat(), friendNearby.getLng());
            if (checkIfMarkerOnLocationExists(markerLocation)) {
                markerLocation = getRandomCoordinate(markerLocation);
            }
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(markerLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.friend_marker))
                    .title(String.valueOf(friendNearby.getId()))
                    .anchor(0.5f, 1f)
                    .snippet("friend"));
            nearbyFriendsMarkerList.add(marker);
        }
    }

    @Override
    public void onFriendsNearbyLoaded(List<FriendNearby> friendList) {
        //delete markers
        Iterator<FriendNearby> i = nearbyFriendsList.iterator();
        while (i.hasNext()) {
            FriendNearby friendNearby = i.next();
            if (!friendList.contains(friendNearby)) {
                i.remove();
                deleteMarkerWithFriendId(friendNearby.getId());
            }
        }

        //add new markers
        for (FriendNearby friendNearby : friendList) {
            boolean isAdded = nearbyFriendsList.add(friendNearby);
            if (isAdded) {
                addFriendMarkerToMap(friendNearby);
            }
        }
    }

    @Override
    public void onChatRoomCreated(int roomId) {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        i.putExtra(Constants.CHAT_ID_TAG, roomId);
        getActivity().startActivity(i);
    }


    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String type = marker.getSnippet();
            if (type != null && type.equals("report")) {
                if (nearbyFriendMapDialogView.isVisible()) {
                    unselectFriendMarker();
                }
                Report report = getReportForMarker(marker);
                if (!nearbyReportDialogView.isVisible() || (nearbyReportDialogView.isVisible() && !nearbyReportDialogView.getSelectedReport().equals(report))) {
                    nearbyReportDialogView.show(report, currentLocation);
                } else {
                    nearbyReportDialogView.hide();
                }
                return true;
            } else if (type != null && type.equals("friend")) {
                if (nearbyReportDialogView.isVisible()) {
                    nearbyReportDialogView.hide();
                }
                if (selectedFriendMarker != null) {
                    selectedFriendMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.friend_marker));
                    selectedFriendMarker.setAnchor(0.5f, 1);
                    selectedFriendMarker = null;
                }
                FriendNearby friendNearby = getFriendForMarker(marker);
                Bitmap b = getImageForFriendSelected(friendNearby.getFullName());
                if (b != null) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(b));
                    marker.setAnchor(0.5f, 1f);
                }
                selectedFriendMarker = marker;
                if (!nearbyFriendMapDialogView.isVisible() || (nearbyFriendMapDialogView.isVisible() && !nearbyFriendMapDialogView.getSelectedFriend().equals(friendNearby))) {
                    nearbyFriendMapDialogView.show(friendNearby);
                } else {
                    unselectFriendMarker();
                }
                return true;
            }
            return false;
        }
    };

    private void unselectFriendMarker() {
        nearbyFriendMapDialogView.hide();
        if (selectedFriendMarker != null) {
            selectedFriendMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.friend_marker));
            selectedFriendMarker.setAnchor(0.5f, 1f);
            selectedFriendMarker = null;
        }

    }

    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (nearbyReportDialogView.isVisible()) {
                nearbyReportDialogView.hide();
            }
            if (nearbyFriendMapDialogView.isVisible()) {
                unselectFriendMarker();
            }
        }
    };

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            selectedForReportPosition = new Location("");
            selectedForReportPosition.setLatitude(latLng.latitude);
            selectedForReportPosition.setLongitude(latLng.longitude);
            showReportDialog();
        }
    };
}
