package com.example.lokata.Driver.Subscription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokata.Driver.DriverActivity;
import com.example.lokata.Fragment.MenuFragment;
import com.example.lokata.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SubscriptionActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView PremiumSubscriptionButton, StandardSubscriptionButton, StarterSubscriptionButton;
    private TextView UserNameTextView, PremiumSubscriptionTypeTextView, PremiumSubscriptionPriceTextView, StandardSubscriptionTypeTextView, StandardSubscriptionPriceTextView, StarterSubscriptionTypeTextView, StarterSubscriptionPriceTextView;
    private ImageView GoBackImageView, ProfileImageView;
    private Button ViewMySubscriptionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Intent i = getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");

        getUserDetails();

        ProfileImageView = findViewById(R.id.profileImageView);
        UserNameTextView = findViewById(R.id.UserNameTextView);

        PremiumSubscriptionTypeTextView = (TextView) findViewById(R.id.PremiumSubscriptionTypeTextView);
        PremiumSubscriptionPriceTextView = (TextView) findViewById(R.id.PremiumSubscriptionPriceTextView);
        StandardSubscriptionTypeTextView = (TextView) findViewById(R.id.StandardSubscriptionTypeTextView);
        StandardSubscriptionPriceTextView = (TextView) findViewById(R.id.StandardSubscriptionPriceTextView);
        StarterSubscriptionTypeTextView = (TextView) findViewById(R.id.StarterSubscriptionTypeTextView);
        StarterSubscriptionPriceTextView = (TextView) findViewById(R.id.StarterSubscriptionPriceTextView);

        GoBackImageView = (ImageView) findViewById(R.id.GoBackImageView);
        GoBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SubscriptionActivity.this, DriverActivity.class);
                i.putExtra("licenseIDGet", userLicenseID);
                startActivity(i);
            }
        });

        ViewMySubscriptionButton = (Button) findViewById(R.id.ViewMySubscriptionButton);
        ViewMySubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String licenseID = getIntent().getStringExtra("licenseIDGet");
                Intent i = new Intent(SubscriptionActivity.this, CurrentSubscriptionActivity.class);
                i.putExtra("licenseIDGet", licenseID);
                startActivity(i);
            }
        });

        PremiumSubscriptionButton = (TextView) findViewById(R.id.PremiumSubscriptionButton);
        PremiumSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                premiumSubscription();

                // Set the text color of the button to sky blue
                PremiumSubscriptionButton.setTextColor(getResources().getColor(R.color.sky_blue));

                // Disable the button
                PremiumSubscriptionButton.setEnabled(false);

                // Set the other buttons' backgrounds to cust_btn.xml and enable them
                StandardSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                StandardSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                StandardSubscriptionButton.setEnabled(true);

                StarterSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                StarterSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                StarterSubscriptionButton.setEnabled(true);

                // Refresh the activity
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

        StandardSubscriptionButton = (TextView) findViewById(R.id.StandardSubscriptionButton);
        StandardSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                standardSubscription();

                // Set the text color of the button to sky blue
                StandardSubscriptionButton.setTextColor(getResources().getColor(R.color.sky_blue));

                // Disable the button
                StandardSubscriptionButton.setEnabled(false);

                // Set the other buttons' backgrounds to cust_btn.xml and enable them
                PremiumSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                PremiumSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                PremiumSubscriptionButton.setEnabled(true);

                StarterSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                StarterSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                StarterSubscriptionButton.setEnabled(true);

                // Refresh the activity
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

        StarterSubscriptionButton = (TextView) findViewById(R.id.StarterSubscriptionButton);
        StarterSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starterSubscription();

                // Set the text color of the button to sky blue
                StarterSubscriptionButton.setTextColor(getResources().getColor(R.color.sky_blue));

                // Disable the button
                StarterSubscriptionButton.setEnabled(false);

                // Set the other buttons' backgrounds to cust_btn.xml and enable them
                PremiumSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                PremiumSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                PremiumSubscriptionButton.setEnabled(true);

                StandardSubscriptionButton.setTextColor(getResources().getColor(R.color.black));
                StandardSubscriptionButton.setBackgroundResource(R.drawable.cust_btn);
                StandardSubscriptionButton.setEnabled(true);

                // Refresh the activity
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });

    }

    public void premiumSubscription() {
        Intent i = getIntent();
        String licenseID = i.getStringExtra("licenseIDGet");

        // Query the Firestore collection for a user with a specific license ID
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docRef = usersRef.document(licenseID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        // Get the data as a Map
                        Map<String, Object> data = snapshot.getData();
                        // Access the specific field you want
                        String userLicenseID = data.get("licenseID").toString();
                        String subscriptionType = PremiumSubscriptionTypeTextView.getText().toString();
                        String subscriptionPrice = PremiumSubscriptionPriceTextView.getText().toString();

                        // Create an Intent to start the new activity and pass the subscription data
                        Intent i = new Intent(SubscriptionActivity.this, SubscriptionPaymentActivity.class);
                        i.putExtra("subscriptionType", subscriptionType);
                        i.putExtra("subscriptionPrice", subscriptionPrice);
                        i.putExtra("licenseIDGet", userLicenseID);
                        startActivity(i);
                    }
                } else {
                    Log.d("DEBUG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void standardSubscription() {
        Intent i = getIntent();
        String licenseID = i.getStringExtra("licenseIDGet");

        // Query the Firestore collection for a user with a specific license ID
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docRef = usersRef.document(licenseID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        // Get the data as a Map
                        Map<String, Object> data = snapshot.getData();
                        // Access the specific field you want
                        String userLicenseID = data.get("licenseID").toString();
                        String subscriptionType = StandardSubscriptionTypeTextView.getText().toString();
                        String subscriptionPrice = StandardSubscriptionPriceTextView.getText().toString();

                        // Create an Intent to start the new activity and pass the subscription data
                        Intent i = new Intent(SubscriptionActivity.this, SubscriptionPaymentActivity.class);
                        i.putExtra("subscriptionType", subscriptionType);
                        i.putExtra("subscriptionPrice", subscriptionPrice);
                        i.putExtra("licenseIDGet", userLicenseID);
                        startActivity(i);
                    }
                } else {
                    Log.d("DEBUG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void starterSubscription() {
        Intent i = getIntent();
        String licenseID = i.getStringExtra("licenseIDGet");

        // Query the Firestore collection for a user with a specific email address
        CollectionReference usersRef = db.collection("Users");
        DocumentReference docRef = usersRef.document(licenseID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        // Get the data as a Map
                        Map<String, Object> data = snapshot.getData();
                        // Access the specific field you want
                        String licenseID = data.get("licenseID").toString();
                        String subscriptionType = StarterSubscriptionTypeTextView.getText().toString();
                        String subscriptionPrice = StarterSubscriptionPriceTextView.getText().toString();

                        // Create an Intent to start the new activity and pass the subscription data
                        Intent i = new Intent(SubscriptionActivity.this, SubscriptionPaymentActivity.class);
                        i.putExtra("subscriptionType", subscriptionType);
                        i.putExtra("subscriptionPrice", subscriptionPrice);
                        i.putExtra("userLicenseIDGet", licenseID);
                        startActivity(i);
                    }
                } else {
                    Log.d("DEBUG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void getUserDetails() {
        Intent i = getIntent();
        String licenseID = i.getStringExtra("licenseIDGet");

        // Query the Firestore collection for a user with a specific email address
        DocumentReference docRef = db.collection("Users").document(licenseID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String firstName = snapshot.getString("firstName");
                        String driverImageUrl = snapshot.getString("driverImage");

                        UserNameTextView.setText(firstName);

                        if (driverImageUrl != null) {
                            byte[] shopImageBytes = Base64.decode(driverImageUrl, Base64.DEFAULT);
                            Bitmap shopImageBitmap = BitmapFactory.decodeByteArray(shopImageBytes, 0, shopImageBytes.length);
                            ProfileImageView.setImageBitmap(shopImageBitmap);
                        }


                    }
                }
            }
        });
    }
}