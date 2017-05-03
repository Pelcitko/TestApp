package cz.tul.lp.testapp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import cz.tul.lp.testapp.R;

/**
 * Created by LP
 */

public class SettingsActivity extends PreferenceActivity {

    private SharedPreferences settings = null;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
    }


    public void setPressureEnable(boolean pressureEnable) {
        editor.putBoolean("PRESSURE_ENABLE", pressureEnable);

        // Commit the edits!
        editor.commit();
    }

}
