package com.xiay.applib.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiay.applib.R;
import com.xiay.applib.view.VerticalSwipeRefreshLayout;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.util.RecyclerViewHelper;

import java.util.List;

/**
 * Created by Xiay on 2017/4/3.
 */

public class AppListFragment  extends Fragment  {
    RecyclerView rv_list;
    RecyclerViewHelper helper;
    VerticalSwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_refehsh_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_list= (RecyclerView) view.findViewById(R.id.rv_list);
        swipeRefreshLayout= (VerticalSwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        helper=new RecyclerViewHelper();
    }
    public void setAdapter(RecyclerBaseAdapter adapter){
        helper.setAdapter(adapter);
    }
    public void setListData(List newData){
        helper.setListData(newData);
    }
    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration){
        helper.setItemDecoration(itemDecoration);
    }

}
