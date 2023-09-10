package com.example.lokata.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lokata.Driver.Notification.NotificationAdapter;
import com.example.lokata.Driver.Notification.NotificationGetterSetter;
import com.example.lokata.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv;
    ArrayList<NotificationGetterSetter> notifications;
    NotificationAdapter notificationAdapter;
    ProgressDialog progressDialog;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification, container, false);

        Intent i = getActivity().getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");

        progressDialog= new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("fetching data...");
        progressDialog.show();

        rv = root.findViewById(R.id.notificationRecyclerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        notifications = new ArrayList<NotificationGetterSetter>();
        notificationAdapter = new NotificationAdapter(getContext(), notifications, userLicenseID);

        rv.setAdapter(notificationAdapter);

        getUserNotifications(userLicenseID);

        return root;
    }

    private void getUserNotifications(String userLicenseID) {
        progressDialog.show();
        db.collection("Users")
                .whereEqualTo("licenseID", userLicenseID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                            boolean userNotif = userDoc.getBoolean("userNotif"); // Get the value of "userNotif" field
                            if (userNotif) {
                                db.collection("Users")
                                        .document(userDoc.getId())
                                        .collection("Notification")
                                        .orderBy("notifDate", Query.Direction.DESCENDING)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                notifications.clear();
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    notifications.add(document.toObject(NotificationGetterSetter.class));
                                                }
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                notificationAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "Firestore Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                db.collection("Users")
                                        .document(userDoc.getId())
                                        .collection("Notification")
                                        .orderBy("notifDate", Query.Direction.DESCENDING)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (queryDocumentSnapshots.isEmpty()) {
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(getContext(), "Notifications are disabled for this user", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    notifications.clear();
                                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                        notifications.add(document.toObject(NotificationGetterSetter.class));
                                                    }
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    notificationAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "Firestore Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getContext(), "No user found with email " + userLicenseID, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(), "Firestore Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}