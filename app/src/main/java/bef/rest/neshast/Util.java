package bef.rest.neshast;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EventListener;
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
    private static final String PREF_USER_NAME = "PREF_USER_NAME";
    public static final String PREF_USER_ID = "PREF_USER_ID";
    private static final String PREF_USER_HAS_REGISTERED = "PREF_USER_HAS_REGISTERED";
    public static final String PREF_PROFILE_PICTURE_PATH = "PREF_PROFILE_PICTURE_PATH";
    public static final String PREF_USER_ORGANIZATION = "PREF_USER_ORGANIZATION";
    private static final String IS_FIRST_RUN = "IS_FIRST_RUN";
    private static final String PREF_IS_IMAGE_UPLOADED = "PREF_IS_IMAGE_UPLOADED";
    private static final String PREF_CONTENT_IS_LOCKED = "PREF_CONTENT_IS_LOCKED";
    private static final String PREF_NUMBER_OF_CHAT_MESSAGES = "PREF_NUMBER_OF_CHAT_MESSAGES";
    private static final String PREF_IS_INTRO_BTN_ENABLED = "PREF_IS_INTRO_BTN_ENABLED";
    public static final String BASE_URL = "http://172.17.0.175:6543/meeting/";
    public static final String SIGNAL = "signal";
    public static final String MESSAGE = "message";

    public static final String APP_DATA_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + ".BefrestNeshast";
    public static final String PROFILE_PICTURE_FILE_NAME = "profilePicture";

    private static FontFamily appDefaultFontFamily = FontFamily.IranSans;

    public static void setUserName(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_USER_NAME, userName).commit();
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

    public static void setUsersProfilePicture(Context context, String path) {
        copyFile(path, APP_DATA_DIRECTORY, PROFILE_PICTURE_FILE_NAME + path.substring(path.lastIndexOf('.')));
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_PROFILE_PICTURE_PATH, APP_DATA_DIRECTORY + File.separator + PROFILE_PICTURE_FILE_NAME + path.substring(path.lastIndexOf('.'))).commit();
    }

    public static void setImageUploaded(Context context, boolean uploaded) {
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putBoolean(PREF_IS_IMAGE_UPLOADED, uploaded).commit();
    }

    public static String getUsersName(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getString(PREF_USER_NAME, "");
    }

    public static String getUserOrganization(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getString(PREF_USER_ORGANIZATION, "");
    }

    public static void setUserId(Context context, long userId) {
        SharedPreferences preferences = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        preferences.edit().putLong(PREF_USER_ID, userId).commit();
    }

    public static long getUserId(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getLong(PREF_USER_ID, 0);
    }


    public static boolean userHasRegistered(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getBoolean(PREF_USER_HAS_REGISTERED, false);
    }

    public static void setUserHasRegistered(Context context, boolean hasRegisrered) {
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putBoolean(PREF_USER_HAS_REGISTERED, hasRegisrered).commit();
    }

    public static final String getProfilePicturePath(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getString(PREF_PROFILE_PICTURE_PATH, "");
    }

    public static void setUserOrganization(Context context, String org) {
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putString(PREF_USER_ORGANIZATION, org).commit();
    }

    public static void increaserNumberOfChatMessages(Context context, int n) {
        SharedPreferences prefs = context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE);
        int current = prefs.getInt(PREF_NUMBER_OF_CHAT_MESSAGES, 0);
        prefs.edit().putInt(PREF_NUMBER_OF_CHAT_MESSAGES, current + n).commit();
    }

    public static void addMessageToChatTable(Context context, String msg, boolean isfromBefrest, long time) {
        ContentValues values = new ContentValues();
        values.put(ChatTable.COLUMN_MSG, msg);
        values.put(ChatTable.COLUMN_TIME, System.currentTimeMillis());
        values.put(ChatTable.COLUMN_IS_FROM_BEFREST, isfromBefrest ? 1 : 0);
        context.getContentResolver().insert(ContentProviderChat.CONTENT_URI, values);
        Util.increaserNumberOfChatMessages(context, 1);
    }

    public static int getNumberOfChatMessages(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getInt(PREF_NUMBER_OF_CHAT_MESSAGES, 0);
    }

    static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    public static boolean userHasProfilePicture(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).contains(PREF_PROFILE_PICTURE_PATH);
    }

    public static void setUserData(Context context, String name, String org, String profilePicPath) {
        setUserName(context, name);
        setUserOrganization(context, org);
        if (profilePicPath != null) setUsersProfilePicture(context, profilePicPath);
    }

    public static boolean shouldUploadImage(Context context) {
        return userHasProfilePicture(context) && !isImageUploaded(context);
    }

    private static boolean isImageUploaded(Context context) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getBoolean(PREF_IS_IMAGE_UPLOADED, false);
    }

    public static boolean isContentLocked(Context context, int index) {
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getBoolean(PREF_CONTENT_IS_LOCKED + index, true);
    }

    public static void unlockContent(Context context, int index) {
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putBoolean(PREF_CONTENT_IS_LOCKED + index, false).commit();
    }

    public static void enableIntroductionBtn(Context context){
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putBoolean(PREF_IS_INTRO_BTN_ENABLED, true).commit();
    }

    public static void disableIntroductionBtn(Context context){
        context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).edit().putBoolean(PREF_IS_INTRO_BTN_ENABLED, false).commit();
    }

    public static boolean isIntroductionBtnEnabled(Context context){
        return context.getSharedPreferences(MAIN_PREFRENCES, Context.MODE_PRIVATE).getBoolean(PREF_IS_INTRO_BTN_ENABLED, false);
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

    public static Typeface getTypeFace(Context context, FontFamily fontFamily, FontWeight fontWeight) {
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(filePath, options);
        Bitmap croped = cropToSquare(filePath, src);
//        int dimension = Math.min(src.getWidth(), src.getHeight());
//        Bitmap croped = ThumbnailUtils.extractThumbnail(src, dimension, dimension);
        return getRoundedCornerBitmap(croped, croped.getWidth());
    }

    public static Bitmap cropToSquare(String filePath, Bitmap srcBmp) {
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            if (orientation == 6)
                matrix.postRotate(90);
            else if (orientation == 3)
                matrix.postRotate(180);
            else if (orientation == 8)
                matrix.postRotate(270);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (srcBmp.getWidth() >= srcBmp.getHeight())
            return Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
                    srcBmp.getHeight(), srcBmp.getHeight(), matrix, true);
        else
            return Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(), srcBmp.getWidth(), matrix, true);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void copyFile(String input, String outputPath, String outputName) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d(TAG, "copyFile: out dir made!");
            }


            in = new FileInputStream(input);
            out = new FileOutputStream(outputPath + File.separator + outputName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void showToast(Context c, String m, @Snackbar.Duration int d) {
        Toast toast = Toast.makeText(c, m, d);
//        setFont(c, FontFamily.Default, FontWeight.Regular, toast.getView().findViewById(com.android.internal.R.id.message));
        toast.show();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void alertNoConnection(Context context) {
        showToast(context, "اتصال به اینترنت را بررسی کنید!", Toast.LENGTH_SHORT);
    }

    public static int getActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        Log.d(TAG, "getActionBarSize: EROOOOORRRRRRRR!!!!!!!!!!!!");
        return dpToPx(56);
    }
}
