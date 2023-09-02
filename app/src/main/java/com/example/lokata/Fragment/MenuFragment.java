package com.example.lokata.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lokata.Driver.Profile.ProfileActivity;
import com.example.lokata.Driver.Subscription.SubscriptionActivity;
import com.example.lokata.Generic.LogInActivity;
import com.example.lokata.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout ProfileLinearLayout;
    private ImageView UserImageView;
    private TextView UserFullNameTextView;
    private CardView SubscriptionCardView, EditProfileCardView, ChangePasswordCardView, NotificationCardView, LogoutCardView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // In your fragment's onCreateView() method
        // Assuming that your fragment is attached to a ShopOwnerHomepage activity
        Intent i = getActivity().getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");

        getUserProfile(userLicenseID);

        // Initialize your fields
        ProfileLinearLayout = (LinearLayout) view.findViewById(R.id.ProfileLinearLayout);
        ProfileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("licenseIDGet", userLicenseID);
                startActivity(i);
            }
        });

        UserImageView = (ImageView) view.findViewById(R.id.UserImageView);
        UserFullNameTextView = view.findViewById(R.id.UserFullNameTextView);

        SubscriptionCardView = (CardView) view.findViewById(R.id.SubscriptionCardView);
        SubscriptionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SubscriptionActivity.class);
                i.putExtra("licenseIDGet", userLicenseID);
                startActivity(i);
            }
        });

        EditProfileCardView = (CardView) view.findViewById(R.id.EditProfileCardView);
        EditProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ChangePasswordCardView = (CardView) view.findViewById(R.id.ChangePasswordCardView);
        ChangePasswordCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        NotificationCardView = (CardView) view.findViewById(R.id.NotificationCardView);
        NotificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LogoutCardView = (CardView) view.findViewById(R.id.LogoutCardView);
        LogoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LogInActivity.class));
                getActivity().finish();
            }
        });


        return view;
    }

    public void getUserProfile(String userLicenseID) {
        DocumentReference docRef = db.collection("Users").document(userLicenseID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String middleName = documentSnapshot.getString("middleName");
                String lastName = documentSnapshot.getString("lastName");
                String driverImageUrl = documentSnapshot.getString("driverImage");

                UserFullNameTextView.setText(firstName + " " + middleName + " " + lastName);

                if (driverImageUrl != null) {
                    byte[] shopImageBytes = Base64.decode(driverImageUrl, Base64.DEFAULT);
                    Bitmap shopImageBitmap = BitmapFactory.decodeByteArray(shopImageBytes, 0, shopImageBytes.length);
                    UserImageView.setImageBitmap(shopImageBitmap);
                }
            }
        });
    }
}