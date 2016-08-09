package swmaestro.lightsoo.hyodolgallery.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import swmaestro.lightsoo.hyodolgallery.Data.Anni;
import swmaestro.lightsoo.hyodolgallery.View.AnniView;


/**
 * Created by LG on 2016-07-17.
 */
public class AnniAdapter extends BaseAdapter {

    List<Anni> items = new ArrayList<Anni>();


    public void add(Anni item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List<Anni> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(Anni item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AnniView view;
        if(convertView==null){
            view = new AnniView(parent.getContext());
//            view.setOn
        }else{
            view = (AnniView)convertView;
        }
        view.setAnni(items.get(position));
        return view;
    }
}