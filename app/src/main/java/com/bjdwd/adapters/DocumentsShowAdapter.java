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
 * Created by dell on 2017/3/28.
 */

public class DocumentsShowAdapter extends BaseAdapter {
    private Context context;
    private List<Map> fileLists;
    private LayoutInflater inflater = null;

    public DocumentsShowAdapter(Context context, List<Map> fileLists) {
        this.context = context;
        this.fileLists = fileLists;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Map> fileLists) {
        this.fileLists = fileLists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileLists.size();
    }

    @Override
    public Object getItem(int position) {
        if (fileLists != null) {
            return fileLists.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class FileHolder {
        ImageView iv_fileico;
        TextView tv_filename;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileHolder fileHolder;
        if (convertView == null) {
            fileHolder = new FileHolder();
            convertView = inflater.inflate(R.layout.item_details_lv, null);
            fileHolder.iv_fileico = (ImageView) convertView.findViewById(R.id.iv_fileico);
            fileHolder.tv_filename = (TextView) convertView.findViewById(R.id.tv_filename);
            convertView.setTag(fileHolder);
        } else {
            fileHolder = (FileHolder) convertView.getTag();
        }

        String fileExtension = fileLists.get(position).get("Extension").toString();

        fileExtension.toLowerCase();

        if (fileExtension.equals(".zip") || fileExtension.equals(".htm") || fileExtension.equals(".doc")
                || fileExtension.equals(".docx")|| fileExtension.equals(".ppt")|| fileExtension.equals(".pptx")) {
            fileHolder.iv_fileico.setImageResource(R.mipmap.document);
        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg") || fileExtension.equals(".jpeg")
                || fileExtension.equals(".gif") || fileExtension.equals(".bmp")|| fileExtension.equals(".JPG")) {
            fileHolder.iv_fileico.setImageResource(R.mipmap.pic);
        } else if (fileExtension.equals(".pdf")) {
            fileHolder.iv_fileico.setImageResource(R.mipmap.pdf);
        } else if (fileExtension.equals(".mp4")) {
            fileHolder.iv_fileico.setImageResource(R.mipmap.video);
        } else if (fileExtension.equals(".mp3")) {
            fileHolder.iv_fileico.setImageResource(R.mipmap.audio);
        } else {
            if (fileLists.get(position).get("IsCommon").toString().equals("true")) {
                fileHolder.iv_fileico.setImageResource(R.mipmap.wenjiajia);
            } else {
                fileHolder.iv_fileico.setImageResource(R.mipmap.wenzi_js);
            }

        }

        fileHolder.tv_filename.setText(fileLists.get(position).get("DirectoryName").toString());

        return convertView;
    }
}