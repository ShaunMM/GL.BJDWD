package com.bjdwd.dbutils;

import android.util.Log;

import com.bjdwd.beans.AssignIdFileBackBean;
import com.bjdwd.beans.DBUpdateBackBean;
import com.bjdwd.beans.FileRequestBackBean;
import com.bjdwd.beans.NewFileBean;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.SQLClass;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by BYJ on 2017/4/10.
 */
public class WRDatabase {

    public static boolean wrTables(HandleDBHelper handleDBHelper, String fileReceipt, String dirParentID) {
        Gson gson = new Gson();
        FileRequestBackBean fileRequestBackBean = new FileRequestBackBean();
        fileRequestBackBean = gson.fromJson(fileReceipt, FileRequestBackBean.class);

        FileRequestBackBean.DataBean.DirsBean dirsBean = new FileRequestBackBean.DataBean.DirsBean();
        FileRequestBackBean.DataBean.FilesBean filesBean = new FileRequestBackBean.DataBean.FilesBean();

        boolean dirsisok = false;
        boolean fileisok = false;
        boolean directoryisok = false;

        handleDBHelper.delete("DirsInfo", new String[]{"ParentId"}, new String[]{dirParentID});
//      handleDBHelper.delete("FileInfo", new String[]{"FileDirectoryId"}, new String[]{dirParentID});
//      handleDBHelper.delete("DirectoryInfo", new String[]{"MemuId"}, new String[]{dirParentID});

        for (int i = 0; i < fileRequestBackBean.getData().getDirs().size(); i++) {
            dirsBean = fileRequestBackBean.getData().getDirs().get(i);

            //只写入没有下载过的数据
            List<Map> mapList = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", dirsBean.getId() + "");
            if (mapList.size() == 0) {

                dirsisok = handleDBHelper.insert("DirsInfo", new String[]{"DirId", "DirName", "DepartmentId", "ParentId",
                                "IsTopestDir", "CreateTime", "LastModifyTime", "CreatorId", "IsDeleted", "IsCommon"},
                        new Object[]{
                                dirsBean.getId(),
                                dirsBean.getDirName(),
                                dirsBean.getDepartmentId(),
                                dirsBean.getParentId(),
                                dirsBean.isIsTopestDir(),
                                dirsBean.getCreateTime(),
                                dirsBean.getLastModifyTime(),
                                dirsBean.getCreatorId(),
                                dirsBean.isIsDeleted(),
                                dirsBean.isIsCommon()});
                Log.i("============》", "wrtopDir:   "+" 执行啦00000000000000000000");
                Log.i("============》", "wrtopDir:   "+ dirsBean.getId() +"  "+dirsBean.getDirName()+"  ");

//                List<Map> list = SQLClass.getDataToDirId(handleDBHelper, "DirectoryInfo", dirsBean.getId() + "");
                List<Map> list = SQLClass.getDataToDirName(handleDBHelper, "DirectoryInfo", dirsBean.getDirName() + "");
                if (list.size()==0) {
                    Log.i("============》", "DirectoryInfo:   "+" 执行啦");
                    Log.i("============》", "DirectoryInfo:   "+ dirsBean.getId() +"  "+dirsBean.getDirName()+"  "+"----》"+dirsBean.isIsDeleted());
                    directoryisok = handleDBHelper.insert("DirectoryInfo", new String[]{"DirectoryId", "DirectoryName", "DirectoryType", "Extension",
                                    "IsDeleted", "Isload", "Path", "LocaPath", "MemuId", "IsCommon"},
                            new Object[]{
                                    dirsBean.getId(),
                                    dirsBean.getDirName(),
                                    "dir",
                                    null,
                                    dirsBean.isIsDeleted(),
                                    "false",
                                    null,
                                    null,
                                    dirsBean.getParentId(),
                                    dirsBean.isIsCommon()

                            });
                }
            }
        }

        for (int n = 0; n < fileRequestBackBean.getData().getFiles().size(); n++) {
            filesBean = fileRequestBackBean.getData().getFiles().get(n);
            if (filesBean.getSize() == 0) {
                fileisok = true;
            } else {
                List<Map> mapList = SQLClass.getDataToFileInfo(handleDBHelper, "FileInfo", filesBean.getFileInfoId() + "");
                if (mapList.size() == 0) {
                    fileisok = handleDBHelper.insert("FileInfo", new String[]{"FileName", "FileInfoId", "FileDirectoryId", "SystemUserId",
                                    "LastModifyTime", "IsDeleted", "Extension", "Size", "Path", "UploadTime", "FileIsDeleted",
                                    "HashCode", "DepartmentId", "IsCommon"},
                            new Object[]{
                                    filesBean.getFileName(),
                                    filesBean.getFileInfoId(),
                                    filesBean.getFileDirectoryId(),
                                    filesBean.getSystemUserId(),
                                    filesBean.getLastModifyTime(),
                                    filesBean.isIsDeleted(),
                                    filesBean.getExtension(),
                                    filesBean.getSize(),
                                    filesBean.getPath(),
                                    filesBean.getUploadTime(),
                                    filesBean.isFileIsDeleted(),
                                    filesBean.getHashCode(),
                                    filesBean.getDepartmentId(),
                                    filesBean.isIsCommon()});

                    Log.i("FileInfo", "FileInfo:   "+" 执行啦1111111111111111111111111111");
                    Log.i("FileInfo", "FileInfo insert DirectoryInfo:   "+ dirsBean.getId() +"  "+dirsBean.getDirName()+"  "+"---》"+ filesBean.isIsDeleted());

                    directoryisok = handleDBHelper.insert("DirectoryInfo", new String[]{"DirectoryId", "DirectoryName", "DirectoryType", "Extension",
                                    "IsDeleted", "Isload", "Path", "LocaPath", "MemuId", "IsCommon"},
                            new Object[]{
                                    filesBean.getFileInfoId(),
                                    filesBean.getFileName(),
                                    "file",
                                    filesBean.getExtension(),
                                    filesBean.isIsDeleted(),
                                    "false",
                                    filesBean.getPath(),
                                    null,
                                    filesBean.getFileDirectoryId(),
                                    filesBean.isIsCommon()
                            });
                }
            }
        }

        return (dirsisok && fileisok && directoryisok);
    }

    public static boolean wrtopDir(HandleDBHelper handleDBHelper, String fileReceipt) {
        Gson gson = new Gson();
        FileRequestBackBean fileRequestBackBean = new FileRequestBackBean();
        fileRequestBackBean = gson.fromJson(fileReceipt, FileRequestBackBean.class);
        FileRequestBackBean.DataBean.DirsBean dirsBean = new FileRequestBackBean.DataBean.DirsBean();

        boolean dirsisok = false;
        boolean directoryisok = false;

        handleDBHelper.delete("DirsInfo", new String[]{"ParentId"}, new String[]{"0"});
        handleDBHelper.delete("DirectoryInfo", new String[]{"MemuId"}, new String[]{"0"});

        for (int i = 0; i < fileRequestBackBean.getData().getDirs().size(); i++) {
            dirsBean = fileRequestBackBean.getData().getDirs().get(i);
            //只写入没有下载过的数据
            List<Map> mapList = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", dirsBean.getId() + "");
            if (mapList.size() == 0) {
                dirsisok = handleDBHelper.insert("DirsInfo", new String[]{"DirId", "DirName", "DepartmentId", "ParentId",
                                "IsTopestDir", "CreateTime", "LastModifyTime", "CreatorId", "IsDeleted", "IsCommon"},
                        new Object[]{
                                dirsBean.getId(),
                                dirsBean.getDirName(),
                                dirsBean.getDepartmentId(),
                                dirsBean.getParentId(),
                                dirsBean.isIsTopestDir(),
                                dirsBean.getCreateTime(),
                                dirsBean.getLastModifyTime(),
                                dirsBean.getCreatorId(),
                                dirsBean.isIsDeleted(),
                                dirsBean.isIsCommon()});
                Log.i("============》", "wrtopDir:   "+" 执行啦2222222222222222222222222");
                Log.i("============》", "wrtopDir:   "+ dirsBean.getId() +"  "+dirsBean.getDirName()+"  ");
                directoryisok = handleDBHelper.insert("DirectoryInfo", new String[]{"DirectoryId", "DirectoryName", "DirectoryType", "Extension",
                                "IsDeleted", "Isload", "Path", "LocaPath", "MemuId", "IsCommon"},
                        new Object[]{
                                dirsBean.getId(),
                                dirsBean.getDirName(),
                                "dir",
                                null,
                                dirsBean.isIsDeleted(),
                                "false",
                                null,
                                null,
                                dirsBean.getParentId(),
                                dirsBean.isIsCommon()

                        });
                Log.i("============》", "wrtopDir:   "+" 执行啦333333333333333333333333333");
                Log.i("============》", "wrtopDir:   "+ dirsBean.getId() +"  "+dirsBean.getDirName()+"  ");

            }
        }

        return (dirsisok && directoryisok);
    }

//    public static boolean wrtopDirUpdate(HandleDBHelper handleDBHelper, String fileReceipt) {
//        Gson gson = new Gson();
//        AssignIdFileBackBean assignIdFileBackBean = new AssignIdFileBackBean();
//        assignIdFileBackBean = gson.fromJson(fileReceipt, AssignIdFileBackBean.class);
//        AssignIdFileBackBean.DataBean dataBean = new AssignIdFileBackBean.DataBean();
//
//        boolean dirsisok = false;
//        boolean directoryisok = false;
//
//        for (int i = 0; i < assignIdFileBackBean.getData().size(); i++) {
//            dataBean = assignIdFileBackBean.getData().get(i);
//            //只写入没有下载过的数据
//            List<Map> mapList = SQLClass.getDataToDirInfo(handleDBHelper, "DirsInfo", dataBean.getId() + "");
//            if (mapList.size() == 0) {
//                dirsisok = handleDBHelper.insert("DirsInfo", new String[]{"DirId", "DirName", "DepartmentId", "ParentId",
//                                "IsTopestDir", "CreateTime", "LastModifyTime", "CreatorId", "IsDeleted", "IsCommon"},
//                        new Object[]{
//                                dataBean.getId(),
//                                dataBean.getDirName(),
//                                dataBean.getDepartmentId(),
//                                dataBean.getParentId(),
//                                dataBean.isIsTopestDir(),
//                                dataBean.getCreateTime(),
//                                dataBean.getLastModifyTime(),
//                                dataBean.getCreatorId(),
//                                dataBean.isIsDeleted(),
//                                dataBean.isIsCommon()});
//
//                directoryisok = handleDBHelper.insert("DirectoryInfo", new String[]{"DirectoryId", "DirectoryName", "DirectoryType", "Extension",
//                                "IsDeleted", "Isload", "Path", "LocaPath", "MemuId", "IsCommon"},
//                        new Object[]{
//                                dataBean.getId(),
//                                dataBean.getDirName(),
//                                "dir",
//                                null,
//                                dataBean.isIsDeleted(),
//                                "false",
//                                null,
//                                null,
//                                dataBean.getParentId(),
//                                dataBean.isIsCommon()
//
//                        });
//            }
//        }
//
//        return (dirsisok && directoryisok);
//    }


    //更新数据操作
//    public static boolean wrDBOperatingRecord(ISystemConfig systemConfig, HandleDBHelper handleDBHelper, String updataReceipt) {
//        Gson gson = new Gson();
//        DBUpdateBackBean dbUpdateBackBean = new DBUpdateBackBean();
//        dbUpdateBackBean = gson.fromJson(updataReceipt, DBUpdateBackBean.class);
//        DBUpdateBackBean.DataBean dataBean = new DBUpdateBackBean.DataBean();
//        boolean dbupdateisok = false;
//
//        // 往更新操作记录表1和表2 里边写数据
//        for (int i = 0; i < dbUpdateBackBean.getData().size(); i++) {
//            dataBean = dbUpdateBackBean.getData().get(i);
//
//            dbupdateisok = handleDBHelper.insert("DBOperatingRecord", new String[]{"TableName", "TargetId", "OperatingType",
//                            "UpdateTime"},
//                    new Object[]{
//                            dataBean.getTableName(),
//                            dataBean.getTargetId(),
//                            dataBean.getOperateType(),
//                            dataBean.getUpdateTime()});
//        }
//
//        List<String> tableName = new ArrayList<>();//更新的数据表名List
//        List<String> tableNameList = new ArrayList<>();//去重数据表名List
//        List<Map<String, Object>> firstpList = new ArrayList<>();
//        for (int i = 0; i < dbUpdateBackBean.getData().size(); i++) {
//            tableName.add(dbUpdateBackBean.getData().get(i).getTableName());
//            Map map = new HashMap();
//            map.put("TableName", dbUpdateBackBean.getData().get(i).getTableName());
//            map.put("TargetId", dbUpdateBackBean.getData().get(i).getTargetId());
//            map.put("OperatingType", dbUpdateBackBean.getData().get(i).getOperateType());
//            map.put("UpdateTime", dbUpdateBackBean.getData().get(i).getUpdateTime());
//            firstpList.add(map);
//        }
//
//        tableNameList = NetUtils.removeDuplicate(tableName);//去重以后修改的表名集合
//
//        //通过表名查询数据库获取目标ID list
//        if (tableNameList.size() != 0) {
//            for (int num = 0; num < tableNameList.size(); num++) {
//                //获取有对同一张有更新操作的表所有操作集合
//                List<Map<String, Object>> singleTableData = NetUtils.getSingleTableNameList(firstpList, "TableName", tableNameList.get(num));//同一表名集合
//                List<String> ListId = NetUtils.getTableNamelist(singleTableData, "TargetId");//目标ID去重集合
//                Map<String, Map> m = new HashMap<>();
//                for (int i = 0; i < singleTableData.size(); i++) {
//                    m.put(singleTableData.get(i).get("TargetId") + "", (Map) singleTableData.get(i));
//                }
//
//                List<Map<String, Object>> singleTableDatalist = new ArrayList<>();
//                for (int i = 0; i < ListId.size(); i++) {
//                    singleTableDatalist.add(m.get(ListId.get(i)));
//                }
//
//                //再根据操作类型字段分类 “1”插入操作  “2”更新操作  “3”删除操作
//                //相当于看成操作目录表
//                List<Map<String, Object>> addTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "OperatingType", 1);
//                if (addTableDatalist.size() != 0) {
//                    NetUtils.Dosingletableinsert(addTableDatalist, handleDBHelper, systemConfig);
//                }
//
//                List<Map<String, Object>> upTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "OperatingType", 2);
//                if (upTableDatalist.size() != 0) {
//                    NetUtils.DosingletableUpdata(upTableDatalist, handleDBHelper, systemConfig);
//                }
//
//                List<Map<String, Object>> deleteTableDatalist = NetUtils.getSingleTableNameList(singleTableDatalist, "OperatingType", 3);
//                try {
//                    if (deleteTableDatalist.size() != 0)
//                        NetUtils.DosingletableDelete(deleteTableDatalist, handleDBHelper);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return dbupdateisok;
//    }

    //通过文件类 Id 查文件夹
//    public static List<Map> getDataToDBOperatingRecord(HandleDBHelper handleDBHelper, String tablename, String code) {
//        String sql = "select * from " + tablename + " where  id > ?";
//        List<Map> listsearch = handleDBHelper.queryListMap(sql, new String[]{code});
//        return listsearch;
//    }

    //获取最新添加的文件
    public static boolean wrNewFile(HandleDBHelper handleDBHelper, String newFileReceipt) {
        Gson gson = new Gson();

        NewFileBean newFileBean = new NewFileBean();
        newFileBean = gson.fromJson(newFileReceipt, NewFileBean.class);
        NewFileBean.DataBean dataBean;
        boolean newfileisok = false;

        for (int i = 0; i < newFileBean.getData().size(); i++) {
            dataBean = newFileBean.getData().get(i);
            newfileisok = handleDBHelper.insert("NewFile", new String[]{"FileId", "Extension", "Size",
                            "Path", "HashCode", "UploadTime", "IsDeleted"},
                    new Object[]{
                            dataBean.getId(),
                            dataBean.getExtension(),
                            dataBean.getSize(),
                            dataBean.getPath(),
                            dataBean.getHashCode(),
                            dataBean.getUploadTime(),
                            dataBean.isIsDeleted()});
        }
        return newfileisok;
    }
}