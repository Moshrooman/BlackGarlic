package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.MenuId;
import id.blackgarlic.blackgarlic.model.Menus;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import id.blackgarlic.blackgarlic.welcome.WelcomeActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//Implementation for the adding the menus to box when logged out needs to be applied to:

//1. When they click log in when logged out.

//These are the things that MainActivity has to start with when they have menu's in box and log in.

//1. Change currentMenuList;
//2. Change currentMenuIdList;
//3. Change currentSelectedMenuListUrls;
//4. Change portionSizes;
//5. Change individualPrices;
//6. Subtotal cost

//And I need to set the new adapter with these things

//1. listViewOrderSummary.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, currentMenuListUrls, portionSizeList, individualPricesAdapter));

//And finally, I need to set the subtotalpricetextview.

//subTotalPriceTextView.setText("SUBTOTAL: IDR " + new DecimalFormat().format(subTotalCost));

//Then I moved these 2 instantiations up above checking if is logged in is true.

//subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);
//listViewOrderSummary = (ListView) findViewById(R.id.orderSummaryListView);
//orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
//orderQuantityTextView = (TextView) findViewById(R.id.orderQuantityTextView);

//Then I have to go into blackgarlicadapter and set all of those to the ones in the sharedpreference.

//Then I have to set the selectedBoxImageView by first doing above, moving instantiations to the top and also adding this into the if:

//orderQuantityTextView.bringToFront();
//
// if (currentMenuList.size() == 0) {
//     orderBoxImageView.setImageResource(R.drawable.orderboxone);
//     orderQuantityTextView.setVisibility(View.GONE);
// } else {
//     orderBoxImageView.setImageResource(R.drawable.orderboxtwopng);
//     orderQuantityTextView.setVisibility(View.VISIBLE);
//     orderQuantityTextView.setText(String.valueOf(currentMenuList.size()));
// }

//Then, after I have set all of these, then I delete all of the keys inside of the sharedpreference,and also clear all lists when they:

//1. Log out, first had to check if currentMenuslist == null, if not then delete all shared preference, and clear all lists.
//2. Click back when in mainactivity, closing the application. Same as above.
//3. When they click placeorder and have a successful response. Gonna do this later.

public class MainActivity extends AppCompatActivity implements BlackGarlicAdapter.MyListItemClickListener, AdapterView.OnItemClickListener {

    private RecyclerView recyclerView;

    private static UserCredentials userCredentials;
    private static List<Data> menuList;
    private static List<Integer> menuIdList;
    private static Integer boxId;
    private static TextView orderQuantityTextView;
    private static ImageView orderBoxImageView;
    private static com.sothree.slidinguppanel.SlidingUpPanelLayout sliding_layout;
    private static ListView listViewOrderSummary;

    private static List<Data> currentMenuList;
    private static List<Integer> currentMenuIdList;
    private static List<String> currentSelectedMenuListUrls;
    private static List<String> portionSizes;
    private static List<String> individualPrices;

    private static int subTotalCost;

    private static DrawerLayout drawerLayout;
    private static ListView drawerListView;
    private static String[] drawerEntries;

    private static ImageView navBarToggleImageView;
    private static int numberOfTimesClicked = 0;
    private static int currentRotation = 0;

    private static Button proceedToCheckoutButton;

    private static boolean isLoggedIn = false;

    private static TextView subTotalPriceTextView;

    public static List<Data> getCurrentMenuList() {
        return currentMenuList;
    }

    public static List<Integer> getCurrentMenuIdList() {
        return currentMenuIdList;
    }

    public static List<String> getCurrentSelectedMenuListUrls() {
        return currentSelectedMenuListUrls;
    }

    public static List<String> getPortionSizes() {
        return portionSizes;
    }

    public static int getSubTotalCost() {
        return subTotalCost;
    }

    public static List<String> getIndividualPrices() {
        return individualPrices;
    }

    public static Integer getBoxId() {
        return boxId;
    }

    public static void setIsLoggedIn(boolean isLoggedIn) {
        MainActivity.isLoggedIn = isLoggedIn;
    }

    public static boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);
        listViewOrderSummary = (ListView) findViewById(R.id.orderSummaryListView);
        orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
        orderQuantityTextView = (TextView) findViewById(R.id.orderQuantityTextView);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        //Start of Doing work to add the boxes they selected before logging in to after they logged in

        if ((isLoggedIn == true) && (sharedPreferences.contains("currentMenuList"))) {

            Gson gson = new Gson();

            Type dataType = new TypeToken<List<Data>>(){}.getType();
            Type integerType = new TypeToken<List<Integer>>(){}.getType();
            Type stringType = new TypeToken<List<String>>(){}.getType();

            currentMenuList = gson.fromJson(sharedPreferences.getString("currentMenuList", ""), dataType);
            currentMenuIdList = gson.fromJson(sharedPreferences.getString("currentMenuIdList", ""), integerType);
            currentSelectedMenuListUrls = gson.fromJson(sharedPreferences.getString("currentSelectedMenuListUrls", ""), stringType);
            portionSizes = gson.fromJson(sharedPreferences.getString("currentPortionSizes", ""), stringType);
            individualPrices = gson.fromJson(sharedPreferences.getString("currentIndividualPrices", ""), stringType);
            subTotalCost = sharedPreferences.getInt("currentSubtotalCost", 0);

            listViewOrderSummary.setAdapter(new MyAdapter(currentMenuList, currentMenuIdList, currentSelectedMenuListUrls, portionSizes, individualPrices));

            subTotalPriceTextView.setText("SUBTOTAL: IDR " + new DecimalFormat().format(subTotalCost));

            orderQuantityTextView.bringToFront();

            if (currentMenuList.size() == 0) {
                orderBoxImageView.setImageResource(R.drawable.orderboxone);
                orderQuantityTextView.setVisibility(View.GONE);
            } else {
                orderBoxImageView.setImageResource(R.drawable.orderboxtwopng);
                orderQuantityTextView.setVisibility(View.VISIBLE);
                orderQuantityTextView.setText(String.valueOf(currentMenuList.size()));
            }


        }

        //End of doing work to add the boxes they selected before logging in to after they logged in.

        if (sharedPreferences.contains("Credentials")) {
            String json = sharedPreferences.getString("Credentials", "");
            userCredentials = new Gson().fromJson(json, UserCredentials.class);
            isLoggedIn = true;
        } else {
            userCredentials = LogInScreen.getUserCredentials();
            isLoggedIn = false;
        }

        numberOfTimesClicked = 0;
        currentRotation = 0;

        sliding_layout = (com.sothree.slidinguppanel.SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        sliding_layout.setDragView(findViewById(R.id.tabRelativeLayout));

        drawerListView = (ListView) findViewById(R.id.drawerListView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerEntries = getResources().getStringArray(R.array.navBarEntires);

        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);

        if (isLoggedIn == false) {
            Button mainActivityLogInButton = (Button) findViewById(R.id.mainActivityLogInButton);
            Button mainActivitySignUpButton = (Button) findViewById(R.id.mainActivitySignUpButton);

            mainActivityLogInButton.setVisibility(View.VISIBLE);
            mainActivitySignUpButton.setVisibility(View.VISIBLE);
            welcomeTextView.setVisibility(View.GONE);
            drawerEntries = new String[3];
            drawerEntries[0] = "Home";
            drawerEntries[1] = "Log In";
            drawerEntries[2] = "Create Account";

            mainActivityLogInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(MainActivity.this, LogInScreen.class);
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putBoolean("nonlogin", false);
                    editor.commit();
                    startActivity(loginIntent);
                }
            });

            mainActivitySignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signupIntent = new Intent(MainActivity.this, CreateAccount.class);
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putBoolean("nonlogin", false);
                    editor.commit();
                    startActivity(signupIntent);
                }
            });
        } else {
            welcomeTextView.setVisibility(View.VISIBLE);
            String welcomeTextViewString = (String) welcomeTextView.getText();
            welcomeTextViewString = welcomeTextViewString.replace("Name", userCredentials.getCustomer_name());
            welcomeTextView.setText(welcomeTextViewString);

            drawerEntries = new String[3];
            drawerEntries[0] = "Home";
            drawerEntries[1] = "My Account";
            drawerEntries[2] = "Log Out";
        }

        drawerListView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, drawerEntries));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerListView.setOnItemClickListener(this);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                numberOfTimesClicked++;

                //Do Nothing Here Because it only runs these things after it has been opened.
                //So we want to set the visibility to View.VISIBLE when we call to open it which is in the onclicklistener for the
                //navBarToggleImageView
                //Add to number of times clicked

                Log.e("Own Rotation", String.valueOf(currentRotation));

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                if (currentRotation == 90) {

                    Animation ani = new RotateAnimation(
                            90, /* from degree*/
                            0, /* to degree */
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ani.setDuration(400);

                    ani.setFillAfter(true);
                    navBarToggleImageView.startAnimation(ani);
                    currentRotation = 0;

                }

                numberOfTimesClicked++;

                //After we call drawerLayout.closeDrawer, after it finishes we want to set the view to View.GONE
                //So that we can click the stuff underneath it
                //Add to number of times clicked
                drawerLayout.setVisibility(View.GONE);

                Log.e("Own Rotation", String.valueOf(currentRotation));

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

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

        //Joda-Time Library, parsing it into whitesignupbutton jodatime object, easier to add dates.
        LocalDate localDate = new LocalDate(localTime);

        Log.e("Before Date: ", String.valueOf(localDate));

        Integer checkIfAfter3Integer = Integer.valueOf(checkIfAfter3);

        //Need to check if greater than 15, not just equals to 15
        if ((checkIfAfter3Integer.intValue() >= 15)) {
            localDate = localDate.plusDays(1);
        }

        //Add 2 days
        localDate = localDate.plusDays(2);

        //Check if sunday or monday, if it is, skip to tuesday
        if (localDate.getDayOfWeek() == 7) {
            localDate = localDate.plusDays(2);
        } else if (localDate.getDayOfWeek() == 1) {
            localDate = localDate.plusDays(1);
        }

        Log.e("After Date: ", String.valueOf(localDate));

        String BlackGarlicMenusNew = "http://api.blackgarlic.id:7000/app/menu/"+String.valueOf(localDate)+"";

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        navBarToggleImageView = (ImageView) findViewById(R.id.navBarToggleImageView);

        navBarToggleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((numberOfTimesClicked == 0) || (numberOfTimesClicked % 2 == 0)) {

                    Animation ani = new RotateAnimation(
                            0, /* from degree*/
                            90, /* to degree */
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ani.setDuration(400);

                    ani.setFillAfter(true);
                    navBarToggleImageView.startAnimation(ani);
                    currentRotation = 90;

                    drawerLayout.setVisibility(View.VISIBLE);
                    drawerLayout.openDrawer(Gravity.LEFT);
                    drawerLayout.bringToFront();

                } else {

                    Animation ani = new RotateAnimation(
                            90, /* from degree*/
                            0, /* to degree */
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    ani.setDuration(400);

                    ani.setFillAfter(true);
                    navBarToggleImageView.startAnimation(ani);
                    currentRotation = 0;

                    drawerLayout.closeDrawer(Gravity.LEFT);

                }
            }
        });

        proceedToCheckoutButton = (Button) findViewById(R.id.proceedToCheckOutButton);
        proceedToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proceedToCheckoutButton.setEnabled(false);

                if (isLoggedIn == true) {
                    if (null == currentMenuList || currentMenuList.size() == 0) {
                        proceedToCheckoutButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "You Have Nothing In Your Box!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent proceedToCheckOutIntent = new Intent(MainActivity.this, CheckOut.class);
                        startActivity(proceedToCheckOutIntent);
                    }
                } else {

                    if (null == currentMenuList || currentMenuList.size() == 0) {
                        proceedToCheckoutButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "You Have Nothing In Your Box!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent logInIntent = new Intent(MainActivity.this, LogInScreen.class);
                        startActivity(logInIntent);
                    }

                }
            }
        });

        StringRequest request = new StringRequest(Request.Method.GET, BlackGarlicMenusNew, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Response: ", response);

                Toast.makeText(MainActivity.this, "Taking From Internet", Toast.LENGTH_SHORT).show();

                MenuId menuId = new Gson().fromJson(response, MenuId.class);
                Menus menus = new Gson().fromJson(response, Menus.class);

                menuList = menus.getDataList();
                menuIdList = menuId.getMenuIds();
                boxId = menuId.getBoxId();

                for (int i = 0; i < menuList.size(); i++) {
                    int currentsize = menuList.size();

                    if (menuList.get(i).getMenu_type().equals("1")) {
                        menuList.remove(i);
                        menuIdList.remove(i);

                        if (menuList.size() != currentsize) {
                            i = i - 1;
                        }
                    }
                }

                for (int i = 0; i < menuList.size(); i++) {

                    if ((menuList.get(i).getMenu_type().equals("4")) || (menuList.get(i).getMenu_type().equals("6"))) {
                        Data currentBreakfastMenu = menuList.get(i);
                        Integer currentBreakfastMenuIntenger = menuIdList.get(i);

                        menuList.remove(i);
                        menuIdList.remove(i);

                        menuList.add(0, currentBreakfastMenu);
                        menuIdList.add(0, currentBreakfastMenuIntenger);
                    }

                }

                BlackGarlicAdapter blackGarlicAdapter = new BlackGarlicAdapter(menuList, menuIdList, MainActivity.this, MainActivity.this);

//              BlackGarlicDAO.getInstance().storeMenus(MainActivity.this, firstMenuList);

                recyclerView.setAdapter(blackGarlicAdapter);
                recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(blackGarlicAdapter));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                if (BlackGarlicDAO.getInstance().getMenusFromDB(MainActivity.this).length != 0) {
//
//                    Menu[] menuListDatabase = BlackGarlicDAO.getInstance().getMenusFromDB(MainActivity.this);
//                    Toast.makeText(MainActivity.this, "Taking From Database", Toast.LENGTH_SHORT).show();
//                    BlackGarlicAdapter blackGarlicAdapter2 = new BlackGarlicAdapter(menuListDatabase, MainActivity.this, MainActivity.this);
//                    recyclerView.setAdapter(blackGarlicAdapter2);
//
//                } else {
//
//                    Toast.makeText(MainActivity.this, "Please Connect To The Internet!", Toast.LENGTH_LONG).show();
//
//                }

            }
        });

        ConnectionManager.getInstance(MainActivity.this).add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();

        proceedToCheckoutButton.setEnabled(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        SharedPreferences.Editor editor = SplashActivity.getSharedPreferences().edit();

        if (keyCode == event.KEYCODE_BACK) {
            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {

                if (isLoggedIn == true) {

                    editor.remove("currentMenuList");
                    editor.remove("currentMenuIdList");
                    editor.remove("currentSelectedMenuListUrls");
                    editor.remove("currentPortionSizes");
                    editor.remove("currentIndividualPrices");
                    editor.remove("currentSubtotalCost");
                    editor.commit();

                    currentMenuList.clear();
                    currentMenuIdList.clear();
                    currentSelectedMenuListUrls.clear();
                    portionSizes.clear();
                    individualPrices.clear();
                    subTotalCost = 0;

                }


                finish();
            }
        }

        return false;

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void OnItemClick(RippleView rippleView, List<Data> selectedMenuList, List<Integer> selectedMenuIdList, String menuAdded, List<String> currentMenuListUrls, List<String> portionSizeList, List<String> individualPricesAdapter) {

        subTotalCost = subTotalCost - subTotalCost;

        currentMenuList = selectedMenuList;
        currentMenuIdList = selectedMenuIdList;
        currentSelectedMenuListUrls = currentMenuListUrls;
        portionSizes = portionSizeList;
        individualPrices = individualPricesAdapter;

        for (int i = 0; i < individualPrices.size(); i++) {

            String cost = individualPrices.get(i).replace("IDR", "");
            cost = cost.replace(".", "");
            cost = cost.replace(" ", "");

            int intcost = Integer.parseInt(cost);

            subTotalCost = subTotalCost + intcost;

        }

        Log.e("SubTotalCost: ", String.valueOf(subTotalCost));

        orderQuantityTextView.bringToFront();

        if (selectedMenuList.size() == 0) {
            orderBoxImageView.setImageResource(R.drawable.orderboxone);
            orderQuantityTextView.setVisibility(View.GONE);
        } else {
            orderBoxImageView.setImageResource(R.drawable.orderboxtwopng);
            orderQuantityTextView.setVisibility(View.VISIBLE);
            orderQuantityTextView.setText(String.valueOf(selectedMenuList.size()));
        }

        for (int i = 0; i < selectedMenuList.size(); i++) {
            Log.e("Selected Menu: ", selectedMenuList.get(i).getMenu_name() + ": " + String.valueOf(selectedMenuIdList.get(i)));
        }

        subTotalPriceTextView.setText("SUBTOTAL: IDR " + new DecimalFormat().format(subTotalCost));

        Log.e("---------------", "-----------------");

        Toast.makeText(MainActivity.this, "Added: " + menuAdded, Toast.LENGTH_SHORT).show();

        listViewOrderSummary.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, currentMenuListUrls, portionSizeList, individualPricesAdapter));

    }

    //Finished radio buttons through all in blackgarlic adapter using android onclick, creating new list of strings adding menu urls
    //only concatenating _4 if the menutype is 3.
    //Finished the 4 person and 2 person (in whitesignupbutton separate list of strings)
    //Finished the individual prices in each list view item (in whitesignupbutton separate list of strings)
    //Created subtotal int by parsing all strings in individual price list and adding together
    //Created whitesignupbutton subtotal text view, setting it the to the subtotal price variable.

    public void clearAll(View view) {

        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        TextView subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);

        view.startAnimation(animScale);

        if (null == currentMenuList || currentMenuList.size() == 0) {
            return;
        } else {
            currentMenuList.clear();
            currentMenuIdList.clear();
            currentSelectedMenuListUrls.clear();
            individualPrices.clear();
            portionSizes.clear();
            subTotalCost = subTotalCost - subTotalCost;
            subTotalPriceTextView.setText("SUBTOTAL: ");
            ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
            orderBoxImageView.setImageResource(R.drawable.orderboxone);
            orderQuantityTextView.setText("");
            orderQuantityTextView.setVisibility(View.GONE);
            listViewOrderSummary.setAdapter(new MyAdapter(currentMenuList, currentMenuIdList, currentSelectedMenuListUrls, portionSizes, individualPrices));
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        if (isLoggedIn == true) {
            if (position == 0) {
                drawerListView.setItemChecked(position, true);
                Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(welcomeIntent);

            }else if (position == 1) {
                drawerListView.setItemChecked(position, true);
                Intent myAccountIntent = new Intent(MainActivity.this, MyAccount.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(myAccountIntent);
            } else if (position == 2) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Credentials");
                editor.remove("currentMenuList");
                editor.remove("currentMenuIdList");
                editor.remove("currentSelectedMenuListUrls");
                editor.remove("currentPortionSizes");
                editor.remove("currentIndividualPrices");
                editor.remove("currentSubtotalCost");

                editor.commit();

                currentMenuList.clear();
                currentMenuIdList.clear();
                currentSelectedMenuListUrls.clear();
                portionSizes.clear();
                individualPrices.clear();
                subTotalCost = 0;

                LogInScreen.setUserCredentials(null);
                finish();
                startActivity(getIntent());

            }
        } else {
            if (position == 0) {
                drawerListView.setItemChecked(position, true);
                Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(welcomeIntent);

            } else if (position == 1) {
                drawerListView.setItemChecked(position, true);
                Intent logInIntent = new Intent(MainActivity.this, LogInScreen.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.putBoolean("nonlogin", false);
                editor.commit();
                startActivity(logInIntent);
            } else {
                drawerListView.setItemChecked(position, true);
                Intent createAccountIntent = new Intent(MainActivity.this, CreateAccount.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                SharedPreferences.Editor editor =  sharedPreferences.edit();
                editor.putBoolean("nonlogin", false);
                editor.commit();
                startActivity(createAccountIntent);
            }
        }

    }

    public class MyAdapter extends BaseAdapter {

        private List<Data> currentMenuList;
        private List<Integer> currentMenuIdList;
        private List<String> currentMenuListUrls;
        private List<String> portionSizeList;
        private List<String> individualPrices;

        public MyAdapter(List<Data> currentSelectedMenuList, List<Integer> currentSelectedMenuIdList, List<String> currentSelectedMenuListUrls, List<String> portionSize, List<String> individualPrices){
            this.currentMenuList = currentSelectedMenuList;
            this.currentMenuIdList = currentSelectedMenuIdList;
            this.currentMenuListUrls = currentSelectedMenuListUrls;
            this.portionSizeList = portionSize;
            this.individualPrices = individualPrices;
        }

        @Override
        public int getCount() {
            return currentMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return currentMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View orderSummaryView = getLayoutInflater().inflate(R.layout.row_ordersummary, null);

            TextView orderSummaryMenuName = (TextView) orderSummaryView.findViewById(R.id.orderSummaryMenuName);
            NetworkImageView orderSummaryMenuImage = (NetworkImageView) orderSummaryView.findViewById(R.id.orderSummaryMenuImage);
            TextView orderPortionSize = (TextView) orderSummaryView.findViewById(R.id.portionSizeTextView);
            TextView price = (TextView) orderSummaryView.findViewById(R.id.individualPriceTextView);

            orderSummaryMenuName.setText(currentMenuList.get(position).getMenu_name());

            orderSummaryMenuImage.setImageUrl(currentMenuListUrls.get(position).toString(), ConnectionManager.getImageLoader(MainActivity.this));

            orderPortionSize.setText(portionSizeList.get(position).toString());

            price.setText(individualPrices.get(position).toString());

            return orderSummaryView;
        }
    }


}