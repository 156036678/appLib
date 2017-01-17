package com.xiay.applib.listener;

/**
 * @author Xiay
 */
public interface AppUpdateListener {
    /***
     *
     * @param updateUrl  不为null 表示有更新
     * @param isForce  是否强制更新
     */
     void updateListener(String updateUrl,boolean isForce);

}
