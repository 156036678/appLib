package com.xiay.applib.view.recyclerview.listener;

import android.view.View;

import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;

/**
 * Created by AllenCoder on 2016/8/03.
 * A convenience class to extend when you only want to OnItemChildLongClickListener for a subset
 * of all the SimpleClickListener. This implements all methods in the
 * {@link SimpleClickListener}
 **/
public abstract class OnItemChildLongClickListener extends SimpleClickListener {


    @Override
    public void onItemClick(RecyclerBaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(RecyclerBaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(RecyclerBaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(RecyclerBaseAdapter adapter, View view, int position) {
        SimpleOnItemChildLongClick(adapter,view,position);
    }
    public abstract void SimpleOnItemChildLongClick(RecyclerBaseAdapter adapter, View view, int position);
}
