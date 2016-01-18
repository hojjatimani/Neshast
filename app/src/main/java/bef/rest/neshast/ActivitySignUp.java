package bef.rest.neshast;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivitySignUp extends Activity {
    Button next;
    ImageView profilePicture;
    EditText name;
    EditText organization;
    String profilePicPath;
    ImageView befrestIcon;
    private static final String TAG = "ActivitySignUp";
    public static final int CHOOSE_PIC_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);
        initViews();
    }

    private void initViews() {
        next = (Button) findViewById(R.id.next);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
        befrestIcon = (ImageView) findViewById(R.id.befrestIcon);
        setFonts();
        setSize();
        setTexts();
        //set Profile Picture Size
    }

    private void setListeners() {
    }

    private void setSize() {
        final FrameLayout profilePictureContainer = (FrameLayout) findViewById(R.id.profilePictureContainer);
        profilePictureContainer.post(new Runnable() {
            @Override
            public void run() {
                profilePicture.getLayoutParams().height = profilePictureContainer.getHeight();
                profilePicture.getLayoutParams().width = profilePictureContainer.getHeight();
                profilePicture.requestLayout();
                profilePicture.setImageBitmap(Util.getRoundedCornerBitmap(((BitmapDrawable) profilePicture.getDrawable()).getBitmap(), profilePictureContainer.getWidth()));
            }
        });
    }

    private void setFonts() {
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name, organization, next,
                findViewById(R.id.oddrunName), findViewById(R.id.befrestName), findViewById(R.id.oddrunSite));
    }

    private void setTexts() {

    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    public void onViewClicked(View view) {
        int vId = view.getId();
        if (vId == next.getId()) {
//            signUpUser();
            animateColor();
        } else if (vId == profilePicture.getId()) {
            openImageIntent();
        }
    }

    private void signUpUser() {
        if (!Util.isNetworkAvailable(this)) {
            Util.showToast(this, "اتصال به اینترنت را بررسی کنید!", Toast.LENGTH_SHORT);
        } else {
            String name = this.name.getText().toString();
            String org = organization.getText().toString();
            if (name == null || name.length() < 1 || org == null || org.length() < 1) {
                Util.showToast(this, "لطفا نام و سازمان خود را به درستی وارد نمایید!", Toast.LENGTH_SHORT);
            } else {
                Util.setUserName(this, name);
                Util.setUserOrganization(this, org);
                if (profilePicPath != null) Util.setUsersProfilePicture(this, profilePicPath);
                startActivity(new Intent(this, ActivityMain.class));
                finish();
            }
        }
    }

    private Uri outputFileUri;

    private void openImageIntent() {

// Determine Uri of camera image to save.
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        root.mkdirs();
        final String fname = System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, CHOOSE_PIC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PIC_CODE) {
            if (resultCode == RESULT_OK) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
                profilePicPath = Util.getRealPathFromURI(this, selectedImageUri);
                profilePicture.setImageBitmap(Util.decodeSampledBitmapFromFile(profilePicPath, profilePicture.getWidth(), profilePicture.getHeight()));
            }
        }
    }

    private void animateColor() {
        int colorFrom = getResources().getColor(R.color.befrestBademjooni);
        int colorTo = getResources().getColor(R.color.befrestRed);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(animationLength);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                profilePicture.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();

        ViewPropertyAnimator animate = profilePicture.animate();
        int screenWidth = getScreenWidth();
        int width = profilePicture.getWidth();
        float right = profilePicture.getRight();
        float scaleXBy = befrestIcon.getWidth() / (float) profilePicture.getWidth();
        animate.scaleX(scaleXBy);
        animate.scaleY(befrestIcon.getHeight() / (float) profilePicture.getHeight());
        Log.d(TAG, "animate: y - >      top - >   " + profilePicture.getY() + "     " + profilePicture.getTop() );
        animate.translationX(screenWidth - right + (width - width * scaleXBy) / 2);
        animate.translationY((-1) * profilePicture.getTop());
//        BounceInterpolator bounceInterpolator = new BounceInterpolator();
//        ObjectAnimator anim = ObjectAnimator.ofFloat(profilePicture, "translationY", 0f, -200 );
//        anim.setInterpolator(bounceInterpolator);
//        anim.setDuration(1100).start();
    }

    int animationLength = 2000;
}
