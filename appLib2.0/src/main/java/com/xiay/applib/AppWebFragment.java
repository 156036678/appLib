package com.xiay.applib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
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
public abstract class AppWebFragment extends AppFragment {

	protected WebView webView;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWebView();
		onWebViewCreated(webView);
	}
    protected abstract WebView onWebViewCreated(WebView webView);
	protected void initWebView() {
		ViewGroup layout = (ViewGroup)getView();
		webView = new WebView(getActivity());
		webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.addView(webView);
		 //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	            return true;
	        }
			@Override
			public void onPageFinished(WebView view,String url)
			{
				activity.getDialog().dismiss();
			}
		});
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		WebSettings settings = webView.getSettings();
		if (getPhoneAndroidSDK() >= 14) {// 4.0需打开硬件加速
			getActivity().getWindow().setFlags(0x1000000, 0x1000000);
		}
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 开启 DOM storage API 功能
		settings.setDomStorageEnabled(true);
		// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);//加上这句有的手机无法播放优酷视频

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		webView.removeAllViews();
		webView.destroy();
	}


	@Override
	public void onPause() {
		// webView.pauseTimers();
		try {
			webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}
}
