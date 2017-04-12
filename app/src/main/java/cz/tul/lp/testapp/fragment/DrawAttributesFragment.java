package cz.tul.lp.testapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.UUID;

import cz.tul.lp.testapp.R;

/**
 * Created by LP on 02.04.2017.
 */

public class DrawAttributesFragment extends Fragment {

    private View mSeekFragment = null;
    private View mColorFragment = null;
    public DrawAttributesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_draw:
                mColorFragment.setVisibility(View.VISIBLE);
                mSeekFragment.setVisibility(View.INVISIBLE);
                return true;

            case R.id.navigation_color:
                mColorFragment.setVisibility(View.INVISIBLE);
                mSeekFragment.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}