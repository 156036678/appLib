package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * 配置url地址
 *
 * @xiay
 */
public class ConfigUrl {
    private static final String APP_CONFIG_DOMAIN = "APP_CONFIG_DOMAIN";
    private static final String APP_CONFIG_LAST_URL = "APP_CONFIG_LAST_URL";
    /**
     * 域名
     */
    private static String mDomain;
    private static String mLastUrl;

    /**
     * @param domain  域名
     * @param lastUrl 域名后面的请求地址
     */
    public static void init(String domain, String lastUrl) {
        mDomain = domain;
        mLastUrl = lastUrl;
        SPUtil.getInstance().saveString(APP_CONFIG_DOMAIN, domain);
        SPUtil.getInstance().saveString(APP_CONFIG_LAST_URL, lastUrl);
    }

    /**
     * @param domain 域名
     */
    public static void init(String domain) {
        mDomain = domain;
        SPUtil.getInstance().saveString(APP_CONFIG_DOMAIN, domain);
    }

    public static String getDomain() {
        if (mDomain != null) {
            return mDomain;
        }
        return mDomain = SPUtil.getInstance().getString("APP_CONFIG_DOMAIN");
    }

    public static String getUrl() {
        if (getLastUrl() == null)
            return getDomain();
        else
            return getDomain() + getLastUrl();
    }

    public static String getLastUrl() {
        if (mLastUrl == null) {
            mLastUrl = SPUtil.getInstance().getString("APP_CONFIG_LAST_URL");
        }
        return mLastUrl;
    }
}
