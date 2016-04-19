package id.blackgarlic.blackgarlic.model.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Justin Kwik on 19/04/2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService{

        @Override
        public void onTokenRefresh() {
            Log.e("In Refresh: ", "True");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

}
