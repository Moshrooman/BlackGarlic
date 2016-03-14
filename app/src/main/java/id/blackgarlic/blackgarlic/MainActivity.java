package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.joda.time.LocalDate;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.MenuId;
import id.blackgarlic.blackgarlic.model.Menus;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BlackGarlicAdapter.MyListItemClickListener {

    //TODO: X BUTTON NEXT TO EACH MENU
    //When clicked, i have to remove 1 from selected integer in mainActivity, and Blackgarlicadapter
    //Also have to remove the view at at position
    //take away that menu from the selectedMenuList in BlackGarlicAdapter
    //set that particular menu's boolean to true

    private RecyclerView recyclerView;

    private static View specificView;
    private static UserCredentials userCredentials;
    private static List<Data> menuList;
    private static List<Integer> menuIdList;
    private static TextView orderQuantityTextView;
    private static com.sothree.slidinguppanel.SlidingUpPanelLayout sliding_layout;
    private static ListView listViewOrderSummary;
    private static NestedScrollView nestedScrollView;

    private static List<Data> currentMenuList;
    private static List<Integer> currentMenuIdList;
    private static List<String> currentSelectedMenuListUrls;
    private static List<String> portionSizes;
    private static List<String> individualPrices;

    private static int subTotalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderQuantityTextView = (TextView) findViewById(R.id.orderQuantityTextView);
        sliding_layout = (com.sothree.slidinguppanel.SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        listViewOrderSummary = (ListView) findViewById(R.id.orderSummaryListView);

        sliding_layout.setDragView(findViewById(R.id.tabRelativeLayout));

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

        //Joda-Time Library, parsing it into a jodatime object, easier to add dates.
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

        userCredentials = LogInScreen.getUserCredentials();
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        String welcomeTextViewString = (String) welcomeTextView.getText();
        welcomeTextViewString = welcomeTextViewString.replace("Name", userCredentials.getCustomer_name());
        welcomeTextView.setText(welcomeTextViewString);

        welcomeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAccountIntent = new Intent(MainActivity.this, MyAccount.class);
                startActivity(myAccountIntent);
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

                    if (menuList.get(i).getMenu_type().equals("4")) {
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

        TextView subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);

        for (int i = 0; i < individualPrices.size(); i++) {

            String cost = individualPrices.get(i).replace("IDR", "");
            cost = cost.replace(".", "");
            cost = cost.replace(" ", "");

            int intcost = Integer.parseInt(cost);

            subTotalCost = subTotalCost + intcost;

        }

        Log.e("SubTotalCost: ", String.valueOf(subTotalCost));

        ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);

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

        subTotalPriceTextView.setText("SUBTOTAL: " + String.valueOf(subTotalCost));

        Log.e("---------------", "-----------------");

        Toast.makeText(MainActivity.this, "Added: " + menuAdded, Toast.LENGTH_SHORT).show();

        listViewOrderSummary.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, currentMenuListUrls, portionSizeList, individualPricesAdapter));

    }

    //Finished radio buttons through all in blackgarlic adapter using android onclick, creating new list of strings adding menu urls
    //only concatenating _4 if the menutype is 3.
    //Finished the 4 person and 2 person (in a separate list of strings)
    //Finished the individual prices in each list view item (in a separate list of strings)
    //Created subtotal int by parsing all strings in individual price list and adding together
    //Created a subtotal text view, setting it the to the subtotal price variable.



    public void clearAll(View view) {

        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        TextView subTotalPriceTextView = (TextView) findViewById(R.id.subtotalTextView);

        view.startAnimation(animScale);

        if (currentMenuList.size() == 0) {
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