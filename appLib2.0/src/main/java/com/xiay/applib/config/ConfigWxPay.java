package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * @xiay 微信配置
 */
public class ConfigWxPay {
    private String appId;


    private ConfigWxPay() {
    }

    private static class MyObjectHandler {
        private static ConfigWxPay singleOne = new ConfigWxPay();
    }

    public static ConfigWxPay getInstance() {
        return MyObjectHandler.singleOne;
    }

    /**
     * 获取微信应用APP_ID
     * @return appId
     */
    public String getAppId() {
        if (appId != null) {
            return appId;
        }
        return appId = SPUtil.getInstance().getString("ConfigWxPayAppId");
    }

    /**
     * 设置微信应用APP_ID
     *
     * @param appId 应用的 APP_ID*
     */
    public void setAppId(String appId) {
        this.appId = appId;
        SPUtil.getInstance().saveString("ConfigWxPayAppId", appId);
    }
}
