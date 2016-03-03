package id.blackgarlic.blackgarlic.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by Justin Kwik on 03/03/2016.
 */
public class MenuId {

    @SerializedName("menu_ids")
    private int[] menuIds;

    public int[] getMenuIds() {
        int[] menuIdList = new int[menuIds.length];

        for (int i = 0; i < menuIds.length; i++) {

            menuIdList[i] = menuIds[i];

        }

        return menuIdList;
    }

}
