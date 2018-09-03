package com.bjdwd.activitys;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.adapters.MainGVAdapter;
import com.bjdwd.beans.FileRequestBackBean;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.dbutils.WRDatabase;
import com.bjdwd.filedownloader.SDCardHelper;
import com.bjdwd.receivers.NetReceiver;
import com.bjdwd.tools.CustomDialog;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.StorageTool;
import com.bjdwd.tools.StringListUtils;
import com.bjdwd.tools.WiFiTool;
import com.google.gson.Gson;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by BYJ on 2017/3/22.
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

    // 消息通知
    private NotificationManager notificationManager;
    private final static int NOTIF_ID = 1;
    private Notification.Builder builder;
    private Notification notification;
    private NetReceiver netReceiver = new NetReceiver();

    private Timer timer;
    private Timer accessTimer;
    private Timer progressDialogTimer;
    private Timer updateTimer;

    private ProgressDialog progressDialog;
    private DownloadRequest downloadRequest;
    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    //cancelprogressDialog();
                    if (mainGVAdapter == null) {
                        mainGVAdapter = new MainGVAdapter(MainActivity.this, functionsItemList);
                        gv_functions.setAdapter(mainGVAdapter);
                    } else {
                        mainGVAdapter.setData(functionsItemList);
                    }
                    //获取顶级目录下目录
                    getData();
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 0) {
                ShowToastTool.showToast(MainActivity.this, "登录已过最长时效，需重新登录");
                systemConfig.setFirstUse(true);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.arg1 == 2) {
                cancelprogressDialog();
                ShowToastTool.showToast(MainActivity.this, "暂无新的更新资料...");
            } else if (msg.arg1 == 7) {
                systemConfig.setFirstUse(false);
                if (mainGVAdapter == null) {
                    mainGVAdapter = new MainGVAdapter(MainActivity.this, functionsItemList);
                    gv_functions.setAdapter(mainGVAdapter);
                } else {
                    mainGVAdapter.setData(functionsItemList);
                }
                setprogressDialog("首次使用数据初始化文件下载中...");
                openTimer();
                downloadInfor();
            } else if (msg.arg1 == 8) {
                cancelprogressDialog();
                if (!systemConfig.isFirstUse()) {
                    Toast toast = Toast.makeText(MainActivity.this, "数据获取结束、文件下载完毕...", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else if (msg.arg1 == 9) {
                cancelprogressDialog();
                ShowToastTool.showToast(MainActivity.this, "下载文件到平板出现问题...");
            } else if (msg.arg1 == 10) {
                if (topDirListMap.size() != 0) {
                    functionsItemList.clear();
                    initAdapterData(topDirListMap);
                    if (mainGVAdapter == null) {
                        mainGVAdapter = new MainGVAdapter(MainActivity.this, functionsItemList);
                        gv_functions.setAdapter(mainGVAdapter);
                    } else {
                        mainGVAdapter.setData(functionsItemList);

                    }
                }
            } else if (msg.arg1 == 11) {
                //非第一次更新资料获取到数据
                if (functionsItemList.size() != 0) {
                    if (mainGVAdapter == null) {
                        mainGVAdapter = new MainGVAdapter(MainActivity.this, functionsItemList);
                        gv_functions.setAdapter(mainGVAdapter);
                    } else {
                        mainGVAdapter.setData(functionsItemList);
                    }
                    //接着更新目录
                    upadatefile();
                }
            } else if (msg.arg1 == 12) {
                //非第一次更新资料下载文件
                cancelprogressDialog();
                //删除后台已经删除的文件
                deleteInfor();
                //查看需要下载的
                needFiles();
            } else if (msg.arg1 == 13) {
                initNotification();
                getUpdata();
            } else if (msg.arg1 == 14) {
                //非第一次更新资料未获取到数据
                cancelprogressDialog();
            }
        }
    };


    private static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        netReceiver.setMainActivityClass(MainActivity.this);

        BJDWDApplication.addActivity(this);
        initData();
        initView();
    }

    private void openTimer() {
        progressDialogTimer = new Timer();
        progressDialogTimer.schedule(new TimerTask() {
            public void run() {
                cancelprogressDialog();
                addLocalPath();
                this.cancel();
            }
        }, 1000 * 60 * 5);
    }

    private void initData() {
        gv_functions = (GridView) findViewById(R.id.gv_functions);
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
        systemConfig = SystemConfigFactory.getInstance(MainActivity.this).getSystemConfig();

        if (WiFiTool.isWifi(this) && systemConfig.isFirstUse()) {
            getTopestDir();
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                    requestCode = 10;
                    Message loginMessage = downloadHandler.obtainMessage();
                    loginMessage.arg1 = requestCode;
                    downloadHandler.sendMessage(loginMessage);
                }
            }, 300);
        }
    }

    private void initView() {
        ll_datadownload = (LinearLayout) findViewById(R.id.ll_datadownload);
        ll_datadownload.setOnClickListener(this);

        gv_functions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("dirId", String.valueOf(topDirListMap.get(position).get("DirId")));
                bundle.putString("dirName", topDirListMap.get(position).get("DirName").toString());
                bundle.putString("position", String.valueOf(position));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        //注册网络监听广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_datadownload) {
            if (WiFiTool.isWifi(this)) {
                getUpdata();
            } else {
                ShowToastTool.showToast(MainActivity.this, "WiFi未连接...");
            }
        }
    }

    public void getTopestDir() {
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
                        Gson gson = new Gson();
                        FileRequestBackBean fileRequestBackBean = new FileRequestBackBean();
                        fileRequestBackBean = gson.fromJson(fileReceipt, FileRequestBackBean.class);
                        if (fileRequestBackBean.getData().getDirs().size() <= 0) {
                            requestCode = 14;
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

    public void getData() {
        //根据顶级目录更新其余目录
        BJDWDApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                if (topDirListMap.size() != 0) {
                    for (int i = 0; i < topDirListMap.size(); i++) {
                        getFileDirData((int) topDirListMap.get(i).get("DirId"));

                        if (i == topDirListMap.size() - 1) {
                            //先查现有顶级目录的更新   --->查更新回执和文件跟新
                            String httpUrl = systemConfig.getHost() + systemConfig.getWebPort();
                            String fileparm = LiteralClass.UPDATE_FILE + systemConfig.getNewFileMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                            String parm = LiteralClass.UPDATE_TABLE + systemConfig.getCatalogMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                            //更新回执
                            String updataReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                            //文件回执
                            String newFileReceipt = HttpTool.submitGetRequest(httpUrl, fileparm, LiteralClass.ENCODING);
                            boolean wrnewfileisok = false;
                            try {
                                JSONObject json = new JSONObject(updataReceipt);
                                String jsonArray = json.getString("data");
                                List<Map<String, Object>> list = StringListUtils.getList(jsonArray);
                                //服务器有更新操作  --> (1)修改最大ID (2)发通知更新
                                int maxId = Integer.parseInt(systemConfig.getCatalogMaxId()) + list.size();
                                systemConfig.setCatalogMaxId(maxId + "");

                                JSONObject jsonObject = new JSONObject(newFileReceipt);
                                if (jsonObject.getString("code").equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                                    requestCode = 0;
                                } else {
                                    wrnewfileisok = WRDatabase.wrNewFile(handleDBHelper, newFileReceipt);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (wrnewfileisok) {
                                requestCode = 7;
                            }
                        }
                    }
                }
                Message loginMessage = downloadHandler.obtainMessage();
                loginMessage.arg1 = requestCode;
                downloadHandler.sendMessage(loginMessage);
            }
        });
    }

    public void getUpdata() {
        if (!systemConfig.isFirstUse()) {
            setprogressDialog("正在检测服务端是否有数据更新...获取数据中");
        }
        BJDWDApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (systemConfig.isFirstUse()) {
                    topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                    if (topDirListMap.size() != 0) {
                        for (int i = 0; i < topDirListMap.size(); i++) {
                            getFileDirData((int) topDirListMap.get(i).get("DirId"));

                            if (i == topDirListMap.size() - 1) {
                                //先查现有顶级目录的更新   --->查更新回执和文件跟新
                                String httpUrl = systemConfig.getHost() + systemConfig.getWebPort();
                                String fileparm = LiteralClass.UPDATE_FILE + systemConfig.getNewFileMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                                String parm = LiteralClass.UPDATE_TABLE + systemConfig.getCatalogMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                                //更新回执
                                String updataReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                                //文件回执
                                String newFileReceipt = HttpTool.submitGetRequest(httpUrl, fileparm, LiteralClass.ENCODING);
                                boolean wrnewfileisok = false;
                                try {
                                    JSONObject json = new JSONObject(updataReceipt);
                                    String jsonArray = json.getString("data");
                                    List<Map<String, Object>> list = StringListUtils.getList(jsonArray);
                                    //服务器有更新操作  --> (1)修改最大ID (2)发通知更新
                                    int maxId = Integer.parseInt(systemConfig.getCatalogMaxId()) + list.size();
                                    systemConfig.setCatalogMaxId(maxId + "");

                                    JSONObject jsonObject = new JSONObject(newFileReceipt);
                                    if (jsonObject.getString("code").equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                                        requestCode = 0;
                                    } else {
                                        wrnewfileisok = WRDatabase.wrNewFile(handleDBHelper, newFileReceipt);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (wrnewfileisok) {
                                    requestCode = 7;
                                }
                            }
                        }
                    }
                } else {
                    //非第一次使用更新资料
                    //还是先查看顶级目录 --- 刷新界面    ----->接着其余更新目录 ---> 再下载文件
                    httpUrl = systemConfig.getHost() + systemConfig.getWebPort() + LiteralClass.API_FILE;
                    parm = "0" + LiteralClass.APP_TOKEN + systemConfig.getToken();
                    String fileReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                    boolean wrisok = false;
                    try {
                        JSONObject jsonObject = new JSONObject(fileReceipt);
                        if (jsonObject.get("code").toString().equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                            requestCode = 0;
                        } else {
                            Gson gson = new Gson();
                            FileRequestBackBean fileRequestBackBean = new FileRequestBackBean();
                            fileRequestBackBean = gson.fromJson(fileReceipt, FileRequestBackBean.class);
                            if (fileRequestBackBean.getData().getDirs().size() <= 0) {
                                requestCode = 14;
                            } else {
                                wrisok = WRDatabase.wrtopDir(handleDBHelper, fileReceipt);
                                if (wrisok) {
                                    functionsItemList.clear();
                                    topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                                    initAdapterData(topDirListMap);
                                    requestCode = 11;
                                } else {
                                    functionsItemList.clear();
                                    topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
                                    initAdapterData(topDirListMap);
                                    requestCode = 11;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Message loginMessage = downloadHandler.obtainMessage();
                loginMessage.arg1 = requestCode;
                downloadHandler.sendMessage(loginMessage);
            }
        });
    }

    private String deleteInfor() {
        List<Map> fileList = handleDBHelper.queryListMap("select * from DirectoryInfo where Isload = 'true' and DirectoryType ='file' and IsDeleted='1' ", null);
        if (fileList.size() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                String localPath = fileList.get(i).get("LocaPath").toString();
                File file = new File(localPath);
                deleteFile(file);
            }
        }
        return fileList.size() + "个文件";
    }

    private String downloadInfor() {
        List<Map> fileList = handleDBHelper.queryListMap("select * from DirectoryInfo where Isload = 'false' and DirectoryType ='file' and IsDeleted='0' ", null);
        downloadAndSave(fileList);
        return fileList.size() + "个文件";
    }

    private void downloadAndSave(final List<Map> fileinfor) {
        for (int j = 0; j < fileinfor.size(); j++) {
            final Map filemap = fileinfor.get(j);
            String filepath = filemap.get("Path") + "";
            Log.e("文件详细信息", "----->"+filemap.get("MemuId").toString()+"-->"+filemap.get("LocaPath").toString()+"-->"
                    +filepath+"-->"+filemap.get("DirectoryName").toString()+"-->"+filemap.get("IsDeleted").toString()+"-->"
                    +filemap.get("Isload").toString()+"-->"+filemap.get("Extension").toString());
            String url = systemConfig.getHost() + systemConfig.getFilePort() + filepath;
            final String fileName = filepath.split("/")[filepath.split("/").length - 1];

            downloadRequest = NoHttp.createDownloadRequest(url,
                    SDCardHelper.fileSdkPath(StorageTool.FILEPATH),
                    fileName,
                    true,
                    false);
            Log.e("文件本服务器路径", "----->" + filepath);
            String loachpath = SDCardHelper.fileSdkPath(StorageTool.FILEPATH) + File.separator + fileName;
            Log.i("文件本地路径", "----->" + loachpath);

            if (!filemap.get("Extension").equals(".zip")) {
                handleDBHelper.update("DirectoryInfo", new String[]{"LocaPath", "IsLoad"},
                        new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
            }

            BJDWDApplication.downloadQueue.add(0, downloadRequest, new DownloadListener() {
                @Override
                public void onDownloadError(int i, Exception e) {
                    Message messagefirst = downloadHandler.obtainMessage();
                    messagefirst.arg1 = 9;
                    downloadHandler.sendMessage(messagefirst);
                    Log.e("文件本服务器路径", "----->111");
                }

                @Override
                public void onStart(int i, boolean b, long l, Headers headers, long l1) {
                }

                @Override
                public void onProgress(int i, int i1, long l, long l1) {
                    Log.e("文件下载进度", "----->" + i + "====>" + i1 + "---->" + l + "====>" + l1);
                }

                @Override
                public void onFinish(int i, String s) {
                    systemConfig.setNewFileMaxId(fileinfor.size() + "");
                }

                @Override
                public void onCancel(int what) {
                    Message messagefirst = downloadHandler.obtainMessage();
                    messagefirst.arg1 = 9;
                    downloadHandler.sendMessage(messagefirst);
                    Log.e("文件下载", "--->下载取消");
                }
            });
        }
    }


    //zip的文件解压，下载下来的文件补添加本地路径
    private void addLocalPath() {
        List<Map> zipfiles = handleDBHelper.queryListMap("select * from DirectoryInfo where Isload = 'false' and Extension ='.zip' and IsDeleted='0' ", null);
        if (zipfiles.size() != 0) {
            fileIsExist(zipfiles);
        }
    }

    private void fileIsExist(List<Map> list) {
        for (int i = 0; i < list.size(); i++) {
            Map filemap = list.get(i);
            String filepath = filemap.get("Path") + "";
            final String fileName = filepath.split("/")[filepath.split("/").length - 1];
            String loachpath = SDCardHelper.fileSdkPath(StorageTool.FILEPATH) + File.separator + fileName;
            File file = new File(loachpath);
            if (file.exists()) {
                final String fileSavePath = StorageTool.getuuid();//解压文件保存路径

                if (filemap.get("FileExtension").equals(".zip")) {
                    try {
                        String unzipfilepath = loachpath + fileSavePath;
                        StorageTool.unZipFile(loachpath, unzipfilepath);
                        String filetestpath = StorageTool.getFileDir(unzipfilepath);
                        handleDBHelper.update("DirectoryInfo", new String[]{"LocaPath", "IsLoad"},
                                new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});

                        Log.e("添加文件本地路径zip", "---->" + loachpath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("添加文件本地路径非zip", "---->" + loachpath);
                    handleDBHelper.update("DirectoryInfo", new String[]{"LocaPath", "IsLoad"},
                            new Object[]{loachpath, "true"}, new String[]{"Id"}, new String[]{filemap.get("Id") + ""});
                }
            }
        }
    }

    private CustomDialog downloaddialog;

    private void updateTimer() {
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            public void run() {
                cancelprogressDialog();
                addLocalPath();
                this.cancel();
            }
        }, 1000 * 60 * 3);
    }

    private void needFiles() {
        List<Map> needFilesList = handleDBHelper.queryListMap("select * from DirectoryInfo where Isload = 'false' and DirectoryType ='file' and IsDeleted='0' ", null);
        if (needFilesList.size() > 0) {
            updateTimer();
            selectDownload(needFilesList);
            downloaddialog.show();
        } else {
            ShowToastTool.showToast(this, "无需要下载的文件！");
        }
    }


    private void selectDownload(final List<Map> needlists) {
        downloaddialog = new CustomDialog(this, R.style.mydialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_selectdownload, null);
        downloaddialog.setContentView(contentView);
        downloaddialog.setCanceledOnTouchOutside(true);

        Window dialogWindow = downloaddialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        lp.width = (int) (width * 0.5);
        lp.height = (int) (height * 0.4);
        dialogWindow.setAttributes(lp);

        Button bt_announcement = (Button) contentView.findViewById(R.id.bt_announcement);
        Button bt_close = (Button) contentView.findViewById(R.id.bt_close);


        bt_announcement.setText("开始下载" + needlists.size() + "个最新文件");
        bt_announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
                downloadAndSave(needlists);
            }
        });

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloaddialog != null) {
                    downloaddialog.dismiss();
                }
            }
        });
    }

    private void dialog() {
        if (downloaddialog != null) {
            downloaddialog.dismiss();
            setprogressDialog("正在下载文件中请耐心等待...");
        }
    }

    private void getFileDirData(final int dirParentID) {
        final String httpUrl = systemConfig.getHost() + systemConfig.getWebPort();
        final String parm = LiteralClass.API_FILE + dirParentID + LiteralClass.APP_TOKEN + systemConfig.getToken();
        String fileReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
        boolean wrisok = false;
        try {
            JSONObject jsonObject = new JSONObject(fileReceipt);
            if (jsonObject.getString("code").equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                requestCode = 0;
            } else {
                traversalFolder(fileReceipt, dirParentID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void traversalFolder(String fileReceipt, final int dirParentID) {
        String httpUrls = systemConfig.getHost() + systemConfig.getWebPort();
        Gson gson = new Gson();
        FileRequestBackBean fileRequestBackBean = new FileRequestBackBean();
        fileRequestBackBean = gson.fromJson(fileReceipt, FileRequestBackBean.class);
        FileRequestBackBean.DataBean.DirsBean dirsBean = new FileRequestBackBean.DataBean.DirsBean();

        WRDatabase.wrTables(handleDBHelper, fileReceipt, String.valueOf(dirParentID));

        if (fileRequestBackBean.getData().getDirs().size() > 0) {
            for (int i = 0; i < fileRequestBackBean.getData().getDirs().size(); i++) {
                dirsBean = fileRequestBackBean.getData().getDirs().get(i);
                final String parms = LiteralClass.API_FILE + dirsBean.getId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                String fileReceipts = HttpTool.submitGetRequest(httpUrls, parms, LiteralClass.ENCODING);
                WRDatabase.wrTables(handleDBHelper, fileReceipts, String.valueOf(dirsBean.getId()));
                traversalFolder(fileReceipts, dirsBean.getId());
            }
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

    private void upadatefile() {
        topDirListMap = SQLClass.getDataToParentId(handleDBHelper, "DirsInfo", String.valueOf(0));
        BJDWDApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                if (topDirListMap.size() != 0) {
                    for (int i = 0; i < topDirListMap.size(); i++) {
                        getFileDirData((int) topDirListMap.get(i).get("DirId"));
                        if (i == topDirListMap.size() - 1) {
                            String httpUrl = systemConfig.getHost() + systemConfig.getWebPort();
                            String fileparm = LiteralClass.UPDATE_FILE + systemConfig.getNewFileMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                            String parm = LiteralClass.UPDATE_TABLE + systemConfig.getCatalogMaxId() + LiteralClass.APP_TOKEN + systemConfig.getToken();
                            //更新回执
                            String updataReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                            //文件回执
                            String newFileReceipt = HttpTool.submitGetRequest(httpUrl, fileparm, LiteralClass.ENCODING);
                            boolean wrnewfileisok = false;
                            try {
                                JSONObject json = new JSONObject(updataReceipt);
                                String jsonArray = json.getString("data");
                                List<Map<String, Object>> list = StringListUtils.getList(jsonArray);
                                //服务器有更新操作  --> (1)修改最大ID (2)发通知更新
                                int maxId = Integer.parseInt(systemConfig.getCatalogMaxId()) + list.size();
                                systemConfig.setCatalogMaxId(maxId + "");

                                JSONObject jsonObject = new JSONObject(newFileReceipt);
                                if (jsonObject.getString("code").equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                                    requestCode = 0;
                                } else {
                                    requestCode = 12;
                                    wrnewfileisok = WRDatabase.wrNewFile(handleDBHelper, newFileReceipt);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (wrnewfileisok) {
                                requestCode = 12;
                            }
                        }
                    }

                }
                Message messageupdata = downloadHandler.obtainMessage();
                messageupdata.arg1 = requestCode;
                downloadHandler.sendMessage(messageupdata);
            }
        });
    }

    public void initNotification() {
        if (builder == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            builder = new Notification.Builder(getApplicationContext());
        }
        sendNotification();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void sendNotification() {
        notification = builder.setSmallIcon(R.drawable.bjdwd).setContentTitle("北京电务段车载设备作业指导系统").setContentText("   检测到服务器端有更新...")
                .setWhen(System.currentTimeMillis()).setAutoCancel(true).build();
        notificationManager.notify(NOTIF_ID, notification);
    }

    public void setprogressDialog(String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_HOLO_DARK);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(str + "....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        try {
            if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelprogressDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netReceiver);
    }
}