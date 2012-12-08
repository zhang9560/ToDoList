package app.todolist.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import app.todolist.R;
import app.todolist.data.PriorityArrayAdapter;
import app.todolist.data.TaskProvider;
import app.todolist.utils.JOleDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskInfoFragment extends Fragment implements View.OnClickListener {

    private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            JOleDateTime dateTime = new JOleDateTime(year,  month, day);
            TaskInfoFragment.this.mDueDate = dateTime.getDateTime();
            mDueDateBtn.setText(sDueDateFormat.format(dateTime.getTime()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_info, null);
        mTitle = (EditText)view.findViewById(R.id.task_info_title);
        mPrioritySpinner = (Spinner)view.findViewById(R.id.task_info_priority);
        mPrioritySpinner.setAdapter(new PriorityArrayAdapter(getActivity()));
        mDueDateBtn = (Button)view.findViewById(R.id.task_info_due_date);
        mDueDateBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == mDueDateBtn) {
            new DatePickerFragment().show(getFragmentManager(), "datepicker");
        }
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.KEY_TITLE, mTitle.getText().toString());
        values.put(TaskProvider.KEY_LIST_ID, 1);
        values.put(TaskProvider.KEY_DUE_DATE, mDueDate);
        values.put(TaskProvider.KEY_PRIORITY, mPrioritySpinner.getSelectedItemId());

        return values;
    }

    protected static final SimpleDateFormat sDueDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private EditText mTitle;
    private Spinner mPrioritySpinner;
    private Button mDueDateBtn;
    private double mDueDate = 0;
}
