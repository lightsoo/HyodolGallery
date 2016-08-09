package swmaestro.lightsoo.hyodolgallery.Intro;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Data.Message;
import swmaestro.lightsoo.hyodolgallery.Event.AddEventActivity;
import swmaestro.lightsoo.hyodolgallery.GCM.RegistrationIntentService;
import swmaestro.lightsoo.hyodolgallery.MainActivity;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.Manager.PropertyManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.LoginAPI;
import swmaestro.lightsoo.hyodolgallery.RestAPI.PushService;

/**
 * 플로우 ; 먼저 푸쉬 토큰이 있는지 확인한다음
 * 없으면 : 구글서비스 체크 -> RegistrationIntentService에서 토큰 등록 -> Splash의 mRegBroadcastReceiver로 돌아와서 doRealStart();
 * 있으면 : 구글서비스 체크 -> doRealStart();
 */

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    Handler mHandler = new Handler(Looper.getMainLooper());


//for GCM
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000; //gcm 요청 시간
    private BroadcastReceiver mRegBroadcastReceiver; //gcm 리시버

    private String regToken; //GCM을 보낼떄 사용할 id들
    private String loginType;
//    둘다 같은 프리프런스에서 가져오는데 일단 페북이랑 로컬이랑 이해하기 쉽도록 각각 변수를 뒀다.
    private String userLoginId;
    private String email, pwd;

    //for facebook
    CallbackManager callbackManager = CallbackManager.Factory.create();
    LoginManager mLoginManager = LoginManager.getInstance();
    AccessTokenTracker mTokenTracker;

    private ImageView logo ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        //첫 app 실행 시키고 RegistrationIntentService에서 토큰 생성후 다시
        mRegBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "mRegBroadcastReceiver, onReceive()");
                doRealStart();
            }
        };
        setUpIfNeeded();


//        goLoginActivity();
//        goMainActivity();
//        doRealStart();
    }

    public void init(){
        logo = (ImageView)findViewById(R.id.logo);
        Glide.with(getApplicationContext())
                .load(R.drawable.logo)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(logo);


    }
    //첫 이벤트 설정, 처음 보내진것을 토대로 D-day를 계산한다. 유저 컬럼에 넣어서 하는게 좋을듯하다.
    private void goFirstEvent(){
        startActivity(new Intent(this, AddEventActivity.class));
        finish();
    }

    private void doRealStart(){
        loginType = PropertyManager.getInstance().getLoginType();
        userLoginId = PropertyManager.getInstance().getUserLoginId();
        //로그인 한적이 없을 경우 혹은 로그아웃했을 경우 → 로그인 액티비티로 이동
        if(TextUtils.isEmpty(loginType)){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "로그인 한적이 없어서 로그인페이지로 이동");
                    goLoginActivity();
                }
            }, 500);
        }else {
            switch (loginType){
                case PropertyManager.LOGIN_TYPE_FACEBOOK:
                    //로그인 id가 존재할경우
                    if(!TextUtils.isEmpty(userLoginId)){

                        Log.d(TAG, "id가 있는경우 :!TextUtils.isEmpty(userLoginId))");
//                        Log.d(TAG, "userLoginId : " + userLoginId );


                        Call call = NetworkManager.getInstance().getAPI(LoginAPI.class).authFacebookLogin(userLoginId);
                        call.enqueue(new Callback() {
                            @Override
                            public void onResponse(Response response, Retrofit retrofit) {
                                if (response.isSuccess()) {//이전에 가입되었던 사람이라면 OK,
//푸쉬 토큰
                                    String token = PropertyManager.getInstance().getRegistrationToken();
                                    Call call_token = NetworkManager.getInstance().getAPI(PushService.class).regtoken(token);
                                    call_token.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Response response, Retrofit retrofit) {
                                            Toast.makeText(SplashActivity.this, "페이스북 연동 로그인으로 입장 합니다.", Toast.LENGTH_SHORT).show();
                                            goMainActivity();
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {

                                        }
                                    });
                                } else {
                                    //아니라면 not registered
                                    mLoginManager.logOut();
                                    goLoginActivity();
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Toast.makeText(SplashActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                                goLoginActivity();
                            }
                        });

                        mLoginManager.logInWithReadPermissions(this, null);
                    }else{//id가 없을경우에 로그인 페이지로 이동!!!
                        Log.d(TAG, "id가 없는경우 : !TextUtils.isEmpty(userLoginId))");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SplashActivity.this, "Welcome! please log-in!", Toast.LENGTH_SHORT).show();
                                goLoginActivity();
                            }
                        }, 500);
                    }
                    break;

                case PropertyManager.LOGIN_TYPE_LOCAL:

                    email = PropertyManager.getInstance().getUserLoginId();
                    pwd = PropertyManager.getInstance().getUserLoginPwd();

                    Call call_login = NetworkManager.getInstance().getAPI(LoginAPI.class).authLocalLogin(email, pwd);
                    call_login.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                Message msg = (Message) response.body();
                                String token = PropertyManager.getInstance().getRegistrationToken();
                                Call call_token = NetworkManager.getInstance().getAPI(PushService.class).regtoken(token);
                                call_token.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Response response, Retrofit retrofit) {
                                        Toast.makeText(SplashActivity.this, "로컬 로그인 성공", Toast.LENGTH_SHORT).show();
                                        goMainActivity();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });

                            } else {
                                Toast.makeText(SplashActivity.this, "서버전송인데 200ok가 아니야...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                    break;
            }
        }
    }
//테스트 용도
    private void goMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goLoginActivity(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    //gcm 리시버를 위한 메서드들
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegBroadcastReceiver,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegBroadcastReceiver);
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST &&
                resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult()");
            setUpIfNeeded();
        }
    }

    //플레이서비스가 사용가능한지 체크하고 사용불가능이면 서비스 시작
//    토큰이 없을경우 토큰생성 서비스롤 실행
    private void setUpIfNeeded() {
        if (checkPlayServices()) {
            regToken = PropertyManager.getInstance().getRegistrationToken();

            if (!regToken.equals("")) {
                Log.d(TAG, "setUpIfNeeded(1) : " + regToken);
                doRealStart();
            } else {
//                토큰이 없는경우 서비스로 갔어 이제 서비스에서
                Log.d(TAG, "setUpIfNeeded(2) : " + regToken);
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    //플레이서비스가 사용가능한지 체크하는 메서드
    private boolean checkPlayServices() {

        Log.d(TAG, "checkPlayServices()");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

}
