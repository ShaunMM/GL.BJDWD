package com.bjdwd.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.activitys.HomeActivity;
import com.bjdwd.activitys.LoginActivity;
import com.bjdwd.activitys.MainActivity;
import com.bjdwd.adapters.DocumentsShowAdapter;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.dbutils.WRDatabase;
import com.bjdwd.filedownloader.SDCardHelper;
import com.bjdwd.interfaces.FragmentBackListener;
import com.bjdwd.tools.FileShowTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.StorageTool;
import com.bjdwd.tools.WiFiTool;
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

/**
 * Created by BYJ on 2017/3/27.
 */
public class ByLawsFragment extends Fragment implements FragmentBackListener {
    private View view;
    private TextView tv_currentfunction;
    private ListView lv_details;
    private List<Map> fileLists; //Adapter数据源
    private List<Map> lists; //文件数据
    private HandleDBHelper handleDBHelper;
    private DocumentsShowAdapter documentsShowAdapter;
    private String dirName;
    private int dirId;
    private List<String> allFolderName;
    private List<String> allFolderParentId;
    private ISystemConfig systemConfig;
    private String httpUrl;
    private String parm;

    private int requestCode = -1;
    private String directoryId;
    private String fileInfoId;

    private ProgressDialog progressDialog;
    private DownloadRequest downloadRequest;

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    cancelprogressDialog();
                    List<Map> mapList = SQLClass.getFileDataToId(handleDBHelper, "DirectoryInfo", fileInfoId);
                    if (mapList.size() != 0) {
                        FileShowTool.setFileShow(getContext(), mapList.get(0));
                    }
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 0) {
                ShowToastTool.showToast(getContext(), "登录已过最长时效，需重新登录");
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else if (msg.arg1 == 2) {
                cancelprogressDialog();
            } else if (msg.arg1 == 3) {
                cancelprogressDialog();
                ShowToastTool.showToast(getContext(), "网络请求下载文件失败");
            } else if (msg.arg1 == 4) {
                cancelprogressDialog();
                setAdapterData();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            ((HomeActivity) context).setBackListener(this);
            ((HomeActivity) context).setInterception(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allFolderName = new ArrayList<>();
        allFolderParentId = new ArrayList<>();

        dirId = Integer.parseInt(getArguments().get("dirId").toString());
        dirName = getArguments().get("dirName").toString();
        allFolderName.add(dirName);
        allFolderParentId.add(String.valueOf(dirId));
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment, null);
        initView(view);
        return view;
    }

    private void initData() {
        handleDBHelper = HandleDBHelper.getInstance(getActivity());
        systemConfig = SystemConfigFactory.getInstance(getActivity()).getSystemConfig();
        fileLists = new ArrayList<>();
        fileLists = handleDBHelper.queryListMap("select * from NewFile ", null);
        fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", String.valueOf(dirId));

    }

    private void setAdapterData() {
        if (documentsShowAdapter == null) {
            documentsShowAdapter = new DocumentsShowAdapter(getContext(), fileLists);
            lv_details.setAdapter(documentsShowAdapter);
        } else {
            documentsShowAdapter.setData(fileLists);
        }
    }

    private void initView(View view) {
        tv_currentfunction = (TextView) view.findViewById(R.id.tv_currentfunction);
        lv_details = (ListView) view.findViewById(R.id.lv_details);
        tv_currentfunction.setText(dirName);

        if (fileLists.size() != 0) {
            setAdapterData();
        }

        lv_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = new HashMap<>();
                map = fileLists.get(position);
                fileInfoId = String.valueOf(map.get("Id"));
                directoryId = String.valueOf(map.get("DirectoryId"));
                allFolderName.add(map.get("DirectoryName").toString());
                allFolderParentId.add(directoryId);

                //先是文件夹和文件的分类
                if (map.get("DirectoryType").toString().equals("dir")) {
                    final Map folderInfoMap = map;
                    fileLists.clear();
                    if (WiFiTool.isWifi(getContext())) {
                        getTopestDir(directoryId);
                    } else {
                        fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", directoryId);
                        setAdapterData();
                        ShowToastTool.showToast(getContext(), "无线网络未连接...");
                    }
                } else {
                    if (map.get("LocaPath").length() == 0) {
                        if (WiFiTool.isWifi(getContext())) {
                            //下载修改方法
                            setprogressDialog("获取文件中...");
                            downloadAndSave(map);
                        } else {
                            FileShowTool.setFileShow(getContext(), map);
                        }
                    } else {
                        try {
                            List<Map> mapList = SQLClass.getFileDataToId(handleDBHelper, "DirectoryInfo", fileInfoId);
                            if (mapList.size() != 0) {
                                FileShowTool.setFileShow(getContext(), mapList.get(0));
                            }
                        } catch (UnsupportedOperationException u) {
                            u.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void downloadAndSave(final Map filemap) {
        String filepath = filemap.get("Path") + "";
        String url = systemConfig.getHost() + systemConfig.getFilePort() + filepath;
        final String fileName = filepath.split("/")[filepath.split("/").length - 1];

        downloadRequest = NoHttp.createDownloadRequest(url,
                SDCardHelper.fileSdkPath(StorageTool.FILEPATH),
                fileName,
                true,
                false);
        Log.i("文件服务器路径", "----->" + filepath);
        BJDWDApplication.downloadQueue.add(0, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int i, Exception e) {
                Message messagefirst = downloadHandler.obtainMessage();
                messagefirst.arg1 = 3;
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
                String loachpath = SDCardHelper.fileSdkPath(StorageTool.FILEPATH) + File.separator + fileName;
                addPath(loachpath, filemap);

                Message messagefirst = downloadHandler.obtainMessage();
                messagefirst.arg1 = 1;
                downloadHandler.sendMessage(messagefirst);
                Log.e("文件下载", "--->下载取消");
            }

            @Override
            public void onCancel(int what) {
                Message messagefirst = downloadHandler.obtainMessage();
                messagefirst.arg1 = 3;
                downloadHandler.sendMessage(messagefirst);
                Log.e("文件下载", "--->下载取消");
            }
        });

    }

    private void addPath(String loachpath, Map filemap) {
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

    public void getTopestDir(final String directoryId) {
        if (WiFiTool.isWifi(getContext())) {
            setprogressDialog("获取中...");
            BJDWDApplication.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    httpUrl = systemConfig.getHost() + systemConfig.getWebPort() + LiteralClass.API_FILE;
                    parm = directoryId + LiteralClass.APP_TOKEN + systemConfig.getToken();
                    String fileReceipt = HttpTool.submitGetRequest(httpUrl, parm, LiteralClass.ENCODING);
                    boolean wrisok = false;
                    try {
                        JSONObject jsonObject = new JSONObject(fileReceipt);
                        if (jsonObject.get("code").toString().equals(LiteralClass.LOSE_EFFICACY_CODE)) {
                            requestCode = 0;
                        } else {
                            wrisok = WRDatabase.wrTables(handleDBHelper, fileReceipt, String.valueOf(directoryId));
                            fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", directoryId);
                            requestCode = 4;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Message loginMessage = downloadHandler.obtainMessage();
                    loginMessage.arg1 = requestCode;
                    downloadHandler.sendMessage(loginMessage);
                }
            });

        } else {
            fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", directoryId);
            setAdapterData();
            ShowToastTool.showToast(getContext(), "无线网络未连接...");
        }
    }

    @Override
    public void onbackForward() {
        if (allFolderName.size() == 1) {
            Intent intent = new Intent();
            intent.setClass(getContext(), MainActivity.class);
            startActivity(intent);
            ((HomeActivity) getActivity()).finish();
        } else {
            allFolderName.remove(allFolderName.size() - 1);
            allFolderParentId.remove(allFolderParentId.size() - 1);
            fileLists.clear();
            fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", String.valueOf(allFolderParentId.get(allFolderParentId.size() - 1)));
            documentsShowAdapter.setData(fileLists);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).setBackListener(null);
            ((HomeActivity) getActivity()).setInterception(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fileLists = null;
        handleDBHelper = null;
        documentsShowAdapter = null;
    }

    public void setprogressDialog(String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
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
}