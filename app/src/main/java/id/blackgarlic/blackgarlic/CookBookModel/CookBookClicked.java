package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CookBookClicked extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static ViewPager cookBookViewPager;
    private static LinearLayout dotsLinearLayout;
    private static TextView viewPagerTitle;
    private static CookBookViewPagerAdapter viewPagerAdapter;
    private static int dotCount;
    private static ImageView[] dotsImageViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_book_clicked);

        cookBookViewPager = (ViewPager) findViewById(R.id.cookBookViewPager);
        dotsLinearLayout = (LinearLayout) findViewById(R.id.dotsLinearLayout);
        viewPagerTitle = (TextView) findViewById(R.id.viewPagerTitle);
        viewPagerAdapter = new CookBookViewPagerAdapter(CookBookClicked.this);

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
                params.setMargins(4, 0, 0, 0);
            }

            dotsLinearLayout.addView(dotsImageViewArray[i], params);

        }

        dotsImageViewArray[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteddot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < dotsImageViewArray.length; i++) {
            dotsImageViewArray[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteddot));
        }

        dotsImageViewArray[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteddot));

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
