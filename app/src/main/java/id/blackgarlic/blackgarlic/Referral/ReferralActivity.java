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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class ReferralActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String referralLink = "http://188.166.221.241:3000/app/setupreferral";

    private static List<Data> menuList;
    private static List<Integer> menuIdList;
    private static UserCredentials userCredentials;
    private static SharedPreferences sharedPreferences;

    private static ListView referralMenuListView;

    //References for the tablerow objects
    private static SimpleDraweeView selectedMenuImage1;
    private static SimpleDraweeView selectedMenuImage2;
    private static SimpleDraweeView selectedMenuImage3;
    private static TextView selectedMenuTitle1;
    private static TextView selectedMenuTitle2;
    private static TextView selectedMenuTitle3;
    private static List<SimpleDraweeView> selectedMenuImageList = new ArrayList<SimpleDraweeView>();
    private static List<TextView> selectedMenuTitleList = new ArrayList<TextView>();

    private static int amountOfMenusSelected;

    private static List<Integer> toSubmitMenuIdList = new ArrayList<Integer>();
    private static int[] toSubmitMenuIds;

    private static Button sendReferralButton;
    private static EditText referredEmailAddressEditText;
    private static EmailValidator emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        //The menus taken from the MainActivity will contain the current menus, this is because the cookbook uses a different API Endpoint
        //where it just takes all of the menus.

        //Remember that the StringRequest in the MainActivity uses the localdate to choose the current box.

        emailValidator = EmailValidator.getInstance();
        sendReferralButton = (Button) findViewById(R.id.sendReferralButton);
        referredEmailAddressEditText = (EditText) findViewById(R.id.referredEmailAddressEditText);

        sendReferralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo: When the user clicks the submit referral request button then we have to transfer all of things inside of the toSubmitmenuidlist
                //into the int array of tosubmitmenuids. IF THE VALUE IS NOT 0, because when they click the menu it removes it and replaces the
                //value with 0, otherwise we get issues with out of bounds.

                String referredEmail = "";

                final List<Integer> beforeToSubmitMenuIdList = new ArrayList<Integer>();
                for (int i = 0; i < toSubmitMenuIdList.size(); i++) {
                    beforeToSubmitMenuIdList.add(toSubmitMenuIdList.get(i));
                }

                if (!(emailValidator.isValid(referredEmailAddressEditText.getText().toString()))) {
                    SuperToast superToast = SuperToast.create(ReferralActivity.this, "Invalid Email Address Entered!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                    superToast.show();
                    return;
                } else {
                    referredEmail = referredEmailAddressEditText.getText().toString();
                }

                for (int i = 0; i < toSubmitMenuIdList.size(); i++) {
                    if (toSubmitMenuIdList.get(i) == 0) {
                        toSubmitMenuIdList.remove(i);
                        i--;
                    }
                }

                toSubmitMenuIds = new int[toSubmitMenuIdList.size()];

                for (int i = 0; i < toSubmitMenuIdList.size(); i++) {
                    toSubmitMenuIds[i] = toSubmitMenuIdList.get(i);
                }

                int customerId = Integer.valueOf(userCredentials.getCustomer_id());

                final JSONObject referralBody = new JSONObject();

                try {
                    referralBody.put("menu_ids", Arrays.toString(toSubmitMenuIds));
                    referralBody.put("referrer_id", customerId);
                    referralBody.put("referred_email", referredEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        StringRequest referralRequest = new StringRequest(Request.Method.POST, referralLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response);

                if(response.equals("Email Exists")) {
                    SuperToast superToast = SuperToast.create(ReferralActivity.this, "Can't Refer An Existing Email!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                    superToast.show();
                } else if(response.equals("Email Exists Refer")) {

                    SuperToast superToast = SuperToast.create(ReferralActivity.this, "Email Already Has Pending Referral!", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                    superToast.show();

                } else {
                    SuperToast superToast = SuperToast.create(ReferralActivity.this, response, SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP));
                    superToast.show();
                }

                toSubmitMenuIdList.clear();

                for (int i = 0; i < beforeToSubmitMenuIdList.size(); i++) {
                    toSubmitMenuIdList.add(beforeToSubmitMenuIdList.get(i));
                }

                //we needed to set the 0's back into the tosubmitmenusidlist because remember we removed the 0's, so we keep track of
                //the postions of the 0's deleted and change them back to 0 if the string request was both successful and fail.
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //On error return the 0's at the correct positions
                toSubmitMenuIdList.clear();

                for (int i = 0; i < beforeToSubmitMenuIdList.size(); i++) {
                    toSubmitMenuIdList.add(beforeToSubmitMenuIdList.get(i));
                }

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

                //So that means that there has to be another tab called referral redemption and when they enter the code the menus will come up
                //under neath and will be like "are you sure you want to redeem"? But of course have to check that the referral_status is 0 and
                //not -1 or 1.
                //Then when that happens and they do redeem, then the referral_status will be changed to 1, an order
                //will be placed, and can't be redeemed again due to referral status of 1 in database.


            }
        });

        toSubmitMenuIdList.clear();

        for (int i = 0; i < 3; i++) {
            toSubmitMenuIdList.add(0);
        }

        selectedMenuImageList.clear();
        selectedMenuTitleList.clear();

        referenceSelectedVariables();
        amountOfMenusSelected = 0;

        sharedPreferences = SplashActivity.getSharedPreferences();
        userCredentials = new Gson().fromJson(sharedPreferences.getString("Credentials", ""), UserCredentials.class);
        referralMenuListView = (ListView) findViewById(R.id.referralMenuListView);
        referralMenuListView.setOnItemClickListener(this);

        Gson gSon = new Gson();

        String menuListJson = getIntent().getExtras().getString("menulist");
        String menuIdListJson = getIntent().getExtras().getString("menuidlist");

        Type listDataType = new TypeToken<List<Data>>() {
        }.getType();
        Type integerDataType = new TypeToken<List<Integer>>() {
        }.getType();

        menuList = gSon.fromJson(menuListJson, listDataType);
        menuIdList = gSon.fromJson(menuIdListJson, integerDataType);

        referralMenuListView.setAdapter(new ReferralListViewAdapter());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (amountOfMenusSelected == 3) {
            SuperToast superToast = SuperToast.create(ReferralActivity.this, "Maximum of 3 Menus! Please Press To Remove Menu", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
            superToast.show();
            return;
        }

        String menuNameToAdd = menuList.get(position).getMenu_name();
        Uri menuUriToAdd = Uri.parse(menuList.get(position).BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(menuIdList.get(position))));
        int menuIdToAdd = menuIdList.get(position);

        for (int i = 0; i < selectedMenuTitleList.size(); i++) {
            if ((selectedMenuTitleList.get(i).getVisibility() == View.INVISIBLE) || (selectedMenuTitleList.get(i).getVisibility() == View.GONE)) {
                selectedMenuTitleList.get(i).setVisibility(View.VISIBLE);
                selectedMenuImageList.get(i).setVisibility(View.VISIBLE);

                selectedMenuTitleList.get(i).setText(menuNameToAdd);
                selectedMenuImageList.get(i).setImageURI(menuUriToAdd);
                amountOfMenusSelected++;

                toSubmitMenuIdList.set(i, menuIdToAdd);

                break;
            }
        }

        if (amountOfMenusSelected == 1) {
            sendReferralButton.setEnabled(true);
            sendReferralButton.setBackgroundResource(R.drawable.checkoutbutton);
            sendReferralButton.setTextColor(getResources().getColor(R.color.BGDARKGREEN));
        }

    }

    public class ReferralListViewAdapter extends BaseAdapter {

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

    public void referenceSelectedVariables() {

        selectedMenuImage1 = (SimpleDraweeView) findViewById(R.id.selectedMenuImage1);
        selectedMenuImage2 = (SimpleDraweeView) findViewById(R.id.selectedMenuImage2);
        selectedMenuImage3 = (SimpleDraweeView) findViewById(R.id.selectedMenuImage3);

        selectedMenuTitle1 = (TextView) findViewById(R.id.selectedMenuTitle1);
        selectedMenuTitle2 = (TextView) findViewById(R.id.selectedMenuTitle2);
        selectedMenuTitle3 = (TextView) findViewById(R.id.selectedMenuTitle3);

        selectedMenuImageList.add(selectedMenuImage1);
        selectedMenuImageList.add(selectedMenuImage2);
        selectedMenuImageList.add(selectedMenuImage3);

        selectedMenuTitleList.add(selectedMenuTitle1);
        selectedMenuTitleList.add(selectedMenuTitle2);
        selectedMenuTitleList.add(selectedMenuTitle3);

        for (int i = 0; i < selectedMenuImageList.size(); i++) {
            final int finalI = i;
            selectedMenuImageList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("Clicked Menu: ", String.valueOf(finalI));

                    for (int i = 0; i < toSubmitMenuIdList.size(); i++) {
                        Log.e("B Menu Ids: ", String.valueOf(toSubmitMenuIdList.get(i)));
                    }

                    Log.e("---", "---");

                    selectedMenuImageList.get(finalI).setImageURI(null);
                    selectedMenuTitleList.get(finalI).setText("");

                    toSubmitMenuIdList.set(finalI, 0);
                    selectedMenuImageList.get(finalI).setVisibility(View.GONE);
                    selectedMenuTitleList.get(finalI).setVisibility(View.GONE);

                    for (int i = 0; i < toSubmitMenuIdList.size(); i++) {
                        Log.e("A Menu Ids: ", String.valueOf(toSubmitMenuIdList.get(i)));
                    }

                    Log.e("----------", "--------------------------------");

                    //Looping through everytime to see if everything in the list is gone, if it is then set all to invisible so they can
                    //add menus again

                    int count = 0;

                    for (int i = 0; i < selectedMenuImageList.size(); i++) {
                        if (selectedMenuImageList.get(i).getVisibility() == View.GONE) {
                            count++;
                        }

                        if (count == 3) {
                            Log.e("All Gone: ", "True");
                            for (int j = 0; j < selectedMenuImageList.size(); j++) {
                                selectedMenuImageList.get(i).setVisibility(View.INVISIBLE);
                                selectedMenuTitleList.get(i).setVisibility(View.INVISIBLE);

                                sendReferralButton.setEnabled(false);
                                sendReferralButton.setBackgroundResource(R.drawable.greyedoutloading);
                                sendReferralButton.setTextColor(getResources().getColor(R.color.BGGREY));
                                //sendReferralButton.setText("Send Referral Request");
                            }
                        }
                    }

                    amountOfMenusSelected--;

                    if (amountOfMenusSelected == 0) {
                        sendReferralButton.setEnabled(false);
                        sendReferralButton.setBackgroundResource(R.drawable.greyedoutloading);
                        sendReferralButton.setTextColor(getResources().getColor(R.color.BGGREY));
                    }
                }
            });
        }


    }

}
