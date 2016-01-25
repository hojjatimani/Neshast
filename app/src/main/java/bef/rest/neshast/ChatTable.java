package bef.rest.neshast;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by hojjatimani on 1/24/2016 AD.
 */
public class ChatTable {


    private static final String TYPE_TEXT = " msg";
    public static final String TYPE_INTEGER = " integer";
    public static final String TYPE_REAL = " real";

    private static final String COMMA_SEP = ", ";



    // Database table
    public static final String TABLE_CHATS = "chats";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MSG = "msg";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_IS_FROM_BEFREST = "isFromBefrest";
    // Database creation SQL statement
    private static final String CREATE_CHATS_TABLE =
            "create table " + TABLE_CHATS + "("
                    + COLUMN_ID + TYPE_INTEGER + " primary key autoincrement" + COMMA_SEP
                    + COLUMN_MSG + TYPE_TEXT + " not null" + COMMA_SEP
                    + COLUMN_TIME + TYPE_INTEGER + COMMA_SEP
                    + COLUMN_IS_FROM_BEFREST + TYPE_INTEGER + " );";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_CHATS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ChatTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        onCreate(database);
    }

}
