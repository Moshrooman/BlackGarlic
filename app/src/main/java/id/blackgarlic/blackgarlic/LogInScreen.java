package id.blackgarlic.blackgarlic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.database.DatabaseContract;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class LogInScreen extends AppCompatActivity {

    private static String username;
    private static String password;
    private static UserCredentials userCredentials;

    public static UserCredentials getUserCredentials() {
        return userCredentials;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        if (this.getIntent().getExtras() != null) {
            Toast.makeText(LogInScreen.this, this.getIntent().getExtras().getString("successful"), Toast.LENGTH_LONG).show();
        }

        final EditText usernameEditText = (EditText)findViewById(R.id.userNameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView loggingYouInTextView = (TextView) findViewById(R.id.loggingYouInTextView);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                loggingYouInTextView.setVisibility(View.VISIBLE);

                username = String.valueOf(usernameEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                final String sha1Password = new String(Hex.encodeHex(DigestUtils.sha1(password)));

                String url = "http://api.blackgarlic.id:7000/app/login";

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

                        if (response.contains("No address found!")) {
                            //Take away the no address found and the <br><br> and then
                            Log.e("Response: ", response);

                            String jsonResponse = response.replace("<br><br>No address found!", "");

                            userCredentials = new Gson().fromJson(jsonResponse, UserCredentials.class);

                            Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                        } else {
                            Log.e("Response: ", response);
                            userCredentials = new Gson().fromJson(response, UserCredentials.class);

                            userCredentials.setCity();

                            progressBar.setVisibility(View.GONE);
                            loggingYouInTextView.setVisibility(View.GONE);

                            Intent mainActivityIntent = new Intent(LogInScreen.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                        loggingYouInTextView.setVisibility(View.GONE);
                        Toast.makeText(LogInScreen.this, "Invalid Username/Password!", Toast.LENGTH_LONG).show();

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

        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccountIntent = new Intent(LogInScreen.this, CreateAccount.class);
                startActivity(createAccountIntent);
            }
        });

    }

}
