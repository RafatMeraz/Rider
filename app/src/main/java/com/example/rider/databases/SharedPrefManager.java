package com.example.rider.databases;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rider.model.Req;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mContext;

    private static final String SHARED_PREF_NAME = "abeerfooddriversharepref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID = "id";
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_ORDER_ID = "order_id";
    private static final String KEY_DELIVER_ID = "deliver_id";
    private static final String KEY_DELIVER_USER_ID = "user_id";
    private static final String KEY_DELIVER_SHOP_NAME = "shop_name";
    private static final String KEY_DELIVER_SHOP_ADDRESS = "location";
    private static final String KEY_DELIVER_ITEMS = "items";
    private static final String KEY_DELIVER_LOCATION = "address";


    private SharedPrefManager(Context context) {
        // Specify the application context
        mContext = context;
        // Get the request queue
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        // Return RequestHandler new Instance
        return mInstance;
    }

    public boolean userLogin(int id, String userName, String email){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USERNAME, null) != null) {
            return true;
        }
        return false;
    }

    public int getShopID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_SHOP_ID, 0);
    }

    public boolean setShopId(int shopId){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SHOP_ID, shopId);
        editor.apply();
        return true;
    }
    public boolean clearShopId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SHOP_ID, 0);
        editor.apply();
        return true;
    }
    public int getOrderID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ORDER_ID, 0);
    }

    public boolean setOrderId(int shopId){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ORDER_ID, shopId);
        editor.apply();
        return true;
    }
    public boolean clearOrderId(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ORDER_ID, 0);
        editor.apply();
        return true;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUserName(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    public int getUserID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, 0);

    }
    public String getUserEmail(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public boolean clearDelivery(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_DELIVER_ID, 0);
        editor.putString(KEY_DELIVER_SHOP_NAME, null);
        editor.putString(KEY_DELIVER_SHOP_ADDRESS, null);
        editor.putInt(KEY_DELIVER_USER_ID, 0);
        editor.putString(KEY_DELIVER_LOCATION, null);
        editor.putString(KEY_DELIVER_ITEMS, null);
        editor.apply();
        return true;
    }
    public boolean setDelivery(Req req){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_DELIVER_ID, req.getId());
        editor.putString(KEY_DELIVER_SHOP_NAME, req.getShopName());
        editor.putString(KEY_DELIVER_SHOP_ADDRESS, req.getLocation());
        editor.putInt(KEY_DELIVER_USER_ID, req.getUser_id());
        editor.putString(KEY_DELIVER_LOCATION, req.getAddress());
        editor.putString(KEY_DELIVER_ITEMS, req.getItems());
        editor.apply();
        return true;
    }
    public int getDeliveryID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_DELIVER_ID, 0);
    }

    public String getDeliveryShopName(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DELIVER_SHOP_NAME, null);
    }
    public String getDeliveryShopAddress(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DELIVER_SHOP_ADDRESS, null);
    }

    public int getDeliveryUserID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_DELIVER_USER_ID, 0);
    }
    public String getDeliveryLocation(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DELIVER_LOCATION, null);
    }

    public String getDeliveryItems(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DELIVER_ITEMS, null);
    }



}