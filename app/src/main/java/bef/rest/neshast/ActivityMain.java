package bef.rest.neshast;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityMain extends AppCompatActivity {

    TextView name;
    ImageView profilePicture;
    FloatingActionButton askQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();

    }

    private void initViews() {
        name = (TextView) findViewById(R.id.name);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        profilePicture.post(new Runnable() {
            @Override
            public void run() {
                profilePicture.setImageBitmap(Util.decodeSampledBitmapFromFile(Util.getProfilePicturePath(ActivityMain.this), profilePicture.getWidth(), profilePicture.getHeight()));
//                profilePicture.requestLayout();
            }
        });
        askQuestion = (FloatingActionButton) findViewById(R.id.fab);
        setFonts();
        setTexts();
    }

    private void setTexts() {
        name.setText(Util.getUsersName(this));
    }

    private void setFonts() {
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name);
    }
}
