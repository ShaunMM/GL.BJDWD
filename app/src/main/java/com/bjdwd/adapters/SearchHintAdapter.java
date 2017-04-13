package com.bjdwd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjdwd.R;

import java.util.List;


/**
 * Created by dell on 2017/3/30.
 */

public class SearchHintAdapter extends BaseAdapter {
    private Context context;
    private List<String> listkeycodes;
    private LayoutInflater inflater = null;

    public SearchHintAdapter(Context context, List<String> listkeycodes) {
        this.context = context;
        this.listkeycodes = listkeycodes;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setData(List<String> listkeycodes) {
        this.listkeycodes = listkeycodes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listkeycodes.size();
    }

    @Override
    public Object getItem(int position) {
        if (listkeycodes != null) {
            return listkeycodes.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class HintHolder {
        public TextView tv__hint;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HintHolder hintHolder = null;
        if (convertView == null) {
            hintHolder = new HintHolder();
            convertView = inflater.inflate(R.layout.adapter_searchhintitem, null);
            hintHolder.tv__hint = (TextView) convertView.findViewById(R.id.tv__hint);
            convertView.setTag(hintHolder);
        } else {
            hintHolder = (HintHolder) convertView.getTag();
        }
        hintHolder.tv__hint.setText(listkeycodes.get(position).toString());
        return convertView;
    }
}