package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

        saveBitmapsToCache();

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

    public void saveBitmapsToCache(){


        //To get the bitmap from the imageView
        Bitmap bitmap1 = ((BitmapDrawable)getResources().getDrawable(R.drawable.calendar)).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable)getResources().getDrawable(R.drawable.shoppingcart)).getBitmap();
        Bitmap bitmap3 = ((BitmapDrawable)getResources().getDrawable(R.drawable.deliverytruck)).getBitmap();
        Bitmap bitmap4 = ((BitmapDrawable)getResources().getDrawable(R.drawable.delivery)).getBitmap();
        Bitmap bitmap5 = ((BitmapDrawable)getResources().getDrawable(R.drawable.creditcard)).getBitmap();
        Bitmap bitmap6 = ((BitmapDrawable)getResources().getDrawable(R.drawable.finalshoppingcart)).getBitmap();

        //Saving bitmap to cache. it will later be retrieved using the bitmap_image key
        Cache.getInstance().getLru().put("bitmap_image1", bitmap1);
        Cache.getInstance().getLru().put("bitmap_image2", bitmap2);
        Cache.getInstance().getLru().put("bitmap_image3", bitmap3);
        Cache.getInstance().getLru().put("bitmap_image4", bitmap4);
        Cache.getInstance().getLru().put("bitmap_image5", bitmap5);
        Cache.getInstance().getLru().put("bitmap_image6", bitmap6);
    }

}
