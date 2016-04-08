package id.blackgarlic.blackgarlic.OrderHistory;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by JustinKwik on 4/8/16.
 */
public class BaseArrayObjects {

    public BaseArrayObjects(String order_id, int grandtotal, int payment_status, int order_status, String order_date, String added, String address_content, String mobile, List<menuObjects> menuObjectList) {
        this.order_id = order_id;
        this.grandtotal = grandtotal;
        this.payment_status = payment_status;
        this.order_status = order_status;
        this.order_date = order_date;
        this.added = added;
        this.address_content = address_content;
        this.mobile = mobile;
        this.menuObjectList = menuObjectList;
    }

    @SerializedName("order_id")
    private String order_id;

    @SerializedName("grandtotal")
    private int grandtotal;

    @SerializedName("payment_status")
    private int payment_status;

    @SerializedName("order_status")
    private int order_status;

    @SerializedName("order_date")
    private String order_date;

    @SerializedName("added")
    private String added;

    @SerializedName("address_content")
    private String address_content;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("unique_id")
    private String unique_id;

    @SerializedName("menus")
    private List<menuObjects> menuObjectList;

    //Create a new menuObjects that serialized name the portions and shit
    //Create constructor and getters for all variables
    //Create method that returnslist with all the stuff inside.


    public String getOrder_id() {
        return order_id;
    }

    public int getGrandtotal() {
        return grandtotal;
    }

    public int getPayment_status() {
        return payment_status;
    }

    public int getOrder_status() {
        return order_status;
    }

    public String getOrder_date() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

        DateTime dateTime = new DateTime(order_date);

        Date date = dateTime.toDate();

        return format.format(date);
    }

    public String getAdded() {
        return added;
    }

    public String getAddress_content() {
        return address_content;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public List<menuObjects> getMenuObjectList() {
        return menuObjectList;
    }
}
