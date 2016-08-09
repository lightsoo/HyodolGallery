package swmaestro.lightsoo.hyodolgallery.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import swmaestro.lightsoo.hyodolgallery.Manager.PropertyManager;
import swmaestro.lightsoo.hyodolgallery.R;


public class RegistrationIntentService extends IntentService{
    static final private String TAG = "RegIntentService";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    // 파라미터 없는 public 생성자 꼭 필요
    public RegistrationIntentService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "RegistrationIntentService Start!");
        try {
            // 발급받은 토큰
            InstanceID instanceID = InstanceID.getInstance(this);
            final String token = instanceID.getToken(getString(R.string.GCM_SenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            PropertyManager.getInstance().setRegistrationToken(token);
            Log.d(TAG, "Token : " + token.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Regist Exception", e);
        }


        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }
}
