package com.xiay.applib.util;

import com.nohttp.NoHttp;
import com.nohttp.download.DownloadListener;
import com.nohttp.download.DownloadRequest;
import com.nohttp.extra.HttpUtil;


public class DownloadUtil {
    /**
     * 下载请求.
     */
    private DownloadRequest mDownloadRequest;
    /**
     * @param url 下载地址
     * @param fileFolder 保存的文件夹
     * @param fileName 文件名
     * @param isRange 是否断点续传下载
     * @param isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功
     * @param downloadListener
     */
    public  void downFile(int what,String url,String fileFolder, String fileName,boolean isRange,boolean isDeleteOld,DownloadListener downloadListener){
        mDownloadRequest = NoHttp.createDownloadRequest(url, fileFolder, fileName, isRange, isDeleteOld);
        HttpUtil.getDownloadInstance().add(what, mDownloadRequest, downloadListener);
    }
    /**
     * 默认断点续传
     * @param url 下载地址
     * @param fileFolder 保存的文件夹
     * @param fileName 文件名
     * @param downloadListener
     */
    public  void downFile(String url,String fileFolder, String fileName,DownloadListener downloadListener){
        downFile(0,url,fileFolder,fileName,true,true,downloadListener);
    }
}
