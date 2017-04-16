package cz.tul.lp.testapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import cz.tul.lp.testapp.R;
import cz.tul.lp.testapp.fragment.AddNoteFragment;

/**
 * Created by LP
 */

public class AddNoteActivity extends FragmentActivity implements AddNoteFragment.OnAddNoteListener{
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_container);
    }

    public void onAddNote(String title, String text) {
        Intent result = new Intent();
        result.putExtra(EXTRA_TITLE, title);
        result.putExtra(EXTRA_TEXT, text);

        setResult(RESULT_OK, result);
        finish();
    }
}
