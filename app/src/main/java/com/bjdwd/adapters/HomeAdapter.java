package com.bjdwd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjdwd.R;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/4/12.
 */

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> functionsItemList;
    private LayoutInflater inflater = null;
    private int mSelect = 0;   //选中项

    public HomeAdapter(Context context, List<Map<String, Object>> functionsItemList) {
        this.context = context;
        this.functionsItemList = functionsItemList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List functionsItemList) {
        this.functionsItemList = functionsItemList;
        notifyDataSetChanged();
    }

    public void changeSelected(int positon) { //刷新方法
        if (positon != mSelect) {
            mSelect = positon;
            notifyDataSetChanged();
        }
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

    public class TopDirHolder {
        private ImageView iv_topdir;
        private TextView tv_topdir;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopDirHolder topDirHolder = null;
        if (convertView == null) {
            topDirHolder = new TopDirHolder();
            convertView = inflater.inflate(R.layout.adapter_homelvtopdir, null);
            topDirHolder.iv_topdir = (ImageView) convertView.findViewById(R.id.iv_topdir);
            topDirHolder.tv_topdir = (TextView) convertView.findViewById(R.id.tv_topdir);
            convertView.setTag(topDirHolder);
        } else {
            topDirHolder = (TopDirHolder) convertView.getTag();
        }
        if (mSelect == position) {
            convertView.setBackgroundResource(R.color.colorblue);
        } else {
            convertView.setBackgroundResource(R.color.colorblue2);  //其他项背景
        }

        topDirHolder.iv_topdir.setBackgroundResource((int) functionsItemList.get(position).get("funcationIco"));
        topDirHolder.tv_topdir.setText(functionsItemList.get(position).get("funcationName").toString());
        return convertView;
    }
}
