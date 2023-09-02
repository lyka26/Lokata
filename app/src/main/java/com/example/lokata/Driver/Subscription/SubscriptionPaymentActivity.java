package com.example.lokata.Driver.Subscription;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokata.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubscriptionPaymentActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView SubscriptionTypeTextView, LicenseIDTextView, SubscriptionPriceTextView, TotalPayTextView;
    private ImageView UploadImageView, GoBack;
    private CardView UploadImageCardView;
    private CheckBox PaymentTermsCheckBox;
    private Button PurchaseButton;
    String encodeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_payment);

        Intent i = getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");
        String subscriptionType = i.getStringExtra("subscriptionType");
        String subscriptionPrice = i.getStringExtra("subscriptionPrice");

        SubscriptionTypeTextView = (TextView) findViewById(R.id.SubscriptionTypeTextView);
        LicenseIDTextView = (TextView) findViewById(R.id.LicenseIDTextView);
        SubscriptionPriceTextView = (TextView) findViewById(R.id.SubscriptionPriceTextView);
        TotalPayTextView = (TextView) findViewById(R.id.TotalPayTextView);
        PaymentTermsCheckBox = (CheckBox) findViewById(R.id.PaymentTermsCheckBox);

        GoBack = (ImageView) findViewById(R.id.GoBackImageView);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SubscriptionPaymentActivity.this, SubscriptionActivity.class);
                i.putExtra("licenseIDGet", userLicenseID);
                startActivity(i);
            }
        });

        UploadImageView = (ImageView) findViewById(R.id.UploadImageView);
        UploadImageCardView = (CardView) findViewById(R.id.UploadImageCardView);
        UploadImageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        PurchaseButton = (Button) findViewById(R.id.PurchaseButton);
        PurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserInputs()) {
                    pendingTransaction();
                }
            }
        });

        SubscriptionTypeTextView.setText(subscriptionType);
        LicenseIDTextView.setText(userLicenseID);
        SubscriptionPriceTextView.setText(subscriptionPrice);
        TotalPayTextView.setText(subscriptionPrice);
    }

    // This method is for uploading image
    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            UploadImageView.setImageBitmap(bitmap);
                            encodeImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    // This method will get the subscription details selected from the user from the previous activity
    public void pendingTransaction() {
        Intent intent = getIntent();
        String userLicenseID = intent.getStringExtra("licenseIDGet");

        DocumentReference docRef = db.collection("Users").document(userLicenseID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String userRole = documentSnapshot.getString("userRole");

                // Create a new collection reference for the pending transaction
                DocumentReference transactionRef = db.collection("SubscriptionPaymentApproval").document(userLicenseID);

                // Generate random ID number
                int randomIDNumber = new Random().nextInt(900000) + 100000;
                String subscriptionID = "SUB" + randomIDNumber;

                // Generate transaction number by get the current date and time
                Date now = new Date();
                // Generate a random number between 0 and 999999
                int randomNumber = (int) (Math.random() * 1000000);
                // Format the date and time and combine it with the random number to create the transaction number
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String transactionNumber = dateFormat.format(now) + randomNumber;

                // Set the data for the new document
                Map<String, Object> data = new HashMap<>();
                data.put("transactionID", transactionNumber);
                data.put("subscriptionID", subscriptionID);
                data.put("subscriptionType", SubscriptionTypeTextView.getText().toString());
                data.put("price", SubscriptionPriceTextView.getText().toString());
                data.put("paymentFile", encodeImage);
                data.put("userLicenseID", userLicenseID);
                data.put("firstName", firstName);
                data.put("lastName", lastName);
                data.put("userRole", userRole);

                // Save the pending transaction data
                transactionRef.set(data)
                        .addOnSuccessListener(aVoid -> Log.d("DEBUG", "Pending transaction data saved successfully"))
                        .addOnFailureListener(e -> Log.e("DEBUG", "Error saving pending transaction data", e));

                Toast.makeText(getApplicationContext(), "Please wait for us to validate your transaction.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(SubscriptionPaymentActivity.this, SubscriptionActivity.class);
                i.putExtra("licenseIDGet", userLicenseID);
                startActivity(i);
            }
        });
    }

    // Validate User Inputs
    public boolean validateUserInputs() {
        if (!PaymentTermsCheckBox.isChecked()) {
            // Checkbox is not checked, show error message
            Toast.makeText(this, "Please check the terms to proceed your purchase.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get the drawable from the ImageView
        Drawable drawable = UploadImageView.getDrawable();
        // Check if the drawable is null or not an instance of BitmapDrawable
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            Toast.makeText(getApplicationContext(), "Image is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}