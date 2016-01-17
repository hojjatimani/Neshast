package bef.rest.neshast;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sia on 10/3/15.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static final String FONTS_PATH = "fonts/";
    private static final String FONTS_EXTENTION = ".ttf";
    private static Map<String, Typeface> fonts = new HashMap<>();
    private static final String MAIN_PREFRENCES = "MAIN_PREFRENCES";
    private static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    private static final String IS_FIRST_RUN = "IS_FIRST_RUN";

    private static FontFamily appDefaultFontFamily = FontFamily.IranSans;

    public static void setUserName(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(USER_NAME, userName).commit();
    }

    public static String getPhoneId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_FIRST_RUN, true);
    }

    public static void setIsFirstRun(Context context, boolean isFirstRun) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(IS_FIRST_RUN, isFirstRun).commit();
    }


    public static String getUsersName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        return preferences.getString(USER_NAME, "");
    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(USER_ID, userId).commit();
    }


    public static boolean userHasRegistered(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).contains(USER_ID);
    }

    public enum FontFamily {
        IranSans("IranSans"),
        Default(IranSans.toString());

        private String text;

        FontFamily(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum FontWeight {
        Bold("Bold"),
        Regular("Regular");


        private String text;

        FontWeight(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }


    static public void setFont(Context context, FontFamily fontFamily, FontWeight fontWeight, Object... elements) {
        Typeface typeFace = getTypeFace(context, fontFamily, fontWeight);
        setFont(typeFace, elements);
    }

    private static Typeface getTypeFace(Context context, FontFamily fontFamily, FontWeight fontWeight) {
        if (!fonts.containsKey(fontFamily.toString() + fontWeight.toString()))
            fonts.put(fontFamily.toString() + fontWeight.toString(), Typeface.createFromAsset(context.getAssets(), getTypeFacePath(fontFamily, fontWeight)));
        return fonts.get(fontFamily.toString() + fontWeight.toString());
    }

    private static void setFont(Typeface typeface, Object... elements) {
        for (Object element : elements) {
            if (element instanceof TextView)
                ((TextView) element).setTypeface(typeface);
            else if (element instanceof Button)
                ((Button) element).setTypeface(typeface);
            else if (element instanceof ToggleButton)
                ((ToggleButton) element).setTypeface(typeface);
            else
                Log.e(TAG, "invalid input!");
        }
    }

    private static String getTypeFacePath(FontFamily fontFamily, FontWeight fontWeight) {
        return FONTS_PATH + fontFamily.toString() + "-" + fontWeight.toString() + FONTS_EXTENTION;
    }

    public static void setText(Object elem, String text) {
        if (elem instanceof TextView)
            ((TextView) elem).setText(text);
        else if (elem instanceof Button)
            ((Button) elem).setText(text);
        else if (elem instanceof ToggleButton)
            ((ToggleButton) elem).setText(text);
        else
            Log.e(TAG, "invalid input!");
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1) {
        setText(elem0, text0);
        setText(elem1, text1);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2, Object elem3, String text3) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2, elem3, text3);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2, Object elem3, String text3, Object elem4, String text4) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2, elem3, text3, elem4, text4);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2, Object elem3, String text3, Object elem4, String text4, Object elem5, String text5) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2, elem3, text3, elem4, text4, elem5, text5);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2, Object elem3, String text3, Object elem4, String text4, Object elem5, String text5, Object elem6, String text6) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2, elem3, text3, elem4, text4, elem5, text5, elem6, text6);
    }

    public static void setText(Object elem0, String text0, Object elem1, String text1, Object elem2, String text2, Object elem3, String text3, Object elem4, String text4, Object elem5, String text5, Object elem6, String text6, Object elem7, String text7) {
        setText(elem0, text0);
        setText(elem1, text1, elem2, text2, elem3, text3, elem4, text4, elem5, text5, elem6, text6, elem7, text7);
    }

    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
