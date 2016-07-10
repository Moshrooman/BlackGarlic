package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import id.blackgarlic.blackgarlic.R;

/**
 * Created by Justin Kwik on 08/07/2016.
 */
public class CookBookViewPagerAdapter extends PagerAdapter {

    Context mcontext;
    CookBookObject mCookBookObject;

    public CookBookViewPagerAdapter(Context context, CookBookObject cookBookObject) {
        this.mcontext = context;
        this.mCookBookObject = cookBookObject;
    }

    @Override
    public int getCount() {
        return mCookBookObject.getCookBookStepList().size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
