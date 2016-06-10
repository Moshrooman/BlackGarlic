package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CookBook extends AppCompatActivity {

    private static final String cookBookLink = "http://188.166.221.241:3000/app/cookbook";

    private RecyclerView cookBookRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book);

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

                List<CookBookObject> cookBookObjectList = cookBookList.getCookBookList();

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

                CookBookAdapter cookBookAdapter = new CookBookAdapter(cookBookObjectList, CookBook.this);

                cookBookRecyclerView.setAdapter(cookBookAdapter);

                //So first I made the main array called "menus" that way I can find it in the cookbook list.
                //Then I made the cookbook object which has all of the main objects in each element but also a list of cookbook steps
                //Then in the cookbook steps I serialized named all the things in the steps array in each element.

                //But important thing is that I had to make the whole thing an object because if I were to do the new Gson().fromJson
                //and have the variable an array, which it should be, because the first item would be an array called menus,
                //then it wouldn't work because I would have no access to the method in cookbookList that returns a list,
                //so what I had to do was make the whole thing into an object by adding { in the beginning and } in the end,
                //so that way I can call the .getCookBookList on it.

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
