package bef.rest.neshast;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import rest.bef.Befrest;
import rest.bef.FileLog;

/**
 * Created by hojjatimani on 1/25/2016 AD.
 */
public class ApplicationLoader extends android.app.Application {
    private static final String TAG = "ApplicationLoader";
    static boolean isChatting = false;

    static final long uId = 10012; //uid
    static String chId;

    static final int xapi_v = 1;
    static final int sdk_v = 1;

    private static final String SHARED_KEY = "23e78b4b-079b-4556-aad0-beded33ed064";
    private static final String API_KEY = "2B907635AB3DBD1F148C35FED5DCA300";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Util.userHasRegistered(this)) {
            startBefrest(this);
        }
    }

    public static void startBefrest(Context context) {
        chId = "" + Util.getUserId(context);
        Log.d(TAG, "startBefrest: chId=" + chId);
        String AUTH = sign(String.format(Locale.US, "/xapi/%d/subscribe/%d/%s/%d", xapi_v, uId, chId, sdk_v));
//        Befrest.init(context, uId, chId, AUTH);
        Befrest.setChId(context, chId);
        Befrest.setUId(context, uId);
        Befrest.setAuth(context, AUTH);
        Befrest.start(context);
    }

    private static String sign(String parameter2) {
        MessageDigest dig = null;
        try {
            dig = MessageDigest.getInstance("md5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String payload = String.format("%s,%s", SHARED_KEY, generateTokenInYourServer(parameter2));

        dig.reset();
        dig.update(payload.getBytes());
        byte[] digest = dig.digest();

        String b64 = Base64.encodeToString(digest, Base64.DEFAULT);
        b64 = b64.replace("+", "-").replace("=", "").replace("/", "_").replace("\n", "");
        FileLog.d("SIGN", b64);
        return b64;
    }

    private static String generateTokenInYourServer(String parameter2) {
        MessageDigest dig = null;
        try {
            dig = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String payload = String.format("%s,%s", API_KEY, parameter2);
        dig.reset();
        dig.update(payload.getBytes());
        byte[] digest = dig.digest();

        String b64 = Base64.encodeToString(digest, Base64.DEFAULT);
        b64 = b64.replace("+", "-").replace("=", "").replace("/", "_").replace("\n", "");
        return b64;
    }

    public static void setIsChatting(boolean isChatting) {
        ApplicationLoader.isChatting = isChatting;
    }
}
