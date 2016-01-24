package bef.rest.neshast;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ActivityChat extends AppCompatActivity {
    private static final String TAG = "ActivityChat";
    TextView name;
    ImageView profilePicture;
    EditText msg;
    ImageButton send;
    AdapterChat chatAdapter;
    RecyclerView chat;
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        db = new MyDatabaseHelper(this);
//        db.insertMessage(new ChatItem("بفرست یک توضیح مفصلی درباره فلان و بیسار در نحوه لورم ایپسوم کردنش میدهد", true, System.currentTimeMillis()));
//        db.insertMessage(new ChatItem("اوکی :)", false, System.currentTimeMillis()));
//        db.insertMessage(new ChatItem("قول میدم بیام نشست", false, System.currentTimeMillis()));

//        for (int i = 0; i < 1000; i++) {
//            db.insertMessage(new ChatItem("باشه بابا بیا ", true, System.currentTimeMillis()));
//        }
        chatAdapter = new AdapterChat(this, db.getMessagesCursor());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
//        llm.setReverseLayout(true);
        chat.setLayoutManager(llm);
        chat.setAdapter(chatAdapter);

    }

    private void initViews() {
        name = (TextView) findViewById(R.id.lable_name);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        profilePicture.post(new Runnable() {
            @Override
            public void run() {
                if (Util.userHasProfilePicture(ActivityChat.this))
                    profilePicture.setImageBitmap(Util.decodeSampledBitmapFromFile(Util.getProfilePicturePath(ActivityChat.this), profilePicture.getWidth(), profilePicture.getHeight()));
//                profilePicture.requestLayout();
            }
        });
        msg = (EditText) findViewById(R.id.msg);
        send = (ImageButton) findViewById(R.id.send);
        chat = (RecyclerView) findViewById(R.id.chat);
        send.setEnabled(false);
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (msg.getText().toString().trim().length() > 0)
                    send.setEnabled(true);
                else send.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        animateSendPanel();
        setFonts();
        setTexts();
    }

    private void setTexts() {
        name.setText(Util.getUsersName(this));
    }

    private void setFonts() {
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name, msg);
    }

    private void animateSendPanel() {
//        final View sendPanel = findViewById(R.id.sendPanel);
//        sendPanel.post(new Runnable() {
//            @Override
//            public void run() {
//                int screenHeight = Util.getScreenHeight(ActivityChat.this);
//                int vHeight = sendPanel.getHeight();
//                sendPanel.setY(screenHeight);
//                Log.d(TAG, "animateSendPanel: screenH" + screenHeight + "  vH" + vHeight);
//                sendPanel.animate().translationY(screenHeight - vHeight);
//            }
//        });
    }

    public void onViewClicked(View view) {
        int vId = view.getId();
        if (vId == send.getId()) {
            db.insertMessage(new ChatItem(msg.getText().toString(), false, System.currentTimeMillis()));
            msg.setText("");
            Cursor messagesCursor = db.getMessagesCursor();
            chatAdapter.changeCursor(messagesCursor);
//            chatAdapter.notifyDataSetChanged();
            chat.smoothScrollToPosition(messagesCursor.getCount());
        }
    }
}
