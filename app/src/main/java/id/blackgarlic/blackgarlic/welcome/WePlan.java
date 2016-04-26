package id.blackgarlic.blackgarlic.welcome;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;


public class WePlan extends Fragment {


    public WePlan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wePlanFragment = inflater.inflate(R.layout.fragment_we_plan, container, false);

        return wePlanFragment;

    }

    @Override
    public void onPause() {
        super.onDestroy();

        final ImageView wePlanImageView = (ImageView) getView().findViewById(R.id.wePlanImageView);
        wePlanImageView.setImageDrawable(null);

    }
}
