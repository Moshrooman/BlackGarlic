package id.blackgarlic.blackgarlic.welcome;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import id.blackgarlic.blackgarlic.R;


public class WeShop extends Fragment {


    public WeShop() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View weShopFragment = inflater.inflate(R.layout.fragment_we_shop, container, false);
        return weShopFragment;
    }

    @Override
    public void onPause() {
        super.onDestroy();

        final ImageView weShopImageView = (ImageView) getView().findViewById(R.id.weShopImageView);
        weShopImageView.setImageDrawable(null);
    }
}
