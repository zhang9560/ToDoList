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
import app.todolist.utils.JOleDateTime;

import java.text.SimpleDateFormat;

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

        holder.priority.setImageResource(priority2Res(cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PRIORITY))));
        holder.title.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TITLE)));
        holder.tags.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAGS)));

        int subtaskCount = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_SUBTASK_COUNT));
        if (subtaskCount > 0) {
            holder.subtaskCount.setText(String.format("(%d)", subtaskCount));
        }

        double dateTime = cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DUE_DATE));
        if(dateTime > 0) {
            JOleDateTime dueDate = new JOleDateTime(dateTime);
            holder.dueDate.setText(sDueDateFormat.format(dueDate.getTime()));
        }
    }

    private int priority2Res(int priority) {
        switch (priority) {
            case -2:
                return R.drawable.priority_none;
            case 0:
                return R.drawable.priority_0;
            case 1:
                return R.drawable.priority_1;
            case 2:
                 return R.drawable.priority_2;
            case 5:
                return R.drawable.priority_5;
            case 6:
                return R.drawable.priority_6;
            case 7:
                return R.drawable.priority_7;
            case 8:
                return R.drawable.priority_8;
            case 9:
                return R.drawable.priority_9;
            case 10:
                return R.drawable.priority_10;
            default:
                return R.drawable.priority_none;
        }
    }

    protected static final SimpleDateFormat sDueDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private Context mContext;
}
