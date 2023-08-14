package com.example.lokata.Generic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokata.DashboardActivity;
import com.example.lokata.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextInputLayout LicenseIDInput, PasswordInput;
    private TextInputEditText LicenseIDText, PasswordText;
    private Button LoginButton;
    private TextView ForgotPasswordTextView, RegistrationTextView;
    String userLicenseID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // set helper text error for layout style outline box
        LicenseIDInput = (TextInputLayout) findViewById(R.id.LicenseIDInput);
        PasswordInput = (TextInputLayout) findViewById(R.id.PasswordInput);

        // get the input data from the views
        LicenseIDText = (TextInputEditText) findViewById(R.id.LicenseIDText);
        PasswordText = (TextInputEditText) findViewById(R.id.PasswordText);

        ForgotPasswordTextView = (TextView) findViewById(R.id.ForgotPasswordTextView);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        RegistrationTextView = (TextView) findViewById(R.id.UserRegisterTextView);

        ForgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(LogInActivity.this, AccountRecovery.class));*/
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateUserInputs()){
                    checkUserEmail();
                    getDeviceToken();
                }
            }
        });

        RegistrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void checkUserEmail(){
        // Query the Firestore collection for a user with a specific license ID and password
        CollectionReference usersRef = db.collection("RegistrationApproval");
        Query query = usersRef.whereEqualTo("licenseID", LicenseIDText.getText().toString())
                .whereEqualTo("password", PasswordText.getText().toString());
        Task<QuerySnapshot> querySnapshotTask = query.get();

        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Check if the snapshot is empty
                    if (task.getResult().size() == 0) {
                        // Handle the case where no user was found with the provided email and password
                        getLoginDetails();
                    } else {
                        // Iterate over the results to find the user
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Toast.makeText(getApplicationContext(), "Your account is currently awaiting approval! Please wait for a while.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                } else {
                    // Handle the case where there was an error executing the query
                    Toast.makeText(getApplicationContext(), "Error retrieving user information.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // login a user with the following credentials such as email and password
    private void getLoginDetails() {
        // Query the Firestore collection for a user with a specific license ID and password
        CollectionReference usersRef = db.collection("Users");
        Query query = usersRef.whereEqualTo("licenseID", LicenseIDText.getText().toString())
                .whereEqualTo("password", PasswordText.getText().toString());
        Task<QuerySnapshot> querySnapshotTask = query.get();

        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Check if the snapshot is empty
                    if (task.getResult().size() == 0) {
                        // Handle the case where no user was found with the provided license ID and password
                        Toast.makeText(getApplicationContext(), "Invalid license ID or password.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Iterate over the results to find the user
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                //Verified status from the document
                                boolean verifiedFieldValue = document.getBoolean("verified");

                                // Check if the user has been verified
                                if (!verifiedFieldValue) {
                                    Toast.makeText(getApplicationContext(), "We noticed your account has not been verified! Please verify your account to be able to login.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent;
                                // Start the Home Page activity
                                intent = new Intent(LogInActivity.this, DashboardActivity.class);
                                intent.putExtra("licenseIDGet", userLicenseID);
                                startActivity(intent);
                                return;
                            }
                        }

                        // Handle the case where no document was found with the provided email and password
                        Toast.makeText(getApplicationContext(), "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where there was an error executing the query
                    Toast.makeText(getApplicationContext(), "Error retrieving user information.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate User Inputs
    public boolean validateUserInputs(){
        // check if all required fields are filled
        if (LicenseIDText.getText().toString().trim().isEmpty()) {
            LicenseIDInput.setHelperText("Field can't be empty");
            LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            LicenseIDInput.setHelperText(null);
            LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (PasswordText.getText().toString().trim().isEmpty()) {
            PasswordInput.setHelperText("Field can't be empty");
            PasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            PasswordInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (PasswordText.getText().toString().contains(" ")) {
            PasswordInput.setHelperText("Spaces are not allowed");
            PasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            PasswordInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            PasswordInput.setHelperText(null);
            PasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            PasswordInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        // if all validation checks pass, return true
        return true;
    }

    public void getDeviceToken(){
        //Get device token id
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        /*System.out.println("TOKEN: " + token);*/

                        // Assuming you have the license ID and token values
                        String licenseID = LicenseIDText.getText().toString();
                        String storeToken = token;

                        // Access Firestore instance
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Create a new field and store the token
                        Map<String, Object> data = new HashMap<>();
                        data.put("token", storeToken);

                        db.collection("Users")
                                .document(licenseID)
                                .update(data)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("New field added successfully in Firestore.");
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Error adding new field in Firestore: " + e.getMessage());
                                });
                    }
                });
    }
}