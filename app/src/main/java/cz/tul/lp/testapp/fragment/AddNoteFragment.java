package cz.tul.lp.testapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cz.tul.lp.testapp.R;


public class AddNoteFragment extends Fragment {

    private OnAddNoteListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAddNoteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddNoteListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note, null);

        Button submit = (Button)view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmitClicked();
            }
        });

        return view;
    }

    public void onSubmitClicked(){
        View root = getView();

        String title = ((EditText)root.findViewById(R.id.title)).getText().toString();
        String text = ((EditText)root.findViewById(R.id.text)).getText().toString();

        mListener.onAddNote(title, text);
    }

    public static interface OnAddNoteListener {
        public void onAddNote(String title, String text);
    }
}
