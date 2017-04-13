package com.bjdwd.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.activitys.HomeActivity;
import com.bjdwd.activitys.MainActivity;
import com.bjdwd.adapters.DocumentsShowAdapter;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.filedownloader.FileUtil;
import com.bjdwd.filedownloader.SDCardHelper;
import com.bjdwd.interfaces.FragmentBackListener;
import com.bjdwd.tools.DialogTool;
import com.bjdwd.tools.FileShowTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.WiFiTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/27.
 */

public class NormalTaskFragment extends Fragment implements FragmentBackListener {
    private View view;
    private TextView tv_currentfunction;
    private ListView lv_details;
    private List<Map> fileLists; //Adapter数据源
    private HandleDBHelper handleDBHelper;
    private DocumentsShowAdapter documentsShowAdapter;
    private String pathname;
    private int fileid;
    private List<String> allFolderName;
    private List<String> allFolderParentId;

    private int requestCode = -1;
    private String fileInfoId;

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                try {
                    DialogTool.cancelprogressDialog();
                    //打开文件
                    List<Map> mapList = SQLClass.getFileDataToId(handleDBHelper, "DirectoryInfo", fileInfoId);
                    if (mapList.size() != 0) {
                        FileShowTool.setFileShow(getContext(), mapList.get(0));
                    }
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (msg.arg1 == 2) {
                DialogTool.cancelprogressDialog();
                ShowToastTool.showToast(getContext(), "本地路径写入数据库失败");
            } else if (msg.arg1 == 3) {
                DialogTool.cancelprogressDialog();
                ShowToastTool.showToast(getContext(), "网络请求下载文件失败");
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
        fileid = 4;
        pathname = getArguments().get("filename").toString();
        allFolderName.add(pathname);
        allFolderParentId.add(String.valueOf(fileid));
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
        fileLists = new ArrayList<>();
        //查询目录表
        fileLists = SQLClass.getDirectoryDataToMemuId(handleDBHelper, "DirectoryInfo", String.valueOf(fileid));
    }

    private void initView(View view) {
        tv_currentfunction = (TextView) view.findViewById(R.id.tv_currentfunction);
        lv_details = (ListView) view.findViewById(R.id.lv_details);
        tv_currentfunction.setText(pathname);
        if (fileLists.size() != 0) {
            documentsShowAdapter = new DocumentsShowAdapter(getContext(), fileLists);
            lv_details.setAdapter(documentsShowAdapter);
        }

        //文件显示
        lv_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //先判断是否连接网络
                if (WiFiTool.isWifi(getContext())) {
                    Map<String, String> map = new HashMap<>();
                    map = fileLists.get(position);
                    fileInfoId = String.valueOf(map.get("Id")); //MemuId      ParentId" = FileDirectoryId": 4,
                    allFolderName.add(map.get("DirectoryName").toString());
                    allFolderParentId.add(fileInfoId);
                    //根基文件夹 Id 继续查询数据库 再判断是文件夹还是文件
                    if (map.get("DirectoryType").toString().equals("dir")) {

                        final Map folderInfoMap = map;
                        fileLists.clear();
                        fileLists = SQLClass.getDataToDirId(handleDBHelper, "DirectoryInfo", String.valueOf(folderInfoMap.get("DirectoryId")));
                        documentsShowAdapter.setData(fileLists);
                        tv_currentfunction.setText(getCurrentDirectory(allFolderName));
                    } else {
                        //文件是否已经下载
                        if (map.get("LocaPath").length() == 0) {
                            //进行网络 请求 下载数据
                            DialogTool.setprogressDialog(getContext(), "文件下载中");
                            final Map<String, String> finalMap = map;
                            BJDWDApplication.getExecutorService().execute(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isSave = false;
                                    boolean isUpdDate = false;
                                    String filepath = finalMap.get("Path").toString();
                                    String fileName = filepath.split("/")[filepath.split("/").length - 1];
                                    String loadfilepath = LiteralClass.LOAD_FILE_URL + filepath;//网络请求路径
                                    if (finalMap.get("Extension").equals(".zip")) {
                                        //获得压缩文件
                                        byte[] bytes = HttpTool.getByteContent(loadfilepath);
                                        if (bytes != null && bytes.length != 0) {
                                            isSave = SDCardHelper.saveFileToSDCardCustomDir(bytes, "BJDWD", fileName);
                                        }
                                        if (isSave) {
                                            try {
                                                String filepathzip = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
                                                String unzipfilepath = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
                                                FileUtil.unZipFile(filepathzip, unzipfilepath);
                                                String filetestpath = FileUtil.getFileDir(unzipfilepath);
                                                isUpdDate = handleDBHelper.update("DirectoryInfo", new String[]{"LocaPath", "Isload"},
                                                        new Object[]{filetestpath, "true"},
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
                                        byte[] bytes = HttpTool.getByteContent(loadfilepath);

                                        if (bytes != null && bytes.length != 0) {
                                            isSave = SDCardHelper.saveFileToSDCardCustomDir(bytes, "BJDWD", fileName);
                                            if (isSave) {
                                                String loachpath = SDCardHelper.fileSdkPath("BJDWD") + "/" + fileName;
                                                isUpdDate = handleDBHelper.update("DirectoryInfo",
                                                        new String[]{"LocaPath", "Isload"}, new Object[]{loachpath, "true"}, new String[]{"Id"},
                                                        new String[]{String.valueOf(finalMap.get("Id"))});
                                                if (isUpdDate) {
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
                            //已下载  就调用打开  (打开文件)
                            FileShowTool.setFileShow(getContext(), map);
                        }
                    }
                } else {
                    ShowToastTool.showToast(getContext(), "无线网络未连接...");
                }

            }
        });
    }

    private String getCurrentDirectory(List<String> list) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            s.append(list.get(i));
            s.append("〉");
        }
        s.deleteCharAt(s.lastIndexOf(" 〉"));
        String showpath = s.toString();
        return showpath;
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
            tv_currentfunction.setText(getCurrentDirectory(allFolderName));
            fileLists.clear();
            if (allFolderParentId.size() == 1) {
                fileLists = SQLClass.getDataToDirId(handleDBHelper, "DirectoryInfo", String.valueOf(allFolderParentId.get(allFolderParentId.size() - 1)));
            } else {

                fileLists = SQLClass.getDataToId(handleDBHelper, "DirectoryInfo", String.valueOf(allFolderParentId.get(allFolderParentId.size() - 1)));
            }
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