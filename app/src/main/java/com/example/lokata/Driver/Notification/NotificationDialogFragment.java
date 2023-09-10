package com.example.lokata.Driver.Notification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokata.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationDialogFragment extends DialogFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static NotificationDialogFragment newInstance(String userLicenseID, String notifDetail, String notifDate) {
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("userLicenseID", userLicenseID);
        args.putString("notifDetail", notifDetail);
        args.putString("notifDate", notifDate);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_dialog, null);

        TextView detailTextView = view.findViewById(R.id.notificationDetailTextView);
        TextView dateTextView = view.findViewById(R.id.notificationDateTextView);

        // Get the notification ID from arguments
        String notificationDetail = getArguments().getString("notifDetail");
        // Retrieve the userLicenseID from arguments
        String userLicenseID = getArguments().getString("userLicenseID");

        // Retrieve the notification data from Firestore
        Query notificationQuery = db.collection("Users")
                .document(userLicenseID)
                .collection("Notification")
                .whereEqualTo("notifDetail", notificationDetail)
                .limit(1);

        notificationQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        // Map the Firestore document data to NotificationGetterSetter object
                        NotificationGetterSetter notification = document.toObject(NotificationGetterSetter.class);

                        // Set the notification details in the dialog
                        detailTextView.setText(notification.getNotifDetail());
                        dateTextView.setText(notification.getNotifDate());
                    }
                } else {
                    // Handle the case when fetching data from Firestore fails
                    // You can show an error message or perform any required error handling here
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }
}
