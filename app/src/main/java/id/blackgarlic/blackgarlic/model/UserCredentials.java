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

    @SerializedName("address_id")
    private String address_id;

    @SerializedName("customer_status")
    private String customer_status;

    @SerializedName("customer_email")
    private String customer_email;

    public UserCredentials(String customer_name, String customer_id, String address_content, String city, String mobile, String zipcode, String address_notes, String address_id, String customer_status, String customer_email) {
        this.customer_name = customer_name;
        this.customer_id = customer_id;
        this.address_content = address_content;
        this.city = city;
        this.mobile = mobile;
        this.zipcode = zipcode;
        this.address_notes = address_notes;
        this.address_id = address_id;
        this.customer_status = customer_status;
        this.customer_email = customer_email;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getAddress_id() {
        return address_id;
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

    public String getCustomer_status() {
        return customer_status;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public String setCity() {

        if (this.city.equals("1")) {
            this.city = "Jakarta Pusat";
        } else if (this.city.equals("2")) {
            this.city = "Jakarta Selatan";
        } else if (this.city.equals("3")) {
            this.city = "Jakarta Barat";
        } else if (this.city.equals("4")) {
            this.city = "Jakarta Utara";
        } else if (this.city.equals("5")) {
            this.city = "Jakarta Timur";
        } else if (this.city.equals("6")) {
            this.city = "Tangerang";
        } else if (this.city.equals("7")) {
            this.city = "Bekasi";
        } else if (this.city.equals("8")) {
            this.city = "Tangerang Selatan";
        } else if (this.city.equals("9")) {
            this.city = "Depok";
        } else if (this.city.equals("10")) {
            this.city = "Bogor";
        }

        return this.city;

    }

}
