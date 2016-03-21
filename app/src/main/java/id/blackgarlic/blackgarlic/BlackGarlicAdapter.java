package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.andexert.library.RippleView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.Data;

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
        final Data currentMenu = mmenuList.get(position);
        myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
        myViewHolder.menuDescriptionTextView.setText(currentMenu.getMenu_description());

        if (currentMenu.getMenu_type().equals("4")) {
            myViewHolder.priceTextView.setText("IDR 80.000");
        } else {
            myViewHolder.priceTextView.setText("IDR 100.000");
        }

        myViewHolder.rippleViewButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                currentSelectedMenus.add(mmenuList.get(position));
                currentSelectedMenuIds.add(mmenuIdList.get(position));

                if ((mmenuList.get(position).getMenu_type().equals("3"))&& (mmenuList.get(position).getFourPersonEnabled() == true)) {
                    currentSelectedMenusImageUrl.add(mmenuList.get(position).getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position)) + "_4"));
                } else {
                    currentSelectedMenusImageUrl.add(mmenuList.get(position).getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))));
                }

                if (mmenuList.get(position).getFourPersonEnabled() == true) {
                    portionSizes.add("4P");
                } else {
                    portionSizes.add("2P");
                }

                individualPrices.add(String.valueOf(myViewHolder.priceTextView.getText()));

                mListener.OnItemClick(myViewHolder.rippleViewButton, currentSelectedMenus, currentSelectedMenuIds, currentMenu.getMenu_name(), currentSelectedMenusImageUrl, portionSizes, individualPrices);
            }
        });

        if (currentMenu.getFourPersonEnabled() == true) {
            myViewHolder.radioGroupMenu.check(R.id.radioButtonFourPerson);

            //Here is Breakfast and fourperson enabled, so just keep the same image so concatenate menu subname
            if (currentMenu.getMenu_type().equals("4")) {
                if (  !(currentMenu.getMenu_subname().equals(""))  ) {
                    myViewHolder.menuTitleTextView.setText(myViewHolder.menuTitleTextView.getText() + " & " + currentMenu.getMenu_subname());
                }
                myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))),
                        ConnectionManager.getImageLoader(mContext));
                myViewHolder.priceTextView.setText("IDR 140.000");

                //Here is original (3) and four person enabled, so change the image to + _4 so concatenate menu subname
            } else {
                if (  !(currentMenu.getMenu_subname().equals(""))  ) {
                    myViewHolder.menuTitleTextView.setText(myViewHolder.menuTitleTextView.getText() + " & " + currentMenu.getMenu_subname());
                }
                myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position)) + "_4"),
                        ConnectionManager.getImageLoader(mContext));
                myViewHolder.priceTextView.setText("IDR 150.000");

            }
        } else {
            myViewHolder.radioGroupMenu.check(R.id.radioButtonTwoPerson);

            //Here is Breakfast and two person enabled, so just keep the same image and change to original menu title
            if (currentMenu.getMenu_type().equals("4")) {
                myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
                myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))),
                        ConnectionManager.getImageLoader(mContext));
                myViewHolder.priceTextView.setText("IDR 80.000");

                //Here is Original and two enabled, so just keep the same image and change to original menu title
            } else {
                myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
                myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))),
                        ConnectionManager.getImageLoader(mContext));
                myViewHolder.priceTextView.setText("IDR 100.000");
            }
        }

    }

    @Override
    public long getHeaderId(int position) {

        if (position <= 2) {
            return 1;
        } else {
            return 2;
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

        if (position <= 2) {
            headerTextView.setText("Breakfast");
        } else {
            headerTextView.setText("Original");
        }
    }

    @Override
    public int getItemCount() {
        return mmenuList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView menuTitleTextView;
        public NetworkImageView menuNetworkImageView;
        public ImageView selectedImageView;
        public TextView switchToDescription;
        public ViewFlipper viewFlipper;
        public TextView menuDescriptionTextView;
        public TextView backToMenu;
        public RippleView rippleViewButton;

        public LinearLayout twoPersonLinearLayout;
        public LinearLayout fourPersonLinearLayout;

        public RadioGroup radioGroupMenu;
        public RadioButton twoPersonRadioButton;
        public RadioButton fourPersonRadioButton;

        public TextView priceTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            menuTitleTextView = (TextView) itemView.findViewById(R.id.menuTitleTextView);
            menuNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.menuNetworkImageView);
            switchToDescription = (TextView) itemView.findViewById(R.id.switchToDescription);
            viewFlipper = (ViewFlipper) itemView.findViewById(R.id.viewFlipper);
            menuDescriptionTextView = (TextView) itemView.findViewById(R.id.menuDescriptionTextView);
            backToMenu = (TextView) itemView.findViewById(R.id.backToMenu);
            rippleViewButton = (RippleView) itemView.findViewById(R.id.rippleView);

            twoPersonLinearLayout = (LinearLayout) itemView.findViewById(R.id.twoPersonLinearLayout);
            fourPersonLinearLayout = (LinearLayout) itemView.findViewById(R.id.fourPersonLinearLayout);

            radioGroupMenu = (RadioGroup) itemView.findViewById(R.id.radioGroupMenu);
            twoPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonTwoPerson);
            fourPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonFourPerson);
            radioGroupMenu.setOnClickListener(this);

            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);

            switchToDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                }
            });

            backToMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.RIGHT_LEFT);
                }
            });

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
        public void OnItemClick(RippleView rippleView, List<Data> menuList, List<Integer> menuIdList, String menuAdded, List<String> currentSelectedMenusUrl, List<String> portionSizes, List<String> individualPrices);
    }

}
