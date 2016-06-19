package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CookBook extends AppCompatActivity{

    private static final String cookBookLink = "http://188.166.221.241:3000/app/cookbook";

    private RecyclerView cookBookRecyclerView;

    private static List<CookBookObject> cookBookObjectList = new ArrayList<CookBookObject>();

    //So the idea here is that we have 2 different lists, one list that will have the first 20 in the last added and will create the first
    //contents of the adapter and this list is cookBookObjectListAdapter.

    //Then the second list is the cookbookObjectListSearchAdapter which will constantly be getting cleared and added into depending on
    //what is entered in the search bar.

    private static List<CookBookObject> cookBookObjectListAdapter = new ArrayList<CookBookObject>();

    private static List<CookBookObject> cookBookObjectListSearchAdapter = new ArrayList<CookBookObject>();

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

        cookBookObjectList.clear();
        cookBookObjectListAdapter.clear();
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

                    cookBookAdapterSearch = new CookBookAdapter(null, cookBookObjectListSearchAdapter, null, cookBookObjectList, CookBook.this);
                    cookBookAdapterSearch.setSearchBoolean(true);
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

        StringRequest cookBookRequest = new StringRequest(Request.Method.POST, cookBookLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String modifiedResponse = "{\"menus\"" + ": " + response;

                modifiedResponse = modifiedResponse + "}";

                CookBookList cookBookList = new Gson().fromJson(modifiedResponse, CookBookList.class);

                cookBookObjectList = cookBookList.getCookBookList();

                int totalRemoved = 0;

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

                for (int i = 0; i < 20; i++) {

                    cookBookObjectListAdapter.add(cookBookObjectList.get(i));

                    if (i == 19) {
                        startingPositionForAddingIntoAdapterList = startingPositionForAddingIntoAdapterList + 20;
                    }
                }

                //PUT THIS IN NOTES THEN GO BACK UP TO SETTING DRAWEE VIEW.
                cookBookAdapter = new CookBookAdapter(cookBookObjectListAdapter, null, cookBookObjectList, null, CookBook.this);
                cookBookAdapter.setSearchBoolean(false);
                cookBookRecyclerView.setAdapter(cookBookAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ConnectionManager.getInstance(CookBook.this).add(cookBookRequest);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
