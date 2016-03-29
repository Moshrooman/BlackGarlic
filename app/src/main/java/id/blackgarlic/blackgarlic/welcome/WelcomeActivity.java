package id.blackgarlic.blackgarlic.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;

import id.blackgarlic.blackgarlic.LogInScreen;
import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        final SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        radioGroup.check(R.id.radioButton0);

                        break;
                    case 1:
                        radioGroup.check(R.id.radioButton1);

                        break;
                    case 2:
                        radioGroup.check(R.id.radioButton2);

                        break;
                    case 3:
                        radioGroup.check(R.id.radioButton3);

                        break;
                    case 4:

                        if (!sharedPreferences.contains("firsttime")) {
                            SharedPreferences.Editor editor =  sharedPreferences.edit();
                            editor.putBoolean("firsttime", false);
                            editor.commit();

                            radioGroup.check(R.id.radioButton4);
                            Intent switchToLogInActivity = new Intent(WelcomeActivity.this, LogInScreen.class);
                            startActivity(switchToLogInActivity);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                            return;

                        } else {

                            radioGroup.check(R.id.radioButton4);
                            Intent mainActivityIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(mainActivityIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                            return;

                        }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

    }

    public class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new YoutubeWelcomeVideo();

            } else if (position == 1){
                return new WePlan();

            } else if (position == 2){
                return new WeShop();

            } else if (position ==3) {
                return new YouCook();

            } else {
                return new SwitchToLoginFragment();
            }
        }


        @Override
        public int getCount() {
            return 5;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
