package swmaestro.lightsoo.hyodolgallery;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by Lightsoo on 2016. 8. 9..
 */

public class MyApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        /*//갤러리 권한 체크 (사용권한이 없는 경우 -1)
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //권한이 없을경우
        }*/
        mContext = this;
        //facebook sdk를 사용하기위해서 한번 로그인하면 이걸 쉐프에 저장한다. 그래서 다음번에 로그인할때 이걸쓰기 위한 처리작업
        //페이스북 기능을 쓰기전에 호출되어야한다.
        //facebook SDK에 application context값을 설저해주고, 쉐프에 저장된 로그인정보(accessToken정보)가 있으면 불러와서
        //AccessToken에 해당 값을 설정해 주는 역할을 한다.
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
    public static Context getContext(){
        return mContext;
    }

}
