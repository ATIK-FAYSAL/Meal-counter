package com.atik_faysal.model;

import android.content.Context;
/**
 * Created by USER on 2/9/2018.
 */

public class NoticeModel
{
        String userName,id,title,notice,date;


        public NoticeModel(String userName,String id,String title,String notice,String date)
        {
                this.userName = userName;
                this.id = id;
                this.title = title;
                this.notice = notice;
                this.date = date;
        }


        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getNotice() {
                return notice;
        }

        public void setNotice(String notice) {
                this.notice = notice;
        }

        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }

}
