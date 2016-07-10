package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CookBookClicked extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static ViewPager cookBookViewPager;
    private static LinearLayout dotsLinearLayout;
    private static TextView viewPagerTitle;
    private static CookBookViewPagerAdapter viewPagerAdapter;
    private static int dotCount;
    private static ImageView[] dotsImageViewArray;
    private static int currentPosition;
    private static CookBookObject cookBookObject;
    private static Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_clicked);

        String cookBookString = getIntent().getExtras().getString("object", "");
        cookBookObject = new Gson().fromJson(cookBookString, CookBookObject.class);

        cookBookViewPager = (ViewPager) findViewById(R.id.cookBookViewPager);
        dotsLinearLayout = (LinearLayout) findViewById(R.id.dotsLinearLayout);
        viewPagerTitle = (TextView) findViewById(R.id.viewPagerTitle);
        viewPagerAdapter = new CookBookViewPagerAdapter(CookBookClicked.this, cookBookObject);
        currentPosition = 0;
        exitButton = (Button) findViewById(R.id.exitButton);

        cookBookViewPager.setAdapter(viewPagerAdapter);
        cookBookViewPager.setCurrentItem(0);
        cookBookViewPager.setOnPageChangeListener(this);

        dotCount = viewPagerAdapter.getCount();
        dotsImageViewArray = new ImageView[dotCount];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < dotCount; i++) {
            dotsImageViewArray[i] = new ImageView(this);
            dotsImageViewArray[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteddot));

            if (i == 0) {
                params.setMargins(0, 0, 0, 0);
            } else {
                params.setMargins(15, 0, 0, 0);
            }

            dotsLinearLayout.addView(dotsImageViewArray[i], params);

        }

        dotsImageViewArray[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteddot));

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        exitButton.setBackgroundResource(R.drawable.exitbuttonclicked);
                        exitButton.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case MotionEvent.ACTION_UP:
                        exitButton.setBackgroundResource(R.drawable.exitbutton);
                        exitButton.setTextColor(getResources().getColor(R.color.BGGREEN));
                        break;

                }

                return false;
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Animation upscaleDotLeftToRight = AnimationUtils.loadAnimation(CookBookClicked.this, R.anim.anim_upscale_dot_lefttoright);
        Animation upscaleDotRightToLeft = AnimationUtils.loadAnimation(CookBookClicked.this, R.anim.anim_upscale_dot_righttoleft);

        Animation downscaleDotLeftToRight = AnimationUtils.loadAnimation(CookBookClicked.this, R.anim.anim_downscale_dot_lefttoright);
        Animation downscaleDotRightToLeft = AnimationUtils.loadAnimation(CookBookClicked.this, R.anim.anim_downscale_dot_righttoleft);

        for (int i = 0; i < dotsImageViewArray.length; i++) {

            if (dotsImageViewArray[i].getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.selecteddot).getConstantState())) {
                dotsImageViewArray[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteddot));

                if (position > currentPosition) {
                    dotsImageViewArray[i].startAnimation(downscaleDotLeftToRight);
                } else {
                    dotsImageViewArray[i].startAnimation(downscaleDotRightToLeft);
                }

            }

        }

        dotsImageViewArray[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteddot));

        if (position > currentPosition) {
            dotsImageViewArray[position].startAnimation(upscaleDotRightToLeft);
            currentPosition = position;
        } else {
            dotsImageViewArray[position].startAnimation(upscaleDotLeftToRight);
            currentPosition = position;
        }

        if (position == 0) {
            viewPagerTitle.setText("Overview");
        } else {
            viewPagerTitle.setText("Step " + String.valueOf(position) + " of " + String.valueOf(cookBookObject.getCookBookStepList().size()));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
