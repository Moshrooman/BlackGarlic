package id.blackgarlic.blackgarlic.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;

/**
 * Created by JustinKwik on 1/18/16.
 */
public class Menu {

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    @SerializedName("menu_id")
    private String menuId;

    @SerializedName("menu_name")
    private String menuName;

    private String menuImageUrl;

    public String getMenuImageUrl() {
        return BLACKGARLIC_PICTURES.replace("menu_id", menuId);
    }

    public boolean mIsSelected = false;

    public Menu(String menuId, String menuName, String menuImageUrl, boolean mIsSelected) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuImageUrl = menuImageUrl;
        this.mIsSelected = mIsSelected;
    }

    public boolean getIsSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

}

