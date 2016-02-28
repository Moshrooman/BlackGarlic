package id.blackgarlic.blackgarlic.welcome;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;

import id.blackgarlic.blackgarlic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeWelcomeVideo extends Fragment {


    public YoutubeWelcomeVideo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View youTubeWelcomeVideoFragment = inflater.inflate(R.layout.fragment_youtube_welcome_video, container, false);

        final ImageView playImageView = (ImageView) youTubeWelcomeVideoFragment.findViewById(R.id.playBlackGarlicVideo);
        final Animation animScale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale);

        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopUpYouTube.class);
                playImageView.startAnimation(animScale);
                startActivity(intent);
            }
        });

        return youTubeWelcomeVideoFragment;

    }



}
