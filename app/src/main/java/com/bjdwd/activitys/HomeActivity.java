package com.bjdwd.activitys;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.adapters.HomeAdapter;

import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.dbutils.WRDatabase;
import com.bjdwd.fragments.ByLawsFragment;

import com.bjdwd.interfaces.FragmentBackListener;
import com.bjdwd.tools.DialogTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dell on 2017/3/27.
 */
public class HomeActivity extends FragmentActivity {

    private ListView lv_topdir;
    private FragmentManager manager;
    private ByLawsFragment byLawsFragment;

    private int positionId;  //点击的位置
    private int dirParentID;  //点击的文件父ID
    private String dirName; //点击的大功能名

    private ISystemConfig systemConfig;
    private String httpUrl;
    private HandleDBHelper handleDBHelper;
    private int requestCode = -1;
    private List<Map> fileLists; //目录数据源

    private List<Map> topDirListMap;
    private List<Map<String, Object>> functionsItemList = new ArrayList<Map<String, Object>>();
    private HomeAdapter homeAdapter;

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    DialogTool.cancelprogressDialog();
                    if (byLawsFragment == null) {
                        initFragments();
                    }
                    initView(positionId);
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 0) {
                ShowToastTool.showToast(HomeActivity.this, "登录已过最长时效，需重新登录");
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.arg1 == 2) {
                DialogTool.cancelprogressDialog();
                if (byLawsFragment == null) {
                    initFragments();
                }
                initView(positionId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BJDWDApplication.addActivity(this);
        systemConfig = SystemConfigFactory.getInstance(HomeActivity.this).getSystemConfig();
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
        httpUrl = systemConfig.getHost() + systemConfig.getWebPort() + LiteralClass.API_FILE;

        dirParentID = Integer.parseInt(getIntent().getStringExtra("dirId"));
        dirName = getIntent().getStringExtra("dirName");
        positionId = Integer.parseInt(getIntent().getStringExtra("position"));

        lv_topdir = (ListView) findViewById(R.id.lv_topdir);
        initTopDIrData();

        initFragments();
        initView(positionId);
    }


    private void initTopDIrData() {
        topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
        initAdapterData(topDirListMap);
        if (functionsItemList.size() != 0) {
            homeAdapter = new HomeAdapter(this, functionsItemList);
            lv_topdir.setAdapter(homeAdapter);
        }
    }

    private void initAdapterData(List<Map> topDirListMap) {
        TypedArray typedArrayicos = getResources().obtainTypedArray(R.array.functionsicon);
        for (int i = 0; i < topDirListMap.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            int ico = typedArrayicos.getResourceId(i, 0);
            map.put("funcationName", topDirListMap.get(i).get("DirName").toString());
            map.put("funcationIco", ico);
            functionsItemList.add(map);
        }
    }

    private void initFragments() {
        manager = getSupportFragmentManager();
        byLawsFragment = new ByLawsFragment();
    }

    private void initView(int position) {
        lvItemChecked(position);
        lv_topdir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionId = position;
                if (byLawsFragment == null) {
                    initFragments();
                }
                initView(positionId);
            }
        });
    }

    public void lvItemChecked(int position) {
        homeAdapter.changeSelected(position);
        showFragmentByPosition(position);
    }

    public void showFragmentByPosition(int position) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (byLawsFragment.isAdded()) {
            transaction.remove(byLawsFragment);
            byLawsFragment = new ByLawsFragment();
        }

        transaction.add(R.id.frg_conn, byLawsFragment);
        Bundle bundle = new Bundle();
        bundle.putString("dirId", String.valueOf(topDirListMap.get(position).get("DirId")));
        bundle.putString("dirName", topDirListMap.get(position).get("DirName").toString());
        byLawsFragment.setArguments(bundle);
        transaction.show(byLawsFragment);
        transaction.commit();
    }

    private FragmentBackListener backListener;
    private boolean isInterception = false;

    public FragmentBackListener getBackListener() {
        return backListener;
    }

    public void setBackListener(FragmentBackListener backListener) {
        this.backListener = backListener;
    }

    public boolean isInterception() {
        return isInterception;
    }

    public void setInterception(boolean isInterception) {
        this.isInterception = isInterception;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isInterception()) {
                if (backListener != null) {
                    backListener.onbackForward();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        backListener = null;
        super.onDestroy();
        BJDWDApplication.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}