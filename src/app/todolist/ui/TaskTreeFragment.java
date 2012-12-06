package app.todolist.ui;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.data.TaskTreeAdapter;

public class TaskTreeFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TaskProvider.TASK_URI, null, null, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new TaskTreeAdapter(getActivity(), cursor, 0);
            setListAdapter(mAdapter);
        } else {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView listView, View item, int position, long id) {
        Log.d("TaskTreeFragment[onListItemClick]", "position = " + position + "task id = " + item.getTag(R.id.tag_key_task_id));
    }

    private TaskTreeAdapter mAdapter;
}
