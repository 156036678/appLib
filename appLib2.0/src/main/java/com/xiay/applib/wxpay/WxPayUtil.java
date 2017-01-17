package com.xiay.applib.wxpay;

import com.nohttp.extra.AbHttpActivity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import org.json.JSONException;
import org.json.JSONObject;
import cn.xiay.ui.Toast;
import cn.xiay.util.SPUtil;

public class WxPayUtil {
	private IWXAPI api;
	AbHttpActivity act;
	PayReq req;
	/**
	 * @param act
	 * @param jObj  服务端返回的json对象
     */
	public WxPayUtil(AbHttpActivity act,JSONObject jObj){
		try {
			String appId=SPUtil.getString("WxPayAppId");
			if (appId==null){
				Toast.show("请先配置微信AppId");
				return;
			}
			api = WXAPIFactory.createWXAPI(act,appId );
			this.act=act;
			PayReq req = new PayReq();
			req.appId = jObj.getString("appid");
			req.partnerId = jObj.getString("partnerid");
			req.prepayId = jObj.getString("prepayid");
			req.nonceStr = jObj.getString("noncestr");
			req.timeStamp = jObj.getString("timestamp");
			req.packageValue = jObj.getString("package");
			req.sign = jObj.getString("sign");
			req.extData = "app data"; // optional
			this.req=req;
			pay();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public  void  pay(){
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		if (!isPaySupported){
			Toast.show("未安装微信或微信版本过低,请先下载安装");
			return;
		}
		api.sendReq(req);
	}
}
