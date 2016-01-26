package bef.rest.neshast;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class FragmentContent4 extends Fragment {
    View parent;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
        if (Util.isContentLocked(context, 4))
            parent = inflater.inflate(R.layout.fragment_lock, null);
        else {
            parent = inflater.inflate(R.layout.fragment_content_4, null);
            initViews();
        }
        return parent;
    }

    private void initViews() {
        setFont();
    }

    private void setFont() {
        Util.setFont(context, Util.FontFamily.Default, Util.FontWeight.Regular, parent.findViewById(R.id.label), parent.findViewById(R.id.befrest));
    }
}