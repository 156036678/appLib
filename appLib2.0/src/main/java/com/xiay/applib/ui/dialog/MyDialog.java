package com.xiay.applib.ui.dialog;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiay.applib.R;

import cn.xiay.dialog.BaseDialog;
import cn.xiay.dialog.ClickListener;
import cn.xiay.util.autolayout.utils.AutoUtils;

import static com.xiay.applib.R.id.btn_mid;

/**
 * 创建自定义dialog
 * 
 * @author xiay
 */

public class MyDialog extends BaseDialog {
	Context ctx;
	public MyDialog(Context context) {
		super(context, R.style.Dialog);
		ctx=context;
	}
	public MyDialog(Context context, int layout) {
		super(context, layout, R.style.Dialog);
		ctx=context;
    
	}

	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		super.setOnDismissListener(listener);
		setCancelable(true);
	}

	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
	}
	@Override
	public BaseDialog setOnClickView(View view) {
		return super.setOnClickView(view);
	}
	@Override
	public BaseDialog setOnClickView(View view, ClickListener listener) {
		return super.setOnClickView(view,listener);
	}
	@Override
	public BaseDialog setViewText(int viewId, String text) {
		return super.setViewText(viewId, text);
	}
	public MyDialog showCancle(String msg) {
		if (!isShowing()) {
			setContentView(R.layout.dialog_msg_one);
			AutoUtils.auto(findViewById(R.id.ll_dialog_root));
			//ViewUtil.scaleContentView((LinearLayout)findViewById(R.id.ll_dialog_root));
			TextView tv_msg = findView(R.id.tv_dialog_msg);
			tv_msg.setText(msg);
			Button btn =findView(btn_mid);
			setOnClickView(btn);
			if (!((Activity)ctx).isFinishing())
				show();
		}
		return this;
	}
	public MyDialog showCancle(String msg, ClickListener listener) {
		if (!isShowing()) {
			setContentView(R.layout.dialog_msg_one);
			AutoUtils.auto(findViewById(R.id.ll_dialog_root));
			//ViewUtil.scaleContentView((LinearLayout)findViewById(R.id.ll_dialog_root));
			TextView tv_msg = findView(R.id.tv_dialog_msg);
			tv_msg.setText(msg);
			Button btn =findView(btn_mid);
			setOnClickView(btn,listener);
			if (!((Activity)ctx).isFinishing())
				show();
		}
		return this;
	}
	public MyDialog showCancleAndSure(String msg, String cancleBtnText, String sureBtnText, ClickListener listener) {
		return showCancleAndSure(null,msg,cancleBtnText,sureBtnText,listener);
	}
	public MyDialog showCancleAndSure(String title, String msg, String cancleBtnText, String sureBtnText, final ClickListener listener) {
		if (!isShowing()) {
			setContentView(R.layout.dialog_msg_two);
			AutoUtils.auto(findViewById(R.id.ll_dialog_root));
			//ViewUtil.scaleContentView((LinearLayout)findViewById(R.id.ll_dialog_root));
			if (title!=null){
				TextView tv_title = findView(R.id.tv_dialog_title);
				tv_title.setText(title);
			}
			TextView tv_msg = findView(R.id.tv_dialog_msg);
			tv_msg.setText(msg);
			Button bt_cancle = findView(R.id.btn_cancle);
			Button bt_sure = findView(R.id.btn_sure);
			bt_cancle.setText(cancleBtnText);
			setOnClickView(bt_cancle);
			bt_sure.setText(sureBtnText);
			setOnClickView(bt_sure,listener);
			if (!((Activity)ctx).isFinishing())
			  show();
		}
		return this;
	}


	public MyDialog showExit(ClickListener listener) {
		showCancleAndSure("退出提示", "是否要离开了呢？", "才不", "是滴", listener);
		return this;
	}


    TextView tipTextView;
    @Override
	public BaseDialog showLoading(String msg) {
		// LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		if (!isShowing()) {
			setContentView(R.layout.dialog_loading_material);
			if (msg != null) {
				tipTextView = findView(R.id.tipTextView);// 提示文字
				tipTextView.setText(msg);// 设置加载信息
			}
			show();
		}
		return this;
	}
    public void setLoadingProgress(String msg){
        if (tipTextView!=null){
            tipTextView.setText(msg);// 设置加载信息
        }
    }
	@Override
	public BaseDialog showNoNetWork() {
		return showCancle("还没有连接网络哦");
	}
}
