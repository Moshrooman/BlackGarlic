package id.blackgarlic.blackgarlic.Voucher;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Justin Kwik on 28/05/2016.
 */
public class VoucherObject {

    public VoucherObject(int voucher_id, int voucher_value) {
        this.voucher_id = voucher_id;
        this.voucher_value = voucher_value;
    }

    @SerializedName("voucher_id")
    private int voucher_id;

    @SerializedName("voucher_value")
    private int voucher_value;

    public int getVoucher_id() {
        return voucher_id;
    }

    public int getVoucher_value() {
        return voucher_value;
    }

    public void setVoucher_id(int voucher_id) {
        this.voucher_id = voucher_id;
    }

    public void setVoucher_value(int voucher_value) {
        this.voucher_value = voucher_value;
    }
}
