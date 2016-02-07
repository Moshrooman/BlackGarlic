package id.blackgarlic.blackgarlic.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;

public class YouCook extends Fragment {

    public YouCook() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View youCookFragment = inflater.inflate(R.layout.fragment_you_cook, container, false);
        return youCookFragment;
    }


}
