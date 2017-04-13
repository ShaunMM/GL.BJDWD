package com.bjdwd.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/4/5.
 */

public class JSONConvertTool {

    //将json 数组转换为Map对象
    public static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = (String) keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JSONObject对象转成List对象
    public static List<Map<String, Object>> jsonObjectToList(
            JSONObject jsonObject, String[] keyNames, String key) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        JSONArray jsonArray = null;
        try {
            if (key != null) {
                jsonArray = jsonObject.getJSONArray(key);
            } else {
                return null;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<String, Object>();

                for (int j = 0; j < keyNames.length; j++) {
                    map.put(keyNames[j], jsonObject2.get(keyNames[j]));
                }
                list.add(map);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //把json 转换为ArrayList形式
    public static List<Map<String, Object>> getList(String jsonString) {

        List<Map<String, Object>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject;
            list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                list.add(getMap(jsonObject.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //提取返回数据中的jsonarray
    public static List getlist2(String responsejson) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(responsejson);
            JSONArray array = (JSONArray) object.get("data");
            Map<String, String> hashMap = new HashMap<String, String>();
            list = getList(array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //得到键值对的值数组
    public static Object[] getDBkeystring(String[] strings, Map<String, Object> map) {
        Object[] o = new Object[strings.length];
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals("Id")) {
                o[i] = Integer.parseInt(map.get("Id") + "");
            } else
                o[i] = map.get(strings[i]);
        }
        return o;
    }

    //将毫秒转成时间yyyy-MM-dd HH:mm
    public static String GetStringFromLong(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = new Date(millis);
        return sdf.format(dt);
    }
}
