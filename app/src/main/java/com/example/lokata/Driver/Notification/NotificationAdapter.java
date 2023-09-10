package com.example.lokata.Driver.Notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.lokata.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private final String userLicenseID;
    // variable for our array list and context
    private ArrayList<NotificationGetterSetter> notifArrayList;
    private Context context;
    private FirebaseFirestore db;
    private NotificationGetterSetter modal;

    // constructor
    public NotificationAdapter(Context context, ArrayList<NotificationGetterSetter> arrayList, String userLicenseID) {
        this.notifArrayList = arrayList;
        this.context = context;
        this.userLicenseID = userLicenseID;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shownotifications, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        final NotificationGetterSetter modal = notifArrayList.get(position);
        holder.notifDetail.setText(modal.getNotifDetail());
        holder.notifDate.setText(modal.getNotifDate());
        holder.notifImage.setImageBitmap(getNotificationImage(modal.getNotifImage()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are calling an intent.
                /*Intent i = new Intent(context, ViewNotificationDetailsActivity.class);

                // below we are passing all our values.
                i.putExtra("emailGet", userEmail);
                i.putExtra("notifDetail", modal.getNotifDetail());
                i.putExtra("notifDate", modal.getNotifDate());

                // starting our activity.
                context.startActivity(i);*/

                showNotificationDetails(userLicenseID, modal.getNotifDetail(), modal.getNotifDate());
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                showPopupMenu(context, view, modal.getNotifDetail(), position);
            }
        });
    }

    private void showNotificationDetails(String userEmail, String notifDetail, String notifDate) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        NotificationDialogFragment dialogFragment = NotificationDialogFragment.newInstance(userEmail, notifDetail, notifDate);
        dialogFragment.show(fragmentManager, "activity_notification_details_dialog");
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return notifArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notifDetail, notifDate;
        ImageView notifImage;
        AppCompatImageButton menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notifDetail = itemView.findViewById(R.id.notifDetailTextView);
            notifDate = itemView.findViewById(R.id.notifDateTextView);
            notifImage = itemView.findViewById(R.id.notifImageView);
            menu = itemView.findViewById(R.id.menu);
        }
    }


    public Bitmap getNotificationImage (String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
    }

    public void showPopupMenu(Context context, View view, String notifDetail, int position) {
        Intent i = ((Activity) context).getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");

        if (userLicenseID == null || userLicenseID.isEmpty()) {
            Toast.makeText(context, "Invalid license ID", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.remove_menu) {
                    try {
                        db.collection("Users")
                                .whereEqualTo("licenseID", userLicenseID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                                            db.collection("Users")
                                                    .document(userDoc.getId())
                                                    .collection("Notification")
                                                    .whereEqualTo("notifDetail", notifDetail)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                                DocumentSnapshot notifDoc = queryDocumentSnapshots.getDocuments().get(0);
                                                                db.collection("Users")
                                                                        .document(userDoc.getId())
                                                                        .collection("Notification")
                                                                        .document(notifDoc.getId())
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                Toast.makeText(context, "Successfully Deleted Notification!", Toast.LENGTH_SHORT).show();
                                                                                notifArrayList.remove(position);
                                                                                notifyItemRemoved(position);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(context, "Firestore Error", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Firestore Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Delete FAILED!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (Exception e) {
                        Toast.makeText(context, "Firestore Error", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

}
