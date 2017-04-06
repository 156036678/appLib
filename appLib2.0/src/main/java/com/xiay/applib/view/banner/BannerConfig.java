package com.xiay.applib.view.banner;


import cn.xiay.util.autolayout.utils.AutoUtils;

public class BannerConfig {
    public static final int NOT_INDICATOR=0;
    public static final int CIRCLE_INDICATOR=1;
    public static final int NUM_INDICATOR=2;
    public static final int NUM_INDICATOR_TITLE=3;
    public static final int CIRCLE_INDICATOR_TITLE=4;
    public static final int CIRCLE_INDICATOR_TITLE_INSIDE=5;
    public static final int LEFT=5;
    public static final int CENTER=6;
    public static final int RIGHT=7;

    public static final int INDICATOR_SIZE= AutoUtils.getPercentWidthSizeBigger(20);
    public static final int PADDING_SIZE= AutoUtils.getPercentWidthSizeBigger(10);
    public static final int TIME=3500;
    public static final boolean IS_AUTO_PLAY=true;

    public static final int TITLE_BACKGROUND=-1;
    public static final int TITLE_HEIGHT=-1;
    public static final int TITLE_TEXT_COLOR=-1;
    public static final int TITLE_TEXT_SIZE=-1;

}
