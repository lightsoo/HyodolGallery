package swmaestro.lightsoo.hyodolgallery.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import swmaestro.lightsoo.hyodolgallery.View.InfoEventItemView;

public class InfoEventItemAdapter extends BaseAdapter{

    private static final String TAG = "InfoEventItemAdapter";
    List<String> items = new ArrayList<String>();

    public void add(String url){
        items.add(url);
        notifyDataSetChanged();
    }

    public void addAll(List<String> url){
        items.addAll(url);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //뷰홀더에서다가 데이터를 입력해서 리턴함으로서 출력하는거야.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoEventItemView view;
        if(convertView == null){
            view = new InfoEventItemView(parent.getContext());
        }else{
            view = (InfoEventItemView)convertView;
        }

        Log.d(TAG, items.get(position));
        //객체리턴했어
        view.setInfoEventImageView(items.get(position));
        return view;

    }
}
