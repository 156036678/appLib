package com.xiay.applib.view.tab.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;

import com.xiay.applib.view.tab.GradientTabStrip;
import com.xiay.applib.view.tab.bean.TabBean;

import java.util.ArrayList;

/**
 * Created by Xiay on 2017/3/26.
 */
public class TabStripAdapter extends FragmentPagerAdapter implements GradientTabStrip.GradientTabAdapter {


    public TabStripAdapter(FragmentManager fm) {
        super(fm);
    }
    /**TabBean*/
    public ArrayList<TabBean> tabBeans = new ArrayList<>();
    /**页面标题*/
    public ArrayList<String> pageTitles = new ArrayList<>();
    private GradientTabStrip gradientTabStrip;
    private ArrayMap<Integer, String> tagInfo = new ArrayMap<>();

    public TabStripAdapter(FragmentManager fm, GradientTabStrip gradientTabStrip) {
        super(fm);
        this.gradientTabStrip = gradientTabStrip;
    }

    @Override
    public int getCount() {
        return tabBeans.size();
    }

    @Override
    public Fragment getItem(int position) {
        return tabBeans.get(position).fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabBeans.get(position).text;
    }

    @Override
    public Drawable getNormalDrawable(int position, Context context) {
        return ContextCompat.getDrawable(context, tabBeans.get(position).iconNormal);
    }

    @Override
    public Drawable getSelectedDrawable(int position, Context context) {
        return ContextCompat.getDrawable(context, tabBeans.get(position).iconSelected);
    }

    /**
     * 添加tag标记
     * @param position
     * @param text
     */
    public void addTag(int position, String text) {
        tagInfo.put(position, text);
        gradientTabStrip.invalidate();

    }
    /**
     * 移除tag标记
     * @param position
     */
    public void removeTag(int position) {
        tagInfo.remove(position);
        gradientTabStrip.invalidate();

    }

    @Override
    public boolean isTagEnable(int position) {
        return tagInfo.get(position) != null;
    }

    @Override
    public String getTag(int position) {
        return tagInfo.get(position);
    }
}
