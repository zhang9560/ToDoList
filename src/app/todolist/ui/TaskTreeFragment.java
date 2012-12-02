package app.todolist.ui;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import app.todolist.data.TaskProvider;
import app.todolist.data.TaskTreeAdapter;

public class TaskTreeFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TaskProvider.TAG_URI, null, null, null, null);
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

    private TaskTreeAdapter mAdapter;
}
