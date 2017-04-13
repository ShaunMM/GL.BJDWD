package com.bjdwd.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bjdwd.R;
import com.bjdwd.adapters.NotePadAdapter;
import com.bjdwd.config.SQLClass;
import com.bjdwd.dbutils.HandleDBHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/28.
 * 记事本
 */
public class NoteActivity extends Activity implements View.OnClickListener {
    private LinearLayout note_linearLayout;
    private Button bt_addnote;
    private ListView lv_notes;
    private HandleDBHelper handleDBHelper;
    private List<Map> noteLists;
    private NotePadAdapter notePadAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        initData();
        initView();
    }

    private void initData() {
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
        noteLists = SQLClass.getAllData(handleDBHelper, "NotePad");
        notePadAdapter = new NotePadAdapter(NoteActivity.this, noteLists);
    }

    private void initView() {
        note_linearLayout = (LinearLayout) findViewById(R.id.note_linearLayout);
        bt_addnote = (Button) findViewById(R.id.bt_addnote);
        lv_notes = (ListView) findViewById(R.id.lv_notes);
        bt_addnote.setOnClickListener(this);
        if (noteLists.size() == 0) {
            note_linearLayout.setVisibility(View.GONE);
        } else {

            lv_notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NotePadDetailActivity.class);
                    intent.putExtra("noteId", noteLists.get(position).get("Id").toString());
                    startActivity(intent);
                    NoteActivity.this.finish();
                }
            });

            lv_notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    //AlertDialog,来判断是否删除记录。
                    new AlertDialog.Builder(NoteActivity.this)
                            .setTitle("删除")
                            .setMessage("是否删除笔记")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SQLClass.deleteAData(handleDBHelper, "NotePad", "Id", noteLists.get(position).get("Id").toString());
                                    List<Map> nowNoteLists = SQLClass.getAllData(handleDBHelper, "NotePad");
                                    if (nowNoteLists.size() == 0) {
                                        note_linearLayout.setVisibility(View.GONE);
                                    } else {
                                        notePadAdapter.setData(nowNoteLists);

                                    }
                                }
                            })
                            .create().show();
                    return true;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_addnote) {
            Intent intent = new Intent(getApplicationContext(), NotePadDetailActivity.class);
            startActivity(intent);
            NoteActivity.this.finish();
        }
    }
}