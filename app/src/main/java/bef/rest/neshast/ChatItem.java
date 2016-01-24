package bef.rest.neshast;

import android.database.Cursor;

/**
 * Created by hojjatimani on 1/24/2016 AD.
 */
public class ChatItem {
    String msg;
    boolean isFromBefrest;
    long time;

    public ChatItem(String text, boolean fromBefrest, long time) {
        this.msg = text;
        this.isFromBefrest = fromBefrest;
        this.time = time;
    }

    public ChatItem() {
    }

    public static ChatItem fromCursor(Cursor cursor) {
        ChatItem item = new ChatItem();
        cursor.getInt(0);
        item.msg = cursor.getString(1);
        item.time = cursor.getLong(2);
        item.isFromBefrest = cursor.getInt(3) == 1;
        return item;
    }
}
