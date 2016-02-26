package id.blackgarlic.blackgarlic.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

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
                        radioGroup.check(R.id.radioButton4);
                        Intent switchToMainActivityIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(switchToMainActivityIntent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        return;
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
                return new SwitchToMainActivity();
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

    public void playVideo(View view) {
        Intent intent = new Intent(WelcomeActivity.this, PopUpYouTube.class);
        startActivity(intent);



    }

}
