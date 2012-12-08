package app.todolist.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import app.todolist.R;
import app.todolist.data.PriorityArrayAdapter;
import app.todolist.data.TaskProvider;

public class TaskInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_info, null);
        mTitle = (EditText)view.findViewById(R.id.task_info_title);
        mPrioritySpinner = (Spinner)view.findViewById(R.id.task_info_priority);
        mPrioritySpinner.setAdapter(new PriorityArrayAdapter(getActivity()));

        return view;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.KEY_TITLE, mTitle.getText().toString());
        values.put(TaskProvider.KEY_LIST_ID, 1);
        values.put(TaskProvider.KEY_DUE_DATE, 0);
        values.put(TaskProvider.KEY_PRIORITY, mPrioritySpinner.getSelectedItemId());

        return values;
    }

    private EditText mTitle;
    private Spinner mPrioritySpinner;
}
