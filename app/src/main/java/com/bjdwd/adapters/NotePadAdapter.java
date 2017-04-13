package com.bjdwd.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bjdwd.R;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/28.
 */
public class NotePadAdapter extends BaseAdapter {
    private Context context;
    private List<Map> notetLists;
    private LayoutInflater inflater = null;

    public NotePadAdapter(Context context, List<Map> notetLists) {
        this.context = context;
        this.notetLists = notetLists;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setData(List<Map> notetLists) {
        this.notetLists = notetLists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return notetLists.size();
    }

    @Override
    public Object getItem(int position) {
        if (notetLists != null) {
            return notetLists.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class NotePadHolder {
        TextView tv_notetitle;
        TextView tv_notecontent;
        TextView tv_notetime;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotePadHolder notePadHolder;
        if (convertView == null) {
            notePadHolder = new NotePadHolder();
            convertView = inflater.inflate(R.layout.notepad_item, null);
            notePadHolder.tv_notetitle = (TextView) convertView.findViewById(R.id.tv_notetitle);
            notePadHolder.tv_notecontent = (TextView) convertView.findViewById(R.id.tv_notecontent);
            notePadHolder.tv_notetime = (TextView) convertView.findViewById(R.id.tv_notetime);
            convertView.setTag(notePadHolder);
        } else {
            notePadHolder = (NotePadHolder) convertView.getTag();
        }

        notePadHolder.tv_notetitle.setText(notetLists.get(position).get("Title").toString());
        notePadHolder.tv_notecontent.setText(notetLists.get(position).get("Content").toString());
        notePadHolder.tv_notetime.setText(notetLists.get(position).get("Time").toString());
        return convertView;
    }
}
