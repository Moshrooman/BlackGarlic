package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReferralConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_confirmation);

        String confirmationCode = getIntent().getExtras().getString("code");
        String confirmationEmail = getIntent().getExtras().getString("email");

        TextView emailReferredTextView = (TextView) findViewById(R.id.emailReferredTextView);
        TextView codeReferredTextView = (TextView) findViewById(R.id.codeReferredTextView);

        codeReferredTextView.setText(confirmationCode);
        emailReferredTextView.setText(confirmationEmail);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
