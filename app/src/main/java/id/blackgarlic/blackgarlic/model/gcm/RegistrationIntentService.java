package id.blackgarlic.blackgarlic.model.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import id.blackgarlic.blackgarlic.SplashActivity;

/**
 * Created by Justin Kwik on 19/04/2016.
 */
public class RegistrationIntentService extends IntentService{

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = SplashActivity.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("1070187106506",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            editor.putString("gcmtoken", token);
            editor.commit();

            Log.e("New Token: ", token);

            if (sharedPreferences.contains("gcmtoken")) {
                Log.e("Store Token: ", "Success");
            } else {
                Log.e("Store Token: ", "Failed");
            }

        } catch (Exception e) {

            Log.e("Failed Token: ", "TRUE");
        }

    }

}

