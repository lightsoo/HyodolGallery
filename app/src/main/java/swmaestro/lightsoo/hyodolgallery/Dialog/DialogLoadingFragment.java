package swmaestro.lightsoo.hyodolgallery.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenfrvr.rubberloader.RubberLoaderView;

import swmaestro.lightsoo.hyodolgallery.R;


/**
 * 일반적인 통신할때 사용할 다이얼로그
 */
public class DialogLoadingFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NO_TITLE, R.style.custom_loading_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_loading, container, false);
        RubberLoaderView rubberLoaderView = (RubberLoaderView) view.findViewById(R.id.loaderview);
        rubberLoaderView.startLoading();
        return view;
    }
}


