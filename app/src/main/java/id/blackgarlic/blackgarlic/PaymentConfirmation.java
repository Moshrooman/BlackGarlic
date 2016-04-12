package id.blackgarlic.blackgarlic;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import id.blackgarlic.blackgarlic.OrderHistory.BaseArrayObjects;
import id.blackgarlic.blackgarlic.OrderHistory.menuObjects;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class PaymentConfirmation extends AppCompatActivity {

    private static String orderHistoryLink = "http://jdev.blackgarlic.id:7000/app/orderhistory";

    private static String paymentConfirmationLink = "http://jdev.blackgarlic.id:7000/app/paymentconfirmation";

    private static UserCredentials userCredentials;

    private static BaseArrayObjects[] baseArrayObjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        userCredentials = null;

        final Spinner orderNumberSpinner = (Spinner) findViewById(R.id.orderNumberPaymentConfirmationSpinner);
        //Setting spinner to disabled unless there is an order number when they haven't paid
        orderNumberSpinner.setEnabled(false);

        EditText transferredToBank = (EditText) findViewById(R.id.transferedToBankPaymentConfirmationEditText);
        final EditText accountNumber = (EditText) findViewById(R.id.accountNumberPaymentConfirmationEditText);
        final EditText fromBank = (EditText) findViewById(R.id.fromBankPaymentConfirmationEditText);
        final EditText ownerOfAccount = (EditText) findViewById(R.id.ownerOfAccountPaymentConfirmationEditText);
        Button confirmPaymentButton = (Button) findViewById(R.id.buttonConfirmPaymentPaymentConfirmation);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
        final UserCredentials credentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);
        userCredentials = credentials;


        final JSONObject paymentConfirmationOrderHistoryBody = new JSONObject();
        try {

            paymentConfirmationOrderHistoryBody.put("customer_id", String.valueOf(credentials.getCustomer_id()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest orderHistoryRequest = new StringRequest(Request.Method.POST, orderHistoryLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                baseArrayObjectList = new Gson().fromJson(response, BaseArrayObjects[].class);

                //Looping through the array that we got from the stringrequest to see if there is one where there is any entries where there is
                //Payment status 0 and NOT an order status of -1.
                //And if we do find one then we are going to add that unique_id

                List<String> orderNumbersForPaymentConfirmation = new ArrayList<String>();

                for (int i = 0; i < baseArrayObjectList.length; i++) {

                    if ((baseArrayObjectList[i].getPayment_status()) == 0 && (baseArrayObjectList[i].getOrder_status() != -1)) {
                        orderNumbersForPaymentConfirmation.add(baseArrayObjectList[i].getUnique_id());
                        orderNumberSpinner.setEnabled(true);
                    }

                }

                ArrayAdapter<String> orderNumberAdapter = new ArrayAdapter<String>(PaymentConfirmation.this, android.R.layout.simple_spinner_dropdown_item, orderNumbersForPaymentConfirmation);

                orderNumberSpinner.setAdapter(orderNumberAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", "True");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return paymentConfirmationOrderHistoryBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        ConnectionManager.getInstance(PaymentConfirmation.this).add(orderHistoryRequest);


        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderNumberSpinner.isEnabled() == false) {
                    Toast.makeText(PaymentConfirmation.this, "You Have No Unpaid Orders!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final JSONObject confirmPaymentBody = new JSONObject();
                try {

                    confirmPaymentBody.put("email", sharedPreferences.getString("Email", ""));
                    confirmPaymentBody.put("bank", fromBank.getText());
                    confirmPaymentBody.put("rekening", accountNumber.getText());
                    confirmPaymentBody.put("customer_name", ownerOfAccount.getText());
                    confirmPaymentBody.put("order_id", orderNumberSpinner.getItemAtPosition(orderNumberSpinner.getSelectedItemPosition()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest confirmPaymentRequest = new StringRequest(Request.Method.POST, paymentConfirmationLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response: ", response);
                        Toast.makeText(PaymentConfirmation.this, "Your Request Has Been Sent!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ", "True!");
                        Toast.makeText(PaymentConfirmation.this, "No Internet!", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return confirmPaymentBody.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                ConnectionManager.getInstance(PaymentConfirmation.this).add(confirmPaymentRequest);


            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainActivityIntent = new Intent(PaymentConfirmation.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainActivityIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
