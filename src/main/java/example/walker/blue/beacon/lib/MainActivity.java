package example.walker.blue.beacon.lib;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import example.walker.blue.beacon.lib.fragments.BeaconListFragment;

/**
 * Main activity of the application. 
 */
public class MainActivity extends ActionBarActivity {

    /**
     * Value used to request the dialog which prompts the user to
     * turn bluetooth on
     */
    private static final int PROMPT_ENABLE_BT = 1;

    /**
     * Progress bar located on the ActionBar
     */
    private ProgressBar progressBar;
    /**
     * Instance of BeaconListFragment being used
     */
    private BeaconListFragment beaconListFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        this.beaconListFragment = new BeaconListFragment();
        changeFragment(beaconListFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROMPT_ENABLE_BT && resultCode == RESULT_OK) {
            this.beaconListFragment.startScanning(true);
        }
    }
    @Override
    public void onBackPressed() {
        final Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment != null && !(currentFragment instanceof BeaconListFragment)) {
            goBackToMainList();
        } else {
            this.moveTaskToBack(true);
        }
    }

    /**
     * Changes the fragment being displayed in the content_frame and pushes
     * that transaction into the back stack. 
     *
     * @param fragment Fragment
     */
    public void changeFragment(final Fragment fragment) {
        if (fragment != null) {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Sets the visibility of the progressbar located in the actionbar
     *
     * @param visibility boolean (true -> Visible false -> invisible)
     */
    public void setProgressbarVisibility(final boolean visibility) {
        this.progressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Calls the actiivty to prompt the user to enable bluetooth
     */
    public void promptUserEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, PROMPT_ENABLE_BT);
    }

    /**
     * Checks if there are entries in the BackStack. If there are entries,
     * the most recent one is popped. If there aren't any entries, nothing happens. 
     */
    private void goBackToMainList() {
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }
}
