package id.blackgarlic.blackgarlic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.model.Menu;

/**
 * Created by JustinKwik on 1/19/16.
 */
public class BlackGarlicAdapter extends RecyclerView.Adapter<BlackGarlicAdapter.MyViewHolder> {

    int selectedInteger;

    public final String BLACKGARLIC_PICTURES = "http://blackgarlic.id/inc/images/menu/menu_id.jpg";

    Context mContext;

    Menu[] mmenuList = new Menu[9];

    MyListItemClickListener mListener;

    List<Menu> currentSelectedMenus = new ArrayList<Menu>();


    public BlackGarlicAdapter(Menu[] menuList, Context context, MyListItemClickListener listener) {
        mmenuList = menuList;
        mContext = context;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_menu, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        Menu currentMenu = mmenuList[position];
        myViewHolder.menuTitleTextView.setText(currentMenu.getMenuName());
        myViewHolder.menuNetworkImageView.setImageUrl(currentMenu.getMenuImageUrl(),
                ConnectionManager.getImageLoader(mContext));


        if (currentMenu.getIsSelected() == true) {
            myViewHolder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.selectedImageView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mmenuList.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView menuTitleTextView;
        public NetworkImageView menuNetworkImageView;
        public ImageView selectedImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            menuTitleTextView = (TextView) itemView.findViewById(R.id.menuTitleTextView);
            menuNetworkImageView = (NetworkImageView) itemView.findViewById(R.id.menuNetworkImageView);
            selectedImageView = (ImageView) itemView.findViewById(R.id.selectedImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if ((mmenuList[getPosition()].getIsSelected() == false) && (selectedInteger != 5)) {
                mmenuList[getPosition()].setIsSelected(true);
                selectedInteger++;
                notifyDataSetChanged();
                //This is called if the chosen menus are still under 5 selected
            } else if ((selectedInteger == 5) && (mmenuList[getPosition()].getIsSelected() == false)) {
                mmenuList[getPosition()].setIsSelected(false);
                notifyDataSetChanged();
                //This if statement is called if user has already chosen 5 menus and they select a NEW menu
            } else {
                    mmenuList[getPosition()].setIsSelected(false);
                    selectedInteger--;
                    notifyDataSetChanged();
                //This if statement is called if the user has already chosen 5 menus and they select an already selected Menu
                //OR Even if they haven't chosen 5 menus, and selects and already selected menu
            }

            currentSelectedMenus.clear();

            for (int i = 0; i < mmenuList.length; i++) {
                if (mmenuList[i].getIsSelected() == true) {
                    currentSelectedMenus.add(mmenuList[i]);
                }
            }

            Log.e("Selected Menus : ", String.valueOf(currentSelectedMenus.size()));

            mListener.OnItemClick(mmenuList[getPosition()], v, currentSelectedMenus);

        }
    }

    public static interface MyListItemClickListener{
        public void OnItemClick(Menu onItemClicked, View view, List<Menu> menuList);
    }

    public void clearCurrentSelectedMenus() {
        this.currentSelectedMenus.clear();
    }

    public void clearmmenuList() {
        for (int i = 0; i < mmenuList.length; i++) {
            if (this.mmenuList[i].getIsSelected() == true) {
                this.mmenuList[i].setIsSelected(false);
            }
        }
    }

    public void setAdapterSelectedInteger(int input) {
       this.selectedInteger = input;
    }



}
