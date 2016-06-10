package id.blackgarlic.blackgarlic.CookBookModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Kwik on 10/06/2016.
 */
public class CookBookSteps {

    public CookBookSteps(int steps_order, String content_id, String content_en) {
        this.steps_order = steps_order;
        this.content_id = content_id;
        this.content_en = content_en;
    }

    @SerializedName("steps_order")
    private int steps_order;

    @SerializedName("content_id")
    private String content_id;

    @SerializedName("content_en")
    private String content_en;

    public int getSteps_order() {
        return steps_order;
    }

    public String getContent_id() {
        return content_id;
    }

    public String getContent_en() {
        return content_en;
    }

    public void setSteps_order(int steps_order) {
        this.steps_order = steps_order;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }

    public void setContent_en(String content_en) {
        this.content_en = content_en;
    }
}
