package cz.tul.lp.testapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LP on 13.02.2017.
 * A placeholder fragment containing a simple view.
 */
public class DrawFragment extends Fragment {

    public DrawFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }
}
