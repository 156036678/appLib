package com.xiay.applib.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.xiay.applib.R;
import com.xiay.applib.view.ProgressWheel;

/**
 * @author xiay
 */

public class MyProgressDialog extends Dialog {
	Context ctx;
	ProgressWheel progressWheel;
	String progressMsg;
	public MyProgressDialog(Context context) {
		super(context, R.style.Dialog);
		ctx=context;
		progressMsg="已上传";
	}
	public MyProgressDialog(Context context,String progressMsg) {
		super(context, R.style.Dialog);
		ctx=context;
		this.progressMsg=progressMsg;
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


    TextView tipTextView;
	public MyProgressDialog showLoading(String msg) {
		if (!isShowing()) {
			setContentView(R.layout.dialog_progress_material);
			if (msg != null) {
				tipTextView = (TextView) findViewById(R.id.tipTextView);// 提示文字
				progressWheel = (ProgressWheel) findViewById(R.id.pb_progress);// 提示文字
				progressWheel.setProgress(0);
				tipTextView.setText(msg);// 设置加载信息
				progressWheel.setCallback(new ProgressWheel.ProgressCallback() {
					@Override
					public void onProgressUpdate(float progress) {
						tipTextView.setText(progressMsg+"已上传"+((int)(progress*100))+"%");
					}
				});
			}
			show();
		}
		return this;
	}
    public void setProgress(float progress){
        if (tipTextView!=null){
			progressWheel.setProgress(progress);// 设置加载信息
        }
    }
}
