package com.bjdwd.beans;

/**
 * Created by dell on 2017/4/5.
 */

public class LoginBackBean {

    /**
     * code : 200
     * msg : 数据请求成功
     * data : {"User":{"Id":3,"Name":"白亚军","Gender":0,"HeadPortraitPath":"","DepartmentId":1,"AddTime":"2017-03-29T11:09:23.66","IsDeleted":false,"DepartmentName":"北京电务段","ParentDepartmentId":0,"WorkNo":"600812","Username":"600812","UserType":1,"Password":""},"Token":"2:1491393151:6281656349ccda62d26cc5514381224a"}
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
        /**
         * User : {"Id":3,"Name":"白亚军","Gender":0,"HeadPortraitPath":"","DepartmentId":1,"AddTime":"2017-03-29T11:09:23.66","IsDeleted":false,"DepartmentName":"北京电务段","ParentDepartmentId":0,"WorkNo":"600812","Username":"600812","UserType":1,"Password":""}
         * Token : 2:1491393151:6281656349ccda62d26cc5514381224a
         */

        private UserBean User;
        private String Token;

        public UserBean getUser() {
            return User;
        }

        public void setUser(UserBean User) {
            this.User = User;
        }

        public String getToken() {
            return Token;
        }

        public void setToken(String Token) {
            this.Token = Token;
        }

        public static class UserBean {
            /**
             * Id : 3
             * Name : 白亚军
             * Gender : 0
             * HeadPortraitPath :
             * DepartmentId : 1
             * AddTime : 2017-03-29T11:09:23.66
             * IsDeleted : false
             * DepartmentName : 北京电务段
             * ParentDepartmentId : 0
             * WorkNo : 600812
             * Username : 600812
             * UserType : 1
             * Password :
             */

            private int Id;
            private String Name;
            private int Gender;
            private String HeadPortraitPath;
            private int DepartmentId;
            private String AddTime;
            private boolean IsDeleted;
            private String DepartmentName;
            private int ParentDepartmentId;
            private String WorkNo;
            private String Username;
            private int UserType;
            private String Password;

            public int getId() {
                return Id;
            }

            public void setId(int Id) {
                this.Id = Id;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public int getGender() {
                return Gender;
            }

            public void setGender(int Gender) {
                this.Gender = Gender;
            }

            public String getHeadPortraitPath() {
                return HeadPortraitPath;
            }

            public void setHeadPortraitPath(String HeadPortraitPath) {
                this.HeadPortraitPath = HeadPortraitPath;
            }

            public int getDepartmentId() {
                return DepartmentId;
            }

            public void setDepartmentId(int DepartmentId) {
                this.DepartmentId = DepartmentId;
            }

            public String getAddTime() {
                return AddTime;
            }

            public void setAddTime(String AddTime) {
                this.AddTime = AddTime;
            }

            public boolean isIsDeleted() {
                return IsDeleted;
            }

            public void setIsDeleted(boolean IsDeleted) {
                this.IsDeleted = IsDeleted;
            }

            public String getDepartmentName() {
                return DepartmentName;
            }

            public void setDepartmentName(String DepartmentName) {
                this.DepartmentName = DepartmentName;
            }

            public int getParentDepartmentId() {
                return ParentDepartmentId;
            }

            public void setParentDepartmentId(int ParentDepartmentId) {
                this.ParentDepartmentId = ParentDepartmentId;
            }

            public String getWorkNo() {
                return WorkNo;
            }

            public void setWorkNo(String WorkNo) {
                this.WorkNo = WorkNo;
            }

            public String getUsername() {
                return Username;
            }

            public void setUsername(String Username) {
                this.Username = Username;
            }

            public int getUserType() {
                return UserType;
            }

            public void setUserType(int UserType) {
                this.UserType = UserType;
            }

            public String getPassword() {
                return Password;
            }

            public void setPassword(String Password) {
                this.Password = Password;
            }
        }
    }
}