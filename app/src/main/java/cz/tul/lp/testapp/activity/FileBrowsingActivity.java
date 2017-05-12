package cz.tul.lp.testapp.activity;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.improvelectronics.sync.android.SyncFtpListener;
import com.improvelectronics.sync.android.SyncFtpService;
import com.improvelectronics.sync.android.SyncStreamingService;
import com.improvelectronics.sync.obex.OBEXFtpFolderListingItem;

import java.util.ArrayList;
import java.util.List;

import cz.tul.lp.testapp.FileBrowserAdapter;
import cz.tul.lp.testapp.R;

/**
 * Created by LP
 */

public class FileBrowsingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browsing);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment implements SyncFtpListener {

        private SyncFtpService mFtpService;
        private boolean mFtpServiceBound, mConnectedToFtp;
        private ProgressBar mProgressBar;
        private FileBrowserAdapter mFileBrowsingAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Bind to the ftp service.
            Intent intent = new Intent(getActivity(), SyncFtpService.class);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            // Set up the list view and adapter.
            mFileBrowsingAdapter = new FileBrowserAdapter(getActivity(), new ArrayList<OBEXFtpFolderListingItem>());
            setListAdapter(mFileBrowsingAdapter);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_file_browsing, container, false);
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            mProgressBar = (ProgressBar) getListView().getEmptyView().findViewById(R.id.progressBar);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mFtpServiceBound) {
                // Be sure to send a disconnect if the server was still connected.
                if(mConnectedToFtp) mFtpService.disconnect();

                // Don't forget to remove the listener and unbind from the service.
                mFtpService.removeListener(this);
                getActivity().unbindService(mConnection);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            OBEXFtpFolderListingItem bluetoothFtpFolderListingItem = (OBEXFtpFolderListingItem) mFileBrowsingAdapter.getItem(position);
            if (bluetoothFtpFolderListingItem.getSize() == 0) { // This is a folder.
                // Change the folder for the user.
                mFtpService.changeFolder(bluetoothFtpFolderListingItem.getName());
            }
        }

        @Override
        public void onFtpDeviceStateChange(int oldState, int newState) {
            if (newState == SyncFtpService.STATE_CONNECTED) {
                // Connect to the ftp server.
                mFtpService.connect();
            }
        }

        @Override
        public void onConnectComplete(int result) {
            if(result == SyncFtpService.RESULT_OK) {
                mConnectedToFtp = true;
                mFtpService.changeFolder("");
            } else {
                Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDisconnectComplete(int result) {
            mConnectedToFtp = false;
        }

        @Override
        public void onFolderListingComplete(List<OBEXFtpFolderListingItem> items, int result) {
            mProgressBar.setVisibility(View.GONE);

            if (result == SyncFtpService.RESULT_OK) {
                mFileBrowsingAdapter.setFolderListingItems(items);
            } else {
                Toast.makeText(getActivity(), "Failed to retrieve folder listing", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChangeFolderComplete(Uri uri, int result) {
            // Remove all the current items and set new message.
            mFileBrowsingAdapter.setFolderListingItems(new ArrayList<OBEXFtpFolderListingItem>());
            mProgressBar.setVisibility(View.VISIBLE);

            // Get the contents of the folder.
            mFtpService.listFolder();
        }

        @Override
        public void onDeleteComplete(OBEXFtpFolderListingItem file, int result) {

        }

        @Override
        public void onGetFileComplete(OBEXFtpFolderListingItem file, int result) {

        }

        private final ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                // Set up the service
                mFtpServiceBound = true;
                SyncFtpService.SyncFtpBinder binder = (SyncFtpService.SyncFtpBinder) service;
                mFtpService = binder.getService();
                mFtpService.addListener(PlaceholderFragment.this);// Add listener to retrieve events from ftp service.

                if(mFtpService.getState() == SyncStreamingService.STATE_CONNECTED) {
                    // Connect to the ftp server.
                    mFtpService.connect();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                mFtpService = null;
                mFtpServiceBound = false;
            }
        };
    }
}
