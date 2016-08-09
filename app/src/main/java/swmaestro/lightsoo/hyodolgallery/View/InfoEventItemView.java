package swmaestro.lightsoo.hyodolgallery.View;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import swmaestro.lightsoo.hyodolgallery.R;


/**
 * Created by LG on 2016-07-20.
 */
public class InfoEventItemView extends FrameLayout {
    ImageView imageView;

    public InfoEventItemView(Context context) {
        super(context);
        init();

    }
    public void init(){
        inflate(getContext(), R.layout.view_infoevent_item, this);
        imageView = (ImageView)findViewById(R.id.infoevent_image_item);
    }
    public void setInfoEventImageView(String url){
//        urlView.setText(url);

        //Glide
        Glide.with(getContext())
                .load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);


    }
}
