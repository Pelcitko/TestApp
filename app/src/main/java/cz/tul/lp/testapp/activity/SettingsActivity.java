package cz.tul.lp.testapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import cz.tul.lp.testapp.R;

/**
 * Created by LP on 20.03.2017.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
