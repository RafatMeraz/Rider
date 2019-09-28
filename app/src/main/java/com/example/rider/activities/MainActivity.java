package com.example.rider.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.R;
import com.example.rider.databases.SharedPrefManager;
import com.example.rider.fragments.HomeFragment;
import com.example.rider.fragments.OrderFragment;
import com.example.rider.fragments.ProfileFragment;
import com.example.rider.model.User;
import com.example.rider.services.ScheduledService;
import com.example.rider.utils.Constaint;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            Constaint.currentUser = new User(
                    SharedPrefManager.getInstance(this).getUserID(),
                    SharedPrefManager.getInstance(this).getUserEmail(),
                    SharedPrefManager.getInstance(this).getUserName()
            );
            loadDriverStatus();
            getActiveStatus();
        }

        Fragment fragment = new OrderFragment();
        loadFragment(fragment);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (SharedPrefManager.getInstance(getApplicationContext()).getDeliveryID() != 0){
            Intent intent = new Intent(getApplicationContext(), PendingDelivery.class);
            startActivity(intent);
        }

//        Intent intentService = new Intent(getApplicationContext(), ScheduledService.class);
//        startService(intentService);
    }

    private void getActiveStatus(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.GET_ACTIVE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            if(userResponse.getInt("is_active")==1){
                                Constaint.cactiveStatus = 1;
                            } else{
                                Constaint.cactiveStatus = 0;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(Constaint.currentUser.getId()));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void loadDriverStatus() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Constaint.CURRENT_IS_BUSY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject coreObject = new JSONObject(response);
                    Constaint.is_busy_status = coreObject.getInt("is_busy");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DynamicToast.makeError(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(Constaint.currentUser.getId()));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_order:
                    fragment = new OrderFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_user:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }


    };


    private void loadFragment(Fragment fragment) {

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

}
