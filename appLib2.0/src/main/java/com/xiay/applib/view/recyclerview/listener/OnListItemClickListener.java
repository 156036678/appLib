package com.xiay.applib.view.recyclerview.listener;

import android.view.View;

/**
 * create by: allen on 16/8/3.
 */

public interface OnListItemClickListener<ADT,AD> {

    public void onItemClick(AD adapter, View view, ADT item,int position) ;
    public void onItemLongClick(AD adapter, View view, ADT item,int position) ;
    public void onItemChildClick(AD adapter, View view,ADT item, int position) ;
    public void onItemChildLongClick(AD adapter, View view,ADT item, int position) ;

}
