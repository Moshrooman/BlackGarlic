package id.blackgarlic.blackgarlic.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin Kwik on 03/03/2016.
 */
public class MenuId {

    @SerializedName("menu_ids")
    private List<Integer> menuIds;

    @SerializedName("box_ids")
    private List<Integer> boxIds;

    public List<Integer> getMenuIds() {
        List<Integer> menuIdList = new ArrayList<Integer>();

        for (int i = 0; i < menuIds.size(); i++) {

            menuIdList.add(menuIds.get(i));

        }

        return menuIdList;
    }

    public Integer getBoxId() {

        if (boxIds.size() != 1) {

            Log.e("More than 1: ", "True");

            Integer boxId = boxIds.get(0);

            for (int i = 1; i < boxIds.size(); i++) {

                if (boxIds.get(i) > boxId) {
                    boxId = boxIds.get(i);
                }

                return boxId;

            }

        } else {
            Log.e("More than 1: ", "False");
            Integer boxId = boxIds.get(0);
            return boxId;
        }

        return -1;
    }

}
