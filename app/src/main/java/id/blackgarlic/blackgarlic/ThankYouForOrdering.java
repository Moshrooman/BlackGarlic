package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ThankYouForOrdering extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_for_ordering);

//        if (getIntent().getExtras().getString("paymentMethod").equals("bank_transfer")) {
//            RelativeLayout bankTransferThankYourForOrderingRelativeLayout = (RelativeLayout) findViewById(R.id.bankTransferThankYourForOrderingRelativeLayout);
//            bankTransferThankYourForOrderingRelativeLayout.setVisibility(View.VISIBLE);
//        }

        TextView thankYouForOrderingDateTextView = (TextView) findViewById(R.id.thankYouForOrderingDateTextView);
        TextView thankYouForOrderingOrderConfirmationTextView = (TextView) findViewById(R.id.thankYouForOrderingOrderConfirmationTextView);

        thankYouForOrderingDateTextView.setText(getIntent().getExtras().getString("orderDate"));
        thankYouForOrderingOrderConfirmationTextView.setText(getIntent().getExtras().getString("uniqueId"));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("clickedBack", true);
            editor.commit();
            Intent mainActivityIntent = new Intent(ThankYouForOrdering.this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainActivityIntent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
