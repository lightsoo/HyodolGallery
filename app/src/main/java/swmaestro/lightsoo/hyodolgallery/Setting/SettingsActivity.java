package swmaestro.lightsoo.hyodolgallery.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import swmaestro.lightsoo.hyodolgallery.R;

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout relativeLayout1, relativeLayout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //이걸로 기존에 뜨는 Title을 안보이게 한다.
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //
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


        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication().getApplicationContext(), AddLoverActivity.class);
                startActivity(intent);
            }
        });

        //가이드라인
        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(SettingsActivity.this, "준비중입니다..", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void init(){
        relativeLayout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
        relativeLayout2 = (RelativeLayout)findViewById(R.id.relativeLayout2);
    }
}
