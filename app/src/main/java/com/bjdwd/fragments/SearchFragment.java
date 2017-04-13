package com.bjdwd.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bjdwd.R;
import com.bjdwd.activitys.HomeActivity;
import com.bjdwd.activitys.MainActivity;
import com.bjdwd.adapters.DocumentsShowAdapter;
import com.bjdwd.adapters.SearchHintAdapter;
import com.bjdwd.config.SQLClass;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.interfaces.FragmentBackListener;
import com.bjdwd.tools.FileShowTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by dell on 2017/3/27.
 * 搜索
 */

public class SearchFragment extends Fragment implements View.OnClickListener, FragmentBackListener {
    private View view;
    private EditText ed_search;
    private Button bt_search;
    private ListView lv_searchhint;
    private ListView lv_searchresults;
    private HandleDBHelper handleDBHelper;
    private DocumentsShowAdapter documentsShowAdapter;
    private List<String> allFolderName;
    private List<String> allFolderParentId;
    private String pathname;
    private int fileid;

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
        handleDBHelper = HandleDBHelper.getInstance(getContext());
        allFolderName = new ArrayList<>();
        allFolderParentId = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ed_search = (EditText) view.findViewById(R.id.ed_search);//输入关键字搜索
        lv_searchhint = (ListView) view.findViewById(R.id.lv_searchhint);//搜索提示
        lv_searchresults = (ListView) view.findViewById(R.id.lv_searchresults);
        bt_search = (Button) view.findViewById(R.id.bt_search);//搜索按钮
        bt_search.setOnClickListener(this);
        setsearchedittext();
    }

    //搜索
    private void setsearchedittext() {
        ed_search.addTextChangedListener(new TextWatcher() {
            SearchHintAdapter searchHintAdapter = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    lv_searchhint.setVisibility(View.GONE);
                } else {
                    List<Map> searchcodelist = SQLClass.getSearchHints(handleDBHelper, "SysTypeInfo", s.toString());
                    List<String> listkeycodes = new ArrayList<>();
                    if (searchcodelist.size() > 0) {
                        //有搜索提示的
                        for (int i = 0; i < searchcodelist.size(); i++) {
                            String keycode = searchcodelist.get(i).get("Keywords") + "";
                            listkeycodes.add(keycode);
                        }

                        if (listkeycodes.size() > 0) {
                            lv_searchhint.setVisibility(View.VISIBLE);
                            if (searchHintAdapter != null) {
                                searchHintAdapter.setData(listkeycodes);
                            } else {
                                searchHintAdapter = new SearchHintAdapter(getActivity(), listkeycodes);
                            }
                            lv_searchhint.setAdapter(searchHintAdapter);
                            final List<String> finalListkeycodes = listkeycodes;
                            lv_searchhint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    String code = finalListkeycodes.get(i).toString();
                                    ed_search.setText(code);
                                    ed_search.setSelection(code.length());
                                    showClickHintResult(code, i);
                                }
                            });
                        }
                    } else {
                        //没有搜索提示的
                        List<Map> localFileMaps = new ArrayList<>();
                        String str = ed_search.getText().toString();
                        localFileMaps = SQLClass.getSearchFiles(handleDBHelper, "DepartmentFile", str);
                        showNoHintResult(localFileMaps);
                    }
                }
            }
        });
    }

    //点击搜索提示  搜索结果显示
    private void showClickHintResult(String code, int position) {
        //查系统字典
        List<Map> fileLists = new ArrayList<>();
        fileLists = SQLClass.getSearchFiles(handleDBHelper, "SysTypeInfo", code);

        //根据文件信息表 FileInfo
        Map fileInfoMap = SQLClass.getFileInfoToId(handleDBHelper, "FileInfo", fileLists.get(position).get("FileInfoId").toString());
        String fileExtension = fileInfoMap.get("FileExtensions").toString();
        fileExtension.toLowerCase();
        if (!fileExtension.equals("")) {
            FileShowTool.setFileShow(getContext(), fileInfoMap);
        } else {
            //点击进来不是文件夹情况
            //根基文件夹 Id 继续查询数据库
            Map folderInfoMap = fileLists.get(position);
            fileLists.clear();
            fileLists = SQLClass.getDataToId(handleDBHelper, "DirectoryInfo", String.valueOf(folderInfoMap.get("FileInfoId")));
            documentsShowAdapter.setData(fileLists);
            lv_searchresults.setAdapter(documentsShowAdapter);
        }
    }

    //无搜索提示  搜索结果显示
    private void showNoHintResult(List<Map> localFileMaps) {
        ((InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (lv_searchhint.getVisibility() == View.VISIBLE) {
            lv_searchhint.setVisibility(View.GONE);
        }

        if (documentsShowAdapter == null) {
            documentsShowAdapter = new DocumentsShowAdapter(getActivity(), localFileMaps);
        } else {
            documentsShowAdapter.setData(localFileMaps);
        }
        lv_searchresults.setAdapter(documentsShowAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            List<Map> localFileMaps = new ArrayList<>();
            String str = ed_search.getText().toString();
            localFileMaps = SQLClass.getSearchFiles(handleDBHelper, "DirectoryInfo", str);
            showNoHintResult(localFileMaps);
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
    public void onbackForward() {
        Intent intent = new Intent();
        intent.setClass(getContext(), MainActivity.class);
        startActivity(intent);
        ((HomeActivity) getActivity()).finish();
    }
}