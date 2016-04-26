package id.blackgarlic.blackgarlic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.Data;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by JustinKwik on 1/19/16.
 */
public class BlackGarlicAdapter extends RecyclerView.Adapter<BlackGarlicAdapter.MyViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    Context mContext;

    List<Data> mmenuList = new ArrayList<Data>();

    List<Integer> mmenuIdList;

    MyListItemClickListener mListener;

    List<Data> currentSelectedMenus = new ArrayList<Data>();

    List<Integer> currentSelectedMenuIds = new ArrayList<Integer>();

    List<String> currentSelectedMenusImageUrl = new ArrayList<String>();

    List<String> portionSizes = new ArrayList<String>();

    List<String> individualPrices = new ArrayList<String>();

    public void clearAllLists() {

        if (currentSelectedMenus != null) {
            currentSelectedMenus.clear();
            currentSelectedMenuIds.clear();
            currentSelectedMenusImageUrl.clear();
            portionSizes.clear();
            individualPrices.clear();
        }

    }

    public BlackGarlicAdapter(List<Data> menuList, List<Integer> menuIdList, Context context, MyListItemClickListener listener) {
        mmenuList = menuList;
        mContext = context;
        mListener = listener;
        mmenuIdList = menuIdList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_menu, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {

        SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        final Data currentMenu = mmenuList.get(position);

        final TextView quantityTextView = (TextView) myViewHolder.quantityTextView;
        quantityTextView.setText(String.valueOf(currentMenu.getQuantity()));
        final Animation popUp = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale);

        myViewHolder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.plusButton.startAnimation(popUp);
                currentMenu.setQuantity(currentMenu.getQuantity() + 1);
                quantityTextView.setText(String.valueOf(currentMenu.getQuantity()));
            }
        });

        myViewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.minusButton.startAnimation(popUp);

                if (currentMenu.getQuantity() != 1) {
                    currentMenu.setQuantity(currentMenu.getQuantity() - 1);
                    quantityTextView.setText(String.valueOf(currentMenu.getQuantity()));
                }

            }
        });

        CircleProgressBarDrawable progressBar = new CircleProgressBarDrawable();
        progressBar.setBackgroundColor(mContext.getResources().getColor(R.color.BGGREY));
        progressBar.setColor(mContext.getResources().getColor(R.color.BGGREEN));

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder.setFadeDuration(400).setProgressBarImage(progressBar).build();

        myViewHolder.menuNetworkImageView.setHierarchy(hierarchy);

        myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
        myViewHolder.menuDescriptionTextView.setText(currentMenu.getMenu_description());
        CalligraphyTypefaceSpan robotoMedium = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
        CalligraphyTypefaceSpan robotoThin = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Thin.ttf"));

        //Setting false if kids
        if (currentMenu.getMenu_type().equals("7")) {
            myViewHolder.fourPersonLinearLayout.setEnabled(false);
            myViewHolder.radioGroupMenu.setEnabled(false);
        }

        if ((currentMenu.getMenu_type().equals("3")) || (currentMenu.getMenu_type().equals("5"))) {

            SpannableStringBuilder twoPersonOriginalStringBuilder = new SpannableStringBuilder();
            twoPersonOriginalStringBuilder.append("2 Persons\n").append("IDR 50.000/Person");
            twoPersonOriginalStringBuilder.setSpan(robotoMedium, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            twoPersonOriginalStringBuilder.setSpan(robotoThin, 10, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.twoPersonTextView.setText(twoPersonOriginalStringBuilder, TextView.BufferType.SPANNABLE);

            SpannableStringBuilder fourPersonOriginalStringBuilder = new SpannableStringBuilder();
            fourPersonOriginalStringBuilder.append("4 Persons\n").append("IDR 37.500/Person");
            fourPersonOriginalStringBuilder.setSpan(robotoMedium, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fourPersonOriginalStringBuilder.setSpan(robotoThin, 10, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.fourPersonTextView.setText(fourPersonOriginalStringBuilder, TextView.BufferType.SPANNABLE);

        } else if ((currentMenu.getMenu_type().equals("5")) || (currentMenu.getMenu_type().equals("6"))){
            SpannableStringBuilder twoPersonBreakfastStringBuilder = new SpannableStringBuilder();
            twoPersonBreakfastStringBuilder.append("2 Persons\n").append("IDR 40.000/Person");
            twoPersonBreakfastStringBuilder.setSpan(robotoMedium, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            twoPersonBreakfastStringBuilder.setSpan(robotoThin, 10, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.twoPersonTextView.setText(twoPersonBreakfastStringBuilder, TextView.BufferType.SPANNABLE);

            SpannableStringBuilder fourPersonBreakfastStringBuilder = new SpannableStringBuilder();
            fourPersonBreakfastStringBuilder.append("4 Persons\n").append("IDR 35.000/Person");
            fourPersonBreakfastStringBuilder.setSpan(robotoMedium, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fourPersonBreakfastStringBuilder.setSpan(robotoThin, 10, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.fourPersonTextView.setText(fourPersonBreakfastStringBuilder, TextView.BufferType.SPANNABLE);
        } else if (currentMenu.getMenu_type().equals("7")) {
            SpannableStringBuilder twoPersonChildStringBuilder = new SpannableStringBuilder();
            twoPersonChildStringBuilder.append("2 Children\n").append("IDR 45.000/Child");
            twoPersonChildStringBuilder.setSpan(robotoMedium, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            twoPersonChildStringBuilder.setSpan(robotoThin, 11, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.twoPersonTextView.setText(twoPersonChildStringBuilder, TextView.BufferType.SPANNABLE);

            SpannableStringBuilder fourPersonChildStringBuilder = new SpannableStringBuilder();
            fourPersonChildStringBuilder.append("4 Children\n").append("Unavailable");
            fourPersonChildStringBuilder.setSpan(robotoMedium, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            fourPersonChildStringBuilder.setSpan(robotoThin, 10, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            myViewHolder.fourPersonTextView.setText(fourPersonChildStringBuilder, TextView.BufferType.SPANNABLE);

        }

        myViewHolder.switchToDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.viewFlipper.setFlipInterval(1000);
                CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, myViewHolder.viewFlipper.getChildAt(0).getHeight());
                myViewHolder.viewFlipper.getChildAt(1).setLayoutParams(params);
                AnimationFactory.flipTransition(myViewHolder.viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                currentMenu.setIsFlipped(true);
            }
        });

        myViewHolder.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.viewFlipper.setFlipInterval(1000);
                AnimationFactory.flipTransition(myViewHolder.viewFlipper, AnimationFactory.FlipDirection.RIGHT_LEFT);
                currentMenu.setIsFlipped(false);
            }
        });

        if (currentMenu.getIsFlipped() == true) {
            myViewHolder.viewFlipper.setFlipInterval(0);

            if (!(myViewHolder.viewFlipper.getDisplayedChild() == 1)) {
                myViewHolder.viewFlipper.setDisplayedChild(1);
            }

        } else {
            myViewHolder.viewFlipper.setFlipInterval(0);

            if (!(myViewHolder.viewFlipper.getDisplayedChild() == 0)) {
                myViewHolder.viewFlipper.setDisplayedChild(0);
            }

        }

        if ((currentMenu.getMenu_type().equals("4")) || (currentMenu.getMenu_type().equals("6"))) {
            myViewHolder.priceTextView.setText("IDR 80.000");
        } else if ((currentMenu.getMenu_type().equals("3")) || (currentMenu.getMenu_type().equals("5"))){
            myViewHolder.priceTextView.setText("IDR 100.000");
        } else if (currentMenu.getMenu_type().equals("7")) {
            myViewHolder.priceTextView.setText("IDR 90.000");
        }

        if ((MainActivity.getIsLoggedIn() == true) && (sharedPreferences.contains("currentMenuList"))) {
            Gson gson = new Gson();

            Type dataType = new TypeToken<List<Data>>(){}.getType();
            Type integerType = new TypeToken<List<Integer>>(){}.getType();
            Type stringType = new TypeToken<List<String>>(){}.getType();

            currentSelectedMenus = gson.fromJson(sharedPreferences.getString("currentMenuList", ""), dataType);
            currentSelectedMenuIds = gson.fromJson(sharedPreferences.getString("currentMenuIdList", ""), integerType);
            currentSelectedMenusImageUrl = gson.fromJson(sharedPreferences.getString("currentSelectedMenuListUrls", ""), stringType);
            portionSizes = gson.fromJson(sharedPreferences.getString("currentPortionSizes", ""), stringType);
            individualPrices = gson.fromJson(sharedPreferences.getString("currentIndividualPrices", ""), stringType);

        }

        myViewHolder.addToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.addToMenuButton.setEnabled(false);
                startAnimation(myViewHolder.addToMenuButton, position, myViewHolder.priceTextView, currentMenu);
            }
        });


        if (currentMenu.getFourPersonEnabled() == true) {
            myViewHolder.radioGroupMenu.check(R.id.radioButtonFourPerson);

            //Here is Breakfast and fourperson enabled, so just keep the same image so concatenate menu subname
            if ((currentMenu.getMenu_type().equals("4")) || (currentMenu.getMenu_type().equals("6"))) {
                if (  !(currentMenu.getMenu_subname().equals(""))  ) {
                    myViewHolder.menuTitleTextView.setText(myViewHolder.menuTitleTextView.getText() + " & " + currentMenu.getMenu_subname());
                }

                Uri uri = Uri.parse(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                myViewHolder.menuNetworkImageView.setImageURI(uri);
                myViewHolder.priceTextView.setText("IDR 140.000");

                //Here is original (3) and four person enabled, so change the image to + _4 so concatenate menu subname
            } else if ((currentMenu.getMenu_type().equals("3")) || (currentMenu.getMenu_type().equals("5"))){
                if (  !(currentMenu.getMenu_subname().equals(""))  ) {
                    myViewHolder.menuTitleTextView.setText(myViewHolder.menuTitleTextView.getText() + " & " + currentMenu.getMenu_subname());
                }
                Uri uri = Uri.parse(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position)) + "_4"));
                myViewHolder.menuNetworkImageView.setImageURI(uri);
                myViewHolder.priceTextView.setText("IDR 150.000");

            }

        } else {
            myViewHolder.radioGroupMenu.check(R.id.radioButtonTwoPerson);

            //Here is Breakfast and two person enabled, so just keep the same image and change to original menu title
            if ((currentMenu.getMenu_type().equals("4"))  || (currentMenu.getMenu_type().equals("6"))) {
                myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
                Uri uri = Uri.parse(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                myViewHolder.menuNetworkImageView.setImageURI(uri);
                myViewHolder.priceTextView.setText("IDR 80.000");

                //Here is Original and two enabled, so just keep the same image and change to original menu title
            } else if ((currentMenu.getMenu_type().equals("3"))  || (currentMenu.getMenu_type().equals("5"))){
                myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
                Uri uri = Uri.parse(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                myViewHolder.menuNetworkImageView.setImageURI(uri);
                myViewHolder.priceTextView.setText("IDR 100.000");
            } else if (currentMenu.getMenu_type().equals("7")) {
                myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
                Uri uri = Uri.parse(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                myViewHolder.menuNetworkImageView.setImageURI(uri);
                myViewHolder.priceTextView.setText("IDR 90.000");

            }
        }

    }

    @Override
    public long getHeaderId(int position) {

        if (position <= 2) {
            return 1;
        } else if ((position >= 3) && (position <= 9)){
            return 2;
        } else {
            return 3;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        TextView headerTextView = (TextView) holder.itemView.findViewById(R.id.headerTextView);

        int breakfastHeaderCount = 0;

        for (int i = 0; i < mmenuList.size(); i++) {
            if ((mmenuList.get(i).getMenu_type().equals("4")) || (mmenuList.get(i).getMenu_type().equals("6"))) {
                breakfastHeaderCount++;
            }
        }

        if (position <= breakfastHeaderCount - 1) {
            headerTextView.setText("BREAKFAST");
        } else if ((position >= breakfastHeaderCount) && (position <= mmenuList.size() - 3)){
            headerTextView.setText("ORIGINAL");
        } else {
            headerTextView.setText("KIDS");
        }
    }

    @Override
    public int getItemCount() {
        return mmenuList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView menuTitleTextView;
        public SimpleDraweeView menuNetworkImageView;
        public ImageView selectedImageView;
        public TextView switchToDescription;
        public ViewFlipper viewFlipper;
        public TextView menuDescriptionTextView;
        public TextView backToMenu;
        public Button addToMenuButton;

        public LinearLayout twoPersonLinearLayout;
        public LinearLayout fourPersonLinearLayout;

        public RadioGroup radioGroupMenu;
        public RadioButton twoPersonRadioButton;
        public RadioButton fourPersonRadioButton;

        public TextView priceTextView;

        public TextView twoPersonTextView;
        public TextView fourPersonTextView;

        public Button minusButton;
        public Button plusButton;
        public TextView quantityTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            menuTitleTextView = (TextView) itemView.findViewById(R.id.menuTitleTextView);
            menuNetworkImageView = (SimpleDraweeView) itemView.findViewById(R.id.menuNetworkImageView);
            switchToDescription = (TextView) itemView.findViewById(R.id.switchToDescription);
            viewFlipper = (ViewFlipper) itemView.findViewById(R.id.viewFlipper);
            menuDescriptionTextView = (TextView) itemView.findViewById(R.id.menuDescriptionTextView);
            backToMenu = (TextView) itemView.findViewById(R.id.backToMenu);
            addToMenuButton = (Button) itemView.findViewById(R.id.addToMenuButton);

            twoPersonLinearLayout = (LinearLayout) itemView.findViewById(R.id.twoPersonLinearLayout);
            fourPersonLinearLayout = (LinearLayout) itemView.findViewById(R.id.fourPersonLinearLayout);

            radioGroupMenu = (RadioGroup) itemView.findViewById(R.id.radioGroupMenu);
            twoPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonTwoPerson);
            fourPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonFourPerson);
            radioGroupMenu.setOnClickListener(this);

            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);

            twoPersonTextView = (TextView) itemView.findViewById(R.id.twopersonTextView);
            fourPersonTextView = (TextView) itemView.findViewById(R.id.fourpersonTextView);

            minusButton = (Button) itemView.findViewById(R.id.minusButton);
            plusButton = (Button) itemView.findViewById(R.id.plusButton);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantityTextView);

        }

        @Override
        public void onClick(View v) {
            if (mmenuList.get(getPosition()).getFourPersonEnabled() == false) {
                mmenuList.get(getPosition()).setFourPersonEnabled(true);
                notifyDataSetChanged();
            } else {
                mmenuList.get(getPosition()).setFourPersonEnabled(false);
                notifyDataSetChanged();
            }

        }
    }

    public static interface MyListItemClickListener{
        public void OnItemClick(List<Data> menuList, List<Integer> menuIdList, String menuAdded, List<String> currentSelectedMenusUrl, List<String> portionSizes, List<String> individualPrices, Data currentMenu);
    }

    public void startAnimation(final Button button, final int position, final TextView priceTextView, final Data currentMenu) {
        final float[] from = new float[3],
                to =   new float[3];

        Color.colorToHSV(Color.parseColor("#03c9a9"), from);   // from light green
        Color.colorToHSV(Color.parseColor("#00b200"), to);     // to dark green

        final ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
        anim.setDuration(1000);                              // for 1000 ms

        final float[] hsv  = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                button.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });

        anim.start();

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                button.setBackgroundResource(R.drawable.addtobox);
                button.setEnabled(true);

                for (int i = 0; i < currentMenu.getQuantity(); i++) {
                    currentSelectedMenus.add(mmenuList.get(position));
                    currentSelectedMenuIds.add(mmenuIdList.get(position));
                }

                if (((mmenuList.get(position).getMenu_type().equals("3")) || (mmenuList.get(position).getMenu_type().equals("5"))) && (mmenuList.get(position).getFourPersonEnabled() == true)) {

                    for (int i = 0; i < currentMenu.getQuantity(); i++) {
                        currentSelectedMenusImageUrl.add(mmenuList.get(position).getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position)) + "_4"));
                    }

                } else {

                    for (int i = 0; i < currentMenu.getQuantity(); i++) {
                        currentSelectedMenusImageUrl.add(mmenuList.get(position).getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                    }

                }

                if (mmenuList.get(position).getFourPersonEnabled() == true) {

                    for (int i = 0; i < currentMenu.getQuantity(); i++) {
                        portionSizes.add("4P");
                    }

                } else {

                    for (int i = 0; i < currentMenu.getQuantity(); i++) {
                        portionSizes.add("2P");
                    }

                }

                for (int i = 0; i < currentMenu.getQuantity(); i++) {
                    individualPrices.add(String.valueOf(priceTextView.getText()));
                }


                mListener.OnItemClick(currentSelectedMenus, currentSelectedMenuIds, currentMenu.getMenu_name(), currentSelectedMenusImageUrl, portionSizes, individualPrices, currentMenu);


            }
        });

    }

}
