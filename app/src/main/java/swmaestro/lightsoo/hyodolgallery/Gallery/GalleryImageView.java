package swmaestro.lightsoo.hyodolgallery.Gallery;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import swmaestro.lightsoo.hyodolgallery.R;


/**
 * 갤러리에서 하나의 이미지들을 설정해주는거야
 * view_gallery_image_item 레이아웃에 이미지뷰 2개가 있는데
 * 기본적으로 gallery_image_item 이미지뷰에 사진들을 출력해주고
 * 이후 선택이 되면 gallery_image_checked을 덮어씌워서 표현한다.
 */

public class GalleryImageView extends RelativeLayout implements Checkable {

    ImageView ivImage, ivChecked;

    public GalleryImageView(Context context) {
        super(context);
        init();
    }

    public GalleryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_gallery_image_item, this);
        ivImage = (ImageView)findViewById(R.id.gallery_image_item);
        ivChecked = (ImageView)findViewById(R.id.gallery_image_checked);
    }

    boolean isChecked = false;
    private void drawCheck(){
        if(isChecked){
            ivChecked.setVisibility(VISIBLE);
            ivChecked.setBackgroundColor(Color.parseColor("#ffcc11"));
            //           checkView.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            ivChecked.setVisibility(INVISIBLE);
            ivChecked.setBackgroundColor(Color.TRANSPARENT);
            // checkView.setImageResource(android.R.drawable.checkbox_off_background);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if(isChecked != checked){
            isChecked = checked;
            drawCheck();
        }
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }



}
