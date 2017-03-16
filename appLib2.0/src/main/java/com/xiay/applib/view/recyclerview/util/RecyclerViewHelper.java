package com.xiay.applib.view.recyclerview.util;

import android.app.Activity;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiay.applib.R;
import com.xiay.applib.view.VerticalSwipeRefreshLayout;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.RecyclerViewHolder;
import com.xiay.applib.view.recyclerview.WrapContentLinearLayoutManager;
import com.xiay.applib.view.recyclerview.listener.OnListItemClickListener;
import com.xiay.applib.view.recyclerview.recyclerviewflexibledivider.GridSpacingItemDecoration;
import com.xiay.applib.view.recyclerview.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.xiay.util.ViewUtil;

import static com.nohttp.NoHttp.getContext;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.EMPTY_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.FOOTER_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.HEADER_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.LOADING_VIEW;

/**
 * Created by Xiay on 2017/2/14.
 */

public  class RecyclerViewHelper<ADT> implements RecyclerView.OnItemTouchListener {
    int currentPage=1;
    protected int perPageSize;
    public RecyclerBaseAdapter adapter;
    /**是否显示没有更多数据View*/
    public boolean isShowEnd = true;
    private String emptyMessage;
    private ViewGroup emptyView;
    Activity activity;
    public RecyclerView rv_list;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GestureDetectorCompat mGestureDetector;
    private View mPressedView = null;
    private boolean mIsPrepressed = false;
    private boolean mIsShowPress = false;
    private Set<Integer> childClickViewIds;
    private Set<Integer> longClickViewIds;
    OnListItemClickListener onListItemClickListener;
    ;
    /**
     *
     * @param activity Activity
     * @param rv_list RecyclerView
     * @param adapter  RecyclerBaseAdapter
     * @param onListItemClickListener  list 点击事件监听类
     * @param perPageSize 每页显示多少条数据
     * @param emptyMessage 没有数据的时候显示的文字消息
     */
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, OnListItemClickListener onListItemClickListener, int perPageSize, String emptyMessage) {
        this.perPageSize = perPageSize;
        this.adapter = adapter;
        this.rv_list = rv_list;
        this.activity = activity;
        this.onListItemClickListener = onListItemClickListener;
        this.emptyMessage = emptyMessage;
        rv_list.setLayoutManager(new WrapContentLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        //      添加动画
        rv_list.setItemAnimator(new DefaultItemAnimator());
        this.adapter = adapter;
        rv_list.setAdapter(adapter);
        if (emptyMessage!=null) {
            emptyView=getEmptyView(emptyMessage);
        }
        if (onListItemClickListener!=null){
            rv_list.addOnItemTouchListener(this);
            mGestureDetector = new GestureDetectorCompat(rv_list.getContext(), new ItemTouchHelperGestureListener(rv_list));
        }
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, OnListItemClickListener onListItemClickListener,String emptyMessage) {
        this(activity,rv_list,adapter,onListItemClickListener,10,emptyMessage);
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, OnListItemClickListener onListItemClickListener, int perPageSize) {
        this(activity,rv_list,adapter,onListItemClickListener,perPageSize,null);
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter, OnListItemClickListener onListItemClickListener) {
        this(activity,rv_list,adapter,onListItemClickListener,10,null);
    }
    public RecyclerViewHelper(Activity activity, RecyclerView rv_list, RecyclerBaseAdapter adapter) {
        this(activity,rv_list,adapter,null,10,null);
    }
    public  void setItemDecoration(RecyclerView.ItemDecoration itemDecoration){
            rv_list.addItemDecoration(itemDecoration);
    }

    /**
     * 设置下拉刷新监听
     * @param swipeRefreshLayout
     * @param onRefreshListener
     */
    public void setOnRefreshListener(View swipeRefreshLayout, SwipeRefreshLayout.OnRefreshListener onRefreshListener){
            if (swipeRefreshLayout instanceof VerticalSwipeRefreshLayout)
                mSwipeRefreshLayout = (VerticalSwipeRefreshLayout) swipeRefreshLayout;
            else  if (swipeRefreshLayout instanceof SwipeRefreshLayout)
                mSwipeRefreshLayout = (SwipeRefreshLayout) swipeRefreshLayout;
            mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.yellow);
            mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    /**
     *
     * @param dividerHeight -2 使用默认高度和颜色
     * @param dividerColor -1 使用透明颜色
     * @return
     */
    public RecyclerView.ItemDecoration getItemDecoration(int dividerHeight, int dividerColor) {
        RecyclerView.ItemDecoration itemDecoration = null;
        if (dividerHeight != -2) {//如果不是默认
            if (dividerColor == -1)
                dividerColor = R.color.transparent;
            if (dividerHeight != -1)
                itemDecoration = new HorizontalDividerItemDecoration.Builder(activity).size(ViewUtil.scaleValue(dividerHeight)).color(activity.getResources().getColor(dividerColor)).build();
        } else {
            itemDecoration = new HorizontalDividerItemDecoration.Builder(getContext()).size(ViewUtil.scaleValue(1)).color(activity.getResources().getColor(R.color.gray_listLine)).build();
        }
        return itemDecoration;
    }
    public RecyclerView.ItemDecoration getItemDecoration() {
        return getItemDecoration(-2,-1);
    }
    /**
     * 设置表格布局横向item个数
     *
     * @param count
     */
    public void setGridLayoutCount(int count) {
        rv_list.setLayoutManager(new GridLayoutManager(activity, count));
        rv_list.addItemDecoration(new GridSpacingItemDecoration(count, 10, false));
        //	rv_list.addItemDecoration(new GridSpacingItemDecoration(3, ViewUtil.scaleValue(6), false));
    }

    /***
     * 设置当前页数
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public void setListData(List<ADT> newData) {
        setListData(newData,null);
    }
    public void setListData(List<ADT> newData, String noDataText) {
        if (newData == null) {
            if (mSwipeRefreshLayout!=null)
                mSwipeRefreshLayout.setRefreshing(false);
            addEmptyView(noDataText);
            adapter.getData().clear();
            adapter.notifyDataSetChanged();
            adapter.removeAllFooterView();
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


    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        private RecyclerView rv_list;

        @Override
        public boolean onDown(MotionEvent e) {
            mIsPrepressed = true;
            mPressedView = rv_list.findChildViewUnder(e.getX(), e.getY());
            super.onDown(e);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {
//                mPressedView.setPressed(true);
                mIsShowPress = true;
            }
            super.onShowPress(e);
        }

        public ItemTouchHelperGestureListener(RecyclerView rv_list) {
            this.rv_list = rv_list;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mIsPrepressed && mPressedView != null) {

                final View pressedView = mPressedView;
                RecyclerViewHolder vh = (RecyclerViewHolder) rv_list.getChildViewHolder(pressedView);

                if (isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    return false;
                }
                childClickViewIds = vh.getChildClickViewIds();

                if (childClickViewIds != null && childClickViewIds.size() > 0) {
                    for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
                        View childView = pressedView.findViewById((Integer) it.next());
                        if (inRangeOfView(childView, e) && childView.isEnabled()) {
                            setPressViewHotSpot(e, childView);
                            childView.setPressed(true);
                            int pos = vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
                            onListItemClickListener.onItemChildClick(adapter, childView, adapter.getItem(pos), pos);
                            resetPressedView(childView);
                            return true;
                        } else {
                            childView.setPressed(false);
                        }
                    }
                    setPressViewHotSpot(e, pressedView);
                    mPressedView.setPressed(true);
                    for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
                        View childView = pressedView.findViewById((Integer) it.next());
                        childView.setPressed(false);
                    }
                    int pos = vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
                    onListItemClickListener.onItemClick(adapter, pressedView, adapter.getItem(pos), pos);
                } else {
                    setPressViewHotSpot(e, pressedView);
                    mPressedView.setPressed(true);
                    for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
                        View childView = pressedView.findViewById((Integer) it.next());
                        childView.setPressed(false);
                    }
                    int pos = vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
                    onListItemClickListener.onItemClick(adapter, pressedView, adapter.getItem(pos), pos);
                }
                resetPressedView(pressedView);

            }
            return true;
        }

        private void resetPressedView(final View pressedView) {
            if (pressedView != null) {
                pressedView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pressedView != null) {
                            pressedView.setPressed(false);
                        }

                    }
                }, 100);
            }

            mIsPrepressed = false;
            mPressedView = null;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            boolean isChildLongClick = false;
            if (mIsPrepressed && mPressedView != null) {
                mPressedView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                RecyclerViewHolder vh = (RecyclerViewHolder) rv_list.getChildViewHolder(mPressedView);
                if (!isHeaderOrFooterPosition(vh.getLayoutPosition())) {
                    longClickViewIds = vh.getItemChildLongClickViewIds();
                    if (longClickViewIds != null && longClickViewIds.size() > 0) {
                        for (Iterator it = longClickViewIds.iterator(); it.hasNext(); ) {
                            View childView = mPressedView.findViewById((Integer) it.next());
                            if (inRangeOfView(childView, e) && childView.isEnabled()) {
                                setPressViewHotSpot(e, childView);
                                int pos = vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
                                onListItemClickListener.onItemChildLongClick(adapter, childView, adapter.getItem(pos), pos);
                                childView.setPressed(true);
                                mIsShowPress = true;
                                isChildLongClick = true;
                                break;
                            }
                        }
                    }
                    if (!isChildLongClick) {
                        int pos = vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
                        onListItemClickListener.onItemLongClick(adapter, mPressedView, adapter.getItem(pos), pos);
                        setPressViewHotSpot(e, mPressedView);
                        mPressedView.setPressed(true);
                        for (Iterator it = longClickViewIds.iterator(); it.hasNext(); ) {
                            View childView = mPressedView.findViewById((Integer) it.next());
                            childView.setPressed(false);
                        }
                        mIsShowPress = true;
                    }

                }

            }
        }
    }
    private void setPressViewHotSpot(final MotionEvent e, final View mPressedView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * when   click   Outside the region  ,mPressedView is null
             */
            if (mPressedView != null && mPressedView.getBackground() != null) {
                mPressedView.getBackground().setHotspot(e.getRawX(), e.getY() - mPressedView.getY());
            }
        }
    }

    public boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        if (view.getVisibility() != View.VISIBLE) {
            return false;
        }
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x
                || ev.getRawX() > (x + view.getWidth())
                || ev.getRawY() < y
                || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    private boolean isHeaderOrFooterPosition(int position) {
        /**
         *  have a headview and EMPTY_VIEW FOOTER_VIEW LOADING_VIEW
         */
        if (adapter == null) {
            if (rv_list != null) {
                adapter = (RecyclerBaseAdapter) rv_list.getAdapter();
            } else {
                return false;
            }
        }
        int type = adapter.getItemViewType(position);
        return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (rv_list == null) {
            this.rv_list = rv;
        }
        if (!mGestureDetector.onTouchEvent(e) && e.getActionMasked() == MotionEvent.ACTION_UP && mIsShowPress) {
            if (mPressedView != null) {
                RecyclerViewHolder vh = (RecyclerViewHolder) rv_list.getChildViewHolder(mPressedView);
                if (vh == null || vh.getItemViewType() != LOADING_VIEW) {
                    mPressedView.setPressed(false);
                }
                mPressedView = null;
            }
            mIsShowPress = false;
            mIsPrepressed = false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
