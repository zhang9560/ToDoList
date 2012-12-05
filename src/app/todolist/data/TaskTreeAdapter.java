package app.todolist.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import app.todolist.R;

public class TaskTreeAdapter extends CursorAdapter {

    private static class ViewHolder {
        ImageView priority;
        TextView title;
        TextView subtaskCount;
        TextView dueDate;
        TextView tags;
    }

    public TaskTreeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_item, null);
        ViewHolder holder = new ViewHolder();

        holder.priority = (ImageView)view.findViewById(R.id.task_priority_img);
        holder.title = (TextView)view.findViewById(R.id.task_title);
        holder.subtaskCount = (TextView)view.findViewById(R.id.subtask_count);
        holder.dueDate = (TextView)view.findViewById(R.id.task_due_date);
        holder.tags = (TextView)view.findViewById(R.id.task_tags);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.title.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TITLE)));
        holder.dueDate.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DUE_DATE))));
        holder.tags.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAGS)));

        int subtaskCount = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_SUBTASK_COUNT));
        if (subtaskCount > 0) {
            holder.subtaskCount.setText(String.format("(%d)", subtaskCount));
        }
    }

    private Context mContext;
}
