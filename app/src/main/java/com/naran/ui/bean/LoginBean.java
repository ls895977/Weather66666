package com.naran.ui.bean;

import java.util.List;

public class LoginBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * RoleID : 0
         * UserName : 13735491303
         * ID : 22
         * FireWarning : 1
         * ParentID : 0
         * FullName : 巴图18
         * CHineseAreaName : 阿巴嘎旗
         * HeadImg : http://121.41.123.152:8088/HeadImgUrl/22head1546936497209.png
         * AreaNO : 53192
         * Contact : 13735491303
         * Edition : 16
         * ParentUserName : 13735491303
         * State : 1
         */

        private int RoleID;
        private String UserName;
        private int ID;
        private int FireWarning;
        private int ParentID;
        private String FullName;
        private String CHineseAreaName;
        private String HeadImg;
        private String AreaNO;
        private String Contact;
        private int Edition;
        private String ParentUserName;
        private int State;

        public int getRoleID() {
            return RoleID;
        }

        public void setRoleID(int RoleID) {
            this.RoleID = RoleID;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getFireWarning() {
            return FireWarning;
        }

        public void setFireWarning(int FireWarning) {
            this.FireWarning = FireWarning;
        }

        public int getParentID() {
            return ParentID;
        }

        public void setParentID(int ParentID) {
            this.ParentID = ParentID;
        }

        public String getFullName() {
            return FullName;
        }

        public void setFullName(String FullName) {
            this.FullName = FullName;
        }

        public String getCHineseAreaName() {
            return CHineseAreaName;
        }

        public void setCHineseAreaName(String CHineseAreaName) {
            this.CHineseAreaName = CHineseAreaName;
        }

        public String getHeadImg() {
            return HeadImg;
        }

        public void setHeadImg(String HeadImg) {
            this.HeadImg = HeadImg;
        }

        public String getAreaNO() {
            return AreaNO;
        }

        public void setAreaNO(String AreaNO) {
            this.AreaNO = AreaNO;
        }

        public String getContact() {
            return Contact;
        }

        public void setContact(String Contact) {
            this.Contact = Contact;
        }

        public int getEdition() {
            return Edition;
        }

        public void setEdition(int Edition) {
            this.Edition = Edition;
        }

        public String getParentUserName() {
            return ParentUserName;
        }

        public void setParentUserName(String ParentUserName) {
            this.ParentUserName = ParentUserName;
        }

        public int getState() {
            return State;
        }

        public void setState(int State) {
            this.State = State;
        }
    }
}
