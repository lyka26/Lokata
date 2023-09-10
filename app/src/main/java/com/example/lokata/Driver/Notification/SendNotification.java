package com.example.lokata.Driver.Notification;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// This is a class for sending notifications using Firebase Cloud Messaging (FCM)
public class SendNotification {
    // The URL for sending notifications using FCM
    private static final String Base_Url = "https://fcm.googleapis.com/fcm/send";

    // The server key that is used for authorization
    private static final String SERVER_KEY = "key=AAAA_ML7o9A:APA91bEzyQ93NhjjxzRz_U1M_Em1YI_BZhFjSj21GYZHvhF2Rh1ybZAOlwCHB4yiGyioC7nL4KWyM7GKTpd6snRuV31COCpBLAKniQ4CY3wEePoEtAe-u1jBf9FSbsPWdsQvHcmM-Ilq";

    // This function sends a notification with the specified title, body, and token
    public static void pushNotification(Context context, String title, String body, String token) {

        // Allow network access on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Create a new Volley request queue for sending the notification
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            // Create a JSON object for the notification data
            JSONObject json = new JSONObject();
            json.put("to", token);

            // Create a JSON object for the notification message
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            // Add the notification message to the notification data
            json.put("notification", notification);

            // Create a new JSON object request for sending the notification
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Base_Url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Callback for when the notification is successfully sent
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Callback for when there is an error sending the notification
                }
            }) {
                // Override the headers for the request
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };

            // Add the JSON object request to the Volley queue
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
