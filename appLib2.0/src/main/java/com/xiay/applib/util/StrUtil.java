package com.xiay.applib.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xiay on 2015/12/16 0016.
 */
public class StrUtil {
    /**
     * 验证邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }
    /**
     * 描述：手机号格式验证.
     *
     * @param str 指定的手机号码字符串
     * @return 是否为手机号码格式:是为true，否则false
     */
    public static boolean isMobileNo(String str) {
        if (str.length()!=11)
            return false;
        boolean isMobileNo = false;
        try {
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(str);
            isMobileNo = m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMobileNo;
    }
    /**
     * 描述：把手机号码中间4位显示成星号（*）
     * @param str 指定的字符串
     */
    public static String hidePhoneNum(String str) {
        if (str.length()<11)
            return "";
        return str.substring(0,3) + "****" + str.substring(7, str.length());
    }

    /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        if(cardId.length()<15){
            return false;
        }
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if(bit == 'N'){
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }
    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    private static char getBankCardCheckCode(String nonCheckCodeCardId){
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }
    public static String formatNum(Object value) {
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        String newvalue;
        if (value instanceof String)
            newvalue =decimalFormat.format(Double.valueOf((String) value));
        else
            newvalue =decimalFormat.format(value);
       return newvalue.equals(".00")?"0":newvalue;
    }
    public static String insertStringAtPosition(CharSequence src, String dec, int position){
        StringBuffer stringBuffer = new StringBuffer(src);
        return stringBuffer.insert(position, dec).toString();

    }
}
