package id.blackgarlic.blackgarlic;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import id.blackgarlic.blackgarlic.model.Data;
import id.blackgarlic.blackgarlic.model.UserCredentials;

public class CheckOut extends AppCompatActivity {

    private static List<Boolean> dateBooleanList = new ArrayList<Boolean>();
    private static List<Button> dateButtonList = new ArrayList<Button>();
    private static List<LocalDate> localDateList = new ArrayList<LocalDate>();

    private static int incrementingInteger = 0;

    private static String selectedDate = "";

    private static List<Data> selectedMenuList;
    private static List<Integer> selectedMenuIdList;
    private static List<String> selectedMenuListUrls;
    private static List<String> selectedPortionSizes;
    private static int subTotalCost;
    private static List<String> selectedIndividualPrices;

    private static ListView orderSummaryListView;

    private static UserCredentials userCredentials;

    private static Boolean gojekButtonBoolean;

    private static Boolean etobeeButtonBoolean;

    private static String deliveryFee;

    private static String deliveryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        deliveryFee = "";
        deliveryTime = "";

        userCredentials = LogInScreen.getUserCredentials();

        TextView customerNameTextView = (TextView) findViewById(R.id.customerNameTextView);
        customerNameTextView.setText(userCredentials.getCustomer_name());

        EditText checkOutZidCodeEditText = (EditText) findViewById(R.id.checkOutZidCodeEditText);
        checkOutZidCodeEditText.setText(userCredentials.getZipcode());

        final Spinner checkOutCityDropDown = (Spinner) findViewById(R.id.checkOutCityDropDown);
        String[] cities = new String[]{"Jakarta Pusat", "Jakarta Selatan", "Jakarta Barat", "Jakarta Utara", "Jakarta Timur", "Tangerang", "Bekasi", "Tangerang Selatan", "Depok"};
        ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
        checkOutCityDropDown.setAdapter(cityadapter);
        checkOutCityDropDown.setSelection(cityadapter.getPosition(userCredentials.getCity()));

        EditText checkOutAddressContent = (EditText) findViewById(R.id.checkOutAddressContent);
        checkOutAddressContent.setText(userCredentials.getAddress_content());

        EditText checkOutMobile = (EditText) findViewById(R.id.checkOutMobile);
        checkOutMobile.setText(userCredentials.getMobile());

        EditText checkOutAddressNotes = (EditText) findViewById(R.id.checkOutAddressNotes);
        checkOutAddressNotes.setText(userCredentials.getAddress_notes());

        gojekButtonBoolean = false;
        etobeeButtonBoolean = false;

        final Button gojekButton = (Button) findViewById(R.id.gojekButton);

        final Button etobeeButton = (Button) findViewById(R.id.etobeeButton);

        final TextView deliveryTypeTextView = (TextView) findViewById(R.id.deliveryTypeTextView);

        final TextView deliveryFeeTextView = (TextView) findViewById(R.id.deliveryFeeTextView);

        final TextView gojekCircumstanceTextView = (TextView) findViewById(R.id.gojekCircumstanceTextView);

        final ImageView etobeedeliverytimeImageView = (ImageView) findViewById(R.id.etobeedeliverytimeImageView);

        final TextView selectDeliveryTimeTextView = (TextView) findViewById(R.id.selectDeliveryTimeTextView);

        final Spinner deliveryTimeDropDown = (Spinner) findViewById(R.id.deliveryTimeDropDown);
        String[] deliveryTimes = new String[]{"12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00"};
        ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, deliveryTimes);
        deliveryTimeDropDown.setAdapter(deliveryAdapter);

        checkOutCityDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (   ((position) >= 0 && (position <= 4)) || (position == 7)  ){
                    gojekButton.setVisibility(View.VISIBLE);
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    gojekButton.setBackgroundResource(R.drawable.deactivatedgojek);
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";

                    Log.e("Delivery Time: ", deliveryTime);
                    Log.e("Delivery Fee: ", deliveryFee);
                } else {
                    gojekButton.setVisibility(View.GONE);
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";

                    Log.e("Delivery Time: ", deliveryTime);
                    Log.e("Delivery Fee: ", deliveryFee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etobeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(etobeeButtonBoolean == true)) {
                    etobeeButtonBoolean = true;
                    gojekButtonBoolean = false;
                    etobeeButton.setBackgroundResource(R.drawable.activatedetobee);
                    gojekButton.setBackgroundResource(R.drawable.deactivatedgojek);
                    deliveryFee = "FREE!!!";
                    deliveryTypeTextView.setText("ETOBEE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    gojekCircumstanceTextView.setVisibility(View.GONE);
                    etobeedeliverytimeImageView.setVisibility(View.VISIBLE);
                    selectDeliveryTimeTextView.setText("Your Box will arrive between");
                    deliveryTimeDropDown.setVisibility(View.GONE);
                    deliveryTime = "12";
                }

                Log.e("Delivery Time: ", deliveryTime);
                Log.e("Delivery Fee: ", deliveryFee);

            }
        });

        gojekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(gojekButtonBoolean == true)) {
                    gojekButtonBoolean = true;
                    etobeeButtonBoolean = false;
                    gojekButton.setBackgroundResource(R.drawable.activatedgojek);
                    etobeeButton.setBackgroundResource(R.drawable.deactivatedetobee);
                    deliveryFee = "IDR 20.000";
                    deliveryTypeTextView.setText("OJEK ONLINE");
                    deliveryFeeTextView.setText("Delivery Fee : " + deliveryFee);
                    gojekCircumstanceTextView.setVisibility(View.VISIBLE);
                    etobeedeliverytimeImageView.setVisibility(View.GONE);
                    selectDeliveryTimeTextView.setText("Choose your delivery time");
                    deliveryTimeDropDown.setVisibility(View.VISIBLE);
                    deliveryTimeDropDown.setSelection(0);
                    deliveryTime = "12";
                }

                Log.e("Delivery Time: ", deliveryTime);
                Log.e("Delivery Fee: ", deliveryFee);

            }
        });

        deliveryTimeDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(deliveryTimeDropDown.getItemAtPosition(position)));
                stringBuilder.delete(2, 13);

                String deliveryTimeFromStringBuilder = stringBuilder.toString();

                if (gojekButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.activatedgojek).getConstantState())) {

                    deliveryTime = deliveryTimeFromStringBuilder;
                    Log.e("Delivery Time: ", deliveryTime);

                }
                Log.e("Delivery Fee: ", deliveryFee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectedMenuList = MainActivity.getCurrentMenuList();
        selectedMenuIdList = MainActivity.getCurrentMenuIdList();
        selectedMenuListUrls = MainActivity.getCurrentSelectedMenuListUrls();
        selectedPortionSizes = MainActivity.getPortionSizes();
        subTotalCost = MainActivity.getSubTotalCost();
        selectedIndividualPrices = MainActivity.getIndividualPrices();

        dateBooleanList.clear();
        dateButtonList.clear();
        localDateList.clear();
        selectedDate = "";

        incrementingInteger = 0;

        orderSummaryListView = (ListView) findViewById(R.id.orderSummaryListView);
        orderSummaryListView.setClickable(false);

        Button firstDate = (Button) findViewById(R.id.firstDate);
        Button secondDate = (Button) findViewById(R.id.secondDate);
        Button thirdDate = (Button) findViewById(R.id.thirdDate);
        Button fourthDate = (Button) findViewById(R.id.fourthDate);
        Button fifthDate = (Button) findViewById(R.id.fifthDate);

        boolean firstDateBoolean = false;
        boolean secondDateBoolean = false;
        boolean thirdDateBoolean = false;
        boolean fourthDateBoolean = false;
        boolean fifthDateBoolean = false;

        dateButtonList.add(firstDate);
        dateButtonList.add(secondDate);
        dateButtonList.add(thirdDate);
        dateButtonList.add(fourthDate);
        dateButtonList.add(fifthDate);

        dateBooleanList.add(firstDateBoolean);
        dateBooleanList.add(secondDateBoolean);
        dateBooleanList.add(thirdDateBoolean);
        dateBooleanList.add(fourthDateBoolean);
        dateBooleanList.add(fifthDateBoolean);

        //Getting Current date and time:
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date currentLocalTime = cal.getTime();

        //Checking variables, returns the current hours:
        DateFormat checkDate = new SimpleDateFormat("HH");
        checkDate.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        String checkIfAfter3 = checkDate.format(currentLocalTime);

        //Actual Variables
        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        date.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        String localTime = date.format(currentLocalTime);

        //Joda-Time Library, parsing it into a jodatime object, easier to add dates and time now.
        LocalDate localDate = new LocalDate(localTime);
        Integer checkIfAfter3Integer = Integer.valueOf(checkIfAfter3);

        //Only go through with this for loop only if it is in between sunday and thursday 3pm
        if ((localDate.getDayOfWeek() == 7) || ((localDate.getDayOfWeek() >= 1) && (localDate.getDayOfWeek() <= 4)) ) {

            //This will be false until friday so in this else we localdate + 5 days
            if (  !(  (localDate.getDayOfWeek() == 4) && (checkIfAfter3Integer >= 15)  )  ) {

                //Setting it forward 2 days as the dates will always start 2 days ahead
                LocalDate localDate2 = new LocalDate(localDate.plusDays(2));

                for (int i = localDate2.getDayOfWeek(); i < 7; i++) {

                    if (incrementingInteger == 0) {
                        String dayOfWeek = String.valueOf(localDate2.dayOfWeek().getAsText(Locale.ENGLISH));
                        String month = String.valueOf(localDate2.monthOfYear().getAsText(Locale.ENGLISH));
                        String dayOfMonth = String.valueOf(localDate2.dayOfMonth().getAsText(Locale.ENGLISH));

                        localDateList.add(localDate2);
                        dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                        incrementingInteger++;
                    } else {
                        String dayOfWeek = String.valueOf(localDate2.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                        String month = String.valueOf(localDate2.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                        String dayOfMonth = String.valueOf(localDate2.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                        localDateList.add(localDate2.plusDays(incrementingInteger));
                        dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                        incrementingInteger++;
                    }

                }

                //Separate to make the rest the of the Buttons not visible
                //Don't need to remove anything from the localdate list because we are already adding it to the specific needs.

                for (int i = incrementingInteger; i < 5; i++) {
                    dateBooleanList.remove(i);
                    dateButtonList.get(i).setVisibility(View.GONE);
                    dateButtonList.remove(i);
                }


                //Then separate to check if 3 o clock, if it is delete the first one.

                if ((checkIfAfter3Integer.intValue() >= 15)) {
                    dateButtonList.get(0).setVisibility(View.GONE);
                    dateButtonList.remove(0);
                    dateBooleanList.remove(0);
                    localDateList.remove(0);
                }

                //This else will only be called if it is still thursday and after 3pm, but before friday
            } else {
                Log.e("Else: ", "First");
                LocalDate localDate3 = new LocalDate(localDate.plusDays(5));

                for (int i = localDate3.getDayOfWeek(); i < 7; i++) {
                    String dayOfWeek = String.valueOf(localDate3.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                    String month = String.valueOf(localDate3.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                    String dayOfMonth = String.valueOf(localDate3.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                    localDateList.add(localDate3.plusDays(incrementingInteger));
                    dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                    incrementingInteger++;
                }


            }

            //This else will only be called if it is after friday so if day = 5 add 4
            //If day = 6 add 3
        } else {

            Log.e("Else: ", "Second");

            LocalDate localDate4 = new LocalDate(localDate);

            if (localDate4.getDayOfWeek() == 5) {
                localDate4 = localDate4.plusDays(4);
            } else if (localDate4.getDayOfWeek() == 6) {
                localDate4 = localDate4.plusDays(3);
            }

            for (int i = localDate4.getDayOfWeek(); i < 7; i++) {
                String dayOfWeek = String.valueOf(localDate4.plusDays(incrementingInteger).dayOfWeek().getAsText(Locale.ENGLISH));
                String month = String.valueOf(localDate4.plusDays(incrementingInteger).monthOfYear().getAsText(Locale.ENGLISH));
                String dayOfMonth = String.valueOf(localDate4.plusDays(incrementingInteger).dayOfMonth().getAsText(Locale.ENGLISH));

                localDateList.add(localDate4.plusDays(incrementingInteger));
                dateButtonList.get(incrementingInteger).setText(dayOfWeek + "," + "\n" + dayOfMonth + " " + month);
                incrementingInteger++;
            }

        }

        for (int i = 0; i < dateButtonList.size(); i++) {

            final int j = i;

            dateButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        dateBooleanList.set(i, false);

                    }

                    //When I set the position at j to true, i was looking at the dateButtonList.get(i)
                    //I wanted to set the position in the dateBooleanList that matched the position of the dateButtonList clicked
                    //So if I invoked the dateButtonList at position 3's OnClickListener I want to set the dateBooleanList at position 3
                    dateBooleanList.set(j, true);

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        if (dateBooleanList.get(i).booleanValue() == true) {
                            dateButtonList.get(i).setBackgroundResource(R.drawable.greydateselection);
                            dateButtonList.get(i).setTextColor(getResources().getColor(R.color.white));
                        } else if (   !(dateButtonList.get(i).getBackground().getConstantState().equals(getResources().getDrawable(android.R.drawable.btn_default).getConstantState()) ) && !(dateButtonList.get(i).getTextColors().equals(getResources().getColor(R.color.black)))   ){
                            dateButtonList.get(i).setBackgroundResource(android.R.drawable.btn_default);
                            dateButtonList.get(i).setTextColor(getResources().getColor(R.color.black));
                        }

                    }

                    for (int i = 0; i < dateBooleanList.size(); i++) {

                        if (dateBooleanList.get(i).booleanValue() == true) {
                            selectedDate = localDateList.get(i).toString();
                        }

                    }

                    Log.e("Selected Date: ", selectedDate);

                }
            });

        }

        orderSummaryListView.setAdapter(new MyAdapter(selectedMenuList, selectedMenuIdList, selectedMenuListUrls, selectedPortionSizes, selectedIndividualPrices));
        setListViewHeightBasedOnItems(orderSummaryListView);
    }

    public class MyAdapter extends BaseAdapter{

        private List<Data> selectedMenuList;
        private List<Integer> selectedMenuIdList;
        private List<String> selectedMenuListUrls;
        private List<String> selectedPortionSizes;
        private List<String> selectedIndividualPrices;

        public MyAdapter(List<Data> currentSelectedMenuList, List<Integer> currentSelectedMenuIdList, List<String> currentSelectedMenuListUrls, List<String> portionSize, List<String> individualPrices){
            this.selectedMenuList = currentSelectedMenuList;
            this.selectedMenuIdList = currentSelectedMenuIdList;
            this.selectedMenuListUrls = currentSelectedMenuListUrls;
            this.selectedPortionSizes = portionSize;
            this.selectedIndividualPrices = individualPrices;
        }

        @Override
        public int getCount() {
            return selectedMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return selectedMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View orderCheckOutView = getLayoutInflater().inflate(R.layout.row_ordercheckout, null);

            NetworkImageView orderCheckOutNetworkImageView = (NetworkImageView) orderCheckOutView.findViewById(R.id.orderCheckOutNetworkImageView);
            TextView orderCheckOutMenuTitle = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutMenuTitle);
            TextView orderCheckOutPortion = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutPortion);
            TextView orderCheckOutMenuType = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutMenuType);
            TextView orderCheckOutPrice = (TextView) orderCheckOutView.findViewById(R.id.orderCheckOutPrice);

            orderCheckOutNetworkImageView.setImageUrl(selectedMenuListUrls.get(position).toString(), ConnectionManager.getImageLoader(CheckOut.this));
            orderCheckOutMenuTitle.setText(selectedMenuList.get(position).getMenu_name());

            String initialPortion = selectedPortionSizes.get(position);
            initialPortion = initialPortion + "erson";

            String finalPortion = initialPortion.substring(0,1) + " " + initialPortion.substring(1, initialPortion.length());
            orderCheckOutPortion.setText(finalPortion);

            if (selectedMenuList.get(position).getMenu_type().equals("3")) {
                orderCheckOutMenuType.setText("Original");
            } else {
                orderCheckOutMenuType.setText("Breakfast");
            }

            orderCheckOutPrice.setText(selectedIndividualPrices.get(position));

            return orderCheckOutView;
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}
