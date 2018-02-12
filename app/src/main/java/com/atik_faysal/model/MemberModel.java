package com.atik_faysal.model;

import java.lang.reflect.Member;

/**
 * Created by USER on 2/1/2018.
 */

public class MemberModel
{
        String name,userName,phone,taka,type,date,group;

        public MemberModel(String name,String userName,String phone,String group,String taka,String type,String date)
        {
                this.name = name;
                this.userName = userName;
                this.phone = phone;
                this.taka = taka;
                this.type = type;
                this.date = date;
                this.group = group;
        }


        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public String getPhone() {
                return phone;
        }

        public void setPhone(String phone) {
                this.phone = phone;
        }

        public String getTaka() {
                return taka;
        }

        public void setTaka(String taka) {
                this.taka = taka;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }

        public String getGroup() {
                return group;
        }

        public void setGroup(String group) {
                this.group = group;
        }
}
