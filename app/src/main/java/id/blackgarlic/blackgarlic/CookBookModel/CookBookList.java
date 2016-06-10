package id.blackgarlic.blackgarlic.CookBookModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin Kwik on 10/06/2016.
 */
public class CookBookList {

    @SerializedName("menus")
    private CookBookObject[] cookBookObjectArray;

    public List<CookBookObject> getCookBookList() {

        List<CookBookObject> cookBookObjectList = new ArrayList<CookBookObject>();

        for (int i = 0; i < cookBookObjectArray.length; i++) {
            cookBookObjectList.add(cookBookObjectArray[i]);
        }

        return cookBookObjectList;

    }
}
