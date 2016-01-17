package bef.rest.neshast;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ActivityMain extends Activity {
    TextView timer;
    RelativeLayout signUpPanel;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        new CountDownTimer(getNeshastDate().getTime() - System.currentTimeMillis(), 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void initViews() {
        timer = (TextView) findViewById(R.id.timer);
        final View label = findViewById(R.id.label_timeRemaing);
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, timer, label);
        signUpPanel = (RelativeLayout) findViewById(R.id.signUpPanel);
        next = (Button) findViewById(R.id.next);

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.setVisibility(View.GONE);
                label.setVisibility(View.GONE);
                signUpPanel.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
            }
        });
    }

    private Date getNeshastDate(){
        Date date = new Date();
        date.setHours(date.getHours() + 3);
        date.setMinutes(date.getMinutes() + 30);
        return date;
    }

    private void updateTimer(long remainingTime){
        int h = (int)(remainingTime / (3600 * 1000));
        remainingTime -= h * 3600 * 1000;
        int m = (int)(remainingTime / (60 * 1000));
        remainingTime -= m * 60 * 1000;
        int s = (int)(remainingTime / 1000);
        timer.setText(String.format("%02d:%02d:%02d", h, m, s));
    }
}
