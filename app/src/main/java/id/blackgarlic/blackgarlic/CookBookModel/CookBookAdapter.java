package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.Voucher.VoucherObject;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by Justin Kwik on 10/06/2016.
 */
public class CookBookAdapter extends RecyclerView.Adapter<CookBookAdapter.MyCookBookViewHolder> {

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";

    //So again, the idea here is so that we have 2 different sets of lists, 2 lists for if they search, 2 lists for if they don't.

    List<CookBookObject> cookBookList = new ArrayList<CookBookObject>();
    List<CookBookObject> cookBookListSearch = new ArrayList<CookBookObject>();
    Context mContext;
    List<CookBookObject> fullCookBookList = new ArrayList<CookBookObject>();
    List<CookBookObject> fullCookBookListSearch = new ArrayList<CookBookObject>();
    boolean lastOne = false;
    int amountRemovedOnLastOne = 0;
    boolean searchBoolean = false;
    String searchedString = "";

    public void setSearchedString(String newSearchedString) {
        this.searchedString = newSearchedString;
    }

    public void setSearchBoolean(boolean newSearchBoolean) {
        this.searchBoolean = newSearchBoolean;
    }

    public void addMenus() {

        //SKIP THIS COME BACK WHEN TOLD
        //So we first check if searchboolean is false, if it is then we automatically execute myasynctask.

        if (searchBoolean == false) {
            new MyAsyncTask().execute();

            //Else if searchboolean is true AND cookbooklistsearch.size is greater than or equal to 20, then we go through with the asynctask.
            //Remember in the getitemviewtype, we only returned to load more, when searchboolean is true, when cookbooklistsearch.size
            //is greater than or equal to 20, meaning there might be more to add.

            //GO DOWN TO ASYNC TASK
        } else if ((searchBoolean == true) && (cookBookListSearch.size() >= 20)) {
            Log.e("Loading More: ", "True");
            new MyAsyncTask().execute();
        }

    }

    public CookBookAdapter(List<CookBookObject> cookBookObjectList, List<CookBookObject> cookBookObjectListParameterSearch, List<CookBookObject> fullCookBookListParameter, List<CookBookObject> fullCookBookListSearchParameter, Context context) {


        //Have to loop through parameters and manually add them into list, because if i set them equal to the parameters then it will reference
        //the same object so if i change value here, it'll change value in cookbook activity because parameter came from there.

        if ((cookBookObjectList != null) && (cookBookObjectList.size() != 0)) {
            for (int i = 0; i < cookBookObjectList.size(); i++) {
                this.cookBookList.add(cookBookObjectList.get(i));
            }
        }

        if ((cookBookObjectListParameterSearch != null) && (cookBookObjectListParameterSearch.size() != 0)) {
            for (int i = 0; i < cookBookObjectListParameterSearch.size(); i++) {
                this.cookBookListSearch.add(cookBookObjectListParameterSearch.get(i));
            }
        }

        if ((fullCookBookListParameter != null) && (fullCookBookListParameter.size() != 0)) {
            for (int i = 0; i < fullCookBookListParameter.size(); i++) {
                this.fullCookBookList.add(fullCookBookListParameter.get(i));
            }
        }

        if ((fullCookBookListSearchParameter != null) && (fullCookBookListSearchParameter.size() != 0)) {
            for (int i = 0; i < fullCookBookListSearchParameter.size(); i++) {
                this.fullCookBookListSearch.add(fullCookBookListSearchParameter.get(i));
            }
        }

        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {

        if (searchBoolean == false) {

            //If search boolean is false the first thing is we have to check if we are at the very last object, if it is we return 2
            //which returns an empty view
            if (position == fullCookBookList.size()) {
                Log.e("Return: ", "2");
                return 2;

                //Then we check if we are at the last one in the visible list, if we are we return 1, which returns a loading view
            } else if ((position == cookBookList.size())) {
                Log.e("Return: ", "1");
                return 1;

                //Then if we are not at the last of the visible list we have to return 0, which returns an actual row
            } else if (position != cookBookList.size()) {
                Log.e("Return: ", "0");
                return 0;

                //And in the else we return 3 which also returns an empty row.
            } else {
                Log.e("Return: ", "3");
                return 3;
            }

        } else {

            //So if search boolean is false, the first thing we have to check is if we are at the last visible position, and fullcookBooklistsearch size is 0,
            //because this would mean that we are at the last one and there is no more in the fullCookBookList that we have to add, so we return
            //2 which returns an empty row.
            //We can do this because we take care of the deleting of the fullCookBookList in our asyncTask.
            if ((position == cookBookListSearch.size()) && (fullCookBookListSearch.size() == 0)) {
                return 2;

                //Then in order to return a loading row, we have to be at the last visible item, AND fullcookbooklistsearch size is not 0
                //suggesting that there is more to add even after deleting... AND cookBookListSearch size is greater than or equal to 20
                //because if it isn't, then it wouldn't have to delete anything from fullcookbooklist because in the addmenus method
                //it only goes through with the async task if cookbooklistsearch.size is equal to or greater than 20.
            } else if ((position == cookBookListSearch.size()) && (fullCookBookListSearch.size() != 0) && (cookBookListSearch.size() >= 20)) {
                return 1;

                //Then finally if we are not at the last visible position we return an actual menu view.
            } else if (position != cookBookListSearch.size()) {
                return 0;
            } else {

                //And everything else, we return 3 which also returns an empty view.
                return 3;
            }

        }


    }

    @Override
    public MyCookBookViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //All this stuff ties in to the method above, that returns the view type, it just returns a corresponding view for different viewtypes.

        if ((viewType == 3 || viewType == 2)) {
            View emptyView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emptyrowcookbook, viewGroup, false);
            return new MyCookBookViewHolder(emptyView);
        } else if ((viewType == 1)) {
            //Inflate loading shit
            View progressView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cookbookloadingrow, viewGroup, false);
            return new MyCookBookViewHolder(progressView);
        } else {
            View menuView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cookbookrow, viewGroup, false);
            return new MyCookBookViewHolder(menuView);
        }

    }

    @Override
    public void onBindViewHolder(MyCookBookViewHolder myViewHolder, int position) {

        //So the idea here is that we want to explicitly split up the code so that it does different things if search boolean is false
        //or if its true

        //So we've seen the code below already, which is if searchboolean is false.

        if (searchBoolean == false) {
            if (position != cookBookList.size()) {

                Uri uri = Uri.parse(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(cookBookList.get(position).getMenu_id())));
                myViewHolder.cookBookImage.setImageURI(uri);

                int menuTitleLength = cookBookList.get(position).getMenu_name().length();
                int menuSubNameLength = cookBookList.get(position).getMenu_subname().length();

                String menuTitle = cookBookList.get(position).getMenu_name();
                String menuSubname = cookBookList.get(position).getMenu_subname();

                CalligraphyTypefaceSpan robotoMedium = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
                CalligraphyTypefaceSpan robotoThin = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Thin.ttf"));

                SpannableStringBuilder menuTitleStringBuilder = new SpannableStringBuilder();

                if (menuSubNameLength != 0) {
                    menuTitleStringBuilder.append(menuTitle).append("\n" + menuSubname);
                    menuTitleStringBuilder.setSpan(robotoMedium, 0, menuTitleLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    menuTitleStringBuilder.setSpan(robotoThin, menuTitleLength + 1, menuTitleLength + menuSubNameLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    menuTitleStringBuilder.append(menuTitle);
                    menuTitleStringBuilder.setSpan(robotoMedium, 0, menuTitleLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                myViewHolder.cookBookTextView.setText(menuTitleStringBuilder, TextView.BufferType.SPANNABLE);

            } else {

                if (lastOne == false) {

                    addMenus();

                }

            }

            //Here is the else if searchboolean is true.
            //Basically what we did here is that we copy and pasted the same code except we changed everything that if there is cookBookList,
            //we changed it to cookBookListSearch, if there is fullcookbooklist, we changed it to fullcookbooklistsearch.
        } else {
            if (position != cookBookListSearch.size()) {

                Uri uri = Uri.parse(BLACKGARLIC_PICTURES.replace("menu_id", String.valueOf(cookBookListSearch.get(position).getMenu_id())));
                myViewHolder.cookBookImage.setImageURI(uri);

                int menuTitleLength = cookBookListSearch.get(position).getMenu_name().length();
                int menuSubNameLength = cookBookListSearch.get(position).getMenu_subname().length();

                String menuTitle = cookBookListSearch.get(position).getMenu_name();
                String menuSubname = cookBookListSearch.get(position).getMenu_subname();

                CalligraphyTypefaceSpan robotoMedium = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Medium.ttf"));
                CalligraphyTypefaceSpan robotoThin = new CalligraphyTypefaceSpan(TypefaceUtils.load(mContext.getAssets(), "fonts/Roboto-Thin.ttf"));

                SpannableStringBuilder menuTitleStringBuilder = new SpannableStringBuilder();

                if (menuSubNameLength != 0) {
                    menuTitleStringBuilder.append(menuTitle).append("\n" + menuSubname);
                    menuTitleStringBuilder.setSpan(robotoMedium, 0, menuTitleLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    menuTitleStringBuilder.setSpan(robotoThin, menuTitleLength + 1, menuTitleLength + menuSubNameLength + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    menuTitleStringBuilder.append(menuTitle);
                    menuTitleStringBuilder.setSpan(robotoMedium, 0, menuTitleLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                myViewHolder.cookBookTextView.setText(menuTitleStringBuilder, TextView.BufferType.SPANNABLE);

            } else {

                //Then of course we check if there is last one, if the last one wasn't loaded, then we call add menus, GO DOWN NOW TO GETITEMCOUNT OVERRIDE METHOD.

                if (lastOne == false) {

                    addMenus();

                }
            }
        }



    }

    @Override
    public int getItemCount() {

        //So in the getitemcount method we also split it up depending on the search boolean, if the search boolean is false, then we return
        //the cookbooklist.size + 1, remember its + 1 because we have to give space for the loading view, then if the search boolean is
        //true, then we return the cookbooklistsearch size + 1. GO UP NOW TO ADD MENUS METHOD.
        if (searchBoolean == false) {
            return cookBookList.size() + 1;
        } else {
            return cookBookListSearch.size() + 1;
        }


    }

    public class MyCookBookViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView cookBookImage;
        public TextView cookBookTextView;

        public MyCookBookViewHolder(View itemView) {
            super(itemView);

            cookBookImage = (SimpleDraweeView) itemView.findViewById(R.id.cookBookImage);
            cookBookTextView = (TextView) itemView.findViewById(R.id.cookBookTextView);
        }
    }


    private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {

            if (searchBoolean == false) {
                Log.e("Size: ", String.valueOf(cookBookList.size()));
            } else {
                Log.e("Size: ", String.valueOf(cookBookListSearch.size()));
            }


            Runnable runnable2Secs = new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            };

            Handler handler = new Handler();

            handler.postDelayed(runnable2Secs, (int) (Math.random() * 1500));

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Again, everything that we did before we added it into the parameter if searchBoolean is false.

            if (searchBoolean == false) {
                int startingPositionForAddingIntoAdapterList = CookBook.getStartingPositionForAddingIntoAdapterList();

                if (startingPositionForAddingIntoAdapterList + 20 > fullCookBookList.size()) {

                    Log.e("Before Starting: ", String.valueOf(startingPositionForAddingIntoAdapterList));

                    for (int i = startingPositionForAddingIntoAdapterList; i < fullCookBookList.size(); i++) {
                        cookBookList.add(fullCookBookList.get(i));
                        amountRemovedOnLastOne++;
                    }

                    CookBook.setStartingPositionForAddingIntoAdapterList(CookBook.getStartingPositionForAddingIntoAdapterList() + (fullCookBookList.size() - startingPositionForAddingIntoAdapterList));

                    Log.e("After Starting: ", String.valueOf(CookBook.getStartingPositionForAddingIntoAdapterList()));

                    Log.e("Last One: ", String.valueOf(amountRemovedOnLastOne));

                    lastOne = true;

                    return null;

                } else {

                    Log.e("Before Starting: ", String.valueOf(startingPositionForAddingIntoAdapterList));

                    for (int i = startingPositionForAddingIntoAdapterList; i < startingPositionForAddingIntoAdapterList + 20; i++) {
                        cookBookList.add(fullCookBookList.get(i));
                    }

                    CookBook.setStartingPositionForAddingIntoAdapterList(CookBook.getStartingPositionForAddingIntoAdapterList() + 20);

                    Log.e("After Starting: ", String.valueOf(CookBook.getStartingPositionForAddingIntoAdapterList()));

                    lastOne = false;

                    return null;
                }

                //Then here, we check if searchboolean is true AND cookbooklistsearch.size is greater than or equal to 20, meaning
                //there might be some to add.
            } else if ((searchBoolean == true) && (cookBookListSearch.size() >= 20)){

                //STARTING FROM HERE --------------------------- GO DOWN TO WHERE IT SAYS ENDING HERE

                //Loop to delete everything that is already in cookbooklist from fullcookbooklist to avoid duplicates
                for (int i = 0; i < cookBookListSearch.size(); i++) {

                    String menuname = cookBookListSearch.get(i).getMenu_name();

                    for (int j = 0; j < fullCookBookListSearch.size(); j++) {
                        if (fullCookBookListSearch.get(j).getMenu_name().equals(menuname)) {
                            fullCookBookListSearch.remove(j);
                            j--;
                        }
                    }
                }


                //Loop to delete anything that doesn't contain the searched string.
                for (int i = 0; i < fullCookBookListSearch.size(); i++) {
                    if (!(fullCookBookListSearch.get(i).getMenu_name().contains(searchedString))) {
                        fullCookBookListSearch.remove(i);
                        i--;
                    }
                }

                //ENDING HERE IS WHERE IF I WERE TO SET EQUAL AND MAKE THE LIST THE SAME ENTITY BY USING = IN CONSTRUCTOR IT WOULD DELETE FROM MAINACTIVITY ALSO.

                //So now we are left with fullcookbooklist that doesn't have any duplicates from cookbooklist, and doesn't have anything
                //that doesn't contain the searched string.


                //So now if the fullcookbooklist size is still above 20, then we add 20, and set lastone to false, OTHERWISE if it is
                //less than 20, our for loops have to be different because we just add the remaining, not the first 20.
                if (fullCookBookListSearch.size() >= 20) {

                    //Keep in mind that after adding from fullcookbooklistsearch to cookbooklistsearch we need to delete from fullcookbooklistsearch
                    //This is so that next time fullCookBookListSearch won't have the items that was just added
                    //To do that we just count until we've added 20 by creating int variable and check if its 20 then we break.
                    //Also, in the for loop we always add at position 0 because once we add at position 0, then delete it, the next item
                    //will be moved to the position at 0.
                    int addedOrDeletedCount = 0;

                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        cookBookListSearch.add(fullCookBookListSearch.get(0));
                        fullCookBookListSearch.remove(0);
                        addedOrDeletedCount++;

                        if (addedOrDeletedCount == 20) {
                            break;
                        }
                    }

                    lastOne = false;
                } else {

                    //Here is a bit different, this will be called when the fullcookbooklist.size is less than 20, which again, means that
                    //there are less than 20 items left to add, so as a result we add whats left, not the first 20. So for this one,
                    //we again loop through Integer.Max_Value, and first check if fullcookBookListSearch.size is 0, and if it isn't, then
                    //it means that there are still items remaining, so if it is then we add from it, and delete it, like above.

                    //Otherwise if the fullcookbooklistsearch.size is 0, then we break out of the for loop, which means that we have added
                    //the last of what was left in the fullcookbooklistsearch that there was to add.

                    //GO NOW TO COOKBOOK CLASS.
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {

                        if (fullCookBookListSearch.size() > 0) {
                            cookBookListSearch.add(fullCookBookListSearch.get(0));
                            fullCookBookListSearch.remove(0);
                        } else {
                            break;
                        }

                    }

                    lastOne = true;

                }

                return null;

            } else {
                return null;
            }


        }
    }

}
