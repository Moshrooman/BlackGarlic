package id.blackgarlic.blackgarlic.Referral;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JustinKwik on 1/7/17.
 */
public class Referral_Redeemed_Object {

    @SerializedName("unique_id")
    private int unique_id;

    @SerializedName("order_date")
    private String order_date;

    @SerializedName("address_content")
    private String address_content;

    @SerializedName("customer_name")
    private String customer_name;

    public Referral_Redeemed_Object(int unique_id, String order_date, String address_content, String customer_name) {
        this.unique_id = unique_id;
        this.order_date = order_date;
        this.address_content = address_content;
        this.customer_name = customer_name;
    }

    public int getUnique_id() {
        return unique_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getAddress_content() {
        return address_content;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setUnique_id(int unique_id) {
        this.unique_id = unique_id;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public void setAddress_content(String address_content) {
        this.address_content = address_content;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

}
