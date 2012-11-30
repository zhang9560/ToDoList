package app.todolist.data;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import app.todolist.ui.TaskListFragment;

public class ViewPagerMainAdapter extends FragmentPagerAdapter {

    public ViewPagerMainAdapter(FragmentManager fm, Context context) {
        super(fm);

        mFragments = new Fragment[3];
        mFragments[0] = Fragment.instantiate(context, TaskListFragment.class.getName(), null);
        mFragments[1] = Fragment.instantiate(context, TaskListFragment.class.getName(), null);
        mFragments[2] = Fragment.instantiate(context, TaskListFragment.class.getName(), null);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments[i];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    private Fragment[] mFragments;
}
