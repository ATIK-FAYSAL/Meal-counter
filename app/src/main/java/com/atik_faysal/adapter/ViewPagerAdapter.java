package com.atik_faysal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.atik_faysal.others.InputMeal;
import com.atik_faysal.others.MakeShoppingList;
import com.atik_faysal.others.MyMeal;
import com.atik_faysal.others.ShoppingList;

/**
 * Created by USER on 2/23/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
        private int numberOfPage;
        private String action;
        public ViewPagerAdapter(FragmentManager fm, int numberOfPage,String action)
        {
                super(fm);
                this.numberOfPage = numberOfPage;
                this.action = action;
        }

        @Override
        public Fragment getItem(int position) {
                Fragment object = null;
                if(position==0)
                {
                        if(action.equals("shopping"))object = new ShoppingList();
                        else if(action.equals("schedule"))object = new InputMeal();
                        return object;
                }
                else if(position==1)
                {
                        if(action.equals("shopping"))object = new MakeShoppingList();
                        else if(action.equals("schedule"))object = new MyMeal();
                        return object;
                }
                else return null;
        }

        @Override
        public int getCount() {
                return numberOfPage;
        }
}
