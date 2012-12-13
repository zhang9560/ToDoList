package app.todolist.data;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TagsAdapter extends CursorAdapter {

    public TagsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAG_NAME));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.simple_dropdown_item_1line, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text = (TextView)view;
        text.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAG_NAME)));
    }
}
