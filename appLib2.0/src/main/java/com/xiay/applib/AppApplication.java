package com.xiay.applib;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.nohttp.Config;
import com.nohttp.NoHttp;
import com.nohttp.cache.DBCacheStore;
import com.nohttp.cookie.DBCookieStore;
import com.nohttp4okhttp.OkHttpNetworkExecutor;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiay.applib.bean.AppAction;
import com.xiay.applib.ui.dialog.MyToast;
import com.xiay.applib.util.rxjava.RxBus;
import com.xiay.applib.util.rxjava.RxUtil;
import com.xiay.applib.util.rxjava.bean.RxTask;

import cn.xiay.bean.MyDevice;
import cn.xiay.ui.Toast;
import cn.xiay.util.SPUtil;
import cn.xiay.util.SystemUtil;
import cn.xiay.util.log.Log;

public  class AppApplication extends Application {
	public static int versionCode;
	public static ArrayMap<String, Object> data=new ArrayMap();
	public  static boolean isDebug=false;
	public static Context context;
	@Override
	public void onCreate() {
		context=getApplicationContext();
		// 获取当前包名
		String packageName = context.getPackageName();
		if (packageName.equals(SystemUtil.getInstance().getProcessName(android.os.Process.myPid()))) {
			RxUtil.executeRxTask(new RxTask() {
				@Override
				public void doInIOThread() {
					SPUtil.init(context);
					displayMetrics(context);
					//NoHttp.initialize(AppTinkerApplication.this);
					// 如果你需要自定义配置：
					NoHttp.initialize(context, new Config()
							// 设置全局连接超时时间，单位毫秒，默认10s。
							.setConnectTimeout(15 * 1000)
							// 设置全局服务器响应超时时间，单位毫秒，默认10s。
							.setReadTimeout(15 * 1000)
							// 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
							.setCacheStore(new DBCacheStore(context).setEnable(true) // 如果不使用缓存，设置false禁用。
							)
							// 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
							.setCookieStore(new DBCookieStore(context).setEnable(false) // 如果不维护cookie，设置false禁用。
							)
							// 配置网络层，默认使用URLConnection，如果想用OkHttp：OkHttpNetworkExecutor。
							.setNetworkExecutor(new OkHttpNetworkExecutor())
					);
					//  SealAppContext.init(context);
					//获取手机IMEI
					versionCode= SystemUtil.getInstance().getVersionCode(context);
					MyDevice.IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

					if (MyDevice.IMEI!=null)
						SPUtil.saveString("imei",MyDevice.IMEI);
//					if (!isDebug)
//						CrashReport.initCrashReport(context, "900027867", isDebug);
					initData();
					CrashReport.initCrashReport(getApplicationContext(), "c5b62410e5", true);
				}
				@Override
				public void doInUIThread() {
					Toast.init(context);
					MyToast.init(context);
				}
			});
		}

		super.onCreate();
	//	Log.i("zzzz onCreate="+SystemUtil.getInstance().getCurProcessName(context));

	}
	public  void initData(){}
	/**计算屏幕宽高,通过Device类获取*/
	public  void displayMetrics(Context ctx) {
		WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		DisplayMetrics dm=new DisplayMetrics();
		display.getMetrics(dm);
		int w=dm.widthPixels;
		int h=dm.heightPixels;
		MyDevice.sDensity=dm.density;
		MyDevice.sScaledDensity=dm.scaledDensity;
		MyDevice.sWidth=w;
		MyDevice.sHeight=h;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {// 判断底部navigator是否已经显示
			DisplayMetrics realDisplayMetrics = new DisplayMetrics();
			display.getRealMetrics(realDisplayMetrics);
			int realHeight = realDisplayMetrics.heightPixels;
			int realWidth = realDisplayMetrics.widthPixels;
			MyDevice.isSoftKeysShow=(realWidth - MyDevice.sWidth) > 0 || (realHeight - MyDevice.sHeight) > 0;
		}
		//解决横屏比例问题
		if (w>h) {
			MyDevice.sWidth=h;
			MyDevice.sHeight=w;
		}
	}
	public void setIsDebug(boolean isDebug){
		AppApplication.isDebug=isDebug;
		if (isDebug){
			Log.isPrint=true;
		}
	}
	/**数据初始化完成之后必须调用此方法*/
	public void initFinish(){
		RxBus.get().post(AppAction.ON_APPLICATION_INIT_FINISH);
	}
}