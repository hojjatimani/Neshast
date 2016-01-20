package bef.rest.neshast;

import android.animation.Animator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivitySignUp extends FragmentActivity {
    Button next;
    ImageView profilePicture;
    EditText name;
    EditText organization;
    String profilePicPath;
    ImageView befrestIcon;
    TextView nameLabel;
    RelativeLayout content;

    ViewPager pager;
    FloatingActionButton chat;
    private static final String TAG = "ActivitySignUp";
    public static final int CHOOSE_PIC_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.userHasRegistered(this)) {
            setContentView(R.layout.activity_main);
            initViews();
            addContent();
            initContentViews();
            setUserName();
            if (Util.userHasProfilePicture(this)) setUserProfilePicture();
        } else {
            setContentView(R.layout.activity_sign_up_2);
            initViews();
            initLoginViews();
        }
    }

    private void initViews() {
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
        befrestIcon = (ImageView) findViewById(R.id.befrestIcon);
        nameLabel = (TextView) findViewById(R.id.lable_name);
        setFonts();
        setSize();
        setTexts();
        //set Profile Picture Size
//        profilePicture.post(new Runnable() {
//            @Override
//            public void run() {
//                profilePicture.setImageBitmap(Util.getRoundedCornerBitmap(((BitmapDrawable) profilePicture.getDrawable()).getBitmap(), profilePicture.getHeight()));
//            }
//        });
    }

    private void initLoginViews() {
        next = (Button) findViewById(R.id.next);
        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
    }

    private void initContentViews() {
        pager = (ViewPager) content.findViewById(R.id.pager);
        chat = (FloatingActionButton) content.findViewById(R.id.chat);
        AdapterPager adapterPager = new AdapterPager(getSupportFragmentManager());
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

    private void setSize() {
        profilePicture.post(new Runnable() {
            @Override
            public void run() {
                profilePicture.getLayoutParams().width = profilePicture.getHeight();
                profilePicture.requestLayout();
            }
        });
    }

    private void setFonts() {
        Util.setFont(this, Util.FontFamily.Default, Util.FontWeight.Regular, name, organization, next,
                findViewById(R.id.oddrunName), nameLabel, findViewById(R.id.oddrunSite));
    }

    private void setTexts() {

    }

    public void onViewClicked(View view) {
        int vId = view.getId();
        if (vId == next.getId()) {
            signUpUser();
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
                Util.setUserHasRegistered(this, true);
                animateUi();
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
        int actionBarSize = getActionBarSize();
        int screenWidth = Util.getScreenWidth(this);
        int width = profilePicture.getWidth();
        int height = profilePicture.getHeight();
        int right = profilePicture.getRight();
        int top = profilePicture.getTop();
        int padding = getResources().getDimensionPixelSize(R.dimen.space_small);
        float scaleXBy = (actionBarSize - 2 * padding) / (float) width;
        float scaleYBy = (actionBarSize- 2 * padding) / (float) height;
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

    private int getActionBarSize(){
        return findViewById(R.id.actionBarSize).getHeight();
    }

    int animDurarion = 1000;
    int shortAnimDuration = 500;
}