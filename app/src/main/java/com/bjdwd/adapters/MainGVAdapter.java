package com.bjdwd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjdwd.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/27.
 */

public class MainGVAdapter extends BaseAdapter {
    private Context context;
    private List <Map<String, Object>> functionsItemList;
    private LayoutInflater inflater = null;

    public MainGVAdapter(Context context, List <Map<String, Object>> functionsItemList) {
        this.context = context;
        this.functionsItemList = functionsItemList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List functionsItemList) {
        this.functionsItemList = functionsItemList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return functionsItemList.size();
    }

    @Override
    public Object getItem(int position) {
        if (functionsItemList != null) {
            return functionsItemList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class FunctionHolder {
        public LinearLayout main_gv_item;
        public ImageView gv_itemimage;
        public TextView gv_itemtext;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FunctionHolder functionHolder = null;
        if (convertView == null) {
            functionHolder = new FunctionHolder();
            convertView = inflater.inflate(R.layout.item_main_gv, null);
            functionHolder.main_gv_item = (LinearLayout) convertView.findViewById(R.id.main_gv_item);
            functionHolder.gv_itemimage = (ImageView) convertView.findViewById(R.id.gv_itemimage);
            functionHolder.gv_itemtext = (TextView) convertView.findViewById(R.id.gv_itemtext);
            convertView.setTag(functionHolder);
        } else {
            functionHolder = (FunctionHolder) convertView.getTag();
        }

        functionHolder.main_gv_item.setBackgroundColor((int)functionsItemList.get(position).get("funcationBGColor"));
        functionHolder.gv_itemimage.setBackgroundResource((int)functionsItemList.get(position).get("funcationIco"));
        functionHolder.gv_itemtext.setText(functionsItemList.get(position).get("funcationName").toString());
        return convertView;
    }
}
