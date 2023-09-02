package com.example.lokata.Generic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lokata.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userRole = "Driver";
    private TextInputLayout FirstNameInput, MiddleNameInput, LastNameInput, EmailInput, LicenseIDInput, MobileNumberInput, AddressInput, PostalCodeInput, PasswordInput, ConfirmationPasswordInput;
    private TextInputEditText FirstNameText, MiddleNameText, LastNameText, EmailText, LicenseIDText, MobileNumberText, AddressText, PostalCodeText, PasswordText, ConfirmationPasswordText;
    private Button RegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // set helper inout error for layout style outline box
        FirstNameInput = (TextInputLayout) findViewById(R.id.FirstNameInput);
        MiddleNameInput = (TextInputLayout) findViewById(R.id.MiddleNameInput);
        LastNameInput = (TextInputLayout) findViewById(R.id.LastNameInput);
        EmailInput = (TextInputLayout) findViewById(R.id.EmailInput);
        LicenseIDInput = (TextInputLayout) findViewById(R.id.LicenseIDInput);
        MobileNumberInput = (TextInputLayout) findViewById(R.id.MobileNumberInput);
        AddressInput = (TextInputLayout) findViewById(R.id.AddressInput);
        PostalCodeInput = (TextInputLayout) findViewById(R.id.PostalCodeInput);
        PasswordInput = (TextInputLayout) findViewById(R.id.PasswordInput);
        ConfirmationPasswordInput = (TextInputLayout) findViewById(R.id.ConfirmationPasswordInput);

        // get the text data from the views
        FirstNameText = (TextInputEditText) findViewById(R.id.FirstNameText);
        MiddleNameText = (TextInputEditText) findViewById(R.id.MiddleNameText);
        LastNameText = (TextInputEditText) findViewById(R.id.LastNameText);
        EmailText = (TextInputEditText) findViewById(R.id.EmailText);
        LicenseIDText = (TextInputEditText) findViewById(R.id.LicenseIDText);
        MobileNumberText = (TextInputEditText) findViewById(R.id.MobileNumberText);
        AddressText = (TextInputEditText) findViewById(R.id.AddressText);
        PostalCodeText = (TextInputEditText) findViewById(R.id.PostalCodeText);
        PasswordText = (TextInputEditText) findViewById(R.id.PasswordText);
        ConfirmationPasswordText = (TextInputEditText) findViewById(R.id.ConfirmationPasswordText);
        RegistrationButton = (Button) findViewById(R.id.RegisterButton);

        RegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Users")
                        .whereEqualTo("email", EmailText.getText().toString().trim())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()) {
                                        // Email is already registered
                                        EmailInput.setHelperText("Email is already registered");
                                        EmailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                                        EmailInput.setBoxStrokeColor(getResources().getColor(R.color.red));
                                    } else {
                                        db.collection("Users")
                                                .whereEqualTo("licenseID", LicenseIDText.getText().toString().trim())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            if (!querySnapshot.isEmpty()) {
                                                                // LicenseID is already registered
                                                                LicenseIDInput.setHelperText("License ID is already registered");
                                                                LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                                                                LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.red));
                                                            } else {
                                                                if(validateUserInputs()){
                                                                    registerDriver(EmailText.getText().toString(), userRole);
                                                                }
                                                            }
                                                        } else {
                                                            Log.d("DEBUGG", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("DEBUGG", "Error getting documents: ", task.getException());
                                }
                            }
                        });

                // Reset helper text and hint color for email input field
                EmailInput.setHelperText(null);
                EmailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
                EmailInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));

                // Reset helper text and hint color for license ID input field
                LicenseIDInput.setHelperText(null);
                LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
                LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
            }
        });
    }

    // register method
    public void registerDriver(String EmailText, String userRole) {
        String email = EmailText;
        String defaultImage = "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";

        // Create a new collection reference
        DocumentReference documentRef = db.collection("Users").document(LicenseIDText.getText().toString());

        // Set the data for the new document
        Map<String, Object> data = new HashMap<>();
        data.put("licenseID", LicenseIDText.getText().toString());
        data.put("firstName", capitalize(FirstNameText.getText().toString()));
        data.put("middleName", capitalize(MiddleNameText.getText().toString()));
        data.put("lastName", capitalize(LastNameText.getText().toString()));
        data.put("email", email);
        data.put("mobileNumber", MobileNumberText.getText().toString());
        data.put("address", AddressText.getText().toString());
        data.put("postalCode", PostalCodeText.getText().toString());
        data.put("password", PasswordText.getText().toString());
        data.put("confirmPassword", ConfirmationPasswordText.getText().toString());
        data.put("userRole", userRole);
        data.put("driverImage", defaultImage);
        data.put("userNotif", true);
        data.put("verified", false);

        // Set the data in the Firestore document
        documentRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    defaultSubscription(EmailText, userRole);

                    Toast.makeText(getApplicationContext(), "Account successfully registered!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, RegistrationActivity.class));
                    Log.d("REGISTER", "Successfully Registered!");
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error registering account: " + e.getMessage(), Toast.LENGTH_LONG).show());

    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    private void defaultSubscription(String EmailText, String userRole) {
        String licenseID = LicenseIDText.getText().toString();
        String subscriptionType = "Free Subscription";
        String subscriptionPrice = "0.00";
        String currentDate = "n/a";
        String expirationDate = "n/a";
        String status = "Active";

        // Generate random ID number
        int randomIDNumber = new Random().nextInt(900000) + 100000;
        String subscriptionID = "SUB" + randomIDNumber;

        // Create a new collection reference
        DocumentReference documentRef = db.collection("ManageSubscription").document(licenseID);

        // Set the data for the new document
        Map<String, Object> dataInsert = new HashMap<>();
        dataInsert.put("subscriptionID", subscriptionID);
        dataInsert.put("subscriptionType", subscriptionType);
        dataInsert.put("price", subscriptionPrice);
        dataInsert.put("userLicenseID", licenseID);
        dataInsert.put("userRole", userRole);
        dataInsert.put("startDate", currentDate);
        dataInsert.put("endDate", expirationDate);
        dataInsert.put("status", status);

        // Set the data in the Firestore document
        documentRef.set(dataInsert);
    }

    // Validate User Inputs
    public boolean validateUserInputs() {
        // Validation patterns
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;

        // check if all required fields are filled
        if (FirstNameText.getText().toString().trim().isEmpty()) {
            FirstNameInput.setHelperText("Field can't be empty");
            FirstNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            FirstNameInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            FirstNameInput.setHelperText(null);
            FirstNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            FirstNameInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (MiddleNameText.getText().toString().trim().isEmpty()) {
            MiddleNameInput.setHelperText("Field can't be empty");
            MiddleNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            MiddleNameInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            MiddleNameInput.setHelperText(null);
            MiddleNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            MiddleNameInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (LastNameText.getText().toString().trim().isEmpty()) {
            LastNameInput.setHelperText("Field can't be empty");
            LastNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            LastNameInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            LastNameInput.setHelperText(null);
            LastNameInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            LastNameInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (EmailText.getText().toString().trim().isEmpty()) {
            EmailInput.setHelperText("Field can't be empty");
            EmailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            EmailInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (!emailPattern.matcher(EmailText.getText().toString().trim()).matches()) {
            EmailInput.setHelperText("Invalid email");
            EmailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            EmailInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            EmailInput.setHelperText(null);
            EmailInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            EmailInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (TextUtils.isEmpty(LicenseIDText.getText().toString().trim())) {
            LicenseIDInput.setHelperText("Field can't be empty");
            LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (LicenseIDText.getText().toString().contains(" ")) {
            LicenseIDInput.setHelperText("Spaces are not allowed");
            LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            LicenseIDInput.setHelperText(null);
            LicenseIDInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            LicenseIDInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (MobileNumberText.getText().toString().trim().isEmpty()) {
            MobileNumberInput.setHelperText("Field can't be empty");
            MobileNumberInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            MobileNumberInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (!MobileNumberText.getText().toString().trim().matches("\\+63\\d{10}")) {
            MobileNumberInput.setHelperText("Invalid phone number");
            MobileNumberInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            MobileNumberInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            MobileNumberInput.setHelperText(null);
            MobileNumberInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            MobileNumberInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (AddressText.getText().toString().trim().isEmpty()) {
            AddressInput.setHelperText("Field can't be empty");
            AddressInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            AddressInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            AddressInput.setHelperText(null);
            AddressInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            AddressInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        if (PostalCodeText.getText().toString().trim().isEmpty()) {
            PostalCodeInput.setHelperText("Field can't be empty");
            PostalCodeInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            PostalCodeInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            PostalCodeInput.setHelperText(null);
            PostalCodeInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            PostalCodeInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
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

        if (ConfirmationPasswordText.getText().toString().trim().isEmpty()) {
            ConfirmationPasswordInput.setHelperText("Field can't be empty");
            ConfirmationPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            ConfirmationPasswordInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (ConfirmationPasswordText.getText().toString().contains(" ")) {
            ConfirmationPasswordInput.setHelperText("Spaces are not allowed");
            ConfirmationPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            ConfirmationPasswordInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else if (!ConfirmationPasswordText.getText().toString().equals(PasswordText.getText().toString())) {
            ConfirmationPasswordInput.setHelperText("Password does not match");
            ConfirmationPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            ConfirmationPasswordInput.setBoxStrokeColor(getResources().getColor(R.color.red));
            return false;
        } else {
            ConfirmationPasswordInput.setHelperText(null);
            ConfirmationPasswordInput.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.sky_blue)));
            ConfirmationPasswordInput.setBoxStrokeColor(getResources().getColor(R.color.sky_blue));
        }

        // if all validation checks pass, return true
        return true;
    }
}