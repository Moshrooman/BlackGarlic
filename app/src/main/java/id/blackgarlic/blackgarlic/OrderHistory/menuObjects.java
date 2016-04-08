package id.blackgarlic.blackgarlic.OrderHistory;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JustinKwik on 4/8/16.
 */
public class menuObjects {

    public menuObjects(int portion, int menu_id, String menu_name, int menu_type) {
        this.portion = portion;
        this.menu_id = menu_id;
        this.menu_name = menu_name;
        this.menu_type = menu_type;
    }

    @SerializedName("portion")
    private int portion;

    @SerializedName("menu_id")
    private int menu_id;

    @SerializedName("menu_name")
    private String menu_name;

    @SerializedName("menu_type")
    private int menu_type;

    public int getPortion() {
        return portion;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public int getMenu_type() {
        return menu_type;
    }
}
