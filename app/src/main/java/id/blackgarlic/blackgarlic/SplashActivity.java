package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import id.blackgarlic.blackgarlic.welcome.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPreferences sharedPreferences = getSharedPreferences("My Preferences", Context.MODE_PRIVATE);

        Runnable runnable2secs = new Runnable() {
            @Override
            public void run() {

                if (sharedPreferences.getBoolean("Welcome Boolean", true)) {
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putBoolean("Welcome Boolean", false);
                    editor.commit();
                    nextWelcomeActivity();
                } else {

                    goToMainActivity();
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

    public void goToMainActivity() {
        Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

}
