package guardian.dns.com.guardian.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocationDataContract.TrackEntry.TABLE_NAME + " (" +
                    LocationDataContract.TrackEntry._ID + " INTEGER PRIMARY KEY," +
                    LocationDataContract.TrackEntry.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    LocationDataContract.TrackEntry.COLUMN_NAME_LON + REAL_TYPE + COMMA_SEP +
                    LocationDataContract.TrackEntry.COLUMN_NAME_TIME + TEXT_TYPE +

            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocationDataContract.TrackEntry.TABLE_NAME;

    public LocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}