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
public abstract   class OnItemLongClickListener<ADT,AD extends RecyclerBaseAdapter<ADT>> implements RecyclerBaseAdapter.OnItemLongClickListener {
    @Override
    public boolean onItemLongClick(RecyclerBaseAdapter adapter, View view, int position) {
       return onItemLongClick((AD)adapter,view,(ADT)adapter.getItem(position),position);
    }

    public abstract boolean onItemLongClick(AD adapter, View view, ADT item,int position) ;

}
