package com.example.rider.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class ScheduledService extends Service {

    private Timer timer = new Timer();


    public ScheduledService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e( "run: ", "Running service");
                if (Constaint.cactiveStatus==1){
                    uploadLocation();
                }
            }
        }, 0, 2000);
    }

    private void uploadLocation() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.UPDATE_CURRENT_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("error")){
                                DynamicToast.makeError(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } else{
                                Log.e( "onResponse: ", String.valueOf(jsonObject.getBoolean("error")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(Constaint.currentUser.getId()));
                params.put("latitude", String.valueOf(Constaint.CURRENT_LOCATION.getLatitude()));
                params.put("longitude", String.valueOf(Constaint.CURRENT_LOCATION.getLongitude()));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}
