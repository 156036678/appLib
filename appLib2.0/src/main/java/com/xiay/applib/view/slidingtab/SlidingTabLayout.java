package com.xiay.applib.view.slidingtab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiay.applib.R;

import java.util.ArrayList;
import java.util.List;

import cn.xiay.util.autolayout.utils.AutoUtils;


public class SlidingTabLayout extends ViewGroup implements OnPageChangeListener {
  OnPageSelectedListenter pageSelectedListenter;
  private List<UnitViewHolder> viewHolders = new ArrayList<>();
  boolean enableAnim;
  /**左边第一个view距离左边的间距 和最右边的view距离最右边的间距*/
  private int margin;
  public SlidingTabLayout(Context context) {
    super(context);
  }

  public SlidingTabLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SlidingTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setUpViewPager(final ViewPager viewPager, List<DataHolder> dataHolders) {
    this.enableAnim=true;
    viewPager.addOnPageChangeListener(this);
    for (int i = 0; i < dataHolders.size(); ++i) {
      RelativeLayout v = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.sliding_tab_layout, this, false);
    //  ViewUtil.scaleContentView(v);
      AutoUtils.auto(v);
      final int j = i;
      v.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          viewPager.setCurrentItem(j, false);
        }
      });
      DataHolder dataHolder = dataHolders.get(i);
      TextView tv = (TextView) v.findViewById(R.id.text);
      tv.setText(dataHolder.title);
      TextView tv2 = (TextView) v.findViewById(R.id.text_front);
      tv2.setText(dataHolder.title);
      tv2.setTextColor(dataHolder.titleTargetColor);
      tv2.setAlpha(0);
      ImageView iv = (ImageView) v.findViewById(R.id.image_bg);
      iv.setImageDrawable(dataHolder.back);
      ImageView iv2 = (ImageView) v.findViewById(R.id.image_front);
      iv2.setImageDrawable(dataHolder.front);
      iv2.setAlpha((float) 0);
      viewHolders.add(new UnitViewHolder(tv, tv2, iv, iv2));
      addView(v);
    }
  }
  public void setUpViewPagerNoAnim(final ViewPager viewPager, List<DataHolder> dataHolders) {
    this.enableAnim=false;
       viewPager.addOnPageChangeListener(this);
    for (int i = 0; i < dataHolders.size(); ++i) {
      RelativeLayout v = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.sliding_tab_layout, this, false);
      AutoUtils.auto(v);
      final int j = i;
      v.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          viewPager.setCurrentItem(j, false);
        }
      });
      DataHolder dataHolder = dataHolders.get(i);
      TextView tv = (TextView) v.findViewById(R.id.text);
      tv.setText(dataHolder.title);
      TextView tv2 = (TextView) v.findViewById(R.id.text_front);
      tv2.setText(dataHolder.title);
      tv2.setTextColor(dataHolder.titleTargetColor);
      ImageView iv = (ImageView) v.findViewById(R.id.image_bg);
      iv.setImageDrawable(dataHolder.back);
      ImageView iv2 = (ImageView) v.findViewById(R.id.image_front);
      iv2.setImageDrawable(dataHolder.front);
      viewHolders.add(new UnitViewHolder(tv, tv2, iv, iv2));
      addView(v);
      onPageSelected(0);
    }
  }
  public  void  setMargin(int margin){
     this.margin= AutoUtils.getPercentWidthSizeBigger(margin);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int wSize = MeasureSpec.getSize(widthMeasureSpec);
    int hSize = MeasureSpec.getSize(heightMeasureSpec);
    int wMode = MeasureSpec.getMode(widthMeasureSpec);
    int hMode = MeasureSpec.getMode(heightMeasureSpec);

    int wrapW = 0, wrapH = 0;
    int count = getChildCount();
    if (count == 0) {
      wrapW = 0;
      wrapH = getPaddingBottom() + getPaddingTop();
    } else {
      int cw = wSize / count;
      measureChildren(MeasureSpec.makeMeasureSpec(cw, MeasureSpec.AT_MOST),
          MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.AT_MOST));
      wrapH = getChildAt(0).getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
    }

    if (wMode == MeasureSpec.AT_MOST && hMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(wrapW, wrapH);
    } else if (wMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(wrapW, hSize);
    } else if (hMode == MeasureSpec.AT_MOST) {
      setMeasuredDimension(wSize, wrapH);
    }
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int count = getChildCount();
    if (count != 0) {
      int t = getPaddingTop(), r = 0, cw = getMeasuredWidth() / count;
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        int rl = r + (cw - child.getMeasuredWidth()) / 2;
        if (i==0){
          child.layout(rl-margin, t, rl + child.getMeasuredWidth(), t + child.getMeasuredHeight());
        }else if (i==count-1){
          child.layout(rl+margin, t, rl + child.getMeasuredWidth(), t + child.getMeasuredHeight());
        }else {
          child.layout(rl, t, rl + child.getMeasuredWidth(), t + child.getMeasuredHeight());
        }
        r += cw;
      }
    }
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (enableAnim){
      viewHolders.get(position).front.setAlpha(1 - positionOffset);
      viewHolders.get(position).titleFront.setAlpha(1 - positionOffset);
      if (position + 1 < viewHolders.size()) {
        viewHolders.get(position + 1).front.setAlpha(positionOffset);
        viewHolders.get(position + 1).titleFront.setAlpha(positionOffset);
      }
      for (int i = 0; i < position; ++i) {
        viewHolders.get(i).front.setAlpha((float) 0);
        viewHolders.get(i).titleFront.setAlpha(0);
      }
      for (int i = position + 2; i < viewHolders.size(); ++i) {
        viewHolders.get(i).front.setAlpha((float) 0);
        viewHolders.get(i).titleFront.setAlpha(0);
      }
      if (pageSelectedListenter!=null){
        pageSelectedListenter.onPageSelected(position);
      }
    }
  }

  @Override public void onPageSelected(int position) {
    if (!enableAnim){
      for (int i = 0; i < viewHolders.size(); ++i) {
        viewHolders.get(i).front.setVisibility(INVISIBLE);
        viewHolders.get(i).bg.setVisibility(VISIBLE);
        viewHolders.get(i).titleFront.setVisibility(INVISIBLE);
        viewHolders.get(i).title.setVisibility(VISIBLE);
      }
      viewHolders.get(position).front.setVisibility(VISIBLE);
      viewHolders.get(position).bg.setVisibility(INVISIBLE);
      viewHolders.get(position).titleFront.setVisibility(VISIBLE);
      viewHolders.get(position).title.setVisibility(INVISIBLE);
      if (pageSelectedListenter!=null){
        pageSelectedListenter.onPageSelected(position);
      }
    }


  }

  @Override public void onPageScrollStateChanged(int state) {

  }
  public  void  setOnPageSelectedListenter(OnPageSelectedListenter listenter){
    pageSelectedListenter=listenter;
  }
public  interface  OnPageSelectedListenter{
   void  onPageSelected(int position);
}
  private static class UnitViewHolder {
    private TextView title;
    private TextView titleFront;
    private ImageView bg;
    private ImageView front;

    public UnitViewHolder(TextView title, TextView titleFront, ImageView bg, ImageView front) {
      this.title = title;
      this.titleFront = titleFront;
      this.bg = bg;
      this.front = front;
    }
  }
}
