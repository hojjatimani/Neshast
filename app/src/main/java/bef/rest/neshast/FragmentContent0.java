package bef.rest.neshast;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class FragmentContent0 extends Fragment {
    View parent;
    TextView timer;
    private Date neshastDate;
    Context context;
    CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        parent = inflater.inflate(R.layout.fragment_content_0, null);
        initViews();
        if (countDownTimer == null)
            countDownTimer = new CountDownTimer(getNeshastDate().getTime() - System.currentTimeMillis(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {

                }
            };
        countDownTimer.start();
        return parent;
    }

    private void initViews() {
        timer = (TextView) parent.findViewById(R.id.timer);
        setFont();
    }

    private void setFont() {
        Util.setFont(context, Util.FontFamily.Default, Util.FontWeight.Regular, timer, parent.findViewById(R.id.label));
    }

    private Date getNeshastDate() {
        Date date = new Date();
        date.setHours(date.getHours() + 3);
        date.setMinutes(date.getMinutes() + 30);
        return date;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void updateTimer(long remainingTime) {
        int h = (int) (remainingTime / (3600 * 1000));
        remainingTime -= h * 3600 * 1000;
        int m = (int) (remainingTime / (60 * 1000));
        remainingTime -= m * 60 * 1000;
        int s = (int) (remainingTime / 1000);
        timer.setText(String.format("%d:%02d:%02d", h, m, s));
    }
}
