package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import id.blackgarlic.blackgarlic.model.UserCredentials;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CookBook extends AppCompatActivity{

    private static final String cookBookLink = "http://188.166.221.241:3000/app/cookbook";

    private static final String checkFavoritesLink = "http://188.166.221.241:3000/app/getfavorites";

    private static UserCredentials userCredentials;

    private RecyclerView cookBookRecyclerView;

    private static List<CookBookObject> cookBookObjectList = new ArrayList<CookBookObject>();

    //So the idea here is that we have 2 different lists, one list that will have the first 20 in the last added and will create the first
    //contents of the adapter and this list is cookBookObjectListAdapter.

    //Then the second list is the cookbookObjectListSearchAdapter which will constantly be getting cleared and added into depending on
    //what is entered in the search bar.

    private static List<CookBookObject> cookBookObjectListAdapter = new ArrayList<CookBookObject>();

    private static List<CookBookObject> cookBookObjectListSearchAdapter = new ArrayList<CookBookObject>();

    private static List<CookBookObject> cookBookObjectListFavoritesAdapter = new ArrayList<CookBookObject>();

    private static int startingPositionForAddingIntoAdapterList;

    private static EditText cookBookSearchEditText;

    private static SimpleDraweeView cookBookSearchDraweeView;

    //So the knowledge that we learnt is that if we create a new instance of an adapter and fill in the parameters, then the cookbookadapter
    //class then sets their variables to the variables passed in the constructor, then the variable in the cookbookadapter will now point to
    //the same object as the parameter does, which is the object here in cookbook class, so we avoided that by going through for loop.

    //However, we want to create 2 different cookbookAdapters because for the first cookbookAdapter, this will be given value in the string
    //request so we will use this for when the user deletes everything from the search and when the user first opens the cookbook.
    //But for if the user searches something, then thats when we want to create a new adapter using the cookBookAdapter search for everytime
    //that they search.
    //But by recycling and continuously using the cookBookAdapter for the blank search bar the menus loaded before will already be there,
    //and also, the cookbookadapter deals with the staringpositionforaddingintoadapterlist variable to mark the last position of the last
    //menu added, so that way it doesn't fuck up. The way it would fuck up is that if everytime we would create a new adapter, we always pass the
    //first 20 items and in cookbookadapter itll switch the startingposition to 40 after loading new 20. Then now if we search and delete so create a new one,
    //then starting position will be 40 so instead of adding the menus starting at position 20, itll start at 40.

    //NOW GO DOWN TO STRING REQUEST TO SHOW THE PARAMETERS FILLED IN.
    private static CookBookAdapter cookBookAdapter;

    private static CookBookAdapter cookBookAdapterSearch;

    private static CookBookAdapter cookBookAdapterFavorites;

    private static List<String> favoritesList = new ArrayList<String>();

    private static ExpandableListView filtersExpandableListView;

    private static Button favoritesFilterButtonStatic;
    private static Button favoriteCountHeartStatic;

    public static void setFavoriteCountHeart(boolean trueOrFalse) {
        if (favoriteCountHeartStatic != null) {
            Log.e("Adding: ", "True");
            Log.e("Boolean: ", String.valueOf(trueOrFalse));
            if (trueOrFalse == true) {
                int beforecount = Integer.valueOf(favoriteCountHeartStatic.getText().toString());
                final int aftercount = beforecount + 1;

                Log.e("Count: ", String.valueOf(aftercount));
                favoriteCountHeartStatic.post(new Runnable() {
                    @Override
                    public void run() {
                        favoriteCountHeartStatic.setText(String.valueOf(aftercount));
                    }
                });
            } else {
                int beforecount = Integer.valueOf(favoriteCountHeartStatic.getText().toString());
                final int aftercount = beforecount - 1;

                Log.e("Count: ", String.valueOf(aftercount));
                favoriteCountHeartStatic.post(new Runnable() {
                    @Override
                    public void run() {
                        favoriteCountHeartStatic.setText(String.valueOf(aftercount));
                    }
                });

            }
        }
    }

    public static int getStartingPositionForAddingIntoAdapterList() {
        return startingPositionForAddingIntoAdapterList;
    }

    public static void setStartingPositionForAddingIntoAdapterList(int startingPositionForAddingIntoAdapterList) {
        CookBook.startingPositionForAddingIntoAdapterList = startingPositionForAddingIntoAdapterList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);

        favoritesFilterButtonStatic = null;
        favoriteCountHeartStatic = null;

        filtersExpandableListView = (ExpandableListView) findViewById(R.id.filtersExpandableListView);
        filtersExpandableListView.setAdapter(new FilterExpandableListViewAdapter());

        userCredentials = new Gson().fromJson(SplashActivity.getSharedPreferences().getString("Credentials", ""), UserCredentials.class);

        cookBookObjectList.clear();
        cookBookObjectListAdapter.clear();
        favoritesList.clear();
        startingPositionForAddingIntoAdapterList = 0;

        //Then here we reference the edit text that we just created.

        cookBookSearchEditText = (EditText) findViewById(R.id.cookBookSearchEditText);
        cookBookSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(s).length() == 0) {

                    //Then just like explained above, we create same settings as the stringrequest adapter.

                    cookBookRecyclerView.setAdapter(cookBookAdapter);
                } else {

                    //Also explained like above, we first clear it.

                    cookBookObjectListSearchAdapter.clear();

                    //Then what we gotta do is we gotta add into our cookbookobjectlistsearchadapter the menus whos menu_names contains
                    //the string searched.
                    //In order to do that we have to loop through Integer.MAX_VALUE and create an outside variable addedCount.
                    //Then we check if the menu's name at position i of cookbookobject list contains the string searched,
                    //then we add that into the cookbookobjectlistsearchadapter and increment addedCount.
                    //Then outside of this if we check if addedCount is 20 OR if i is at cookbookobjectlist.size - 1, which means that
                    //it has reached the end of the list and hasn't added 20, meaning there are less than 20 items that contain
                    //this searched string.

                    int addedCount = 0;

                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        if (cookBookObjectList.get(i).getMenu_name().contains(String.valueOf(s))) {
                            cookBookObjectListSearchAdapter.add(cookBookObjectList.get(i));
                            addedCount++;
                        }

                        if ((addedCount == 20) || (i == cookBookObjectList.size() - 1)) {
                            break;
                        }
                    }

                    //Then we create new cookbookadapter, again, like explained above, filling in second and fourth parameter, setting
                    //searched boolean to true and setting the searchedstring to the string.value of s (which is the searched string).

                    cookBookAdapterSearch = new CookBookAdapter(null, cookBookObjectListSearchAdapter, null, cookBookObjectList, null, CookBook.this);
                    cookBookAdapterSearch.setSearchBoolean(true);
                    cookBookAdapterSearch.setFavoriteBoolean(false);
                    cookBookAdapterSearch.setSearchedString(String.valueOf(s));

                    //Then set the adapter.
                    cookBookRecyclerView.setAdapter(cookBookAdapterSearch);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Here we set the searchdraweeview to the drawable search that we put in our folder.
        //NOW GO TO WHERE WE REFERENCED EDIT TEXT AND GO FROM THERE.
        cookBookSearchDraweeView = (SimpleDraweeView) findViewById(R.id.cookBookSearchDraweeView);
        cookBookSearchDraweeView.setImageDrawable(getResources().getDrawable(R.drawable.search));

        cookBookRecyclerView = (RecyclerView) findViewById(R.id.cookBookRecyclerView);
        cookBookRecyclerView.setHasFixedSize(true);
        cookBookRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cookBookRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Start of outer cookbook string request.
        StringRequest cookBookRequest = new StringRequest(Request.Method.POST, cookBookLink, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response1) {

                //THen in the on response we do favorites request cuz we wanna delete shit.
                final JSONObject checkFavoritesBody = new JSONObject();

                try {
                    checkFavoritesBody.put("customer_id", String.valueOf(userCredentials.getCustomer_id()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Start of inner favorites string request

                StringRequest checkFavoritesRequest = new StringRequest(Request.Method.POST, checkFavoritesLink, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response2) {

                        Log.e("Favorites: ", response2);

                        String modifiedResponse = "{\"menus\"" + ": " + response1;
                        modifiedResponse = modifiedResponse + "}";

                        String modifiedResponseFavorites = "{\"favorites\"" + ": " + response2;
                        modifiedResponseFavorites = modifiedResponseFavorites + "}";

                        CookBookList cookBookList = new Gson().fromJson(modifiedResponse, CookBookList.class);
                        cookBookObjectList = cookBookList.getCookBookList();

                        if (!(response2.equals("empty"))) {
                            FavoritesArray favoritesArrayObject = new Gson().fromJson(modifiedResponseFavorites, FavoritesArray.class);
                            favoritesList = favoritesArrayObject.getFavoritesArray();
                        }

                        int totalRemoved = 0;

                        //Deleting all duplicates within the same list
                        for (int i = 0; i < cookBookObjectList.size(); i++) {
                            String menuTitle = cookBookObjectList.get(i).getMenu_name();

                            for (int j = i + 1; j < cookBookObjectList.size(); j++) {
                                if (cookBookObjectList.get(j).getMenu_name().equals(menuTitle)) {
                                    cookBookObjectList.remove(j);
                                    j--;
                                    totalRemoved++;
                                }
                            }

                            if (i == cookBookObjectList.size() - 1) {
                                Log.e("Removed: ", String.valueOf(totalRemoved));
                            }
                        }

                        if (!(response2.equals("empty"))) {
                            //Setting all the favorites using the favorites retrieved
                            for (int i = 0; i < favoritesList.size(); i++) {
                                String menuNameToFavorite = favoritesList.get(i);

                                for (int j = 0; j < cookBookObjectList.size(); j++) {
                                    if (cookBookObjectList.get(j).getMenu_name().equals(menuNameToFavorite)) {
                                        cookBookObjectList.get(j).setIsFavorited(true);
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < 20; i++) {

                            cookBookObjectListAdapter.add(cookBookObjectList.get(i));

                            if (i == 19) {
                                startingPositionForAddingIntoAdapterList = startingPositionForAddingIntoAdapterList + 20;
                            }
                        }

                        //PUT THIS IN NOTES THEN GO BACK UP TO SETTING DRAWEE VIEW.
                        cookBookAdapter = new CookBookAdapter(cookBookObjectListAdapter, null, cookBookObjectList, null, null, CookBook.this);
                        cookBookAdapter.setSearchBoolean(false);
                        cookBookAdapter.setFavoriteBoolean(false);
                        cookBookRecyclerView.setAdapter(cookBookAdapter);

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
                        return checkFavoritesBody.toString().getBytes();
                    }
                };

                ConnectionManager.getInstance(CookBook.this).add(checkFavoritesRequest);
                //End of inner, favorites string request

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ConnectionManager.getInstance(CookBook.this).add(cookBookRequest);

        //End of outer cookbook string request
    }

    public class FilterExpandableListViewAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) CookBook.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cookbookfilterheader, null);
            }

            ImageView rightSideFilterArrow = (ImageView) convertView.findViewById(R.id.rightSideFilterArrow);
            ImageView leftSideFilterArrow = (ImageView) convertView.findViewById(R.id.leftSideFilterArrow);

            if (isExpanded == true) {
                rightSideFilterArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrowdown));
                leftSideFilterArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrowdown));
            } else {
                rightSideFilterArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrowright));
                leftSideFilterArrow.setImageDrawable(getResources().getDrawable(R.drawable.arrowright));
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) CookBook.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.cookbookfilterexpanded, null);
            }

            if ((favoritesFilterButtonStatic == null) && (favoriteCountHeartStatic == null)) {
                favoritesFilterButtonStatic = (Button) convertView.findViewById(R.id.favoriteFilterButton);
                favoriteCountHeartStatic = (Button) convertView.findViewById(R.id.favoriteCountHeart);
            }

            int favoriteCount = 0;

            for (int i = 0; i < cookBookObjectList.size(); i++) {
                if (cookBookObjectList.get(i).getIsFavorited() == true) {
                    favoriteCount++;
                }

                if (i == cookBookObjectList.size() - 1) {
                    favoriteCountHeartStatic.setText(String.valueOf(favoriteCount));
                }
            }

            favoritesFilterButtonStatic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favoritesFilterButtonStatic.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.borderfilterbefore).getConstantState())) {
                        favoritesFilterButtonStatic.setBackgroundDrawable(getResources().getDrawable(R.drawable.borderfilterafter));
                        favoritesFilterButtonStatic.setTextColor(getResources().getColor(R.color.white));

                        favoriteCountHeartStatic.setBackgroundDrawable(getResources().getDrawable(R.drawable.redheartblackoutline));
                        favoriteCountHeartStatic.setTextColor(getResources().getColor(R.color.white));

                        cookBookObjectListFavoritesAdapter.clear();

                        for (int i = 0; i < cookBookObjectList.size(); i++) {
                            if(cookBookObjectList.get(i).getIsFavorited() == true) {
                                cookBookObjectListFavoritesAdapter.add(cookBookObjectList.get(i));
                            }
                        }

                        cookBookAdapterFavorites = new CookBookAdapter(null, null, null, null, cookBookObjectListFavoritesAdapter, CookBook.this);
                        cookBookAdapterFavorites.setFavoriteBoolean(true);
                        cookBookRecyclerView.setAdapter(cookBookAdapterFavorites);

                        // need to loop through the list and check if the boolean for is favorite is true, if it is put in new list and
                        //create a new adapter with this new list and set the recyclerview adapter to this.

                        //then make sure that in the cookbookadapter there is a boolean for favorites and load everything, not 20 at a time.
                    } else {
                        favoritesFilterButtonStatic.setBackgroundDrawable(getResources().getDrawable(R.drawable.borderfilterbefore));
                        favoritesFilterButtonStatic.setTextColor(getResources().getColor(R.color.red));

                        favoriteCountHeartStatic.setBackgroundDrawable(getResources().getDrawable(R.drawable.whiteheartredoutline));
                        favoriteCountHeartStatic.setTextColor(getResources().getColor(R.color.black));

                        cookBookRecyclerView.setAdapter(cookBookAdapter);
                    }
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
