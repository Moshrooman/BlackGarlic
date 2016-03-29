package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import id.blackgarlic.blackgarlic.welcome.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Runnable runnable2secs = new Runnable() {
            @Override
            public void run() {

                if (!sharedPreferences.contains("boolean")) {
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putBoolean("boolean", false);
                    editor.commit();
                    nextWelcomeActivity();
                } else {

                    goToLoginActivity();
                }

            }
        };

        android.os.Handler myHandler = new android.os.Handler();
        myHandler.postDelayed(runnable2secs, 2000);

    }

    public void nextWelcomeActivity() {
        Intent welcomeActivityIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
        startActivity(welcomeActivityIntent);
        finish();
    }

    public void goToLoginActivity() {
        Intent loginActivityIntent = new Intent(SplashActivity.this, LogInScreen.class);
        startActivity(loginActivityIntent);
        finish();
    }

}
