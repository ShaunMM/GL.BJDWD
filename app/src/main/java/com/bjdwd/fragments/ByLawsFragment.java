package com.bjdwd.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.test.mock.MockApplication;
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
import com.bjdwd.filedownloader.FileUtil;
import com.bjdwd.filedownloader.SDCardHelper;
import com.bjdwd.interfaces.FragmentBackListener;
import com.bjdwd.tools.DialogTool;
import com.bjdwd.tools.FileShowTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.WiFiTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/27.
 */
public class ByLawsFragment extends Fragment implements FragmentBackListener {
    private View view;
    private TextView tv_currentfunction;
    private ListView lv_details;
    private List<Map> fileLists; //Adapter数据源
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
    private String memudId;

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    DialogTool.cancelprogressDialog();
                    List<Map> mapList = SQLClass.getFileDataToId(handleDBHelper, "DirectoryInfo", fileInfoId);
                    if (mapList.size() != 0) {
                        FileShowTool.setFileShow(getContext(), mapList.get(0));
                    }
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 2) {
                DialogTool.cancelprogressDialog();
            } else if (msg.arg1 == 3) {
                DialogTool.cancelprogressDialog();
                ShowToastTool.showToast(getContext(), "网络请求下载文件失败");
            } else if (msg.arg1 == 4) {
                DialogTool.cancelprogressDialog();
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
                memudId = String.valueOf(map.get("MemuId"));//MemuId      ParentId" = FileDirectoryId": 4,
                allFolderName.add(map.get("DirectoryName").toString());
                allFolderParentId.add(directoryId);

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
                            DialogTool.setprogressDialog(getContext(), "文件下载中");
                            final Map<String, String> finalMap = map;
                            BJDWDApplication.getExecutorService().execute(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isSave = false;
                                    boolean isUpdDate = false;
                                    String filepath = finalMap.get("Path").toString();
                                    String fileName = filepath.split("/")[filepath.split("/").length - 1];
                                    String loadfilepath = LiteralClass.LOAD_FILE_URL + filepath;
                                    if (finalMap.get("Extension").equals(".zip") || finalMap.get("Extension").equals(".rar")) {
                                        //获得压缩文件
                                        byte[] bytes = HttpTool.getByteContent(loadfilepath);
                                        if (bytes != null && bytes.length != 0) {
                                            isSave = SDCardHelper.saveFileToSDCardCustomDir(bytes, "BJDWD", fileName);
                                        }
                                        if (isSave) {
                                            try {
                                                String loachpath = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
//                                                String filepathzip = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
//                                                String unzipfilepath = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
//                                                FileUtil.unZipFile(filepathzip, unzipfilepath);
//                                                String filetestpath = FileUtil.getFileDir(unzipfilepath);
                                                isUpdDate = handleDBHelper.update("DirectoryInfo", new String[]{"LocaPath", "Isload"},
                                                        new Object[]{loachpath, "true"},
                                                        new String[]{"Id"}, new String[]{String.valueOf(finalMap.get("Id"))});
                                                if (isUpdDate) {
                                                    requestCode = 1;
                                                } else {
                                                    requestCode = 2;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            requestCode = 3;
                                        }
                                    } else {
                                        //非压缩文件
                                        byte[] bytes = HttpTool.getByteContent(loadfilepath);
                                        if (bytes != null && bytes.length != 0) {
                                            isSave = SDCardHelper.saveFileToSDCardCustomDir(bytes, "BJDWD", fileName);
                                            if (isSave) {
                                                String loachpath = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
                                                isUpdDate = handleDBHelper.update("DirectoryInfo",
                                                        new String[]{"LocaPath", "Isload"}, new Object[]{loachpath, "true"}, new String[]{"Id"},
                                                        new String[]{String.valueOf(finalMap.get("Id"))});
                                                if (isUpdDate) {
                                                    fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", memudId);
                                                    requestCode = 1;
                                                } else {
                                                    requestCode = 2;
                                                }
                                            } else {
                                                requestCode = 3;
                                            }
                                        }
                                    }

                                    Message loginMessage = new Message();
                                    loginMessage.arg1 = requestCode;
                                    downloadHandler.sendMessage(loginMessage);
                                }
                            });
                        } else {
                            FileShowTool.setFileShow(getContext(), map);
                        }
                    } else {
                        ShowToastTool.showToast(getContext(), "无线网络未连接...");
                    }
                }

            }
        });
    }


    public void getTopestDir(final String directoryId) {
        if (WiFiTool.isWifi(getContext())) {
            DialogTool.setprogressDialog(getContext(), "目录更新中");
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
}