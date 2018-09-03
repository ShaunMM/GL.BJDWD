package com.bjdwd.config;

import android.util.Log;

import com.bjdwd.dbutils.HandleDBHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/28.
 */

public class SQLClass {

    public static List<Map> getAllData(HandleDBHelper handleDBHelper, String tablename) {
        String sql = "select * from " + tablename;
        List<Map> listsearch = handleDBHelper.queryListMap(sql, null);
        return listsearch;
    }

    //通过文件类 ParentId 查文件夹
    public static List<Map> getDataToParentId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where ParentId=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件类 Id 查文件夹
    public static List<Map> getDataToDirInfo(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where DirId=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件类 Id 查文件夹
    public static List<Map> getDataToFileInfo(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where FileInfoId=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件类 Id 查文件夹
    public static List<Map> getDataToId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where Id=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }


    //通过文件类 Id 查文件夹
    public static List<Map> getDataToDirId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where DirectoryId =?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    public static List<Map> getDataToDirName(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where DirectoryName =?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件类 Id 查文件夹
    public static List<Map> getFileDataToId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where Id=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过目录Id 查文件信息
    public static List<Map> getDirectoryDataToMemuId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where MemuId=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过FileDirectoryId 查文件信息
    public static List<Map> getDataToFileDirectoryId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + " where FileInfoId=?";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件Id查文件信息(只返回单个的文件信息)
    public static Map getFileInfoToId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + "tablename " + "where ParentId=?";
        Map listsearch = handleDBHelper.queryItemMap(sql, new String[]{code});
        return listsearch;
    }

    //通过文件Id查文件信息(只返回单个的文件信息)
    public static Map getOneFileInfoToId(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + tablename + "where FileInfoId=?";
        Map listsearch = handleDBHelper.queryItemMap(sql, new String[]{code});
        return listsearch;
    }

    //查询搜索提示
    public static List<Map> getSearchHints(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + "tablename " + " where FileName like ?";
        String str = "%" + code + "%";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{str, 0 + ""});
        return listsearch;
    }

    //通过文件名模糊查询（不出现提示情况下）
    public static List<Map> getSearchFiles(HandleDBHelper handleDBHelper, String tablename, String code) {
        String sql = "select * from " + "tablename " + " where FileName like ? and IsDelete=?";
        String str = "%" + code + "%";
        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{str, 0 + ""});
        return listsearch;
    }

    public static boolean deleteData(HandleDBHelper handleDBHelper, String tablename, String column, String code) {
        boolean isDelete = handleDBHelper.delete(tablename, new String[]{column}, new String[]{code});
        return isDelete;
    }

}
