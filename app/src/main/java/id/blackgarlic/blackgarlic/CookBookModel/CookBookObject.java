package id.blackgarlic.blackgarlic.CookBookModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Justin Kwik on 10/06/2016.
 */
public class CookBookObject {

    public CookBookObject(int menu_id, String menu_name, String menu_subname, String menu_description, String menu_code, int cuisine_id, int base_id, int menu_type, int menu_status, int child, int box_id, List<CookBookSteps> cookBookStepList) {
        this.menu_id = menu_id;
        this.menu_name = menu_name;
        this.menu_subname = menu_subname;
        this.menu_description = menu_description;
        this.menu_code = menu_code;
        this.cuisine_id = cuisine_id;
        this.base_id = base_id;
        this.menu_type = menu_type;
        this.menu_status = menu_status;
        this.child = child;
        this.box_id = box_id;
        this.cookBookStepList = cookBookStepList;
    }

    @SerializedName("menu_id")
    private int menu_id;

    @SerializedName("menu_name")
    private String menu_name;

    @SerializedName("menu_subname")
    private String menu_subname;

    @SerializedName("menu_description")
    private String menu_description;

    @SerializedName("menu_code")
    private String menu_code;

    @SerializedName("cuisine_id")
    private int cuisine_id;

    @SerializedName("base_id")
    private int base_id;

    @SerializedName("menu_type")
    private int menu_type;

    @SerializedName("menu_status")
    private int menu_status;

    @SerializedName("child")
    private int child;

    @SerializedName("box_id")
    private int box_id;

    @SerializedName("steps")
    private List<CookBookSteps> cookBookStepList;

    private boolean isFavorited = false;

    public boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public int getMenu_id() {
        return menu_id;
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

    public String getMenu_code() {
        return menu_code;
    }

    public int getCuisine_id() {
        return cuisine_id;
    }

    public int getBase_id() {
        return base_id;
    }

    public int getMenu_type() {
        return menu_type;
    }

    public int getMenu_status() {
        return menu_status;
    }

    public int getChild() {
        return child;
    }

    public int getBox_id() {
        return box_id;
    }

    public List<CookBookSteps> getCookBookStepList() {
        return cookBookStepList;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public void setMenu_subname(String menu_subname) {
        this.menu_subname = menu_subname;
    }

    public void setMenu_description(String menu_description) {
        this.menu_description = menu_description;
    }

    public void setMenu_code(String menu_code) {
        this.menu_code = menu_code;
    }

    public void setCuisine_id(int cuisine_id) {
        this.cuisine_id = cuisine_id;
    }

    public void setBase_id(int base_id) {
        this.base_id = base_id;
    }

    public void setMenu_type(int menu_type) {
        this.menu_type = menu_type;
    }

    public void setMenu_status(int menu_status) {
        this.menu_status = menu_status;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public void setBox_id(int box_id) {
        this.box_id = box_id;
    }

    public void setCookBookStepList(List<CookBookSteps> cookBookStepList) {
        this.cookBookStepList = cookBookStepList;
    }
}
