package com.example.rider.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.rider.databases.SharedPrefManager;
import com.example.rider.model.Req;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PendingDelivery extends AppCompatActivity {

    private TextView orderIdTV, pickUpTV, deliveryTV, phoneTV, foodItemsTV;
    private Button deliverButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accepted_order_layout);


        if (Constaint.isConnectedToInternet(getApplicationContext())){
            loadOrderDetails();
        }
        orderIdTV = findViewById(R.id.pendingOrderIDTV);
        pickUpTV = findViewById(R.id.pendingPickUpAddressTV);
        deliveryTV = findViewById(R.id.pendingDeliveryAddressTV);
        phoneTV = findViewById(R.id.pendingPhoneNumberTV);
        foodItemsTV = findViewById(R.id.pendingFoodItemsTV);
        deliverButton = findViewById(R.id.pendingDeliveredButton);

        orderIdTV.setText("ORDER ID#"+SharedPrefManager.getInstance(getApplicationContext()).getDeliveryID());
        pickUpTV.setText("Pick up address: "+SharedPrefManager.getInstance(getApplicationContext()).getDeliveryShopName()+" "+SharedPrefManager.getInstance(getApplicationContext()).getDeliveryShopAddress());
        deliveryTV.setText("Delivery address: "+SharedPrefManager.getInstance(getApplicationContext()).getDeliveryLocation());
        foodItemsTV.setText("Food items : "+SharedPrefManager.getInstance(getApplicationContext()).getDeliveryItems());

        deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIsBusyStatusChange();
            }
        });
    }

    private void loadOrderDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.USER_PHONE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject array = new JSONObject(response);
                            if (array.getBoolean("error")){
                                DynamicToast.makeError(getApplicationContext(), array.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                phoneTV.setText(array.getString("phone_number"));
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
                params.put("id", String.valueOf(SharedPrefManager.getInstance(getApplicationContext()).getDeliveryUserID()));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void currentIsBusyStatusChange() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.CHANGE_BUSY_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")){
                                DynamicToast.makeError(getApplicationContext(), jsonObject.getString("messaage"), Toast.LENGTH_SHORT).show();
                            } else {
                                Constaint.is_busy_status = 0;
                                SharedPrefManager.getInstance(getApplicationContext()).clearOrderId();
                                SharedPrefManager.getInstance(getApplicationContext()).clearShopId();
                                SharedPrefManager.getInstance(getApplicationContext()).clearDelivery();
                                finish();
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
                params.put("status", String.valueOf(0));
                params.put("id", String.valueOf(Constaint.currentUser.getId()));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

}
