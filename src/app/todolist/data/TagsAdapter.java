package app.todolist.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import app.todolist.R;

public class TagsAdapter extends CursorAdapter {

    public TagsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.tags_spinner_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CheckBox tag = (CheckBox)view;
        tag.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAG_NAME)));
    }
}
