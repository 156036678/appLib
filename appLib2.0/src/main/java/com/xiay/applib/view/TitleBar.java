package com.xiay.applib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiay.applib.R;

import cn.xiay.util.autolayout.utils.AutoUtils;


/**
 * title bar
 */
public class TitleBar extends RelativeLayout {
    Context context;
    protected LinearLayout leftLayout;
    protected ImageView leftImage;
    protected LinearLayout rightLayout;
    protected ImageView rightImage;
    protected TextView titleView;
    protected TextView rightText;
    protected RelativeLayout titleLayout;

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.app_title_bar, this);
        leftLayout = (LinearLayout) findViewById(R.id.left_layout);
        leftImage = (ImageView) findViewById(R.id.left_image);
        rightLayout = (LinearLayout) findViewById(R.id.right_layout);
        rightImage = (ImageView) findViewById(R.id.right_image);
        titleView = (TextView) findViewById(R.id.title);
        rightText = (TextView) findViewById(R.id.right_text);
        titleLayout = (RelativeLayout) findViewById(R.id.title_bar);
       // ViewUtil.scaleContentView(titleLayout);
        AutoUtils.auto(titleLayout);
        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            String title = ta.getString(R.styleable.TitleBar_titleBarTitle);
            if (title != null)
                titleView.setText(title);
            String rightTitle = ta.getString(R.styleable.TitleBar_titleBarRightText);
            if (rightTitle != null) {
                titleView.setVisibility(VISIBLE);
                titleView.setText(title);
            }
            Drawable leftDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarLeftImage);
            if (null != leftDrawable) {
                leftImage.setImageDrawable(leftDrawable);
            }
            Drawable rightDrawable = ta.getDrawable(R.styleable.TitleBar_titleBarRightImage);
            if (null != rightDrawable) {
                rightImage.setVisibility(VISIBLE);
                rightImage.setImageDrawable(rightDrawable);
            }

            Drawable background = ta.getDrawable(R.styleable.TitleBar_titleBarBackground);
            if (null != background) {
                titleLayout.setBackgroundDrawable(background);
            }
            ta.recycle();
        }
    }

    public ImageView setLeftImageResource(int resId) {
        leftImage.setImageResource(resId);
        leftImage.setVisibility(VISIBLE);
        return leftImage;
    }

    /**
     * 设置自定义layout
     *
     * @param layoutId
     */
    public View setCustomLayout(int layoutId) {
        leftLayout.setVisibility(GONE);
        rightImage.setVisibility(GONE);
        rightText.setVisibility(GONE);
        titleView.setVisibility(GONE);
        View v = LayoutInflater.from(context).inflate(layoutId, this);
        AutoUtils.auto(v);
//        if (v instanceof ViewGroup)
//            ViewUtil.scaleContentView((ViewGroup) v);
//        else
//            ViewUtil.scaleView(v);
        return v;
    }

    public ImageView setRightImageResource(int resId) {
        rightImage.setVisibility(VISIBLE);
        rightImage.setImageResource(resId);
        return rightImage;
    }

    /**
     * 设置右边文字
     */
    public TextView setRightText(String text) {
        rightText.setVisibility(VISIBLE);
        rightText.setText(text);
        return rightText;
    }

    public TextView getRightTextView() {
        return rightText;
    }

    public ImageView getRightImageView() {
        return rightImage;
    }


    public void setLeftLayoutVisibility(int visibility) {
        leftLayout.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility) {
        rightLayout.setVisibility(visibility);
    }

    public void setRightTextViewVisibility(int visibility) {
        rightText.setVisibility(visibility);
    }

    public void setRightImageViewVisibility(int visibility) {
        rightImage.setVisibility(visibility);
    }


    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitleColor(int textColor) {
        titleView.setTextColor(textColor);
    }

    public void setTitleBackgroundColor(int color) {
        titleLayout.setBackgroundColor(color);
    }

    public LinearLayout getLeftLayout() {
        return leftLayout;
    }

    public LinearLayout getRightLayout() {
        return rightLayout;
    }
}
