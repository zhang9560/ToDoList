package app.todolist.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import app.todolist.R;
import app.todolist.data.PrioritySpinnerAdapter;
import app.todolist.data.TaskProvider;
import app.todolist.utils.JOleDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskInfoFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String COMMENT_STYLE_PLAIN_TEXT = "PLAIN_TEXT";
    public static final String COMMENT_STYLE_RICH_TEXT = "849cf988-79fe-418a-a40d-01fe3afcab2c";

    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

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
        mComments = (EditText)view.findViewById(R.id.task_info_comments);
        mTags = (MultiAutoCompleteTextView)view.findViewById(R.id.task_info_tags);
        mPrioritySpinner = (Spinner)view.findViewById(R.id.task_info_priority);
        mPrioritySpinner.setAdapter(new PrioritySpinnerAdapter(getActivity()));
        mDueDateBtn = (Button)view.findViewById(R.id.task_info_due_date);
        mDueDateBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onClick(View view) {
        if (view == mDueDateBtn) {
            new DatePickerFragment().show(getFragmentManager(), "datepicker");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), TaskProvider.TAG_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (mTagsAdapter == null) {
            String[] from = {TaskProvider.KEY_TAG_NAME};
            int[] to = {android.R.id.text1};
            mTagsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, cursor, from, to, 0);
            mTags.setAdapter(mTagsAdapter);
            mTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        } else {
            mTagsAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mTagsAdapter.swapCursor(null);
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.KEY_TITLE, mTitle.getText().toString());
        values.put(TaskProvider.KEY_COMMENTS, mComments.getText().toString());
        values.put(TaskProvider.KEY_COMMENT_STYLE, mCommentStyle);
        values.put(TaskProvider.KEY_LIST_ID, 1);
        values.put(TaskProvider.KEY_DUE_DATE, mDueDate);
        values.put(TaskProvider.KEY_START_DATE, mStartDate);
        values.put(TaskProvider.KEY_CREATION_DATE, mCreationDate);
        values.put(TaskProvider.KEY_PRIORITY, mPrioritySpinner.getSelectedItemId());
        values.put(TaskProvider.KEY_PERCENTDONE, mPercentDone);

        return values;
    }

    protected static final SimpleDateFormat sDueDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private EditText mTitle;
    private EditText mComments;
    private MultiAutoCompleteTextView mTags;
    private Spinner mPrioritySpinner;
    private Button mDueDateBtn;
    private double mDueDate = 0;
    private double mStartDate = 0;
    private double mCreationDate = new JOleDateTime().getDateTime();
    private String mCommentStyle = COMMENT_STYLE_PLAIN_TEXT;
    private int mPercentDone = 0;

    private SimpleCursorAdapter mTagsAdapter;
}
