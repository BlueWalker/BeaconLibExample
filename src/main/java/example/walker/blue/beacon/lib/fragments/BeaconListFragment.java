package example.walker.blue.beacon.lib.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import example.walker.blue.beacon.lib.BeaconListAdapter;
import example.walker.blue.beacon.lib.MainActivity;
import example.walker.blue.beacon.lib.R;
import walker.blue.beacon.lib.beacon.Beacon;
import walker.blue.beacon.lib.beacon.BluetoothDeviceToBeacon;
import walker.blue.beacon.lib.client.BeaconClientBuilder;
import walker.blue.beacon.lib.client.BeaconScanClient;
import walker.blue.beacon.lib.service.ScanEndUserCallback;

/**
 * Fragment that displays a list of Beacons found
 */
public class BeaconListFragment extends ListFragment {

    /**
     * Log message format
     */
    private static final String BEACON_FOUND_LOG = "Name - %s\n\tRaw data (hex) - %s";

    /**
     * Holds the Beacons found
     */
    private List<Beacon> beacons;
    /**
     * The client that scans for Beacons
     */
    private BeaconScanClient scanClient;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        // Lazy initialize all these things since this method gets called
        // every time the fragment is displayed.
        if (this.beacons == null) {
            this.beacons = new ArrayList<Beacon>();
        }
        if (getListAdapter() == null) {
            final BeaconListAdapter adapter = new BeaconListAdapter(getActivity() , this.beacons);
            setListAdapter(adapter);
        }
        if (this.scanClient == null) {
            this.scanClient = new BeaconClientBuilder()
                    .setContext(getActivity())
                    .setLeScanCallback(leScanCallback)
                    .scanInterval(10000)
                    .setUserCallback(userCallback)
                    .build();
            startScanning();
        }
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        final BeaconFragment beaconFragment = new BeaconFragment();
        final Bundle beaconBundle = new Bundle();
        beaconBundle.putParcelable("beacon", (Beacon) getListAdapter().getItem(position));
        beaconFragment.setArguments(beaconBundle);
        ((MainActivity) getActivity()).changeFragment(beaconFragment);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final MenuItem startItem = menu.findItem(R.id.start_scan);
        final MenuItem endItem = menu.findItem(R.id.stop_scan);
        if (this.scanClient.isScanning()) {
            startItem.setVisible(false);
            endItem.setVisible(true);
        } else {
            startItem.setVisible(true);
            endItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.stop_scan:
                stopScanning();
                return true;
            case R.id.start_scan:
                startScanning();
                return true;
            case R.id.config_action:
                stopScanning();
                final ClientConfigFragment clientConfigFragment = new ClientConfigFragment();
                clientConfigFragment.setClient(this.scanClient);
                ((MainActivity) getActivity()).changeFragment(clientConfigFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanning();
    }

    /**
     * Signals the client to start scanning for Beacons and displays
     * the progressbar located in the toolbar
     */
    private void startScanning() {
        startScanning(false);
    }

    /**
     * Checks if bluetooth is enabled and starts scanning
     *
     * @param retry boolean
     */
    public void startScanning(final boolean retry) {
        if (this.scanClient.isBluetoothEnabled()) {
            final BeaconListAdapter listAdapter = (BeaconListAdapter) getListAdapter();
            if (!listAdapter.isEmpty()) {
                listAdapter.clear();
                listAdapter.notifyDataSetChanged();
            }
            ((MainActivity) getActivity()).setProgressbarVisibility(true);
            this.scanClient.startScanning();
            getActivity().invalidateOptionsMenu();
        } else if (!retry) {
            ((MainActivity) getActivity()).promptUserEnableBluetooth();
        }
    }

    /**
     * Signals the client to stop scanning for Beacons, hides the progressbar,
     * and displays the scan button in the toolbar
     */
    private void stopScanning() {
        if (this.scanClient.isScanning()) {
            this.scanClient.stopScanning();
        }
        ((MainActivity) getActivity()).setProgressbarVisibility(false);
        getActivity().invalidateOptionsMenu();
    }

    /**
     * Callback that defines what will happen once a Bluetooth device is found
     */
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BeaconListAdapter listAdapter = (BeaconListAdapter) getListAdapter();
            final Beacon beacon = BluetoothDeviceToBeacon.toBeacon(device, rssi, scanRecord);
            if (beacon == null) {
                return;
            }
            getActivity().runOnUiThread(new LeCallbackRunnable(beacon, listAdapter, rssi));
        }
    };

    /**
     * Callback Executed when the client finishes scanning
     */
    private ScanEndUserCallback userCallback = new ScanEndUserCallback() {
        @Override
        public void execute() {
            stopScanning();
        }
    };

    /**
     * Runnable that is responsible for adding the beacons to the adapter 
     */
    private class LeCallbackRunnable implements Runnable {

        private Beacon beacon;
        private ArrayAdapter<Beacon> listAdapter;
        private int rssi;

        public LeCallbackRunnable(final Beacon beacon, final ArrayAdapter<Beacon> listAdapter, final int rssi) {
            this.beacon = beacon;
            this.listAdapter = listAdapter;
            this.rssi = rssi;
        }

        @Override
        public void run() {
            final int beaconPosition = listAdapter.getPosition(beacon);
            if (beaconPosition < 0) {
                Log.d(this.getClass().getName(),
                        String.format(BEACON_FOUND_LOG, beacon.getName(),
                                BluetoothDeviceToBeacon.rawSignalToString(beacon.getRawData())));
                listAdapter.add(beacon);
            } else {
                listAdapter.getItem(beaconPosition).addMeasuredRSSI(rssi);
            }
        }
    }
}