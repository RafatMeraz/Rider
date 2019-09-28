package com.example.rider.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rider.R;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordET, newPasswordET, confirmPasswordET;
    private Button resetButton;
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initialization();
    }

    private void initialization() {
        backButton = findViewById(R.id.changePasswordBackButton);
        currentPasswordET = findViewById(R.id.changePassCurrentPasswordET);
        newPasswordET = findViewById(R.id.ChangePasswordNewPasswordET);
        confirmPasswordET = findViewById(R.id.changePasswordConfirmPasswordET);
        resetButton = findViewById(R.id.changePasswordResetPasswordButton);

        currentPasswordET.addTextChangedListener(new TextWatcher() {
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
        newPasswordET.addTextChangedListener(new TextWatcher() {
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
        confirmPasswordET.addTextChangedListener(new TextWatcher() {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Updating Password...");
        dialog.setMessage("Please Wait a while....");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.UPDATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            JSONObject userResponse = new JSONObject(response);
                            if (userResponse.getBoolean("error")==false) {
                                DynamicToast.makeSuccess(getApplicationContext(), userResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                DynamicToast.makeError(getApplicationContext(), ""+userResponse.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("old_password", currentPasswordET.getText().toString());
                params.put("new_password", newPasswordET.getText().toString());
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void checkInputs(){
        if (!TextUtils.isEmpty(currentPasswordET.getText().toString()) && currentPasswordET.getText().toString().length() > 6){
            if (!TextUtils.isEmpty(newPasswordET.getText().toString()) && newPasswordET.getText().toString().length() > 6){
                if (!TextUtils.isEmpty(confirmPasswordET.getText().toString()) && confirmPasswordET.getText().toString().length() > 6 && confirmPasswordET.getText().toString().equals(newPasswordET.getText().toString())){
                    resetButton.setEnabled(true);
                    resetButton.setTextColor(getResources().getColor(R.color.white));
                } else {
                    resetButton.setEnabled(false);
                    confirmPasswordET.setError("Does not match with new password");
                    resetButton.setTextColor(getResources().getColor(R.color.dark_white));
                }
            } else {
                resetButton.setEnabled(false);
                newPasswordET.setError("Enter your new password more than 6 letters");
                resetButton.setTextColor(getResources().getColor(R.color.dark_white));
            }
        } else {
            resetButton.setEnabled(false);
            currentPasswordET.setError("Enter your current password");
            resetButton.setTextColor(getResources().getColor(R.color.dark_white));
        }
    }

}
