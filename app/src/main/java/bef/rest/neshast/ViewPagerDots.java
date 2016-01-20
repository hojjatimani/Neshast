package bef.rest.neshast;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class ViewPagerDots extends LinearLayout {
    private static final String TAG = "ViewPagerDots";
    int current = 0;
    LinearLayout dots;
    private static final int animDuration = 100;
    public static final float deSelectedScale = 0.5f;


    public ViewPagerDots(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        dots = (LinearLayout) inflater.inflate(R.layout.view_pager_dots, this);
        selectDot(current);
    }

    private void addDot(Context context, int count) {
        for (int i = 0; i < count; i++) {
            ImageView newDot = new ImageView(context);
            newDot.setImageResource(R.drawable.dot);
            newDot.setScaleX(deSelectedScale);
            newDot.setScaleY(deSelectedScale);
            dots.addView(newDot);
        }
        dots.requestLayout();
    }

    private void removeDots(int count) {
        dots.removeViews(0, count);
        dots.requestLayout();
    }

    public void setNumberOfDots(Context context, int count) {
        int currentCount = dots.getChildCount();
        if (currentCount == count)
            return;
        if (currentCount > count)
            removeDots(currentCount - count);
        else
            addDot(context, count - currentCount);
    }


    public void selectDot(int position) {
        Log.d(TAG, "selectDot: position - >" + position);
        if (position >= dots.getChildCount()) {
            throw new IllegalArgumentException("position out of bound!");
        }
        if (position != current) {
            deSelectCurrent();
        }
        current = position;
        View dot = dots.getChildAt(position);
        dot.animate().setDuration(animDuration).scaleY(1f).scaleX(1f);
    }

    private void deSelectCurrent() {
        Log.d(TAG, "deSelectCurrent: " + current);
        View dot = dots.getChildAt(current);
        dot.animate().setDuration(animDuration).scaleX(deSelectedScale).scaleY(deSelectedScale);
    }
}
