package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Referral_Redeemed extends AppCompatActivity {

    private static TextView referralRedeemedNameTextView;
    private static TextView referralRedeemedUniqueIdTextView;
    private static TextView referralRedeemedAddressTextView;
    private static TextView referralRedeemedDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral__redeemed);

        String jsonResponse = getIntent().getExtras().getString("response_json");

        Referral_Redeemed_Object referralObject = new Gson().fromJson(jsonResponse, Referral_Redeemed_Object.class);

        if(referralRedeemedNameTextView == null) {

            assignVariables();

        }

        referralRedeemedNameTextView.setText(referralObject.getCustomer_name());
        referralRedeemedUniqueIdTextView.setText(referralObject.getUnique_id());
        referralRedeemedAddressTextView.setText(referralObject.getAddress_content());
        referralRedeemedDateTextView.setText(referralObject.getOrder_date());

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void assignVariables() {

        referralRedeemedNameTextView = (TextView) findViewById(R.id.referralRedeemedNameTextView);
        referralRedeemedUniqueIdTextView = (TextView) findViewById(R.id.referralRedeemedUniqueIdTextView);
        referralRedeemedAddressTextView = (TextView) findViewById(R.id.referralRedeemedAddressTextView);
        referralRedeemedDateTextView = (TextView) findViewById(R.id.referralRedeemedDateTextView);

    }

}
