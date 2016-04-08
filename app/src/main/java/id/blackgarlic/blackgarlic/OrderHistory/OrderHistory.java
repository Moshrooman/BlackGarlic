package id.blackgarlic.blackgarlic.OrderHistory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class OrderHistory extends AppCompatActivity {

    private static String orderHistoryLink = "http://10.0.3.2:3000/app/orderhistory";

    private static List<List<menuObjects>> menuObjectList;

    private static BaseArrayObjects[] orderHistoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
        UserCredentials credentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);

        final JSONObject orderHistoryBody = new JSONObject();
        try {

            orderHistoryBody.put("customer_id", String.valueOf(credentials.getCustomer_id()));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        StringRequest orderHistoryRequest = new StringRequest(Request.Method.POST, orderHistoryLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("OH Response: ", response);

                BaseArrayObjects[] baseArrayObjects = new Gson().fromJson(response, BaseArrayObjects[].class);

                orderHistoryArray = baseArrayObjects;

                List<List<menuObjects>> menuArrayList = new ArrayList<>();

                for (int i = 0; i < baseArrayObjects.length; i++) {

                    menuArrayList.add(baseArrayObjects[i].getMenuObjectList());

                }

                menuObjectList = menuArrayList;

                ExpandableListViewAdapter adapter = new ExpandableListViewAdapter();

                ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

                expandableListView.setAdapter(adapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", "True");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return orderHistoryBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        ConnectionManager.getInstance(OrderHistory.this).add(orderHistoryRequest);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainActivityIntent = new Intent(OrderHistory.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainActivityIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class ExpandableListViewAdapter extends BaseExpandableListAdapter {


        //How many headers there are gonna be
        @Override
        public int getGroupCount() {
            return orderHistoryArray.length;
        }

        //How many of the same children are gonna be in each header
        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return orderHistoryArray[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return menuObjectList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) OrderHistory.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandablelistviewheader, null);
            }

            TextView orderIdTextView = (TextView) convertView.findViewById(R.id.orderIdTextView);
            TextView totalTextView = (TextView) convertView.findViewById(R.id.totalTextView);
            TextView paymentStatusTextView = (TextView) convertView.findViewById(R.id.paymentStatusTextView);
            TextView orderStatusTextView = (TextView) convertView.findViewById(R.id.orderStatusTextView);
            TextView deliveryDateTextView = (TextView) convertView.findViewById(R.id.deliveryDateTextView);

            orderIdTextView.setText(String.valueOf(orderHistoryArray[groupPosition].getUnique_id()));
            totalTextView.setText(String.valueOf(orderHistoryArray[groupPosition].getGrandtotal()));
            paymentStatusTextView.setText(String.valueOf(orderHistoryArray[groupPosition].getPayment_status()));
            orderStatusTextView.setText(String.valueOf(orderHistoryArray[groupPosition].getOrder_status()));
            deliveryDateTextView.setText(String.valueOf(orderHistoryArray[groupPosition].getOrder_date()));

            return convertView;

        }

        //So here I need to have parsed the orderhistory api response's orders array. So every object in my array will have access to
        //The array inside called menus, and all of the properties inside including order id and shit
        //So basically I need to parse an ArrayList of objects, call it OrderHistoryObjects
        //Then inside that I need to get the order_id, unique_id, grandtotal, payment_status, order_status, delivery_date, address_content, mobile
        //And inside this I also need to parse an arraylist of objects called OrderHistoryMenuEntryObjects
        //Inside OrderHistoryMenuEntryObjects I need to get portion, menu_id, menu_name, menu_type

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) OrderHistory.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandablelistviewexpanded, null);
            }



            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
