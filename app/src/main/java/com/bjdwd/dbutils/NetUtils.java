package com.bjdwd.dbutils;

import android.util.Log;

import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.LiteralClass;
import com.bjdwd.config.SQLClass;
import com.bjdwd.tools.HttpRequestUtil;
import com.bjdwd.tools.StringListUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 网络地址及网络操作
 */
public class NetUtils {
    private static String httpurl;

    //根据字段进行提取List<string>集合
    public static List<String> getTableNamelist(List<Map<String, Object>> mapList, String code) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < mapList.size(); i++) {
            list.add(mapList.get(i).get(code) + "");
        }
        list = removeDuplicate(list);
        return list;
    }

    //list<String>去重
    public static List<String> removeDuplicate(List<String> list) {
        HashSet<String> h = new HashSet<String>(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    //根据字段code类中的内容筛选
    public static List<Map<String, Object>> getSingleTableNameList(List<Map<String, Object>> maps, String code, Object s) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            if (map.get(code).equals(s)) {
                list.add(map);
            }
        }
        return list;
    }

    //拼接 获取指定Id的目录数据（POST） 请求体
    public static Map<String, String> getcontention(List<String> targetId) {
        Map<String, String> m = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");

        for (int i = 0; i < targetId.size(); i++) {
            stringBuffer.append(targetId.get(i));
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        stringBuffer.append("]");
        String s = stringBuffer.toString();
        m.put("ids", s);
        return m;
    }

//    public static void Dosingletableinsert(List<Map<String, Object>> addTableDatalist, HandleDBHelper handleDBHelper, ISystemConfig systemConfig) {
//        HttpURLConnection conn = null;
//        if (addTableDatalist.size() != 0) {
//            List<String> Ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
//            try {
//                httpurl = LiteralClass.CATALOG_DATA + LiteralClass.APP_TOKEN + systemConfig.getToken();
//                conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(httpurl, getcontention(Ids), null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            String addjson = getresponse(conn);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(addjson);
//                String jsonArray = jsonObject.getString("data");
//                List<Map<String, Object>> jsonarraulist = StringListUtils.getList(jsonArray);
//                for (int i = 0; i < jsonarraulist.size(); i++) {
//                    //先查询是否存在不存在插入
//                    List<Map> list = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", jsonarraulist.get(i).get("Id").toString());
//                    if (list.size() == 0) {
//                        WRDatabase.wrtopDirUpdate(handleDBHelper, addjson);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public static void DosingletableUpdata(List<Map<String, Object>> addTableDatalist, HandleDBHelper handleDBHelper, ISystemConfig systemConfig) {
//        HttpURLConnection conn = null;
//        if (addTableDatalist.size() != 0) {
//            List<String> Ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
//            try {
//                httpurl = LiteralClass.CATALOG_DATA + LiteralClass.APP_TOKEN + systemConfig.getToken();
//                conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(httpurl, getcontention(Ids), null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            String updatejson = getresponse(conn);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(updatejson);
//                String jsonArray = jsonObject.getString("data");
//                List<Map<String, Object>> jsonarraulist = StringListUtils.getList(jsonArray);
//                for (int i = 0; i < jsonarraulist.size(); i++) {
//                    //先删除再插入
//                    boolean isdelete = false;
//                    boolean isok = false;
//
//                    List<Map> list = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", jsonarraulist.get(i).get("Id").toString());
//                    if (list.size() != 0) {
//                        isdelete = SQLClass.deleteData(handleDBHelper, "DirsInfo", "DirId", jsonarraulist.get(i).get("Id").toString());
//                        isok = SQLClass.deleteData(handleDBHelper, "DirectoryInfo", "DirectoryId", jsonarraulist.get(i).get("Id").toString());
//                    } else {
//                        WRDatabase.wrtopDirUpdate(handleDBHelper, updatejson);
//                    }
//
//                    if (isdelete && isok) {
//                        WRDatabase.wrtopDirUpdate(handleDBHelper, updatejson);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    public static void DosingletableDelete(List<Map<String, Object>> addTableDatalist, HandleDBHelper handleDBHelper) throws ExecutionException {

        if (addTableDatalist.size() != 0) {
            List<String> ids = NetUtils.getTableNamelist(addTableDatalist, "TargetId");
            for (int i = 0; i < ids.size(); i++) {
                //先删除再插入
                boolean isdelete = false;
                boolean isok = false;

                List<Map> list = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", ids.get(i).toString());
                if (list.size() != 0) {
                    isdelete = SQLClass.deleteData(handleDBHelper, "DirsInfo", "DirId", ids.get(i).toString());
                    isok = SQLClass.deleteData(handleDBHelper, "DirectoryInfo", "DirectoryId", ids.get(i).toString());
                }
                if (isdelete && isok) {
                    Log.i("ASG", "have deleted");
                }
            }
        }
    }

    public static String getresponse(URLConnection connection) {
        String s = "";
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            isr = new InputStreamReader(is, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        br = new BufferedReader(isr);
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                s += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isr != null) {
            try {
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}