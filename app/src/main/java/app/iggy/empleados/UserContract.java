package app.iggy.empleados;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static app.iggy.empleados.AppProvider.CONTENT_AUTHORITY;
import static app.iggy.empleados.AppProvider.CONTENT_AUTHORITY_URI;

public class UserContract {

    static final String TABLE_NAME = "Users";

    //user fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String USERS_NAME = "Name";
        public static final String USERS_PASSWORD= "Password";

        private Columns(){
//            private constructor to prevent instantiation
        }
    }

    /**
     * the URI to access the Users Table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    static Uri buildUserUri(long employeeId){
        return ContentUris.withAppendedId(CONTENT_URI, employeeId);
    }

    static long getUserId(Uri uri){
        return ContentUris.parseId(uri);
    }

}
