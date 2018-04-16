package com.atik_faysal.model;

/**
 * Created by USER on 3/1/2018.
 */

public class CostModel
{
        private String name,taka,date,id,status;

        public CostModel(String id,String name,String taka,String date,String status)
        {
                this.id = id;
                this.name = name;
                this.taka = taka;
                this.date = date;
                this.status = status;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getTaka() {
                return taka;
        }

        public void setTaka(String taka) {
                this.taka = taka;
        }

        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }
}
