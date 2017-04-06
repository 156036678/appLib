package com.xiay.applib;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nohttp.extra.HttpListener;
import com.nohttp.rest.Response;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.listener.OnItemChildClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemChildLongClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemLongClickListener;
import com.xiay.applib.view.recyclerview.util.RecyclerViewHelper;

import java.util.List;

import cn.xiay.ui.Toast;
import cn.xiay.util.autolayout.utils.AutoUtils;

/***
 *@author Xiay
 * @param <RQ>请求数据类型
 * @param <ADT> Adapter 数据类型
 * @param <AD>Adapter 类型
 */
public abstract class AppListFragment<RQ,ADT,AD extends RecyclerBaseAdapter<ADT>> extends AppFragment  implements  SwipeRefreshLayout.OnRefreshListener, RecyclerBaseAdapter.RequestLoadMoreListener, HttpListener<RQ> {
	private ViewGroup emptyView;
	public RecyclerView rv_list;
	public AD adapter;
	protected int currentPage = 1;
	private RecyclerViewHelper recyclerViewHelper;
	public boolean isShowEnd = true;
	View viewRoot;
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity.isAutoShowNoNetwork=false;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recyclerViewHelper = new RecyclerViewHelper();
	}

	public RecyclerViewHelper getRecyclerViewHelper() {
		return recyclerViewHelper;
	}

	public void initListViewWithLine(View v, AD adapter, String emptyMessage) {
		initListView(v,-2,adapter,emptyMessage);
	}
	public void initListView(View v,int dividerHeight,AD adapter,String emptyMessage) {
		initListView(v,dividerHeight,-1,adapter,emptyMessage);
	}
	/**
	 *  初始化列表
	 * @param dividerHeight  水平分割线高度
	 * @param dividerColor  水平分割线颜色
	 * @param adapter
	 * @param emptyMessage   adapter为空的时候提示文字
	 */
	public void initListView(View v,int dividerHeight,int dividerColor,AD adapter,String emptyMessage) {
		initView(v,adapter,emptyMessage);
		recyclerViewHelper.setItemDecoration(recyclerViewHelper.getItemDecoration(dividerHeight,dividerColor));
	}
	/**
	 * 初始化列表
	 *
	 * @param itemDecoration 分割线样式
	 * @param adapter
	 * @param emptyMessage   adapter为空的时候提示文字
	 */
	public void initListView(View v,RecyclerView.ItemDecoration itemDecoration, AD adapter, String emptyMessage) {
		initView(v,adapter,emptyMessage);
		recyclerViewHelper.setItemDecoration(itemDecoration);
	}
	private void initView(View v,AD adapter, String emptyMessage) {
		viewRoot=v;
		this.adapter=adapter;
		rv_list = (RecyclerView) v.findViewById(R.id.rv_list);
		recyclerViewHelper.setRecyclerView(rv_list,adapter,emptyMessage);
		View swipeLayout=v.findViewById(R.id.swipeLayout);
		if (swipeLayout!=null)
			recyclerViewHelper.setOnRefreshListener(swipeLayout,this);
	}
	public void setOnItemClickListener(final OnItemClickListener<ADT,AD> listener){
		recyclerViewHelper.setOnItemClickListener(listener);
	}
	public void setOnItemLongClickListener(final OnItemLongClickListener<ADT,AD> listener){
		recyclerViewHelper.setOnItemLongClickListener(listener);
	}
	public void setOnItemChildClickListener(final OnItemChildClickListener<ADT,AD> listener){
		recyclerViewHelper.setOnItemChildClickListener(listener);
	}
	public void setOnItemChildLongClickListener(final OnItemChildLongClickListener<ADT,AD> listener){
		recyclerViewHelper.setOnItemChildLongClickListener(listener);
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
	public  void setLoadMoreEnable(){
		setLoadMoreEnable(true);
	}
	/**
	 * @param isShowEnd false 不显示没有更多数据view
     */
	public  void setLoadMoreEnable(boolean isShowEnd){
		this.isShowEnd=isShowEnd;
		adapter.setOnLoadMoreListener(this);
	}
	/**
	 * @param adapter
	 * @param emptyMessage  当列表为空时候显示的信息
	 */
	public  void setAdapter(RecyclerBaseAdapter adapter,String emptyMessage){
		this.adapter=(AD)adapter;
		adapter.setEmptyView(getEmptyView(emptyMessage));
		rv_list.setAdapter(adapter);
	}
	protected ViewGroup getEmptyView(String text) {
		if (emptyView == null) {
			emptyView = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.app_empty_view, (ViewGroup) rv_list.getParent(), false);
			((TextView) emptyView.findViewById(R.id.tv_empty)).setText(text);
			//ViewUtil.scaleContentView(emptyView);
			AutoUtils.auto(emptyView);
		}
		return emptyView;
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
		recyclerViewHelper.addHeaderAndEmptyView(header,emptyText);

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
		recyclerViewHelper.setListData(newData,noDataText);
	}
	@Override
	public void onFailed(int i, Response response) {
		recyclerViewHelper.onFailed();
	}
	/**设置每页加载多少条数据*/
	protected  void  setPerPageSize(int perPageSize){
		recyclerViewHelper.setPerPageSize(perPageSize);
	}
	public abstract void getListData();
}
