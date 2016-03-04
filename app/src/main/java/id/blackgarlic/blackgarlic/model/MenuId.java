package id.blackgarlic.blackgarlic.model;

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

    public List<Integer> getMenuIds() {
        List<Integer> menuIdList = new ArrayList<Integer>();

        for (int i = 0; i < menuIds.size(); i++) {

            menuIdList.add(menuIds.get(i));

        }

        return menuIdList;
    }

}
