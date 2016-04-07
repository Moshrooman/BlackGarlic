package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyAccount extends AppCompatActivity {

    private static UserCredentials userCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);

        //All EditTexts:

        TextView nameEditText = (TextView) findViewById(R.id.nameTextView);
        final EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        final EditText mobileEditText = (EditText) findViewById(R.id.mobileEditText);
        final EditText zipcodeEditText = (EditText) findViewById(R.id.zipcodeEditText);
        final EditText addressnotesEditText = (EditText) findViewById(R.id.addressnotesEditText);
        final Spinner cityDropDown = (Spinner) findViewById(R.id.cityDropDown);
        String[] cities = new String[]{"Jakarta Pusat", "Jakarta Selatan", "Jakarta Barat", "Jakarta Utara", "Jakarta Timur", "Tangerang", "Bekasi", "Tangerang Selatan", "Depok"};
        ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        cityDropDown.setAdapter(cityadapter);

        cityDropDown.setSelection(cityadapter.getPosition(userCredentials.getCity()));
        nameEditText.setText(userCredentials.getCustomer_name());
        addressEditText.setText(userCredentials.getAddress_content());
        mobileEditText.setText(userCredentials.getMobile());
        zipcodeEditText.setText(userCredentials.getZipcode());
        addressnotesEditText.setText(userCredentials.getAddress_notes());

        Button saveInformationButton = (Button) findViewById(R.id.saveInformationButton);
        final Button updateUserCredentials = (Button) findViewById(R.id.updateUserCredentials);

        final ImageView loadingGreyScreen = (ImageView) findViewById(R.id.loadingGreyScreen);
        loadingGreyScreen.bringToFront();

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.myAccountProgressBar);
        progressBar.bringToFront();

        final TextView loadingTextView = (TextView) findViewById(R.id.textViewLoading);
        loadingTextView.bringToFront();

        final String updateUrl = "http://api.blackgarlic.id:7000/app/updateaccount";
        final String getUserCredentialsUrl = "http://api.blackgarlic.id:7000/app/login";

        TextView backToMenusTextView = (TextView) findViewById(R.id.backToMenuTextView);
        backToMenusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(MyAccount.this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
        });

        updateUserCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final JSONObject body2 = new JSONObject();
                try {
                    body2.put("email", sharedPreferences.getString("Email", ""));
                    body2.put("password", sharedPreferences.getString("Password", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Second Body: ", String.valueOf(body2));

                StringRequest request = new StringRequest(Request.Method.POST, getUserCredentialsUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        UserCredentials newUserCredentials = new Gson().fromJson(response, UserCredentials.class);
                        newUserCredentials.setCity();
                        LogInScreen.setUserCredentials(newUserCredentials);

                        editor.remove("Credentials");
                        editor.putString("Credentials", new Gson().toJson(newUserCredentials));

                        editor.commit();

                        loadingTextView.setText("Successfully Updated User Credentials!");

                        Runnable runnable1sec = new Runnable() {
                            @Override
                            public void run() {

                                loadingGreyScreen.setVisibility(View.INVISIBLE);
                                Animation fadeOut = AnimationUtils.loadAnimation(MyAccount.this, R.anim.actual_fade_out);
                                loadingGreyScreen.startAnimation(fadeOut);

                                progressBar.setVisibility(View.INVISIBLE);
                                progressBar.startAnimation(fadeOut);

                                loadingTextView.setVisibility(View.INVISIBLE);
                                loadingTextView.startAnimation(fadeOut);

                                loadingTextView.setText("");

                            }
                        };

                        android.os.Handler myHandler = new android.os.Handler();
                        myHandler.postDelayed(runnable1sec, 1000);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loadingGreyScreen.setVisibility(View.INVISIBLE);
                        Animation fadeOut = AnimationUtils.loadAnimation(MyAccount.this, R.anim.actual_fade_out);
                        loadingGreyScreen.startAnimation(fadeOut);

                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.startAnimation(fadeOut);

                        loadingTextView.setVisibility(View.INVISIBLE);
                        loadingTextView.startAnimation(fadeOut);

                        loadingTextView.setText("");

                        Toast.makeText(MyAccount.this, "No Internet!", Toast.LENGTH_SHORT).show();

                        Log.e("error: ", String.valueOf(error));

                    }
                }) {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body2.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                };

                ConnectionManager.getInstance(MyAccount.this).add(request);

            }
        });

        //The Button That Is Physically Pressed

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingGreyScreen.setVisibility(View.VISIBLE);
                Animation fadeIn = AnimationUtils.loadAnimation(MyAccount.this, R.anim.fade_in);
                loadingGreyScreen.startAnimation(fadeIn);

                progressBar.setVisibility(View.VISIBLE);
                progressBar.startAnimation(fadeIn);

                loadingTextView.setVisibility(View.VISIBLE);
                loadingTextView.startAnimation(fadeIn);


                final JSONObject body = new JSONObject();
                try {
                    body.put("address_id", userCredentials.getAddress_id());
                    body.put("customer_id", userCredentials.getCustomer_id());
                    body.put("address_content", String.valueOf(addressEditText.getText()));
                    body.put("city", String.valueOf(cityDropDown.getSelectedItemPosition() + 1));
                    body.put("mobile", String.valueOf(mobileEditText.getText()));
                    body.put("zipcode", String.valueOf(zipcodeEditText.getText()));
                    body.put("address_notes", String.valueOf(addressnotesEditText.getText()));
                    body.put("address_status", "1");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loadingTextView.setText(response);

                        Runnable runnable2sec = new Runnable() {
                            @Override
                            public void run() {

                                updateUserCredentials.performClick();

                            }
                        };

                        android.os.Handler myHandler = new android.os.Handler();
                        myHandler.postDelayed(runnable2sec, 2000);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loadingGreyScreen.setVisibility(View.INVISIBLE);
                        Animation fadeOut = AnimationUtils.loadAnimation(MyAccount.this, R.anim.actual_fade_out);
                        loadingGreyScreen.startAnimation(fadeOut);

                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.startAnimation(fadeOut);

                        loadingTextView.setVisibility(View.INVISIBLE);
                        loadingTextView.startAnimation(fadeOut);

                        loadingTextView.setText("");

                        Toast.makeText(MyAccount.this, "No Internet!", Toast.LENGTH_SHORT).show();
                        Log.e("error: ", String.valueOf(error));

                    }
                }) {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }


                };

                ConnectionManager.getInstance(MyAccount.this).add(request);

            }
        });

            }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainActivityIntent = new Intent(MyAccount.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainActivityIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
