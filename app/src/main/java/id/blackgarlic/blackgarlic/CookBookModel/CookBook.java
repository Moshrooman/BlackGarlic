package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

    private static List<CookBookObject> cookBookObjectListAdapter = new ArrayList<CookBookObject>();

    private static int startingPositionForAddingIntoAdapterList;

    public static int getStartingPositionForAddingIntoAdapterList() {
        return startingPositionForAddingIntoAdapterList;
    }

    public static void setStartingPositionForAddingIntoAdapterList(int startingPositionForAddingIntoAdapterList) {
        CookBook.startingPositionForAddingIntoAdapterList = startingPositionForAddingIntoAdapterList;
    }

    public static List<CookBookObject> getCookBookObjectList() {
        return cookBookObjectList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);

        cookBookObjectList.clear();
        cookBookObjectListAdapter.clear();
        startingPositionForAddingIntoAdapterList = 0;

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

                CookBookAdapter cookBookAdapter = new CookBookAdapter(cookBookObjectListAdapter, CookBook.this);

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
