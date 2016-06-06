package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfilePicturePopUp extends AppCompatActivity {

    private static UserCredentials userCredentials;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static SimpleDraweeView changeProfilePictureDraweeView;
    private static Button saveProfilePictureButton;
    private static Uri newImageUri;

    private static final String CHANGE_PROFILE_PICTURE_URL = "http://188.166.221.241:3000/app/uploadprofilepicture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_pop_up);

        newImageUri = null;

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
        changeProfilePictureDraweeView.setImageDrawable(MainActivity.getProfileImageDraweeViewGlobal().getDrawable());

        fromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        saveProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                             Log.e("Image: ", newImageEncoded);

                                             final JSONObject bodyRequest = new JSONObject();

                                             try {
                                                 bodyRequest.put("customer_id", userCredentials.getCustomer_id());
                                                 bodyRequest.put("profile_picture", newImageEncoded);
                                             } catch (JSONException e) {
                                                 e.printStackTrace();
                                             }

                                             StringRequest changeProfilePictureRequest = new StringRequest(Request.Method.POST, CHANGE_PROFILE_PICTURE_URL, new Response.Listener<String>() {
                                                 @Override
                                                 public void onResponse(String response) {

                                                     Log.e("Upload Response: ", response);

                                                 }
                                             }, new Response.ErrorListener() {
                                                 @Override
                                                 public void onErrorResponse(VolleyError error) {

                                                 }
                                             }) {
                                                 @Override
                                                 public String getBodyContentType() {
                                                     return "application/json";
                                                 }

                                                 @Override
                                                 public byte[] getBody() throws AuthFailureError {
                                                     return bodyRequest.toString().getBytes();
                                                 }
                                             };

                                             ConnectionManager.getInstance(ProfilePicturePopUp.this).add(changeProfilePictureRequest);

                                         }

                                         @Override
                                         public void onFailureImpl(DataSource dataSource) {
                                             // No cleanup required here.
                                         }
                                     },
                        CallerThreadExecutor.getInstance());

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
        }
    }
}
