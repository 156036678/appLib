package com.xiay.applib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xiay.applib.util.StrUtil;

public class NewlineByPostionTextView extends TextView {
    public NewlineByPostionTextView(Context context) {
        super(context);
    }
    public NewlineByPostionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *
     * @param text
     * @param lineLength  一行显示的文字长度
     */
    public void setMyText(CharSequence text, int lineLength) {
        int rowNum=text.length()/lineLength;
        if (rowNum>0){
            for (int i = 1; i <rowNum+1 ; i++) {
                text= StrUtil.insertStringAtPosition(text,"\n",lineLength*i-1);
            }
        }
        super.setText(text, BufferType.NORMAL);
    }
}
