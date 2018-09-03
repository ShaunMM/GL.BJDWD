package com.bjdwd.beans;

import java.util.List;

/**
 * Created by dell on 2017/5/17.
 */

public class NewFileBean {
    /**
     * code : 200
     * msg : 数据请求成功
     * data : [{"Id":1,"Extension":".doc","Size":151040,"Path":"/docs/2017-5-12/f229094b-086c-4f78-8622-4b8995c420cd.doc","HashCode":"49f2efd3f52489e77c422e36802210c8","UploadTime":"2017-05-12T09:48:10.693","IsDeleted":false},{"Id":2,"Extension":".mp3","Size":132746,"Path":"/docs/2017-5-12/4b00e194-61ad-44af-bd79-f389f7b4f4ef.mp3","HashCode":"db4bb056115a5750c9420ca9a11b8b88","UploadTime":"2017-05-12T09:49:49.213","IsDeleted":false},{"Id":3,"Extension":".mp4","Size":6028880,"Path":"/docs/2017-5-12/5592e99f-ff50-40fc-a21b-d09c8c88743b.mp4","HashCode":"d63aa18a14b541be5dd719a54e5f9e52","UploadTime":"2017-05-12T09:51:01.35","IsDeleted":false},{"Id":4,"Extension":".jpg","Size":177104,"Path":"/docs/2017-5-12/18aebf9c-acd6-4b30-876b-d322abe96da9.jpg","HashCode":"5c180abfd38f7a4fa44191eef14055a7","UploadTime":"2017-05-12T09:53:05.12","IsDeleted":false},{"Id":5,"Extension":".pdf","Size":3910870,"Path":"/docs/2017-5-12/3549b12a-a712-402d-9c01-41f2238b04ea.pdf","HashCode":"47a5d724e6b3aa816356523b648a5e5a","UploadTime":"2017-05-12T09:54:36.387","IsDeleted":false},{"Id":6,"Extension":".pdf","Size":113383,"Path":"/docs/2017-5-12/714f2521-b0b7-4b9a-b9e3-96dd5471e911.pdf","HashCode":"adc5cfd402acadb4daa324a811fc08ee","UploadTime":"2017-05-12T10:43:25.78","IsDeleted":false}]
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
         * Extension : .doc
         * Size : 151040
         * Path : /docs/2017-5-12/f229094b-086c-4f78-8622-4b8995c420cd.doc
         * HashCode : 49f2efd3f52489e77c422e36802210c8
         * UploadTime : 2017-05-12T09:48:10.693
         * IsDeleted : false
         */

        private int Id;
        private String Extension;
        private int Size;
        private String Path;
        private String HashCode;
        private String UploadTime;
        private boolean IsDeleted;

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
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

        public String getHashCode() {
            return HashCode;
        }

        public void setHashCode(String HashCode) {
            this.HashCode = HashCode;
        }

        public String getUploadTime() {
            return UploadTime;
        }

        public void setUploadTime(String UploadTime) {
            this.UploadTime = UploadTime;
        }

        public boolean isIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(boolean IsDeleted) {
            this.IsDeleted = IsDeleted;
        }
    }
}
