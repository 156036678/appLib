/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiay.applib.request;

import com.google.gson.Gson;
import com.nohttp.Headers;
import com.nohttp.RequestMethod;
import com.nohttp.rest.RestRequest;
import com.xiay.applib.bean.BaseBean;

import org.json.JSONObject;

import cn.xiay.util.log.Log;

public class BeanReqeust<T> extends RestRequest<T> {

	private Class<T> clazz;

	public BeanReqeust(String url, RequestMethod requestMethod, Class<T> clazz) {
		super(url, requestMethod);
		this.clazz = clazz;
	}
	public BeanReqeust(String url, RequestMethod requestMethod) {
		super(url, requestMethod);
	}
	public BeanReqeust(String url, Class<T> clazz) {
		this(url, RequestMethod.POST, clazz);
	}
	public BeanReqeust( Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public T parseResponse( Headers responseHeaders, byte[] responseBody) {
		String res = parseResponseString( responseHeaders, responseBody).trim();
		if (Log.isPrint)
	    	super.printLog(res);
		try {
			return new Gson().fromJson(res,clazz);
		} catch (Exception e) {
			Log.e(e);
			T obj= null;
			try {
				obj = clazz.newInstance();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				JSONObject jObj=new JSONObject(res);
				int status=jObj.getInt("status");
				if(status==1){//数据获取成功，但是解析失败
					((BaseBean)obj).status=0;
					((BaseBean)obj).msg="解析数据失败";
				}else {
					((BaseBean)obj).msg=jObj.getString("msg");
				}
				return obj;
			} catch (Exception e1) {
				Log.e(e1);
				((BaseBean)obj).msg="解析数据失败(1)";
				return obj;
			}
		}
	}


}
