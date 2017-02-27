package com.xiay.applib;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.nohttp.extra.EncryptUtil;
import com.xiay.applib.bean.DataGuidePic;
import com.xiay.applib.db.DBGuide;
import com.xiay.applib.db.DBGuidePic;
import com.xiay.applib.listener.HttpCallBack;
import com.xiay.applib.service.AppDownPicService;
import com.xiay.applib.util.AppGreenDao;
import com.xiay.applib.util.rxjava.RxUtil;
import com.xiay.applib.util.rxjava.bean.RxIOTask;
import com.xiay.applib.util.rxjava.bean.RxTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.xiay.dialog.ClickListener;
import cn.xiay.util.NetUtil;
import gen.greendao.DBGuideDao;
import gen.greendao.DBGuidePicDao;
import rx.Observable;
import rx.functions.Action1;

import static com.xiay.applib.util.AppGreenDao.getDaoSession;

/**
 * 启动页面
 *
 * @author Xiay
 */
public abstract class AppLaunchAct extends AppActivity {
    /**是否使用网络图片*/
    private   boolean isUseUrlGuidePic=false;
    /**是否使用网络图片*/
    public void setUseUrlGuidePicEnable(boolean useUrlGuidePic) {
        isUseUrlGuidePic = useUrlGuidePic;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxUtil.executeRxTask(new RxTask<Boolean>() {
            @Override
            public void doInIOThread() {
                if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {//避免华为手机返回桌面后重启应用
                    finish();
                    return;
                }
                setConfigUrl();
                EncryptUtil.setPassword(getRequestPassword());
                setValue(NetUtil.isNetworkConnected(AppLaunchAct.this));
            }

            @Override
            public void doInUIThread() {
                if (!getValue()) {
                    getDialog().showCancle("请先连接网络后来哦~").setOnClickView(R.id.btn_mid, new ClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }
                    }).setCancelable(false);
                } else {
                    if (isUseUrlGuidePic){
                        rxManager.add(Observable.just(0).delay(2500, TimeUnit.MICROSECONDS).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                checkGuidePic();
                            }
                        }));
                    }else {
                        DBGuideDao dBGuideDao = getDaoSession().getDBGuideDao();
                        DBGuide dbGuide = dBGuideDao.queryBuilder().unique();
                        if (dbGuide == null) {//如果是第一次启动
                            dbGuide = new DBGuide();
                            dbGuide.isFirstLunch = false;
                            dBGuideDao.save(dbGuide);//保存最新数据
                            isShowGuideWithApp = true;
                        }
                        isShowGuide(isShowGuideWithApp, isShowGuideWithSDPic);
                    }

                }
            }
        });
    }
    /**如果设置加密密码，必须是16位字符串，不开启加密，返回null即可*/
    protected abstract String getRequestPassword();

    boolean isShowGuideWithApp, isShowGuideWithSDPic;

    private void checkGuidePic() {
        sendJsonObjectPost(initParams("getAppGuide"), new HttpCallBack<JSONObject>() {
            @Override
            public void onSucceed(int what, final JSONObject responseData) {
                RxUtil.doInIOThread(new RxIOTask() {
                    @Override
                    public void doInIOThread() {
                        try {
                            DBGuideDao dBGuideDao = getDaoSession().getDBGuideDao();
                            DBGuide dbGuide = dBGuideDao.queryBuilder().unique();
                            if (responseData.getInt("status") == 1) {
                                JSONArray jArr = responseData.getJSONArray("data");
                                if (dbGuide == null) {//如果是第一次启动
                                    dbGuide = new DBGuide();
                                    dbGuide.oldData = jArr.toString();
                                    dbGuide.isFirstLunch = false;
                                    dBGuideDao.save(dbGuide);//保存最新数据
                                    isShowGuideWithApp = true;
                                    startDownPic(jArr, null);
                                } else if (!jArr.toString().equals(dbGuide.oldData)) {//如果服务器数据和本地数据不同相同下载图片，等图片全部下载成功后oldData改成newData,避免下载失败后无法重新下载问题
                                    dbGuide.newData = jArr.toString();
                                    dBGuideDao.save(dbGuide);
                                    DBGuidePicDao dbGuidePicDao = AppGreenDao.getDaoSession().getDBGuidePicDao();
                                    List<DBGuidePic> dbGuidePics = dbGuidePicDao.queryBuilder().list();
                                    if (dbGuidePics.size() > 0) {//表示服务器修改了引导图，需重新下载
                                        startDownPic(jArr, dbGuidePicDao);
                                    }
                                }else if (dbGuide.isShowNewGuide){
                                    isShowGuideWithSDPic=true;
                                    dbGuide.isShowNewGuide=false;
                                    dBGuideDao.save(dbGuide);
                                }
                            }else {//请求接口返回失败
                                if (dbGuide == null) {//如果是第一次启动
                                    isShowGuideWithApp = true;
                                    dBGuideDao.save(new DBGuide());//保存最新数据
                                }
                            }
                            isShowGuide(isShowGuideWithApp, isShowGuideWithSDPic);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailed(int what, JSONObject responseData) {
                isShowGuide(isShowGuideWithApp, isShowGuideWithSDPic);
            }
        });
    }

    /**
     * 下载图片
     */
    private void startDownPic(JSONArray jArr, DBGuidePicDao dbGuidePicDao) throws JSONException {
        if (jArr.length() > 0) {
            DataGuidePic guidePic = new DataGuidePic();
            ArrayList<DBGuidePic> guidePics = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                DBGuidePic dbGuidePic = new DBGuidePic();
                dbGuidePic.setPics(jArr.getJSONObject(i).getString("url"));
                guidePics.add(dbGuidePic);
                guidePic.pics.add(jArr.getJSONObject(i).getString("url"));
            }
            if (dbGuidePicDao != null) {
                dbGuidePicDao.deleteAll();
                dbGuidePicDao.insertInTx(guidePics);
            }
            Intent i = new Intent(getApplicationContext(), AppDownPicService.class);
            i.putExtra(AppDownPicService.GUIDE_PICS, guidePic);
            startService(i);
        }
    }

    /**
     * 是否第一次启动
     *
     * @param isShowGuideWithApp  //是否显示app默认图片
     * @param isShowGuideWithSDPic //是否显示下载好的SD卡中的图片
     */

    protected abstract void isShowGuide(boolean isShowGuideWithApp, boolean isShowGuideWithSDPic);

    /**
     * 设置请求的Url地址
     */
    protected abstract void setConfigUrl();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }
}
