package com.atik_faysal.mealcounter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.atik_faysal.adapter.ViewPagerAdapter;

/**
 * Created by USER on 3/19/2018.
 */

public class MealClass extends SetTabLayout
{
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.tab_layout);
                super.initComponent();
                super.setToolbar();
                this.setTabLayout();
        }

        //initialize tablayout
        public void setTabLayout()
        {
                tabLayout.addTab(tabLayout.newTab().setText("All Meal"));
                tabLayout.addTab(tabLayout.newTab().setText("My meal"));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                adapter = new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),"schedule");
                pager.setAdapter(adapter);
                pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                                pager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {}

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {}
                });
        }
}
