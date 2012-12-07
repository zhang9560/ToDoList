package app.todolist.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        //Parent id is 0 means the top level tasks.
        mParentIdStack.push(0L);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.task_tree_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_task:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                Log.d(TAG, "task id = " + menuInfo.id);
                break;
        }
        return true;
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
            case R.id.main_activity_menu_new_task:
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                intent.putExtra(TaskProvider.KEY_PARENT_ID, mParentIdStack.peek());
                startActivityForResult(intent, 0);
                break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getLoaderManager().restartLoader(0, null, this);
        }
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
        Log.d(TAG, "[onListItemClick] task id = " + id);

        mParentIdStack.push(id);
        getLoaderManager().restartLoader(0, null, this);
        getActivity().invalidateOptionsMenu();
    }

    private TaskTreeAdapter mAdapter;
    private Stack<Long> mParentIdStack = new Stack<Long>();
}
