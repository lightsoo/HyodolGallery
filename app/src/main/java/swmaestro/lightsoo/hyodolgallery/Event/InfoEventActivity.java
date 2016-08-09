package swmaestro.lightsoo.hyodolgallery.Event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Adapter.InfoEventItemAdapter;
import swmaestro.lightsoo.hyodolgallery.Data.Anni;
import swmaestro.lightsoo.hyodolgallery.Dialog.DialogLoadingFragment;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.EventAPI;

public class InfoEventActivity extends AppCompatActivity {

    public static final String TAG = "InfoEventActivity";
    private int event_id;
    private TextView tv_title, tv_place, toolbar_date;
    private InfoEventItemAdapter infoEventItemAdapter;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        이걸로 기존에 뜨는 Title을 안보이게 한다.
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        getEventInfo();

    }
    public void init(){
        Intent intent = new Intent(getIntent());
        event_id = intent.getExtras().getInt("event_id");
        tv_title = (TextView)findViewById(R.id.tv_info_event_title);
        tv_place=(TextView)findViewById(R.id.tv_info_event_place);
        toolbar_date = (TextView)findViewById(R.id.toolbar_info_event_date);
        gridView = (GridView)findViewById(R.id.event_info_grid);

        infoEventItemAdapter = new InfoEventItemAdapter();
//        Log.d(TAG, "event_id : ", "" + event_id);
    }
    public void getEventInfo(){
        //로딩 다이얼로그
        final DialogLoadingFragment dialog = new DialogLoadingFragment();
        dialog.show(getSupportFragmentManager(), "loading");

        Call call = NetworkManager.getInstance().getAPI(EventAPI.class).getEventInfo(event_id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response, Retrofit retrofit) {
                Anni result = (Anni)response.body();
                Log.d(TAG, "response = " + new Gson().toJson(result));


                List<String> event_url =  result.event_img;
                infoEventItemAdapter.addAll(event_url);
                gridView.setAdapter(infoEventItemAdapter);

                tv_title.setText(result.getTitle());
                tv_place.setText(result.getPlace());
                toolbar_date.setText(parseDate(result.getDate()));





                dialog.dismiss();
                //이미지는 Fitta 꺼 참고 하자
                //이외에 것은 Retrofit_Tutorial_master 참고
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(InfoEventActivity.this, "이벤트 불러오기 실패", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }


    public String parseDate(String date) {
        String ds = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d=null;
        try {
            d = sdf.parse(ds);
        } catch (ParseException e) {
        }
        return sdf.format(d);
    }


}
