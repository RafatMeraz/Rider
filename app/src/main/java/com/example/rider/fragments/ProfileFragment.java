package com.example.rider.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.R;
import com.example.rider.activities.ChangePasswordActivity;
import com.example.rider.activities.EditProfileActivity;
import com.example.rider.activities.LoginActivity;
import com.example.rider.databases.SharedPrefManager;
import com.example.rider.model.User;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView userImageIV;
    private TextView userFullNameTV, emailTV, phoneTV, address1TV, address2TV;
    private Button editButton;
    private CardView logOutButton, changePasswordButton;
    private Switch activeSwitch;
    private SwipeRefreshLayout swipeRefreshLayout;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        userImageIV = rootView.findViewById(R.id.profileImgView);
        userFullNameTV = rootView.findViewById(R.id.profileFullName);
        emailTV = rootView.findViewById(R.id.profileEmailTV);
        phoneTV = rootView.findViewById(R.id.profilePhoneTV);
        address1TV = rootView.findViewById(R.id.profileAddress1TV);
        address2TV = rootView.findViewById(R.id.profileAddress2TV);
        editButton = rootView.findViewById(R.id.profileEditButton);
        logOutButton = rootView.findViewById(R.id.profileLogOutButton);
        changePasswordButton = rootView.findViewById(R.id.changePasswordButton);
        activeSwitch = rootView.findViewById(R.id.activeStatusSwitch);
        swipeRefreshLayout = rootView.findViewById(R.id.profileSwipeToRefreshLayout);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getActivity()).logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });

        if (Constaint.isConnectedToInternet(getActivity())){
            loadUserDetails(Constaint.currentUser.getId());
            getActiveStatus();
        } else {
            DynamicToast.makeError(getActivity(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
        }
        activeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constaint.isConnectedToInternet(getActivity())) {
                    int status = 0;
                    if (activeSwitch.isChecked())
                        status = 1;
                    else
                        status = 0;
                    changeActiveStatus(status);
                } else {
                    DynamicToast.makeError(getActivity(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                i.putExtra("user", user);
                getActivity().startActivity(i);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Constaint.isConnectedToInternet(getActivity())){
                    loadUserDetails(Constaint.currentUser.getId());
                    getActiveStatus();
                } else {
                    DynamicToast.makeError(getActivity(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return  rootView;
    }

    private void loadUserDetails(final int id) {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.USER_DETAILS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            Log.e("response : ", response.toString());

                            user= new User(
                                    id,
                                    userResponse.getString("email"),
                                    userResponse.getString("name"),
                                    userResponse.getString("addressl1"),
                                    userResponse.getString("addressl2"),
                                    userResponse.getString("phone_number"),
                                    userResponse.getString("image")
                            );
                            SharedPrefManager.getInstance(getActivity()).userLogin(Constaint.currentUser.getId(), user.getName(), user.getEmail());
                            Constaint.currentUser = new User(id, user.getEmail(), user.getName());

                            if (!user.getImg().equals("default.png"))
                                Picasso.with(getActivity()).load(Constaint.IMG_BASE_URL+user.getImg()).into(userImageIV);
                            Log.e( "onResponse: ", user.getImg());
                            userFullNameTV.setText(user.getName());
                            emailTV.setText(user.getEmail());
                            phoneTV.setText(user.getPhone());
                            address1TV.setText(user.getAddress1());
                            if (user.getAddress2()=="null")
                                address2TV.setTextColor(Color.parseColor("#ff0000"));
                            else
                                address2TV.setText(user.getAddress2());
                            swipeRefreshLayout.setRefreshing(false);
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
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void changeActiveStatus(final int status){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.UPDATE_ACTIVE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            if (userResponse.getBoolean("error")){
                                DynamicToast.makeError(getActivity(), userResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                if (activeSwitch.isChecked()){
                                    Constaint.cactiveStatus = 1;
                                    DynamicToast.makeSuccess(getActivity(), "Online!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Constaint.cactiveStatus = 0;
                                    DynamicToast.makeSuccess(getActivity(), "Offline !", Toast.LENGTH_SHORT).show();
                                }
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
                params.put("status", String.valueOf(status));
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void getActiveStatus(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.GET_ACTIVE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject userResponse = new JSONObject(response);
                            if(userResponse.getInt("is_active")==1){
                                activeSwitch.setChecked(true);
                            } else{
                                activeSwitch.setChecked(false);
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
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constaint.isConnectedToInternet(getActivity())){
            loadUserDetails(Constaint.currentUser.getId());
            getActiveStatus();
        } else {
            DynamicToast.makeError(getActivity(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }
}
