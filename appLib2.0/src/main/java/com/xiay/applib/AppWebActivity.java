package com.xiay.applib;

import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;

/**
 * 基本web类
 * @author Xiay
 */
public abstract class AppWebActivity extends AppActivity {

	protected WebView web;
	protected void addWebView(ViewGroup content) {
		if (web==null){
			web = new WebView(this);
			web.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			content.addView(web);
			getDialog().showLoading("加载中");
			//覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
			web.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
					view.loadUrl(url);
					return true;
				}
				@Override
				public void onPageFinished(WebView view,String url)
				{
					getDialog().dismiss();
				}
			});
			web.setWebChromeClient(new WebChromeClient(){
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);
				}
			});
			WebSettings settings = web.getSettings();
			if (getPhoneAndroidSDK() >= 14) {// 4.0需打开硬件加速
				getWindow().setFlags(0x1000000, 0x1000000);
			}
			// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//加上这句有的手机无法播放优酷视频
			settings.setJavaScriptEnabled(true);
			settings.setJavaScriptCanOpenWindowsAutomatically(true);
			// 开启 DOM storage API 功能
			settings.setDomStorageEnabled(true);
			settings.setUseWideViewPort(true);
			settings.setLoadWithOverviewMode(true);
		}
	}

	@SuppressWarnings("deprecation")
	protected int getPhoneAndroidSDK() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return version;

	}
	//改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(web.canGoBack())
            {
            	web.goBack();//返回上一页面
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(web !=null) {
			web.removeAllViews();
			web.destroy();
		}
	}

	@Override
	protected void onPause() {
		// webView.pauseTimers();
		try {
			web.getClass().getMethod("onPause").invoke(web, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			web.getClass().getMethod("onResume").invoke(web, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}
}
