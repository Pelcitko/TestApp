package cz.tul.lp.testapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import cz.tul.lp.testapp.R;
import cz.tul.lp.testapp.fragment.SingleNoteFragment;

/**
 * Created by LP
 */

public class SingleNoteActivity  extends FragmentActivity {
    public static final String EXTRA_ID = "id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_note_container);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);

        Fragment f = new SingleNoteFragment(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.commit();
    }
}
