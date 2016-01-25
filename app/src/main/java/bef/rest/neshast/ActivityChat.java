package bef.rest.neshast;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityChat extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ActivityChat";
    TextView name;
    ImageView profilePicture;
    EditText msg;
    ImageButton send;
    AdapterChat chatAdapter;
    RecyclerView chat;
    ContentObserver observer;
    TextView loadMore;
    SwipeRefreshLayout refreshLayout;

    int loadBlockSize = 100;
    int numberOfItemsInList = loadBlockSize;

    int prevNumerOfchats = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationLoader.setIsChatting(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        chat.setLayoutManager(llm);
        fillData();

        ((ScrollView) findViewById(R.id.background)).setEnabled(false);
        observer = new chatsObserver(new Handler());
        getContentResolver().registerContentObserver(ContentProviderChat.CONTENT_URI, true, observer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationLoader.setIsChatting(false);
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
        loadMore = (TextView) findViewById(R.id.loadMore);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
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
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean moreItemsExist = Util.getNumberOfChatMessages(ActivityChat.this) > numberOfItemsInList;
                Log.d(TAG, "onRefresh: moreItesmExist:" + moreItemsExist);
                if (moreItemsExist) {
                    prevNumerOfchats = chatAdapter.getItemCount();
                    numberOfItemsInList += loadBlockSize;
                    getSupportLoaderManager().restartLoader(0, null, ActivityChat.this);
                } else {
                    refreshLayout.setRefreshing(false);
                    Util.showToast(ActivityChat.this, "پیام دیگری نیست!", Toast.LENGTH_SHORT);
                }
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
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name, msg, loadMore);
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
            Util.addMessageToChatTable(this, msg.getText().toString(), false, System.currentTimeMillis());
            msg.setText("");
        }
    }


    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{ChatTable.COLUMN_MSG};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.label};

        getSupportLoaderManager().initLoader(0, null, this);
        chatAdapter = new AdapterChat(this, null);
//        adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
//                to, 0);
//
        chat.setAdapter(chatAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationLoader.setIsChatting(false);
        if (observer != null)
            getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        String[] projection = {ChatTable.COLUMN_ID, ChatTable.COLUMN_MSG, ChatTable.COLUMN_TIME, ChatTable.COLUMN_IS_FROM_BEFREST};
        CursorLoader cursorLoader = new CursorLoader(this,
                ContentProviderChat.CONTENT_URI, projection, null, null, "ROWID DESC limit " + numberOfItemsInList);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        chatAdapter.changeCursor(data);
        int itemCount = chatAdapter.getItemCount();
        if (prevNumerOfchats > -1) {
            int scroll = itemCount - prevNumerOfchats;
            if (scroll < 0) scroll = 0;
            chat.scrollToPosition(scroll);
            prevNumerOfchats = -1;
        } else chat.scrollToPosition(itemCount - 1);
//        chat.post(new Runnable() {
//            @Override
//            public void run() {
//                chat.getLayoutManager().scrollToPosition(itemCount);
//            }
//        });
        Log.d(TAG, "onLoadFinished: itemCount=" + itemCount);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
//        chatAdapter.changeCursor(null);
    }

    class chatsObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public chatsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "onChange: Observer!");
        }
    }
}
