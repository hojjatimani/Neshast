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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class FragmentContent0 extends Fragment {
    View parent;
    TextView timer;
    Context context;
    CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        parent = inflater.inflate(R.layout.fragment_content_0, null);
        initViews();
//        final long neshastTime = getNeshastDate().getTime();
        final long neshastTime = System.currentTimeMillis() + 10000;
        final long now = System.currentTimeMillis();
        if (now > neshastTime) {
            parent.findViewById(R.id.label).setVisibility(View.GONE);
            timer.setText("نشست آغاز شد!");
        } else {
            if (countDownTimer == null)
                countDownTimer = new CountDownTimer(neshastTime - now, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateTimer(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        parent.findViewById(R.id.label).setVisibility(View.GONE);
                        timer.setText("نشست آغاز شد!");
                    }
                };
            countDownTimer.start();
        }
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
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date res = new Date();
        try {
            res = formater.parse("2016-01-28_09-00-00");
        } catch (ParseException e) {

        }
        return res;
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
