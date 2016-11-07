package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReferralRedemption extends AppCompatActivity {

    private static boolean sendReferralCodeButtonActivated;
    private static Button sendReferralCodeButton;
    private static EditText enterReferralCodeEditText;
    private static SharedPreferences sharedPreferences;
    private static UserCredentials userCredentials;
    private static final String referralLink = "http://188.166.221.241:3000/app/checkreferral";
    private static ReferralObject referralObject;
    private static List<String> referralMenuList = new ArrayList<String >();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_redemption);

        //We only want to activate the sendreferralcodebutton if they have entered 5 letters.

        enterReferralCodeEditText = (EditText) findViewById(R.id.enterReferralCodeEditText);
        sendReferralCodeButton = (Button) findViewById(R.id.sendReferralCodeButton);
        sendReferralCodeButtonActivated = false;
        sharedPreferences = SplashActivity.getSharedPreferences();
        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);

        enterReferralCodeEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(5)});

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

                String referralCode = enterReferralCodeEditText.getText().toString();
                String email = userCredentials.getCustomer_email();

                final JSONObject body = new JSONObject();

                try {
                    body.put("referral_code", referralCode);
                    body.put("customer_email", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest referralRequest = new StringRequest(Request.Method.POST, referralLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("Completed")) {

                            SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Referral Already Redeemed!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                        } else if (response.equals("Cancelled")) {

                            SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Referral Was Cancelled!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                        } else {

                            referralObject = new Gson().fromJson(response, ReferralObject.class);

                            //So here basically the menu_ids is set to a string that starts with [, ends with ] and has integers seperated
                            //by commas. So I created the method set referral menus and passed a new arraylist created from callig
                            //Arrays.asList and retrieved this string and replaced all [ with empty string, all ] with empty string,
                            //all spaces with empty string.
                            referralObject.setReferral_menus(new ArrayList<String>(Arrays.asList(referralObject.getMenu_ids().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(","))));
                            referralMenuList = referralObject.getReferral_menus();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes();
                    }
                };

                ConnectionManager.getInstance(ReferralRedemption.this).add(referralRequest);

            }
        });

        enterReferralCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 5) {
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