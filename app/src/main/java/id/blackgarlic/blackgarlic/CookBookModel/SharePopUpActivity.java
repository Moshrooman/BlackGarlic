package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pop_up);

        overridePendingTransition(R.anim.fade_in, R.anim.actual_fade_out);

        cookBookObject = new Gson().fromJson(getIntent().getExtras().getString("object", ""), CookBookObject.class);
        sharePopUpRelativeLayout = (RelativeLayout) findViewById(R.id.sharePopUpRelativeLayout);
        faceBookImage = (SimpleDraweeView) findViewById(R.id.faceBookImage);
        callbackManager = CallbackManager.Factory.create();
        final List<String> permissions = Arrays.asList("publish_action");
        loginManager = LoginManager.getInstance();

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
                        publishImage();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void publishImage() {

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

            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(imageToShare)
                .setCaption("Awesome Menu From BlackGarlic!")
                .setImageUrl(Uri.parse("www.blackgarlic.id"))
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, (FacebookCallback<Sharer.Result>) callbackManager);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}


