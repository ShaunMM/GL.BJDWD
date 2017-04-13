package com.bjdwd.activitys;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bjdwd.R;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.tools.ShowToastTool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dell on 2017/3/28.
 */

public class NotePadDetailActivity extends Activity {
    private TextView tv_username;
    private EditText et_notetitle;
    private EditText et_notecontent;
    private Button bt_notesave;
    private int id;
    private ISystemConfig systemConfig;
    private HandleDBHelper handleDBHelper;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepaddetail);
        initView();
        initData();
    }

    private void initView() {
        tv_username = (TextView) findViewById(R.id.tv_username);
        et_notetitle = (EditText) findViewById(R.id.et_notetitle);
        et_notecontent = (EditText) findViewById(R.id.et_notecontent);
        bt_notesave = (Button) findViewById(R.id.bt_notesave);
    }

    private void initData() {
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        Intent intent = this.getIntent();
        id = intent.getIntExtra("noteId", -1);
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
        username = systemConfig.getUserName();
        tv_username.setText(username);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String times = formatter.format(curDate);
        String title = et_notetitle.getText().toString();
        String content = et_notecontent.getText().toString();

        if (id != -1) {
            //修改数据
            handleDBHelper.update("NotePad", new String[]{"UserName", "Title", "Content", "Time"}, new Object[]{username, title, content, times}, new String[]{"Id"}, new String[]{id + ""});
            Intent intent = new Intent(NotePadDetailActivity.this, NoteActivity.class);
            startActivity(intent);
            NotePadDetailActivity.this.finish();
        } else {
            //新建日记
            if (title.equals("") && content.equals("")) {
                ShowToastTool.showToast(getApplicationContext(), "主题内容不可以为空！");
                NotePadDetailActivity.this.finish();
            } else {
                handleDBHelper.insert("NotePad", new String[]{"UserName", "Title", "Content", "Time"}, new Object[]{});
                Intent intent = new Intent(NotePadDetailActivity.this, NoteActivity.class);
                startActivity(intent);
                NotePadDetailActivity.this.finish();
            }
        }
    }
}
