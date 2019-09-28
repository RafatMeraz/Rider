package com.example.rider.fragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.R;
import com.example.rider.activities.MainActivity;
import com.example.rider.activities.PendingDelivery;
import com.example.rider.databases.SharedPrefManager;
import com.example.rider.model.Req;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    TextView orderIdTV, deliveryAddressTV, pickUpLocationTV, noOrderTV;
    Button acceptButton;
    CardView rootCardView;
    Req req;
    public static final int CHANNEL_ID = 122;
    public static final int notificationId = 1232323;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        orderIdTV = rootView.findViewById(R.id.reqOrderIDTV);
        deliveryAddressTV = rootView.findViewById(R.id.reqDeliveryAddressTV);
        pickUpLocationTV = rootView.findViewById(R.id.reqPickUpAddressTV);
        acceptButton = rootView.findViewById(R.id.requestAcceptButton);
        rootCardView = rootView.findViewById(R.id.mainOrdersCardView);
        noOrderTV = rootView.findViewById(R.id.noOrderTV);

        loadNewOrders();
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constaint.cactiveStatus==1 && Constaint.is_busy_status==0){
                    updateOrder();
                }
            }
        });

        return rootView;
    }

    private void updateOrder() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading");
        dialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.UPADATE_FOOD_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")){
                                DynamicToast.makeError(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                loadNewOrders();
                            } else {
                                Log.e("OnNewReq: ", "Accepted");
                                currentIsBusyStatusChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnNewReq: ", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("driver_id", String.valueOf(SharedPrefManager.getInstance(getActivity()).getShopID()));
                params.put("id", String.valueOf(req.getId()));
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void currentIsBusyStatusChange() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.CHANGE_BUSY_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")){
                                DynamicToast.makeError(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                loadNewOrders();
                            } else {
                                Constaint.is_busy_status = 1;
                                SharedPrefManager.getInstance(getActivity()).setDelivery(req);
                                Intent intent = new Intent(getContext(), PendingDelivery.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnNewReq: ", error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("status", String.valueOf(1));
                params.put("id", String.valueOf(Constaint.currentUser.getId()));
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public void loadNewOrders(){
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.NEW_REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("request")){
                                    JSONObject object = jsonObject.getJSONObject("0");
                                    req = new Req(
                                            object.getInt("id"),
                                            object.getInt("user_id"),
                                            object.getString("address"),
                                            object.getString("shop_name"),
                                            object.getString("item_list"),
                                            object.getString("location"),
                                            object.getDouble("latitude"),
                                            object.getDouble("longitude")
                                    );
                                    noOrderTV.setVisibility(View.INVISIBLE);
                                    rootCardView.setVisibility(View.VISIBLE);
                                    orderIdTV.setText("ORDER ID#"+req.getId());
                                    pickUpLocationTV.setText("Pick up address: "+ req.getShopName()+", "+req.getLocation());
                                    deliveryAddressTV.setText("Delivery address: "+req.getAddress());
                                addNotification();
                            } else {
                                noOrderTV.setVisibility(View.VISIBLE);
                                Log.e("OnNewReq: ", "No new Request");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnNewReq: ", error.getMessage());
            }
        });
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.loc) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


}
