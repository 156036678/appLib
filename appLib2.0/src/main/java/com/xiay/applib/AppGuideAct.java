package com.xiay.applib;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiay.applib.db.DBGuidePic;
import com.xiay.applib.util.GreenDao;
import com.xiay.applib.view.guide.ADPagerAdapter;
import com.xiay.applib.view.guide.CirclePageIndicator;

import java.util.List;

import cn.xiay.bean.MyDevice;

/**
 * 引导页
 * Created by Xiay on 2016/12/27.
 */

public abstract class AppGuideAct extends AppActivity implements View.OnTouchListener {
    private ADPagerAdapter adapter;
    private int size = 0;
    private int lastX = 0;
    private int currentIndex = 0;
    private CirclePageIndicator indicator;
    private boolean locker = true;
    public List<DBGuidePic> guidePics;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        guidePics=getGuidePics();
    }
    /**SD上是否有图片*/
    public boolean hasSDPic(){
        return guidePics.size()>0;
    }
    /**获取引导页图片*/
   public List<DBGuidePic> getGuidePics(){
      return GreenDao.getDaoSession().getDBGuidePicDao().queryBuilder().list();
   }
    /**设置引导页view*/
    public void setGuideView(ViewPager viewPager, List<View> views) {
        if (hasSDPic()){
            for (int i = 0; i <views.size() ; i++) {
                View view=views.get(i);
                if (view instanceof ImageView){
                    Glide.with(AppGuideAct.this).load( guidePics.get(i)).centerCrop().into((ImageView) view);
                }else {
                    ViewGroup viewGroup= (ViewGroup) view;
                    for (int j = 0; j <viewGroup.getChildCount() ; j++) {
                        view=viewGroup.getChildAt(j);
                        if (view instanceof ImageView){
                            Glide.with(AppGuideAct.this).load( guidePics.get(i).getPics()).centerCrop().into((ImageView) view);
                            break;
                        }
                    }
                }
            }
        }
        size = views.size();
        adapter = new ADPagerAdapter(AppGuideAct.this, views);
        viewPager.setAdapter(adapter);
        indicator = (CirclePageIndicator) findViewById(R.id.viewflowindic);
        indicator.setViewPager(viewPager);
        indicator.setmListener(new MypageChangeListener());
        viewPager.setOnTouchListener(AppGuideAct.this);
    }

    public  abstract void onOpenActivity();
    @Override
    public String setTitle() {
        return null;
    }
    @Override
    public int getPageHeaderColorResources() {
        return 0;
    }
    @Override
    public int getStatusBarColor() {
        return 0;
    }
    private class MypageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            currentIndex = arg0;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int)event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if((lastX - event.getX()) > MyDevice.sWidth/2 && (currentIndex == size -1) && locker){
                    locker = false;
                    onOpenActivity();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }


}