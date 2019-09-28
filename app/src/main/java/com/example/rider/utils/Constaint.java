package com.example.rider.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.rider.model.Req;
import com.example.rider.model.User;

public class Constaint {
    public static User currentUser;
    public static final String IMG_BASE_URL = "http://www.mykingscoder.xyz/Android/images/";
    private static final String BASE_URL = "http://www.mykingscoder.xyz/Android/v3/";
    public static final String SIGNIN_URL = BASE_URL + "userLogin.php";
    public static final String CHECK_NEW_REQUEST = BASE_URL + "request.php";
    public static final String CHANGE_BUSY_STATUS = BASE_URL + "changeCurrentStatus.php";
    private static final String BASE_URL1 = "http://www.mykingscoder.xyz/Android/v1/";
    public static final String USER_DETAILS_URL = BASE_URL1 +"userDetails.php";
    public static final String UPDATE_PASSWORD = BASE_URL1 +"updatePassword.php";
    public static final String UPDATE_ACTIVE_STATUS = BASE_URL +"changeActiveStatus.php";
    public static final String GET_ACTIVE_STATUS = BASE_URL +"getActiveStatus.php";
    public static final String UPDATE_USER_URL = BASE_URL1 +"updateUser.php";
    public static final String UPDATE_CURRENT_LOCATION = BASE_URL +"updateLocation.php";
    public static final String NEW_REQUEST = BASE_URL +"request.php";
    public static final String UPADATE_FOOD_STATUS = BASE_URL +"updateFoodStatus.php";
    public static final String CURRENT_IS_BUSY_URL = BASE_URL +"changeCurrentStatus.php";
    public static final String SINGLE_ORDER = BASE_URL +"singleOrder.php";
    public static final String USER_PHONE_URL = BASE_URL +"userPhone.php";

    public static int is_busy_status;
    public static int cactiveStatus ;
    public static Req NEW_REQ ;
    public static Location CURRENT_LOCATION ;

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
