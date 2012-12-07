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
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.data.TaskTreeAdapter;

import java.util.Stack;

public class TaskTreeFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = "TaskTreeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Parent id is 0 means the top level tasks.
        mParentIdStack.push(0L);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);

        MenuItem backItem = menu.findItem(R.id.main_activity_menu_back);
        if (mParentIdStack.peek() > 0) {
            backItem.setVisible(true);
        } else {
            backItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_menu_back:
                mParentIdStack.pop();
                getLoaderManager().restartLoader(0, null, this);
                getActivity().invalidateOptionsMenu();
                break;
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TaskProvider.TASK_URI, null, TaskProvider.KEY_PARENT_ID + "=" + mParentIdStack.peek(), null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new TaskTreeAdapter(getActivity(), cursor, 0, true);
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
        long taskId = Long.valueOf(item.getTag(R.id.tag_key_task_id).toString());
        boolean hasSubtask = Boolean.valueOf(item.getTag(R.id.tag_key_has_subtask).toString());
        Log.d(TAG, "[onListItemClick] task id = " + taskId + ", has subtask = " + hasSubtask);

        if (hasSubtask) {
            mParentIdStack.push(taskId);
            getLoaderManager().restartLoader(0, null, this);
            getActivity().invalidateOptionsMenu();
        }
    }

    private TaskTreeAdapter mAdapter;
    private Stack<Long> mParentIdStack = new Stack<Long>();
}
