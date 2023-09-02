package com.example.lokata.Driver.Profile;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokata.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView ProfileImage;
    private TextView FullNameText, LicenseIDText, FirstNameText, MiddleNameText, LastNameText, MobileNumberText, EmailText, AddressText, PostalCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent i = getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");

        getUserProfile(userLicenseID);

        ProfileImage = findViewById(R.id.profileImageView);
        FullNameText = findViewById(R.id.fullNameTextView);

        // Initialize your Textview fields
        LicenseIDText = findViewById(R.id.licenseIDTextView);
        FirstNameText = findViewById(R.id.firstNameText);
        MiddleNameText = findViewById(R.id.middleNameText);
        LastNameText = findViewById(R.id.lastNameText);
        MobileNumberText = findViewById(R.id.mobileNumberText);
        EmailText = findViewById(R.id.emailText);
        AddressText = findViewById(R.id.addressText);
        PostalCodeText = findViewById(R.id.postalCodeText);
    }

    public void getUserProfile(String userLicenseID) {
        if (userLicenseID == null || userLicenseID.isEmpty()) {
            Log.d("getUserProfile", "Null License ID");
            return;
        }

        DocumentReference docRef = db.collection("Users").document(userLicenseID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String licenseID = documentSnapshot.getString("licenseID");
                String firstName = documentSnapshot.getString("firstName");
                String middleName = documentSnapshot.getString("middleName");
                String lastName = documentSnapshot.getString("lastName");
                String mobileNumber = documentSnapshot.getString("mobileNumber");
                String email = documentSnapshot.getString("email");
                String address = documentSnapshot.getString("address");
                String postalCode = documentSnapshot.getString("postalCode");
                String driverImageUrl = documentSnapshot.getString("driverImage");

                FullNameText.setText(firstName + " "  + middleName + " " + lastName);
                LicenseIDText.setText(licenseID);
                FirstNameText.setText(firstName);
                MiddleNameText.setText(middleName);
                LastNameText.setText(lastName);
                MobileNumberText.setText(mobileNumber);
                EmailText.setText(email);
                AddressText.setText(address);
                PostalCodeText.setText(postalCode);

                if (driverImageUrl != null) {
                    byte[] shopImageBytes = Base64.decode(driverImageUrl, Base64.DEFAULT);
                    Bitmap shopImageBitmap = BitmapFactory.decodeByteArray(shopImageBytes, 0, shopImageBytes.length);
                    ProfileImage.setImageBitmap(shopImageBitmap);
                }
            }
        });
    }
}