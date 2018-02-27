package com.atik_faysal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.atik_faysal.model.MakeShoppingList;
import com.atik_faysal.others.ShoppingList;

/**
 * Created by USER on 2/23/2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
        int numberOfPage;
        public ViewPagerAdapter(FragmentManager fm, int numberOfPage)
        {
                super(fm);
                this.numberOfPage = numberOfPage;
        }

        @Override
        public Fragment getItem(int position) {
                if(position==0)
                        return new ShoppingList();
                else if(position==1)
                        return new MakeShoppingList();

                else return null;
        }

        @Override
        public int getCount() {
                return numberOfPage;
        }
}
