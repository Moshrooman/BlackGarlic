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
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.andexert.library.RippleView;
import com.android.volley.toolbox.NetworkImageView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.w3c.dom.Text;

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

    RadioButtonClickListener mRadioListener;

    public BlackGarlicAdapter(List<Data> menuList, List<Integer> menuIdList, Context context, MyListItemClickListener listener,
                              RadioButtonClickListener radioListener) {
        mmenuList = menuList;
        mContext = context;
        mListener = listener;
        mmenuIdList = menuIdList;
        mRadioListener = radioListener;
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
        myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))),
                ConnectionManager.getImageLoader(mContext));
        myViewHolder.menuDescriptionTextView.setText(currentMenu.getMenu_description());

        myViewHolder.rippleViewButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                currentSelectedMenus.add(mmenuList.get(position));
                currentSelectedMenuIds.add(mmenuIdList.get(position));

                mListener.OnItemClick(myViewHolder.rippleViewButton, currentSelectedMenus, currentSelectedMenuIds, currentMenu.getMenu_name());
            }
        });

        myViewHolder.twoPersonRadioButton.setChecked(true);

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

            twoPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonTwoPerson);
            fourPersonRadioButton = (RadioButton) itemView.findViewById(R.id.radioButtonFourPerson);

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

            twoPersonLinearLayout.setOnClickListener(this);
            fourPersonLinearLayout.setOnClickListener(this);
            twoPersonRadioButton.setOnClickListener(this);
            fourPersonRadioButton.setOnClickListener(this);
            priceTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mRadioListener.OnRadioButtonClick(itemView, mmenuList.get(getPosition()));
        }

    }

    public static interface MyListItemClickListener{
        public void OnItemClick(RippleView rippleView, List<Data> menuList, List<Integer> menuIdList, String menuAdded);
    }

    public static interface RadioButtonClickListener {
        public void OnRadioButtonClick(View view, Data currentMenu);
    }

}
