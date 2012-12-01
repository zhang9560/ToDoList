package app.todolist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class TaskProvider extends ContentProvider {
    public static final String TAG = "TaskProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://app.todolist.provider");

    private static final String DATABASE_NAME = "todolist.db";
    private static final int TASKS = 1;
    private static final int TASK_ID =2;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), "tasks", TASKS);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), "tasks/#", TASK_ID);
    }

    private static final String CREATE_TASKS_TABLE = "create table " +
            "tasks " +
            "(" +
            "_id integer primary key, " +
            "ID integer, " +
            "TITLE text, " +
            "COMMENTS text, " +
            "COMMENTSTYLE text, " +
            "CUSTOMCOMMENTS text, " +
            "PRIORITY integer, " +
            "PERCENTDONE integer, " +
            "CREATIONDATE real, " +
            "LASTMOD real, " +
            "STARTDATE real, " +
            "DUEDATE real, " +
            "DONEDATE real, " +
            "LISTID integer, " +
            "PARENTID integer, " +
            "TAGID text);";

    private static final class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mOpenHelper;
}
