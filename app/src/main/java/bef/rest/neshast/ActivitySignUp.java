package bef.rest.neshast;

import android.animation.Animator;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rest.bef.Befrest;
import rest.bef.BefrestMessage;
import rest.bef.BefrestPushReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivitySignUp extends FragmentActivity {
    private static final String TAG = "ActivitySignUp";

    Button next;
    ImageView profilePicture;
    EditText name;
    EditText organization;
    String profilePicPath;
    ImageView befrestIcon;
    TextView nameLabel;
    RelativeLayout content;
    AdapterPager adapterPager;
    DynamicPushReceiver dPushReceiver;

    ViewPager pager;
    FloatingActionButton chat;

    private static final int animDurarion = 1000;
    private static final int shortAnimDuration = 500;
    public static final int CHOOSE_PIC_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.userHasRegistered(this)) {
            setContentView(R.layout.activity_main);
            initViews();
            addContent();
            initContentViews();
            uploadImageIfNeeded();
            setUserName();
        } else {
            setContentView(R.layout.activity_sign_up);
            initViews();
            initLoginViews();
        }
        if (Util.userHasProfilePicture(this)) setUserProfilePicture();
        if (!Util.isNetworkAvailable(this)) Util.alertNoConnection(this);
        setFonts();

        Befrest.registerPushReceiver(this, (dPushReceiver = new DynamicPushReceiver()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapterPager != null) adapterPager.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dPushReceiver != null) Befrest.unregisterPushReceiver(this, dPushReceiver);
    }

    private void initViews() {
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        befrestIcon = (ImageView) findViewById(R.id.befrestIcon);
        nameLabel = (TextView) findViewById(R.id.lable_name);

        profilePicture.post(new Runnable() {
            @Override
            public void run() {
                profilePicture.getLayoutParams().width = profilePicture.getHeight();
                profilePicture.requestLayout();
            }
        });
    }

    private void initLoginViews() {
        next = (Button) findViewById(R.id.next);
        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
        name.setText(Util.getUsersName(ActivitySignUp.this));
        organization.setText(Util.getUserOrganization(ActivitySignUp.this));
    }

    private void initContentViews() {
        pager = (ViewPager) content.findViewById(R.id.pager);
        chat = (FloatingActionButton) content.findViewById(R.id.chat);
        adapterPager = new AdapterPager(getSupportFragmentManager());
        pager.setAdapter(adapterPager);
        final ViewPagerDots dots = (ViewPagerDots) findViewById(R.id.dots);
        dots.setNumberOfDots(this, adapterPager.getCount());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                dots.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setFonts() {
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name, organization, next,
                findViewById(R.id.oddrunName), nameLabel, findViewById(R.id.oddrunSite));
    }

    private void signUpUser() {
        String name = this.name.getText().toString();
        String org = organization.getText().toString();
        if (name == null || name.length() < 1 || org == null || org.length() < 1)
            Util.showToast(this, "لطفا نام و سازمان خود را به درستی وارد نمایید!", Toast.LENGTH_SHORT);
        else {
            Util.setUserData(this, name, org, profilePicPath);
            if (!Util.isNetworkAvailable(this))
                Util.alertNoConnection(this);
            else {
                registerUserOnServer(name, org);
            }
        }
    }

    private void registerUserOnServer(String name, String org) {
        Log.d(TAG, "registerUserOnServer: ");
        disableNextBtn();
        RetrofitService retrofit = new Retrofit.Builder()
                .baseUrl(Util.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService.class);
        Call<POJOs.RegisterResponse> regRes = retrofit.registerUser(name, org);
        regRes.enqueue(new Callback<POJOs.RegisterResponse>() {
            @Override
            public void onResponse(Response<POJOs.RegisterResponse> response) {
                Log.d(TAG, "onResponse:  isSuccess: " + response.isSuccess() + "   errorCode: " + response.body().errorCode + "     uid: " + response.body().entity.uid);
                if (response.isSuccess())
                    if (response.body().errorCode == 0) {
                        Util.setUserId(ActivitySignUp.this, response.body().entity.uid);
                        Util.setUserHasRegistered(ActivitySignUp.this, true);
                        ApplicationLoader.startBefrest(ActivitySignUp.this);
                        animateUi();
                        uploadImageIfNeeded();
                        return;
                    }
                enableNextBtn();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure");
                t.printStackTrace();
                Toast.makeText(ActivitySignUp.this, "مشکل در ارسال اطلاعات!", Toast.LENGTH_LONG).show();
                enableNextBtn();
            }
        });
    }

    private void enableNextBtn() {
        next.setText("ارسال اطلاعات");
        next.setEnabled(true);
    }

    private void disableNextBtn() {
        next.setText("در حال ارتباط ...");
        next.setEnabled(false);
    }

    private void uploadImageIfNeeded() {
        if (!Util.shouldUploadImage(ActivitySignUp.this))
            return;
        String imgPath = Util.getProfilePicturePath(ActivitySignUp.this);
        MediaType mediaType = MediaType.parse("image/" + imgPath.substring(imgPath.lastIndexOf('.')));
        RetrofitService retrofit = new Retrofit.Builder()
                .baseUrl(Util.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService.class);
        Call<POJOs.UpdateResponse> upRes = retrofit.uploadImage(Util.getUserId(ActivitySignUp.this), RequestBody.create(mediaType, new File(imgPath)));
        upRes.enqueue(new Callback<POJOs.UpdateResponse>() {
            @Override
            public void onResponse(Response<POJOs.UpdateResponse> response) {
                Log.d(TAG, "onResponse (upload): errCode" + response.body().errorCode);
                if (response.isSuccess())
                    if (response.body().errorCode == 0)
                        Util.setImageUploaded(ActivitySignUp.this, true);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure: upload");
                t.printStackTrace();
            }
        });
    }

    private Uri outputFileUri;

    private void openImageIntent() {
//
//// Determine Uri of camera image to save.
//        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
//        root.mkdirs();
//        final String fname = System.currentTimeMillis() + ".jpg";
//        final File sdImageMainDirectory = new File(root, fname);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
//
//        // Camera.
//        final List<Intent> cameraIntents = new ArrayList<Intent>();
//        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        final PackageManager packageManager = getPackageManager();
//        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for (ResolveInfo res : listCam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//
//        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
//
//        // Add the camera options.
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
//
//        startActivityForResult(chooserIntent, CHOOSE_PIC_CODE);


        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1000);//one can be replaced with any action code
    }

    public boolean hasImageCaptureBug() {

        // list of known devices that have the bug
        ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");

        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
                + android.os.Build.DEVICE);

    }

    private void takeImageWithCamera() {
//        Intent cameraIntent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, 1001);

        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (hasImageCaptureBug()) {
            Log.d(TAG, "takeImageWithCamera: hasBug");
            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
            Util.showToast(ActivitySignUp.this, "مشکلی پیش آمده! لطفا تصویر خود را از گالری انتخاب کنید.", Toast.LENGTH_LONG);
        } else {
            Log.d(TAG, "takeImageWithCamera: has not bug");
//            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Util.getProfilePicturePath(this))));
            startActivityForResult(i, 1001);
        }
//        File photo = null;
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        if (android.os.Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED)) {
//            photo = new File(android.os.Environment
//                    .getExternalStorageDirectory(), FILE_NAME);
//        } else {
//            photo = new File(getCacheDir(), FILE_NAME);
//        }
//        if (photo != null) {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//            selectedImageUri = Uri.fromFile(photo);
//            startActivityForResult(intent, 1001);
//        }
    }

    private void pickImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1000);//one can be replaced with any action code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
//                imageview.setImageURI(selectedImage);
                Util.setUsersProfilePicture(ActivitySignUp.this, selectedImage);
                setUserProfilePicture();
            }
        } else if (requestCode == 1001) {
            Log.d(TAG, "onActivityResult: camrea");
            if (resultCode == RESULT_OK) {
//                Log.d(TAG, "onActivityResult: sucsess");
//                Uri u = null;
//                if (hasImageCaptureBug()) {
//                    Log.d(TAG, "onActivityResult: hasbug");
//                    File fi = new File("/sdcard/tmp");
//                    try {
//                        u = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null, null));
//                        if (!fi.delete()) {
//                            Log.i("logMarker", "Failed to delete " + fi);
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d(TAG, "onActivityResult: has not bug");
//                    u = data.getData();
//                }
//                Util.setUsersProfilePicture(ActivitySignUp.this, data.getData());
//                if(hasImageCaptureBug()){
////                    Toast.makeText(ActivitySignUp.this, "مشکلی وجود")
//                }else {
//
//                }
                Util.userSetPictureWithCamera(this);
                setUserProfilePicture();
            }
        }
//        if (requestCode == CHOOSE_PIC_CODE) {
//            if (resultCode == RESULT_OK) {
//                final boolean isCamera;
//                if (data == null) {
//                    isCamera = true;
//                } else {
//                    final String action = data.getAction();
//                    if (action == null) {
//                        isCamera = false;
//                    } else {
//                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    }
//                }
//
//                Uri selectedImageUri;
//                if (isCamera) {
//                    selectedImageUri = outputFileUri;
//                } else {
//                    selectedImageUri = data == null ? null : data.getData();
//                }
////                profilePicPath = Util.getRealPathFromURI(this, selectedImageUri);
////                profilePicture.setImageBitmap(Util.decodeSampledBitmapFromFile(profilePicPath, profilePicture.getWidth(), profilePicture.getHeight()));
////                Util.setUsersProfilePicture();
//
//                Util.setUsersProfilePicture(ActivitySignUp.this, selectedImageUri);
//                setUserProfilePicture();
//            }
//        }
    }

    private void animateUi() {
        animateProfilePicture();
        animateBefrestIcon();
        animateHeader();
        animateBackground();
        animateLoginPanel();
//        content = (RelativeLayout) getLayoutInflater().inflate(R.layout.content, null);
//        content.setAlpha(0);
//        ((ViewGroup) findViewById(R.id.contentHolder)).addView(content);
//        content.animate().setDuration(animDurarion).alpha(1);


//        BounceInterpolator bounceInterpolator = new BounceInterpolator();
//        ObjectAnimator anim = ObjectAnimator.ofFloat(profilePicture, "translationY", 0f, -200 );
//        anim.setInterpolator(bounceInterpolator);
//        anim.setDuration(1100).start();
    }

    private void animateLoginPanel() {
        findViewById(R.id.loginPanel)
                .animate().setDuration(animDurarion).translationX(Util.getScreenWidth(this));
    }

    private void animateBackground() {
//        int colorFrom = getResources().getColor(R.color.befrestBademjooni);
//        int colorTo = getResources().getColor(R.color.lightGray);
//        final View background = findViewById(R.id.background);
//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo).setDuration(animDurarion);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                background.setBackgroundColor((int) animator.getAnimatedValue());
//            }
//
//        });
//        colorAnimation.start();
        findViewById(R.id.background).animate().setDuration(animDurarion).alpha(0);
    }

    private void animateHeader() {
        int actionBarSize = getActionBarSize();

        View header = findViewById(R.id.header);
        header.animate().setDuration(animDurarion).translationY((-1f) * (header.getHeight() - actionBarSize));
    }

    private void animateProfilePicture() {
        profilePicture.setClickable(false);
        int actionBarSize = getActionBarSize();
        int screenWidth = Util.getScreenWidth(this);
        int width = profilePicture.getWidth();
        int height = profilePicture.getHeight();
        int right = profilePicture.getRight();
        int top = profilePicture.getTop();
        int padding = getResources().getDimensionPixelSize(R.dimen.space_small);
        float scaleXBy = (actionBarSize - 2 * padding) / (float) width;
        float scaleYBy = (actionBarSize - 2 * padding) / (float) height;
        profilePicture.animate()
                .setDuration(animDurarion)
                .scaleX(scaleXBy)
                .scaleY(scaleYBy)
                .translationX(screenWidth - right + (width - width * scaleXBy) / 2 - padding)
                .translationY((-1f) * (top + (height - height * scaleYBy) / 2) + padding);
    }

    private void animateBefrestIcon() {
        int left = befrestIcon.getLeft();
        int top = befrestIcon.getTop();
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_small);
        befrestIcon.animate()
                .setDuration(animDurarion)
                .translationX((-1f) * (left - padding))
                .translationY((-1f) * (top - padding))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        nameLabel.animate().setDuration(shortAnimDuration).alpha(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        nameLabel.setY((getActionBarSize() - nameLabel.getHeight()) / 2);
                        setUserName();
                        addContent();
                        initContentViews();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    private void addContent() {
        content = (RelativeLayout) getLayoutInflater().inflate(R.layout.content, null);
        content.setAlpha(0);
        ((ViewGroup) findViewById(R.id.contentHolder)).addView(content);
        content.animate().setDuration(shortAnimDuration).alpha(1);
    }

    private void setUserName() {
        nameLabel.setText(Util.getUsersName(this));
        nameLabel.requestLayout();
        nameLabel.animate().setDuration(shortAnimDuration).alpha(1);
    }

    private void setUserProfilePicture() {
        profilePicture.post(new Runnable() {
            @Override
            public void run() {
                profilePicture.setImageBitmap(Util.decodeSampledBitmapFromFile(Util.getProfilePicturePath(ActivitySignUp.this), profilePicture.getWidth(), profilePicture.getHeight()));
            }
        });
    }

    private int getActionBarSize() {
        return findViewById(R.id.actionBarSize).getHeight();
    }

//    private void showAskDialog() {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_ask);
//        dialog.setCancelable(false);
//        TextView title = (TextView) dialog.findViewById(R.id.title);
//        final EditText question = (EditText) dialog.findViewById(R.id.edit_text);
//        question.setText(tempQuestion);
//        TextView okBTN = (TextView) dialog.findViewById(R.id.ok);
//        TextView cancelBTN = (TextView) dialog.findViewById(R.id.cancel);
//        Util.setFont(ActivitySignUp.this, Util.FontFamily.Default, Util.FontWeight.Regular, title, question, okBTN, cancelBTN);
//        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.show();
//        okBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tempQuestion = question.getText().toString();
//                sendQuestion(tempQuestion);
//                dialog.dismiss();
//            }
//
//        });
//        cancelBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }
//

    private void showGalleryOrCameraDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_gallery_camera);
        dialog.setCancelable(true);
        View gallery = dialog.findViewById(R.id.gallery);
        View camera = dialog.findViewById(R.id.camera);
        Util.setFont(ActivitySignUp.this, Util.FontFamily.Default, Util.FontWeight.Regular, camera, gallery);
        dialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
                dialog.dismiss();
            }

        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageWithCamera();
                dialog.dismiss();
            }
        });
    }

    private class DynamicPushReceiver extends BefrestPushReceiver {

        @Override
        public void onPushReceived(Context context, BefrestMessage[] messages) {
            for (BefrestMessage message : messages) {
                JSONObject jsObject = null;
                try {
                    jsObject = new JSONObject(message.getData());
                    switch (jsObject.getString(Util.SIGNAL)) {
                        case "1":
                            Util.enableIntroductionBtn(context);
                            updatePager();
                            break;
                        case "2":
                            Util.disableIntroductionBtn(context);
                            updatePager();
                            break;
                        case "3":
                        case "4":
                        case "5":
                        case "100":
                            break;
                        default:
                            Util.unlockContent(context, Integer.valueOf(jsObject.getString(Util.SIGNAL)) % 10);
                            updatePager();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updatePager() {
        if (pager != null) {
            int curr = pager.getCurrentItem();
            pager.setAdapter(adapterPager);
            pager.setCurrentItem(curr, false);
        }
    }

    public void onViewClicked(View view) {
        int vId = view.getId();
        if (next != null && (vId == next.getId())) {
            signUpUser();
        } else if (vId == profilePicture.getId()) {
//            openImageIntent();
            showGalleryOrCameraDialog();
        } else if (chat != null && (vId == chat.getId())) {
            if (Util.isNetworkAvailable(this))
                startActivity(new Intent(ActivitySignUp.this, ActivityChat.class));
            else Util.alertNoConnection(this);
        }
    }
}