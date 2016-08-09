package swmaestro.lightsoo.hyodolgallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;

import swmaestro.lightsoo.hyodolgallery.Adapter.AnniAdapter;
import swmaestro.lightsoo.hyodolgallery.Handler.BackPressCloseHandler;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";

    Handler mHandler = new Handler(Looper.getMainLooper());

    SwipeRefreshLayout swipeRefreshLayout;

    ListView lv_anni;
    AnniAdapter anniAdapter;

    private BackPressCloseHandler backPressCloseHandler;
    private ImageButton btn_addevent, btn_settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

}
