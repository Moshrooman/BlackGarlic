package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.CircleProgressBarDrawable;
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
    private static final String updateReferralLink = "http://188.166.221.241:3000/app/updatereferral";
    private static ReferralObject referralObject;

    private static TextView referrerNameTextView;
    private static TextView referrerEmailTextView;
    private static RelativeLayout responseRelativeLayout;

    private static SimpleDraweeView firstMenuImage;
    private static SimpleDraweeView secondMenuImage;
    private static SimpleDraweeView thirdMenuImage;
    private static TextView firstMenuName;
    private static TextView secondMenuName;
    private static TextView thirdMenuName;
    private static List<SimpleDraweeView> menuImageList = new ArrayList<>();
    private static List<TextView> menuNameTextViewList = new ArrayList<>();
    private static List<MenuNameObject> menuList = new ArrayList<>();

    private static Button acceptButton;
    private static Button declineButton;

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_redemption);

        //We only want to activate the sendreferralcodebutton if they have entered 5 letters.

        assignReferences();

        enterReferralCodeEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(5)});

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

        acceptButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        acceptButton.setBackgroundResource(R.drawable.acceptclick);
                        acceptButton.setTextColor(getResources().getColor(R.color.BGGREEN));
                        break;
                    case MotionEvent.ACTION_UP:
                        acceptButton.setBackgroundResource(R.drawable.acceptunclick);
                        acceptButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
                        break;
                }

                return false;
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateReferral(true);

            }
        });

        declineButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        declineButton.setBackgroundResource(R.drawable.declineclick);
                        declineButton.setTextColor(getResources().getColor(R.color.red));
                        break;
                    case (MotionEvent.ACTION_UP):
                        declineButton.setBackgroundResource(R.drawable.declineunclick);
                        declineButton.setTextColor(getResources().getColor(R.color.BGGREY));
                        break;
                }

                return false;
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReferral(false);
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

                        if(response.equals("Non Existent")) {

                            SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Wrong Code/Not Your Referral!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                        } else if (response.equals("Completed")) {

                            SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Referral Already Redeemed!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                        } else if (response.equals("Cancelled")) {

                            SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Referral Was Cancelled!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                        } else {

                            referralObject = new Gson().fromJson(response, ReferralObject.class);

                            menuList = referralObject.getMenuNameList();

                            responseRelativeLayout.setVisibility(View.VISIBLE);
                            referrerNameTextView.setText(referralObject.getReferrerName());
                            referrerEmailTextView.setText(referralObject.getReferrer_email());

                            for (int i = 0; i < menuList.size(); i++) {

                                menuImageList.get(i).setVisibility(View.VISIBLE);
                                menuImageList.get(i).setImageURI(Uri.parse(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(menuList.get(i).getMenu_id()))));

                                menuNameTextViewList.get(i).setVisibility(View.VISIBLE);
                                menuNameTextViewList.get(i).setText(menuList.get(i).getMenu_name());

                            }

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

    public void assignReferences() {

        enterReferralCodeEditText = (EditText) findViewById(R.id.enterReferralCodeEditText);
        sendReferralCodeButton = (Button) findViewById(R.id.sendReferralCodeButton);
        sendReferralCodeButtonActivated = false;
        sharedPreferences = SplashActivity.getSharedPreferences();
        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);
        referrerNameTextView = (TextView) findViewById(R.id.referrerNameTextView);
        referrerEmailTextView = (TextView) findViewById(R.id.referrerEmailTextView);
        responseRelativeLayout = (RelativeLayout) findViewById(R.id.responseRelativeLayout);

        firstMenuImage = (SimpleDraweeView) findViewById(R.id.firstMenuImage);
        secondMenuImage = (SimpleDraweeView) findViewById(R.id.secondMenuImage);
        thirdMenuImage = (SimpleDraweeView) findViewById(R.id.thirdMenuImage);

        CircleProgressBarDrawable progressBar = new CircleProgressBarDrawable();
        progressBar.setBackgroundColor(ReferralRedemption.this.getResources().getColor(R.color.BGGREY));
        progressBar.setColor(ReferralRedemption.this.getResources().getColor(R.color.BGGREEN));

        firstMenuImage.getHierarchy().setProgressBarImage(progressBar);
        secondMenuImage.getHierarchy().setProgressBarImage(progressBar);
        thirdMenuImage.getHierarchy().setProgressBarImage(progressBar);

        menuImageList.clear();
        menuImageList.add(firstMenuImage);
        menuImageList.add(secondMenuImage);
        menuImageList.add(thirdMenuImage);

        firstMenuName = (TextView) findViewById(R.id.firstMenuName);
        secondMenuName = (TextView) findViewById(R.id.secondMenuName);
        thirdMenuName = (TextView) findViewById(R.id.thirdMenuName);
        menuNameTextViewList.clear();
        menuNameTextViewList.add(firstMenuName);
        menuNameTextViewList.add(secondMenuName);
        menuNameTextViewList.add(thirdMenuName);

        acceptButton = (Button) findViewById(R.id.acceptButton);
        declineButton = (Button) findViewById(R.id.declineButton);

    }

    public void updateReferral(boolean acceptOrDecline) {

        List<String> informationList = new ArrayList<String>();
        informationList.add(userCredentials.getAddress_content());
        informationList.add(userCredentials.getAddress_notes());
        informationList.add(userCredentials.getCity());
        informationList.add(userCredentials.getMobile());
        informationList.add(userCredentials.getZipcode());

        for (int i = 0; i < informationList.size(); i++) {

            if (informationList.get(i) == null) {

                SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Please Completely Fill Address Info In My Account!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                superToast.show();

                return;

            } else if(informationList.get(i).equals("") || informationList.get(i).equals("0")) {

                SuperToast superToast = SuperToast.create(ReferralRedemption.this, "Please Completely Fill Address Info In My Account!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                superToast.show();

                return;

            }

        }

        final JSONObject body = new JSONObject();

        final JSONArray menuArray = new JSONArray();

        try {

            for (int i = 0; i < menuList.size(); i++) {

                JSONObject menuObject = new JSONObject();
                menuObject.put("menu_id", menuList.get(i).getMenu_id());
                menuObject.put("portion", 2);

                menuArray.put(menuObject);

            }

            Log.e("Menus: ", String.valueOf(menuArray));

            body.put("menus", menuArray);
            body.put("accepted", acceptOrDecline);
            body.put("referred_email", referralObject.getReferred_email());
            body.put("referrer_id", referralObject.getReferrer_id());
            body.put("customer_name", userCredentials.getCustomer_name());
            body.put("mobile", userCredentials.getMobile());
            body.put("address_content", userCredentials.getAddress_content());
            body.put("city", userCredentials.getCity());
            body.put("zipcode", userCredentials.getZipcode());
            body.put("address_notes", userCredentials.getAddress_notes());
            body.put("customer_id", userCredentials.getCustomer_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest updateReferralRequest = new StringRequest(Request.Method.POST, updateReferralLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent referralRedeemedIntent = new Intent(ReferralRedemption.this, Referral_Redeemed.class);
                Log.e("Succesfully Updated: ", "True");
                startActivity(referralRedeemedIntent);
                finish();

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

        ConnectionManager.getInstance(ReferralRedemption.this).add(updateReferralRequest);

    }
}
