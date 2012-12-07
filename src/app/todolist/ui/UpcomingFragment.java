package app.todolist.ui;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.data.TaskTreeAdapter;
import app.todolist.utils.JOleDateTime;

import java.util.Calendar;

public class UpcomingFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
        // Don't show add task menu in action bar.
        menu.findItem(R.id.main_activity_menu_new_task).setVisible(false);
    }

    @Override
    public void onListItemClick(ListView listView, View item, int position, long id) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        JOleDateTime dateTime = new JOleDateTime();
        long today = (long)dateTime.getDateTime();

        dateTime.add(Calendar.DAY_OF_YEAR, 7); // In one week
        long end = (long)dateTime.getDateTime();

        String where = String.format("%s >= %d and %s < %d", TaskProvider.KEY_DUE_DATE, today, TaskProvider.KEY_DUE_DATE, end, TaskProvider.KEY_SUBTASK_COUNT);
        String order = String .format("%s asc, %s desc", TaskProvider.KEY_DUE_DATE, TaskProvider.KEY_PRIORITY);
        return new CursorLoader(getActivity(), TaskProvider.TASK_URI, null, where, null, order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new TaskTreeAdapter(getActivity(), cursor, 0, false);
            setListAdapter(mAdapter);
        } else {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private TaskTreeAdapter mAdapter;
}
