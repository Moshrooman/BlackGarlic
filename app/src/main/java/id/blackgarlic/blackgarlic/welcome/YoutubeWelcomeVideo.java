package id.blackgarlic.blackgarlic.welcome;


import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.net.URI;

import id.blackgarlic.blackgarlic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeWelcomeVideo extends Fragment{


    public YoutubeWelcomeVideo() {
        // Required empty public constructor
    }

    private static VideoView welcomeVideoViewStatic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View youTubeWelcomeVideoFragment = inflater.inflate(R.layout.fragment_youtube_welcome_video, container, false);

        String path = "android.resource://" + getContext().getPackageName() + "/" + R.raw.blackgarlicvideo;

        Uri uri = Uri.parse(path);

        VideoView welcomeVideoView = (VideoView) youTubeWelcomeVideoFragment.findViewById(R.id.welcomeVideoView);
        welcomeVideoView.setVideoURI(uri);
        welcomeVideoView.start();

        welcomeVideoViewStatic = welcomeVideoView;


        return youTubeWelcomeVideoFragment;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {

            if (!isVisibleToUser) {
                welcomeVideoViewStatic.pause();
            } else {
                welcomeVideoViewStatic.start();
            }

        }
    }
}
