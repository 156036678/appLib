package com.xiay.applib;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xiay.applib.image.crop.Crop;
import com.xiay.applib.ui.dialog.MyToast;

import java.io.File;

/**
 */
public class AppSelectPicAct extends Activity{
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	/***
	 * 图片路径
	 */
	public static final String PHOTO_PATH = "photo_path";
	private RelativeLayout pop_layout;
	/**获取到的图片路径*/
	public static String picPath;
	private Uri photoUri;
	/**图片剪切*/
	public static final String CORP="corp";
	/**是否剪切*/
	public  boolean isCorp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//隐藏状态栏
		//定义全屏参数
		int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//获得当前窗体对象
		Window window=getWindow();
		//设置当前窗体为全屏显示
		window.setFlags(flag, flag);
		setContentView(R.layout.select_pic);
		initView();
	}
	/**
	 * 初始化加载View
	 */
	public void initView() {
		isCorp=getIntent().getBooleanExtra(CORP,false);
		/**获取剪切图片的参数*/
        pop_layout = (RelativeLayout) findViewById(R.id.rl_pop_layout);
        pop_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		findViewById(R.id.btn_take_photo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});
		findViewById(R.id.btn_pick_photo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pickPhoto();
			}
		});
		findViewById(R.id.btn_cancle).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		//执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if(SDState.equals(Environment.MEDIA_MOUNTED))
		{
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
			/***
			 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
			 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
			 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
			 */
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		//	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(saveImagePath)));  
		//	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  
			/**-----------------*/
			startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
		}else{
			Toast.makeText(this,"内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		// 激活系统图库，选择一张图片
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	//	closeActivity();
		return super.onTouchEvent(event);
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode==RESULT_OK){
			 if (requestCode == PHOTO_REQUEST_GALLERY) {//从相册
				 if (data != null) {
					 // 得到图片的全路径
					Uri uri = data.getData();
					 if (isCorp){
						 beginCrop(uri,new File( getImageAbsolutePath(this,uri)).getName());
						// crop(uri);
					 }else {
						 Intent i=new Intent();
						 i.putExtra(PHOTO_PATH,getImageAbsolutePath(this,uri));
						 setResult(RESULT_OK, i);
						 finish();
					 }
					 //crop(uri);
//					 Bitmap bitmap = data.getParcelableExtra("data");
				 }else {
					 finish();
				 }
			 } else if (requestCode == PHOTO_REQUEST_CAMERA) { //拍照
				 String[] pojo = {MediaStore.Images.Media.DATA};
				 Cursor cursor = null;
				 if (android.os.Build.VERSION.SDK_INT >= 11) {
					 try {
						 cursor =  getContentResolver().query(photoUri, pojo, null, null, null);
					 } catch (Exception e) {
						 try {
							 cursor = managedQuery(photoUri, pojo, null, null, null);
						 } catch (Exception e1) {
							 e1.printStackTrace();
						 }
						 e.printStackTrace();
					 }
				 }else {
					 cursor = managedQuery(photoUri, pojo, null, null, null);
				 }

				 if(cursor != null )
				 {
					 int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
					 cursor.moveToFirst();
					 picPath = cursor.getString(columnIndex);///storage/emulated/0/DCIM/Camera/1447300775249.jpg
					 //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
					 if(Integer.parseInt(Build.VERSION.SDK) < 14)
					 {
						 cursor.close();
					 }
					 if(picPath != null && ( picPath.endsWith(".png") || picPath.endsWith(".PNG") ||picPath.endsWith(".jpg") ||picPath.endsWith(".JPG")  ))
					 {
						 if (isCorp){
							 File filePath=new File(picPath);
							 beginCrop(Uri.fromFile(filePath),filePath.getName());
						 }else {
							 Intent i=new Intent();
							 i.putExtra(PHOTO_PATH,picPath);
							 setResult(RESULT_OK, i);
							 finish();
						 }
						 //Uri.fromFile(new File(picPath));
						 //	Uri uri = data.getData();
						 //crop(Uri.fromFile(new File(picPath)));
					 }else{
						 Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
					 }
				 }else {
					 MyToast.showError("系统出错，请退出后重试");
				 }



			 } else if (requestCode == Crop.REQUEST_CROP) {
				 try {
					 Intent i=new Intent();
					 i.putExtra(PHOTO_PATH,getImageAbsolutePath(this,Crop.getOutput(data)));
					 setResult(RESULT_OK, i);
					 finish();
//					 if (data != null) {
//						 Bitmap bitmap = data.getParcelableExtra("data");
//						 Intent i=new Intent();
//						 i.putExtra("data", bitmap);
//						 setResult(RESULT_OK, i);
//						 finish();
//					 }else{
//						 finish();
//					 }

				 } catch (Exception e) {
					 e.printStackTrace();
				 }

			 }
		 }
	}
	


	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	public static String getImageAbsolutePath(Context context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}
	private void beginCrop(Uri source,String fileName) {
		Uri destination = Uri.fromFile(new File(getCacheDir(), fileName));
		Crop.of(source, destination).asSquare().start(this);
	}
	/**
	 * 剪切图片
	 *
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri

	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小,不能太大
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	} */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
