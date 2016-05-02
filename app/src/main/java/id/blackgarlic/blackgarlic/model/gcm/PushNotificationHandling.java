package id.blackgarlic.blackgarlic.model.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.blackgarlic.blackgarlic.ConnectionManager;
import id.blackgarlic.blackgarlic.MainActivity;
import id.blackgarlic.blackgarlic.R;
import id.blackgarlic.blackgarlic.SplashActivity;

/**
 * Created by JustinKwik on 5/2/16.
 */
public class PushNotificationHandling extends IntentService {

    private static String pushOrderHistory = "http://10.0.3.2:3000/app/pushorderhistory";

    private static JSONObject body = new JSONObject();

    public PushNotificationHandling() {
        super("PushNotificationHandling");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();

        try {
            body.put("unique_id", Integer.valueOf(intent.getExtras().getString("uniqueId", "")));
            body.put("regToken", sharedPreferences.getString("gcmtoken", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doStringRequest();
    }


    public void doStringRequest() {
        StringRequest request = new StringRequest(Request.Method.POST, pushOrderHistory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("Push Notification: ", "Success");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Push Notification: ", "Failed");

            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        ConnectionManager.getInstance(PushNotificationHandling.this).add(request);
    }

}
