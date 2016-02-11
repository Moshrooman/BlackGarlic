package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.database.BlackGarlicDAO;
import id.blackgarlic.blackgarlic.database.DBOpenHelper;
import id.blackgarlic.blackgarlic.model.Menu;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BlackGarlicAdapter.MyListItemClickListener{

    public final String BLACKGARLIC_MENUS_URL = "http://api.blackgarlic.id:7000/bo/menu/ordering/2015-12-22";

    private RecyclerView recyclerView;

    int selectedInteger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StringRequest request = new StringRequest(Request.Method.GET, BLACKGARLIC_MENUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(MainActivity.this, "Taking From Internet", Toast.LENGTH_SHORT).show();

                String jsonResponse = response;

                jsonResponse = jsonResponse.replace("\\", "");

                StringBuilder stringBuilder = new StringBuilder(jsonResponse);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                jsonResponse = stringBuilder.toString();

                id.blackgarlic.blackgarlic.model.Menu[] menuList = new Gson().fromJson(jsonResponse, id.blackgarlic.blackgarlic.model.Menu[].class);

                BlackGarlicAdapter blackGarlicAdapter = new BlackGarlicAdapter(menuList, MainActivity.this, MainActivity.this);

                BlackGarlicDAO.getInstance().storeMenus(MainActivity.this, menuList);

                recyclerView.setAdapter(blackGarlicAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (BlackGarlicDAO.getInstance().getMenusFromDB(MainActivity.this).length != 0) {

                    Menu[] menuListDatabase = BlackGarlicDAO.getInstance().getMenusFromDB(MainActivity.this);
                    Toast.makeText(MainActivity.this, "Taking From Database", Toast.LENGTH_SHORT).show();
                    BlackGarlicAdapter blackGarlicAdapter2 = new BlackGarlicAdapter(menuListDatabase, MainActivity.this, MainActivity.this);
                    recyclerView.setAdapter(blackGarlicAdapter2);

                } else {

                    Toast.makeText(MainActivity.this, "Please Connect To The Internet!", Toast.LENGTH_LONG).show();

                }

            }
        });

        ConnectionManager.getInstance(MainActivity.this).add(request);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void OnItemClick(id.blackgarlic.blackgarlic.model.Menu itemClicked, View view, List<Menu> menuList) {

        ImageView orderBoxImageView = (ImageView) findViewById(R.id.orderBoxImageView);

        if (view.findViewById(R.id.selectedImageView).getVisibility() == View.GONE) {
            selectedInteger++;

        } else {
            selectedInteger--;
        }

        if (selectedInteger > 5) {
            selectedInteger--;
            Toast.makeText(MainActivity.this, "You can't order more than 5 menus! Please remove one menu", Toast.LENGTH_LONG).show();
        } else if (selectedInteger == 0) {
            orderBoxImageView.setImageResource(R.drawable.orderboxone);
        } else if (selectedInteger == 1) {
            orderBoxImageView.setImageResource(R.drawable.orderboxtwo);
        } else if (selectedInteger == 2) {
            orderBoxImageView.setImageResource(R.drawable.orderboxthree);
        } else if (selectedInteger == 3) {
            orderBoxImageView.setImageResource(R.drawable.orderboxfour);
        } else if (selectedInteger == 4) {
            orderBoxImageView.setImageResource(R.drawable.orderboxfive);
        } else if (selectedInteger == 5){
            orderBoxImageView.setImageResource(R.drawable.orderboxsix);
        }




        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int heightNetworkImageLayoutandTextView = (int) (display.getHeight() * 0.15);
        int widthNetworkImage = (int) (display.getWidth() * 0.4);

        //Grabbing the relative layout from the content_main.xml that will hold all of the ordered menus
        RelativeLayout mainOrderSummaryLayout = (RelativeLayout) findViewById(R.id.orderSummaryRelativeLayout);

        //Then you clear all of the views, now the next step is to loop through currentSelectedMenus and for every one create a new view
        mainOrderSummaryLayout.removeAllViews();

        for (int j = 0; j < menuList.size(); j++) {

            if (mainOrderSummaryLayout.getChildCount() == 0) {

                //Main Layout
                RelativeLayout.LayoutParams relativeLayoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                RelativeLayout relativeLayout1 = new RelativeLayout(MainActivity.this);
                relativeLayoutParams1.setMargins(0, 5, 0, 0);
                relativeLayout1.setLayoutParams(relativeLayoutParams1);
                relativeLayout1.setId(0);

                //Creating NetWorkImage View

                NetworkImageView networkImageView1 = new NetworkImageView(MainActivity.this);
                networkImageView1.setImageUrl(menuList.get(0).getMenuImageUrl(), ConnectionManager.getImageLoader(MainActivity.this));
                networkImageView1.setLayoutParams(new RelativeLayout.LayoutParams(widthNetworkImage, heightNetworkImageLayoutandTextView));

                //Creating the TextView

                RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                textViewLayoutParams.setMargins(widthNetworkImage, 0, 0, 0);
                TextView textView1 = new TextView(MainActivity.this);
                textView1.setText(menuList.get(0).getMenuName());
                textView1.setLayoutParams(textViewLayoutParams);
                textView1.setGravity(Gravity.CENTER_VERTICAL);
                textView1.setTextSize(15);


                //Adding the netWorkImageView to the layout, adding the relativelayou to the main Layout containing all layouts
                relativeLayout1.addView(networkImageView1);
                relativeLayout1.addView(textView1);
                mainOrderSummaryLayout.addView(relativeLayout1);


            } else if (mainOrderSummaryLayout.getChildCount() == 1) {

                RelativeLayout.LayoutParams relativeLayoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                RelativeLayout relativeLayout2 = new RelativeLayout(MainActivity.this);
                relativeLayoutParams2.setMargins(0, ((int)(display.getHeight()*0.16)) , 0, 0);

                relativeLayout2.setLayoutParams(relativeLayoutParams2);
                relativeLayout2.setId(1);

                //Creating NetWorkImage View

                NetworkImageView networkImageView2 = new NetworkImageView(MainActivity.this);
                networkImageView2.setImageUrl(menuList.get(1).getMenuImageUrl(), ConnectionManager.getImageLoader(MainActivity.this));
                networkImageView2.setLayoutParams(new RelativeLayout.LayoutParams(widthNetworkImage, heightNetworkImageLayoutandTextView));

                //Creating the TextView

                RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                textViewLayoutParams.setMargins(widthNetworkImage, 0, 0, 0);
                TextView textView2 = new TextView(MainActivity.this);
                textView2.setText(menuList.get(1).getMenuName());
                textView2.setLayoutParams(textViewLayoutParams);
                textView2.setGravity(Gravity.CENTER_VERTICAL);
                textView2.setTextSize(15);

                relativeLayout2.addView(networkImageView2);
                relativeLayout2.addView(textView2);
                mainOrderSummaryLayout.addView(relativeLayout2);


            } else if (mainOrderSummaryLayout.getChildCount() == 2) {

                RelativeLayout.LayoutParams relativeLayoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                RelativeLayout relativeLayout3 = new RelativeLayout(MainActivity.this);
                relativeLayoutParams3.setMargins(0, ((int) (display.getHeight() * 0.32)), 0, 0);
                relativeLayout3.setLayoutParams(relativeLayoutParams3);
                relativeLayout3.setId(2);

                //Creating NetWorkImage View

                NetworkImageView networkImageView3 = new NetworkImageView(MainActivity.this);
                networkImageView3.setImageUrl(menuList.get(2).getMenuImageUrl(), ConnectionManager.getImageLoader(MainActivity.this));
                networkImageView3.setLayoutParams(new RelativeLayout.LayoutParams(widthNetworkImage, heightNetworkImageLayoutandTextView));

                //Creating the TextView

                RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                textViewLayoutParams.setMargins(widthNetworkImage, 0, 0, 0);
                TextView textView3 = new TextView(MainActivity.this);
                textView3.setText(menuList.get(2).getMenuName());
                textView3.setLayoutParams(textViewLayoutParams);
                textView3.setGravity(Gravity.CENTER_VERTICAL);
                textView3.setTextSize(15);

                relativeLayout3.addView(networkImageView3);
                relativeLayout3.addView(textView3);
                mainOrderSummaryLayout.addView(relativeLayout3);


            } else if (mainOrderSummaryLayout.getChildCount() == 3) {

                RelativeLayout.LayoutParams relativeLayoutParams4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                RelativeLayout relativeLayout4 = new RelativeLayout(MainActivity.this);
                relativeLayoutParams4.setMargins(0, ((int) (display.getHeight() * 0.48)), 0, 0);
                relativeLayout4.setLayoutParams(relativeLayoutParams4);
                relativeLayout4.setId(3);

                //Creating NetWorkImage View

                NetworkImageView networkImageView4 = new NetworkImageView(MainActivity.this);
                networkImageView4.setImageUrl(menuList.get(3).getMenuImageUrl(), ConnectionManager.getImageLoader(MainActivity.this));
                networkImageView4.setLayoutParams(new RelativeLayout.LayoutParams(widthNetworkImage, heightNetworkImageLayoutandTextView));

                //Creating the TextView

                RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                textViewLayoutParams.setMargins(widthNetworkImage, 0, 0, 0);
                TextView textView4 = new TextView(MainActivity.this);
                textView4.setText(menuList.get(3).getMenuName());
                textView4.setLayoutParams(textViewLayoutParams);
                textView4.setGravity(Gravity.CENTER_VERTICAL);
                textView4.setTextSize(15);

                relativeLayout4.addView(networkImageView4);
                relativeLayout4.addView(textView4);
                mainOrderSummaryLayout.addView(relativeLayout4);

            } else if (mainOrderSummaryLayout.getChildCount() == 4) {

                RelativeLayout.LayoutParams relativeLayoutParams5 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                RelativeLayout relativeLayout5 = new RelativeLayout(MainActivity.this);
                relativeLayoutParams5.setMargins(0, ((int) (display.getHeight() * 0.64)), 0, 0);
                relativeLayout5.setLayoutParams(relativeLayoutParams5);
                relativeLayout5.setId(4);

                //Creating NetWorkImage View

                NetworkImageView networkImageView5 = new NetworkImageView(MainActivity.this);
                networkImageView5.setImageUrl(menuList.get(4).getMenuImageUrl(), ConnectionManager.getImageLoader(MainActivity.this));
                networkImageView5.setLayoutParams(new RelativeLayout.LayoutParams(widthNetworkImage, heightNetworkImageLayoutandTextView));

                //Creating the TextView

                RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, heightNetworkImageLayoutandTextView);
                textViewLayoutParams.setMargins(widthNetworkImage, 0, 0, 0);
                TextView textView5 = new TextView(MainActivity.this);
                textView5.setText(menuList.get(4).getMenuName());
                textView5.setLayoutParams(textViewLayoutParams);
                textView5.setGravity(Gravity.CENTER_VERTICAL);
                textView5.setTextSize(15);

                relativeLayout5.addView(networkImageView5);
                relativeLayout5.addView(textView5);
                mainOrderSummaryLayout.addView(relativeLayout5);

            }

        }
        

        //TODO: CLEAR BUTTON
        //If clear button is clicked, set selected integer to 0
        //Clear the menulist
        //Loop through all menus and set all to false, use a for each true inside menulist
        //And clear the sliding layout of all of its children views

        //TODO: X BUTTON NEXT TO EACH MENU
        //When clicked, i have to remove 1 from selected integer in mainActivity, and Blackgarlicadapter
        //Also have to remove the view at at position
        //take away that menu from the selectedMenuList in BlackGarlicAdapter
        //set that particular menu's boolean to true

        //TODO: TRY TO FIX THE DATABASE, TO LOAD PICTURES WHEN THERE IS NO INTERNET
        //In the BlackGarlicDAO in the method getPostsFromDb what I did was I created a new arraylist and stored the existing posts there
        //Then i created a new array, put all of the stuff that was in the arraylist in there and returned the array
        //The error that I'm getting is that It is null
        //Also in getPostsFromDB I logged the first menu's name and id
        //And created to log taking from internet or database
        //There is an error with the method in the Menu class of getMenuImageUrl

    }

    public void clearAll(View view) {

        //BlackGarlicAdapter getCurrentMmenuListandSelectedInteger = new BlackGarlicAdapter(null, null, null);

        //getCurrentMmenuListandSelectedInteger.setAdapterSelectedInteger(0);
        //getCurrentMmenuListandSelectedInteger.clearCurrentSelectedMenus();
        //getCurrentMmenuListandSelectedInteger.clearmmenuList();

        this.selectedInteger = 0;

        //RelativeLayout mainOrderSummaryLayout = (RelativeLayout) findViewById(R.id.orderSummaryRelativeLayout);

        //mainOrderSummaryLayout.removeAllViews();




    }


}
