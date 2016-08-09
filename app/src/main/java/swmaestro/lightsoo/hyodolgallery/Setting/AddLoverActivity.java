package swmaestro.lightsoo.hyodolgallery.Setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Dialog.DialogLoadingFragment;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.Manager.PropertyManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.EventAPI;

public class AddLoverActivity extends AppCompatActivity {
    private TextView tv_myid, tv_lover_email;
    private EditText et_lover_email;
    private String lover_email;
    private Button btn_lover_make;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lover);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //이걸로 기존에 뜨는 Title을 안보이게 한다.
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //백키가 나온다
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
        btn_lover_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLover();
            }
        });
    }

    public void makeLover(){
        lover_email = et_lover_email.getText().toString();


        boolean check = false;

        if(TextUtils.isEmpty(lover_email)){
            check = false;
        }else{
            check = true;
        }

//        if(check){


        if(check){
            //로딩 다이얼로그
            final DialogLoadingFragment dialog = new DialogLoadingFragment();
            dialog.show(getSupportFragmentManager(), "loading");

            Call call = NetworkManager.getInstance().getAPI(EventAPI.class).loverMake(lover_email);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Response response, Retrofit retrofit) {
                    Toast.makeText(AddLoverActivity.this, "연인이 추가되었습니다. ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(AddLoverActivity.this, "연인이 추가실패하였습니다. ", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }

    }

    public void init(){
        tv_lover_email = (TextView)findViewById(R.id.tv_lover_email);
        et_lover_email = (EditText)findViewById(R.id.et_lover_email);
        btn_lover_make = (Button)findViewById(R.id.btn_lover_make);
        tv_myid = (TextView)findViewById(R.id.tv_myid);

        tv_myid.setText(PropertyManager.getInstance().getUserLoginId());

    }

}
