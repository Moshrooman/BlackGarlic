package id.blackgarlic.blackgarlic.CookBookModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Justin Kwik on 24/06/2016.
 */
public class FavoritesArray {

    @SerializedName("favorites")
    private List<String> favoritesArray;

    public List<String> getFavoritesArray() {
        return favoritesArray;
    }

    public void setFavoritesArray(List<String> favoritesArray) {
        this.favoritesArray = favoritesArray;
    }

    public FavoritesArray(List<String> favoritesArray) {
        this.favoritesArray = favoritesArray;
    }
}
