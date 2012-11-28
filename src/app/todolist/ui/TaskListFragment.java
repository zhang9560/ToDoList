package app.todolist.ui;

import android.app.ListFragment;
import android.os.Bundle;
import app.todolist.data.TaskListAdapter;

public class TaskListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskListAdapter adapter = new TaskListAdapter(getActivity());
        setListAdapter(adapter);
    }
}
