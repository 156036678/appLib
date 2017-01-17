package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * @xiay
 * 支付宝配置
 */
public class ConfigAliPay {

    /**
     *
     * @param pid 商户PID*
     * @param seller  商户收款账号
     * @param privateKey 商户私钥，pkcs8格式
     */
    public ConfigAliPay(String pid, String seller, String privateKey) {
        SPUtil.saveString("AliPayPID",pid);
        SPUtil.saveString("AliPaySeller",seller);
        SPUtil.saveString("AliPayPrivateKey",privateKey);
    }
}
