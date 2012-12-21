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

    public void setIsArchiveMode(boolean isArchivedMode) {
        mIsArchiveMode = isArchivedMode;
    }

    public boolean getIsArchiveMode() {
        return mIsArchiveMode;
    }

    public void addTask(Uri uri, ContentValues values) {
        ContentResolver resolver = getContentResolver();
        long parentId = values.getAsLong(TaskProvider.KEY_PARENT_ID);

        if (resolver.insert(uri, values) != null && parentId > 0) {
            // Update subtask count of new task's parent.
            // Top level tasks have no parents, so needn't update when adding a top level task.
            long subTaskCount = 0;
            long uncompletedSubTaskCount = 0;
            String[] projection = {"count(*)"};
            String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, parentId);
            Cursor cursor = resolver.query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                subTaskCount = cursor.getLong(0);
                cursor.close();
            }

            selection += String.format(" and %s=%d", TaskProvider.KEY_PERCENTDONE, 100);
            cursor = resolver.query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                uncompletedSubTaskCount = subTaskCount - cursor.getLong(0);
                cursor.close();
            }

            ContentValues updateValues = new ContentValues();
            updateValues.put(TaskProvider.KEY_SUBTASK_COUNT, subTaskCount);
            updateValues.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, uncompletedSubTaskCount);
            resolver.update(Uri.withAppendedPath(uri, String.valueOf(parentId)), updateValues, null, null);
        }
    }

    public void completeTask(Uri uri, long taskId, long parentId, boolean done) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        double now = new JOleDateTime().getDateTime();

        values.put(TaskProvider.KEY_PERCENTDONE, done ? 100 : 0);
        values.put(TaskProvider.KEY_DONE_DATE, done ? now : 0);
        values.put(TaskProvider.KEY_LAST_MOD, now);

        if (resolver.update(Uri.withAppendedPath(uri, String.valueOf(taskId)), values, null, null) > 0) {
           updateSubTaskCount(uri, parentId);
        }

        if (done) {
            // Complete all subtasks.
            ArrayList<Long> subTaskIds = getSubTaskIds(uri, taskId);

            if (subTaskIds.size() > 0) {
                String selection = TaskProvider.KEY_ID + " in (";
                for (long id : subTaskIds) {
                    selection += id;
                    selection += ", ";
                }
                selection = selection + taskId + ")";

                values.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, 0);
                resolver.update(uri, values, selection, null);
            }
        }
    }

    public void deleteTask(Uri uri, long taskId, long parentId) {
        ContentResolver resolver = getContentResolver();

        // Delete the task pointed by the taskId, and update its parent's subtask count.
        if (resolver.delete(Uri.withAppendedPath(uri, String.valueOf(taskId)), null, null) > 0) {
            updateSubTaskCount(uri, parentId);
        }

        // Delete all subtasks of the deleted task.
        ArrayList<Long> subTaskIds = getSubTaskIds(uri, taskId);

        if (subTaskIds.size() > 0) {
            String selection = TaskProvider.KEY_ID + " in (";
            for (long id : subTaskIds) {
                selection += id;
                selection += ", ";
            }
            selection = selection.substring(0, selection.length() - 2);
            selection += ")";

            resolver.delete(uri, selection, null);
        }
    }

    public void archiveTask(long taskId, long parentId) {
        ArrayList<Long> list;
        ContentResolver resolver = getContentResolver();
        Cursor cursor;
        ContentValues values = new ContentValues();
        String[] projection = {TaskProvider.KEY_ID};

        // Copy taskId, its parents and children into archive table.
        list = getParentIds(TaskProvider.TASK_URI, taskId);
        list.add(taskId);
        list.addAll(getSubTaskIds(TaskProvider.TASK_URI, taskId));

        for (int i = 0; i < list.size(); i++) {
            // If the task exists in archive table, do not add it.
            cursor = resolver.query(Uri.withAppendedPath(TaskProvider.ARCHIVE_URI, String.valueOf(list.get(i))), projection, null, null, null);
            if (cursor.getCount() == 0) {
                cursor.close();

                cursor = resolver.query(Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(list.get(i))), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    values.put(TaskProvider.KEY_ID, cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_ID)));
                    values.put(TaskProvider.KEY_TITLE, cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TITLE)));
                    values.put(TaskProvider.KEY_COMMENTS, cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_COMMENTS)));
                    values.put(TaskProvider.KEY_COMMENT_STYLE, cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_COMMENT_STYLE)));
                    values.put(TaskProvider.KEY_CUSTOM_COMMENTS, cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_CUSTOM_COMMENTS)));
                    values.put(TaskProvider.KEY_PRIORITY, cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PRIORITY)));
                    values.put(TaskProvider.KEY_PERCENTDONE, cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PERCENTDONE)));
                    values.put(TaskProvider.KEY_CREATION_DATE, cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_CREATION_DATE)));
                    values.put(TaskProvider.KEY_LAST_MOD, cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_LAST_MOD)));
                    values.put(TaskProvider.KEY_START_DATE, cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_START_DATE)));
                    values.put(TaskProvider.KEY_DUE_DATE, cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DUE_DATE)));
                    values.put(TaskProvider.KEY_DONE_DATE, cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DONE_DATE)));
                    values.put(TaskProvider.KEY_PARENT_ID, cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_PARENT_ID)));
                    values.put(TaskProvider.KEY_TAGS, cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAGS)));
                    values.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, 0);
                    values.put(TaskProvider.KEY_SUBTASK_COUNT, 0);
                    cursor.close();
                    addTask(TaskProvider.ARCHIVE_URI, values);
                }
            }
        }

        // Delete taskId and its children from tasks table.
        deleteTask(TaskProvider.TASK_URI, taskId, parentId);
    }

    private void updateSubTaskCount(Uri uri, long parentId) {
        // Top level tasks have no parents, so needn't update when updating a top level task.
        if (parentId > 0) {
            ContentResolver resolver = getContentResolver();
            long subTaskCount = 0;
            long uncompletedSubTaskCount = 0;

            String[] projection = {"count(*)"};
            String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, parentId);
            Cursor cursor = resolver.query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                subTaskCount = cursor.getLong(0);
                cursor.close();
            }

            selection += String.format(" and %s=%d", TaskProvider.KEY_PERCENTDONE, 100);
            cursor = resolver.query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                uncompletedSubTaskCount = subTaskCount - cursor.getLong(0);
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put(TaskProvider.KEY_SUBTASK_COUNT, subTaskCount);
            values.put(TaskProvider.KEY_UNCOMPLETED_SUBTASK_COUNT, uncompletedSubTaskCount);
            resolver.update(Uri.withAppendedPath(uri, String.valueOf(parentId)), values, null, null);
        }
    }

    // Parents are in the lower position, and children are in the higher position.
    private ArrayList<Long> getParentIds(Uri uri, long taskId) {
        String[] projection = {TaskProvider.KEY_PARENT_ID};
        ContentResolver resolver = getContentResolver();
        Cursor cursor;

        ArrayList<Long> parentIds = new ArrayList<Long>();
        parentIds.add(taskId);

        while (true) {
            cursor = resolver.query(Uri.withAppendedPath(uri, String.valueOf(parentIds.get(0))), projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long parentId = cursor.getLong(0);
                cursor.close();

                if (parentId > 0) {
                    parentIds.add(0, parentId);
                    continue;
                }
            }

            break;
        }

        parentIds.remove(parentIds.size() - 1);
        return parentIds;
    }

    // The ArrayList is like a tree, parents in the lower position, and children in the higher position.
    private ArrayList<Long> getSubTaskIds(Uri uri, long taskId) {
        String[] projection = {TaskProvider.KEY_ID};
        ContentResolver resolver = getContentResolver();
        Cursor cursor;

        ArrayList<Long> subTaskIds = new ArrayList<Long>();
        ArrayList<Long> temp = new ArrayList<Long>();
        temp.add(taskId);
        ArrayList<Long> temp2 = new ArrayList<Long>();

        while (temp.size() > 0) {
            for (long id : temp) {
                String selection = String.format("%s=%d", TaskProvider.KEY_PARENT_ID, id);
                cursor =  resolver.query(uri, projection, selection, null, null);
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

        return subTaskIds;
    }

    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private boolean mIsArchiveMode = false;
}
