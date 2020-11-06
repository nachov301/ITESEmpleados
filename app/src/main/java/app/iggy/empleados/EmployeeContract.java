package app.iggy.empleados;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
//imports the constants or statics that we defined in our AppProvider class
import static app.iggy.empleados.AppProvider.CONTENT_AUTHORITY;
import static app.iggy.empleados.AppProvider.CONTENT_AUTHORITY_URI;



public class EmployeeContract {

    static final String TABLE_NAME = "Employees";

//    Employee fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String EMPLOYEES_NAME = "Name";
        public static final String EMPLOYEES_DESCRIPTION = "Description";
        public static final String EMPLOYEES_MAIL = "Mail";
        public static final String EMPLOYEES_ADDRESS = "Address";
        public static final String EMPLOYEES_PHONENUMBER = "PhoneNumber";
        public static final String EMPLOYEES_SORTORDER = "SortOrder";

        private Columns(){
//            private constructor to prevent instantiation
    }
}

/**
 * the URI to access the Employee Table
 */
public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

static Uri buildTaskUri(long employeeId){
    return ContentUris.withAppendedId(CONTENT_URI, employeeId);
}

static long getTaskId(Uri uri){
    return ContentUris.parseId(uri);
}
}
