package com.bjdwd.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.adapters.MainGVAdapter;
import com.bjdwd.beans.FileRequestBackBean;
import com.bjdwd.beans.LoginBackBean;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.dbutils.WRDatabase;
import com.bjdwd.tools.DialogTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.WiFiTool;
import com.google.gson.Gson;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/22.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private LinearLayout ll_datadownload;
    private GridView gv_functions;
    private MainGVAdapter mainGVAdapter;
    private ISystemConfig systemConfig;
    private HandleDBHelper handleDBHelper;
    private int requestCode = -1;
    private String httpUrl;
    private String parm;
    private List<Map> topDirListMap;
    private List<Map<String, Object>> functionsItemList = new ArrayList<Map<String, Object>>();

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    DialogTool.cancelprogressDialog();
                    if (mainGVAdapter == null) {
                        mainGVAdapter = new MainGVAdapter(MainActivity.this, functionsItemList);
                        gv_functions.setAdapter(mainGVAdapter);
                    } else {
                        mainGVAdapter.setData(functionsItemList);
                    }
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 0) {
                ShowToastTool.showToast(MainActivity.this, "登录已过最长时效，需重新登录");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.arg1 == 2) {
                DialogTool.cancelprogressDialog();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        gv_functions = (GridView) findViewById(R.id.gv_functions);
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(MainActivity.this).getSystemConfig();
        if (WiFiTool.isWifi(this)) {
            getTopestDir();
        } else {
            topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
            if (topDirListMap.size() != 0) {
                initAdapterData(topDirListMap);
                mainGVAdapter = new MainGVAdapter(this, functionsItemList);
                gv_functions.setAdapter(mainGVAdapter);
            }
        }
    }

    private void initView() {
        ll_datadownload = (LinearLayout) findViewById(R.id.ll_datadownload);
        ll_datadownload.setOnClickListener(this);
//        gv_functions = (GridView) findViewById(R.id.gv_functions);
        gv_functions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeActivity.class);
                Bundle bundle = new Bundle();
                //传顶级目录Id
                bundle.putString("dirId", String.valueOf(topDirListMap.get(position).get("DirId")));
                bundle.putString("dirName", topDirListMap.get(position).get("DirName").toString());
                bundle.putString("position", String.valueOf(position));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_datadownload) {
            getTopestDir();
        }
    }

    public void getTopestDir() {
        DialogTool.setprogressDialog(MainActivity.this, "目录更新中");
        BJDWDApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                httpUrl = systemConfig.getHost() + systemConfig.getWebPort() + LiteralClass.API_FILE;
                parm = "0" + LiteralClass.APP_TOKEN + systemConfig.getToken();
                String fileReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                boolean wrisok = false;
                try {
                    JSONObject jsonObject = new JSONObject(fileReceipt);
                    if (jsonObject.get("code").toString().equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                        requestCode = 0;
                    } else {
                        wrisok = WRDatabase.wrtopDir(handleDBHelper, fileReceipt);
                        if (wrisok) {
                            functionsItemList.clear();
                            topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                            initAdapterData(topDirListMap);
                            requestCode = 1;
                        } else {
                            requestCode = 2;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message loginMessage = downloadHandler.obtainMessage();
                loginMessage.arg1 = requestCode;
                downloadHandler.sendMessage(loginMessage);
            }
        });

    }

    private void initAdapterData(List<Map> topDirListMap) {
        TypedArray typedArrayicos = getResources().obtainTypedArray(R.array.functionsicon);
        TypedArray typedArraycolors = getResources().obtainTypedArray(R.array.functionsbgcolor);
        for (int i = 0; i < topDirListMap.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            int ico = typedArrayicos.getResourceId(i, 0);
            int color = typedArraycolors.getColor(i, 0);
            map.put("funcationName", topDirListMap.get(i).get("DirName").toString());
            map.put("funcationIco", ico);
            map.put("funcationBGColor", color);
            functionsItemList.add(map);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}