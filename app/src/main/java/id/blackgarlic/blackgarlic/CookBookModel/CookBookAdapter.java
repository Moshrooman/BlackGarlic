package id.blackgarlic.blackgarlic.CookBookModel;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import id.blackgarlic.blackgarlic.R;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by Justin Kwik on 10/06/2016.
 */
public class CookBookAdapter extends RecyclerView.Adapter<CookBookAdapter.MyCookBookViewHolder> {

    public final String BLACKGARLIC_PICTURES = "http://bgmenu.kilatstorage.com/menu_id.jpg";

    List<CookBookObject> cookBookList = new ArrayList<CookBookObject>();
    Context mContext;

    public CookBookAdapter(List<CookBookObject> cookBookObjectList, Context context) {
        this.cookBookList = cookBookObjectList;
        this.mContext = context;
    }

    @Override
    public MyCookBookViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cookbookrow, viewGroup, false);
        return new MyCookBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCookBookViewHolder myViewHolder, int position) {
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

    }

    @Override
    public int getItemCount() {
        return cookBookList.size();
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
}
