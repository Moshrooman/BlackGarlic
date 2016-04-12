package id.blackgarlic.blackgarlic.OrderHistory;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class OrderHistory extends AppCompatActivity {

    private static String orderHistoryLink = "http://jdev.blackgarlic.id:7000/app/orderhistory";

    private static List<List<menuObjects>> menuObjectList;

    private static List<BaseArrayObjects> orderHistoryArray;

    private static UserCredentials userCredentials;

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        userCredentials = null;

        SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
        UserCredentials credentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);
        userCredentials = credentials;

        final JSONObject orderHistoryBody = new JSONObject();
        try {

            orderHistoryBody.put("customer_id", String.valueOf(credentials.getCustomer_id()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("OH BODY", String.valueOf(orderHistoryBody));


        StringRequest orderHistoryRequest = new StringRequest(Request.Method.POST, orderHistoryLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("OH Response: ", response);

                BaseArrayObjects[] baseArrayObjects = new Gson().fromJson(response, BaseArrayObjects[].class);

                orderHistoryArray = new LinkedList<BaseArrayObjects>(Arrays.asList(baseArrayObjects));

                List<List<menuObjects>> menuArrayList = new ArrayList<>();

                for (int i = 0; i < baseArrayObjects.length; i++) {

                    menuArrayList.add(baseArrayObjects[i].getMenuObjectList());

                }

                menuObjectList = menuArrayList;

                for (int i = 0; i < menuObjectList.size(); i++) {

                    if (menuObjectList.get(i).size() == 0) {
                        menuObjectList.remove(i);
                        orderHistoryArray.remove(i);
                        i = i - 1;
                    }
                }

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
            return orderHistoryArray.size();
        }

        //How many of the same children are gonna be in each header
        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return orderHistoryArray.get(groupPosition);
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
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            LayoutInflater infalInflater = (LayoutInflater) OrderHistory.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandablelistviewheader, null);


            TextView orderIdTextView = (TextView) convertView.findViewById(R.id.orderIdTextView);
            TextView totalTextView = (TextView) convertView.findViewById(R.id.totalTextView);
            TextView paymentStatusTextView = (TextView) convertView.findViewById(R.id.paymentStatusTextView);
            TextView orderStatusTextView = (TextView) convertView.findViewById(R.id.orderStatusTextView);
            TextView deliveryDateTextView = (TextView) convertView.findViewById(R.id.deliveryDateTextView);
            ImageView expandedIndicatorImageView = (ImageView) convertView.findViewById(R.id.expandedIndicatorImageView);

            if (isExpanded) {
                expandedIndicatorImageView.setImageResource(R.drawable.arrowdown);
                orderIdTextView.setTextColor(getResources().getColor(R.color.BGGREEN));
                totalTextView.setTextColor(getResources().getColor(R.color.BGGREEN));
                paymentStatusTextView.setTextColor(getResources().getColor(R.color.BGGREEN));
                orderStatusTextView.setTextColor(getResources().getColor(R.color.BGGREEN));
                deliveryDateTextView.setTextColor(getResources().getColor(R.color.BGGREEN));
            } else {
                expandedIndicatorImageView.setImageResource(R.drawable.arrowright);
                orderIdTextView.setTextColor(getResources().getColor(R.color.grey));
                totalTextView.setTextColor(getResources().getColor(R.color.grey));
                paymentStatusTextView.setTextColor(getResources().getColor(R.color.grey));
                orderStatusTextView.setTextColor(getResources().getColor(R.color.grey));
                deliveryDateTextView.setTextColor(getResources().getColor(R.color.grey));
            }

            orderIdTextView.setText(String.valueOf(orderHistoryArray.get(groupPosition).getUnique_id()));
            totalTextView.setText(new DecimalFormat().format(orderHistoryArray.get(groupPosition).getGrandtotal()));

            if (orderHistoryArray.get(groupPosition).getPayment_status() == 1) {
                paymentStatusTextView.setText("Paid");
            } else {
                paymentStatusTextView.setText("Unpaid");
            }

            if(orderHistoryArray.get(groupPosition).getOrder_status() == 2) {
                orderStatusTextView.setText("Completed");
            } else if (orderHistoryArray.get(groupPosition).getOrder_status() == 1) {
                orderStatusTextView.setText("Waiting For Delivery");
            } else if (orderHistoryArray.get(groupPosition).getOrder_status() == 0) {
                orderStatusTextView.setText("Waiting For Payment");
            } else {
                orderStatusTextView.setText("Cancelled");
            }


            deliveryDateTextView.setText(String.valueOf(orderHistoryArray.get(groupPosition).getOrder_date()));

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

            LayoutInflater infalInflater = (LayoutInflater) OrderHistory.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandablelistviewexpanded, null);

            //Start of making table rows for the orders

            TableLayout ordersTableLayout = (TableLayout) convertView.findViewById(R.id.ordersTableLayout);

            for (int i = 0; i < menuObjectList.get(groupPosition).size(); i++) {

                //Networkimageview work

                TableRow orderstablelayoutinflate = (TableRow) View.inflate(OrderHistory.this, R.layout.orderstablelayoutinflate, null);
                orderstablelayoutinflate.setTag(i);
                NetworkImageView networkImageView = (NetworkImageView) orderstablelayoutinflate.findViewById(R.id.menuImageOrderHistory);
                networkImageView.setImageUrl(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(menuObjectList.get(groupPosition).get(i).getMenu_id())),
                        ConnectionManager.getImageLoader(OrderHistory.this));

                //Menu Name TextView
                TextView menuNameOrderHistory = (TextView) orderstablelayoutinflate.findViewById(R.id.menuNameOrderHistory);
                menuNameOrderHistory.setText(String.valueOf(menuObjectList.get(groupPosition).get(i).getMenu_name()));

                //Menu Type TextView
                TextView menuTypeOrderHistory = (TextView) orderstablelayoutinflate.findViewById(R.id.menuTypeOrderHistory);

                if ((menuObjectList.get(groupPosition).get(i).getMenu_type() == 3) || (menuObjectList.get(groupPosition).get(i).getMenu_type() == 5)) {
                    menuTypeOrderHistory.setText("Original");
                } else if ((menuObjectList.get(groupPosition).get(i).getMenu_type() == 4) || (menuObjectList.get(groupPosition).get(i).getMenu_type() == 6)){
                    menuTypeOrderHistory.setText("Breakfast");
                } else if ((menuObjectList.get(groupPosition).get(i).getMenu_type() == 1)) {
                    menuTypeOrderHistory.setText("Express");
                } else {
                    menuTypeOrderHistory.setText("Family");
                }

                //Portion Text View
                TextView portionOrderHistory = (TextView) orderstablelayoutinflate.findViewById(R.id.portionOrderHistory);

                if (menuTypeOrderHistory.getText().equals("Express")) {
                    portionOrderHistory.setText("2P");
                } else if (menuTypeOrderHistory.getText().equals("Family")) {
                    portionOrderHistory.setText("4P");
                } else {
                    portionOrderHistory.setText(menuObjectList.get(groupPosition).get(i).getPortion() + "P");
                }

                //Price Text View
                TextView priceOrderHistory = (TextView) orderstablelayoutinflate.findViewById(R.id.priceOrderHistory);

                if (menuTypeOrderHistory.getText().equals("Express")) {
                    priceOrderHistory.setText("100,000");
                } else if (menuTypeOrderHistory.getText().equals("Family")) {
                    priceOrderHistory.setText("150,000");
                } else {


                    if (((menuObjectList.get(groupPosition).get(i).getMenu_type() == 3) || (menuObjectList.get(groupPosition).get(i).getMenu_type() == 5))
                            && (menuObjectList.get(groupPosition).get(i).getPortion() == 2)){
                        priceOrderHistory.setText("80,000");
                    } else if (((menuObjectList.get(groupPosition).get(i).getMenu_type() == 3) || (menuObjectList.get(groupPosition).get(i).getMenu_type() == 5))
                            && (menuObjectList.get(groupPosition).get(i).getPortion() == 4)) {
                        priceOrderHistory.setText("140,000");
                    } else if (((menuObjectList.get(groupPosition).get(i).getMenu_type() == 4) || (menuObjectList.get(groupPosition).get(i).getMenu_type() == 6))
                            && (menuObjectList.get(groupPosition).get(i).getPortion() == 2)) {
                        priceOrderHistory.setText("100,000");
                    } else {
                        priceOrderHistory.setText("150,000");
                    }

                }

                ordersTableLayout.addView(orderstablelayoutinflate);

            }

            TextView orderhistoryNameTextView = (TextView) convertView.findViewById(R.id.orderhistoryNameTextView);
            TextView orderhistoryAddressTextView = (TextView) convertView.findViewById(R.id.orderhistoryAddressTextView);
            TextView orderhistoryPhoneTextView = (TextView) convertView.findViewById(R.id.orderhistoryPhoneTextView);

            orderhistoryNameTextView.setText(userCredentials.getCustomer_name());
            orderhistoryAddressTextView.setText(orderHistoryArray.get(groupPosition).getAddress_content());
            orderhistoryPhoneTextView.setText(orderHistoryArray.get(groupPosition).getMobile());

            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
