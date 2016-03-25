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

        Integer boxId = 0;

        boxId = boxIds.get(1);

        return boxId;
    }

}
