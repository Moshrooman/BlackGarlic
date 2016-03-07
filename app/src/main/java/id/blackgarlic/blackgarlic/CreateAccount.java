package id.blackgarlic.blackgarlic;

import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.UserCredentials;

public class CreateAccount extends AppCompatActivity {

    private static String firstname;
    private static String lastname;
    private static String email;
    private static String password;
    List<EditText> editTextList = new ArrayList<EditText>();
    boolean emptyFields = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final EmailValidator emailValidator = EmailValidator.getInstance();

        final EditText createAccountFirstName = (EditText) findViewById(R.id.createAccountFirstName);
        final EditText createAccountLastName = (EditText) findViewById(R.id.createAccountLastName);
        final EditText createAccountEmail = (EditText) findViewById(R.id.createAccountEmail);
        final EditText createAccountPassword = (EditText) findViewById(R.id.createAccountPassword);

        createAccountPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emptyFields = false;

                editTextList.add(createAccountFirstName);
                editTextList.add(createAccountLastName);
                editTextList.add(createAccountEmail);
                editTextList.add(createAccountPassword);

                //Then set the hint to red color if there is no entry
                for (int i = 0; i < editTextList.size(); i++) {

                    if (String.valueOf(editTextList.get(i).getText()).equals("")) {
                        editTextList.get(i).setHintTextColor(getResources().getColor(R.color.red));
                        emptyFields = true;
                    } else {
                        String word = String.valueOf(editTextList.get(i).getText());
                        editTextList.get(i).setText(null);
                        editTextList.get(i).setHintTextColor(getResources().getColor(R.color.grey));
                        editTextList.get(i).setText(word);
                    }
                }

                //Creating the toast
                if (emptyFields == true) {
                    Toast.makeText(CreateAccount.this, "Fields In Red Are Required!", Toast.LENGTH_LONG).show();
                    return;
                }

                //Checking if the email is valid
                if (!emailValidator.isValid(String.valueOf(createAccountEmail.getText()))) {
                    Toast.makeText(CreateAccount.this, "Has To Be A Valid Email!", Toast.LENGTH_LONG).show();
                    createAccountEmail.setTextColor(getResources().getColor(R.color.red));
                    return;
                }

                firstname = String.valueOf(createAccountFirstName.getText());
                lastname = String.valueOf(createAccountLastName.getText());
                email = String.valueOf(createAccountEmail.getText());
                password = new String(Hex.encodeHex(DigestUtils.sha1(String.valueOf(createAccountPassword.getText()))));

                String url = "http://api.blackgarlic.id:7000/app/createaccount";

                final JSONObject body = new JSONObject();
                try {
                    body.put("first_name", firstname);
                    body.put("last_name", lastname);
                    body.put("customer_email", email);
                    body.put("customer_password", password);
                    body.put("birthday", "1000-01-01");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("Successful")) {
                            Intent logInScreenIntent = new Intent(CreateAccount.this, LogInScreen.class);
                            logInScreenIntent.putExtra("successful", "Account Successfully Created");
                            startActivity(logInScreenIntent);
                            finish();
                        } else if (response.contains("Duplicate")) {
                            Toast.makeText(CreateAccount.this, "That Email Already Exists!", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(CreateAccount.this, "Please Connect To The Internet!", Toast.LENGTH_LONG).show();


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

                ConnectionManager.getInstance(CreateAccount.this).add(request);

                Log.e("First name: ", firstname);
                Log.e("Last name: ", lastname);
                Log.e("Email: ", email);
                Log.e("Password: ", password);

            }
        });

    }
}

