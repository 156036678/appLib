/*************************************************************************************************
 * 版权所有 (C)2012,  深圳市康佳集团股份有限公司 
 *
 * 文件名称：AppUpdateService.java
 * 内容摘要：升级服务
 * 当前版本：
 * 作         者： hexiaoming
 * 完成日期：2012-12-24
 * 修改记录：
 * 修改日期：
 * 版   本  号：
 * 修   改  人：
 * 修改内容：
 ************************************************************************************************/
package com.xiay.applib.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.xiay.applib.R;
import com.xiay.applib.bean.AppUpdateInfo;
import com.xiay.applib.receiver.AppReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.xiay.util.AppHelper;

/***
 * 升级服务
 *
 * @author xiay
 *
 */
public class AppUpdateService extends Service {

	/********download progress step*********/
	private static final int down_step_custom = 3;

	private static final int TIMEOUT = 10 * 1000;// 超时
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private NotificationManager notificationManager;
	private Intent updateIntent;
	private RemoteViews contentView;
     private  File savePath;
	private File filePath;
	private static final int NOTIFICATION_FLAG = 1;
	private Notification notification;
	private AppUpdateInfo appInfo;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 方法描述：onStartCommand方法
	 * @param    intent, int flags, int startId
	 * @return    int
	 * @see     AppUpdateService
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//app_name = intent.getStringExtra("Key_App_Name");
		//down_url = intent.getStringExtra("Key_Down_Url");
		if (intent!=null){
			appInfo=  intent.getParcelableExtra("appInfo");
			// create file,应该在这个地方加一个返回值的判断SD卡是否准备好，文件是否创建成功，等等！
			if(	createFile(appInfo.getDownName())){
				createNotification();
				createThread();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
  /**创建文件是否成功 成功返回true */
	private boolean  createFile(String appName) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			 savePath = new File(  AppHelper.getInstance().getSDPath()+ "/updateApp/");
			filePath = new File(savePath + "/" + appName + ".apk");
			if (!savePath.exists()) {
				savePath.mkdirs();
			}
			if (!filePath.exists()) {
				try {
					filePath.createNewFile();
					return true;
				} catch (IOException e) {
					Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
					/***************stop service************/
					stopSelf();
					e.printStackTrace();
					return false;
				}
			}else {
				return true;
			}

		}else{
			return false;
		}
	}

	/********* update UI******/
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWN_OK:

					/*********下载完成，点击安装***********/
					String strFilePath=filePath.getAbsolutePath();
					AppHelper.getInstance().installApk(getApplication(),strFilePath);
					contentView.setTextViewText(R.id.notificationTitle,"下载完成，点击安装");
					contentView.setTextViewText(R.id.notificationPercent,"100%");
					contentView.setProgressBar(R.id.notificationProgress, 100, 100, false);
					Intent installIntent = new Intent(getApplicationContext(), AppReceiver.class);
					installIntent.putExtra("filePath", strFilePath);
					installIntent.setAction(AppReceiver.INSTALL);
					final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),NOTIFICATION_FLAG, installIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					Notification.Builder builder = new Notification.Builder(AppUpdateService.this);
					builder.setContentText(appInfo.getDownName() +"下载完成，点击安装");
					builder.setContentTitle(appInfo.getDownName());
					builder.setSmallIcon(appInfo.getIconRersource());
					builder.setTicker("下载完成，点击安装");
					builder.setAutoCancel(true);
					builder.setContent(contentView);
					builder.setWhen(System.currentTimeMillis());
					builder.setContentIntent(pendingIntent);
					builder.setDefaults(Notification.DEFAULT_ALL);//有4种
					 notification = builder.build();
					notificationManager.notify(R.layout.notification_item, notification);
					// notification.setLatestEventInfo(AppUpdateService.this,appInfo.getDownName(), appInfo.getDownName() +"下载完成，点击安装", pendingIntent);
					//stopService(updateIntent);
					/***stop service*****/
					stopSelf();
					break;

				case DOWN_ERROR:
					Notification.Builder builder1 = new Notification.Builder(AppUpdateService.this);
					builder1.setContentText(appInfo.getDownName() +"下载失败");
					builder1.setContentTitle(appInfo.getDownName());
					builder1.setSmallIcon(appInfo.getIconRersource());
					builder1.setTicker(appInfo.getDownName() +"下载失败");
					builder1.setAutoCancel(true);
					builder1.setContent(contentView);
					builder1.setWhen(System.currentTimeMillis());
					notification = builder1.build();
					notificationManager.notify(R.layout.notification_item, notification);
					/***stop service*****/
					//onDestroy();
					stopSelf();
					break;

				default:
					break;
			}
		}
	};

	/**
	 * 方法描述：createThread方法, 开线程下载
	 * @param
	 * @return
	 * @see     AppUpdateService
	 */
	public void createThread() {
		new DownLoadThread().start();
	}

	private class DownLoadThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			try {
				String strFilePath= filePath.toString();
				long downloadSize = downloadUpdateFile(appInfo.getDownUrl(),strFilePath);
				if (downloadSize > 0) {
					// down success										
					message.what = DOWN_OK;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}



	/**
	 * 方法描述：createNotification方法
	 * @param
	 * @return
	 * @see     AppUpdateService
	 */
	public void createNotification() {
		//notification = new Notification(R.drawable.dot_enable,app_name + getString(R.string.is_downing) ,System.currentTimeMillis());
//		notification = new Notification(
//				//R.drawable.video_player,//应用的图标
//				appInfo.getIconRersource(),//应用的图标
//				appInfo.getDownName(),
//				System.currentTimeMillis());
//		notification.flags = Notification.FLAG_ONGOING_EVENT;

		/*** 自定义  Notification 的显示****/
		contentView = new RemoteViews(getPackageName(),R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, appInfo.getDownName() );
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		Notification.Builder builder = new Notification.Builder(AppUpdateService.this);
		builder.setContentTitle(appInfo.getDownName());
		builder.setSmallIcon(appInfo.getIconRersource());
		builder.setAutoCancel(false);
		builder.setContent(contentView);
		builder.setWhen(System.currentTimeMillis());
		notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.contentView = contentView;
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	/***
	 * down file
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		HttpURLConnection httpURLConnection = null;
		int downloadCount = 0;// 已经下载好的大小
      try {
		  int down_step = down_step_custom;// 提示step
		  int totalSize;// 文件总大小
		  int updateCount = 0;// 已经上传的文件大小

		  URL url = new URL(down_url);
		  httpURLConnection = (HttpURLConnection) url.openConnection();
		  httpURLConnection.setConnectTimeout(TIMEOUT);
		  httpURLConnection.setReadTimeout(TIMEOUT);
		  // 获取下载文件的size
		  totalSize = httpURLConnection.getContentLength();
		  if (httpURLConnection.getResponseCode() == 404) {
			  throw new Exception("fail!");
			  //这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
			  //所以不用处理
		  }
		  if (filePath.exists()&&totalSize == filePath.length()){
			  // System.out.println("xxxxxxxxxxx 文件存在="+path);
			  return filePath.length();
		  }
		  inputStream = httpURLConnection.getInputStream();
		  outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉

		  byte buffer[] = new byte[1024*6];
		  int readsize = 0;

		  while ((readsize = inputStream.read(buffer)) != -1) {

//			/*********如果下载过程中出现错误，就弹出错误提示，并且把notificationManager取消*********/
//			if (httpURLConnection.getResponseCode() == 404) {
//				notificationManager.cancel(R.layout.notification_item);
//				throw new Exception("fail!");
//				//这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
//				//所以不用处理
//			}

			  outputStream.write(buffer, 0, readsize);
			  downloadCount += readsize;// 时时获取下载到的大小
			  /*** 每次增张3%**/
			  if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				  updateCount += down_step;
				  // 改变通知栏
				  contentView.setTextViewText(R.id.notificationPercent,updateCount + "%");
				  contentView.setProgressBar(R.id.notificationProgress, 100,updateCount, false);
				  notification.contentView = contentView;
				  notificationManager.notify(R.layout.notification_item, notification);
			  }
		  }
	  }finally {
		  if (httpURLConnection != null) {
			  httpURLConnection.disconnect();
		  }if (inputStream!=null){
			  inputStream.close();
		  }if (outputStream!=null)
		  outputStream.close();
	  }
		return downloadCount;
	}

}