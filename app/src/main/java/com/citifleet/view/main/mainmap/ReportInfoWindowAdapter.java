package com.citifleet.view.main.mainmap;

import android.location.Location;
import android.view.View;
import android.widget.Button;
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
    public View getInfoContents(Marker marker) {
        Report report = fragment.getReportForMarker(marker);
        View view = fragment.getActivity().getLayoutInflater().inflate(R.layout.near_report_dialog, null);
        TextView reportTitle = (TextView) view.findViewById(R.id.titleMiles);
        Button stillHere = (Button) view.findViewById(R.id.stillHereBtn);
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
        stillHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        notHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }

    private static float convertMetersToMiles(float meters) {
        return (meters / Constants.METERS_IN_MILE);
    }
}
