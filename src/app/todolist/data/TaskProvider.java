package app.todolist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TaskProvider extends ContentProvider {
    public static final String TAG = "TaskProvider";

    // Table names of the database.
    public static final String TASKS_TABLE = "tasks";
    public static final String LISTS_TABLE = "lists";
    public static final String TAGS_TABLE = "tags";

    // Columns for all the tables.
    public static final String KEY_ID = "_id";
    public static final String KEY_COMMENTS = "COMMENTS";
    public static final String KEY_COMMENT_STYLE = "COMMENTSTYLE";
    public static final String KEY_CUSTOM_COMMENTS = "CUSTOMCOMMENTS";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_PRIORITY = "PRIORITY";
    public static final String KEY_PERCENTDONE = "PERCENTDONE";
    public static final String KEY_CREATION_DATE = "CREATIONDATE";
    public static final String KEY_LAST_MOD = "LASTMOD";
    public static final String KEY_START_DATE = "STARTDATE";
    public static final String KEY_DUE_DATE = "DUEDATE";
    public static final String KEY_DONE_DATE = "DONEDATE";
    public static final String KEY_TASK_ID = "ID";
    public static final String KEY_LIST_ID = "LISTID";
    public static final String KEY_PARENT_ID = "PARENTID";
    public static final String KEY_TAGS = "TAGS";
    public static final String KEY_SUBTASK_COUNT = "SUBTASKCOUNT";
    public static final String KEY_PROJECT_NAME = "PROJECTNAME";
    public static final String KEY_FILE_NAME = "FILENAME";
    public static final String KEY_TAG_NAME = "NAME";

    public static final Uri CONTENT_URI = Uri.parse("content://app.todolist.provider");
    public static final Uri TASK_URI = Uri.withAppendedPath(CONTENT_URI, TASKS_TABLE);
    public static final Uri LIST_URI = Uri.withAppendedPath(CONTENT_URI, LISTS_TABLE);
    public static final Uri TAG_URI = Uri.withAppendedPath(CONTENT_URI, TAGS_TABLE);

    private static final String DATABASE_NAME = "todolist.db";
    private static final int TASKS = 1;
    private static final int TASK_ID =2;
    private static final int LISTS = 3;
    private static final int LIST_ID = 4;
    private static final int TAGS = 5;
    private static final int TAG_ID = 6;

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), TASKS_TABLE, TASKS);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), TASKS_TABLE + "/#", TASK_ID);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), LISTS_TABLE, LISTS);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), LISTS_TABLE + "/#", LIST_ID);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), TAGS_TABLE, TAGS);
        sUriMatcher.addURI(CONTENT_URI.getAuthority(), TAGS_TABLE + "/#", TAG_ID);
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
            "TAGS text, " +
            "SUBTASKCOUNT integer);";

    private static final String CREATE_LISTS_TABLE = "create table " +
            "lists " +
            "(" +
            "_id integer primary key, " +
            "PROJECTNAME text, " +
            "FILENAME text);";

    private static final String CREATE_TAGS_TABLE = "create table " +
            "tags " +
            "(" +
            "_id integer primary key, " +
            "NAME text);";

    private static final class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.beginTransaction();

            try {
                sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
                sqLiteDatabase.execSQL(CREATE_LISTS_TABLE);
                sqLiteDatabase.execSQL(CREATE_TAGS_TABLE);
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
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
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case TASK_ID:
                qBuilder.appendWhere(KEY_ID + "=" + uri.getLastPathSegment());
            case TASKS:
                qBuilder.setTables(TASKS_TABLE);
                break;
            case LIST_ID:
                qBuilder.appendWhere(KEY_ID + "=" + uri.getLastPathSegment());
            case LISTS:
                qBuilder.setTables(LISTS_TABLE);
                break;
            case TAG_ID:
                qBuilder.appendWhere(KEY_ID + "=" + uri.getLastPathSegment());
            case TAGS:
                qBuilder.setTables(TAGS_TABLE);
                break;
            default:
                return null;
        }

        cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String table;
        Uri retUri;

        switch (sUriMatcher.match(uri)) {
            case TASKS:
                table = TASKS_TABLE;
                retUri = TASK_URI;
                break;
            case LISTS:
                table = LISTS_TABLE;
                retUri = LIST_URI;
                break;
            case TAGS:
                table = TAGS_TABLE;
                retUri  = TAG_URI;
                break;
            default:
                return null;
        }

        long rowId = db.insert(table, null, values);
        if (rowId == -1) { // Something error occurred.
            return null;
        } else {
            return Uri.withAppendedPath(retUri, String.valueOf(rowId));
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String whereClause = selection;
        String table;

        switch (sUriMatcher.match(uri)) {
            case TASK_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case TASKS:
                table = TASKS_TABLE;
                break;
            case LIST_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case LISTS:
                table = LISTS_TABLE;
                break;
            case TAG_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case TAGS:
                table = TAGS_TABLE;
                break;
            default:
                return 0;
        }

        return db.delete(table, whereClause, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String whereClause = selection;
        String table;

        switch (sUriMatcher.match(uri)) {
            case TASK_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case TASKS:
                table = TASKS_TABLE;
                break;
            case LIST_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case LISTS:
                table = LISTS_TABLE;
                break;
            case TAG_ID:
                whereClause = KEY_ID + "=" + uri.getLastPathSegment();
            case TAGS:
                table = TAGS_TABLE;
                break;
            default:
                return 0;
        }

        return db.update(table, values, whereClause, selectionArgs);
    }

    private SQLiteOpenHelper mOpenHelper;
}
