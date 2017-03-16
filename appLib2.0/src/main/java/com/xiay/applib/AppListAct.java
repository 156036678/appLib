package com.xiay.applib;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nohttp.extra.HttpListener;
import com.nohttp.rest.Response;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.listener.OnListItemClickListener;
import com.xiay.applib.view.recyclerview.util.RecyclerViewHelper;

import java.util.List;

import cn.xiay.ui.Toast;

/***
 * @param <RQ>请求数据类型
 * @param <ADT>       Adapter 数据类型
 * @param <AD>Adapter 类型
 * @author Xiay
 */
public abstract class AppListAct<RQ, ADT, AD extends RecyclerBaseAdapter<ADT>> extends AppActivity implements  OnListItemClickListener<ADT, AD>, SwipeRefreshLayout.OnRefreshListener, RecyclerBaseAdapter.RequestLoadMoreListener, HttpListener<RQ> {
    public RecyclerView rv_list;
    public AD adapter;
    protected int currentPage = 1;
    private RecyclerViewHelper recyclerViewHelper;
    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        isAutoShowNoNetwork = false;
    }

    public void initListViewWithLine(AD adapter, String emptyMessage) {
        initListView(-2, adapter, emptyMessage);
    }

    public void initListView(int dividerHeight, AD adapter, String emptyMessage) {
        initListView(dividerHeight, -1, adapter, emptyMessage);
    }

    /**
     * 初始化列表
     *
     * @param dividerHeight 水平分割线高度
     * @param dividerColor  水平分割线颜色
     * @param adapter
     * @param emptyMessage  adapter为空的时候提示文字
     */
    public void initListView(int dividerHeight, int dividerColor, AD adapter, String emptyMessage) {
        initView(adapter, emptyMessage);
        recyclerViewHelper.setItemDecoration(recyclerViewHelper.getItemDecoration(dividerHeight,dividerColor));
    }
    /**
     * 初始化列表
     *
     * @param itemDecoration 分割线样式
     * @param adapter
     * @param emptyMessage   adapter为空的时候提示文字
     */
    public void initListView(RecyclerView.ItemDecoration itemDecoration, AD adapter, String emptyMessage) {
        initView(adapter, emptyMessage);
        recyclerViewHelper.setItemDecoration(itemDecoration);
    }
    private void initView(AD adapter, String emptyMessage) {
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        this.adapter=adapter;
        View swipeLayout=findViewById(R.id.swipeLayout);
        recyclerViewHelper = new RecyclerViewHelper(this, rv_list, adapter,this, emptyMessage);
        if (swipeLayout!=null)
            recyclerViewHelper.setOnRefreshListener(swipeLayout,this);
    }
    /**
     * 设置表格布局横向item个数
     *
     * @param count
     */
    public void setGridLayoutCount(int count) {
        if (recyclerViewHelper ==null){
            Toast.show("请先调用initListView方法");
            return;
        }
        recyclerViewHelper.setGridLayoutCount(count);
    }

    public void setLoadMoreEnable() {
        setLoadMoreEnable(true);
    }

    public void setLoadMoreEnable(boolean isShowEnd) {
        recyclerViewHelper.setLoadMoreEnable(isShowEnd, this);
    }



    @Override
    public void onRefresh() {
        currentPage = 1;
        getListData();
        adapter.removeAllFooterView();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        getListData();
    }

    protected void addHeaderView(View header) {
        if (recyclerViewHelper ==null){
            Toast.show("请先调用initListView方法");
            return;
        }
        recyclerViewHelper.addHeaderView(header);
    }

    protected void addEmptyView(String emptyText) {
        if (recyclerViewHelper ==null){
            Toast.show("请先调用initListView方法");
            return;
        }
        recyclerViewHelper.addEmptyView(emptyText);
    }

    protected void addHeaderAndEmptyView(View header, String emptyText) {
        if (recyclerViewHelper ==null){
            Toast.show("请先调用initListView方法");
            return;
        }
        recyclerViewHelper.addHeaderAndEmptyView(header, emptyText);

    }

    protected void setListData(List<ADT> newData) {
        setListData(newData, null);
    }

    protected void setListData(List<ADT> newData, String noDataText) {
        if (recyclerViewHelper ==null){
            Toast.show("请先调用initListView方法");
            return;
        }
        recyclerViewHelper.setCurrentPage(currentPage);
        recyclerViewHelper.setListData(newData, noDataText);
    }

    @Override
    public void onFailed(int i, Response response) {
        recyclerViewHelper.onFailed();
    }

    /**
     * 设置每页加载多少条数据
     */
    protected void setPerPageSize(int perPageSize) {
        recyclerViewHelper.setPerPageSize(perPageSize);
    }

    public abstract void getListData();
}
