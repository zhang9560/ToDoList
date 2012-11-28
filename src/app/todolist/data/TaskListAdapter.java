package app.todolist.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import app.todolist.R;

public class TaskListAdapter extends BaseAdapter {
    public TaskListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.task_item, null);
            viewHolder = new ViewHolder();
            viewHolder.taskTitle = (TextView)convertView.findViewById(R.id.task_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.taskTitle.setText("Task Title");

        return convertView;
    }

    static class ViewHolder {
        TextView taskTitle;
    }

    private Context mContext;
}
