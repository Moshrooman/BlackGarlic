package id.blackgarlic.blackgarlic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import id.blackgarlic.blackgarlic.model.UserCredentials;

public class MyAccount extends AppCompatActivity {

    private static UserCredentials userCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        userCredentials = LogInScreen.getUserCredentials();

        //All EditTexts:

        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        EditText addressEditText = (EditText) findViewById(R.id.addressEditText);
        EditText cityEditText = (EditText) findViewById(R.id.cityEditText);
        EditText mobileEditText = (EditText) findViewById(R.id.mobileEditText);
        EditText zipcodeEditText = (EditText) findViewById(R.id.zipcodeEditText);
        EditText addressnotesEditText = (EditText) findViewById(R.id.addressnotesEditText);

        nameEditText.setText(userCredentials.getCustomer_name());
        addressEditText.setText(userCredentials.getAddress_content());
        cityEditText.setText(userCredentials.getCity());
        mobileEditText.setText(userCredentials.getMobile());
        zipcodeEditText.setText(userCredentials.getZipcode());
        addressnotesEditText.setText(userCredentials.getAddress_notes());


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent mainActivityIntent = new Intent(MyAccount.this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
