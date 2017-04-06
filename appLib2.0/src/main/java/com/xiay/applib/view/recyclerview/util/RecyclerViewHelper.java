package com.xiay.applib.view.recyclerview.util;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiay.applib.R;
import com.xiay.applib.view.VerticalSwipeRefreshLayout;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.WrapContentLinearLayoutManager;
import com.xiay.applib.view.recyclerview.listener.OnItemChildClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemChildLongClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemClickListener;
import com.xiay.applib.view.recyclerview.listener.OnItemLongClickListener;
import com.xiay.applib.view.recyclerview.recyclerviewflexibledivider.GridSpacingItemDecoration;
import com.xiay.applib.view.recyclerview.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import cn.xiay.util.autolayout.utils.AutoUtils;

import static com.nohttp.NoHttp.getContext;

/**
 * Created by Xiay on 2017/2/14.
 */

public class RecyclerViewHelper< ADT, AD extends RecyclerBaseAdapter<ADT>>  {
    int currentPage = 1;
    protected int perPageSize = 10;
    private RecyclerBaseAdapter adapter;
    /**
     * 是否显示没有更多数据View
     */
    private boolean isShowEnd = true;
    private ViewGroup emptyView;
    private RecyclerView rv_list;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int customEmptyViewLayout = R.layout.app_empty_view;

    /**
     * 设置自定义EmptyView
     *
     * @param customEmptyViewProvider
     */
    public void setCustomEmptyViewProvider(CustomEmptyViewProvider customEmptyViewProvider) {
        this.customEmptyViewLayout = customEmptyViewProvider.getCustomEmptyViewLayout();
    }

    public interface CustomEmptyViewProvider {
        int getCustomEmptyViewLayout();
    }

    public RecyclerViewHelper() {
    }

    /**
     * @param rv_list                 RecyclerView
     * @param adapter                 RecyclerBaseAdapter
     * @param perPageSize             每页显示多少条数据
     * @param emptyMessage            没有数据的时候显示的文字消息
     */
    public RecyclerViewHelper(RecyclerView rv_list, RecyclerBaseAdapter<ADT> adapter, int perPageSize, String emptyMessage) {
        this.perPageSize = perPageSize;
        this.adapter = adapter;
        this.rv_list = rv_list;
        rv_list.setLayoutManager(new WrapContentLinearLayoutManager(rv_list.getContext(), LinearLayoutManager.VERTICAL, false));
        //      添加动画
        rv_list.setItemAnimator(new DefaultItemAnimator());
        this.adapter = adapter;
        rv_list.setAdapter(adapter);
        emptyView = getEmptyView(emptyMessage);
    }


    public RecyclerViewHelper(RecyclerView rv_list, RecyclerBaseAdapter<ADT> adapter) {
        this(rv_list, adapter, 10, null);
    }

    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        rv_list.addItemDecoration(itemDecoration);
    }

    /**
     * @param recyclerView            RecyclerView
     * @param adapter                 RecyclerBaseAdapter
     * @param emptyMessage            没有数据的时候显示的文字消息
     */
    public void setRecyclerView(RecyclerView recyclerView,AD adapter, String emptyMessage) {
        this.rv_list = recyclerView;
        rv_list.setLayoutManager(new WrapContentLinearLayoutManager(rv_list.getContext(), LinearLayoutManager.VERTICAL, false));
        //      添加动画
        rv_list.setItemAnimator(new DefaultItemAnimator());
        emptyView = getEmptyView(emptyMessage);
        this.adapter = adapter;
        rv_list.setAdapter(adapter);
    }

    public void setShowEnd(boolean showEnd) {
        isShowEnd = showEnd;
    }


    /**
     * 设置下拉刷新监听
     *
     * @param swipeRefreshLayout
     * @param onRefreshListener
     */
    public void setOnRefreshListener(View swipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        if (swipeRefreshLayout instanceof VerticalSwipeRefreshLayout)
            mSwipeRefreshLayout = (VerticalSwipeRefreshLayout) swipeRefreshLayout;
        else if (swipeRefreshLayout instanceof SwipeRefreshLayout)
            mSwipeRefreshLayout = (SwipeRefreshLayout) swipeRefreshLayout;
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.yellow);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    /**
     * @param dividerHeight -2 使用默认高度和颜色
     * @param dividerColor  -1 使用透明颜色
     * @return
     */
    public RecyclerView.ItemDecoration getItemDecoration(int dividerHeight, int dividerColor) {
        RecyclerView.ItemDecoration itemDecoration = null;
        if (dividerHeight != -2) {//如果不是默认
            if (dividerColor == -1)
                dividerColor = R.color.transparent;
            if (dividerHeight != -1)
                itemDecoration = new HorizontalDividerItemDecoration.Builder(rv_list.getContext()).size(AutoUtils.getPercentHeightSize(dividerHeight)).color(rv_list.getContext().getResources().getColor(dividerColor)).build();
        } else {
            itemDecoration = new HorizontalDividerItemDecoration.Builder(getContext()).size(AutoUtils.getPercentHeightSize(1)).color(rv_list.getContext().getResources().getColor(R.color.gray_listLine)).build();
        }
        return itemDecoration;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return getItemDecoration(-2, -1);
    }

    /**
     * 设置表格布局横向item个数
     *
     * @param count
     */
    public void setGridLayoutCount(int count) {
        rv_list.setLayoutManager(new GridLayoutManager(rv_list.getContext(), count));
        rv_list.addItemDecoration(new GridSpacingItemDecoration(count, 10, false));
    }

    /***
     * 设置当前页数
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setListData(List<ADT> newData) {
        setListData(newData, null,true);
    }
    public void setListData(List<ADT> newData, boolean isShowEmptyView) {
        setListData(newData, null, isShowEmptyView);
    }
    public void setListData(List<ADT> newData,  String noDataText) {
        setListData(newData, noDataText, true);
    }

    public void setListData(List<ADT> newData, String noDataText, boolean isShowEmptyView) {
        if (newData == null) {
            if (mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
            if (isShowEmptyView)
                addEmptyView(noDataText);
            adapter.getData().clear();
            adapter.notifyDataSetChanged();
            adapter.removeAllFooterView();
            return;
        }
        if (currentPage == 1) {
            if (newData.size() == 0) {
                if (isShowEmptyView)
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
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    public void addEmptyView(String emptyText) {
        if (emptyText != null)
            adapter.setEmptyView(emptyView);

        else {
            addEmptyView();
        }
    }

    public void addEmptyView() {
        adapter.setEmptyView(emptyView);
    }
    public RecyclerBaseAdapter<ADT> getAdapter() {
       return adapter;
    }
    public void setAdapter(RecyclerBaseAdapter adapter) {
      this.adapter=adapter;
    }

    public void toEnd() {
        if (adapter != null) {
            adapter.loadMoreEnd(isShowEnd);
        }
    }

    protected ViewGroup getEmptyView(String text) {
        if (emptyView == null) {
            emptyView = (ViewGroup) LayoutInflater.from(rv_list.getContext()).inflate(customEmptyViewLayout, (ViewGroup) rv_list.getParent(), false);
            if (text != null) {
                View v_emptyText = emptyView.findViewById(R.id.tv_empty);
                if (v_emptyText != null && v_emptyText instanceof TextView)
                    ((TextView) v_emptyText).setText(text);
            }
            AutoUtils.auto(emptyView);
        }
        return emptyView;
    }

    public void onFailed() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
        if (adapter != null)
            adapter.loadMoreFail();
    }

    public void setLoadMoreEnable(boolean isShowEnd, RecyclerBaseAdapter.RequestLoadMoreListener requestLoadMoreListener) {
        this.isShowEnd = isShowEnd;
        adapter.setOnLoadMoreListener(requestLoadMoreListener);
    }

    public void addHeaderAndEmptyView(View header, String emptyText) {
        adapter.removeAllHeaderView();
        adapter.addHeaderView(header);
        adapter.setHeaderAndEmpty(true);
        if (emptyText != null)
            adapter.setEmptyView(getEmptyView(emptyText));

    }

    public void addHeaderView(View header) {
        adapter.removeAllHeaderView();
        adapter.addHeaderView(header);
        adapter.setHeaderAndEmpty(true);
    }

    public void setPerPageSize(int perPageSize) {
        this.perPageSize = perPageSize;
    }
   public void setOnItemClickListener(final OnItemClickListener<ADT,AD> listener){
       adapter.setOnItemClickListener(new RecyclerBaseAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(RecyclerBaseAdapter adapter, View view, int position) {
               listener.onItemClick((AD) adapter,view,(ADT) adapter.getItem(position),position);
           }
       });
   }
   public void setOnItemLongClickListener(final OnItemLongClickListener<ADT,AD> listener){
       adapter.setOnItemLongClickListener(new RecyclerBaseAdapter.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(RecyclerBaseAdapter adapter, View view, int position) {
               return  listener.onItemLongClick((AD) adapter,view,(ADT) adapter.getItem(position),position);
           }
       });
   }
   public void setOnItemChildClickListener(final OnItemChildClickListener<ADT,AD> listener){
       adapter.setOnItemChildClickListener(new RecyclerBaseAdapter.OnItemChildClickListener() {
           @Override
           public boolean onItemChildClick(RecyclerBaseAdapter adapter, View view, int position) {
               return  listener.onItemChildClick((AD) adapter,view,(ADT) adapter.getItem(position),position);
           }
       });
   }
   public void setOnItemChildLongClickListener(final OnItemChildLongClickListener<ADT,AD> listener){
       adapter.setOnItemChildLongClickListener(new RecyclerBaseAdapter.OnItemChildLongClickListener() {
           @Override
           public void onItemChildLongClick(RecyclerBaseAdapter adapter, View view, int position) {
               listener.onItemChildLongClick((AD) adapter,view,(ADT) adapter.getItem(position),position);
           }

       });
   }

}
