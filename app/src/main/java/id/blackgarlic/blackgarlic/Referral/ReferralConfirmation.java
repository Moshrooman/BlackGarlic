package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
        final Button backToMenusReferralConfirmation = (Button) findViewById(R.id.backToMenusReferralConfirmation);

        codeReferredTextView.setText(confirmationCode);
        emailReferredTextView.setText(confirmationEmail);

        backToMenusReferralConfirmation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        backToMenusReferralConfirmation.setBackgroundResource(R.drawable.pressdowncheckout);
                        backToMenusReferralConfirmation.setTextColor(getResources().getColor(R.color.BGGREEN));
                        break;
                    case (MotionEvent.ACTION_UP):
                        backToMenusReferralConfirmation.setBackgroundResource(R.drawable.checkoutbutton);
                        backToMenusReferralConfirmation.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
                        break;
                }

                return false;
            }
        });

        backToMenusReferralConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
