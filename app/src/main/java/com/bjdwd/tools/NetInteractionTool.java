package com.bjdwd.tools;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell on 2017/3/23.
 */
public class NetInteractionTool {

    public static String APPDOOR2 = "http://192.168.1.108:8080";
    public static String APPKEY = APPDOOR2 + "/App/Index?";
    public static String APPKEYQUOTA = APPDOOR2 + "/App/QuotaRecordUpload?";

    public static String APPKEY3 = APPKEY + "signature=bcad117ce31ac75fcfa347acefc8d198&";
    public static String APPKEY2 = APPDOOR2 + "/App/Index";
    public static String SIGNATURE = "signature=bcad117ce31ac75fcfa347acefc8d198";
    public static String Uploadeduri = APPKEY + "signature=bcad117ce31ac75fcfa347acefc8d198&TableName=DbUpdateLog&Operate=6&StartId=";

    //需要上传的数据表单（判断是否更新数据）
    public static String[] uptablenames = {"InstructorTempTake", "InstructorTeach",
            "InstructorRepair", "InstructorPlan", "InstructorPeccancy", "InstructorLocoQuality",
            "InstructorKeyPerson", "InstructorGoodJob", "InstructorCheck", "InstructorWifiRecord", "InstructorAnalysis", "Feedback",
            "ExamRecords", "ExceptionLog"};

    // 将list<Map> 转换成 json字符串
    public static StringBuffer getjson(List<Map> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Map<String, Object> map : list) {
            for (String key : map.keySet()) {
                sb.append("\"").append(key).append("\":\"").append(map.get(key))
                        .append("\"").append(",");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}");
        return sb;
    }

    public static StringBuffer getjsonlist(List<Map> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("{");
            for (Object key : list.get(i).keySet()) {
                sb.append("\"").append(key).append("\":\"").append(list.get(i).get(key))
                        .append("\"").append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("}");
            sb.append(",");
        }
        if (list.size() != 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("]");
        return sb;
    }

    /**
     * 根据字段进行提取List<string>集合
     */
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

    /**
     * 写个去除数组中重复数据的方法
     */
    public static String[] array_unique(String[] a) {
        // array_unique
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 根据code筛选出list集合去重
     * 添加第一个
     */
    public static List<Map<String, Object>> singleTableUpdataList(List<Map<String, Object>> list, String code) {

        List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
        Set<String> keysSet = new HashSet<String>();
        for (Map<String, Object> map : list) {
            String keys = map.get(code) + "";
            int beforeSize = keysSet.size();
            keysSet.add(keys);
            int afterSize = keysSet.size();
            if (afterSize == beforeSize + 1) {
                tmpList.add(map);
            }
        }
        return tmpList;
    }

    /**
     * 根据字段code类中的内容筛选
     */
    public static List<Map<String, Object>> getSingleTableNameList(List<Map<String, Object>> maps, String code, Object s) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            if (map.get(code).equals(s)) {
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 单张表的URL获取
     *
     * @param Ids        想要获取数据的id集合
     * @param tableNames 表名
     */
    public static String getaddurl(List<String> Ids, String tableNames) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(");
        for (int i = 0; i < Ids.size(); i++) {
            stringBuffer.append(Ids.get(i));
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        stringBuffer.append(")");
        // Uri.encode("Comdition=Id In");
        String addurl = APPKEY + Uri.encode("Comdition=Id In") + stringBuffer.toString() + "&TableName=" + tableNames + "&Operate=6&" + SIGNATURE;
        return addurl;
    }

    public static Map<String, String> getcontention(List<String> Idscomditionid, String tablename) {

        Map<String, String> map = new HashMap<>();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Id In(");

        for (int i = 0; i < Idscomditionid.size(); i++) {
            stringBuffer.append(Idscomditionid.get(i));
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.lastIndexOf(","));
        stringBuffer.append(")");
        String s = stringBuffer.toString();

        map.put("Comdition", s);
        map.put("TableName", tablename);
        map.put("Operate", "6");
        map.put("signature", "bcad117ce31ac75fcfa347acefc8d198");
        return map;
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