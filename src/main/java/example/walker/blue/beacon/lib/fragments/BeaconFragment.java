package example.walker.blue.beacon.lib.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import example.walker.blue.beacon.lib.R;
import walker.blue.beacon.lib.beacon.Beacon;


/**
 * Fragment that displays the information of the given beacon
 */
public class BeaconFragment extends Fragment {

    /**
     * Bundle Key for the Beacon
     */
    private static final String BEACON_KEY = "beacon";

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater,
            final ViewGroup container, final Bundle savedInstanceState) {
        // Load all the views from the layout
        final ScrollView mainScroll = (ScrollView) inflater.inflate(R.layout.beacon_layout, null);
        final TextView beaconNameTextView = (TextView) mainScroll.findViewById(R.id.beacon_name_value);
        final TextView beaconAddressTextView = (TextView) mainScroll.findViewById(R.id.beacon_address_value);
        final TextView beaconUUIDTextView = (TextView) mainScroll.findViewById(R.id.beacon_uuid_value);
        final TextView beaconMajorTextView = (TextView) mainScroll.findViewById(R.id.beacon_major_value);
        final TextView beaconMinorTextView = (TextView) mainScroll.findViewById(R.id.beacon_minor_value);
        final TextView beaconCalibrationRSSITextView = (TextView) mainScroll.findViewById(R.id.beacon_calibration_value);
        final TextView beaconMeasuredRSSITextView = (TextView) mainScroll.findViewById(R.id.beacon_measured_value);
        
        // Get the beacon passed in through the bundle
        final Beacon beacon = getArguments().getParcelable(BEACON_KEY);

        beaconNameTextView.setText(beacon.getName() != null ? beacon.getName() : "null");
        beaconAddressTextView.setText(beacon.getAddress() != null ? beacon.getAddress() : "null");
        beaconUUIDTextView.setText(beacon.getUUID());
        beaconMajorTextView.setText(String.valueOf(beacon.getMajor()));
        beaconMinorTextView.setText(String.valueOf(beacon.getMinor()));
        beaconCalibrationRSSITextView.setText(String.valueOf(beacon.getRSSI()));
        beaconMeasuredRSSITextView.setText(valuesToString(beacon.getMeasuredRSSIValues()));
        return mainScroll;
    }

    private String valuesToString(final List<Integer> values) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            stringBuilder.append(values.get(i));
            if (i < values.size() - 1) {
                stringBuilder.append('\n');
            }
        }
        return stringBuilder.toString();
    }
}