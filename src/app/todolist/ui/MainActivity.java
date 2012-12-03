package app.todolist.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import app.todolist.R;
import app.todolist.data.TaskProvider;
import app.todolist.data.ViewPagerMainAdapter;

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
        taskTreeTab.setText(R.string.task_tree);
        taskTreeTab.setTabListener(this);
        mActionBar.addTab(taskTreeTab);

        ActionBar.Tab listViewTab = mActionBar.newTab();
        listViewTab.setText(R.string.list_view);
        listViewTab.setTabListener(this);
        mActionBar.addTab(listViewTab);

        ActionBar.Tab tagsTab = mActionBar.newTab();
        tagsTab.setText(R.string.tags);
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

    private ViewPager mViewPager;
    private ActionBar mActionBar;
}
