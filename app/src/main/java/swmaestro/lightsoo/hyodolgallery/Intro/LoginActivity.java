package swmaestro.lightsoo.hyodolgallery.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Data.Message;
import swmaestro.lightsoo.hyodolgallery.Data.User;
import swmaestro.lightsoo.hyodolgallery.Dialog.DialogLoadingFragment;
import swmaestro.lightsoo.hyodolgallery.Event.AddEventActivity;
import swmaestro.lightsoo.hyodolgallery.Handler.BackPressCloseHandler;
import swmaestro.lightsoo.hyodolgallery.MainActivity;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.Manager.PropertyManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.LoginAPI;
import swmaestro.lightsoo.hyodolgallery.RestAPI.PushService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    //server response code
    private static final int CODE_ID_PASS_INCORRECT = 531;

    private BackPressCloseHandler backPressCloseHandler;

    //for facebook
    CallbackManager callbackManager;
    LoginManager mLoginManager;
    AccessTokenTracker tracker;
    private Button btn_login_fb, btn_login_local;

    //    for signup
    private Button btn_signup;

    //    String loginType;
    String accessToken;
    User user;

    private String email, pwd;
    private EditText et_email, et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

//        이걸로 기존에 뜨는 Title을 안보이게 한다.
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        backPressCloseHandler = new BackPressCloseHandler(this);
        btn_login_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                로그인 전송한 다음에 첫날을 등록 했으면 메인으로가고 아니면 첫날 등록으로 가자
                //로그인하고 응답메시지에서 첫날을 구별하자
                loginLocal();
            }
        });


        btn_login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOrLogout();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSignupActivity();
            }
        });



        //이렇게 생성해주기만하면 트래킹이 작동한다. 그래서 액티비티 종료되면 트랙킹도 종료해야한다.
        //로그인 매니저에서 콜밷 등록을 해서 작업이 종료되면 호출된다!!!
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d(TAG, "트랙커 토큰 체인지!");
                final AccessToken token = AccessToken.getCurrentAccessToken();
                if(token != null){

//                    userLoginId = token.getUserId();
                    accessToken = token.getToken();
//                    Log.d(TAG, "userLoginId : " + userLoginId);
//                    user = new User(userLoginId, PropertyManager.LOGIN_TYPE_FACEBOOK);

                    Call call = NetworkManager.getInstance().getAPI(LoginAPI.class).authFacebookLogin(accessToken);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            if(response.isSuccess()){
                                Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();

                                PropertyManager.getInstance().setUserLoginId(accessToken);
                                PropertyManager.getInstance().setLoginType(PropertyManager.LOGIN_TYPE_FACEBOOK);

                                String token = PropertyManager.getInstance().getRegistrationToken();
                                Call call_token = NetworkManager.getInstance().getAPI(PushService.class).regtoken(token);
                                Log.d(TAG, "token : " + token);
                                call_token.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Response response, Retrofit retrofit) {
                                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                        PropertyManager.getInstance().setUserLoginId(email);
                                        PropertyManager.getInstance().setUserLoginPwd(pwd);
                                        PropertyManager.getInstance().setLoginType(PropertyManager.LOGIN_TYPE_LOCAL);
                                        goMainActivity();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                            } else {
                                if(response.code() == CODE_ID_PASS_INCORRECT){
                                    Toast.makeText(LoginActivity.this, "ID or Password incorrect", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Server Failure.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
            }
        };
    }

    public void init(){
        mLoginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        btn_login_fb = (Button)findViewById(R.id.btn_login_fb);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        btn_login_local = (Button)findViewById(R.id.btn_login_local);

        et_email = (EditText)findViewById(R.id.et_login_email);
        et_pwd = (EditText)findViewById(R.id.et_login_passwd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //트랙킹 종료
        tracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //이걸 반드시해줘야한다. 얘가 있어야 콜백이 호출된다. 액티비티가 받은 결과를 callbackmanager로 토스!!!
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //로그인 성공하면 메인으로 이동하고 이전액티비티는 종료한다.
    private void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //로그인 성공하면 메인으로 이동하고 이전액티비티는 종료한다.
    private void goSignupActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        tracker.stopTracking();
        finish();
    }

    //첫 이벤트 설정, 처음 보내진것을 토대로 D-day를 계산한다. 유저 컬럼에 넣어서 하는게 좋을듯하다.
//    페이스북에서는 로그인이 됬으니까 바로 첫날등록으로 간다.
    private void goFirstEvent(){
        startActivity(new Intent(this, AddEventActivity.class));
        tracker.stopTracking();
        finish();
    }


    private void loginLocal(){
        email = et_email.getText().toString();
        pwd = et_pwd.getText().toString();
        //유효성 검사
        if (preInspection()) {


            final DialogLoadingFragment dialog = new DialogLoadingFragment();
            dialog.show(getSupportFragmentManager(), "loading");

            Call call_login = NetworkManager.getInstance().getAPI(LoginAPI.class).authLocalLogin(email, pwd);
            call_login.enqueue(new Callback() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        Message msg = (Message) response.body();
                        String token = PropertyManager.getInstance().getRegistrationToken();
                        Call call_token = NetworkManager.getInstance().getAPI(PushService.class).regtoken(token);
                        Log.d(TAG, "token : "+token);
                        call_token.enqueue(new Callback() {
                            @Override
                            public void onResponse(Response response, Retrofit retrofit) {
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                PropertyManager.getInstance().setUserLoginId(email);
                                PropertyManager.getInstance().setUserLoginPwd(pwd);
                                PropertyManager.getInstance().setLoginType(PropertyManager.LOGIN_TYPE_LOCAL);
                                goMainActivity();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {

                                Toast.makeText(LoginActivity.this, "서버연결 실패", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    } else {
                        Toast.makeText(LoginActivity.this, "서버전송인데 200ok가 아니야...", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(LoginActivity.this, "서버연결 실패", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    public boolean preInspection() {
        if(TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "빈칸이 있습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

//    for facebook
    private void loginOrLogout(){
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            mLoginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
            mLoginManager.setDefaultAudience(DefaultAudience.FRIENDS);
            mLoginManager.logInWithReadPermissions(this, null);
        } else {
            mLoginManager.logOut();
        }
    }

    @Override
    public void onBackPressed() {backPressCloseHandler.onBackPressed();}

}