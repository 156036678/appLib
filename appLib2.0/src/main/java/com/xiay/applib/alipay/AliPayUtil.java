package com.xiay.applib.alipay;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.xiay.applib.AppActivity;
import com.xiay.applib.ui.dialog.MyToast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import cn.xiay.ui.Toast;
import cn.xiay.util.SPUtil;

public abstract class AliPayUtil {
	private  final int SDK_PAY_FLAG = 1;
	private AppActivity act;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);

					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();

					String resultStatus = payResult.getResultStatus();

					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						//Toast.show("支付成功");
						onSuccess();
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.show("支付结果确认中");
						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							MyToast.showError("您已取消支付");
							onError();
						}
					}
					break;
				}
				default:
					break;
			}
		}
	};
	public  abstract void onSuccess();
	public  abstract void onError();
	public AliPayUtil(AppActivity act) {
		this.act = act;
	}

	/**
	 * 调用SDK支付
	 * @param orderId 订单id(必传)
	 * @param title 订单信息(必传)
	 * @param content 订单描述(必传)
	 * @param money 支付金额(必传)
	 * @param payNotify_url 服务器回调地址(必传)
	 */
	public void pay(String orderId,String title,String content,String money,String payNotify_url) {
		if (content==null||content.trim().length()==0){
			act.getDialog().showCancle("订单描述不能为空");
			return;
		}
		// 订单
		String orderInfo = getOrderInfo(orderId,title, content, money,payNotify_url);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(act);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}


	/**
	 * create the order info. 创建订单信息
	 *
	 */
	private String getOrderInfo(String orderId,String subject, String body, String price,String payNotify_url) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + SPUtil.getString("AliPayPID","2088512921659499") + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SPUtil.getString("AliPaySeller","187279191@qq.com") + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" +payNotify_url
				+ "\"";
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 *
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 *
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content,SPUtil.getString("AliPayPrivateKey","MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMJOymx50cj93gN9\n" +
				"cujMzUBGF6bp2RBxsdsa+efNGTc4PjupLvXrarDh+V0CtIQrOb4lznFN54AFZ2Nf\n" +
				"jy9KcmvXoEM9ML6hNaTXwa8ZG9JHhH8FgERoTu4/DYTLTlsQeBvTZBl3TLiwLPyl\n" +
				"5xiDdZ+7xrYvAYp3/s21fOGhHXHHAgMBAAECgYA7kF+pJSbUEE6Qj1I8XxvESjhR\n" +
				"6Hmr+s5ktj5JeqvyK4GYVGKa7FHGa18/zeZ8ZavLyFcikJkYu49X/SEthArSzvAB\n" +
				"bTbG5ISMTQdfqHAiwtt+m5nOG6kp0dqdFjV74bu5r1kWFtzWlr5IdnhAb05bCttM\n" +
				"LZlMd7SWUjhGNszdwQJBAPTCYqo9dTFG1nnb9TRdLpbxWn06l4gNqCWF9IhlFnV8\n" +
				"/IjUla20PHlzBIVRRh+eP+UiYXl3QC/HPDyRV+rKaNcCQQDLO0A0crzReOaOyEgn\n" +
				"q/gIkoMrus88rIxTBNZcaivri4eJupeXGPAtY0C5sSFs9HligEGIYwq8AkcypkNo\n" +
				"9nCRAkAmH6D+o1P9uOrvUDOBVIJNQIq3tsijiH0IWzUbiaNV6YHsTjCCPfCtehza\n" +
				"Jy6k/iE1r4U/RjPZPU9En97x01VBAkAope0tkFVbwEa9ACoOZULy0/sQYAjbJdfb\n" +
				"Mvh8+29VaU7uqTdwrTg8m8FyYo9A75Tnsqdo1AZtnDcJVl/dOo1BAkEAwmuPDlWo\n" +
				"khKRIG6GuJMLvUMK+W8NLhxe4NjJ5WbeefHbYOrxrOoZUIJCKlfTAXIBvTU309Ng\n" +
				"UtmFDPMDp303Vg=="));
	}

	/**
	 * get the sign type we use. 获取签名方式
	 *
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
