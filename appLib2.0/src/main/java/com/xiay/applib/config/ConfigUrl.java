package com.xiay.applib.config;

import cn.xiay.bean.HttpConfig;
import cn.xiay.util.SPUtil;

/**
 * 配置url地址
 *
 * @xiay
 */
public class ConfigUrl extends HttpConfig {
    public  static  final String APP_CONFIG_URL_HEAD="APP_CONFIG_URL_HEAD";
    public  static  final String APP_CONFIG_URL="APP_CONFIG_URL";
    /**
     * 域名
     */
    public static String url;

    /**
     *
     * @param domainName 域名
     * @param url 域名后面的请求地址
     */
    public ConfigUrl(String domainName, String url) {
        UrlHead = domainName;
        this.url = url;
        SPUtil.saveString(APP_CONFIG_URL_HEAD, domainName);
        SPUtil.saveString(APP_CONFIG_URL, url);
    }
    /**
     *
     * @param domainName 域名
     */
    public ConfigUrl(String domainName) {
        UrlHead = domainName;
        url="";
        SPUtil.saveString(APP_CONFIG_URL_HEAD, domainName);
        SPUtil.saveString(APP_CONFIG_URL, url);
    }
}
