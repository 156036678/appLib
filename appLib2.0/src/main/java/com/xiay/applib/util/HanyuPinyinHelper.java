package com.xiay.applib.util;
import android.content.Context;

import com.xiay.applib.bean.SortItem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 中文转汉语拼音
 * 支持多音字
 */
public class HanyuPinyinHelper {

	private StringBuffer buffer = new StringBuffer();
	private List<String> list = new ArrayList<String>();
	private Properties p = new Properties();
	private boolean isSimple=false;

	public HanyuPinyinHelper(Context ctx) {
		try {
			p.load(new BufferedInputStream(ctx.getAssets().open("hanyu_pinyin.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String[] getHanyuPinyins(char c) {
		int codePointOfChar = c;
		String codepointHexStr = Integer.toHexString(codePointOfChar)
				.toUpperCase();
		String str = (String) p.get(codepointHexStr);
		return str.split(",");
	}

	/**
	 * @param str 需要转换的字符串
	 * @param isSimple true简拼，false全拼
	 * @return 该字符串转换后的所有组合
	 */
	public List<String> hanyuPinYinConvert(String str,boolean isSimple) {
		if (str == null || "".equals(str))
			return null;
		this.isSimple=isSimple;
		list.clear();
		buffer.delete(0, buffer.length());
		convert(0, str);
		return list;
	}


	/**
	 * @param str 需要转换的字符串
	 * @return 该字符串转换后的所有组合，包含全拼和简拼
	 */
	public List<String> hanyuPinYinConvert(String str) {
		if (str == null || "".equals(str))
			return null;
		list.clear();
		buffer.delete(0, buffer.length());
		this.isSimple=true;
		convert(0, str);
		buffer.delete(0, buffer.length());
		this.isSimple=false;
		convert(0, str);
		return list;
	}

	private void convert(int n, String str) {
		if (n == str.length()) {// 递归出口
			String temp=buffer.toString();
			if(!list.contains(temp)){
				list.add(buffer.toString());
			}
			return;
		} else {
			char c = str.charAt(n);
			if (0x3007 == c || (0x4E00 <= c && c <= 0x9FA5)) {// 如果该字符在中文UNICODE范围
				String[] arrayStrings = getHanyuPinyins(c);
				if (arrayStrings == null) {
					buffer.append(c);
					convert(n + 1, str);
				} else if (arrayStrings.length == 0) {
					buffer.append(c);
					convert(n + 1, str);
				} else if (arrayStrings.length == 1) {
					if(isSimple){
						if(!"".equals(arrayStrings[0])){
							buffer.append(arrayStrings[0].charAt(0));
						}
					}else{
						buffer.append(arrayStrings[0]);
					}
					convert(n + 1, str);
				} else {
					int len;
					for (int i = 0; i < arrayStrings.length; i++) {
						len = buffer.length();
						if(isSimple){
							if(!"".equals(arrayStrings[i])){
								buffer.append(arrayStrings[i].charAt(0));
							}
						}else{
							buffer.append(arrayStrings[i]);
						}
						convert(n + 1, str);
						buffer.delete(len, buffer.length());
					}
				}
			} else {// 非中文
				buffer.append(c);
				convert(n + 1, str);
			}
		}
	}

	/**设置汉字第一次字母*/
	public void setFirstLetter(List<SortItem> sortItems) {
		for (SortItem sortItem : sortItems) {
			//汉字转换成拼音
			List<String> list=hanyuPinYinConvert(sortItem.getName());
			String pinyin;
			String name=sortItem.getName();
			if (name.contains("重庆")||name.contains("长安")){
				pinyin = list.get(list.size()-1);
			}
			else {
				pinyin = list.get(0);
			}
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortItem.setFirstLetter(sortString.toUpperCase());
			}else{
				sortItem.setFirstLetter("#");
			}
		}
		Collections.sort(sortItems);
	}
}
