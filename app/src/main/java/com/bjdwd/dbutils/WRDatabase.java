package com.bjdwd.dbutils;

import com.bjdwd.beans.FileRequestBackBean;
import com.bjdwd.config.SQLClass;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/4/10.
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
        handleDBHelper.delete("FileInfo", new String[]{"FileDirectoryId"}, new String[]{dirParentID});
        handleDBHelper.delete("DirectoryInfo", new String[]{"MemuId"}, new String[]{dirParentID});

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

        return (dirsisok && directoryisok);
    }

}
