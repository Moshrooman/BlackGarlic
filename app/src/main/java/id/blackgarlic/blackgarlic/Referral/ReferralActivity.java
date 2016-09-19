package id.blackgarlic.blackgarlic.Referral;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Element;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import id.blackgarlic.blackgarlic.CircleProgressBarDrawable;
import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.LogInScreen;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.MenuId;
import id.blackgarlic.blackgarlic.model.Menus;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReferralActivity extends AppCompatActivity {

    private static final String referralLink = "http://188.166.221.241:3000/app/setupreferral";

    private static List<Data> menuList;
    private static List<Integer> menuIdList;
    private static UserCredentials userCredentials;
    private static SharedPreferences sharedPreferences;

    private static ListView referralMenuListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        //The menus taken from the MainActivity will contain the current menus, this is because the cookbook uses a different API Endpoint
        //where it just takes all of the menus.

        //Remember that the StringRequest in the MainActivity uses the localdate to choose the current box.

        sharedPreferences = SplashActivity.getSharedPreferences();
        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);
        referralMenuListView = (ListView) findViewById(R.id.referralMenuListView);

        Gson gSon = new Gson();

        String menuListJson = getIntent().getExtras().getString("menulist");
        String menuIdListJson = getIntent().getExtras().getString("menuidlist");

        Type listDataType = new TypeToken<List<Data>>(){}.getType();
        Type integerDataType = new TypeToken<List<Integer>>(){}.getType();

        menuList = gSon.fromJson(menuListJson, listDataType);
        menuIdList = gSon.fromJson(menuIdListJson, integerDataType);

        //Need to actually set the menu ids to the menu ids that the user clicks.
        int[] menuIds = {23, 76, 26, 92, 10};
        int customerId = Integer.valueOf(userCredentials.getCustomer_id());
        String referredEmail = "justinkwik@test.com";

        final JSONObject referralBody = new JSONObject();

        try {
            referralBody.put("menu_ids", Arrays.toString(menuIds));
            referralBody.put("referrer_id", customerId);
            referralBody.put("referred_email", referredEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest referralRequest = new StringRequest(Request.Method.POST, referralLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Referral Code: ", response);
                referralMenuListView.setAdapter(new ReferralListViewAdapter());
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
                return referralBody.toString().getBytes();
            }
        };

        ConnectionManager.getInstance(ReferralActivity.this).add(referralRequest);

        //Array of menu_ids
        //Referrer Customer Id
        //Referred Email (can't be existing customer)

        //Referral Status will just be 0 by default.
        //Referral Code will be created in the backend

        //Then when this is done it will generate a random code and insert that into the unique_code column, then the user has to enter
        //the code in order to redeem the menus.

        //So that means that there has to be another tab called referral redemption and when they enter the code the menus will come up
        //under neath and will be like "are you sure you want to redeem"? But of course have to check that the referral_status is 0 and
        //not -1 or 1.
        //Then when that happens and they do redeem, then the referral_status will be changed to 1 and can't be redeemed again

    }

    public void setReferralVariables() {
        referralMenuListView = (ListView) findViewById(R.id.referralMenuListView);
    }

    public class ReferralListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public Object getItem(int position) {
            return menuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return menuIdList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ReferralActivity.this).inflate(R.layout.referral_listview_row, null);
            }

            SimpleDraweeView referralListViewDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.referralListViewDraweeView);
            TextView referralListViewMenuName = (TextView) convertView.findViewById(R.id.referralListViewMenuName);
            TextView referralListViewMenuType = (TextView) convertView.findViewById(R.id.referralListViewMenuType);

            referralListViewDraweeView.setImageURI(Uri.parse(menuList.get(position).BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(menuIdList.get(position)))));
            referralListViewMenuName.setText(menuList.get(position).getMenu_name());
            referralListViewMenuType.setText(menuList.get(position).getMenu_type());

            if ((menuList.get(position).getMenu_type().equals("3")) || (menuList.get(position).getMenu_type().equals("5"))) {
                referralListViewMenuType.setText("Original");
            } else if ((menuList.get(position).getMenu_type().equals("4")) || (menuList.get(position).getMenu_type().equals("6"))) {
                referralListViewMenuType.setText("Breakfast");
            } else if ((menuList.get(position).getMenu_type().equals("7"))) {
                referralListViewMenuType.setText("Kids");
            } else {
                referralListViewMenuType.setText("N/A");
            }

            CircleProgressBarDrawable progressBar = new CircleProgressBarDrawable();
            progressBar.setBackgroundColor(getResources().getColor(R.color.BGGREY));
            progressBar.setColor(getResources().getColor(R.color.BGGREEN));

            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
            GenericDraweeHierarchy hierarchy = builder.setFadeDuration(300).setProgressBarImage(progressBar).build();

            referralListViewDraweeView.setHierarchy(hierarchy);

            return convertView;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
