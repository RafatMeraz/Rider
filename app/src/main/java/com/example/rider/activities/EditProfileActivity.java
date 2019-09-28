package com.example.rider.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.example.rider.model.User;
import com.example.rider.utils.Constaint;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText nameET, emailET, phoneET, address1ET, address2ET;
    private Button saveButton;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = (User) getIntent().getSerializableExtra("user");

        initialization();
    }
    private void initialization() {
        nameET = findViewById(R.id.editProfileFullNameET);
        emailET = findViewById(R.id.editProfileEmailET);
        phoneET = findViewById(R.id.editProfilePhoneET);
        address1ET = findViewById(R.id.editProfileAddress1ET);
        address2ET = findViewById(R.id.editProfileAddress2ET);
        backButton = findViewById(R.id.editProfileBackButton);
        saveButton = findViewById(R.id.editProfileSaveButton);

        nameET.addTextChangedListener(new TextWatcher() {
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
        phoneET.addTextChangedListener(new TextWatcher() {
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
        address1ET.addTextChangedListener(new TextWatcher() {
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

        nameET.setText(currentUser.getName());
        emailET.setText(currentUser.getEmail());
        phoneET.setText(currentUser.getPhone());
        address1ET.setText(currentUser.getAddress1());

        if (currentUser.getAddress2()=="null")
            address2ET.setText("");
        else
            address2ET.setText(currentUser.getAddress2());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(nameET.getText().toString())){
            if (!TextUtils.isEmpty(emailET.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
                if (!TextUtils.isEmpty(phoneET.getText().toString())){
                    if (!TextUtils.isEmpty(address1ET.getText().toString())){
                        saveButton.setEnabled(true);
                    }else {
                        saveButton.setEnabled(false);
                        address1ET.setError("Enter your password more than 6 characters.");
                    }
                } else {
                    saveButton.setEnabled(false);
                    phoneET.setError("Enter your password more than 6 characters.");
                }
            } else {
                saveButton.setEnabled(false);
                emailET.setError("Enter your valid email address.");
            }
        } else {
            saveButton.setEnabled(false);
            nameET.setError("Enter your name");
        }
    }

    private void updateUser() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Updating Profile...");
        dialog.setMessage("Please Wait a while....");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constaint.UPDATE_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            JSONObject userResponse = new JSONObject(response);
                            if (userResponse.getBoolean("error") == false) {
                                DynamicToast.makeSuccess(getApplicationContext(), "Update Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                DynamicToast.makeError(getApplicationContext(), "" + userResponse.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("id", String.valueOf(currentUser.getId()));
                params.put("name", nameET.getText().toString());
                params.put("phone_number", phoneET.getText().toString());
                params.put("addressl1", address1ET.getText().toString());
                if (!TextUtils.isEmpty(address2ET.getText().toString()))
                    params.put("addressl2", address2ET.getText().toString());
                params.put("email", emailET.getText().toString());
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

}
