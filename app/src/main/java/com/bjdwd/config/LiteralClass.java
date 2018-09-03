package com.bjdwd.config;

/**
 * Created by dell on 2017/4/10.
 */

public class LiteralClass {

    public static final String LOSE_EFFICACY_CODE = "40317";
    public static final String API_FILE = "/api/file/";
    public static final String APP_TOKEN = "?app_token=";
    public static final String ENCODING = "utf-8";

    public static final long period = 60000;//1分钟
    public static final long delay = 2000;//1秒

    //检查目录更新
    public static final String UPDATE_TABLE = "/api/update/check/";
    //获取最新添加的文件
    public static final String UPDATE_FILE = "/api/file/check/";

    //获取指定Id的目录数据  /directory/getupdated?app_token=xxx   POST请求
//    public static final String CATALOG_DATA = "http://192.168.0.124:8093/api/directory/getupdated";


}
