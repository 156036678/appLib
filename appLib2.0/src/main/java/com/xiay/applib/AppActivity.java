package com.xiay.applib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.nohttp.RequestMethod;
import com.nohttp.extra.AbHttpActivity;
import com.nohttp.extra.EncryptUtil;
import com.nohttp.extra.HttpListener;
import com.nohttp.rest.JsonArrayRequest;
import com.nohttp.rest.JsonObjectRequest;
import com.nohttp.rest.Request;
import com.nohttp.rest.StringRequest;
import com.xiay.applib.config.ConfigUrl;
import com.xiay.applib.request.BeanReqeust;
import com.xiay.applib.ui.dialog.MyDialog;
import com.xiay.applib.util.StatusBarUtil;
import com.xiay.applib.util.autolayout.AutoFrameLayout;
import com.xiay.applib.util.autolayout.AutoLinearLayout;
import com.xiay.applib.util.autolayout.AutoRelativeLayout;
import com.xiay.applib.util.rxjava.RxManager;
import com.xiay.applib.view.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import cn.xiay.dialog.BaseDialog;
import cn.xiay.dialog.ClickListener;
import cn.xiay.ui.Toast;
import cn.xiay.util.AppActivityManager;
import cn.xiay.util.AppHelper;

/**
 * 描述：继承BaseHttpActivity 可实现网络请求和加载网络图片
 *
 * @author Xiay .
 */
public abstract class AppActivity extends AbHttpActivity {
    /**是否有返回按钮*/
    private boolean hasBackButton;
    private boolean isScaleView = true;
    protected int layoutId;
    public TitleBar titleBar;
    public RxManager rxManager = new RxManager();
    public AppActivity activity;
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs)
    {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT))
        {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT))
        {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT))
        {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }
    protected void onCreate(Bundle paramBundle) {
        activity=this;
        super.onCreate(paramBundle);
        int statusBarColor = getStatusBarColor();
        initPageView();
        if (statusBarColor != 0) {
            StatusBarUtil.setColor(this, getResources().getColor(statusBarColor), 0);
            if (titleBar!=null)
            titleBar.setBackgroundColor(getResources().getColor(statusBarColor));
        }
    }

    protected void initPageView() {
        if (layoutId != -1) {//如果是-1,说明是通过Binding方式设置的布局
            layoutId = setContentView();
            View view;
            if (layoutId != 0) {
                super.setContentView(setContentView());
              //  view = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            } else {
                view = setNewContentView();
                if (view != null)
                    super.setContentView(view);
            }
           // scaleView(view);
        }
        setPageTitle(setTitle(), setTitleTextColor());
    }

//    public void scaleView(View view) {
//        if (view != null) {
//            if (view instanceof ViewGroup) {
//                ViewGroup content = (ViewGroup) view;
//                if (isScaleView) {
//                    ViewUtil.scaleContentView(content);
//                }
//            } else {
//                if (isScaleView) {
//                    ViewUtil.scaleView(view);
//                }
//            }
//        }
//    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
     *
     * @param needOffsetView 需要向下偏移的 View
     */
    public void setTranslucentForImageView(View needOffsetView) {
        StatusBarUtil.setTranslucentForImageView(this, 0, needOffsetView);
    }

    /**
     * 设置状态栏颜色
     */
    public abstract int getStatusBarColor();

    public abstract int setContentView();

    /**
     * 设置New出来的View
     */
    public View setNewContentView() {
        return null;
    }

    /**
     * 设置标题
     */
    public abstract String setTitle();

    /**
     * 设置标题文字颜色
     */
    public int setTitleTextColor() {
        return getResources().getColor(R.color.white);
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected void setPageTitle(String title) {
        setPageTitle(title, -1);
    }

    protected void setPageTitle(String title, int textColor) {
        titleBar=findView(R.id.title_bar);
        if (titleBar!=null){
            if ( title != null){
                titleBar.setTitle(title);
            }
            if (textColor != -1) {
                titleBar.setTitleColor(textColor);
            }
        }
    }

    /**
     * 是否自动适配屏幕
     * 默认ture
     */
    public void isAutoScaleView(boolean scaleView) {
        isScaleView = scaleView;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 固定文字大小，可选
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 退出应用时弹出提示对话框 可选
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isCanBack()) {
                ((MyDialog) getDialog()).showExit(new ClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppActivityManager.getInstance().AppExit(AppActivity.this);
                    }

                });
            } else {
                closeActivity();
            }

            return true;
        }
        return false;
    }
    public boolean isCanBack(){
        return  hasBackButton;
    }
    public boolean setCanBack(){
        return  hasBackButton=true;
    }

    //========================================= 以下实现代码为固定写法  =========================================
    @Override
    public BaseDialog getDialog() {
        return initDialog();
    }

    private BaseDialog initDialog() {
        if (dialog == null) {
            synchronized (AppActivity.this) {
                dialog = new MyDialog(this);
                dialog.setCanceledOnTouchOutside(false);
            }
        }
        return dialog;
    }

    public MyDialog getMyDialog() {
        return (MyDialog) initDialog();
    }

    /**
     * 请求基本json数据（新)
     * type
     *
     * @return
     */
    public Map<String, String> initParams(String method) {
        Map<String, String> params = new ArrayMap<>();
        params.put("method", method);
        return params;
    }

    public <T> void sendBeanPost(Class<T> clazz, final Map<String, String> params, HttpListener<T> httpCallback) {
        sendBeanPost(clazz, params, null, httpCallback);
    }

    public <T> void sendBeanPost(Class<T> clazz, final Map<String, String> params, String dialogMsg, HttpListener<T> httpCallback) {
        sendPost(0, new BeanReqeust<>(clazz), params, dialogMsg, httpCallback);
    }

    public <T> void sendBeanPost(String url, Class<T> clazz, final Map<String, String> params, HttpListener<T> httpCallback) {
        sendBeanPost(url, clazz, params, null, httpCallback);
    }

    public <T> void sendBeanPost(String url, Class<T> clazz, final Map<String, String> params, String dialogMsg, HttpListener<T> httpCallback) {
        sendBeanPost(0, url, clazz, params, dialogMsg, httpCallback);
    }

    public <T> void sendBeanPost(int what, String url, Class<T> clazz, final Map<String, String> params, String dialogMsg, HttpListener<T> httpCallback) {
        sendPost(what, new BeanReqeust<>(url, clazz), params, dialogMsg, httpCallback);
    }

    public void sendStringPost(final Map<String, String> params, HttpListener<String> httpCallback) {
        sendStringPost(0, params, null, httpCallback);
    }

    public void sendStringPost(final Map<String, String> params, String dialogMsg, HttpListener<String> httpCallback) {
        sendStringPost(0, params, dialogMsg, httpCallback);
    }

    public void sendStringPost(int what, final Map<String, String> params, String dialogMsg, HttpListener<String> httpCallback) {
        sendPost(what, new StringRequest(), params, dialogMsg, httpCallback);
    }

    public void sendJsonObjectPost(final Map<String, String> params, HttpListener<JSONObject> httpCallback) {
        sendJsonObjectPost(params, null, httpCallback);
    }

    public void sendJsonObjectPost(final Map<String, String> params, String dialogMsg, HttpListener<JSONObject> httpCallback) {
        sendPost(0, new JsonObjectRequest(), params, dialogMsg, httpCallback);
    }

    public void sendJsonObjectPost(String url, final Map<String, String> params, String dialogMsg, HttpListener<JSONObject> httpCallback) {
        sendPost(0, new JsonObjectRequest(url), params, dialogMsg, httpCallback);
    }

    public void sendJsonObjectPost(int what, String url, final Map<String, String> params, String dialogMsg, HttpListener<JSONObject> httpCallback) {
        sendPost(what, new JsonObjectRequest(url), params, dialogMsg, httpCallback);
    }

    public void sendJsonArrayPost(final Map<String, String> params, HttpListener<JSONArray> httpCallback) {
        sendJsonArrayPost(0, params, null, httpCallback);
    }

    public void sendJsonArrayPost(int what, final Map<String, String> params, String dialogMsg, HttpListener<JSONArray> httpCallback) {
        sendPost(what, new JsonArrayRequest(), params, dialogMsg, httpCallback);
    }

    public <T> void sendPost(Request<T> request, final Map<String, String> params, String dialogMsg, HttpListener<T> httpCallback) {
        sendPost(0, request, params, dialogMsg, httpCallback);
    }

    public <T> void sendPost(int what, Request<T> request, final Map<String, String> params, String dialogMsg, HttpListener<T> httpCallback) {
        request.setRequestMethod(RequestMethod.POST);
        if (request.getUrl() == null) {
            if (getUrl() == null)
                request.setUrl(ConfigUrl.getUrl());
            else
                request.setUrl(getUrl());
        }
        String time = System.currentTimeMillis() + "";
        ArrayMap<String,String> fixedParams = new ArrayMap<>();
        fixedParams.put("time", time + "");
        fixedParams.put("plat", "2");
        fixedParams.put("version", AppHelper.getInstance().getVersionCode() + "");
        fixedParams.put("sign", AppHelper.getInstance().getIMEI());
        fixedParams.put("method", params.get("method"));
        params.remove("method");
        fixedParams.put("data", new Gson().toJson(params));
        params.put("method", fixedParams.get("method"));
        Map<String, String> paramsFinal = new ArrayMap<>();
        if (EncryptUtil.isEnable()) {//如果开启加密
            paramsFinal.put("params", EncryptUtil.encrypt(new Gson().toJson(fixedParams)));
        } else {
            paramsFinal.put("params", new Gson().toJson(fixedParams));
        }
        sendRequest(what, request, paramsFinal, dialogMsg, false, httpCallback);
    }

    public String getUrl() {
        return null;
    }

    /**
     * 跳转activity
     */
    public void openActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * 跳转activity
     */
    public void openActivity(Class clazz) {
        openActivity(new Intent(this, clazz));
    }

    /**
     * 跳转activity
     *
     * @param clazz
     * @param parmas 传值格式  key,value
     */

    public void openActivity(Class clazz, Object... parmas) {
        if (parmas != null) {
            Intent i = new Intent(this, clazz);
            int parmasLenth = parmas.length;
            if (parmasLenth % 2 != 0) {
                Toast.show("参数格式为key,value");
            } else {
                parmasLenth = parmasLenth / 2;
                for (int j = 0; j < parmasLenth; j++) {
                    Object parmas1 = parmas[j + j];
                    Object parmas2 = parmas[j + j + 1];
                    if (parmas1 instanceof String) {
                        String key=(String) parmas1;
                        if (parmas2 instanceof String)
                            i.putExtra(key, (String) parmas2);
                        else if (parmas2 instanceof Integer)
                            i.putExtra(key, (int) parmas2);
                        else if (parmas2 instanceof Boolean)
                            i.putExtra(key, (boolean) parmas2);
                        else if (parmas2 instanceof Parcelable)
                            i.putExtra(key, (Parcelable) parmas2);

                    } else {
                        Toast.show("参数key类型不对");
                        return;
                    }
                }
            }
            openActivity(i);
        }
    }

    /**
     * 跳转activity
     */
    public void openActivitys(Intent[] intents) {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        startActivities(intents);
    }
  public void addBackButton(int imageResId){
      hasBackButton = true;
      if (titleBar!=null){
          titleBar.setLeftImageResource(imageResId);
          titleBar.setLeftLayoutVisibility(View.VISIBLE);
          titleBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  closeActivity();
              }
          });
      }

    }
    /**
     * 跳转activity
     */
    public void openActivitys(Class... classes) {
        Intent[] intents = new Intent[classes.length];
        for (int i = 0; i < classes.length; i++) {
            Intent intent = new Intent(this, classes[i]);
            intents[i] = intent;
        }
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        startActivities(intents);
    }

    /**
     * 跳转activity
     */
    public void openActivityForResult(Class<?> cls, int requestCode) {
        openActivityForResult(new Intent(this, cls), requestCode);
    }

    /**
     * 跳转activity
     */
    public void openActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    /**
     * 替换Fragment
     *
     * @param viewId
     * @param fragment
     * @param isAddToBack
     */
    public void replace(int viewId, Fragment fragment, boolean isAddToBack) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(viewId, fragment);
        if (isAddToBack)
            mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commitAllowingStateLoss();

    }

    /**
     * 替换Fragment
     *
     * @param viewId
     * @param fragment
     */
    public void replace(int viewId, Fragment fragment) {
        replace(viewId, fragment, false);
    }


    @Override
    protected void onDestroy() {
        if (getDialog().isShowing())
            getDialog().dismiss();
        if (rxManager != null)
            rxManager.clear();
        super.onDestroy();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            AppHelper.getInstance().hideSoftInput(this);
        }
        return super.onTouchEvent(event);
    }
}
