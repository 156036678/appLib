package com.xiay.applib.wxpay;

/**微信支付的方式
 */
public class WxPay {
    /**是否是充值*/
    private static boolean isTopUp;
    private static boolean isPay;
    /**开始支付*/
    public static void starPay(){
        isPay=true;
    }
    /**开始充值*/
    public static void starTopUp(){
        isTopUp=true;
    }
    /**是否是充值*/
    public static boolean isTopUp(){
       return isTopUp==true;
    }
    /**是否是支付*/
    public static boolean isPay(){
       return isPay==true;
    }
    public static void init(){
        isPay=false;
        isTopUp=false;
    }
}
