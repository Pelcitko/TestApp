package cz.tul.lp.testapp.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.tul.lp.testapp.Notes;
import cz.tul.lp.testapp.R;

/**
 * Created by LP
 */

public class SingleNoteFragment extends Fragment {

    private long id;

    public SingleNoteFragment(long id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.single_note, container, false);

        TextView title = (TextView) root.findViewById(R.id.title);
        TextView text = (TextView) root.findViewById(R.id.text);

        Notes notes = new Notes(getActivity());
        Cursor note = notes.getNote(id);

        int titleIndex = note.getColumnIndex(Notes.COLUMN_TITLE);
        int textIndex = note.getColumnIndex(Notes.COLUMN_NOTE);

        if (note.getCount() < 1) {
            title.setText(R.string.error);
            title.setError("");
        } else {
            note.moveToNext();
            title.setText(note.getString(titleIndex));
            text.setText(note.getString(textIndex));
        }

        note.close();
        notes.close();
        return root;
    }
}
