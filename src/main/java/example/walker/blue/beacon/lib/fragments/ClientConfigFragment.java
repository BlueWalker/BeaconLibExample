package example.walker.blue.beacon.lib.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import example.walker.blue.beacon.lib.R;
import walker.blue.beacon.lib.client.BeaconScanClient;

/**
 * Fragment that displays configuration of the Client
 */
public class ClientConfigFragment extends Fragment {

    /**
     * Client being configured
     */
    private BeaconScanClient client;
    /**
     * EditText used to enter the desired value for the update interval
     */
    private EditText intervalEditText;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final ScrollView mainScroll = (ScrollView) inflater.inflate(R.layout.client_config, null);
        if (this.client != null) {
            intervalEditText = (EditText) mainScroll.findViewById(R.id.scan_interval_edit);
            intervalEditText.setText(String.valueOf(this.client.getScanInterval()));
            final Button saveButton = (Button) mainScroll.findViewById(R.id.save_button);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAndExit();
                }
            });
        }
        return mainScroll;
    }

    /**
     * Setter for the client being configured by this instance of the menu
     *
     * @param client BeaconScanClient
     */
    public void setClient(final BeaconScanClient client) {
        this.client = client;
    }

    /**
     * Saves the configuration and exits the menu
     */
    private void saveAndExit() {
        final String scanVal = this.intervalEditText.getText().toString();
        if (isNum(scanVal)) {
            this.client.setScanningInterval(Integer.valueOf(scanVal));
        }
        getActivity().onBackPressed();
    }

    /**
     * Checks if the given string is a number
     *
     * @param str String
     * @return boolean
     */
    private boolean isNum(final String str) {
        try {
            Integer.valueOf(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}