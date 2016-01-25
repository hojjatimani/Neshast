package bef.rest.neshast;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hojjatimani on 1/24/2016 AD.
 */
public class AdapterChat extends CursorRecyclerViewAdapter<AdapterChat.ViewHolder> {
    private static final String TAG = "AdapterChat";
    public AdapterChat(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        ChatItem item = ChatItem.fromCursor(cursor);
        if (item.isFromBefrest) {
            viewHolder.answer.setText(item.msg);
            viewHolder.answer.setVisibility(View.VISIBLE);
            viewHolder.question.setText("");
            viewHolder.question.setVisibility(View.GONE);
        } else {
            viewHolder.question.setText(item.msg);
            viewHolder.question.setVisibility(View.VISIBLE);
            viewHolder.answer.setText("");
            viewHolder.answer.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        Util.setFont(parent.getContext(), Util.FontFamily.Default, Util.FontWeight.Regular, vh.question, vh.answer);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView question;
        public TextView answer;

        public ViewHolder(View view) {
            super(view);
            question = (TextView) view.findViewById(R.id.question);
            answer = (TextView) view.findViewById(R.id.answer);
        }
    }
}
