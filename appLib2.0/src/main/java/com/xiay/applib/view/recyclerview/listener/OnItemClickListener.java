package com.xiay.applib.view.recyclerview.listener;

import android.view.View;

import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;

/**
 * Created by AllenCoder on 2016/8/03.
 *
 *
 * A convenience class to extend when you only want to OnItemClickListener for a subset
 * of all the SimpleClickListener. This implements all methods in the
 * {@link SimpleClickListener}
 */
public abstract   class OnItemClickListener<T extends RecyclerBaseAdapter> extends SimpleClickListener {


    @Override
    public void onItemClick(RecyclerBaseAdapter adapter, View view, int position) {
        itemClick((T)adapter,view,position);
    }

    @Override
    public void onItemLongClick(RecyclerBaseAdapter adapter, View view, int position) {
        itemLongClick((T)adapter,view,position);
    }

    @Override
    public void onItemChildClick(RecyclerBaseAdapter adapter, View view, int position) {
        itemChildClick((T)adapter,view,position);
    }

    @Override
    public void onItemChildLongClick(RecyclerBaseAdapter adapter, View view, int position) {

    }
    public abstract void itemClick(T adapter, View view, int position);
    public  void itemChildClick(T adapter, View view, int position){};
    public  void itemLongClick(T adapter, View view, int position){}
}
