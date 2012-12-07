package app.todolist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import app.todolist.R;
import app.todolist.data.TaskProvider;

public class NewTaskActivity extends Activity {
    public static final String TAG = "NewTaskActivity";

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
                ContentValues values = mTaskInfoFragment.getContentValues();
                values.put(TaskProvider.KEY_PARENT_ID, mParentId);
                getContentResolver().insert(TaskProvider.TASK_URI, values);
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

    private long mParentId;
    private TaskInfoFragment mTaskInfoFragment;
}
