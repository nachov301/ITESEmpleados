package app.iggy.empleados;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * provider for the Employees app. This is the only class that knows about {@link AppDatabase}
 **/

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //   i use my package name
    static final String CONTENT_AUTHORITY = "app.iggy.empleados";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int EMPLOYEES = 100;
    private static final int EMPLOYEES_ID = 101;

    private static final int USERS = 200;
    private static final int USERS_ID = 201;

    private static UriMatcher buildUriMatcher() {
        Log.d(TAG, "buildUriMatcher: starts");
//        if there's no table names in the uri the match is gonna return NO_MATCH
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

//        e.g. content://app.iggy.employee/Employee
//        if the task table is specified without an id the match is gonna return 100 which is the value we defined previously
        matcher.addURI(CONTENT_AUTHORITY, EmployeeContract.TABLE_NAME, EMPLOYEES);
//        e.g. content://app.iggy.employee/Employee/8
//        if the task table is specified with an id the match is gonna return 101 which is the value we defined previously
        matcher.addURI(CONTENT_AUTHORITY, EmployeeContract.TABLE_NAME + "/#", EMPLOYEES_ID);

        matcher.addURI(CONTENT_AUTHORITY, UserContract.TABLE_NAME, USERS);

        matcher.addURI(CONTENT_AUTHORITY, UserContract.TABLE_NAME + "/#", USERS_ID);

        Log.d(TAG, "buildUriMatcher: ends");
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: uri: " + uri);
//      gonna compare the uri in the method above "private static UriMatcher buildUriMatcher()"
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case EMPLOYEES:
                queryBuilder.setTables(EmployeeContract.TABLE_NAME);
                break;
            case EMPLOYEES_ID:
                queryBuilder.setTables(EmployeeContract.TABLE_NAME);
//            we use long because the id is a long
                long taskId = EmployeeContract.getTaskId(uri);
                queryBuilder.appendWhere(EmployeeContract.Columns._ID + " = " + taskId);
                break;

            case USERS:
                queryBuilder.setTables(UserContract.TABLE_NAME);
                break;
            case USERS_ID:
                queryBuilder.setTables(UserContract.TABLE_NAME);
//            we use long because the id is a long
                long userId = UserContract.getUserId(uri);
                queryBuilder.appendWhere(UserContract.Columns._ID + " = " + userId);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);

        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
//        gonna return a cursor
        Log.d(TAG, "query: returns");
//        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount()); // TODO remove this line

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return EmployeeContract.CONTENT_TYPE;

            case EMPLOYEES_ID:
                return EmployeeContract.CONTENT_ITEM_TYPE;

            case USERS:
                return UserContract.CONTENT_TYPE;

            case USERS_ID:
                return UserContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case EMPLOYEES:
                db = mOpenHelper.getWritableDatabase();
                //returns the id of the inserted row, returns -1 if an error occurred
                recordId = db.insert(EmployeeContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = EmployeeContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case USERS:
                db = mOpenHelper.getWritableDatabase();
                //returns the id of the inserted row, returns -1 if an error occurred
                recordId = db.insert(UserContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = UserContract.buildUserUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (recordId >= 0){
            //means something was inserted, if it couldn't have been inserted record id would be -1
            Log.d(TAG, "insert: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "insert: nothing inserted");
        }

        Log.d(TAG, "exiting insert, returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "delete: match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case EMPLOYEES_ID:
                db = mOpenHelper.getWritableDatabase();
                long employeeId = EmployeeContract.getTaskId(uri);
                selectionCriteria = EmployeeContract.Columns._ID + " = " + employeeId;
                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(EmployeeContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case USERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long userId = UserContract.getUserId(uri);
                selectionCriteria = UserContract.Columns._ID + " = " + userId;
                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(UserContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0){
            //something was deleted
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "delete: nothing deleted");
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case EMPLOYEES_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = EmployeeContract.getTaskId(uri);
                selectionCriteria = EmployeeContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(EmployeeContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            case USERS_ID:
                db = mOpenHelper.getWritableDatabase();
                long userId = UserContract.getUserId(uri);
                selectionCriteria = UserContract.Columns._ID + " = " + userId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }

                count = db.update(UserContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (count > 0){
            //something was updated
            Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "update: nothing updated");
        }

        return count;
    }

    public boolean check_login(String u_name, String u_pwd) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String select = "SELECT * FROM Users WHERE Name ='" + u_name + "' AND Password='" + u_pwd + "'";
        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            Log.d(TAG,"User exits");
            return true;
        }

        if(c!=null) {
            c.close();
        }
        db.close();
        return false;
    }

    public boolean check_user(String u_name) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String select = "SELECT * FROM Users WHERE Name ='" + u_name + "'";
        Cursor c = db.rawQuery(select, null);

        if (c.moveToFirst()) {
            Log.d(TAG,"User exits");
            return true;
        }

        if(c!=null) {
            c.close();
        }
        db.close();
        return false;
    }

}
