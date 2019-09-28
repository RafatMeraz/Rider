package com.example.rider.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.example.rider.model.User;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button signInButton;
    private TextView forgotPassTV;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();
    }

    private void initialization() {
        emailET = findViewById(R.id.signInEmailET);
        passwordET = findViewById(R.id.signInPasswordET);
        signInButton = findViewById(R.id.signInSignInButton);
        forgotPassTV = findViewById(R.id.signInForgotPasswordTV);
        progressBar = findViewById(R.id.signInProgressBar);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIntoAccouont();
            }
        });
    }

    private void loginIntoAccouont() {

        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constaint.SIGNIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.INVISIBLE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error")) {
                        DynamicToast.makeError(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getBoolean("error") == false) {
                        Log.e("onResponse: ", response.toString());
                        String userName = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        int id = jsonObject.getInt("id");
                        int driver_id = jsonObject.getInt("driver_id");
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(id, userName, email);
                        SharedPrefManager.getInstance(getApplicationContext()).setShopId(driver_id);
                        Constaint.currentUser = new User(id, email, userName);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DynamicToast.makeError(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailET.getText().toString());
                params.put("password", passwordET.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void checkInputs(){

        if (!TextUtils.isEmpty(emailET.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()){
            if (!TextUtils.isEmpty(passwordET.getText().toString()) && passwordET.getText().toString().length()>6){
                signInButton.setEnabled(true);
                signInButton.setTextColor(getResources().getColor(R.color.white));
            } else {
                signInButton.setEnabled(false);
                signInButton.setTextColor(getResources().getColor(R.color.dark_white));
                passwordET.setError("Enter password more than 6 letters");
            }
        } else {
            signInButton.setEnabled(false);
            signInButton.setTextColor(getResources().getColor(R.color.dark_white));
            emailET.setError("Enter a valid email");
        }
    }
}


