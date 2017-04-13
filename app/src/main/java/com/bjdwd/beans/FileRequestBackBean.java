package com.bjdwd.beans;

import java.util.List;

/**
 * Created by dell on 2017/4/5.
 */

public class FileRequestBackBean {
    /**
     * code : 200
     * msg : 数据请求成功
     * data : {"dirs":[{"Id":9,"DirName":"测试-文件夹1","DepartmentId":1,"ParentId":4,"IsTopestDir":false,"CreateTime":"2017-04-01T18:39:26.487","LastModifyTime":"2017-04-01T18:39:26.487","CreatorId":1,"IsDeleted":false,"IsCommon":true},{"Id":10,"DirName":"测试-文件夹2","DepartmentId":1,"ParentId":4,"IsTopestDir":false,"CreateTime":"2017-04-01T18:39:34.533","LastModifyTime":"2017-04-01T18:39:34.533","CreatorId":1,"IsDeleted":false,"IsCommon":true}],"files":[{"Id":1,"FileName":"中国铁路总公司《铁路技术管理规程》(高速铁路部分)","FileInfoId":15,"FileDirectoryId":4,"SystemUserId":1,"LastModifyTime":"2017-04-01T14:25:54.143","IsDeleted":false,"Extension":".doc","Size":11087360,"Path":"/docs/2017-4-1/7d8bc6c1-8c5c-4be0-bb25-28feb4fa690d.doc","UploadTime":"2017-04-01T09:16:32.417","FileIsDeleted":false,"HashCode":"54b346253068680477c3c290f55368bc","DepartmentId":0,"IsCommon":true},{"Id":2,"FileName":"运规","FileInfoId":14,"FileDirectoryId":4,"SystemUserId":1,"LastModifyTime":"2017-04-01T14:28:06.253","IsDeleted":false,"Extension":".doc","Size":154624,"Path":"/docs/2017-4-1/a671d9db-f309-4188-accb-0d681b5fa702.doc","UploadTime":"2017-04-01T09:16:32.06","FileIsDeleted":false,"HashCode":"2393e761f48a5b82b052353837d80432","DepartmentId":0,"IsCommon":true},{"Id":4,"FileName":"CRH系列动车组操作规程铁运190号文件","FileInfoId":16,"FileDirectoryId":4,"SystemUserId":1,"LastModifyTime":"2017-04-01T15:04:20.32","IsDeleted":false,"Extension":".rtf","Size":438885,"Path":"/docs/2017-4-1/39f66d88-0ef4-4411-b2d0-aebb4df00fde.rtf","UploadTime":"2017-04-01T15:04:20.34","FileIsDeleted":false,"HashCode":"0b9ccfe4732be22db5ac9319dba99e46","DepartmentId":0,"IsCommon":true}]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DirsBean> dirs;
        private List<FilesBean> files;

        public List<DirsBean> getDirs() {
            return dirs;
        }

        public void setDirs(List<DirsBean> dirs) {
            this.dirs = dirs;
        }

        public List<FilesBean> getFiles() {
            return files;
        }

        public void setFiles(List<FilesBean> files) {
            this.files = files;
        }

        public static class DirsBean {
            /**
             * Id : 9
             * DirName : 测试-文件夹1
             * DepartmentId : 1
             * ParentId : 4
             * IsTopestDir : false
             * CreateTime : 2017-04-01T18:39:26.487
             * LastModifyTime : 2017-04-01T18:39:26.487
             * CreatorId : 1
             * IsDeleted : false
             * IsCommon : true
             */

            private int Id;
            private String DirName;
            private int DepartmentId;
            private int ParentId;
            private boolean IsTopestDir;
            private String CreateTime;
            private String LastModifyTime;
            private int CreatorId;
            private boolean IsDeleted;
            private boolean IsCommon;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getDirName() {
                return DirName;
            }

            public void setDirName(String DirName) {
                this.DirName = DirName;
            }

            public int getDepartmentId() {
                return DepartmentId;
            }

            public void setDepartmentId(int DepartmentId) {
                this.DepartmentId = DepartmentId;
            }

            public int getParentId() {
                return ParentId;
            }

            public void setParentId(int ParentId) {
                this.ParentId = ParentId;
            }

            public boolean isIsTopestDir() {
                return IsTopestDir;
            }

            public void setIsTopestDir(boolean IsTopestDir) {
                this.IsTopestDir = IsTopestDir;
            }

            public String getCreateTime() {
                return CreateTime;
            }

            public void setCreateTime(String CreateTime) {
                this.CreateTime = CreateTime;
            }

            public String getLastModifyTime() {
                return LastModifyTime;
            }

            public void setLastModifyTime(String LastModifyTime) {
                this.LastModifyTime = LastModifyTime;
            }

            public int getCreatorId() {
                return CreatorId;
            }

            public void setCreatorId(int CreatorId) {
                this.CreatorId = CreatorId;
            }

            public boolean isIsDeleted() {
                return IsDeleted;
            }

            public void setIsDeleted(boolean IsDeleted) {
                this.IsDeleted = IsDeleted;
            }

            public boolean isIsCommon() {
                return IsCommon;
            }

            public void setIsCommon(boolean IsCommon) {
                this.IsCommon = IsCommon;
            }
        }

        public static class FilesBean {
            /**
             * Id : 1
             * FileName : 中国铁路总公司《铁路技术管理规程》(高速铁路部分)
             * FileInfoId : 15
             * FileDirectoryId : 4
             * SystemUserId : 1
             * LastModifyTime : 2017-04-01T14:25:54.143
             * IsDeleted : false
             * Extension : .doc
             * Size : 11087360
             * Path : /docs/2017-4-1/7d8bc6c1-8c5c-4be0-bb25-28feb4fa690d.doc
             * UploadTime : 2017-04-01T09:16:32.417
             * FileIsDeleted : false
             * HashCode : 54b346253068680477c3c290f55368bc
             * DepartmentId : 0
             * IsCommon : true
             */

            private int Id;
            private String FileName;
            private int FileInfoId;
            private int FileDirectoryId;
            private int SystemUserId;
            private String LastModifyTime;
            private boolean IsDeleted;
            private String Extension;
            private int Size;
            private String Path;
            private String UploadTime;
            private boolean FileIsDeleted;
            private String HashCode;
            private int DepartmentId;
            private boolean IsCommon;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getFileName() {
                return FileName;
            }

            public void setFileName(String FileName) {
                this.FileName = FileName;
            }

            public int getFileInfoId() {
                return FileInfoId;
            }

            public void setFileInfoId(int FileInfoId) {
                this.FileInfoId = FileInfoId;
            }

            public int getFileDirectoryId() {
                return FileDirectoryId;
            }

            public void setFileDirectoryId(int FileDirectoryId) {
                this.FileDirectoryId = FileDirectoryId;
            }

            public int getSystemUserId() {
                return SystemUserId;
            }

            public void setSystemUserId(int SystemUserId) {
                this.SystemUserId = SystemUserId;
            }

            public String getLastModifyTime() {
                return LastModifyTime;
            }

            public void setLastModifyTime(String LastModifyTime) {
                this.LastModifyTime = LastModifyTime;
            }

            public boolean isIsDeleted() {
                return IsDeleted;
            }

            public void setIsDeleted(boolean IsDeleted) {
                this.IsDeleted = IsDeleted;
            }

            public String getExtension() {
                return Extension;
            }

            public void setExtension(String Extension) {
                this.Extension = Extension;
            }

            public int getSize() {
                return Size;
            }

            public void setSize(int Size) {
                this.Size = Size;
            }

            public String getPath() {
                return Path;
            }

            public void setPath(String Path) {
                this.Path = Path;
            }

            public String getUploadTime() {
                return UploadTime;
            }

            public void setUploadTime(String UploadTime) {
                this.UploadTime = UploadTime;
            }

            public boolean isFileIsDeleted() {
                return FileIsDeleted;
            }

            public void setFileIsDeleted(boolean FileIsDeleted) {
                this.FileIsDeleted = FileIsDeleted;
            }

            public String getHashCode() {
                return HashCode;
            }

            public void setHashCode(String HashCode) {
                this.HashCode = HashCode;
            }

            public int getDepartmentId() {
                return DepartmentId;
            }

            public void setDepartmentId(int DepartmentId) {
                this.DepartmentId = DepartmentId;
            }

            public boolean isIsCommon() {
                return IsCommon;
            }

            public void setIsCommon(boolean IsCommon) {
                this.IsCommon = IsCommon;
            }
        }
    }
}