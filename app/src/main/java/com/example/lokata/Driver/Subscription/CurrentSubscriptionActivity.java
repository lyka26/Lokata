package com.example.lokata.Driver.Subscription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokata.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CurrentSubscriptionActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView MySubscriptionTypeTextView, StartDateTextView, EndDateTextView, SubscriptionStatusTextView;
    private Button CancelSubscriptionButton;
    private ImageView GoBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_subscription);

        Intent intent = getIntent();
        String userLicenseID = intent.getStringExtra("licenseIDGet");

        getCurrentSubDetails();

        MySubscriptionTypeTextView = (TextView) findViewById(R.id.MySubscriptionTypeTextView);
        StartDateTextView = (TextView) findViewById(R.id.StartDateTextView);
        EndDateTextView = (TextView) findViewById(R.id.EndDateTextView);
        SubscriptionStatusTextView = (TextView) findViewById(R.id.SubscriptionStatusTextView);

        GoBack = (ImageView) findViewById(R.id.GoBackImageView);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CurrentSubscriptionActivity.this, SubscriptionActivity.class);
                i.putExtra("licenseIDGet", userLicenseID );
                startActivity(i);
            }
        });

        CancelSubscriptionButton = (Button) findViewById(R.id.CancelSubscriptionButton);
        CancelSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSubscription();

                // Refresh the activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    public void getCurrentSubDetails() {
        Intent intent = getIntent();
        String userLicenseID = intent.getStringExtra("licenseIDGet");

        final String defaultSubscription = "Free Subscription";

        // Query the Firestore collection for a user with a specific license ID
        DocumentReference docRef = db.collection("ManageSubscription").document(userLicenseID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String subscriptionType = snapshot.getString("subscriptionType");
                        String startDate = snapshot.getString("startDate");
                        String endDate = snapshot.getString("endDate");
                        String status = snapshot.getString("status");

                        MySubscriptionTypeTextView.setText(subscriptionType);
                        StartDateTextView.setText(startDate);
                        EndDateTextView.setText(endDate);
                        SubscriptionStatusTextView.setText(status);

                        if(subscriptionType.equals(defaultSubscription)){
                            // Set the button background to cust_outline_cancel_btn.xml
                            CancelSubscriptionButton.setBackgroundResource(R.drawable.cust_outline_cancel_btn);

                            // Set the text color of the button to red
                            CancelSubscriptionButton.setTextColor(getResources().getColor(R.color.red));

                            // Disable the button
                            CancelSubscriptionButton.setEnabled(false);
                        }else{
                            // Set the button background to cust_outline_cancel_btn.xml
                            CancelSubscriptionButton.setBackgroundResource(R.drawable.cust_btn_cancel);

                            // Set the text color of the button to red
                            CancelSubscriptionButton.setTextColor(getResources().getColor(R.color.white));

                            // Disable the button
                            CancelSubscriptionButton.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    public void cancelSubscription() {
        Intent intent = getIntent();
        String userLicenseID = intent.getStringExtra("licenseIDGet");

        String subscriptionType = "Free Subscription";
        String subscriptionPrice = "0.00";
        String status = "Cancelled";

        CollectionReference usersRef = db.collection("Users");
        DocumentReference docRef = usersRef.document(userLicenseID);

        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String userRole = snapshot.getString("userRole");

                DocumentReference subscriptionRef = db.collection("AdminManageSubscription").document(userLicenseID);

                subscriptionRef.get().addOnSuccessListener(subscriptionSnapshot -> {
                    Map<String, Object> data;

                    if (subscriptionSnapshot.exists()) {
                        data = new HashMap<>();
                        data.put("subscriptionType", subscriptionType);
                        data.put("price", subscriptionPrice);
                        data.put("startDate", "n/a");
                        data.put("endDate", "n/a");
                        data.put("status", status);

                        subscriptionRef.update(data).addOnSuccessListener(aVoid -> {
                            // Redirect to the same page to apply the changes
                            startActivity(getIntent());
                        }).addOnFailureListener(e -> {
                            // Error message
                        });
                    } else {
                        data = new HashMap<>();
                        data.put("subscriptionType", subscriptionType);
                        data.put("price", subscriptionPrice);
                        data.put("userEmail", userLicenseID);
                        data.put("userRole", userRole);
                        data.put("startDate", "n/a");
                        data.put("endDate", "n/a");
                        data.put("status", status);

                        // Update the data in the Firestore document
                        subscriptionRef.set(data).addOnSuccessListener(aVoid -> {
                            // Redirect to the same page to apply the changes
                            startActivity(getIntent());
                        }).addOnFailureListener(e -> {
                            // Error message
                        });
                    }
                }).addOnFailureListener(e -> {
                    // Error message
                });
            } else {
                // Error message
            }
        }).addOnFailureListener(e -> {
            // Error message
        });
    }
}