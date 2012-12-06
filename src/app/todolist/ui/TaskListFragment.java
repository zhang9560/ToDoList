package app.todolist.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import app.todolist.R;
import app.todolist.data.TaskListAdapter;

public class TaskListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        TaskListAdapter adapter = new TaskListAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
        // Don't show add task menu in action bar.
        menu.findItem(R.id.main_activity_menu_add_task).setVisible(false);
    }
}
