package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SharePopUpActivity extends AppCompatActivity {

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";
    public final int EMAIL_REQUEST = 10;

    private static RelativeLayout sharePopUpRelativeLayout;
    private static CookBookObject cookBookObject;
    private static SimpleDraweeView faceBookImage;
    private static CallbackManager callbackManager;
    private static LoginManager loginManager;
    private static Bitmap imageToShare;
    private static FacebookCallback<Sharer.Result> facebookCallback;
    private static ImageView confirmImage;
    private static EditText pictureDescriptionEditText;
    private static Button postButton;
    private static View firstEmptyView;
    private static View secondEmptyView;
    private static int heightNeededToAddFacebook;
    private static List<View> viewListSetLayoutListenerFacebook = new ArrayList<View>();
    private static int countFacebook;
    private static RelativeLayout confirmFaceBookRelativeLayout;
    private static TextView faceBookNameTextView;
    private static TextView faceBookLogOutTextView;
    private static View faceBookNameEmptyView;
    private static View faceBookLogoutEmptyView;

    //Start of reference for email
    private static SimpleDraweeView emailImage;
    private static RelativeLayout confirmEmailRelativeLayout;
    private static ImageView emailConfirmImage;
    private static EditText emailToEditText;
    private static EditText emailCcEditText;
    private static View firstEmailEmptyView;
    private static EditText emailSubjectEditText;
    private static View secondEmailEmptyView;
    private static EditText emailBodyEditText;
    private static View thirdEmailEmptyView;
    private static Button sendEmailButton;
    private static View fourthEmailEmptyView;
    private static List<View> viewListSetLayoutListenerEmail = new ArrayList<View>();
    private static int countEmail;
    private static int heightNeededToAddEmail;

    private static ProgressBar sharePopUpProgressBar;
    private static TextView sharePopUpProgressBarTextView;
    private static RelativeLayout sharePopUpProgressBarRelativeLayout;
    private static View sharePopUpGreyScreen;

    private static int previousHeight;

    private static Profile faceBookProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_pop_up);

        FacebookSdk.sdkInitialize(getApplicationContext());

        overridePendingTransition(R.anim.fade_in, R.anim.actual_fade_out);

        cookBookObject = new Gson().fromJson(getIntent().getExtras().getString("object", ""), CookBookObject.class);
        sharePopUpRelativeLayout = (RelativeLayout) findViewById(R.id.sharePopUpRelativeLayout);
        previousHeight = 0;

        referenceFacebook();
        referenceEmail();

        sharePopUpGreyScreen.bringToFront();
        sharePopUpProgressBarTextView.bringToFront();
        sharePopUpProgressBar.bringToFront();
        sharePopUpProgressBarRelativeLayout.bringToFront();

        //Global layout shit for the facebook views

        viewListSetLayoutListenerFacebook.clear();

        //heightNeededToAddFacebook = facebooknametextview + facebookemtpyview + pictureDescriptionEditText + postButton + firstEmptyView + secondEmptyView;
        viewListSetLayoutListenerFacebook.add(pictureDescriptionEditText);
        viewListSetLayoutListenerFacebook.add(postButton);
        viewListSetLayoutListenerFacebook.add(firstEmptyView);
        viewListSetLayoutListenerFacebook.add(secondEmptyView);
        viewListSetLayoutListenerFacebook.add(faceBookNameTextView);
        viewListSetLayoutListenerFacebook.add(faceBookNameEmptyView);
        viewListSetLayoutListenerFacebook.add(faceBookLogOutTextView);
        viewListSetLayoutListenerFacebook.add(faceBookLogoutEmptyView);

        for (int i = 0; i < viewListSetLayoutListenerFacebook.size(); i++) {
            final int outsideI = i;
            viewListSetLayoutListenerFacebook.get(i).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    heightNeededToAddFacebook = heightNeededToAddFacebook + viewListSetLayoutListenerFacebook.get(outsideI).getHeight();
                    viewListSetLayoutListenerFacebook.get(outsideI).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    viewListSetLayoutListenerFacebook.get(outsideI).setVisibility(View.GONE);
                    countFacebook++;
                    //The reason why we check if countFacebook is the list.size and not size - 1, is because we want all 4 to be done, therefore
                    //if the size of the list is 4 we want all 4 to be done.
                    if (countFacebook == viewListSetLayoutListenerFacebook.size()) {

                        confirmFaceBookRelativeLayout.setVisibility(View.GONE);
                        Log.e("FaceBook Height: ", String.valueOf(heightNeededToAddFacebook));

                    }
                }
            });

        }

        //Global layout shit for the email views

        viewListSetLayoutListenerEmail.clear();

        viewListSetLayoutListenerEmail.add(emailConfirmImage);
        viewListSetLayoutListenerEmail.add(firstEmailEmptyView);
        viewListSetLayoutListenerEmail.add(emailSubjectEditText);
        viewListSetLayoutListenerEmail.add(secondEmailEmptyView);
        viewListSetLayoutListenerEmail.add(emailBodyEditText);
        viewListSetLayoutListenerEmail.add(thirdEmailEmptyView);
        viewListSetLayoutListenerEmail.add(sendEmailButton);
        viewListSetLayoutListenerEmail.add(fourthEmailEmptyView);

        for (int i = 0; i < viewListSetLayoutListenerEmail.size(); i++) {
            final int outSideI = i;

            viewListSetLayoutListenerEmail.get(i).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    heightNeededToAddEmail = heightNeededToAddEmail + viewListSetLayoutListenerEmail.get(outSideI).getHeight();
                    viewListSetLayoutListenerEmail.get(outSideI).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    viewListSetLayoutListenerEmail.get(outSideI).setVisibility(View.GONE);
                    countEmail++;

                    if (countEmail == viewListSetLayoutListenerEmail.size()) {
                        confirmEmailRelativeLayout.setVisibility(View.GONE);

                            sharePopUpRelativeLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), sharePopUpRelativeLayout.getHeight());
                                }
                            });

                        Log.e("Height Email: ", String.valueOf(heightNeededToAddEmail));

                    }
                }
            });
        }

        facebookCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("Facebook Callback: ", String.valueOf(result.getPostId()));
                SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Successfully Posted Picture!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN));
                superToast.show();

                Animation fadeOutAnimation = AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.actual_fade_out);
                sharePopUpProgressBar.setVisibility(View.GONE);
                sharePopUpProgressBar.startAnimation(fadeOutAnimation);

                sharePopUpProgressBarTextView.setVisibility(View.GONE);
                sharePopUpProgressBarTextView.startAnimation(fadeOutAnimation);

                sharePopUpGreyScreen.setVisibility(View.GONE);
                sharePopUpGreyScreen.startAnimation(fadeOutAnimation);

                finish();
            }

            @Override
            public void onCancel() {
                Log.e("Facebook Callback: ", "Cancelled");

                SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Error Posting Picture!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED, SuperToast.Animations.FLYIN));
                superToast.show();

                Animation fadeOutAnimation = AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.actual_fade_out);
                sharePopUpProgressBar.setVisibility(View.GONE);
                sharePopUpProgressBar.startAnimation(fadeOutAnimation);

                sharePopUpProgressBarTextView.setVisibility(View.GONE);
                sharePopUpProgressBarTextView.startAnimation(fadeOutAnimation);

                sharePopUpGreyScreen.setVisibility(View.GONE);
                sharePopUpGreyScreen.startAnimation(fadeOutAnimation);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook Callback: ", error.getCause().toString());
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.actual_fade_out);

                SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Error Posting Picture!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED, SuperToast.Animations.FLYIN));
                superToast.show();

                sharePopUpProgressBar.setVisibility(View.GONE);
                sharePopUpProgressBar.startAnimation(fadeOutAnimation);

                sharePopUpProgressBarTextView.setVisibility(View.GONE);
                sharePopUpProgressBarTextView.startAnimation(fadeOutAnimation);

                sharePopUpGreyScreen.setVisibility(View.GONE);
                sharePopUpGreyScreen.startAnimation(fadeOutAnimation);
            }
        };

        faceBookImage.setImageURI(Uri.parse("https://www.seeklogo.net/wp-content/uploads/2013/11/facebook-flat-vector-logo-400x400.png"));
        emailImage.setImageURI(Uri.parse("https://d3rnbxvnd0hlox.cloudfront.net/images/channels/6/icons/large.png"));

        faceBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> permissions = Arrays.asList("publish_actions");
                loginManager.logInWithPublishPermissions(SharePopUpActivity.this, permissions);
                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("CallBack: ", "Success");
                        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                        roundingParams.setBorder(R.color.red, 4);
                        roundingParams.setRoundAsCircle(false);
                        faceBookImage.getHierarchy().setRoundingParams(roundingParams);
                        emailImage.getHierarchy().setRoundingParams(null);

                        setEmailGone();
                        setFacebookVisible();

                        AnimationSet animationSet = new AnimationSet(true);
                        animationSet.addAnimation(AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.down_to_up));
                        animationSet.addAnimation(AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.fade_in_share));

                        confirmFaceBookRelativeLayout.startAnimation(animationSet);

                        emailImage.setEnabled(true);
                        faceBookImage.setEnabled(false);

                        getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), sharePopUpRelativeLayout.getHeight() - previousHeight + heightNeededToAddFacebook);
                        previousHeight = heightNeededToAddFacebook;

                        setConfirmImageAndBitmap();

                        faceBookProfile = Profile.getCurrentProfile();

                        SpannableStringBuilder faceBookNameStringBuilder = new SpannableStringBuilder();
                        faceBookNameStringBuilder.append("Logged In As: ").append(faceBookProfile.getName());
                        faceBookNameStringBuilder.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        faceBookNameStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 13, 13 + faceBookProfile.getName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        faceBookNameTextView.setText(faceBookNameStringBuilder, TextView.BufferType.SPANNABLE);
                    }

                    @Override
                    public void onCancel() {
                        Log.e("CallBack: ", "Cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("CallBack: ", "Error");
                        Log.e("Error: ", error.getCause().toString());
                    }
                });

            }
        });

        emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(R.color.red, 4);
                roundingParams.setRoundAsCircle(false);
                emailImage.getHierarchy().setRoundingParams(roundingParams);
                faceBookImage.getHierarchy().setRoundingParams(null);

                setFacebookGone();
                setEmailVisible();

                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.down_to_up));
                animationSet.addAnimation(AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.fade_in_share));

                confirmEmailRelativeLayout.startAnimation(animationSet);

                faceBookImage.setEnabled(true);
                emailImage.setEnabled(false);

                getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), sharePopUpRelativeLayout.getHeight() - previousHeight + heightNeededToAddEmail);
                previousHeight = heightNeededToAddEmail;

                setConfirmImageAndBitmap();

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(imageToShare)
                        .setCaption(pictureDescriptionEditText.getText().toString())
                        .build();

                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setContentUrl(Uri.parse("www.blackgarlic.id"))
                        .build();

                ShareApi.share(content, facebookCallback);

                Animation fadeInAnimation = AnimationUtils.loadAnimation(SharePopUpActivity.this, R.anim.fade_in);

                sharePopUpProgressBar.setVisibility(View.VISIBLE);
                sharePopUpProgressBar.startAnimation(fadeInAnimation);

                sharePopUpProgressBarTextView.setVisibility(View.VISIBLE);
                sharePopUpProgressBarTextView.startAnimation(fadeInAnimation);

                sharePopUpGreyScreen.setVisibility(View.VISIBLE);
                sharePopUpGreyScreen.startAnimation(fadeInAnimation);
            }
        });

        postButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        postButton.setBackground(getResources().getDrawable(R.drawable.filterheaderdrawable));
                        postButton.setTextColor(getResources().getColor(R.color.black));
                        break;

                    case MotionEvent.ACTION_DOWN:
                        postButton.setBackground(getResources().getDrawable(R.drawable.filterheaderdrawablelight));
                        postButton.setTextColor(getResources().getColor(R.color.LIGHTGREY));
                        break;

                }

                return false;
            }

        });

        sendEmailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_UP):
                        sendEmailButton.setBackground(getResources().getDrawable(R.drawable.filterheaderdrawable));
                        sendEmailButton.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case(MotionEvent.ACTION_DOWN):
                        sendEmailButton.setBackground(getResources().getDrawable(R.drawable.filterheaderdrawablelight));
                        sendEmailButton.setTextColor(getResources().getColor(R.color.LIGHTGREY));
                        break;
                }

                return false;
            }
        });

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Remember when we use == "" for string, it will always return true because it compares the types.
                //But if we use .equals for strings then it actually compares the contents of the string.

                if ((String.valueOf(emailToEditText.getText()).equals(""))) {
                    SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Please Enter A Recipient!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                    superToast.show();
                    return;
                }

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setDataAndType(Uri.parse("mailto:"), "message/rfc882");

                String[] toArray = String.valueOf(emailToEditText.getText()).split(",", -1);
                String[] ccArray = String.valueOf(emailCcEditText.getText()).split(",", -1);

                emailIntent.putExtra(Intent.EXTRA_EMAIL, toArray);
                emailIntent.putExtra(Intent.EXTRA_CC, ccArray);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubjectEditText.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, emailBodyEditText.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_STREAM, imageToShare);

                try {
                    startActivityForResult(Intent.createChooser(emailIntent, "Confirm Email"), EMAIL_REQUEST);

                    finish();

                } catch (ActivityNotFoundException e) {
                    Log.e("Email Error: ", e.getMessage());
                }
            }
        });

        faceBookLogOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FacebookSdk.sdkInitialize(getApplicationContext());
//                LoginManager.getInstance().logOut();
//                faceBookProfile = null;

                String uri = "fb://about";
                Intent faceBookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(faceBookIntent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setConfirmImageAndBitmap() {

        Uri cookBookImageUri = Uri.parse(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(cookBookObject.getMenu_id())));

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(cookBookImageUri)
                .setProgressiveRenderingEnabled(true)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, SharePopUpActivity.this);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {

                imageToShare = bitmap;
                confirmImage.setImageBitmap(bitmap);
                emailConfirmImage.setImageBitmap(bitmap);

            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());

        }

    public void referenceFacebook() {
        faceBookImage = (SimpleDraweeView) findViewById(R.id.faceBookImage);
        confirmImage = (ImageView) findViewById(R.id.confirmImage);
        pictureDescriptionEditText = (EditText) findViewById(R.id.pictureDescriptionEditText);
        postButton = (Button) findViewById(R.id.postButton);
        firstEmptyView = findViewById(R.id.firstEmptyView);
        secondEmptyView = findViewById(R.id.secondEmptyView);
        confirmFaceBookRelativeLayout = (RelativeLayout) findViewById(R.id.confirmFaceBookRelativeLayout);
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        heightNeededToAddFacebook = 0;
        countFacebook = 0;
        faceBookNameTextView = (TextView) findViewById(R.id.faceBookNameTextView);
        faceBookLogOutTextView = (TextView) findViewById(R.id.faceBookLogOutTextView);
        faceBookNameEmptyView = findViewById(R.id.faceBookNameEmptyView);
        faceBookLogoutEmptyView = findViewById(R.id.faceBookLogoutEmptyView);

        sharePopUpProgressBar = (ProgressBar) findViewById(R.id.sharePopUpProgressBar);
        sharePopUpProgressBarTextView = (TextView) findViewById(R.id.sharePopUpProgressBarTextView);
        sharePopUpProgressBarRelativeLayout = (RelativeLayout) findViewById(R.id.sharePopUpProgressBarRelativeLayout);
        sharePopUpGreyScreen = findViewById(R.id.sharePopUpGreyScreen);
    }

    public void referenceEmail() {
        emailImage = (SimpleDraweeView) findViewById(R.id.emailImage);
        confirmEmailRelativeLayout = (RelativeLayout) findViewById(R.id.confirmEmailRelativeLayout);
        emailConfirmImage = (ImageView) findViewById(R.id.emailConfirmImage);
        emailToEditText = (EditText) findViewById(R.id.emailToEditText);
        emailCcEditText = (EditText) findViewById(R.id.emailCcEditText);
        firstEmailEmptyView = findViewById(R.id.firstEmailEmptyView);
        emailSubjectEditText = (EditText) findViewById(R.id.emailSubjectEditText);
        secondEmailEmptyView = findViewById(R.id.secondEmailEmptyView);
        emailBodyEditText = (EditText) findViewById(R.id.emailBodyEditText);
        thirdEmailEmptyView = findViewById(R.id.thirdEmailEmptyView);
        sendEmailButton = (Button) findViewById(R.id.sendEmailButton);
        fourthEmailEmptyView = findViewById(R.id.fourthEmailEmptyView);
        countEmail = 0;
        heightNeededToAddEmail = 0;
    }

    public void setFacebookVisible() {
        confirmImage.setVisibility(View.VISIBLE);
        pictureDescriptionEditText.setVisibility(View.VISIBLE);
        postButton.setVisibility(View.VISIBLE);
        firstEmptyView.setVisibility(View.VISIBLE);
        secondEmptyView.setVisibility(View.VISIBLE);
        confirmFaceBookRelativeLayout.setVisibility(View.VISIBLE);
        faceBookNameTextView.setVisibility(View.VISIBLE);
        faceBookNameEmptyView.setVisibility(View.VISIBLE);
        faceBookLogOutTextView.setVisibility(View.VISIBLE);
        faceBookLogoutEmptyView.setVisibility(View.VISIBLE);
    }

    public void setFacebookGone() {
        confirmImage.setVisibility(View.GONE);
        pictureDescriptionEditText.setVisibility(View.GONE);
        postButton.setVisibility(View.GONE);
        firstEmptyView.setVisibility(View.GONE);
        secondEmptyView.setVisibility(View.GONE);
        confirmFaceBookRelativeLayout.setVisibility(View.GONE);
        faceBookNameTextView.setVisibility(View.GONE);
        faceBookNameEmptyView.setVisibility(View.GONE);
        faceBookLogOutTextView.setVisibility(View.GONE);
        faceBookLogoutEmptyView.setVisibility(View.GONE);
    }

    public void setEmailVisible() {
        confirmEmailRelativeLayout.setVisibility(View.VISIBLE);
        emailConfirmImage.setVisibility(View.VISIBLE);
        emailToEditText.setVisibility(View.VISIBLE);
        emailCcEditText.setVisibility(View.VISIBLE);
        firstEmailEmptyView.setVisibility(View.VISIBLE);
        emailSubjectEditText.setVisibility(View.VISIBLE);
        secondEmailEmptyView.setVisibility(View.VISIBLE);
        emailBodyEditText.setVisibility(View.VISIBLE);
        thirdEmailEmptyView.setVisibility(View.VISIBLE);
        sendEmailButton.setVisibility(View.VISIBLE);
        fourthEmailEmptyView.setVisibility(View.VISIBLE);
    }

    public void setEmailGone() {
        confirmEmailRelativeLayout.setVisibility(View.GONE);
        emailConfirmImage.setVisibility(View.GONE);
        emailToEditText.setVisibility(View.GONE);
        emailCcEditText.setVisibility(View.GONE);
        firstEmailEmptyView.setVisibility(View.GONE);
        emailSubjectEditText.setVisibility(View.GONE);
        secondEmailEmptyView.setVisibility(View.GONE);
        emailBodyEditText.setVisibility(View.GONE);
        thirdEmailEmptyView.setVisibility(View.GONE);
        sendEmailButton.setVisibility(View.GONE);
        fourthEmailEmptyView.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == EMAIL_REQUEST) && (resultCode == RESULT_OK) && (data != null)) {

            SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Successfully Sent Email!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN));
            superToast.show();

            Log.e("Email Sent: ", "Successful");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }
}


