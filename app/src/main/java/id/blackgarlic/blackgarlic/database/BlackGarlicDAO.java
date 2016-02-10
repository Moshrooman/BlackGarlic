package id.blackgarlic.blackgarlic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.Menu;

/**
 * Created by JustinKwik on 2/9/16.
 */
public class BlackGarlicDAO {

    private static BlackGarlicDAO sInstance;

    public static BlackGarlicDAO getInstance() {

        if (sInstance == null) {
            sInstance = new BlackGarlicDAO();
        }

        return sInstance;

    }

    public boolean storeMenus(Context context, Menu[] menuList) {

        try {

            SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();

            db.beginTransaction();

            for (int j = 0; j < menuList.length; j++) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseContract.PostTable.TITLE, menuList[j].getMenuName());
                cv.put(DatabaseContract.PostTable.IMAGELINK, menuList[j].getMenuImageUrl());

                db.insert(DatabaseContract.PostTable.TABLE_NAME, null, cv);

            }

            db.setTransactionSuccessful();

            db.endTransaction();

            db.close();

        } catch (Exception e) {

            Log.e("Stored Status: ", "Failed");

            return false;

        }

        Log.e("Stored Status: ", "Successful");

        return true;
    }

    public Menu[] getPostsFromDB(Context context) {

        Menu[] menuList = new Menu[9];

        List<Menu> menuListList = new ArrayList<Menu>();

        SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();

        Cursor cursor = db.query(DatabaseContract.PostTable.TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();

        while (cursor.moveToNext()) {


            String menuName = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.TITLE));
            String menuImageUrl = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.IMAGELINK));

            Menu menu = new Menu(menuName, menuImageUrl);

            menuListList.add(menu);

        }

        cursor.close();
        db.close();

        for (int i = 0; i < menuList.length; i++) {
            menuList[i] = menuListList.get(i);
        }

        Log.e("Name: ", menuList[0].getMenuName());
        Log.e("ID: ", menuList[0].getMenuImageUrl());

        return menuList;

    }

}
