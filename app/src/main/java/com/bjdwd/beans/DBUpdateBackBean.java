package com.bjdwd.beans;

import java.util.List;

/**
 * Created by dell on 2017/5/15.
 */

public class DBUpdateBackBean {

    /**
     * code : 200
     * msg : 数据请求成功
     * data : [{"Id":1,"TableName":"DepartFiles","TargetId":1,"OperateType":1,"UpdateTime":"2017-05-12T09:48:10.743"},{"Id":2,"TableName":"DepartFiles","TargetId":2,"OperateType":1,"UpdateTime":"2017-05-12T09:49:49.253"}]
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
         * Id : 1
         * TableName : DepartFiles
         * TargetId : 1
         * OperateType : 1
         * UpdateTime : 2017-05-12T09:48:10.743
         */

        private int Id;
        private String TableName;
        private int TargetId;
        private int OperateType;
        private String UpdateTime;

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getTableName() {
            return TableName;
        }

        public void setTableName(String TableName) {
            this.TableName = TableName;
        }

        public int getTargetId() {
            return TargetId;
        }

        public void setTargetId(int TargetId) {
            this.TargetId = TargetId;
        }

        public int getOperateType() {
            return OperateType;
        }

        public void setOperateType(int OperateType) {
            this.OperateType = OperateType;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }
    }
}
