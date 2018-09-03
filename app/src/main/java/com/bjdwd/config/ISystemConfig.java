package com.bjdwd.config;

/**
 * Created by dell on 2017/3/23.
 */

public interface ISystemConfig {

    public void setUserName(String username);

    public String getUserName();

    public void setUserAccount(String useraccount);

    public String getUserAccount();

    public void setPassword(String password);

    public String getPassword();

    public int getUserType();

    public void setUserType(int usertype);

    public int getUserId();

    public void setUserId(int userid);

    public int getDepartmentId();

    public void setDepartmentId(int departmentid);

    public int getParentDepartmentId();

    public void setParentDepartmentId(int parentpepartmentid);

    public boolean isFirstUse();

    public void setFirstUse(boolean isFirstUse);

    public boolean isFirstLogin();

    public void setFirstLogin(boolean isFirstLogin);

    public boolean isFirstSync();

    public void setFirstSync(boolean isFirstSync);

    public String getHost();

    public void setHost(String host);

    public int getWebPort();

    public void setWebPort(int port);

    public int getFilePort();

    public void setFilePort(int port);

    public String getToken();

    public void setToken(String token);

    public String getCatalogMaxId();

    public void setCatalogMaxId(String catalogmaxid);

    //文件 上次检查更新所保存的最大id值
    public String getNewFileMaxId();

    public void setNewFileMaxId(String newfilemaxid);

    public boolean isGetPermission();

    public void setPermission(boolean isGetPermission);

}
