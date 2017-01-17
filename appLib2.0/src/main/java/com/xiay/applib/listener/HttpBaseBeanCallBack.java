package com.xiay.applib.listener;


import com.xiay.applib.bean.BaseBean;
import com.xiay.applib.ui.dialog.MyToast;

public class HttpBaseBeanCallBack extends HttpCallBack<BaseBean> {
    @Override
    public void onSucceed(int what, BaseBean responseData) {
        if (responseData!=null){
            if (responseData.isSuccess()){
                MyToast.showOk(responseData.msg);
                onSucceed(responseData);
            }else {
                MyToast.showError(responseData.msg);
            }
        }else {
            MyToast.showError("服务器无数据返回");
        }

    }

    @Override
    public void onFailed(int what, BaseBean responseData) {
        super.onFailed(what, responseData);
    }

    public void onSucceed(BaseBean responseData) {}
    public void onFailed(BaseBean responseData) {}
}
