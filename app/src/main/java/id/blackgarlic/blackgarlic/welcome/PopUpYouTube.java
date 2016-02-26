package id.blackgarlic.blackgarlic.welcome;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.util.Timer;

import id.blackgarlic.blackgarlic.R;

public class PopUpYouTube extends AppCompatActivity {

    private static int currentTimeVideo = 0;
    private static RelativeLayout youtubeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_you_tube);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.blackgarlicvideo;

        Uri uri = Uri.parse(path);

        final VideoView welcomeVideoView = (VideoView) findViewById(R.id.welcomeVideoView);

        welcomeVideoView.setVideoURI(uri);
        welcomeVideoView.seekTo(currentTimeVideo);
        welcomeVideoView.start();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.5));

        youtubeLayout = (RelativeLayout) findViewById(R.id.youtubePopUp);

        youtubeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTimeVideo = welcomeVideoView.getCurrentPosition();
                Log.e("Current Position: ", String.valueOf(currentTimeVideo));
            }
        });

        youtubeLayout.performClick();


    }


}

