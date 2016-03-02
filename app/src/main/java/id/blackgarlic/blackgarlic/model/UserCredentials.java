package id.blackgarlic.blackgarlic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Kwik on 02/03/2016.
 */
public class UserCredentials {

    @SerializedName("customer_name")
    private String customer_name;

    @SerializedName("customer_id")
    private String customer_id;

    @SerializedName("address_content")
    private String address_content;

    @SerializedName("city")
    private String city;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("zipcode")
    private String zipcode;

    @SerializedName("address_notes")
    private String address_notes;

    public UserCredentials(String customer_name, String customer_id, String address_content, String city, String mobile, String zipcode, String address_notes) {
        this.customer_name = customer_name;
        this.customer_id = customer_id;
        this.address_content = address_content;
        this.city = city;
        this.mobile = mobile;
        this.zipcode = zipcode;
        this.address_notes = address_notes;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getAddress_content() {
        return address_content;
    }

    public String getCity() {
        return city;
    }

    public String getMobile() {
        return mobile;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getAddress_notes() {
        return address_notes;
    }
}
