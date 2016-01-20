package bef.rest.neshast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class AdapterPager extends FragmentPagerAdapter {
    int count = 5;
    public AdapterPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentContent0();
            case 1:
                return new FragmentContent1();
            case 2:
                return new FragmentContent2();
            case 3:
                return new FragmentContent3();
            case 4:
                return new FragmentContent4();

        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
