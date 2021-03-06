package id.blackgarlic.blackgarlic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class LogInScreen extends AppCompatActivity {

    private static String username;
    private static String password;
    private static UserCredentials userCredentials;
    private static String myAccountSha1Password;

    public static String getMyAccountSha1Password() {
        return myAccountSha1Password;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public static void setUserCredentials(UserCredentials newUserCredentials) {
        userCredentials = newUserCredentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        if (this.getIntent().getExtras() != null) {
            Toast.makeText(LogInScreen.this, this.getIntent().getExtras().getString("successful"), Toast.LENGTH_LONG).show();
        }

        final EditText usernameEditText = (EditText)findViewById(R.id.userNameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView loggingYouInTextView = (TextView) findViewById(R.id.loggingYouInTextView);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final Button seeThisWeeksMenuButton = (Button) findViewById(R.id.seeThisWeeksMenuButton);

        seeThisWeeksMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeThisWeeksMenuButton.setEnabled(false);
                MainActivity.setIsLoggedIn(false);
                Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Converting all lists to json string using gson

                Gson gson = new Gson();

                String currentMenuList = gson.toJson(MainActivity.getCurrentMenuList());
                String currentMenuIdList = gson.toJson(MainActivity.getCurrentMenuIdList());
                String currentSelectedMenuListUrls = gson.toJson(MainActivity.getCurrentSelectedMenuListUrls());
                String currentPortionSizes = gson.toJson(MainActivity.getPortionSizes());
                String currentIndividualPrices = gson.toJson(MainActivity.getIndividualPrices());

                //The subtotal cost is an int anyways, so can add that separately

                int currentSubtotalCost = MainActivity.getSubTotalCost();

                //Then we put it all inside the shared preference:

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (! (null == MainActivity.getCurrentMenuList())) {

                    editor.putString("currentMenuList", currentMenuList);
                    editor.putString("currentMenuIdList", currentMenuIdList);
                    editor.putString("currentSelectedMenuListUrls", currentSelectedMenuListUrls);
                    editor.putString("currentPortionSizes", currentPortionSizes);
                    editor.putString("currentIndividualPrices", currentIndividualPrices);
                    editor.putInt("currentSubtotalCost", currentSubtotalCost);

                    editor.commit();

                }


                loginButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                loggingYouInTextView.setVisibility(View.VISIBLE);

                username = String.valueOf(usernameEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                final String sha1Password = new String(Hex.encodeHex(DigestUtils.sha1(password)));

                myAccountSha1Password = sha1Password;

                String url = "http://188.166.221.241:3000/app/login";

                final JSONObject body = new JSONObject();
                try {
                    body.put("email", username);
                    body.put("password", sha1Password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("\"address_id\":\"-1\"")) {

                            MainActivity.setIsLoggedIn(true);

                            Log.e("Response: ", response);

                            userCredentials = new Gson().fromJson(response, UserCredentials.class);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Credentials", new Gson().toJson(userCredentials));
                            editor.putString("Email", username);
                            editor.putString("Password", myAccountSha1Password);
                            editor.commit();

                            Log.e("Credentials: ", sharedPreferences.getString("Credentials", ""));

                            Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainActivityIntent);
                            finish();
                        } else if (response.contains("Invalid")){

                            loginButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            loggingYouInTextView.setVisibility(View.GONE);
                            Toast.makeText(LogInScreen.this, "Invalid Username/Password!", Toast.LENGTH_SHORT).show();

                        } else {
                            MainActivity.setIsLoggedIn(true);
                            Log.e("Response: ", response);
                            userCredentials = new Gson().fromJson(response, UserCredentials.class);

                            userCredentials.setCity();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Credentials", new Gson().toJson(userCredentials));
                            editor.putString("Email", username);
                            editor.putString("Password", myAccountSha1Password);
                            editor.commit();

                            Log.e("Credentials: ", sharedPreferences.getString("Credentials", ""));

                            progressBar.setVisibility(View.GONE);
                            loggingYouInTextView.setVisibility(View.GONE);

                            Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainActivityIntent);
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loginButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        loggingYouInTextView.setVisibility(View.GONE);
                        Toast.makeText(LogInScreen.this, "No Internet Connection!", Toast.LENGTH_LONG).show();

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

                ConnectionManager.getInstance(LogInScreen.this).add(request);

            }
        });

        final Button createAccountButton = (Button) findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountButton.setEnabled(false);
                Intent createAccountIntent = new Intent(LogInScreen.this, CreateAccount.class);
                startActivity(createAccountIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        Button seeThisWeeksMenuButton = (Button) findViewById(R.id.seeThisWeeksMenuButton);

        loginButton.setEnabled(true);
        createAccountButton.setEnabled(true);
        seeThisWeeksMenuButton.setEnabled(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        if (sharedPreferences.contains("nonlogin")) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.remove("nonlogin");
                editor.commit();
                Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
