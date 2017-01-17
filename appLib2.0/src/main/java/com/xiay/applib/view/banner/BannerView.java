package com.xiay.applib.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiay.applib.R;

import java.util.ArrayList;
import java.util.List;

public class BannerView extends FrameLayout implements ViewPager.OnPageChangeListener {
    /**
     * 常量已经移入BannerConfig类里
     */
    @Deprecated
    public static final int NOT_INDICATOR=0;
    @Deprecated
    public static final int CIRCLE_INDICATOR=1;
    @Deprecated
    public static final int NUM_INDICATOR=2;
    @Deprecated
    public static final int NUM_INDICATOR_TITLE=3;
    @Deprecated
    public static final int CIRCLE_INDICATOR_TITLE=4;
    @Deprecated
    public static final int LEFT=5;
    @Deprecated
    public static final int CENTER=6;
    @Deprecated
    public static final int RIGHT=7;

    public String tag="banner";
    private int mIndicatorMargin = BannerConfig.PADDING_SIZE;
    private int mIndicatorWidth = BannerConfig.INDICATOR_SIZE;
    private int mIndicatorHeight = BannerConfig.INDICATOR_SIZE;
    private int bannerStyle=BannerConfig.NOT_INDICATOR;
    private int delayTime=BannerConfig.TIME;
    private boolean isAutoPlay=BannerConfig.IS_AUTO_PLAY;
    private int mIndicatorSelectedResId ;
    private int mIndicatorUnselectedResId ;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;
    private int defaultImage=-1;
    private int count=0;
    private int currentItem;
    private int gravity=-1;
    private int lastPosition=1;
    private List<ImageView> imageViews;
    private List<ImageView> indicatorImages;
    private  List<BannerBean> bannerBeen;
    private Context context;
    private ViewPager viewPager;
    private LinearLayout indicator,indicatorInside,titleView;
    private Handler handler = new Handler();
    private OnBannerClickListener listener;
    private OnLoadImageListener imageListener;
    private String[] titles;
    private TextView bannerTitle , numIndicatorInside , numIndicator;
    private BannerPagerAdapter adapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public BannerView(Context context) {
        this(context, null);
    }
    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        imageViews = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        initView(context, attrs);
    }
    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mIndicatorWidth =typedArray.getDimensionPixelSize(R.styleable.BannerView_indicator_width, BannerConfig.INDICATOR_SIZE);
        mIndicatorHeight =typedArray.getDimensionPixelSize(R.styleable.BannerView_indicator_height, BannerConfig.INDICATOR_SIZE);
        mIndicatorMargin =typedArray.getDimensionPixelSize(R.styleable.BannerView_indicator_margin, BannerConfig.PADDING_SIZE);
        mIndicatorSelectedResId =typedArray.getResourceId(R.styleable.BannerView_indicator_drawable_selected, R.mipmap.dot_selected);
        mIndicatorUnselectedResId =typedArray.getResourceId(R.styleable.BannerView_indicator_drawable_unselected, R.mipmap.dot_unselected);
        defaultImage =typedArray.getResourceId(R.styleable.BannerView_default_image, defaultImage);
        delayTime =typedArray.getDimensionPixelSize(R.styleable.BannerView_delay_time, BannerConfig.TIME);
        isAutoPlay =typedArray.getBoolean(R.styleable.BannerView_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        titleBackground =typedArray.getColor(R.styleable.BannerView_title_background, BannerConfig.TITLE_BACKGROUND);
        titleHeight =typedArray.getDimensionPixelSize(R.styleable.BannerView_title_height, BannerConfig.TITLE_HEIGHT);
        titleTextColor =typedArray.getColor(R.styleable.BannerView_title_textcolor, BannerConfig.TITLE_TEXT_COLOR);
        titleTextSize =typedArray.getDimensionPixelSize(R.styleable.BannerView_title_textsize, BannerConfig.TITLE_TEXT_SIZE);
        typedArray.recycle();
    }
    private void initView(Context context, AttributeSet attrs) {
        imageViews.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        titleView = (LinearLayout) view.findViewById(R.id.titleView);
        indicator = (LinearLayout) view.findViewById(R.id.indicator);
        indicatorInside = (LinearLayout) view.findViewById(R.id.indicatorInside);
        bannerTitle = (TextView) view.findViewById(R.id.bannerTitle);
        numIndicator = (TextView) view.findViewById(R.id.numIndicator);
        numIndicatorInside = (TextView) view.findViewById(R.id.numIndicatorInside);
        handleTypedArray(context, attrs);
    }

    public void isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay=isAutoPlay;
        startAutoPlay();
    }
    public void setDelayTime(int delayTime) {
        this.delayTime=delayTime;
    }

    public void setIndicatorGravity(int type) {
        switch (type){
            case BannerConfig.LEFT:
                this.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
                break;
            case BannerConfig.CENTER:
                this.gravity=Gravity.CENTER;
                break;
            case BannerConfig.RIGHT:
                this.gravity=Gravity.RIGHT|Gravity.CENTER_VERTICAL;
                break;
        }
    }
    public void setBannerTitleList(List<String> titles) {
        setBannerTitle(titles.toArray(new String[titles.size()]));
    }
    public void setBannerTitle(String[] titles) {
        this.titles=titles;
        if (bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE||
                bannerStyle==BannerConfig.NUM_INDICATOR_TITLE||
                bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
            if (titleBackground!=-1){
                titleView.setBackgroundColor(titleBackground);
            }
            if (titleHeight!=-1){
                titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,titleHeight));
            }
            if (titleTextColor!=-1){
                bannerTitle.setTextColor(titleTextColor);
            }
            if (titleTextSize!=-1){
                bannerTitle.setTextSize(titleTextSize);
            }
            if (titles != null && titles.length > 0) {
                bannerTitle.setText(titles[0]);
                bannerTitle.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.VISIBLE);
            }
        }
    }
    public void setBannerStyle(int bannerStyle) {
        this.bannerStyle=bannerStyle;
        switch (bannerStyle){
            case BannerConfig.CIRCLE_INDICATOR:
                indicator.setVisibility(View.VISIBLE);
                break;
            case BannerConfig.NUM_INDICATOR:
                numIndicator.setVisibility(View.VISIBLE);
                break;
            case BannerConfig.NUM_INDICATOR_TITLE:
                numIndicatorInside.setVisibility(View.VISIBLE);
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE:
                indicator.setVisibility(View.VISIBLE);
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
                indicatorInside.setVisibility(VISIBLE);
                break;
        }
    }
    public void setBanners(List<BannerBean> bannerBeen){
        setBanners(bannerBeen, null);
    }
    public void setBanners(List<BannerBean> bannerBeen, OnLoadImageListener imageListener) {
        this.bannerBeen = bannerBeen;
        setImageList(imageListener);
    }
    private void initImages(){
        imageViews.clear();
        if (bannerStyle==BannerConfig.CIRCLE_INDICATOR||
                bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE||
                bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
            createIndicator();
        }else if (bannerStyle==BannerConfig.NUM_INDICATOR_TITLE){
            numIndicatorInside.setText("1/"+count);
        }else if (bannerStyle==BannerConfig.NUM_INDICATOR){
            numIndicator.setText("1/"+count);
        }
    }
    private void setImageArray(Object[] imagesUrl, OnLoadImageListener imageListener) {
        if (imagesUrl==null||imagesUrl.length<=0) {
            Log.e(tag,"Please set the images data.");
            return;
        }
        count = imagesUrl.length;
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ScaleType.FIT_XY);
            Object url=null;
            if (i == 0) {
                url=imagesUrl[count - 1];
            } else if (i == count + 1) {
                url=imagesUrl[0];
            } else {
                url=imagesUrl[i - 1];
            }
            imageViews.add(iv);
            if(imageListener!=null){
                imageListener.OnLoadImage(iv,url);
            }else{
                if (defaultImage!=-1)
                    Glide.with(context).load(url).centerCrop().crossFade().into(iv);
                else
                    Glide.with(context).load(url).centerCrop().crossFade().placeholder(defaultImage).into(iv);
            }
        }
        setData();
    }
    private void setImageList(OnLoadImageListener imageListener) {
        if (bannerBeen ==null|| bannerBeen.size()<=0) {
            Log.e(tag,"Please set the images data.");
            return;
        }
        if (bannerStyle==BannerConfig.CIRCLE_INDICATOR){
            indicator.setVisibility(View.VISIBLE);
        }
        count = bannerBeen.size();
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ScaleType.FIT_XY);
            String url;
            if (i == 0) {
                url= bannerBeen.get(count - 1).getPicUrl();
            } else if (i == count + 1) {
                url= bannerBeen.get(0).getPicUrl();
            } else {
                url= bannerBeen.get(i - 1).getPicUrl();
            }
            imageViews.add(iv);
            if(imageListener!=null){
                imageListener.OnLoadImage(iv,url);
            }else{
                if (defaultImage!=-1)
                    Glide.with(context).load(url).centerCrop().crossFade().into(iv);
                else
                    Glide.with(context).load(url).centerCrop().crossFade().placeholder(defaultImage).into(iv);
            }
        }
        setData();
    }
    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        indicatorInside.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.FIT_XY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth,mIndicatorHeight);
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if(i==0){
                imageView.setImageResource(mIndicatorSelectedResId);
            }else{
                imageView.setImageResource(mIndicatorUnselectedResId);
            }
            indicatorImages.add(imageView);
            if (bannerStyle==BannerConfig.CIRCLE_INDICATOR||
                    bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE)
                indicator.addView(imageView, params);
            else if(bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                indicatorInside.addView(imageView, params);
        }
    }


    private void setData() {
        currentItem = 1;
        if (adapter==null) {
            adapter = new BannerPagerAdapter();
            viewPager.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
        viewPager.setFocusable(true);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(this);
        if (gravity!=-1)
            indicator.setGravity(gravity);
        startAutoPlay();
    }

    private void startAutoPlay() {
        if (isAutoPlay) {
            handler.removeCallbacks(task);
            handler.postDelayed(task, delayTime);
        }
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (isAutoPlay) {
                if (count>1) {
                    if (viewPager.hasWindowFocus()&&viewPager.isShown()){//viewPager 不可见时，暂停轮播  by Xiay
                        currentItem = currentItem % (count + 1) + 1;
                        if (currentItem == 1) {
                            viewPager.setCurrentItem(currentItem, false);
                        } else {
                            viewPager.setCurrentItem(currentItem);
                        }
                    }
                    handler.postDelayed(task, delayTime);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (count>1) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isAutoPlay(false);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isAutoPlay=true;
                    isAutoPlay(isAutoPlay);
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(imageViews.get(position));
            final ImageView view=imageViews.get(position);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        if (position==0){//已知当图片有8张的时候一来就向左滑动，会闪退问题，原因还不明，暂时加上这个判断
                            listener.OnBannerClick(bannerBeen.get(imageViews.size()-3),imageViews.size()-3);
                        }else {
                            listener.OnBannerClick(bannerBeen.get(position-1),position-1);
                        }

                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:
                isAutoPlay = false;
                break;
            case 2:
                isAutoPlay = true;
                break;
            case 0:
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(count, false);
                } else if (viewPager.getCurrentItem() == count + 1) {
                    viewPager.setCurrentItem(1, false);
                }
                currentItem = viewPager.getCurrentItem();
                isAutoPlay = true;
                break;
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (bannerStyle==BannerConfig.CIRCLE_INDICATOR||
                bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE||
                bannerStyle==BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE) {
            indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
            indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
            lastPosition = position;
        }
        if (position==0) position=1;
        switch (bannerStyle){
            case BannerConfig.CIRCLE_INDICATOR:
                break;
            case BannerConfig.NUM_INDICATOR:
                if (position>count) position=count;
                numIndicator.setText(position+"/"+count);
                break;
            case BannerConfig.NUM_INDICATOR_TITLE:
                if (position>count) position=count;
                numIndicatorInside.setText(position+"/"+count);
                if (titles!=null&&titles.length>0){
                    if (position>titles.length) position=titles.length;
                    bannerTitle.setText(titles[position-1]);
                }
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE:
                if (titles!=null&&titles.length>0){
                    if (position>titles.length) position=titles.length;
                    bannerTitle.setText(titles[position-1]);
                }
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE:
                if (titles!=null&&titles.length>0){
                    if (position>titles.length) position=titles.length;
                    bannerTitle.setText(titles[position-1]);
                }
                break;
        }

    }


    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void setOnBannerImageListener(OnLoadImageListener imageListener) {
        this.imageListener = imageListener;
    }
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }
    public interface OnBannerClickListener {
        void OnBannerClick(BannerBean bannerBean, int position);
    }
    public interface OnLoadImageListener {
        void OnLoadImage(ImageView view, Object url);
    }
}
