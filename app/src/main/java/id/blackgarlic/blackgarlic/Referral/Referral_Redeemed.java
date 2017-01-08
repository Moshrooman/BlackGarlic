package id.blackgarlic.blackgarlic.Referral;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import id.blackgarlic.blackgarlic.R;

public class Referral_Redeemed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral__redeemed);

        String jsonResponse = getIntent().getExtras().getString("response_json");

        Referral_Redeemed_Object referralObject = new Gson().fromJson(jsonResponse, Referral_Redeemed_Object.class);

        Log.e("Customer Name: ", referralObject.getCustomer_name());
        Log.e("Address: ", referralObject.getAddress_content());
        Log.e("Date: ", referralObject.getOrder_date());
        Log.e("Unique Id: ", String.valueOf(referralObject.getUnique_id()));

    }

}
