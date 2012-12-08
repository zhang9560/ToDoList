package app.todolist.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import app.todolist.R;

public class PrioritySpinnerAdapter extends BaseAdapter {
    public PrioritySpinnerAdapter(Context context) {
        mContext = context;
        mPriorityTitles = context.getResources().getStringArray(R.array.priority_array);
    }

    @Override
    public int getCount() {
        return mPriorityTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mPriorityTitles[position];
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return -2; // priority none
        }
        return position - 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.prioriy_spinner_item, null);
            holder = new ViewHolder();
            holder.color = (ImageView)convertView.findViewById(R.id.priority_spinner_item_color);
            holder.title = (TextView)convertView.findViewById(R.id.priority_spinner_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.color.setBackgroundColor(mContext.getResources().getColor(mPriorityColors[position]));
        holder.title.setText(mPriorityTitles[position]);

        return convertView;
    }

    private class ViewHolder {
        public ImageView color;
        public TextView title;
    }

    private Context mContext;
    private String[] mPriorityTitles;
    private int[] mPriorityColors = {
            R.color.priority_none,
            R.color.priority_0,
            R.color.priority_1,
            R.color.priority_2,
            R.color.priority_3,
            R.color.priority_4,
            R.color.priority_5,
            R.color.priority_6,
            R.color.priority_7,
            R.color.priority_8,
            R.color.priority_9,
            R.color.priority_10
    };
}
