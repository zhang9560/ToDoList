package app.todolist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.utils.JOleDateTime;

public class EditTaskActivity extends Activity {

    public static final String TAG = "EditTaskActivity";

    private class EditTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(EditTaskActivity.this, null, getString(R.string.please_wait), true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver resolver = getContentResolver();

            // Update current task in database.
            ContentValues values = mTaskInfoFragment.getContentValues();
            values.put(TaskProvider.KEY_LAST_MOD, new JOleDateTime().getDateTime());
            resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(mTaskId)), values, null, null);

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

        Intent intent = getIntent();

        mTaskId = intent.getLongExtra(TaskProvider.KEY_ID, 0);
        Log.d(TAG,"task id =" + mTaskId);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mTaskInfoFragment = new TaskInfoFragment();
        mTaskInfoFragment.setTaskId(mTaskId);
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
                new EditTask().execute();
                setResult(RESULT_OK);
                finish();
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

    private long mTaskId;
    private TaskInfoFragment mTaskInfoFragment;
}
