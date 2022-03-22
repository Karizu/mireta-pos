package com.boardinglabs.mireta.standalone.component.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.boardinglabs.mireta.standalone.component.network.entities.Brand;
import com.boardinglabs.mireta.standalone.component.network.entities.Business;
import com.boardinglabs.mireta.standalone.component.network.entities.InquiryStatus.InquiryStatusOperations;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.PaymentTypeResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Role;
import com.boardinglabs.mireta.standalone.component.network.entities.StockLocation;
import com.boardinglabs.mireta.standalone.component.network.entities.User;
import com.orhanobut.hawk.Hawk;
import com.boardinglabs.mireta.standalone.component.network.gson.GAgent;
import com.boardinglabs.mireta.standalone.component.network.gson.GTopup;
import com.boardinglabs.mireta.standalone.modul.old.scanqr.QuickResponse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhimas on 10/9/17.
 */

public class PreferenceManager {

    private static final String SESSION_TOKEN = "sessionToken";
    private static final String SESSION_TOKEN_ARDI = "sessionTokenArdi";
    private static final String USER_ID_ARDI = "userIdArdi";
    private static final String IS_LOGIN = "isLogin";
    private static final String IS_ARDI = "isARDI";
    private static final String USER_LOGIN = "userLogin";
    private static final String AGENT = "agent";
    private static final String TOPUP = "topup";
    private static final String REFFERAL_ID = "refferalId";
    private static final String IS_AKUPAY = "isAkupay";
    private static final String QR_RESPONSE = "qrResponse";
    private static final String PARKING_ID = "parkingId";
    private static final String IMEI = "imei";
    private static final String AVATAR = "avatar";
    private static final String SAVED_TOKEN = "tokenfirebase";
    private static final String KEY_ACCESS = "token";
    private static final String FB_TOKEN = "firebasetoken";
    private static final String PASS_VOID = "passVoid";
    private static final String BITMAP_STRING = "bitmapString";
    private static final String OPERATION_DATA = "operationData";
    private static final String IS_OFFLINE_MODE = "isOfflineMode";
    private static final String ITEM_VARIANTS_LIST = "itemVariantList";
    private static final String TRANSACTION_TYPE_LIST = "transactionTypeList";


    private static Bitmap largeIcon;
    private static final Bitmap BITMAP_HEADER = null;


    private static final String USER = "user";
    private static final String BRAND = "brand";
    private static final String STOCK_LOCATION = "stock_location";
    private static final String ROLE = "role";
    private static final String BUSINESS = "business";

    private static final String BOOTH_ID = "booth_id";
    private static final String MASTER_KEY = "master_key";

    private static Context ctx;
    private static PreferenceManager mInstance;

    public PreferenceManager(Context context) {
//        Hawk.init(context)
//                .setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST)
//                .setStorage(HawkBuilder.newSharedPrefStorage(context))
//                .setPassword("P@ssw0rd123")
//                .build();
        Hawk.init(context).build();
    }
    public static synchronized PreferenceManager getInstance(Context context){
        if (mInstance == null)
            mInstance = new PreferenceManager(context);
        return mInstance;
    }


    public static Bitmap getBitmapHeader() {
        return Hawk.get(BITMAP_STRING, null);
    }


    public static void setBitmapHeader(Bitmap bitmapHeader) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapHeader.compress(Bitmap.CompressFormat.PNG, 90, stream);
        Hawk.put(BITMAP_STRING, bitmapHeader);
    }



    public static String getSessionToken() {
        return Hawk.get(SESSION_TOKEN, "");
    }


    public static void setSessionToken(String token) {
        Hawk.put(SESSION_TOKEN, token);
    }

    public static void setOperationData(InquiryStatusOperations inquiryStatusOperations) {
        Hawk.put(OPERATION_DATA, inquiryStatusOperations);
    }

    public static InquiryStatusOperations getOperationData() {
        return Hawk.get(OPERATION_DATA, new InquiryStatusOperations());
    }

    public static void setItemVariantsList(List<ItemVariants> itemVariantsList) {
        Hawk.put(ITEM_VARIANTS_LIST, itemVariantsList);
    }

    public static List<ItemVariants> getItemVariantList() {
        return Hawk.get(ITEM_VARIANTS_LIST, new ArrayList<>());
    }

    public static void setTransactionType(List<PaymentTypeResponse> transactionType) {
        Hawk.put(TRANSACTION_TYPE_LIST, transactionType);
    }

    public static List<PaymentTypeResponse> getTransactionType() {
        return Hawk.get(TRANSACTION_TYPE_LIST, new ArrayList<>());
    }

    public static String getSessionTokenArdi() {
        return Hawk.get(SESSION_TOKEN_ARDI, "");
    }

    public static void setSessionTokenArdi(String token) {
        Hawk.put(SESSION_TOKEN_ARDI, token);
    }

    public static void setPassVoid(String pass) {
        Hawk.put(PASS_VOID, pass);
    }

    public static String getPassVoid() {
        return Hawk.get(PASS_VOID, "");
    }

    public static String getUserIdArdi() {
        return Hawk.get(USER_ID_ARDI, "");
    }

    public static void setUserIdArdi(String user_id) {
        Hawk.put(USER_ID_ARDI, user_id);
    }

    public static void logIn(String token, String name, String mobile, String whitelistTopUp) {
        Hawk.put(IS_LOGIN, true);
        Hawk.put(SESSION_TOKEN, token);
        Hawk.put(USER_LOGIN, new String[]{name, mobile, whitelistTopUp});
    }

    public static void setAvatar(String avatarString) {
        Hawk.put(AVATAR,avatarString);
    }

    public static String getAvatar() {
        return Hawk.get(AVATAR);
    }

    public static void logOut() {
        //Hawk.put(USER_LOGIN, null);
        Hawk.put(IS_LOGIN, false);
        Hawk.put(IS_ARDI, false);
        Hawk.put(SESSION_TOKEN, "");
        Hawk.put(SESSION_TOKEN_ARDI, "");
        Hawk.put(AGENT, null);
    }

    public static String[] getUserInfo() {
        return Hawk.get(USER_LOGIN);
    }

    public static Boolean isLogin() {
        return Hawk.get(IS_LOGIN, false);
    }

    public static Boolean isArdi() {
        return Hawk.get(IS_ARDI, false);
    }

    public static void setAgent(GAgent agent) {
        Hawk.put(AGENT, agent);
    }

    public static GAgent getAgent() {
        return Hawk.get(AGENT);
    }

    public static void setTopup(GTopup topup) {
        Hawk.put(TOPUP, topup);
    }

    public static GTopup getTopup() {
        return Hawk.get(TOPUP);
    }

    public static void setRefferalId(String id) {
        Hawk.put(REFFERAL_ID, id);
    }

    public static String getRefferalId() {
        return Hawk.get(REFFERAL_ID,"");
    }

    public static void setStatusAkupay(boolean isAkupay) {
        Hawk.put(IS_AKUPAY, isAkupay);
    }

    public static boolean getStatusAkupay() {
        return Hawk.get(IS_AKUPAY, false);
    }

    public static void setQrResponse(QuickResponse reseponse) {
        Hawk.put(QR_RESPONSE, reseponse);
    }

    public static  QuickResponse getQrResponse() {
        return Hawk.get(QR_RESPONSE);
    }

    public static void setParkingId(String transactionId) {
        Hawk.put(PARKING_ID, transactionId);
    }

    public static String getParkingId() {
        return Hawk.get(PARKING_ID);
    }

    public static void emptyParkingId() {
        setParkingId("");
    }

    //public static boolean saveToken(String token){
    //    SharedPreferences sharedPreferences = ctx.getSharedPreferences(SAVED_TOKEN, Context.MODE_PRIVATE);
    //    SharedPreferences.Editor editor = sharedPreferences.edit();
    //    editor.putString(KEY_ACCESS, token);
    //    editor.apply();
    //    return true;
    //}

    //public String getToken(){
    //    SharedPreferences sharedPreferences = ctx.getSharedPreferences(SAVED_TOKEN, Context.MODE_PRIVATE);
    //    return sharedPreferences.getString(KEY_ACCESS.null)
    //}

    public static void setSavedToken(String token){
        Hawk.put(FB_TOKEN,token);
    }
    public static String getSavedToken() {
        return Hawk.get(FB_TOKEN);
    }
	public static String getImei() {
        return Hawk.get(IMEI);
    }

    public static void setImei(String imei) {
        Hawk.put(IMEI, imei);
    }

    public static boolean isOfflineMode() {
        return Hawk.get(IS_OFFLINE_MODE, false);
    }

    public static void setIsOfflineMode(boolean isOfflineMode) {
        Hawk.put(IS_OFFLINE_MODE, isOfflineMode);
    }

    public static void saveLogIn(String token, String id,String first_name, String username) {
        Hawk.put(IS_LOGIN, true);
        Hawk.put(SESSION_TOKEN, token);
        Hawk.put(USER_LOGIN, new String[]{id, username, first_name});
    }

    public static void setBoothId(String boothId){
        Hawk.put(BOOTH_ID, boothId);
    }

    public static String getBoothId(){
        return Hawk.get(BOOTH_ID, "");
    }

    public static void setMasterKey(String masterKey){
        Hawk.put(MASTER_KEY, masterKey);
    }

    public static String getMasterKey(){
        return Hawk.get(MASTER_KEY, "");
    }

    public static void saveLogInArdi(){
        Hawk.put(IS_ARDI, true);
    }

    public static void saveUser(User user) {
        Hawk.put(USER, user);
    }

    public static User getUser() {
        return Hawk.get(USER);
    }

    public static void saveBusiness(Business business) {
        Hawk.put(BUSINESS, business);
    }

    public static Business getBusiness() {
        return Hawk.get(BUSINESS);
    }

    public static void saveBrand(Brand brand) {
        Hawk.put(BRAND, brand);
    }

    public static Brand getBrand() {
        return Hawk.get(BRAND);
    }


    public static void saveStockLocation(StockLocation stockLocation) {
        Hawk.put(STOCK_LOCATION, stockLocation);
    }

    public static StockLocation getStockLocation() {
        return Hawk.get(STOCK_LOCATION);
    }

    public static void saveRole(Role role) {
        Hawk.put(ROLE, role);
    }

    public static Role getRole() {
        return Hawk.get(ROLE, null);
    }
}
