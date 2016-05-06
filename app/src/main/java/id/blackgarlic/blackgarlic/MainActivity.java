package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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

import id.blackgarlic.blackgarlic.OrderHistory.OrderHistory;
import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.MenuId;
import id.blackgarlic.blackgarlic.model.Menus;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import id.blackgarlic.blackgarlic.model.gcm.RegistrationIntentService;
import id.blackgarlic.blackgarlic.welcome.WelcomeActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


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

    private static Button clearAllButton;

    private static BlackGarlicAdapter blackGarlicAdapter2;

    private static LayoutInflater layoutInflater;

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

    public static void setSubTotalCost(int subTotalCost) {
        MainActivity.subTotalCost = subTotalCost;
    }

    public static void setSubTotalCostTextViewText(int newSubTotalCost) {
        if (newSubTotalCost > 0) {
            MainActivity.subTotalPriceTextView.setText("SUBTOTAL: IDR " + new DecimalFormat().format(newSubTotalCost));
        } else {
            MainActivity.subTotalPriceTextView.setText("SUBTOTAL: ");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ProgressBar mainActivityProgressBar = (ProgressBar) findViewById(R.id.mainActivityProgressBar);
        final TextView loadingThisWeeksMenuTextView = (TextView) findViewById(R.id.loadingThisWeeksMenuTextView);

        mainActivityProgressBar.bringToFront();
        loadingThisWeeksMenuTextView.bringToFront();

        mainActivityProgressBar.setVisibility(View.VISIBLE);
        loadingThisWeeksMenuTextView.setVisibility(View.VISIBLE);

        clearAllButton = (Button) findViewById(R.id.clearAllButton);

        subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);
        listViewOrderSummary = (ListView) findViewById(R.id.orderSummaryListView);
        orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
        orderQuantityTextView = (TextView) findViewById(R.id.orderQuantityTextView);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if ((checkPlayServices()) && !(sharedPreferences.contains("gcmtoken"))) {
            Log.e("Retrieving New Token: ", "True");
            Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Log.e("Current Token: ", sharedPreferences.getString("gcmtoken", ""));
        }

        Log.e("Menus In SP: ", String.valueOf(sharedPreferences.contains("currentMenuList")));

        if (sharedPreferences.contains("fromSplash")) {
            Log.e("In From Splash: ", "True");
            editor.remove("fromSplash");
            editor.remove("currentMenuList");
            editor.remove("currentMenuIdList");
            editor.remove("currentSelectedMenuListUrls");
            editor.remove("currentPortionSizes");
            editor.remove("currentIndividualPrices");
            editor.remove("currentSubtotalCost");

            editor.commit();

        }

        if (sharedPreferences.contains("Credentials")) {
            String json = sharedPreferences.getString("Credentials", "");
            userCredentials = new Gson().fromJson(json, UserCredentials.class);
            isLoggedIn = true;
        } else {
            userCredentials = LogInScreen.getUserCredentials();
            isLoggedIn = false;
        }

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

        numberOfTimesClicked = 0;
        currentRotation = 0;

        sliding_layout = (com.sothree.slidinguppanel.SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        sliding_layout.setDragView(findViewById(R.id.tabRelativeLayout));

        drawerListView = (ListView) findViewById(R.id.drawerListView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerEntries = getResources().getStringArray(R.array.navBarEntires);

        TextView menuMainActivityTextView = (TextView) findViewById(R.id.menuMainActivityTextView);

        if (isLoggedIn == false) {
            Button mainActivityLogInButton = (Button) findViewById(R.id.mainActivityLogInButton);
            Button mainActivitySignUpButton = (Button) findViewById(R.id.mainActivitySignUpButton);

            mainActivityLogInButton.setVisibility(View.VISIBLE);
            mainActivitySignUpButton.setVisibility(View.VISIBLE);
            menuMainActivityTextView.setVisibility(View.GONE);
            drawerEntries = new String[3];
            drawerEntries[0] = "Home";
            drawerEntries[1] = "Log In";
            drawerEntries[2] = "Sign Up";

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
            menuMainActivityTextView.setVisibility(View.VISIBLE);

            drawerEntries = new String[6];
            drawerEntries[0] = "Home";
            drawerEntries[1] = "My Account";
            drawerEntries[2] = "Order History";
            drawerEntries[3] = "Payment Confirmation";
            drawerEntries[4] = "Log Out";
        }

        drawerListView.setAdapter(new MyNavBarAdapter());

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

        String BlackGarlicMenusNew = "http://188.166.221.241:3000/app/menu/"+String.valueOf(localDate)+"";

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
                    if (currentMenuList == null || currentMenuList.size() < 2) {
                        proceedToCheckoutButton.setEnabled(true);

                        SuperToast superToast = SuperToast.create(MainActivity.this, "Please Add A Minimum Of 2 Menus!", SuperToast.Duration.SHORT, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP));
                        superToast.show();

                    } else {
                        Intent proceedToCheckOutIntent = new Intent(MainActivity.this, CheckOut.class);
                        startActivity(proceedToCheckOutIntent);
                    }
                } else {

                    if (currentMenuList == null || currentMenuList.size() < 2) {
                        proceedToCheckoutButton.setEnabled(true);
                        SuperToast superToast = SuperToast.create(MainActivity.this, "Please Add A Minimum Of 2 Menus!", SuperToast.Duration.SHORT, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP));
                        superToast.show();
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

                for (int i = 0; i < menuList.size(); i++) {
                    if (menuList.get(i).getMenu_type().equals("7")) {
                        Data currentKidsMenu = menuList.get(i);
                        Integer currentKidsMenuInteger = menuIdList.get(i);

                        menuList.remove(i);
                        menuIdList.remove(i);

                        menuList.add(menuList.size() - 1, currentKidsMenu);
                        menuIdList.add(menuIdList.size() - 1, currentKidsMenuInteger);
                    }
                }

                BlackGarlicAdapter blackGarlicAdapter = new BlackGarlicAdapter(menuList, menuIdList, MainActivity.this, MainActivity.this);
                blackGarlicAdapter2 = blackGarlicAdapter;

                if (sharedPreferences.contains("clickedBack")) {
                    Log.e("Contained: ", "clickedBack");
                    editor.remove("currentMenuList");
                    editor.remove("currentMenuIdList");
                    editor.remove("currentSelectedMenuListUrls");
                    editor.remove("currentPortionSizes");
                    editor.remove("currentIndividualPrices");
                    editor.remove("currentSubtotalCost");
                    editor.remove("clickedBack");
                    editor.commit();
                    currentMenuList.clear();
                    currentMenuIdList.clear();
                    currentSelectedMenuListUrls.clear();
                    portionSizes.clear();
                    individualPrices.clear();
                    subTotalCost = 0;

                    blackGarlicAdapter.clearAllLists();
                    listViewOrderSummary.setAdapter(new MyAdapter(currentMenuList, currentMenuIdList, currentSelectedMenuListUrls, portionSizes, individualPrices));

                    subTotalPriceTextView.setText("SUBTOTAL: ");
                    ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
                    orderBoxImageView.setImageResource(R.drawable.orderboxone);

                    TextView orderQuantityTextView2 = (TextView) findViewById(R.id.orderQuantityTextView);
                    orderQuantityTextView2.setText("");
                    orderQuantityTextView2.setVisibility(View.GONE);
                }

                recyclerView.setAdapter(blackGarlicAdapter);
                recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(blackGarlicAdapter));

                mainActivityProgressBar.setVisibility(View.GONE);
                loadingThisWeeksMenuTextView.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mainActivityProgressBar.setVisibility(View.GONE);
                loadingThisWeeksMenuTextView.setVisibility(View.GONE);

                SuperToast superToast = SuperToast.create(MainActivity.this, "No Internet!\n\nPlease Connect To The Internet And Restart The Application", SuperToast.Duration.EXTRA_LONG, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                superToast.setTextSize(21);
                superToast.show();

            }
        });

        ConnectionManager.getInstance(MainActivity.this).add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Runnable runnableSystemGc = new Runnable() {
            @Override
            public void run() {
                Log.e("Ran Resume GC: ", "True");
                System.gc();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnableSystemGc, 2500);

        proceedToCheckoutButton.setEnabled(true);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return false;
            } else {

                moveTaskToBack(true);
                return super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void OnItemClick(List<Data> selectedMenuList, List<Integer> selectedMenuIdList, String menuAdded, List<String> currentMenuListUrls, List<String> portionSizeList, List<String> individualPricesAdapter, Data currentMenu) {

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

        SuperToast superToast = SuperToast.create(this, "Added To Box:\n\n"+String.valueOf(currentMenu.getQuantity())+"x "+menuAdded+"", SuperToast.Duration.SHORT, Style.getStyle(Style.GREEN, SuperToast.Animations.POPUP));

        superToast.show();

        listViewOrderSummary.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, currentMenuListUrls, portionSizeList, individualPricesAdapter));

    }

    @Override
    public void SwitchToPdfActivity(String menuId) {
        Intent switchToPdfIntent = new Intent(MainActivity.this, PDFWebView.class);
        switchToPdfIntent.putExtra("menu_id", menuId);
        startActivity(switchToPdfIntent);
    }

    public void clearAll(View view) {

        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        TextView subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);

        view.startAnimation(animScale);

        if (null == currentMenuList || currentMenuList.size() == 0) {
            return;
        } else {

            SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

            SharedPreferences.Editor editor = sharedPreferences.edit();

            currentMenuList.clear();
            currentMenuIdList.clear();
            currentSelectedMenuListUrls.clear();
            individualPrices.clear();
            portionSizes.clear();

            blackGarlicAdapter2.clearAllLists();

            editor.remove("currentMenuList");
            editor.remove("currentMenuIdList");
            editor.remove("currentSelectedMenuListUrls");
            editor.remove("currentPortionSizes");
            editor.remove("currentIndividualPrices");
            editor.remove("currentSubtotalCost");

            editor.commit();

            Log.e("Before Clear All: ", String.valueOf(sharedPreferences.contains("currentMenuList")));

            subTotalCost = subTotalCost - subTotalCost;
            subTotalPriceTextView.setText("SUBTOTAL: ");
            ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);
            orderBoxImageView.setImageResource(R.drawable.orderboxone);
            orderQuantityTextView.setText("");
            orderQuantityTextView.setVisibility(View.GONE);
            listViewOrderSummary.setAdapter(new MyAdapter(currentMenuList, currentMenuIdList, currentSelectedMenuListUrls, portionSizes, individualPrices));

            Runnable runnableSystemGc = new Runnable() {
                @Override
                public void run() {
                    System.gc();
                    Log.e("Ran Clear All GC: ", "True");
                }
            };
            Handler handler = new Handler();
            handler.postDelayed(runnableSystemGc, 2500);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        System.gc();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        if (isLoggedIn == true) {
            if (position == 1) {
                drawerListView.setItemChecked(position, true);
                Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(welcomeIntent);

            }else if (position == 2) {
                drawerListView.setItemChecked(position, true);
                Intent myAccountIntent = new Intent(MainActivity.this, MyAccount.class);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(myAccountIntent);
            } else if (position == 3) {

                Intent orderHistoryIntent = new Intent(MainActivity.this, OrderHistory.class);
                drawerListView.setItemChecked(position, true);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(orderHistoryIntent);

            } else if(position == 4) {

                Intent paymentConfirmationIntent = new Intent(MainActivity.this, PaymentConfirmation.class);
                drawerListView.setItemChecked(position, true);
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(paymentConfirmationIntent);

            } else if (position == 5) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Credentials");
                editor.remove("Email");
                editor.remove("Password");
                editor.remove("currentMenuList");
                editor.remove("currentMenuIdList");
                editor.remove("currentSelectedMenuListUrls");
                editor.remove("currentPortionSizes");
                editor.remove("currentIndividualPrices");
                editor.remove("currentSubtotalCost");

                editor.commit();

                if (!(null == currentMenuList)) {
                    currentMenuList.clear();
                    currentMenuIdList.clear();
                    currentSelectedMenuListUrls.clear();
                    portionSizes.clear();
                    individualPrices.clear();
                    subTotalCost = 0;

                }


                LogInScreen.setUserCredentials(null);
                isLoggedIn = false;
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

    public class MyNavBarAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return drawerEntries.length;
        }

        @Override
        public Object getItem(int position) {
            return drawerEntries[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if ((isLoggedIn == true) && (position == 0)) {
                    convertView = layoutInflater.inflate(R.layout.welcomechefnavbar, null);
                    convertView.setEnabled(false);
                } else {
                    convertView = layoutInflater.inflate(R.layout.rownavbar, null);
                }

            }

            if ((isLoggedIn == true) && (position == 0)) {
                TextView chefNameTextView = (TextView) convertView.findViewById(R.id.chefNameTextView);
                chefNameTextView.setText(userCredentials.getCustomer_name());
            }  else if ((isLoggedIn == true) && (position == 5)) {
                TextView navBarEntry = (TextView) convertView.findViewById(R.id.navBarEntryImageView);
                navBarEntry.setText(drawerEntries[position - 1]);
                navBarEntry.setTextColor(getResources().getColor(R.color.BGRED));
            }  else if ((isLoggedIn == true) && (position != 0)) {
                TextView navBarEntry = (TextView) convertView.findViewById(R.id.navBarEntryImageView);
                navBarEntry.setText(drawerEntries[position - 1]);
            } else {
                TextView navBarEntry = (TextView) convertView.findViewById(R.id.navBarEntryImageView);
                navBarEntry.setText(drawerEntries[position]);
            }


            return convertView;
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.row_ordersummary, null);
            }

            TextView orderSummaryMenuName = (TextView) convertView.findViewById(R.id.orderSummaryMenuName);
            SimpleDraweeView orderSummaryMenuImage = (SimpleDraweeView) convertView.findViewById(R.id.orderSummaryMenuImage);

            TextView orderPortionSize = (TextView) convertView.findViewById(R.id.portionSizeTextView);
            TextView price = (TextView) convertView.findViewById(R.id.individualPriceTextView);
            final TextView deleteMenuFromBoxTextView = (TextView) convertView.findViewById(R.id.deleteMenuFromBoxTextView);

            orderSummaryMenuName.setText(currentMenuList.get(position).getMenu_name());

            Uri uri = Uri.parse(currentMenuListUrls.get(position).toString());
            orderSummaryMenuImage.setImageURI(uri);
            orderSummaryMenuImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            orderPortionSize.setText(portionSizeList.get(position).toString());

            price.setText(individualPrices.get(position).toString());

            final View finalConvertView = convertView;
            deleteMenuFromBoxTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteMenuFromBoxTextView.setEnabled(false);

                    Animation righttoleftanimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_to_left);
                    righttoleftanimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            deleteMenuFromBoxTextView.setEnabled(true);
                            SuperToast superToast = SuperToast.create(MainActivity.this, "Removed From Box:\n\n " + getCurrentMenuList().get(position).getMenu_name() + "", SuperToast.Duration.SHORT, Style.getStyle(Style.RED, SuperToast.Animations.POPUP));
                            superToast.show();

                            getCurrentMenuList().remove(position);
                            getCurrentMenuIdList().remove(position);
                            getCurrentSelectedMenuListUrls().remove(position);
                            getPortionSizes().remove(position);

                            String updatedIndividualPrices = getIndividualPrices().get(position).replace("IDR ", "");
                            updatedIndividualPrices = updatedIndividualPrices.replace(".", "");

                            setSubTotalCost(getSubTotalCost() - Integer.valueOf(updatedIndividualPrices));

                            setSubTotalCostTextViewText(getSubTotalCost());

                            getIndividualPrices().remove(position);

                            if (getCurrentMenuList().size() == 0) {
                                orderBoxImageView.setImageResource(R.drawable.orderboxone);
                                orderQuantityTextView.setVisibility(View.GONE);
                            } else {
                                orderBoxImageView.setImageResource(R.drawable.orderboxtwopng);
                                orderQuantityTextView.setVisibility(View.VISIBLE);
                                orderQuantityTextView.setText(String.valueOf(getCurrentMenuList().size()));
                            }

                            listViewOrderSummary.setAdapter(new MyAdapter(getCurrentMenuList(), getCurrentMenuIdList(), getCurrentSelectedMenuListUrls(), getPortionSizes(), getIndividualPrices()));

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    finalConvertView.startAnimation(righttoleftanimation);

                }
            });

            return convertView;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.e("MainActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
