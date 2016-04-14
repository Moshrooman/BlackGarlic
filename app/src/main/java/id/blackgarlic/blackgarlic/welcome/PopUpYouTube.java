package id.blackgarlic.blackgarlic.welcome;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import id.blackgarlic.blackgarlic.R;

public class PopUpYouTube extends AppCompatActivity {

    private static int currentTimeVideo = 0;
    private static RelativeLayout youtubeLayout;
    private static boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_you_tube);

        youtubeLayout = (RelativeLayout) findViewById(R.id.youtubePopUp);

        final ImageView replayImageView = (ImageView) findViewById(R.id.replayImageView);
        replayImageView.bringToFront();

        String path = "android.resource://" + getPackageName() + "/" + R.raw.blackgarlicandroid;

        Uri uri = Uri.parse(path);

        final VideoView welcomeVideoView = (VideoView) findViewById(R.id.welcomeVideoView);

        welcomeVideoView.setVideoURI(uri);

        welcomeVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        getWindow().setLayout(welcomeVideoView.getWidth(), welcomeVideoView.getHeight());

                        MediaController mediaController = new MediaController(PopUpYouTube.this);

                        mediaController.setAnchorView(welcomeVideoView);

                        welcomeVideoView.setMediaController(mediaController);

                    }
                });

            }
        });


        youtubeLayout.setClickable(false);

        youtubeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTimeVideo = welcomeVideoView.getCurrentPosition();
                Log.e("Current Position: ", String.valueOf(currentTimeVideo));
            }
        });

        Button closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.bringToFront();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeLayout.performClick();
                finish();
            }
        });

        welcomeVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentTimeVideo = welcomeVideoView.getCurrentPosition();
                isFinished = true;
                final Animation fadein = AnimationUtils.loadAnimation(PopUpYouTube.this, R.anim.fade_in);
                replayImageView.setVisibility(View.VISIBLE);
                replayImageView.startAnimation(fadein);


                replayImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFinished = false;
                        replayImageView.setVisibility(View.GONE);
                        welcomeVideoView.seekTo(0);
                        welcomeVideoView.start();
                    }
                });
            }
        });

        if (isFinished == false) {
            welcomeVideoView.seekTo(currentTimeVideo);
            welcomeVideoView.start();
        } else if (isFinished == true) {
            welcomeVideoView.seekTo(88050);
            welcomeVideoView.start();
            welcomeVideoView.pause();
            final Animation fadein = AnimationUtils.loadAnimation(PopUpYouTube.this, R.anim.fade_in);
            replayImageView.setVisibility(View.VISIBLE);
            replayImageView.startAnimation(fadein);


            replayImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFinished = false;
                    replayImageView.setVisibility(View.GONE);
                    welcomeVideoView.seekTo(0);
                    welcomeVideoView.start();
                }
            });
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            youtubeLayout.performClick();
        }

        return super.onKeyDown(keyCode, event);

    }


}

