package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * @xiay
 * 微信配置
 */
public class ConfigWxPay {
    /**
     *
     * @param appId 应用的 APP_ID*
     */
    public ConfigWxPay(String appId) {
        SPUtil.saveString("WxPayAppId",appId);
    }
}
