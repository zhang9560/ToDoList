package app.todolist.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import app.todolist.R;

public class TaskTreeAdapter extends CursorAdapter {

    private static class ViewHolder {
        TextView title;
    }

    public TaskTreeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_item, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView)view.findViewById(R.id.task_title);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.title.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAG_NAME)));
    }

    private Context mContext;
}
