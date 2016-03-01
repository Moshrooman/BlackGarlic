package id.blackgarlic.blackgarlic;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

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

public class LogInScreen extends AppCompatActivity {

    private static String username;
    private static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen);

        final EditText usernameEditText = (EditText)findViewById(R.id.userNameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = String.valueOf(usernameEditText.getText());
                password = String.valueOf(passwordEditText.getText());

                String sha1Password = new String(Hex.encodeHex(DigestUtils.sha1(password)));

                usernameEditText.setText("");
                passwordEditText.setText("");

                //Run the httpget method for the login, save all of the things that it returns, and if it gets it, go to mainActivity

                String url = "http://api.blackgarlic.id:7000/app/login/"+ username +"/"+ sha1Password +"";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Data: ", response);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(LogInScreen.this, "Invalid Username/Password!", Toast.LENGTH_LONG).show();

                    }
                });

                ConnectionManager.getInstance(LogInScreen.this).add(request);

            }
        });
    }

}
