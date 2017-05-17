package id.blackgarlic.blackgarlic.Referral;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Kwik on 16/11/2016.
 */

 //THIS IS A TEST
public class MenuNameObject {

    @SerializedName("menu_name")
    private String menu_name;

    @SerializedName("menu_id")
    private int menu_id;

    public MenuNameObject(String menu_name, int menu_id) {
        this.menu_name = menu_name;
        this.menu_id = menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }
}
