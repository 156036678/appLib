package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * 配置请求参数方法签名
 * @xiay
 */
public class ConfigMethodSign {
    /**请求参数方法签名（必填）*/
    public static  String methodSign;
    public ConfigMethodSign(String methodSign) {
        this.methodSign=methodSign;
        SPUtil.saveString("methodSign",methodSign);
    }
}
