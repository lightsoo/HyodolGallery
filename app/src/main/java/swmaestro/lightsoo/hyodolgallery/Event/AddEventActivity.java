package swmaestro.lightsoo.hyodolgallery.Event;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import swmaestro.lightsoo.hyodolgallery.Dialog.DialogLoadingFragment;
import swmaestro.lightsoo.hyodolgallery.MainActivity;
import swmaestro.lightsoo.hyodolgallery.Manager.NetworkManager;
import swmaestro.lightsoo.hyodolgallery.R;
import swmaestro.lightsoo.hyodolgallery.RestAPI.EventAPI;
import swmaestro.lightsoo.hyodolgallery.RestAPI.PushService;

public class AddEventActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "AddEventActivity";

    private DatePicker datePicker;
    private EditText et_title, et_place;
    private ImageButton btn_ok;
    private  String event_title, event_date, event_place;
    private boolean checkDate = false;

    int dataColumnIndex = -1;
    SimpleCursorAdapter mAdapter;
    GridView gridView;
    //
    private String day, tommorow;
    private Date d_day;
    //최대 선택갯수
    public static final int IMAGE_CHECK_THRESHOLD = 5;

//    file upload
    private File [] file = new File[IMAGE_CHECK_THRESHOLD];
    private String[] paths;
    private HashMap<String, RequestBody> partMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
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
        getSupportLoaderManager().initLoader(0, null, this);

        //DB의 데이타를
        String[] from = {MediaStore.Images.Media.DATA};
        //gallery_image_item에다가 출력하려는 거야
        int[] to = {R.id.gallery_image_item};
        //직접 새로운 레이아웃을 만들었어
        mAdapter = new SimpleCursorAdapter(this, R.layout.view_gallery_image_item_layout, null, from, to, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == dataColumnIndex) {
                    ImageView iv = (ImageView) view;
                    String path = cursor.getString(columnIndex);
                    String uri = "file://" + path;
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(iv);
//                    ImageLoader.getInstance().displayImage(uri.toString(), iv);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(mAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        getSupportLoaderManager().initLoader(0, null, this);

        //사진 선택갯수
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //리스트뷰에서 체크된걸 확인하는 객체야
                SparseBooleanArray selection = gridView.getCheckedItemPositions();
                int count = 0;
                for (int index = 0; index < selection.size(); index++) {
                    int pos = selection.keyAt(index);
                    //get(pos) 사진이 선택된거야
                    if (selection.get(pos)) {
                        count++;
                    }
                }
                //코드로 설정 다 한후에 그림이 그려진다.
                //view의 setChecked가 먼저 불려진다. 할지라도 여기서 한번더 설정이 된 후에
                //그 값을 가지고 onresume다음에 draw되는 시점에 그려진다.!
                if (count > IMAGE_CHECK_THRESHOLD) {
                    gridView.setItemChecked(position, false);
                    Toast.makeText(AddEventActivity.this, "사진은 최대 5장까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        datePicker.init(datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        event_date = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        checkDate = true;

//                        Toast.makeText(AddEventActivity.this, event_date, Toast.LENGTH_SHORT).show();
                        try {
                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                            //특정날짜
                            d_day = f.parse(event_date);
                            day = "" + d_day.getTime() / 1000;
                            tommorow = "" + (d_day.getTime() + (long) (1000 * 60 * 60 * 24)) / 1000;
                            getSupportLoaderManager().restartLoader(0, null, AddEventActivity.this);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_title = et_title.getText().toString();
                event_place = et_place.getText().toString();
                if(preInspection()){
//                    goMainActivity();
                    pictureAdd();

                    for(int i = 0;i< paths.length;i++){
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file[i]);
//                        part.put("" + key + "\"; filename=\"" + key + ".jpg", file);
                        partMap.put("event_img"+i+"\"; filename=\"image.jpg", requestBody);
                    }

                    //여기서 한개 보내면 에러가 떠...
                    //여기서 어떤 사이즈옷인지, 분류할 어떤 데이터인지도 같이 보내줘서 서버에서 처리한다.
//                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file[0]);
//                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file[1]);

                    //로딩 다이얼로그
                    final DialogLoadingFragment dialog = new DialogLoadingFragment();
                    dialog.show(getSupportFragmentManager(), "loading");

                    Call call = NetworkManager.getInstance().getAPI(EventAPI.class).addEvent(event_title, event_date, event_place, partMap);
//                    Call call = NetworkManager.getInstance().getAPI(EventAPI.class).addEvent(event_title, event_date, event_place, requestBody1, requestBody2);

                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Response response, Retrofit retrofit) {
                            Toast.makeText(AddEventActivity.this, "이벤트 추가 성공", Toast.LENGTH_SHORT).show();
//                            goMainActivity();

                            Call call1 = NetworkManager.getInstance().getAPI(PushService.class).pushmessage("연인이 이벤트를 추가했어요");
                            call1.enqueue(new Callback() {
                                @Override
                                public void onResponse(Response response, Retrofit retrofit) {

                                }

                                @Override
                                public void onFailure(Throwable t) {

                                }
                            });

                            finish();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(AddEventActivity.this, "이벤트 추가 실패", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
    }


    public void init(){
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        et_title = (EditText)findViewById(R.id.et_title);
        et_place = (EditText)findViewById(R.id.et_place);
        btn_ok = (ImageButton)findViewById(R.id.btn_ok);
        //       about gallery
        gridView = (GridView)findViewById(R.id.gallery_image_grid);
        partMap = new HashMap<String, RequestBody>();
    }

    public boolean preInspection() {
        if(TextUtils.isEmpty(event_title) ||
                TextUtils.isEmpty(event_place)) {
            Toast.makeText(AddEventActivity.this, "빈칸이 있습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!checkDate) {
            Toast.makeText(AddEventActivity.this, "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



//    about gallery
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //갤러리 내장 DB에서 이미지를 가져오기위한 쿼리 세팅
        //Select하고자 하는 컬럼 명(이미지 id, 경로명)
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,};
        String selection =
                MediaStore.Images.Media.DATE_ADDED + "<" + tommorow  +" AND " +  MediaStore.Images.Media.DATE_ADDED + ">" + day;
        String[] selectionArgs=null;

        Log.d(TAG,"day : " + day);
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        return new CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataColumnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void pictureAdd(){
        SparseBooleanArray array = gridView.getCheckedItemPositions();

        List<String> pathList = new ArrayList<String>();
        for (int index = 0; index < array.size(); index++){
            int position = array.keyAt(index);
            if(array.get(position)){
                //어떻게 이렇게 커서로 변환이 가능한걸까?
                Cursor c = (Cursor)gridView.getItemAtPosition(position);
                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
//                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                pathList.add(path);
            }
        }
        // image 경로...
        paths = pathList.toArray(new String[pathList.size()]);


        for(int i = 0; i < paths.length; i++){
            Log.d(TAG, paths[i].toString());
            //여기에 리스트뷰로 하자,

            file[i] = new File(paths[i].toString());

        }


    }

}
