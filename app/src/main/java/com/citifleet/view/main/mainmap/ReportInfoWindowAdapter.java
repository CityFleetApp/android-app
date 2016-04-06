package com.citifleet.view.main.mainmap;

import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.Report;
import com.citifleet.model.ReportType;
import com.citifleet.util.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by vika on 06.04.16.
 */
public class ReportInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private MainMapFragment fragment;

    public ReportInfoWindowAdapter(MainMapFragment fragment) {
        this.fragment = fragment;

    }

    @Override
    public View getInfoContents(final Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        final Report report = fragment.getReportForMarker(marker);
        View view = fragment.getActivity().getLayoutInflater().inflate(R.layout.near_report_dialog, null);
        // We must call this to set the current marker and infoWindow references
        // to the MapWrapperLayout
        // fragment.mapWrapperLayout.setMarkerWithInfoWindow(marker, view);
        TextView reportTitle = (TextView) view.findViewById(R.id.titleMiles);
        final Button stillHere = (Button) view.findViewById(R.id.stillHereBtn);
        Button notHereBtn = (Button) view.findViewById(R.id.notHereBtn);
        int iconResId = ReportType.values()[report.getReportType() - 1].getSmallIconResId();
        reportTitle.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        Location currentLocation = fragment.getCurrentLocation();
        Location reportLocation = new Location("");
        reportLocation.setLatitude(report.getLat());
        reportLocation.setLongitude(report.getLng());
        float distanceInMeters = currentLocation.distanceTo(reportLocation);
        float distanceInMiles = convertMetersToMiles(distanceInMeters);
        reportTitle.setText(fragment.getString(R.string.in_miles, String.format("%.1f", distanceInMiles)));
        final LinearLayout buttonsLayout = (LinearLayout) view.findViewById(R.id.buttons);
        buttonsLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "still here position: x:" + buttonsLayout.getLeft() + " till " + buttonsLayout.getWidth() + " y: " + buttonsLayout.getTop() + " till " + buttonsLayout.getHeight());
            }
        });
        stillHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onReportConfirmDenyClicked(report.getId(), true);
                marker.hideInfoWindow();
            }
        });
        notHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onReportConfirmDenyClicked(report.getId(), false);
                marker.hideInfoWindow();
            }
        });

        return view;
    }

    private static float convertMetersToMiles(float meters) {
        return (meters / Constants.METERS_IN_MILE);
    }
}
