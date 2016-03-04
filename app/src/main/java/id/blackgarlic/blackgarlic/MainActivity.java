package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.ScrollableViewHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.MenuId;
import id.blackgarlic.blackgarlic.model.Menu;
import id.blackgarlic.blackgarlic.model.Menus;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BlackGarlicAdapter.MyListItemClickListener {

    //TODO: X BUTTON NEXT TO EACH MENU
    //When clicked, i have to remove 1 from selected integer in mainActivity, and Blackgarlicadapter
    //Also have to remove the view at at position
    //take away that menu from the selectedMenuList in BlackGarlicAdapter
    //set that particular menu's boolean to true

    public final String BLACKGARLIC_MENUS_URL = "http://api.blackgarlic.id:7000/bo/menu/ordering/2015-12-22";

    private RecyclerView recyclerView;

    int selectedInteger;

    private static Menu[] firstMenuList = new Menu[9];
    private static View specificView;
    private static UserCredentials userCredentials;
    private static List<Data> menuList;
    private static List<Integer> menuIdList;
    private static TextView orderQuantityTextView;
    private static com.sothree.slidinguppanel.SlidingUpPanelLayout sliding_layout;
    private static ListView listViewOrderSummary;
    private static NestedScrollView nestedScrollView;

    public static List<Data> getMenuList() {
        return menuList;
    }

    public static List<Integer> getMenuIdList() {
        return menuIdList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderQuantityTextView = (TextView) findViewById(R.id.orderQuantityTextView);
        sliding_layout = (com.sothree.slidinguppanel.SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        listViewOrderSummary = (ListView) findViewById(R.id.orderSummaryListView);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        sliding_layout.setScrollableViewHelper(new NestedScrollableViewHelper());

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
        if ((localDate.getDayOfWeek() == 7) || (localDate.getDayOfWeek() == 1)) {
            localDate = localDate.dayOfWeek().setCopy(2);

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

                BlackGarlicAdapter blackGarlicAdapter = new BlackGarlicAdapter(menuList, menuIdList, MainActivity.this, MainActivity.this);

//              BlackGarlicDAO.getInstance().storeMenus(MainActivity.this, firstMenuList);

                recyclerView.setAdapter(blackGarlicAdapter);

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
//        listViewOrderSummary.setAdapter(new MyAdapter(menuList, menuIdList));

    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void OnItemClick(Data itemClicked, View view, List<Data> selectedMenuList, List<Integer> selectedMenuIdList) {

        specificView = view;

        ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);

        orderQuantityTextView.bringToFront();

        if (view.findViewById(R.id.selectedImageView).getVisibility() == View.GONE) {
            selectedInteger++;

        } else {
            selectedInteger--;
        }

        if (selectedInteger == 0) {
            orderBoxImageView.setImageResource(R.drawable.orderboxone);
            orderQuantityTextView.setVisibility(View.GONE);
        } else {
            orderBoxImageView.setImageResource(R.drawable.orderboxtwopng);
            orderQuantityTextView.setVisibility(View.VISIBLE);
            orderQuantityTextView.setText(String.valueOf(selectedInteger));
        }

        for (int i = 0; i < selectedMenuList.size(); i++) {
            Log.e("Selected Menu: ", selectedMenuList.get(i).getMenu_name() + ": " + String.valueOf(selectedMenuIdList.get(i)));
        }

    }


    public void clearAll(View view) {

        Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        view.startAnimation(animScale);

//        RelativeLayout mainOrderSummaryLayout = (RelativeLayout) findViewById(R.id.orderSummaryRelativeLayout);

//        if (mainOrderSummaryLayout.getChildCount() == 0) {
//            return;
//        }

        //Create private static variable to get all firstMenuList
        for (int i = 0; i < firstMenuList.length; i++) {

            if (firstMenuList[i].getIsSelected() == true) {
                firstMenuList[i].setIsSelected(false);
            }

            if (specificView.findViewById(R.id.selectedImageView).getVisibility() == View.VISIBLE) {
                specificView.findViewById(R.id.selectedImageView).setVisibility(View.GONE);
            }

            Log.e("Menu Selected: ", String.valueOf(firstMenuList[i].getIsSelected()));

        }

        BlackGarlicAdapter newBlackGarlicAdapter = new BlackGarlicAdapter(menuList, menuIdList, MainActivity.this, MainActivity.this);

        recyclerView.setAdapter(newBlackGarlicAdapter);

        ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);

        orderBoxImageView.setImageResource(R.drawable.orderboxone);

        this.selectedInteger = 0;

//        mainOrderSummaryLayout.removeAllViews();


    }

    public class NestedScrollableViewHelper extends ScrollableViewHelper {
        public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
            if (nestedScrollView instanceof NestedScrollView) {
                if(isSlidingUp){
                    return nestedScrollView.getScrollY();
                } else {
                    NestedScrollView nsv = ((NestedScrollView) nestedScrollView);
                    View child = nsv.getChildAt(0);
                    return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
                }
            } else {
                return 0;
            }
        }
    }

    public class MyAdapter extends BaseAdapter {

        List<Data> menuListAdapter;
        int[] menuIdListAdapter;

        @Override
        public int getCount() {
            return menuListAdapter.size();
        }

        @Override
        public Object getItem(int position) {
            return menuListAdapter.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View orderSummaryView = getLayoutInflater().inflate(R.layout.row_ordersummary, null);

            TextView orderSummaryMenuName = (TextView) findViewById(R.id.orderSummaryMenuName);
            NetworkImageView orderSummaryMenuImage = (NetworkImageView) findViewById(R.id.orderSummaryMenuImage);

            orderSummaryMenuName.setText(menuListAdapter.get(position).getMenu_name());
            orderSummaryMenuImage.setImageUrl(menuListAdapter.get(position).getMenuUrl().replace("menu_id", String.valueOf(menuIdListAdapter[position])),
                    ConnectionManager.getImageLoader(MainActivity.this));

            return orderSummaryView;
        }
    }

}