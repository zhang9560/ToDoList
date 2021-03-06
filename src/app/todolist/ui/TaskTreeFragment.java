package app.todolist.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private class TaskOperation extends AsyncTask<Long, Void, Void> {
        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(TaskTreeFragment.this.getActivity(), null, TaskTreeFragment.this.getString(R.string.please_wait), true, false);
        }

        @Override
        protected Void doInBackground(Long... params) {
            int contextMenuId = params[0].intValue();
            long taskId = params[1];
            Uri uri = mActivity.getIsArchiveMode() ? TaskProvider.ARCHIVE_URI : TaskProvider.TASK_URI;

            switch (contextMenuId) {
                case R.id.complete_task:
                    mActivity.completeTask(uri, taskId, mParentIdStack.peek(), params[2] == 1L);
                    break;
                case R.id.delete_task:
                    mActivity.deleteTask(uri, taskId, mParentIdStack.peek());
                    break;
                case R.id.archive_task:
                    mActivity.archiveTask(taskId, mParentIdStack.peek());
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.dismiss();
            mDialog = null;
            refreshTaskTree();
        }

        private ProgressDialog mDialog;
    }

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
        mActivity = (MainActivity)getActivity();
        getLoaderManager().initLoader(0, null, this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mActivity.getMenuInflater().inflate(R.menu.task_tree_list_context_menu, menu);

        // Don't show archive menu item in archive mode.
        menu.findItem(R.id.archive_task).setVisible(!mActivity.getIsArchiveMode());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.edit_task:
                Intent intent = new Intent(mActivity, EditTaskActivity.class);
                intent.putExtra(TaskProvider.KEY_ID, menuInfo.id);
                intent.putExtra(TaskProvider.KEY_PARENT_ID, mParentIdStack.peek());
                startActivityForResult(intent, 0);
                break;
            case R.id.delete_task:
                deleteTask(menuInfo.id);
                break;
            case R.id.archive_task:
                archiveTask(menuInfo.id);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);

        // Don't display back button in top level.
        menu.findItem(R.id.main_activity_menu_back).setVisible(mParentIdStack.peek() > 0);
        // Do not allow to add new task in archive mode.
        menu.findItem(R.id.main_activity_menu_new_task).setVisible(!mActivity.getIsArchiveMode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_menu_back:
                mParentIdStack.pop();
                refreshTaskTree();
                mActivity.invalidateOptionsMenu();
                break;
            case R.id.main_activity_menu_new_task:
                Intent intent = new Intent(mActivity, NewTaskActivity.class);
                intent.putExtra(TaskProvider.KEY_PARENT_ID, mParentIdStack.peek());
                startActivityForResult(intent, 0);
                break;
            case R.id.main_activity_menu_switch_list:
                // Go back to top level.
                mParentIdStack.clear();
                mParentIdStack.push(0L);
                mActivity.setIsArchiveMode(!mActivity.getIsArchiveMode());
                refreshTaskTree();
                mActivity.invalidateOptionsMenu();
                break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            refreshTaskTree();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID,  mParentIdStack.peek());
        return new CursorLoader(mActivity, mActivity.getIsArchiveMode() ? TaskProvider.ARCHIVE_URI : TaskProvider.TASK_URI, null, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new TaskTreeAdapter(mActivity, cursor, 0, true, mHandler);
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
        refreshTaskTree();
        mActivity.invalidateOptionsMenu();
    }

    private void refreshTaskTree() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void completeTask(long taskId, boolean done) {
        new TaskOperation().execute((long)R.id.complete_task, taskId, done ? 1L : 0L);
    }

    private void deleteTask(long taskId) {
        new TaskOperation().execute((long)R.id.delete_task, taskId);
    }

    private void archiveTask(long taskId) {
        new TaskOperation().execute((long)R.id.archive_task, taskId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            completeTask(bundle.getLong(TaskProvider.KEY_ID), bundle.getInt(TaskProvider.KEY_PERCENTDONE) == 100);
        }
    };

    private TaskTreeAdapter mAdapter;
    private Stack<Long> mParentIdStack = new Stack<Long>();
    private MainActivity mActivity;
}
