package id.blackgarlic.blackgarlic.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin Kwik on 03/03/2016.
 */
public class Data {

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    @SerializedName("menu_name")
    private String menu_name;

    @SerializedName("menu_subname")
    private String menu_subname;

    @SerializedName("menu_description")
    private String menu_description;

    @SerializedName("menu_type")
    private String menu_type;

    private boolean fourPersonEnabled = false;

    private boolean isFlipped = false;

    public boolean getFourPersonEnabled() {
        return this.fourPersonEnabled;
    }

    public void setFourPersonEnabled(boolean input) {
        this.fourPersonEnabled = input;
    }

    public String getMenuUrl() {
        return BLACKGARLIC_PICTURES;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public String getMenu_subname() {
        return menu_subname;
    }

    public String getMenu_description() {
        return menu_description;
    }

    public String getMenu_type() {
        return menu_type;
    }

    public boolean getIsFlipped() {
        return isFlipped;
    }

    public void setIsFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public Data(String menu_name, String menu_subname, String menu_description, String menu_type) {
        this.menu_name = menu_name;
        this.menu_subname = menu_subname;
        this.menu_description = menu_description;
        this.menu_type = menu_type;
    }

}
