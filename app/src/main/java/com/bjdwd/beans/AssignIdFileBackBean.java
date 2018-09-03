package com.bjdwd.beans;

import java.util.List;

/**
 * Created by dell on 2017/5/18.
 */

public class AssignIdFileBackBean {
    /**
     * code : 200
     * msg : 数据请求成功
     * data : [{"Id":2,"DirName":"作业指导书","DepartmentId":0,"ParentId":0,"IsTopestDir":true,"CreateTime":"2017-05-12T09:46:49.24","LastModifyTime":"2017-05-12T09:46:49.24","CreatorId":1,"IsDeleted":false,"IsCommon":true},{"Id":3,"DirName":"北京电务段一区文件夹","DepartmentId":0,"ParentId":1,"IsTopestDir":false,"CreateTime":"2017-05-12T10:37:46.807","LastModifyTime":"2017-05-12T10:37:46.807","CreatorId":6,"IsDeleted":false,"IsCommon":false}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * Id : 2
         * DirName : 作业指导书
         * DepartmentId : 0
         * ParentId : 0
         * IsTopestDir : true
         * CreateTime : 2017-05-12T09:46:49.24
         * LastModifyTime : 2017-05-12T09:46:49.24
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
}
