package app.todolist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.utils.JOleDateTime;

public class NewTaskActivity extends Activity {
    public static final String TAG = "NewTaskActivity";

    private class InsertTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(NewTaskActivity.this, null, getString(R.string.please_wait), true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver resolver = getContentResolver();

            // Insert new task to database.
            ContentValues values = mTaskInfoFragment.getContentValues();
            values.put(TaskProvider.KEY_PARENT_ID, mParentId);
            values.put(TaskProvider.KEY_LAST_MOD, new JOleDateTime().getDateTime());
            values.put(TaskProvider.KEY_SUBTASK_COUNT, 0);
            values.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, 0);

            if (resolver.insert(TaskProvider.TASK_URI, values) != null && mParentId > 0) {
                // Update subtask count of new task's parent.
                // Top level tasks have no parents, so needn't update when adding a top level task.
                long subTaskCount = 0;
                long uncompletedSubTaskCount = 0;
                String[] projection = {"count(*)"};
                String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, mParentId);
                Cursor cursor = resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    subTaskCount = cursor.getLong(0);
                    cursor.close();
                }

                selection += String.format(" and %s=%d", TaskProvider.KEY_PERCENTDONE, 100);
                cursor = resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    uncompletedSubTaskCount = subTaskCount - cursor.getLong(0);
                    cursor.close();
                }

                values.clear();
                values.put(TaskProvider.KEY_SUBTASK_COUNT, subTaskCount);
                values.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, uncompletedSubTaskCount);
                resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(mParentId)), values, null, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }

        private ProgressDialog mDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParentId = getIntent().getLongExtra(TaskProvider.KEY_PARENT_ID, 0);
        Log.d(TAG, "parent id = " + String.valueOf(mParentId));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mTaskInfoFragment = new TaskInfoFragment();
        fragmentTransaction.add(android.R.id.content,mTaskInfoFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_task_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_task_activity_menu_done:
                new InsertTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    private long mParentId;
    private TaskInfoFragment mTaskInfoFragment;
}
