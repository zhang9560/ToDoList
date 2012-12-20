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

import java.util.ArrayList;

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

        values.put(TaskProvider.KEY_PERCENTDONE, done ? 100 : 0);
        values.put(TaskProvider.KEY_DONE_DATE, done ? now : 0);
        values.put(TaskProvider.KEY_LAST_MOD, now);

        if (resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(taskId)), values, null, null) > 0) {
           updateSubTaskCount(parentId);
        }
    }

    public void deleteTask(long taskId, long parentId) {
        ContentResolver resolver = getContentResolver();

        // Delete the task pointed by the taskId, and update its parent's subtask count.
        if (resolver.delete(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(taskId)), null, null) > 0) {
            updateSubTaskCount(parentId);
        }

        // Delete all subtasks of the deleted task, include archived tasks.
        String[] projection = {TaskProvider.KEY_ID};
        Cursor cursor;

        ArrayList<Long> subTaskIds = new ArrayList<Long>();
        ArrayList<Long> temp = new ArrayList<Long>();
        temp.add(taskId);
        ArrayList<Long> temp2 = new ArrayList<Long>();

        while (temp.size() > 0) {
            for (long id : temp) {
                String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, id);
                cursor =  resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long subTaskId = cursor.getLong(0);
                        temp2.add(subTaskId);
                        subTaskIds.add(subTaskId);
                    }
                    cursor.close();
                }
            }

            temp.clear();
            for (long id : temp2) {
                temp.add(id);
            }
            temp2.clear();
        }

        // Now all subtask ids are in subTaskIds, delete them all at once.
        if (subTaskIds.size() > 0) {
            String selection = TaskProvider.KEY_ID + " in (";
            for (long id : subTaskIds) {
                selection += id;
                selection += ", ";
            }
            selection = selection.substring(0, selection.length() - 2);
            selection += ")";

            resolver.delete(TaskProvider.TASK_URI, selection, null);
        }
    }

    private void updateSubTaskCount(long parentId) {
        // Top level tasks have no parents, so needn't update when updating a top level task.
        if (parentId > 0) {
            ContentResolver resolver = getContentResolver();
            long subTaskCount = 0;
            long doneSubTaskCount = 0;

            String[] projection = {"count(*)"};
            String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, parentId);
            Cursor cursor = resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                subTaskCount = cursor.getLong(0);
                cursor.close();
            }

            selection += String.format(" and %s=%d", TaskProvider.KEY_PERCENTDONE, 100);
            cursor = resolver.query(TaskProvider.TASK_URI, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                doneSubTaskCount = cursor.getLong(0);
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put(TaskProvider.KEY_SUBTASK_COUNT, subTaskCount);
            values.put(TaskProvider.KEY_DONE_SUBTASK_COUNT, doneSubTaskCount);
            resolver.update(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(parentId)), values, null, null);
        }
    }

    private ViewPager mViewPager;
    private ActionBar mActionBar;
}
