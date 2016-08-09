package swmaestro.lightsoo.hyodolgallery.View;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import swmaestro.lightsoo.hyodolgallery.Data.Anni;
import swmaestro.lightsoo.hyodolgallery.R;


/**
 * Created by LG on 2016-07-17.
 */
public class AnniView extends FrameLayout {

    private TextView titleView, dateView, placeView;
    private Anni anni;

    public AnniView(Context context) {
        super(context);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.view_main_anniitem, this);
        titleView = (TextView)findViewById(R.id.view_tv_title);
        dateView = (TextView)findViewById(R.id.view_tv_date);
        placeView = (TextView)findViewById(R.id.view_tv_place);
    }

    public void setAnni(Anni anni){
        this.anni = anni;
        this.titleView.setText(anni.getTitle());


//        this.dateView.setText(anni.getDate());

//이거 나중에하자...
//       this.dateView.setText(parseDate(anni.getDate()));


        this.placeView.setText(anni.getPlace());
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
