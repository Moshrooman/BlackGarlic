package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReferralRedemption extends AppCompatActivity {

    private static boolean sendReferralCodeButtonActivated;
    private static Button sendReferralCodeButton;
    private static EditText enterReferralCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_redemption);

        //We only want to activate the sendreferralcodebutton if they have entered 5 letters.

        enterReferralCodeEditText = (EditText) findViewById(R.id.enterReferralCodeEditText);
        sendReferralCodeButton = (Button) findViewById(R.id.sendReferralCodeButton);
        sendReferralCodeButtonActivated = false;

        sendReferralCodeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sendReferralCodeButtonActivated == false) {
                    return false;
                }

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        sendReferralCodeButton.setBackgroundResource(R.drawable.pressdowncheckout);
                        sendReferralCodeButton.setTextColor(getResources().getColor(R.color.BGGREEN));
                        break;
                    case (MotionEvent.ACTION_UP):
                        sendReferralCodeButton.setBackgroundResource(R.drawable.checkoutbutton);
                        sendReferralCodeButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
                        break;
                }

                return false;
            }
        });

        sendReferralCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendReferralCodeButtonActivated == false) {
                    return;
                }



            }
        });

        enterReferralCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 5) {
                    sendReferralCodeButtonActivated = true;
                    sendReferralCodeButton.setBackgroundResource(R.drawable.checkoutbutton);
                    sendReferralCodeButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));

                } else {
                    sendReferralCodeButtonActivated = false;
                    sendReferralCodeButton.setBackgroundResource(R.drawable.greyedoutloading);
                    sendReferralCodeButton.setTextColor(getResources().getColor(R.color.BGGREY));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
