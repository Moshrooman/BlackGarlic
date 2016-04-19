package id.blackgarlic.blackgarlic.model.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

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

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("1070187106506",
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.e("Token: ", token);

        } catch (Exception e) {

            Log.e("Failed Token: ", "TRUE");
        }

    }

}

