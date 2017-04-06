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
public abstract   class OnItemClickListener<ADT,AD extends RecyclerBaseAdapter<ADT>> implements RecyclerBaseAdapter.OnItemClickListener {
    @Override
    public void onItemClick(RecyclerBaseAdapter adapter, View view, int position) {
        onItemClick((AD)adapter,view,(ADT)adapter.getItem(position),position);
    }

    public abstract void onItemClick(AD adapter, View view, ADT item,int position) ;

}
