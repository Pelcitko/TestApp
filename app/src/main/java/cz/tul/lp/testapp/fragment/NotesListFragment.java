package cz.tul.lp.testapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import cz.tul.lp.testapp.Notes;
import cz.tul.lp.testapp.R;


public class NotesListFragment extends ListFragment {
    private static final int MENU_DELETE_ID = 0;

    private OnNoteClickedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_main, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNoteClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNoteClickedListener");
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
        updateList();
    }

    public void updateList() {
        Context ctx = getActivity();
        Notes notes = new Notes(ctx);

        String[] from = { Notes.COLUMN_TITLE };
        int[] to = { android.R.id.text1 };

        ListAdapter adapter = new SimpleCursorAdapter(ctx,
                android.R.layout.simple_list_item_1, notes.getNotes(), from,
                to, 0);

        setListAdapter(adapter);

        notes.close();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onNoteClicked(id);
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_DELETE_ID, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_DELETE_ID:
                deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteNote(long id){
        Context ctx = getActivity();
        Notes notes = new Notes(ctx);

        if(notes.deleteNote(id)){
            Toast.makeText(ctx, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            updateList();
        } else{
            Toast.makeText(ctx, R.string.note_not_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    public static interface OnNoteClickedListener {
        public void onNoteClicked(long id);
    }
}
