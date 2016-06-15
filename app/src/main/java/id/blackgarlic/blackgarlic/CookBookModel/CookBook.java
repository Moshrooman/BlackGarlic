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

    //So to understand this part, we need to understand that CookBookAdapter's constructor takes 4 LIST parameters (exclude context parameter)
    //the first one is for the adapters cookBookList, the second one is for the adapters cookBookListSearch
    //the third one is for adapters fullcookBookList, and the fourth one is for the adapters fullCookBookListSearch
    //so 1 and 3 pertain to if the user DIDN'T search
    //and 2 and 4 pertain to if the user DID search

    //So the reason why we only have 1 adapter is because when the user searches, we call new CookBookAdapter on this 1 adapter,
    //and if the user then deletes everything in the search bar, then it will also call new CookBookAdapter on this 1 adapter.

    //However, knowing the constructor information we know above, we will be filling in specific parameters in the constructor and leaving
    //the other ones blank, depending on if they have anything in the search bar, vs. if they do have something in the search bar.

    //So using the knowledge we just learnt, we can call new CookBookAdapter as it will indeed create a whole new object, and previous
    //values from the CookBookAdapter class will not be carried. So knowing that, because in our constructor for CookBookAdapter
    // we are using for loops to add values into our variables in the CookBookAdapter as opposed to using "="
    // (TO AVOID REFERENCING THE SAME OBJECT, BECAUSE IF WE USE "=" IF WE DELETE SOMETHING FROM THE LIST OF COOKBOOKADAPTER IT WILL DELETE
    //FROM THE LIST HERE IN COOKBOOKADAPTER BECAUSE WE PASSED IT AS THE PARAMETER, AN OBJECT IN DIFFERENT CLASS REFERENCES IT, SO NOW
    //THEY POINT TO THE SAME OBJECT, SO CHANGE IN ONE RESULTS IN CHANGE IN ANOTHER), it won't add onto a list with previous values
    //because new CookBookAdapter actually does create a new Object from the class.

    //So the first time that this adapter is given value and is called = new CookBookAdapter on, is in the string request.
    //And because the first list that is gonna be shown to the user is the absolute full list, without any search, we pass the
    //cookbookobjectlistadapter (with 20 of the first items in it) as the first parameter, and the full cookbookobjectlist as the third
    //parameter, and therefore make the second and fourth parameter null, also remember to set search boolean to false.

    //Then if the user then searches something in the edittext.addontextchanged listener, we create a new adapter out of this 1 adapter
    //again, then we pass the cookBookObjectListSearchAdapter as the second parameter (which will contain the items that contains
    //the searched string, and will have been emptied out prior to prevent adding onto existing items, and the cookBookObjectList as the
    //fourth parameter, then as a result we make the first and third parameters null, also we set the searchedboolean to true.

    //Then in the addontextchangedlistener if the user then deletes all what he searched, we create the same adapter that we did in the
    //string request because this is the correct setting for if the user didn't search anything, and we set the searched boolean to false.

    //NOW GO DOWN TO STRING REQUEST TO SHOW THE PARAMETERS FILLED IN.
    private static CookBookAdapter cookBookAdapter;

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

                    cookBookAdapter = new CookBookAdapter(cookBookObjectListAdapter, null, cookBookObjectList, null, CookBook.this);
                    cookBookAdapter.setSearchBoolean(false);
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

                    cookBookAdapter = new CookBookAdapter(null, cookBookObjectListSearchAdapter, null, cookBookObjectList, CookBook.this);
                    cookBookAdapter.setSearchBoolean(true);
                    cookBookAdapter.setSearchedString(String.valueOf(s));

                    //Then set the adapter.
                    cookBookRecyclerView.setAdapter(cookBookAdapter);
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

                int positionToGrabString = 0;
                int totalRemoved = 0;

                for (int i = positionToGrabString; i < cookBookObjectList.size(); i++) {
                    String menuTitle = cookBookObjectList.get(positionToGrabString).getMenu_name();

                    for (int j = i + 1; j < cookBookObjectList.size(); j++) {
                        if (cookBookObjectList.get(j).getMenu_name().equals(menuTitle)) {
                            cookBookObjectList.remove(j);
                            j--;
                            totalRemoved++;
                        }
                    }

                    positionToGrabString++;

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
