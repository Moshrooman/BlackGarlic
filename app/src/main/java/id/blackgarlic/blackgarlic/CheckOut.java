package id.blackgarlic.blackgarlic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class CheckOut extends AppCompatActivity {

    private static List<Boolean> dateBooleanList = new ArrayList<Boolean>();
    private static List<Button> dateButtonList = new ArrayList<Button>();
    private static List<LocalDate> localDateList = new ArrayList<LocalDate>();

    private static int incrementingInteger = 0;

    private static String selectedDate = "";

    private static List<Data> selectedMenuList;
    private static List<Integer> selectedMenuIdList;
    private static List<String> selectedMenuListUrls;
    private static List<String> selectedPortionSizes;
    private static int subTotalCost;
    private static List<String> selectedIndividualPrices;

    private static ListView orderSummaryListView;

    private static UserCredentials userCredentials;

    private static Boolean gojekButtonBoolean;

    private static Boolean etobeeButtonBoolean;

    private static String deliveryFee;

    private static String deliveryTime;

    private static String selectedPaymentMethod;

    private static int grandTotal;

    //Connecting to local database doesnt work because it doesnt contain the menu ids retrieved here.
    private static String orderApiLink = "http://api.blackgarlic.id:7000/app/order";

    private static Integer boxId;

    private static final String updateUrl = "http://api.blackgarlic.id:7000/app/updateaccount";

    private static final String getUserCredentialsUrl = "http://api.blackgarlic.id:7000/app/login";

    private static Button updateAppCredentialsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        retrieveBitmapFromCache();

        updateAppCredentialsButton = (Button) findViewById(R.id.updateAppCredentialsButton);

        grandTotal = 0;
        deliveryFee = "";
        deliveryTime = "";
        selectedPaymentMethod = "";

        boxId = MainActivity.getBoxId();

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);

        final TextView employeeDiscountTitleTextView = (TextView) findViewById(R.id.employeeDiscountTitleTextView);
        final TextView employeeDiscountTextView = (TextView) findViewById(R.id.employeeDiscountTextView);

        final TextView boxForTextView = (TextView) findViewById(R.id.boxForTextView);

        final TextView grandTotalTextView = (TextView) findViewById(R.id.grandTotalTextView);

        final TextView deliveryFeeAnswerTextView = (TextView) findViewById(R.id.deliveryFeeAnswerTextView);

        final ScrollView checkOutScrollView = (ScrollView) findViewById(R.id.checkOutScrollView);
        final RelativeLayout shippingOptionsRelativeLayout = (RelativeLayout) findViewById(R.id.shippingOptionsRelativeLayout);

        final CheckBox checkOutCheckBox = (CheckBox) findViewById(R.id.checkOutCheckBox);
        final Button placeOrderButton = (Button) findViewById(R.id.placeOrderButton);

        TextView customerNameTextView = (TextView) findViewById(R.id.customerNameTextView);
        customerNameTextView.setText(userCredentials.getCustomer_name());

        final EditText checkOutZipCodeEditText = (EditText) findViewById(R.id.checkOutZidCodeEditText);
        checkOutZipCodeEditText.setText(userCredentials.getZipcode());

        final Spinner checkOutCityDropDown = (Spinner) findViewById(R.id.checkOutCityDropDown);
        String[] cities = new String[]{"Jakarta Pusat", "Jakarta Selatan", "Jakarta Barat", "Jakarta Utara", "Jakarta Timur", "Tangerang", "Bekasi", "Tangerang Selatan", "Depok"};
        ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        checkOutCityDropDown.setAdapter(cityadapter);
        checkOutCityDropDown.setSelection(cityadapter.getPosition(userCredentials.getCity()));

        final EditText checkOutAddressContent = (EditText) findViewById(R.id.checkOutAddressContent);
        checkOutAddressContent.setText(userCredentials.getAddress_content());

        final EditText checkOutMobile = (EditText) findViewById(R.id.checkOutMobile);
        checkOutMobile.setText(userCredentials.getMobile());

        final EditText checkOutAddressNotes = (EditText) findViewById(R.id.checkOutAddressNotes);
        checkOutAddressNotes.setText(userCredentials.getAddress_notes());

        gojekButtonBoolean = false;
        etobeeButtonBoolean = false;

        final Button gojekButton = (Button) findViewById(R.id.gojekButton);

        final Button etobeeButton = (Button) findViewById(R.id.etobeeButton);

        final TextView deliveryTypeTextView = (TextView) findViewById(R.id.deliveryTypeTextView);

        final TextView deliveryFeeTextView = (TextView) findViewById(R.id.deliveryFeeTextView);

        final TextView gojekCircumstanceTextView = (TextView) findViewById(R.id.gojekCircumstanceTextView);

        final ImageView etobeedeliverytimeImageView = (ImageView) findViewById(R.id.etobeedeliverytimeImageView);

        final TextView selectDeliveryTimeTextView = (TextView) findViewById(R.id.selectDeliveryTimeTextView);

        final Spinner deliveryTimeDropDown = (Spinner) findViewById(R.id.deliveryTimeDropDown);
        String[] deliveryTimes = new String[]{"12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00"};
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deliveryTimes);
        deliveryTimeDropDown.setAdapter(deliveryAdapter);

        selectedMenuList = MainActivity.getCurrentMenuList();
        selectedMenuIdList = MainActivity.getCurrentMenuIdList();
        selectedMenuListUrls = MainActivity.getCurrentSelectedMenuListUrls();
        selectedPortionSizes = MainActivity.getPortionSizes();
        subTotalCost = MainActivity.getSubTotalCost();
        selectedIndividualPrices = MainActivity.getIndividualPrices();

        checkOutCityDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((position) >= 0 && (position <= 4)) || (position == 7)) {
                    gojekButton.setVisibility(View.VISIBLE);
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    gojekButton.setBackgroundResource(R.drawable.deactivatedgojek);
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";
                    deliveryFeeAnswerTextView.setText(deliveryFee);
                    if (userCredentials.getCustomer_status().equals("2")) {

                        employeeDiscountTitleTextView.setVisibility(View.VISIBLE);
                        employeeDiscountTextView.setVisibility(View.VISIBLE);

                        employeeDiscountTextView.setText("- IDR " + new DecimalFormat().format((subTotalCost / 2)));

                        grandTotal = (subTotalCost / 2);
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));

                        Log.e("grandtotalDiscount: ", String.valueOf(grandTotal));
                    } else {
                        grandTotal = subTotalCost;
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));
                    }


                    Log.e("Delivery Time: ", deliveryTime);
                    Log.e("Delivery Fee: ", deliveryFee);
                    Log.e("Grand Total: ", String.valueOf(grandTotal));

                } else {
                    gojekButton.setVisibility(View.GONE);
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";
                    deliveryFeeAnswerTextView.setText(deliveryFee);
                    if (userCredentials.getCustomer_status().equals("2")) {

                        employeeDiscountTitleTextView.setVisibility(View.VISIBLE);
                        employeeDiscountTextView.setVisibility(View.VISIBLE);

                        employeeDiscountTextView.setText("- IDR " + new DecimalFormat().format((subTotalCost / 2)));

                        grandTotal = (subTotalCost / 2);
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));

                        Log.e("grandtotalDiscount: ", String.valueOf(grandTotal));
                    } else {
                        grandTotal = subTotalCost;
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));
                    }

                    Log.e("Delivery Time: ", deliveryTime);
                    Log.e("Delivery Fee: ", deliveryFee);
                    Log.e("Grand Total: ", String.valueOf(grandTotal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etobeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(etobeeButtonBoolean == true)) {
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    gojekButton.setBackgroundResource(R.drawable.deactivatedgojek);
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    gojekCircumstanceTextView.setVisibility(View.GONE);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";
                    deliveryFeeAnswerTextView.setText(deliveryFee);
                    if (userCredentials.getCustomer_status().equals("2")) {

                        employeeDiscountTitleTextView.setVisibility(View.VISIBLE);
                        employeeDiscountTextView.setVisibility(View.VISIBLE);

                        employeeDiscountTextView.setText("- IDR " + new DecimalFormat().format((subTotalCost / 2)));

                        grandTotal = (subTotalCost / 2);
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));

                        Log.e("grandtotalDiscount: ", String.valueOf(grandTotal));
                    } else {
                        grandTotal = subTotalCost;
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));
                    }
                }

                Log.e("Delivery Time: ", deliveryTime);
                Log.e("Delivery Fee: ", deliveryFee);
                Log.e("Grand Total: ", String.valueOf(grandTotal));

            }
        });

        gojekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(gojekButtonBoolean == true)) {
                    gojekButtonBoolean = true;
                    etobeeButtonBoolean = false;
                    gojekButton.setBackgroundResource(R.drawable.activatedgojek);
                    etobeeButton.setBackgroundResource(R.drawable.deactivatedetobee);
                    deliveryFee = "IDR 20.000";
                    deliveryTypeTextView.setText("OJEK ONLINE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    gojekCircumstanceTextView.setVisibility(View.VISIBLE);
                    etobeedeliverytimeImageView.setVisibility(View.GONE);
                    selectDeliveryTimeTextView.setText("Choose your delivery time");
                    deliveryTimeDropDown.setVisibility(View.VISIBLE);
                    deliveryTimeDropDown.setSelection(0);
                    deliveryTime = "12";
                    deliveryFeeAnswerTextView.setText(deliveryFee);

                    if (userCredentials.getCustomer_status().equals("2")) {

                        int discountedSubTotalCost = (subTotalCost / 2);

                        employeeDiscountTitleTextView.setVisibility(View.VISIBLE);
                        employeeDiscountTextView.setVisibility(View.VISIBLE);

                        employeeDiscountTextView.setText("- IDR " + new DecimalFormat().format(discountedSubTotalCost));

                        grandTotal = (subTotalCost / 2) + 20000;
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));

                        Log.e("grandtotalDiscount: ", String.valueOf(grandTotal));
                    } else {
                        grandTotal = subTotalCost + 20000;
                        grandTotalTextView.setText("IDR " + new DecimalFormat().format(grandTotal));
                    }



                }

                checkOutScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        checkOutScrollView.smoothScrollTo(0, shippingOptionsRelativeLayout.getTop());
                    }
                });

                Log.e("Delivery Time: ", deliveryTime);
                Log.e("Delivery Fee: ", deliveryFee);
                Log.e("Grand Total: ", String.valueOf(grandTotal));

            }
        });

        deliveryTimeDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(deliveryTimeDropDown.getItemAtPosition(position)));
                stringBuilder.delete(2, 13);

                String deliveryTimeFromStringBuilder = stringBuilder.toString();

                if (gojekButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.activatedgojek).getConstantState())) {

                    deliveryTime = deliveryTimeFromStringBuilder;
                    Log.e("Delivery Time: ", deliveryTime);

                }
                Log.e("Delivery Fee: ", deliveryFee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final TextView methodInfoTextView = (TextView) findViewById(R.id.methodInfoTextView);
        final Spinner paymentMethodDropDown = (Spinner) findViewById(R.id.paymentMethodDropDown);
        String[] paymentMethods = new String[] {"Select A Payment Method", "TRANSFER" /*KARTU KREDIT ONLINE", "DOKU WALLET"*/};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods);
        paymentMethodDropDown.setAdapter(paymentAdapter);
        paymentMethodDropDown.setSelection(0);

        paymentMethodDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    methodInfoTextView.setText("Please Select A Payment Method!");
                    selectedPaymentMethod = "";
                } else if (position == 1) {

                    if (!(selectedDate.equals(""))) {
                        LocalDate deliveryTimeBankTransfer = new LocalDate(selectedDate).minusDays(2);
                        String deliveryTimeBankTransferString = deliveryTimeBankTransfer.dayOfMonth().getAsText() + " " + deliveryTimeBankTransfer.monthOfYear().getAsText() + " " + deliveryTimeBankTransfer.year().getAsText() + " " + "15:00";
                        methodInfoTextView.setText("BANK TRANSFER\n\nBCA BANK\nAcc. No.: 537 532 0255\nBranch: Sudirman Mansion\nAcc. Name: BGI Jaya Indonesia PT" +
                                "\n\nOnce you have made the transfer, please confirm your transfer so we can verify your purchase\n\n" +
                                "Please complete the transfer before" + " " + deliveryTimeBankTransferString +
                                " or your order will be cancelled automatically\n\n1x24 Hours Verification");
                        selectedPaymentMethod = "bank_transfer";
                        checkOutScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                checkOutScrollView.scrollTo(0, shippingOptionsRelativeLayout.getBottom());
                            }
                        });
                    } else {
                        paymentMethodDropDown.setSelection(0);
                        checkOutScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                checkOutScrollView.smoothScrollTo(0, checkOutScrollView.getTop());
                            }
                        });
                        Toast.makeText(CheckOut.this, "Please Select A Delivery Date!", Toast.LENGTH_SHORT).show();
                    }

                }

//                else if (position == 2) {
//                    methodInfoTextView.setText("ONLINE CREDIT CARD\n\nAvailable for credit card with Visa / MasterCard\n\n" +
//                            "Once you have completed the ordering process, you will be redirected to DOKU's website to finalize the payment" +
//                            "\n\n\nDOKU is our official payment gateway partner\n\nReal-Time Verification");
//
//                } else {
//                    methodInfoTextView.setText("DOKU Wallet\n\nAvailable for DOKU Wallet account holder\n\nOnce you have completed the ordering process, you will be redirected to DOKU webiste to finalize the payment" +
//                            "\n\n\nDon't have whitesignupbutton DOKU Wallet? Create here\n\nReal-Time Verification");
//
//                }

                Log.e("Payment_Method: ", selectedPaymentMethod);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkOutCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    placeOrderButton.setClickable(true);
                    placeOrderButton.setBackgroundResource(R.drawable.checkoutbutton);
                } else {
                    placeOrderButton.setClickable(false);
                    placeOrderButton.setBackgroundResource(R.drawable.greyedoutloading);
                }
            }
        });

        updateAppCredentialsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final JSONObject updateAppCredentialsBody = new JSONObject();
                try {
                    updateAppCredentialsBody.put("email", sharedPreferences.getString("Email", ""));
                    updateAppCredentialsBody.put("password", sharedPreferences.getString("Password", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest updateAppCredentialsRequest = new StringRequest(Request.Method.POST, getUserCredentialsUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response2) {

                        Log.e("Update Credentials: ", "APP");

                        Log.e("Response: ", response2);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        UserCredentials newUserCredentials = new Gson().fromJson(response2, UserCredentials.class);
                        newUserCredentials.setCity();
                        LogInScreen.setUserCredentials(newUserCredentials);

                        editor.remove("Credentials");
                        editor.putString("Credentials", new Gson().toJson(newUserCredentials));

                        editor.commit();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return updateAppCredentialsBody.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                ConnectionManager.getInstance(CheckOut.this).add(updateAppCredentialsRequest);

            }
        });
        
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDate == "") {
                    Toast.makeText(CheckOut.this, "Please Select A Delivery Date!", Toast.LENGTH_SHORT).show();

                } else if (selectedPaymentMethod == "") {
                    Toast.makeText(CheckOut.this, "Please Select A Payment Method!", Toast.LENGTH_SHORT).show();

                } else {

                    //Start of stringrequest to update user credentials

                    final JSONObject updateWebCredentialsBody = new JSONObject();
                    try {
                        updateWebCredentialsBody.put("address_id", userCredentials.getAddress_id());
                        updateWebCredentialsBody.put("customer_id", userCredentials.getCustomer_id());
                        updateWebCredentialsBody.put("address_content", String.valueOf(checkOutAddressContent.getText()));
                        updateWebCredentialsBody.put("city", String.valueOf(checkOutCityDropDown.getSelectedItemPosition() + 1));
                        updateWebCredentialsBody.put("mobile", String.valueOf(checkOutMobile.getText()));
                        updateWebCredentialsBody.put("zipcode", String.valueOf(checkOutZipCodeEditText.getText()));
                        updateWebCredentialsBody.put("address_notes", String.valueOf(checkOutAddressNotes.getText()));
                        updateWebCredentialsBody.put("address_status", "1");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    placeOrderButton.setEnabled(false);

                    Toast.makeText(CheckOut.this, "Placing Order", Toast.LENGTH_SHORT).show();


                    final JSONObject addressObject = new JSONObject();
                    try {
                        addressObject.put("customer_name", userCredentials.getCustomer_name());
                        addressObject.put("mobile", String.valueOf(checkOutMobile.getText()));
                        addressObject.put("address_content", String.valueOf(checkOutAddressContent.getText()));
                        addressObject.put("city", String.valueOf(checkOutCityDropDown.getSelectedItemPosition() + 1));
                        addressObject.put("zipcode", String.valueOf(checkOutZipCodeEditText.getText()));
                        addressObject.put("address_notes", String.valueOf(checkOutAddressNotes.getText()));
                        addressObject.put("delivery_time", String.valueOf(deliveryTime));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final JSONArray menuJsonArray = new JSONArray();
                    try {

                        for (int i = 0; i < selectedMenuIdList.size(); i++) {
                            JSONObject object = new JSONObject();
                            object.put("menu_id", String.valueOf(selectedMenuIdList.get(i)));
                            object.put("portion", String.valueOf(selectedPortionSizes.get(i).replace("P", "")));

                            menuJsonArray.put(object);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final JSONObject body = new JSONObject();
                    try {
                        body.put("order_source", "app");
                        body.put("customer_id", userCredentials.getCustomer_id());
                        body.put("box_id", String.valueOf(boxId));
                        body.put("order_date", selectedDate);
                        body.put("payment_method", selectedPaymentMethod);

                        if (userCredentials.getCustomer_status().equals("2")) {
                            body.put("balance_discount", String.valueOf((subTotalCost / 2)));
                        } else {
                            body.put("balance_discount", "0");
                        }

                        body.put("voucher_discount", "0");

                        if (deliveryFee.equals("FREE!!!")) {
                            body.put("delivery_fee", "0");
                        } else {
                            String df = String.valueOf(deliveryFee);
                            df = df.replace("IDR ", "");
                            df = df.replace(".", "");
                            body.put("delivery_fee", df);
                        }

                        body.put("grandtotal", String.valueOf(grandTotal));

                        body.put("address", addressObject);
                        body.put("menu", menuJsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("Body", String.valueOf(body) );

                    //Connecting to local database doesnt work because it doesnt contain the menu ids retrieved here.

                    StringRequest request = new StringRequest(Request.Method.POST, orderApiLink, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Status: ", "Successful");
                            Log.e("Order ID: ", response);

                            //When the response is successful then we are going to update the webCredentials
                            StringRequest updateWebCredentialsRequest = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.e("Update User: ", "WEB");

                                    Runnable runnable2sec = new Runnable() {
                                        @Override
                                        public void run() {

                                            //If the web response is successful we are going to click the buttons on click listener above
                                            //Which will update the apps credentials
                                            updateAppCredentialsButton.performClick();


                                        }
                                    };

                                    android.os.Handler myHandler = new android.os.Handler();
                                    myHandler.postDelayed(runnable2sec, 2000);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {

                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    return updateWebCredentialsBody.toString().getBytes();
                                }

                                @Override
                                public String getBodyContentType() {
                                    return "application/json";
                                }

                            };

                            ConnectionManager.getInstance(CheckOut.this).add(updateWebCredentialsRequest);

                            //End of string request to update user credentials


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            placeOrderButton.setEnabled(true);
                            Log.e("Status", "Unsuccessful");



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

                    ConnectionManager.getInstance(CheckOut.this).add(request);
                }

            }
        });

        dateBooleanList.clear();
        dateButtonList.clear();
        localDateList.clear();
        selectedDate = "";

        incrementingInteger = 0;

        orderSummaryListView = (ListView) findViewById(R.id.orderSummaryListView);
        orderSummaryListView.setClickable(false);

        Button firstDate = (Button) findViewById(R.id.firstDate);
        Button secondDate = (Button) findViewById(R.id.secondDate);
        Button thirdDate = (Button) findViewById(R.id.thirdDate);
        Button fourthDate = (Button) findViewById(R.id.fourthDate);
        Button fifthDate = (Button) findViewById(R.id.fifthDate);

        boolean firstDateBoolean = false;
        boolean secondDateBoolean = false;
        boolean thirdDateBoolean = false;
        boolean fourthDateBoolean = false;
        boolean fifthDateBoolean = false;

        dateButtonList.add(firstDate);
        dateButtonList.add(secondDate);
        dateButtonList.add(thirdDate);
        dateButtonList.add(fourthDate);
        dateButtonList.add(fifthDate);

        dateBooleanList.add(firstDateBoolean);
        dateBooleanList.add(secondDateBoolean);
        dateBooleanList.add(thirdDateBoolean);
        dateBooleanList.add(fourthDateBoolean);
        dateBooleanList.add(fifthDateBoolean);

        //Getting Current date and time:
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();

        //Checking variables, returns the current hours:
        DateFormat checkDate = new SimpleDateFormat("HH");
        checkDate.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        String checkIfAfter3 = checkDate.format(currentLocalTime);

        //Actual Variables
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        date.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        String localTime = date.format(currentLocalTime);

        //Joda-Time Library, parsing it into whitesignupbutton jodatime object, easier to add dates and time now.
        LocalDate localDate = new LocalDate(localTime);
        Integer checkIfAfter3Integer = Integer.valueOf(checkIfAfter3);

        //Only go through with this for loop only if it is in between sunday and thursday 3pm
        if ((localDate.getDayOfWeek() == 7) || ((localDate.getDayOfWeek() >= 1) && (localDate.getDayOfWeek() <= 4)) ) {

            //This will be false until friday so in this else we localdate + 5 days
            if (  !(  (localDate.getDayOfWeek() == 4) && (checkIfAfter3Integer >= 15)  )  ) {

                //Setting it forward 2 days as the dates will always start 2 days ahead
                LocalDate localDate2 = new LocalDate(localDate.plusDays(2));

                for (int i = localDate2.getDayOfWeek(); i < 7; i++) {

                    if (incrementingInteger == 0) {
                        String dayOfWeek = String.valueOf(localDate2.dayOfWeek().getAsText(Locale.ENGLISH));
                        String month = String.valueOf(localDate2.monthOfYear().getAsText(Locale.ENGLISH));
                        String dayOfMonth = String.valueOf(localDate2.dayOfMonth().getAsText(Locale.ENGLISH));

                        localDateList.add(localDate2);
                        dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                        incrementingInteger++;
                    } else {
                        String dayOfWeek = String.valueOf(localDate2.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                        String month = String.valueOf(localDate2.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                        String dayOfMonth = String.valueOf(localDate2.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                        localDateList.add(localDate2.plusDays(incrementingInteger));
                        dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                        incrementingInteger++;
                    }

                }

                //Separate to make the rest the of the Buttons not visible
                //So make not button at index not visible first, then delete, then delete from boolean list.
                //Don't need to remove anything from the localdate list because we are already adding it to the specific needs.

                //i = 3
                //limit is 5, still within limit so goes through with iteration
                // [] [] [] [x] []

                //Deletes at index 3 but i stays 3 because of if statement comparing size before and after
                //limit is now 4, still within limit so goes through with iteration
                //[] [] [] [x]

                //Deletes at index 3 again, but i stays 3
                //[] [] [] x
                //Outside of array but index is 3, and limit 3 so it stops.
                for (int i = incrementingInteger; i < dateBooleanList.size(); i++) {
                    int initialSize = dateBooleanList.size();

                    dateButtonList.get(i).setVisibility(View.GONE);
                    dateButtonList.remove(i);
                    dateBooleanList.remove(i);

                    if (initialSize != dateBooleanList.size()) {
                        i = i - 1;
                    }
                }

                //Then separate to check if 3 o clock, if it is delete the first one.

                if ((checkIfAfter3Integer.intValue() >= 15)) {
                    dateButtonList.get(0).setVisibility(View.GONE);
                    dateButtonList.remove(0);
                    dateBooleanList.remove(0);
                    localDateList.remove(0);
                }

                //This else will only be called if it is still thursday and after 3pm, but before friday
            } else {
                Log.e("Else: ", "First");
                LocalDate localDate3 = new LocalDate(localDate.plusDays(5));

                for (int i = localDate3.getDayOfWeek(); i < 7; i++) {
                    String dayOfWeek = String.valueOf(localDate3.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                    String month = String.valueOf(localDate3.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                    String dayOfMonth = String.valueOf(localDate3.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                    localDateList.add(localDate3.plusDays(incrementingInteger));
                    dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                    incrementingInteger++;
                }


            }

            //This else will only be called if it is after friday so if day = 5 add 4
            //If day = 6 add 3
        } else {

            Log.e("Else: ", "Second");

            LocalDate localDate4 = new LocalDate(localDate);

            if (localDate4.getDayOfWeek() == 5) {
                localDate4 = localDate4.plusDays(4);
            } else if (localDate4.getDayOfWeek() == 6) {
                localDate4 = localDate4.plusDays(3);
            }

            for (int i = localDate4.getDayOfWeek(); i < 7; i++) {
                String dayOfWeek = String.valueOf(localDate4.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                String month = String.valueOf(localDate4.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                String dayOfMonth = String.valueOf(localDate4.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                localDateList.add(localDate4.plusDays(incrementingInteger));
                dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                incrementingInteger++;
            }

        }

        for (int i = 0; i < dateButtonList.size(); i++) {

            final int j = i;

            dateButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        dateBooleanList.set(i, false);

                    }

                    //When I set the position at j to true, i was looking at the dateButtonList.get(i)
                    //I wanted to set the position in the dateBooleanList that matched the position of the dateButtonList clicked
                    //So if I invoked the dateButtonList at position 3's OnClickListener I want to set the dateBooleanList at position 3
                    dateBooleanList.set(j, true);

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        if (dateBooleanList.get(i).booleanValue() == true) {
                            dateButtonList.get(i).setBackgroundResource(R.drawable.greydateselection);
                            dateButtonList.get(i).setTextColor(getResources().getColor(R.color.white));
                        } else if (   !(dateButtonList.get(i).getBackground().getConstantState().equals(getResources().getDrawable(android.R.drawable.btn_default).getConstantState()) ) && !(dateButtonList.get(i).getTextColors().equals(getResources().getColor(R.color.black)))   ){
                            dateButtonList.get(i).setBackgroundResource(android.R.drawable.btn_default);
                            dateButtonList.get(i).setTextColor(getResources().getColor(R.color.black));
                        }

                    }

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        if (dateBooleanList.get(i).booleanValue() == true) {
                            selectedDate = localDateList.get(i).toString();
                        }

                    }

                    LocalDate finalDeliveryDate = new LocalDate(selectedDate);

                    boxForTextView.setText("Box for " + finalDeliveryDate.dayOfWeek().getAsText() + ", " + finalDeliveryDate.dayOfMonth().getAsText() + " " + finalDeliveryDate.monthOfYear().getAsText());

                    Log.e("Selected Date: ", selectedDate);

                }
            });

        }

        orderSummaryListView.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, selectedMenuListUrls, selectedPortionSizes, selectedIndividualPrices));
        setListViewHeightBasedOnItems(orderSummaryListView);

        TextView subTotalPriceTextView = (TextView) findViewById(R.id.subTotalPriceTextView);

        subTotalPriceTextView.setText("IDR " + new DecimalFormat().format(subTotalCost));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainActivityIntent = new Intent(CheckOut.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainActivityIntent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button placeOrderButton = (Button) findViewById(R.id.placeOrderButton);
        placeOrderButton.setEnabled(true);

    }

    public class MyAdapter extends BaseAdapter{

        private List<Data> selectedMenuList;
        private List<Integer> selectedMenuIdList;
        private List<String> selectedMenuListUrls;
        private List<String> selectedPortionSizes;
        private List<String> selectedIndividualPrices;

        public MyAdapter(List<Data> currentSelectedMenuList, List<Integer> currentSelectedMenuIdList, List<String> currentSelectedMenuListUrls, List<String> portionSize, List<String> individualPrices){
            this.selectedMenuList = currentSelectedMenuList;
            this.selectedMenuIdList = currentSelectedMenuIdList;
            this.selectedMenuListUrls = currentSelectedMenuListUrls;
            this.selectedPortionSizes = portionSize;
            this.selectedIndividualPrices = individualPrices;
        }

        @Override
        public int getCount() {
            return selectedMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return selectedMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View orderCheckOutView = getLayoutInflater().inflate(R.layout.row_ordercheckout, null);

            NetworkImageView orderCheckOutNetworkImageView = (NetworkImageView) orderCheckOutView.findViewById(R.id.orderCheckOutNetworkImageView);
            TextView orderCheckOutMenuTitle = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutMenuTitle);
            TextView orderCheckOutPortion = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutPortion);
            TextView orderCheckOutMenuType = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutMenuType);
            TextView orderCheckOutPrice = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutPrice);

            orderCheckOutNetworkImageView.setImageUrl(selectedMenuListUrls.get(position).toString(), ConnectionManager.getImageLoader(CheckOut.this));
            orderCheckOutMenuTitle.setText(selectedMenuList.get(position).getMenu_name());

            String initialPortion = selectedPortionSizes.get(position);
            initialPortion = initialPortion + "erson";

            String finalPortion = initialPortion.substring(0,1) + " " + initialPortion.substring(1, initialPortion.length());
            orderCheckOutPortion.setText(finalPortion);

            if ((selectedMenuList.get(position).getMenu_type().equals("3")) || (selectedMenuList.get(position).getMenu_type().equals("5"))) {
                orderCheckOutMenuType.setText("Original");
            } else {
                orderCheckOutMenuType.setText("Breakfast");
            }

            orderCheckOutPrice.setText(selectedIndividualPrices.get(position));

            return orderCheckOutView;
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }


    public void retrieveBitmapFromCache(){

        //The imageView that you want to set to the retrieved bitmap
        ImageView deliveryDateImageView = (ImageView) findViewById(R.id.deliveryDateImageView);
        ImageView orderSummaryImageView = (ImageView) findViewById(R.id.orderSummaryImageView);
        ImageView deliveryAddressImageView = (ImageView) findViewById(R.id.deliveryAddressImageView);
        ImageView shippingOptionsImageView = (ImageView) findViewById(R.id.shippingOptionsImageView);
        ImageView paymentMethodImageView = (ImageView) findViewById(R.id.paymentOptionImageView);
        ImageView yourOrderImageView = (ImageView) findViewById(R.id.yourOrderImageView);

        //To get bitmap from cache using the key. Must cast retrieved cache Object to Bitmap
        Bitmap bitmap1 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image1");
        Bitmap bitmap2 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image2");
        Bitmap bitmap3 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image3");
        Bitmap bitmap4 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image4");
        Bitmap bitmap5 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image5");
        Bitmap bitmap6 = (Bitmap)Cache.getInstance().getLru().get("bitmap_image6");

        //Setting imageView to retrieved bitmap from cache
        deliveryDateImageView.setImageBitmap(bitmap1);
        orderSummaryImageView.setImageBitmap(bitmap2);
        deliveryAddressImageView.setImageBitmap(bitmap3);
        shippingOptionsImageView.setImageBitmap(bitmap4);
        paymentMethodImageView.setImageBitmap(bitmap5);
        yourOrderImageView.setImageBitmap(bitmap6);

    }

}
