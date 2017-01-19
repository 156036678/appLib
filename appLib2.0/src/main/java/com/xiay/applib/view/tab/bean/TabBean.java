package com.xiay.applib.view.tab.bean;

import android.support.v4.app.Fragment;

/**
 * Created by Xiay on 2017/1/19.
 */

public class TabBean {
    public Fragment fragment;
    public String text;
    public int iconNormal;
    public int iconSelected;

    public TabBean(Fragment fragment, String text, int iconNormal, int iconSelected) {
        this.fragment = fragment;
        this.text = text;
        this.iconNormal = iconNormal;
        this.iconSelected = iconSelected;
    }
}
