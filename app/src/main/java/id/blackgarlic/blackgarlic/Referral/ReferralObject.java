package id.blackgarlic.blackgarlic.Referral;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Justin Kwik on 07/11/2016.
 */
public class ReferralObject {

    @SerializedName("referral_id")
    private int referral_id;

    @SerializedName("referrer_id")
    private int referrer_id;

    @SerializedName("referred_email")
    private String referred_email;

    @SerializedName("menu_ids")
    private String menu_ids;

    @SerializedName("referral_status")
    private int referral_status;

    @SerializedName("referral_code")
    private String referral_code;

    private List<String> referral_menus;

    public ReferralObject(int referral_id, int referrer_id, String referred_email, String menu_ids, int referral_status, String referral_code, List<String> referral_menus) {
        this.referral_id = referral_id;
        this.referrer_id = referrer_id;
        this.referred_email = referred_email;
        this.menu_ids = menu_ids;
        this.referral_status = referral_status;
        this.referral_code = referral_code;
        this.referral_menus = referral_menus;
    }

    public List<String> getReferral_menus() {
        return referral_menus;
    }

    public int getReferral_id() {
        return referral_id;
    }

    public int getReferrer_id() {
        return referrer_id;
    }

    public String getReferred_email() {
        return referred_email;
    }

    public String getMenu_ids() {
        return menu_ids;
    }

    public int getReferral_status() {
        return referral_status;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_id(int referral_id) {
        this.referral_id = referral_id;
    }

    public void setReferrer_id(int referrer_id) {
        this.referrer_id = referrer_id;
    }

    public void setReferred_email(String referred_email) {
        this.referred_email = referred_email;
    }

    public void setMenu_ids(String menu_ids) {
        this.menu_ids = menu_ids;
    }

    public void setReferral_status(int referral_status) {
        this.referral_status = referral_status;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public void setReferral_menus(List<String> referral_menus) {
        this.referral_menus = referral_menus;
    }
}
