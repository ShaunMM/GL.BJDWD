package com.bjdwd.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dell on 2017/3/23.
 */
public class SystemConfig implements ISystemConfig {
    private final static String CONFIG_FILENAME = "appbase";
    private final static String ISFIRSTUSE = "isfirstuse";
    private final static String ISFIRSTLOGIN = "isfirstlogin";
    private final static String ISFIRSTSYNC = "isfirstsync";
    private final static String USERNAME = "username";
    private final static String USERACCOUNT = "useraccount";
    private final static String PASSWORD = "password";
    private final static String USERTYPE = "usertype";
    private final static String USERID = "userid";
    private final static String DEPARTMENTID = "departmentid";
    private final static String PARENTDEPARTMENTID = "parentdepartmentid";
    private final static String HOST = "host";
    private final static String WEBPORT = "WebPort";
    private final static String TOKEN = "token";

    private Context context;
    private SharedPreferences systemConfigSharedPreferences;

    public SystemConfig(Context ctx) {
        this.context = ctx;
        SharedPreferences sp = this.context.getSharedPreferences(CONFIG_FILENAME, Context.MODE_WORLD_READABLE);
        this.setSystemConfigSharedPreferences(sp);
    }

    public SharedPreferences getSystemConfigSharedPreferences() {
        return systemConfigSharedPreferences;
    }

    public void setSystemConfigSharedPreferences(SharedPreferences systemConfigSharedPreferences) {
        this.systemConfigSharedPreferences = systemConfigSharedPreferences;
    }

    @Override
    public void setUserName(String username) {
        this.getSystemConfigSharedPreferences().edit().putString(USERNAME, username).commit();

    }

    @Override
    public String getUserName() {
        return this.getSystemConfigSharedPreferences().getString(USERNAME, "");
    }

    @Override
    public void setUserAccount(String useraccount) {
        this.getSystemConfigSharedPreferences().edit().putString(USERACCOUNT, useraccount).commit();

    }

    @Override
    public String getUserAccount() {
        return this.getSystemConfigSharedPreferences().getString(USERACCOUNT, "");
    }

    @Override
    public void setPassword(String password) {
        this.getSystemConfigSharedPreferences().edit().putString(PASSWORD, password).commit();

    }

    @Override
    public String getPassword() {
        return this.getSystemConfigSharedPreferences().getString(PASSWORD, "");
    }

    @Override
    public int getUserType() {
        return this.getSystemConfigSharedPreferences().getInt(USERTYPE, 0);
    }

    @Override
    public void setUserType(int usertype) {
        this.getSystemConfigSharedPreferences().edit().putInt(USERTYPE, usertype).commit();

    }

    @Override
    public int getUserId() {
        return this.getSystemConfigSharedPreferences().getInt(USERID, 0);
    }

    @Override
    public void setUserId(int userid) {
        this.getSystemConfigSharedPreferences().edit().putInt(USERID, userid).commit();

    }

    @Override
    public int getDepartmentId() {
        return this.getSystemConfigSharedPreferences().getInt(DEPARTMENTID, 0);
    }

    @Override
    public void setDepartmentId(int departmentid) {
        this.getSystemConfigSharedPreferences().edit().putInt(DEPARTMENTID, departmentid).commit();
    }

    @Override
    public int getParentDepartmentId() {
        return this.getSystemConfigSharedPreferences().getInt(PARENTDEPARTMENTID, 0);
    }

    @Override
    public void setParentDepartmentId(int parentdepartmentid) {
        this.getSystemConfigSharedPreferences().edit().putInt(PARENTDEPARTMENTID, parentdepartmentid).commit();

    }

    @Override
    public String getHost() {
        return this.getSystemConfigSharedPreferences().getString(HOST, "http://192.168.1.103:");
    }

    @Override
    public void setHost(String host) {
        this.getSystemConfigSharedPreferences().edit().putString(HOST, host).commit();

    }

    @Override
    public int getWebPort() {
        return this.getSystemConfigSharedPreferences().getInt(WEBPORT, 8016);
    }

    @Override
    public void setWebPort(int port) {
        this.getSystemConfigSharedPreferences().edit().putInt(WEBPORT, port).commit();

    }

    @Override
    public String getToken() {
        return this.getSystemConfigSharedPreferences().getString(TOKEN, "");
    }

    @Override
    public void setToken(String token) {
        this.getSystemConfigSharedPreferences().edit().putString(TOKEN, token).commit();
    }

    @Override
    public boolean isFirstUse() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISFIRSTUSE, true);
    }

    @Override
    public void setFirstUse(boolean isFirstUse) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISFIRSTUSE, isFirstUse).commit();

    }

    @Override
    public boolean isFirstLogin() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISFIRSTLOGIN, true);
    }

    @Override
    public void setFirstLogin(boolean isFirstLogin) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISFIRSTLOGIN, isFirstLogin).commit();

    }

    @Override
    public boolean isFirstSync() {
        return this.getSystemConfigSharedPreferences().getBoolean(ISFIRSTSYNC, true);
    }

    @Override
    public void setFirstSync(boolean isFirstSync) {
        this.getSystemConfigSharedPreferences().edit().putBoolean(ISFIRSTSYNC, isFirstSync).commit();

    }

}
