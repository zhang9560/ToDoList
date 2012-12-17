package app.todolist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.data.ViewPagerMainAdapter;
import app.todolist.utils.JOleDateTime;

public class MainActivity extends Activity implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Add view page to scroll between tabs.
        mViewPager = (ViewPager)findViewById(R.id.view_pager_main);
        ViewPagerMainAdapter viewPagerMainAdapter = new ViewPagerMainAdapter(getFragmentManager(), this);
        mViewPager.setAdapter(viewPagerMainAdapter);
        mViewPager.setOnPageChangeListener(this);

        // Add action bar and navigation tabs.
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab taskTreeTab = mActionBar.newTab();
        taskTreeTab.setText(R.string.all);
        taskTreeTab.setTabListener(this);
        mActionBar.addTab(taskTreeTab);

        ActionBar.Tab listViewTab = mActionBar.newTab();
        listViewTab.setText(R.string.upcoming);
        listViewTab.setTabListener(this);
        mActionBar.addTab(listViewTab);

        ActionBar.Tab tagsTab = mActionBar.newTab();
        tagsTab.setText(R.string.overdue);
        tagsTab.setTabListener(this);
        mActionBar.addTab(tagsTab);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mActionBar.setSelectedNavigationItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void completeTask(long taskId, long parentId, boolean done) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        double now = new JOleDateTime().getDateTime();

        values.put(TaskProvider.KEY_DONE_DATE, done ? now : 0);
        values.put(TaskProvider.KEY_LAST_MOD, now);
        values.put(TaskProvider.KEY_PERCENTDONE, done ? 100 : 0);

        if (resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(taskId)), values, null, null) > 0 && parentId > 0) {
            // Update done subtask count of the task's parent.
            // Top level tasks have no parents, so needn't update when updating a top level task.
            String[] projection = {"count(*)"};
            String selection = String.format("%s=%d and %s=%d", TaskProvider.KEY_PARENT_ID, parentId, TaskProvider.KEY_PERCENTDONE, 100);
            Cursor cursor = resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                values.clear();
                values.put(TaskProvider.KEY_DONE_SUBTASK_COUNT, cursor.getLong(0));
                resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(parentId)), values, null, null);
            }
        }
    }

    private ViewPager mViewPager;
    private ActionBar mActionBar;
}
