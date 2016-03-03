package id.blackgarlic.blackgarlic.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin Kwik on 03/03/2016.
 */
public class Menus {

    @SerializedName("menus")
    private Data[] menus;

    public List<Data> getDataList() {
        List<Data> dataList = new ArrayList<Data>();

        for (int i = 0; i < menus.length; i++) {
            dataList.add(menus[i]);
        }

        return dataList;
    }

}
