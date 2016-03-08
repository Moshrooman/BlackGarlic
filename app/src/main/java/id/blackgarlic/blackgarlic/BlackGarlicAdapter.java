package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

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

    int selectedInteger;

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    Context mContext;

    List<Data> mmenuList = new ArrayList<Data>();

    List<Integer> mmenuIdList;

    MyListItemClickListener mListener;

    List<Data> currentSelectedMenus = new ArrayList<Data>();

    List<Integer> currentSelectedMenuIds = new ArrayList<Integer>();


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
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Data currentMenu = mmenuList.get(position);
        myViewHolder.menuTitleTextView.setText(currentMenu.getMenu_name());
        myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuUrl().replace("menu_id", String.valueOf(mmenuIdList.get(position))),
                ConnectionManager.getImageLoader(mContext));
        myViewHolder.menuDescriptionTextView.setText(currentMenu.getMenu_description());


        if (currentMenu.getIsSelected() == true) {
            myViewHolder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.selectedImageView.setVisibility(View.GONE);
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

        public MyViewHolder(View itemView) {
            super(itemView);
            menuTitleTextView = (TextView) itemView.findViewById(R.id.menuTitleTextView);
            menuNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.menuNetworkImageView);
            selectedImageView = (ImageView) itemView.findViewById(R.id.selectedImageView);
            itemView.setOnClickListener(this);
            switchToDescription = (TextView) itemView.findViewById(R.id.switchToDescription);
            viewFlipper = (ViewFlipper) itemView.findViewById(R.id.viewFlipper);
            menuDescriptionTextView = (TextView) itemView.findViewById(R.id.menuDescriptionTextView);
            backToMenu = (TextView) itemView.findViewById(R.id.backToMenu);

            switchToDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                }
            });

            backToMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationFactory.flipTransition(viewFlipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                }
            });


        }

        @Override
        public void onClick(View v) {
            if (mmenuList.get(getPosition()).getIsSelected() == false) {
                mmenuList.get(getPosition()).setIsSelected(true);
                selectedInteger++;
                notifyDataSetChanged();
            }  else if (mmenuList.get(getPosition()).getIsSelected() == true){
                    mmenuList.get(getPosition()).setIsSelected(false);
                    selectedInteger--;
                    notifyDataSetChanged();
            }

            currentSelectedMenus.clear();

            currentSelectedMenuIds.clear();

            for (int i = 0; i < mmenuList.size(); i++) {
                if (mmenuList.get(i).getIsSelected() == true) {
                    currentSelectedMenus.add(mmenuList.get(i));
                    currentSelectedMenuIds.add(Integer.valueOf(mmenuIdList.get(i)));
                }
            }

            mListener.OnItemClick(mmenuList.get(getPosition()), v, currentSelectedMenus, currentSelectedMenuIds);

        }
    }

    public static interface MyListItemClickListener{
        public void OnItemClick(Data onItemClicked, View view, List<Data> menuList, List<Integer> menuIdList);
    }


}
