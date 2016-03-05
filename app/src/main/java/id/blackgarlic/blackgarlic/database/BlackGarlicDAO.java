package id.blackgarlic.blackgarlic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.Data;

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

    public boolean storeMenus(Context context, List<Data> menuList) {

        try {

            SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();

            db.beginTransaction();

//            for (int j = 0; j < menuList.length; j++) {
//                ContentValues cv = new ContentValues();
//                cv.put(DatabaseContract.PostTable.TITLE, menuList[j].getMenuName());
//                cv.put(DatabaseContract.PostTable.IMAGELINK, menuList[j].getMenuImageUrl());
//                cv.put(DatabaseContract.PostTable.MENUID, menuList[j].getMenuId());
//                cv.put(DatabaseContract.PostTable.ISSELECTED, menuList[j].getIsSelected());
//
//                Log.e("Name: ", cv.getAsString(DatabaseContract.PostTable.TITLE));
//                Log.e("URL: ", cv.getAsString(DatabaseContract.PostTable.IMAGELINK));
//
//                db.insert(DatabaseContract.PostTable.TABLE_NAME, null, cv);
//
//            }

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

//    public Menu[] getMenusFromDB(Context context) {
//
//        Menu[] menuList = new Menu[9];
//
//        List<Menu> menuListList = new ArrayList<Menu>();
//
//        SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
//
//        Cursor cursor = db.query(DatabaseContract.PostTable.TABLE_NAME, null, null, null, null, null, null);
//
//        cursor.moveToFirst();
//
//        while (cursor.moveToNext()) {
//
//
//            String menuName = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.TITLE));
//            String menuImageUrl = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.IMAGELINK));
//            String menuId = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.MENUID));
//            String isSelected = cursor.getString(cursor.getColumnIndex(DatabaseContract.PostTable.ISSELECTED));
//
//            Menu menu = new Menu(menuId, menuName, menuImageUrl, Boolean.valueOf(isSelected));
//
//            Log.e("Name: ", String.valueOf(menu.getMenuName()));
//            Log.e("URL: ", String.valueOf(menu.getMenuImageUrl()));
//
//            menuListList.add(menu);
//
//        }
//
//        //Log.e("Name: ", menuListList.get(0).getMenuName());
//        //Log.e("ID: ", menuListList.get(0).getMenuImageUrl());
//
//        cursor.close();
//        db.close();
//
//        if (menuListList.size() == 0) {
//            return new Menu[0];
//        }
//
//        for (int i = 0; i < menuList.length; i++) {
//            menuList[i] = menuListList.get(menuListList.size() - i - 1);
//        }
//
//        return menuList;
//
//    }

}
