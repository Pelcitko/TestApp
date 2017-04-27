package cz.tul.lp.testapp.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.improvelectronics.sync.android.SyncCaptureReport;
import com.improvelectronics.sync.android.SyncPath;
import com.improvelectronics.sync.android.SyncStreamingListener;
import com.improvelectronics.sync.android.SyncStreamingService;

import java.util.List;

import cz.tul.lp.testapp.CanvasView;
import cz.tul.lp.testapp.R;

/**
 * Created by LP on 13.02.2017.
 * A placeholder fragment containing a simple view.
 */
public class DrawFragment extends Fragment implements SyncStreamingListener{

        private SyncStreamingService mStreamingService = null;;
        private boolean mStreamingServiceBound = false;
        private CanvasView mCanvasView = null;

    public DrawFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind to the ftp service.
        Intent intent = new Intent(getActivity(), SyncStreamingService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mCanvasView = (CanvasView)getView().findViewById(R.id.canvas);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStreamingServiceBound) {
            // Put the Boogie Board Sync back into MODE_NONE.
            // This way it doesn't use Bluetooth and saves battery life. <= !!!
            if(mStreamingService.getState() == SyncStreamingService.STATE_CONNECTED) mStreamingService.setSyncMode(SyncStreamingService.MODE_NONE);

            // Don't forget to remove the listener and unbind from the service.
            mStreamingService.removeListener(this);
            getActivity().unbindService(mConnection);
        }
    }

    /**
     * Called when the state of the streaming connection has changed.
     *
     * @param prevState old state of streaming connection
     * @param newState  new state of streaming connection
     */
    @Override
    public void onStreamingStateChange(int prevState, int newState) {
        // Put the streaming service in capture mode to get data from Boogie Board Sync.
        if(newState == SyncStreamingService.STATE_CONNECTED) {
            mStreamingService.setSyncMode(SyncStreamingService.MODE_CAPTURE);
        }
    }

    /**
     * Called when the Boogie Board Sync was erased from the device.
     */
    @Override
    public void onErase() {
        mCanvasView.clear();
        Toast.makeText(getActivity(), "Erase button pushed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the Boogie Board Sync saved a file.
     */
    @Override
    public void onSave() {
        Toast.makeText(getActivity(), "Save button pushed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when paths were drawn to the Boogie Board Sync.
     *
     * @param paths
     */
    @Override
    public void onDrawnPaths(List<SyncPath> paths) {
        this.mCanvasView.addPath(paths.get(paths.size()-1));
    }

    /**
     * Called when the Boogie Board Sync returned a {@link SyncCaptureReport #SyncCaptureReport}.
     *
     * @param captureReport
     */
    @Override
    public void onCaptureReport(SyncCaptureReport captureReport) {

    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Set up the service
            mStreamingServiceBound = true;
            SyncStreamingService.SyncStreamingBinder binder = (SyncStreamingService.SyncStreamingBinder) service;
            mStreamingService = binder.getService();
            mStreamingService.addListener(DrawFragment.this);// Add listener to retrieve events from streaming service.

            // Put the streaming service in capture mode to get data from Boogie Board Sync.
            if(mStreamingService.getState() == SyncStreamingService.STATE_CONNECTED) {
                mStreamingService.setSyncMode(SyncStreamingService.MODE_CAPTURE);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mStreamingService = null;
            mStreamingServiceBound = false;
        }
    };
}
