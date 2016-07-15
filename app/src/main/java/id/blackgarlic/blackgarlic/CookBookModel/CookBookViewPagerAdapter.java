package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by Justin Kwik on 08/07/2016.
 */
public class CookBookViewPagerAdapter extends PagerAdapter {

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";

    Context mcontext;
    CookBookObject mCookBookObject;

    public CookBookViewPagerAdapter(Context context, CookBookObject cookBookObject) {
        this.mcontext = context;
        this.mCookBookObject = cookBookObject;
    }

    @Override
    public int getCount() {

        //If have no ingredients just leave 1 space for the overview
        if (mCookBookObject.getCookBookIngredientsList().size() == 0) {
            return mCookBookObject.getCookBookStepList().size() + 1;
        } else {
            return mCookBookObject.getCookBookStepList().size() + 2;
        }
        //But if there is ingredients, leave 2 spaces, 1 for overview and 1 for ingredients.

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (position == 0) {
            View clickedOverviewView = LayoutInflater.from(mcontext).inflate(R.layout.clicked_overview, container, false);

            //Instantiations
            SimpleDraweeView cookBookClickedImage = (SimpleDraweeView) clickedOverviewView.findViewById(R.id.cookBookClickedImage);
            TextView cookBookClickedImageTitle = (TextView) clickedOverviewView.findViewById(R.id.cookBookClickedImageTitle);
            TextView descriptionTextView = (TextView) clickedOverviewView.findViewById(R.id.descriptionTextView);

            //Setting Image Work
            cookBookClickedImage.setImageURI(Uri.parse(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(mCookBookObject.getMenu_id()))));

            //Setting Image Title Work with Fonts

            String menuTitle = mCookBookObject.getMenu_name();
            String menuSubname = mCookBookObject.getMenu_subname();

            CalligraphyTypefaceSpan robotoBold = new CalligraphyTypefaceSpan(TypefaceUtils.load(mcontext.getAssets(), "fonts/Roboto-Bold.ttf"));
            CalligraphyTypefaceSpan robotoThin = new CalligraphyTypefaceSpan(TypefaceUtils.load(mcontext.getAssets(), "fonts/Roboto-Thin.ttf"));

            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

            if (menuSubname.length() == 0) {
                stringBuilder.append(menuTitle);
                stringBuilder.setSpan(robotoBold, 0, menuTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                stringBuilder.append(menuTitle).append("\n" + menuSubname);
                stringBuilder.setSpan(robotoBold, 0, menuTitle.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(robotoThin, menuTitle.length() + 1, menuTitle.length() + menuSubname.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            cookBookClickedImageTitle.setText(stringBuilder, TextView.BufferType.SPANNABLE);

            //Setting description only if the description is not empty

            if (mCookBookObject.getMenu_description().equals("")) {
                descriptionTextView.setTextColor(mcontext.getResources().getColor(R.color.red));
                descriptionTextView.setText("Not Available At This Moment!");
            } else {
                descriptionTextView.setText(mCookBookObject.getMenu_description());
            }

            container.addView(clickedOverviewView);
            return clickedOverviewView;

        } else if (mCookBookObject.getCookBookIngredientsList().size() == 0) {

            View clickedStepsView = LayoutInflater.from(mcontext).inflate(R.layout.clicked_steps, container, false);

            //Instantiations
            TextView stepNumberTextView = (TextView) clickedStepsView.findViewById(R.id.stepNumberTextView);
            TextView stepsTextViewEnglish = (TextView) clickedStepsView.findViewById(R.id.stepsTextViewEnglish);
            TextView stepsTextViewIndo = (TextView) clickedStepsView.findViewById(R.id.stepsTextViewIndo);

            //Getting list from cookbookobject
            List<CookBookSteps> cookBookStepList = mCookBookObject.getCookBookStepList();

            //Setting textviews

            stepNumberTextView.setText(String.valueOf(position));
            stepsTextViewEnglish.setText(cookBookStepList.get(position - 1).getContent_en());
            stepsTextViewIndo.setText(cookBookStepList.get(position - 1).getContent_id());

            container.addView(clickedStepsView);
            return clickedStepsView;

        } else if ((mCookBookObject.getCookBookIngredientsList().size() != 0) && (position == 1)){
            View ingredientsView = LayoutInflater.from(mcontext).inflate(R.layout.clicked_ingredients, container, false);

            //So what i wanna do is loop through the ingredints list, and until the ingredients list size, I inflate a layout that has table
            //row as root layout and i set all the stuff inside, then i add the view into the table layout that I get from the clicked_ingredients

            TableLayout ingredientsTableLayout = (TableLayout) ingredientsView.findViewById(R.id.ingredientsTableLayout);

            List<CookBookIngredients> ingredientsList = mCookBookObject.getCookBookIngredientsList();

            for (int i = 0; i < ingredientsList.size(); i++) {

                TableRow ingredientsTableRow = (TableRow) View.inflate(mcontext, R.layout.ingredients_row, null);
                ingredientsTableRow.setTag(i);

                TextView firstIngredient = (TextView) ingredientsTableRow.findViewById(R.id.firstIngredient);
                TextView secondIngredient = (TextView) ingredientsTableRow.findViewById(R.id.secondIngredient);

                firstIngredient.setText(ingredientsList.get(i).getIngredient_name());
                i++;
                if (i != ingredientsList.size()) {
                    secondIngredient.setText(ingredientsList.get(i).getIngredient_name());
                } else {
                    secondIngredient.setVisibility(View.GONE);
                }

                ingredientsTableLayout.addView(ingredientsTableRow);

            }

            container.addView(ingredientsView);
            return ingredientsView;
        } else {
            View clickedStepsView = LayoutInflater.from(mcontext).inflate(R.layout.clicked_steps, container, false);

            //Instantiations
            TextView stepNumberTextView = (TextView) clickedStepsView.findViewById(R.id.stepNumberTextView);
            TextView stepsTextViewEnglish = (TextView) clickedStepsView.findViewById(R.id.stepsTextViewEnglish);
            TextView stepsTextViewIndo = (TextView) clickedStepsView.findViewById(R.id.stepsTextViewIndo);

            //Getting list from cookbookobject
            List<CookBookSteps> cookBookStepList = mCookBookObject.getCookBookStepList();

            //Setting textviews

            stepNumberTextView.setText(String.valueOf(position - 1));
            stepsTextViewEnglish.setText(cookBookStepList.get(position - 2).getContent_en());
            stepsTextViewIndo.setText(cookBookStepList.get(position - 2).getContent_id());

            container.addView(clickedStepsView);
            return clickedStepsView;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView) object);
    }
}
