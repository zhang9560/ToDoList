package app.todolist.ui;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import app.todolist.data.TaskProvider;
import app.todolist.data.TaskTreeAdapter;

public class TaskTreeFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor cursor = getActivity().getContentResolver().query(TaskProvider.TAG_URI, null, null, null, null);
        TaskTreeAdapter adapter = new TaskTreeAdapter(getActivity(), cursor, 0);
        this.setListAdapter(adapter);
    }
}
