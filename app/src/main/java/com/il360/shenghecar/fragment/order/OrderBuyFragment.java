package com.il360.shenghecar.fragment.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.order.DetailsBuyOrderActivity_;
import com.il360.shenghecar.adapter.OrderBuyAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.fragment.MyFragment;
import com.il360.shenghecar.model.order.ArrayOfOrder;
import com.il360.shenghecar.model.order.Order;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.ListViewForScrollView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@EFragment(R.layout.fra_order_buy)
public class OrderBuyFragment extends MyFragment {
	
	@ViewById
	TextView tvTab1;
	@ViewById TextView tvTab2;
	@ViewById TextView tvTab3;
	@ViewById TextView tvLine1;
	@ViewById TextView tvLine2;
	@ViewById TextView tvLine3;
	
	@ViewById PullToRefreshScrollView pull_refresh_scrollview;
	@ViewById ListViewForScrollView myList;
	
	private List<Order> myOrder = new ArrayList<Order>();
	private OrderBuyAdapter myAdapter;
	
	private int flag;
	/** 当前页码 **/
	private int pageNo = 1;
	/** 默认每页加载个数 **/
	private int pageSize = 20;
	
	private ArrayOfOrder arrayOfOrder;
	
	protected ProgressDialog transDialog;
	
	@AfterViews
	void init() {
		initViews();
		tvTab1();
		initRefreshListener();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		
	}
	
	@Click
	void tvTab1() {
		tvTab1.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_logo));
		tvTab2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvTab3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvLine1.setBackgroundResource(R.color.main_logo);
		tvLine2.setBackgroundResource(R.color.line_d);
		tvLine3.setBackgroundResource(R.color.line_d);
		flag = 1;
		myOrder.clear();
		showList();
	}
	
	@Click 
	void tvTab2() {
		tvTab2.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_logo));
		tvTab1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvTab3.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvLine2.setBackgroundResource(R.color.main_logo);
		tvLine1.setBackgroundResource(R.color.line_d);
		tvLine3.setBackgroundResource(R.color.line_d);
		flag = 2;
		myOrder.clear();
		showList();
	}
	
	@Click 
	void tvTab3() {
		tvTab3.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_logo));
		tvTab2.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvTab1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
		tvLine3.setBackgroundResource(R.color.main_logo);
		tvLine2.setBackgroundResource(R.color.line_d);
		tvLine1.setBackgroundResource(R.color.line_d);
		flag = 3;
		myOrder.clear();
		showList();
	}

	private void initRefreshListener() {
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				myList.postDelayed(new Runnable() {
					@Override
					public void run() {
						pageNo = 1;
						showList();
					}
				}, 500);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (isLast()) {
					myList.postDelayed(new Runnable() {
						@Override
						public void run() {
							pull_refresh_scrollview.onRefreshComplete();
							showInfo("已经到底部了！");
						}
					}, 1000);
				} else {
					pageNo++;
					showList();
				}
			}
		});
	}

	protected void showList() {
//		if (flag == 1 || flag == 2 || flag == 3) {
//			pull_refresh_scrollview.setMode(Mode.BOTH);
//			myAdapter = new OrderBuyAdapter(myOrder, getActivity());
//			myList.setAdapter(myAdapter);
//			myList.setOnItemClickListener(new myListClickListener());
//			myAdapter.notifyDataSetChanged();
//			initMyList();
//		} else {
//			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
//			pull_refresh_scrollview.onRefreshComplete();
//		}
		
	}
	
	class myListClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(getActivity(),DetailsBuyOrderActivity_.class);
			intent.putExtra("order", myOrder.get(position));
			startActivityForResult(intent, 1);
		}
	}

	private void initMyList() {
		transDialog = ProgressDialog.show(getActivity(), null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("pageNo", pageNo + "");
					params.put("pageSize", pageSize + "");
					params.put("token", UserUtil.getToken());
					if(flag == 2 || flag ==3) {
						params.put("status", flag + "");
					} 
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"order/queryOrderList1", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							JSONObject objRes = obj.getJSONObject("result");
							JSONObject objResult = objRes.getJSONObject("returnResult");
							arrayOfOrder = FastJsonUtils.getSingleBean(objResult.toString(), ArrayOfOrder.class);
							if (arrayOfOrder != null && arrayOfOrder.getList() != null && arrayOfOrder.getList().size() > 0) {
								if (pageNo == 1) {
									myOrder.clear();
								}
								myOrder.addAll(arrayOfOrder.getList());
							}
						} else {
							showInfo(obj.getString("desc"));
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("OrderBuyFragment", "initMyList", e);
					LogUmeng.reportError(getActivity(), e);
				} finally {
					FragmentActivity fragAct = getActivity();
					if (fragAct != null) {
						fragAct.runOnUiThread(new Runnable() {
							public void run() {

								if (transDialog != null && transDialog.isShowing()) {
									transDialog.dismiss();
								}
								myAdapter.notifyDataSetChanged();
								pull_refresh_scrollview.onRefreshComplete();
							}
						});

					}
				}
			}
		});
	}

	protected boolean isLast() {
		if (arrayOfOrder != null && pageNo * pageSize < arrayOfOrder.getTotalCount()) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			pageNo = 1;
			showList();
		}
	}

	public void showInfo(final String info) {
		FragmentActivity fragAct = getActivity();
		if (fragAct != null) {
			fragAct.runOnUiThread(new Runnable() {
				public void run() {
					if (transDialog != null && transDialog.isShowing()) {
						transDialog.dismiss();
					}
					Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
}
