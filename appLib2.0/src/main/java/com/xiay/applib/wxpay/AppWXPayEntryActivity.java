package com.xiay.applib.wxpay;


import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiay.applib.AppActivity;
import cn.xiay.util.SPUtil;


public abstract class AppWXPayEntryActivity extends AppActivity implements IWXAPIEventHandler {
    protected IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, SPUtil.getString("WxPayAppId"));
        api.handleIntent(getIntent(), this);
    }

    @Override
    public int getStatusBarColor() {
        return 0;
    }

    @Override
    public int setContentView() {
        return 0;
    }

    @Override
    public String setTitle() {
        return null;
    }

    @Override
    public int getPageHeaderColorResources() {
        return 0;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }
    public abstract void onSuccess();
    public abstract void onCancle();
    public abstract void onFail(BaseResp resp);
    @Override
    public void onReq(BaseReq baseReq) {
    }
    //支付结果回调
    @Override
    public void onResp(BaseResp resp) {
        {
            if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                if (resp.errCode==0){//支付成功
                    onSuccess();
                }else if (resp.errCode==-2){//您已取消支付!
                    onCancle();
                }
                else {
                    onFail(resp);
                }
            }
            WxPay.init();
            finish();
        }
    }
}