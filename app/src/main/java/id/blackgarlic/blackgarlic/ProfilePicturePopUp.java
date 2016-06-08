package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfilePicturePopUp extends AppCompatActivity {

    private static UserCredentials userCredentials;
    private static SimpleDraweeView changeProfilePictureDraweeView;
    private static Button saveProfilePictureButton;

    private static Uri newImageUri;
    private static Bitmap newImageBitmap;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_TAKE_PICTURE = 2;

    private static String galleryOrPicture;

    private static final String CHANGE_PROFILE_PICTURE_URL = "http://188.166.221.241:3000/app/uploadprofilepicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_pop_up);

        galleryOrPicture = "";

        Log.e("Has Profile: ", String.valueOf(MainActivity.getHasProfilePicture()));

        overridePendingTransition(R.anim.fade_in, R.anim.actual_fade_out);

        newImageUri = null;
        newImageBitmap = null;

        userCredentials = new Gson().fromJson(SplashActivity.getSharedPreferences().getString("Credentials", ""), UserCredentials.class);

        final RelativeLayout profilePictureRootRelativeLayout = (RelativeLayout) findViewById(R.id.profilePictureRootRelativeLayout);
        TextView changeProfilePictureChefNameTextView = (TextView) findViewById(R.id.changeProfilePictureChefNameTextView);
        changeProfilePictureDraweeView = (SimpleDraweeView) findViewById(R.id.changeProfilePictureDraweeView);
        Button fromGalleryButton = (Button) findViewById(R.id.fromGalleryButton);
        Button takePictureButton = (Button) findViewById(R.id.takePictureButton);
        saveProfilePictureButton = (Button) findViewById(R.id.saveProfilePictureButton);

        saveProfilePictureButton.setEnabled(false);

        profilePictureRootRelativeLayout.post(new Runnable() {
            @Override
            public void run() {
                getWindow().setLayout(profilePictureRootRelativeLayout.getWidth(), profilePictureRootRelativeLayout.getHeight());
            }
        });

        changeProfilePictureChefNameTextView.setText(userCredentials.getCustomer_name());

        if (MainActivity.getHasProfilePicture() == false) {
            Uri uriAnonymousProfileImageDraweeView = Uri.parse("http://learngroup.org/assets/images/logos/default_male.jpg");
            changeProfilePictureDraweeView.setImageURI(uriAnonymousProfileImageDraweeView);
        } else {
            changeProfilePictureDraweeView.setImageDrawable(MainActivity.getProfileImageCircleImageView().getDrawable());
        }

        fromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pictureIntent, RESULT_TAKE_PICTURE);
            }
        });

        saveProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if the picture that was chosen was taken from the gallery
                if (galleryOrPicture.equals("gallery")) {

                    ImageRequest imageRequest = ImageRequestBuilder
                            .newBuilderWithSource(newImageUri)
                            .setProgressiveRenderingEnabled(true)
                            .build();

                    ImagePipeline imagePipeline = Fresco.getImagePipeline();

                    DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, ProfilePicturePopUp.this);

                    dataSource.subscribe(new BaseBitmapDataSubscriber() {

                                             @Override
                                             public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                                                 String newImageEncoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                                                 final JSONObject bodyRequest = new JSONObject();

                                                 try {
                                                     bodyRequest.put("customer_id", userCredentials.getCustomer_id());
                                                     bodyRequest.put("profile_picture", newImageEncoded);
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }

                                                 changeProfilePictureRequest(bodyRequest);

                                             }

                                             @Override
                                             public void onFailureImpl(DataSource dataSource) {
                                                 // No cleanup required here.
                                             }
                                         },
                            CallerThreadExecutor.getInstance());

                } else if (galleryOrPicture.equals("picture")) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    newImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                    String newImageBitmapEncoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    JSONObject body = new JSONObject();

                    try {
                        body.put("customer_id", userCredentials.getCustomer_id());
                        body.put("profile_picture", newImageBitmapEncoded);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    changeProfilePictureRequest(body);

                }

            }

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            newImageUri = selectedImageUri;

            changeProfilePictureDraweeView.setImageURI(selectedImageUri);
            saveProfilePictureButton.setBackgroundResource(R.drawable.checkoutbutton);
            saveProfilePictureButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
            saveProfilePictureButton.setEnabled(true);

            galleryOrPicture = "gallery";

        } else if (requestCode == RESULT_TAKE_PICTURE && resultCode == RESULT_OK && data != null) {

            Bitmap pictureTakenBitmap = (Bitmap) data.getExtras().get("data");

            newImageBitmap = pictureTakenBitmap;

            changeProfilePictureDraweeView.setImageBitmap(pictureTakenBitmap);
            saveProfilePictureButton.setBackgroundResource(R.drawable.checkoutbutton);
            saveProfilePictureButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
            saveProfilePictureButton.setEnabled(true);

            galleryOrPicture = "picture";
        }
    }

    public void changeProfilePictureRequest(final JSONObject body) {

        StringRequest changeProfilePictureRequest = new StringRequest(Request.Method.POST, CHANGE_PROFILE_PICTURE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                MainActivity.setHasProfilePicture(true);

                SuperToast superToast = SuperToast.create(ProfilePicturePopUp.this, "Successfully Changed Profile Picture!", SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP));
                superToast.show();

                byte[] encodedByte = Base64.decode(response, Base64.DEFAULT);
                Bitmap newBitmap = BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.length);

                MainActivity.setVisibilityDraweeView(View.GONE);
                MainActivity.setVisibilityCircularView(View.VISIBLE);

                MainActivity.setProfileImageCircleImageView(newBitmap);

                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SuperToast superToast = SuperToast.create(ProfilePicturePopUp.this, "Error Uploading Profile Picture!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                superToast.show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }
        };

        ConnectionManager.getInstance(ProfilePicturePopUp.this).add(changeProfilePictureRequest);

    }
}
