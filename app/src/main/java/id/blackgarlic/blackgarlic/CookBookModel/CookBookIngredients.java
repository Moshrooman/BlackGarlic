package id.blackgarlic.blackgarlic.CookBookModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Kwik on 15/07/2016.
 */
public class CookBookIngredients {

    public CookBookIngredients(String ingredient_id, String ingredient_name) {
        this.ingredient_id = ingredient_id;
        this.ingredient_name = ingredient_name;
    }

    @SerializedName("ingredient_id")
    private String ingredient_id;

    @SerializedName("ingredient_name")
    private String ingredient_name;

    public String getIngredient_id() {
        return ingredient_id;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public void setIngredient_id(String ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public void setIngredient_name(String ingredient_name) {
        this.ingredient_name = ingredient_name;
    }
}
