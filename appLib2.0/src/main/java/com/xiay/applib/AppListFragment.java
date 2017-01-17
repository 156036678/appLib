package com.xiay.applib;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nohttp.extra.HttpListener;
import com.nohttp.rest.Response;
import com.xiay.applib.view.VerticalSwipeRefreshLayout;
import com.xiay.applib.view.recyclerview.RecyclerBaseAdapter;
import com.xiay.applib.view.recyclerview.RecyclerViewHolder;
import com.xiay.applib.view.recyclerview.WrapContentLinearLayoutManager;
import com.xiay.applib.view.recyclerview.listener.OnListItemClickListener;
import com.xiay.applib.view.recyclerview.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.xiay.util.ViewUtil;

import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.EMPTY_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.FOOTER_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.HEADER_VIEW;
import static com.xiay.applib.view.recyclerview.RecyclerBaseAdapter.LOADING_VIEW;
/***
 *@author Xiay
 * @param <RQ>请求数据类型
 * @param <ADT> Adapter 数据类型
 * @param <AD>Adapter 类型
 */
public abstract class AppListFragment<RQ,ADT,AD extends RecyclerBaseAdapter<ADT>> extends AppFragment  implements RecyclerView.OnItemTouchListener,OnListItemClickListener<ADT,AD>, SwipeRefreshLayout.OnRefreshListener, RecyclerBaseAdapter.RequestLoadMoreListener, HttpListener<RQ> {
	private ViewGroup emptyView;
	public RecyclerView rv_list;
	public AD adapter;
	private GestureDetectorCompat mGestureDetector;
	private View mPressedView = null;
	private boolean mIsPrepressed = false;
	private boolean mIsShowPress = false;
	private Set<Integer> childClickViewIds;
	private Set<Integer> longClickViewIds;
	protected VerticalSwipeRefreshLayout mSwipeRefreshLayout;
	protected int currentPage = 1;
	/**
	 * 每次分页获取的大小
	 */
	protected int perPageSize = 10;
	public boolean isShowEnd = true;
	View viewRoot;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity.isAutoShowNoNetwork=false;
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
		viewRoot=v;
		rv_list = (RecyclerView) viewRoot.findViewById(R.id.rv_list);
		if (dividerHeight!=-2){//如果不是默认
			if (dividerColor==-1)
				dividerColor=R.color.transparent;
			if (dividerHeight!=-1)
				rv_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(activity).size(ViewUtil.scaleValue(dividerHeight)).color(getResources().getColor(dividerColor)).build());
		}else {
			rv_list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
		}
		rv_list.setLayoutManager(new WrapContentLinearLayoutManager(activity));
		//      添加动画
		rv_list.setItemAnimator(new DefaultItemAnimator());
		rv_list.addOnItemTouchListener(this);
		mGestureDetector = new GestureDetectorCompat(rv_list.getContext(), new ItemTouchHelperGestureListener(rv_list));
		this.adapter=  adapter;
		if (emptyMessage!=null)
			adapter.setEmptyView(getEmptyView(emptyMessage));
		rv_list.setAdapter(adapter);
		mSwipeRefreshLayout = (VerticalSwipeRefreshLayout)viewRoot.findViewById(R.id.swipeLayout);
		if (mSwipeRefreshLayout!=null){
			mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.yellow);
			mSwipeRefreshLayout.setOnRefreshListener(this);
		}
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
			ViewUtil.scaleContentView(emptyView);
		}
		return emptyView;
	}
	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
		if (rv_list == null) {
			this.rv_list = rv;
		}
		if (!mGestureDetector.onTouchEvent(e) && e.getActionMasked() == MotionEvent.ACTION_UP && mIsShowPress) {
			if (mPressedView!=null){
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
						if (inRangeOfView(childView, e)&&childView.isEnabled()) {
							setPressViewHotSpot(e,childView);
							childView.setPressed(true);
							int pos= vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
							onItemChildClick(adapter, childView,adapter.getItem(pos),pos );
							resetPressedView(childView);
							return true;
						}else {
							childView.setPressed(false);
						}
					}
					setPressViewHotSpot(e,pressedView);
					mPressedView.setPressed(true);
					for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
						View childView = pressedView.findViewById((Integer) it.next());
						childView.setPressed(false);
					}
					int pos= vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
					onItemClick(adapter, pressedView,adapter.getItem(pos),pos);
				} else {
					setPressViewHotSpot(e,pressedView);
					mPressedView.setPressed(true);
					for (Iterator it = childClickViewIds.iterator(); it.hasNext(); ) {
						View childView = pressedView.findViewById((Integer) it.next());
						childView.setPressed(false);
					}
					int pos= vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
					onItemClick(adapter, pressedView,adapter.getItem(pos),pos);
				}
				resetPressedView(pressedView);

			}
			return true;
		}

		private void resetPressedView(final View pressedView) {
			if (pressedView!=null){
				pressedView.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (pressedView!=null){
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
			boolean isChildLongClick =false;
			if (mIsPrepressed && mPressedView != null) {
				mPressedView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				RecyclerViewHolder vh = (RecyclerViewHolder) rv_list.getChildViewHolder(mPressedView);
				if (!isHeaderOrFooterPosition(vh.getLayoutPosition())) {
					longClickViewIds = vh.getItemChildLongClickViewIds();
					if (longClickViewIds != null && longClickViewIds.size() > 0) {
						for (Iterator it = longClickViewIds.iterator(); it.hasNext(); ) {
							View childView = mPressedView.findViewById((Integer) it.next());
							if (inRangeOfView(childView, e)&&childView.isEnabled()) {
								setPressViewHotSpot(e,childView);
								int pos= vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
								onItemChildLongClick(adapter, childView,adapter.getItem(pos),pos);
								childView.setPressed(true);
								mIsShowPress = true;
								isChildLongClick =true;
								break;
							}
						}
					}
					if (!isChildLongClick){
						int pos= vh.getLayoutPosition() - adapter.getHeaderLayoutCount();
						onItemLongClick(adapter, mPressedView,adapter.getItem(pos),pos);
						setPressViewHotSpot(e,mPressedView);
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
	private void setPressViewHotSpot(final MotionEvent e,final  View mPressedView) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			/**
			 * when   click   Outside the region  ,mPressedView is null
			 */
			if (mPressedView !=null && mPressedView.getBackground() != null) {
				mPressedView.getBackground().setHotspot(e.getRawX(), e.getY()-mPressedView.getY());
			}
		}
	}
	public boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		if (view.getVisibility()!=View.VISIBLE){
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
		if (adapter==null){
			if (rv_list!=null){
				adapter= (AD) rv_list.getAdapter();
			}else {
				return false;
			}
		}
		int type = adapter.getItemViewType(position);
		return (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW);
	}
	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		mGestureDetector.onTouchEvent(e);
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

	}
	protected void addHeaderView(View header) {
		adapter.removeAllHeaderView();
		adapter.addHeaderView(header);
		adapter.setHeaderAndEmpty(true);
	}

	protected void addEmptyView(String emptyText) {
		if (emptyText != null && emptyView == null)
			adapter.setEmptyView(getEmptyView(emptyText));
	}

	protected void addHeaderAndEmptyView(View header, String emptyText) {
		adapter.removeAllHeaderView();
		adapter.addHeaderView(header);
		adapter.setHeaderAndEmpty(true);
		if (emptyText!=null)
			adapter.setEmptyView(getEmptyView(emptyText));
	}

	protected void setListData(List<ADT> newData) {
		setListData(newData, null);
	}

	protected void setListData(List<ADT> newData, String noDataText) {
		if (newData == null) {
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
	public void toEnd() {
		if (adapter != null) {
			adapter.loadMoreEnd(isShowEnd);
		}
	}
	public abstract void getListData();
	@Override
	public void onFailed(int i, Response response) {
		if (mSwipeRefreshLayout!=null)
		  mSwipeRefreshLayout.setRefreshing(false);
		if (adapter!=null)
			adapter.loadMoreFail();
	}
}
