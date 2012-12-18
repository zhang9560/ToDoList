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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import app.todolist.R;
import app.todolist.data.PrioritySpinnerAdapter;
import app.todolist.data.TagsAdapter;
import app.todolist.data.TaskProvider;
import app.todolist.utils.JOleDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskInfoFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String COMMENT_STYLE_PLAIN_TEXT = "PLAIN_TEXT";
    public static final String COMMENT_STYLE_RICH_TEXT = "849cf988-79fe-418a-a40d-01fe3afcab2c";

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
            mDueDateText.setText(sDueDateFormat.format(dateTime.getTime()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_info, null);
        mTitle = (EditText)view.findViewById(R.id.task_info_title);
        mComments = (EditText)view.findViewById(R.id.task_info_comments);
        mPrioritySpinner = (Spinner)view.findViewById(R.id.task_info_priority);
        mPrioritySpinner.setAdapter(new PrioritySpinnerAdapter(getActivity()));
        mDueDateText = (EditText)view.findViewById(R.id.task_info_due_date_text);
        mDueDateBtn = (ImageView)view.findViewById(R.id.task_info_due_date_btn);
        mDueDateBtn.setOnClickListener(this);

        mTags = (MultiAutoCompleteTextView)view.findViewById(R.id.task_info_tags);
        mTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mTags.showDropDown();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mTaskId > 0) {
            Uri uri = Uri.withAppendedPath(TaskProvider.TASK_URI, String.valueOf(mTaskId));
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mTitle.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TITLE)));
                mTags.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_TAGS)));

                int priority = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PRIORITY));
                if (priority >= 0) {
                    mPrioritySpinner.setSelection(priority + 1);
                }

                mComments.setText(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_COMMENTS)));
                mCommentStyle = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_COMMENT_STYLE));
                if (mCommentStyle.equals(COMMENT_STYLE_RICH_TEXT)) {
                    mCustomComments = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_CUSTOM_COMMENTS));
                }

                mDueDate = cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_DUE_DATE));
                if (mDueDate > 0) {
                    mDueDateText.setText(sDueDateFormat.format(new JOleDateTime(mDueDate).getTime()));
                }

                mStartDate = cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_START_DATE));
                mCreationDate = cursor.getDouble(cursor.getColumnIndex(TaskProvider.KEY_CREATION_DATE));
                mPercentDone = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_PERCENTDONE));

                cursor.close();
            }
        }

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
            mTagsAdapter = new TagsAdapter(getActivity(), cursor, 0);
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
        if (mCommentStyle.equals(COMMENT_STYLE_RICH_TEXT)) {
            values.put(TaskProvider.KEY_CUSTOM_COMMENTS, mCustomComments);
        }

        values.put(TaskProvider.KEY_TAGS, mTags.getEditableText().toString());
        values.put(TaskProvider.KEY_LIST_ID, 1);
        values.put(TaskProvider.KEY_DUE_DATE, mDueDate);
        values.put(TaskProvider.KEY_START_DATE, mStartDate);
        values.put(TaskProvider.KEY_CREATION_DATE, mCreationDate);
        values.put(TaskProvider.KEY_PRIORITY, mPrioritySpinner.getSelectedItemId());
        values.put(TaskProvider.KEY_PERCENTDONE, mPercentDone);

        return values;
    }

    public void setTaskId(long id) {
        mTaskId = id;
    }

    protected static final SimpleDateFormat sDueDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private EditText mTitle;
    private EditText mComments;
    private MultiAutoCompleteTextView mTags;
    private Spinner mPrioritySpinner;
    private EditText mDueDateText;
    private ImageView mDueDateBtn;

    private double mDueDate = 0;
    private double mStartDate = 0;
    private double mCreationDate = new JOleDateTime().getDateTime();
    private String mCommentStyle = COMMENT_STYLE_PLAIN_TEXT;
    private String mCustomComments;
    private int mPercentDone = 0;

    private long mTaskId = 0;
    private TagsAdapter mTagsAdapter;
}
