package com.boardinglabs.mireta.standalone.component.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BuildConfig;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoBoldTextView;
import com.cloudpos.utils.T;

import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

/**
 * Created by Dhimas on 9/29/17.
 */

public class MethodUtil extends Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MethodUtil.context = getApplicationContext();
    }

    public static String getDateOnly(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time);
    }

    public static void showCustomToast(Activity activityContext, String message, int image) {
        if (!TextUtils.isEmpty(message) && message.contains("AKUPAY") && !PreferenceManager.getStatusAkupay()) {
            message = message.replace("AKUPAY", "DOOMO");
        }
        if (activityContext != null && !TextUtils.isEmpty(message)) {
            LayoutInflater inflater = activityContext.getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) activityContext.findViewById(R.id.toast_layout_root));
            RobotoBoldTextView messageTextView = (RobotoBoldTextView) layout.findViewById(R.id.textview_message);
            ImageView imageView = (ImageView) layout.findViewById(R.id.imageview_icon);
            messageTextView.setText(message);
            if (image != 0) imageView.setImageResource(image);
            else imageView.setVisibility(View.GONE);
            Toast toast = new Toast(activityContext);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
//            if (message.equalsIgnoreCase(Constant.EXPIRED_SESSION) || message.equalsIgnoreCase(Constant.EXPIRED_ACCESS_TOKEN)) {
//                BaseActivity base = new BaseActivity() {
//                    @Override
//                    protected int getLayoutResourceId() {
//                        return 0;
//                    }
//
//                    @Override
//                    protected void setContentViewOnChild() {
//
//                    }
//
//                    @Override
//                    protected void onCreateAtChild() {
//
//                    }
//
//                    @Override
//                    protected void onBackBtnPressed() {
//
//                    }
//                };
//                base.goToLoginPage();
//            }
        }
    }

    public static String getPrintBTLabelValue(String label, String value, boolean usingNextLine, boolean endWithNewLine){
        int lineCharCount = 42;
        int labelCharCount = label.length();
        int valueCharCount = value.length();

        if (labelCharCount + valueCharCount > (lineCharCount-2)){
            usingNextLine = true;
        }

        String output = "";
        if (!usingNextLine){
            output += label;
            for (int i = labelCharCount; i < lineCharCount-valueCharCount; i++){
                output += " ";
            }
            output += value;
        }
        else{
            output = label;
            for (int i = labelCharCount; i < lineCharCount; i++){
                output += " ";
            }
            output += "\n";
            for (int i = 0; i < valueCharCount; i++){
                output += " ";
            }
            output += value;
        }
        if (endWithNewLine){
            output += "";
        }
        return output;
    }

    public static String toCurrencyFormat(final String value) {

        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 3 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append(".");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static void setCurrency(final EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edt.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    edt.setText(clean);
                    edt.setSelection(clean.length());
                    edt.addTextChangedListener(this);
                }
            }
        });
    }

    public static String toDateFormat(final String value) {
        if (!TextUtils.isEmpty(value)) {
            String formattedPrice = value.replaceAll("[^\\d]", "");

            String reverseValue = new StringBuilder(formattedPrice).reverse().toString();
            StringBuilder finalValue = new StringBuilder();
            for (int i = 1; i <= reverseValue.length(); i++) {
                char val = reverseValue.charAt(i - 1);
                finalValue.append(val);
                if (i % 2 == 0 && i != reverseValue.length() && i > 0) {
                    finalValue.append("/");
                }
            }

            return finalValue.reverse().toString();
        }

        return StringUtils.EMPTY;
    }

    public static String formatTokenNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append("-");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String formatCardNumber(final String number) {
        String cleanString = number.replace(" ", "");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cleanString.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(cleanString.charAt(i));
        }

        return result.toString();
    }

    public static String[] formatDateAndTime(String dateTime) {
        String[] tempDateTime = new String[2];
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("id")).parse(dateTime);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id","ID"));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH : mm : ss");
            tempDateTime[0] = dateFormat.format(date);
            tempDateTime[1] = timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempDateTime;
    }

    public static String getHMac(String inputString, String unixTime, boolean isSearchMerchant) throws Exception {
        String sha256Hash;
        if (isSearchMerchant) {
            sha256Hash = inputString;
        } else {
            sha256Hash = inputString.replace("%20", "+");
        }

        String hash2 = BuildConfig.SECRET_ID;

        for (int i = 1; i < 11; i++) {
            sha256Hash = HashGenerator.generateSHA256(sha256Hash + HashGenerator.generateMD5(String.valueOf(i)) + unixTime);
        }

        for (int i = 1; i < 11; i++) {
            hash2 = HashGenerator.generateSHA256(hash2 + HashGenerator.generateMD5(String.valueOf(i)));
        }

        String hash3 = sha256Hash;
        for (int i = 1; i < 11; i++) {
            hash3 = HashGenerator.generateSHA256(hash3 + HashGenerator.generateMD5(String.valueOf(i)) + hash2);
        }

        return hash3;
    }

    public static String formatDateCreditcard(String date) {
        String cleanDate = date.trim();
        StringBuilder result = new StringBuilder();
        for (int i =0; i < cleanDate.length() ;i++) {
            if (i % 2 == 0 && i != 0) {
                result.append("/");
            }
            result.append(cleanDate.charAt(i));
        }
        return result.toString();

    }

    public static StringBuilder printLog(String listOrder){
        int maxLogSize = 1000;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i <= listOrder.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = Math.min(end, listOrder.length());
            stringBuilder.insert(0, listOrder.substring(start, end));
        }

        return stringBuilder;
    }

    public static Bitmap bitmapPhoto(ImageView imageView, String currentPhotoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
    }

    public static String getErrorResponse(String errorBody){
        try {
            JSONObject jObjError = new JSONObject(errorBody);
            return jObjError.getString("error");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject jObjError = new JSONObject(errorBody);
            return jObjError.getString("message");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Failed to parse response";
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }

    public static String GetCountryZipCode(Context ctx){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl= ctx.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
