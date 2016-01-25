package bef.rest.neshast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hojjatimani on 12/22/2015 AD.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";

    public static final String DB_NAME = "chatstable.db";
    public static final int DB_VERSION = 1;

//    private static final String TYPE_TEXT = " msg";
//    public static final String TYPE_INTEGER = " integer";
//    public static final String TYPE_REAL = " real";
//
//    private static final String COMMA_SEP = ", ";
//
//
//    public static final String TABLE_MESSAGES = "messages";
//    public static final String COLUMN_ID = "_id";
//    public static final String COLUMN_MSG = "msg";
//    public static final String COLUMN_TIME = "time";
//    public static final String COLUMN_IS_FROM_BEFREST = "isFromBefrest";
//
//    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_MSG, COLUMN_TIME, COLUMN_IS_FROM_BEFREST};
//
//
//    private static final String CREATE_MESSAGES_TABLE =
//            "create table " + TABLE_MESSAGES + "("
//                    + COLUMN_ID + TYPE_INTEGER + " primary key autoincrement" + COMMA_SEP
//                    + COLUMN_MSG + TYPE_TEXT + " not null" + COMMA_SEP
//                    + COLUMN_TIME + TYPE_INTEGER + COMMA_SEP
//                    + COLUMN_IS_FROM_BEFREST + TYPE_INTEGER + " );";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ChatTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ChatTable.onUpgrade(db, oldVersion, newVersion);
    }

//    public void insertMessage(ChatItem msg) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_MSG, msg.msg);
//        values.put(COLUMN_TIME, msg.time);
//        values.put(COLUMN_IS_FROM_BEFREST, msg.isFromBefrest ? 1 : 0);
//        db.insert(TABLE_MESSAGES, null, values);
//        db.close();
//    }

//    public Message[] getAllMessages() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_MESSAGES, ALL_COLUMNS, null, null, null, null, null);
//        Message[] messages = new Message[cursor.getCount()];
//        cursor.moveToFirst();
//        for (int i = 0; !cursor.isAfterLast(); i++) {
//            messages[i] = cursorToMessage(cursor);
//            cursor.moveToNext();
//        }
//        db.close();
//        return messages;
//    }

//    public Cursor getMessagesCursor() {
//        SQLiteDatabase db = getReadableDatabase();
//        return db.query(TABLE_MESSAGES, ALL_COLUMNS, null, null, null, null, null);
//    }

//    private Message cursorToMessage(Cursor cursor) {
//        Message res = new Message();
//        cursor.getInt(0);
//        res.data = cursor.getString(1);
//        res.serverTime = cursor.getString(2);
//        res.receiveTime = cursor.getLong(3);
//        return res;
//    }
//
//    public Message[] getMessages(long from, long to) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_MESSAGES, ALL_COLUMNS, COLUMN_IS_FROM_BEFREST + " > " + from + " AND " + COLUMN_IS_FROM_BEFREST + " < " + to, null, null, null, COLUMN_TIME);
//        Message[] messages = new Message[cursor.getCount()];
//        cursor.moveToFirst();
//        for (int i = 0; !cursor.isAfterLast(); i++) {
//            messages[i] = cursorToMessage(cursor);
//            cursor.moveToNext();
//        }
//        db.close();
//        return messages;
//    }
//
//    public int getNumberOfAllMessages() {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_MESSAGES, null, null, null, null, null, null);
//        db.close();
//        return cursor.getCount();
//    }
}