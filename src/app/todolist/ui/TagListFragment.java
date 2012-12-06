package app.todolist.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import app.todolist.R;

public class TagListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
        // Don't display add task and search menu in action bar.
        menu.findItem(R.id.main_activity_menu_add_task).setVisible(false);
        menu.findItem(R.id.main_activity_menu_search).setVisible(false);
    }
}
