package com.atik_faysal.model;

/**
 * Created by USER on 3/20/2018.
 */

public class MealModel
{
        private String date,name,breakfast,lunch,dinner,total;

        public MealModel(String date,String name,String breakfast,String lunch,String dinner,String total)
        {
                this.date = date;
                this.name = name;
                this.breakfast = breakfast;
                this.lunch = lunch;
                this.dinner = dinner;
                this.total = total;
        }


        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getBreakfast() {
                return breakfast;
        }

        public void setBreakfast(String breakfast) {
                this.breakfast = breakfast;
        }

        public String getLunch() {
                return lunch;
        }

        public void setLunch(String lunch) {
                this.lunch = lunch;
        }

        public String getDinner() {
                return dinner;
        }

        public void setDinner(String dinner) {
                this.dinner = dinner;
        }

        public String getTotal() {
                return total;
        }

        public void setTotal(String total) {
                this.total = total;
        }
}
