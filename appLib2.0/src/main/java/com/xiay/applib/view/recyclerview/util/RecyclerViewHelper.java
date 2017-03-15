package com.xiay.applib.view.recyclerview.util;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiay.applib.R;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;

import java.util.List;

import cn.xiay.util.ViewUtil;

/**
 * Created by Xiay on 2017/2/14.
 */

public class RecyclerViewHelper<ADT> {
    int currentPage;
    protected int perPageSize;
    public RecyclerBaseAdapter adapter;
    /**是否显示没有更多数据View*/
    public boolean isShowEnd = true;
    private String emptyMessage;
    private ViewGroup emptyView;
    Activity activity;
    public RecyclerView rv_list;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, int perPageSize, String emptyMessage) {
        this.perPageSize = perPageSize;
        this.adapter = adapter;
        this.rv_list = rv_list;
        this.activity = activity;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
        this.emptyMessage = emptyMessage;
        if (emptyMessage!=null) {
            emptyView=getEmptyView(emptyMessage);
        }
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, int currentPage, int perPageSize) {
        this(activity,rv_list,adapter,swipeRefreshLayout,perPageSize,null);
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, int currentPage) {
        this(activity,rv_list,adapter,swipeRefreshLayout,10,null);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setListData(List<ADT> newData, String noDataText) {
        if (newData == null) {
            if (mSwipeRefreshLayout!=null)
                mSwipeRefreshLayout.setRefreshing(false);
            addEmptyView(noDataText);
            return;
        }
        if (currentPage == 1) {
            if (newData.size() == 0) {
                addEmptyView(noDataText);
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
                adapter.removeAllFooterView();
            } else if (newData.size() < perPageSize) {
                adapter.setNewData(newData);
                toEnd();
            } else {
                adapter.setNewData(newData);
            }
        } else {
            if (newData.size() < perPageSize) {
                if (newData.size() > 0)
                    adapter.addData(newData);
                toEnd();
            } else {
                adapter.addData(newData);
                adapter.loadMoreComplete();
            }
        }
        if (mSwipeRefreshLayout!=null)
            mSwipeRefreshLayout.setRefreshing(false);
    }
    public void addEmptyView(String emptyText) {
        if (emptyText != null)
            adapter.setEmptyView(emptyView);
    }
    public void toEnd() {
        if (adapter != null) {
            adapter.loadMoreEnd(isShowEnd);
        }
    }
    protected ViewGroup getEmptyView(String text) {
        if (emptyView == null) {
            emptyView = (ViewGroup)activity.getLayoutInflater().inflate(R.layout.app_empty_view, (ViewGroup) rv_list.getParent(), false);
            ((TextView) emptyView.findViewById(R.id.tv_empty)).setText(text);
            ViewUtil.scaleContentView(emptyView);
        }
        return emptyView;
    }
    public void onFailed() {
        if (mSwipeRefreshLayout!=null)
            mSwipeRefreshLayout.setRefreshing(false);
        if (adapter!=null)
            adapter.loadMoreFail();
    }
    public  void setLoadMoreEnable(boolean isShowEnd,RecyclerBaseAdapter.RequestLoadMoreListener requestLoadMoreListener){
        this.isShowEnd=isShowEnd;
        adapter.setOnLoadMoreListener(requestLoadMoreListener);
    }
    public void addHeaderAndEmptyView(View header, String emptyText) {
        adapter.removeAllHeaderView();
        adapter.addHeaderView(header);
        adapter.setHeaderAndEmpty(true);
        if (emptyText!=null)
            adapter.setEmptyView(getEmptyView(emptyText));

    }
    public void addHeaderView(View header) {
        adapter.removeAllHeaderView();
        adapter.addHeaderView(header);
        adapter.setHeaderAndEmpty(true);
    }
    public  void  setPerPageSize(int perPageSize){
     this.perPageSize=perPageSize;
    }
}
