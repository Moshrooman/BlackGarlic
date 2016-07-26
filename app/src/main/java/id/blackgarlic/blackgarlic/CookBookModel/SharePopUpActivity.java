package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SharePopUpActivity extends AppCompatActivity {

    private static RelativeLayout sharePopUpRelativeLayout;
    private static CookBookObject cookBookObject;
    private static SimpleDraweeView faceBookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pop_up);

        overridePendingTransition(R.anim.fade_in, R.anim.actual_fade_out);

        cookBookObject = new Gson().fromJson(getIntent().getExtras().getString("object", ""), CookBookObject.class);
        sharePopUpRelativeLayout = (RelativeLayout) findViewById(R.id.sharePopUpRelativeLayout);
        faceBookImage = (SimpleDraweeView) findViewById(R.id.faceBookImage);

        sharePopUpRelativeLayout.post(new Runnable() {
            @Override
            public void run() {
                getWindow().setLayout(sharePopUpRelativeLayout.getWidth(), sharePopUpRelativeLayout.getHeight());
            }
        });

        faceBookImage.setImageURI(Uri.parse("https://www.seeklogo.net/wp-content/uploads/2013/11/facebook-flat-vector-logo-400x400.png"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
