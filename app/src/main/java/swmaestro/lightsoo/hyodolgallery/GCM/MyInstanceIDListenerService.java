package swmaestro.lightsoo.hyodolgallery.GCM;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by LG on 2016-07-21.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        // 토큰 변경 - 앱 서버에 반영하는 코드 필요
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }


}
