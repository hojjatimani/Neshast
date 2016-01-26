package bef.rest.neshast;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hojjatimani on 1/19/2016 AD.
 */
public class FragmentContent1 extends Fragment {
    private static final String TAG = "FragmentContent1";
    View parent;
    Context context;

    ImageButton btn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = inflater.getContext();
//        if (Util.isContentLocked(context, 1))
//            parent = inflater.inflate(R.layout.fragment_lock, null);
//        else {
        parent = inflater.inflate(R.layout.fragment_content_1, null);
        initViews();
        if (Util.isIntroductionBtnEnabled(context))
            btn.setEnabled(true);
        else btn.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNetworkAvailable(context)) {
                    RetrofitService retrofit = new Retrofit.Builder()
                            .baseUrl(Util.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(RetrofitService.class);
                    Call<POJOs.AskResponse> askResponse = retrofit.showMe(Util.getUserId(context));
                    askResponse.enqueue(new Callback<POJOs.AskResponse>() {
                        @Override
                        public void onResponse(Response<POJOs.AskResponse> response) {
                            Log.d(TAG, "onResponse (Show me): errCode" + response.body().errorCode);
                            Toast.makeText(context, "موفق", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d(TAG, "onFailure: Show me");
                            t.printStackTrace();
                            Toast.makeText(context, "ناموفق", Toast.LENGTH_LONG).show();
                        }
                    });
                } else Util.alertNoConnection(context);
            }
        });
//        }
        return parent;
    }

    private void initViews() {
        btn = (ImageButton) parent.findViewById(R.id.btn);
        setFont();
    }

    private void setFont() {
        Util.setFont(context, Util.FontFamily.Default, Util.FontWeight.Regular, parent.findViewById(R.id.label));
    }
}
