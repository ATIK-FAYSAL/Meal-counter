package com.atik_faysal.model;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by USER on 1/25/2018.
 */

public class SearchableModel implements Searchable
{

        String groupName;

        public SearchableModel(String gName)
        {
                this.groupName = gName;
        }

        public void setTitle(String mTitle) {
                this.groupName = mTitle;
        }

        @Override
        public String getTitle() {
                return groupName;
        }
}
