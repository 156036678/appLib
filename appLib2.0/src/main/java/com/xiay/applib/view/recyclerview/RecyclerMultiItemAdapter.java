package com.xiay.applib.view.recyclerview;

import android.util.SparseArray;
import android.view.ViewGroup;

import com.xiay.applib.view.recyclerview.entity.MultiItemEntity;

import java.util.List;


public abstract class RecyclerMultiItemAdapter<T extends MultiItemEntity> extends RecyclerBaseAdapter<T> {

    /**
     * layouts indexed with their types
     */
    private SparseArray<Integer> layouts;


    public RecyclerMultiItemAdapter() {
        super();
    }
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data    A new list is created out of this one to avoid mutable list
     */
    public RecyclerMultiItemAdapter(List<T> data) {
        super( data);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return (mData.get(position)).getItemType();
    }


    @Override
    protected RecyclerViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createRecyclerViewHolder(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        return layouts.get(viewType);
    }

    protected void addItemType(int type, int layoutResId) {
        if (layouts == null) {
            layouts = new SparseArray<>();
        }
        layouts.put(type, layoutResId);
    }



    protected abstract void convert(RecyclerViewHolder helper, T item);


}


