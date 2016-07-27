package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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
import com.facebook.share.widget.ShareDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SharePopUpActivity extends AppCompatActivity {

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pop_up);

        FacebookSdk.sdkInitialize(getApplicationContext());

        overridePendingTransition(R.anim.fade_in, R.anim.actual_fade_out);

        cookBookObject = new Gson().fromJson(getIntent().getExtras().getString("object", ""), CookBookObject.class);
        sharePopUpRelativeLayout = (RelativeLayout) findViewById(R.id.sharePopUpRelativeLayout);
        faceBookImage = (SimpleDraweeView) findViewById(R.id.faceBookImage);
        confirmImage = (ImageView) findViewById(R.id.confirmImage);
        pictureDescriptionEditText = (EditText) findViewById(R.id.pictureDescriptionEditText);
        postButton = (Button) findViewById(R.id.postButton);
        callbackManager = CallbackManager.Factory.create();
        final List<String> permissions = Arrays.asList("publish_actions");
        loginManager = LoginManager.getInstance();
        facebookCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("Facebook Callback: ", String.valueOf(result.getPostId()));
                SuperToast superToast = SuperToast.create(SharePopUpActivity.this, "Successfully Posted Picture!", SuperToast.Duration.MEDIUM, Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN));
                superToast.show();
                finish();
            }

            @Override
            public void onCancel() {
                Log.e("Facebook Callback: ", "Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook Callback: ", error.getCause().toString());
            }
        };

        sharePopUpRelativeLayout.post(new Runnable() {
            @Override
            public void run() {
                getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), sharePopUpRelativeLayout.getHeight());
            }
        });

        faceBookImage.setImageURI(Uri.parse("https://www.seeklogo.net/wp-content/uploads/2013/11/facebook-flat-vector-logo-400x400.png"));

        faceBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.logInWithPublishPermissions(SharePopUpActivity.this, permissions);
                loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("CallBack: ", "Success");
                        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                        roundingParams.setBorder(R.color.red, 4);
                        roundingParams.setRoundAsCircle(false);
                        faceBookImage.getHierarchy().setRoundingParams(roundingParams);
                        confirmImage.setVisibility(View.VISIBLE);
                        pictureDescriptionEditText.setVisibility(View.VISIBLE);
                        postButton.setVisibility(View.VISIBLE);

                        getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), 1500);

                        setConfirmImageAndBitmap();
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

            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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


