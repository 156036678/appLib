package com.xiay.applib.config;

import cn.xiay.util.SPUtil;

/**
 * @xiay 支付宝配置
 */
public class ConfigAliPay {
    private final String CONFIG_ALIPAY_PID = "ConfigAliPayPID";
    private final String CONFIG_ALIPAY_SELLER = "ConfigAliPaySeller";
    private final String CONFIG_ALIPAY_PRIVATE_KEY = "ConfigAliPayPrivateKey";
    private String aliPayPID;
    private String aliPaySeller;
    private String aliPayPrivateKey;

    /**
     * @param pid        签约合作者身份ID*
     * @param seller     商户收款账号
     * @param privateKey 商户私钥，pkcs8格式
     */
    public void setAliPayInfo(String pid, String seller, String privateKey) {
        this.aliPayPID = pid;
        this.aliPaySeller = seller;
        this.aliPayPrivateKey = privateKey;
        SPUtil.getInstance().saveString(CONFIG_ALIPAY_PID, pid);
        SPUtil.getInstance().saveString(CONFIG_ALIPAY_SELLER, seller);
        SPUtil.getInstance().saveString(CONFIG_ALIPAY_PRIVATE_KEY, privateKey);
    }

    /**
     * 签约合作者身份ID
     */
    public String getPID() {
        if (aliPayPID != null)
            return aliPayPID;
        return aliPayPID = SPUtil.getInstance().getString(CONFIG_ALIPAY_PID);

    }

    /**
     * 商户收款账号
     */
    public String getSeller() {
        if (aliPaySeller != null)
            return aliPaySeller;
        return aliPaySeller = SPUtil.getInstance().getString(CONFIG_ALIPAY_SELLER);

    }

    /**
     * 商户私钥，pkcs8格式
     */
    public String getPrivateKey() {
        if (aliPayPrivateKey != null)
            return aliPayPrivateKey;
        return aliPayPrivateKey = SPUtil.getInstance().getString(CONFIG_ALIPAY_PRIVATE_KEY);

    }

    private ConfigAliPay() {
    }

    private static class MyObjectHandler {
        private static ConfigAliPay singleOne = new ConfigAliPay();
    }

    public static ConfigAliPay getInstance() {
        return MyObjectHandler.singleOne;
    }
}
