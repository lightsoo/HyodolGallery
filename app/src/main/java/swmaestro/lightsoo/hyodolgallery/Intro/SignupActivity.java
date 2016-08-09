package swmaestro.lightsoo.hyodolgallery.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Data.Message;
import swmaestro.lightsoo.hyodolgallery.Dialog.DialogLoadingFragment;
import swmaestro.lightsoo.hyodolgallery.MainActivity;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.Manager.PropertyManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.LoginAPI;
import swmaestro.lightsoo.hyodolgallery.RestAPI.PushService;
import swmaestro.lightsoo.hyodolgallery.RestAPI.SignupAPI;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText et_email, et_pwd, et_repwd, et_name;
    private String email, pwd, repwd, name;
    private Button btn_emailcheck, btn_signup;

    private boolean checkEmail = false; //아이디 중복확인성공 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //백키가 나온다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //백키 이벤트
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        init();

        btn_emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();

                boolean check = false;


                if (!email.contains("@")) {
                        Toast.makeText(SignupActivity.this, "유효한 아이디가 아닙니다.", Toast.LENGTH_SHORT).show();
                    check = false;
                }else if(TextUtils.isEmpty(email)){
                    check = false;
                }else{
                    check = true;
                }

                if(check){
                    final DialogLoadingFragment dialog = new DialogLoadingFragment();
                    dialog.show(getSupportFragmentManager(), "loading");
                    Call call_emailcheck = NetworkManager.getInstance().getAPI(SignupAPI.class).check(email);
                    call_emailcheck.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            Message msg = (Message) response.body();
                            Log.d(TAG, msg.getMsg());

                            if (msg.getMsg() == "이메일 사용 불가능") {
                                Toast.makeText(SignupActivity.this, "중복된 이메일주소입니다.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "이메일 사용 확인 : " + msg.getMsg());
                                checkEmail = false;
                                dialog.dismiss();
                            } else {
                                Toast.makeText(SignupActivity.this, "사용 가능한 이메일주소입니다.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "이메일 사용 확인 : " + msg.getMsg());
                                checkEmail = true;
                                dialog.dismiss();
                            }
                            dialog.dismiss();


                        }

                        @Override
                        public void onFailure(Throwable t) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                pwd = et_pwd.getText().toString();
                repwd = et_repwd.getText().toString();
                name = et_name.getText().toString();
                //유효성 검사
                if (preInspection()) {
                    final DialogLoadingFragment dialog = new DialogLoadingFragment();
                    dialog.show(getSupportFragmentManager(), "loading");

                    Call call_join = NetworkManager.getInstance().getAPI(SignupAPI.class).join(email, pwd, name);
                    call_join.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            if (response.isSuccess()) {
//                                Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Message msg = (Message) response.body();

                                Call call_login = NetworkManager.getInstance().getAPI(LoginAPI.class).authLocalLogin(email, pwd);
                                call_login.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Response response, Retrofit retrofit) {
                                        String token = PropertyManager.getInstance().getRegistrationToken();
                                        Call call_token = NetworkManager.getInstance().getAPI(PushService.class).regtoken(token);
                                        call_token.enqueue(new Callback() {
                                            @Override
                                            public void onResponse(Response response, Retrofit retrofit) {
                                                Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                PropertyManager.getInstance().setUserLoginId(email);
                                                PropertyManager.getInstance().setUserLoginPwd(pwd);
                                                PropertyManager.getInstance().setLoginType(PropertyManager.LOGIN_TYPE_LOCAL);
                                                goMainActivity();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {

                                                Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                        Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                            } else {
                                Toast.makeText(SignupActivity.this, "서버전송인데 200ok가 아니야...", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(SignupActivity.this, "서버전송 실패 : ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void init() {
        et_email = (EditText) findViewById(R.id.et_signup_email);
        et_pwd = (EditText) findViewById(R.id.et_signup_pwd);
        et_repwd = (EditText) findViewById(R.id.et_signup_repwd);
        et_name = (EditText) findViewById(R.id.et_signup_name);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_emailcheck = (Button)findViewById(R.id.btn_emailcheck);
    }

    public boolean preInspection() {
        if (!email.contains("@")) {
            Toast.makeText(SignupActivity.this, "유효한 아이디가 아닙니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!checkEmail) {
            Toast.makeText(SignupActivity.this, "아이디 중복확인을 해주세요", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(pwd.equals(repwd))){
            Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(pwd)||
                TextUtils.isEmpty(repwd)||
                TextUtils.isEmpty(name)) {
            Toast.makeText(SignupActivity.this, "빈칸이 있습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else {

            return true;
        }
    }


    //로그인 성공하면 메인으로 이동하고 이전액티비티는 종료한다.
    private void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
