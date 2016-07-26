package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "id.blackgarlic.blackgarlic", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Runnable runnable2secs = new Runnable() {
            @Override
            public void run() {

                ImageView blackGarlicLogoSplash = (ImageView) findViewById(R.id.blackGarlicLogoSplash);

                if (!sharedPreferences.contains("boolean")) {
                    blackGarlicLogoSplash.setImageDrawable(null);
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putBoolean("boolean", false);
                    editor.commit();
                    nextWelcomeActivity();
                } else {
                    blackGarlicLogoSplash.setImageDrawable(null);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("fromSplash", true);
                    editor.commit();

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
