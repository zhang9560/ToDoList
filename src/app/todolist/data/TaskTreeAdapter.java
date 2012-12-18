package app.todolist.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import app.todolist.R;
import app.todolist.utils.JOleDateTime;

import java.text.SimpleDateFormat;

public class TaskTreeAdapter extends CursorAdapter implements CheckBox.OnClickListener {

    private static class ViewHolder {
        ImageView priority;
        TextView title;
        TextView subtaskCount;
        TextView dueDate;
        TextView tags;
        CheckBox completed;
    }

    public TaskTreeAdapter(Context context, Cursor c, int flags, boolean showSubtaskCount, Handler handler) {
        super(context, c, flags);
        mShowSubtaskCount = showSubtaskCount;
        mHandler = handler;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, null);
        ViewHolder holder = new ViewHolder();

        holder.priority = (ImageView)view.findViewById(R.id.task_priority_img);
        holder.title = (TextView)view.findViewById(R.id.task_title);
        holder.subtaskCount = (TextView)view.findViewById(R.id.subtask_count);
        holder.dueDate = (TextView)view.findViewById(R.id.task_due_date);
        holder.tags = (TextView)view.findViewById(R.id.task_tags);
        holder.completed = (CheckBox)view.findViewById(R.id.task_completion_checkbox);
        holder.completed.setOnClickListener(this);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.completed.setTag(R.id.tag_key_task_id, cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_ID)));
        holder.priority.setBackgroundResource(priority2Res(cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PRIORITY))));

        // Clear contents.
        holder.subtaskCount.setText(null);
        holder.dueDate.setText(null);
        holder.tags.setText(null);

        String title = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TITLE));
        if (cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PERCENTDONE)) == 100) {
            holder.completed.setChecked(true);
            // If the task is done, it doesn't show any other information except task's title.
            SpannableString strikeTitle = new SpannableString(title);
            strikeTitle.setSpan(sStrikethroughSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.title.setText(strikeTitle);
        } else {
            holder.completed.setChecked(false);
            holder.title.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.title.setText(title);
            holder.tags.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAGS)));

            int doneSubTaskCount = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_DONE_SUBTASK_COUNT));
            int subtaskCount = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_SUBTASK_COUNT));

            if (subtaskCount > 0 && mShowSubtaskCount) {
                holder.subtaskCount.setText(String.format("(%d / %d)", doneSubTaskCount, subtaskCount));
            }

            double dateTime = cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DUE_DATE));
            holder.dueDate.setText(null); // Clear content.
            if(dateTime > 0) {
                JOleDateTime dueDate = new JOleDateTime(dateTime);
                holder.dueDate.setText(sDueDateFormat.format(dueDate.getTime()));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mHandler != null) {
            CheckBox checkBox = (CheckBox)view;
            long taskId = Long.valueOf(checkBox.getTag(R.id.tag_key_task_id).toString());
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putLong(TaskProvider.KEY_ID, taskId);
            bundle.putInt(TaskProvider.KEY_PERCENTDONE, checkBox.isChecked() ? 100 : 0);
            msg.setData(bundle);

            mHandler.sendMessage(msg);
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
            case 3:
                return R.drawable.priority_3;
            case 4:
                return R.drawable.priority_4;
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

    private static final SimpleDateFormat sDueDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private static final StrikethroughSpan sStrikethroughSpan = new StrikethroughSpan();

    private boolean mShowSubtaskCount;
    private Handler mHandler;
}
