package com.il360.shenghecar.fragment.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.order.DetailsRecoveryOrderActivity_;
import com.il360.shenghecar.adapter.OrderRecoveryAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.fragment.MyFragment;
import com.il360.shenghecar.model.recovery.ArrayOfUserRecovery;
import com.il360.shenghecar.model.recovery.UserRecovery;
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

@EFragment(R.layout.fra_order_recovery)
public class OrderRecoveryFragment extends MyFragment {
	@ViewById
	TextView tvTab1;
	@ViewById
	TextView tvTab2;
	@ViewById
	TextView tvTab3;
	@ViewById
	TextView tvLine1;
	@ViewById
	TextView tvLine2;
	@ViewById
	TextView tvLine3;

	@ViewById
	PullToRefreshScrollView pull_refresh_scrollview;
	@ViewById
	ListViewForScrollView myList;

	private List<UserRecovery> myRecovery1 = new ArrayList<UserRecovery>();
	private List<UserRecovery> myRecovery2 = new ArrayList<UserRecovery>();
	private List<UserRecovery> myRecovery3 = new ArrayList<UserRecovery>();
	private OrderRecoveryAdapter myAdapter;

	private int flag;

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


	private void initRefreshListener() {
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				myList.postDelayed(new Runnable() {
					@Override
					public void run() {
						tvTab1();
					}
				}, 500);
			}
		});
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
		showList();
	}


	private void showList() {
		if (flag == 1) {
			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
			myAdapter = new OrderRecoveryAdapter(myRecovery1, getActivity());
			myList.setAdapter(myAdapter);
			myList.setOnItemClickListener(new myListClickListener());
			myAdapter.notifyDataSetChanged();
			initMyList();
		} else if (flag == 2) {
			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
			myAdapter = new OrderRecoveryAdapter(myRecovery2, getActivity());
			myList.setAdapter(myAdapter);
			myList.setOnItemClickListener(new myListClickListener());
			myAdapter.notifyDataSetChanged();
		} else if (flag == 3) {
			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
			myAdapter = new OrderRecoveryAdapter(myRecovery3, getActivity());
			myList.setAdapter(myAdapter);
			myList.setOnItemClickListener(new myListClickListener());
			myAdapter.notifyDataSetChanged();
		} else {
			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
			pull_refresh_scrollview.onRefreshComplete();
		}
	}
	
	class myListClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(getActivity(), DetailsRecoveryOrderActivity_.class);
			if (flag == 1) {
				intent.putExtra("userRecovery", myRecovery1.get(position));
			} else if (flag == 2) {
				intent.putExtra("userRecovery", myRecovery2.get(position));
			} else if (flag == 3) {
				intent.putExtra("userRecovery", myRecovery3.get(position));
			}
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
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"recovery/queryUserRecovery", params);
					if (result.getSuccess()) {
						ArrayOfUserRecovery arrayOfUserRecovery = FastJsonUtils.getSingleBean(result.getResult(), ArrayOfUserRecovery.class);
						if (arrayOfUserRecovery.getCode() == 1) {
							if (arrayOfUserRecovery != null && arrayOfUserRecovery.getResult() != null && arrayOfUserRecovery.getResult().size() > 0) {
								myRecovery1.clear();
								myRecovery2.clear();
								myRecovery3.clear();
								myRecovery1.addAll(arrayOfUserRecovery.getResult());
								
								for (int i = 0; i < arrayOfUserRecovery.getResult().size(); i++) {
									if(arrayOfUserRecovery.getResult().get(i).getStatus() == 0
											|| arrayOfUserRecovery.getResult().get(i).getStatus() == 1
											|| arrayOfUserRecovery.getResult().get(i).getStatus() == 2
											|| arrayOfUserRecovery.getResult().get(i).getStatus() == 3){
										myRecovery2.add(arrayOfUserRecovery.getResult().get(i));
									} else {
										myRecovery3.add(arrayOfUserRecovery.getResult().get(i));
									}
								}
								
							} else {
								showInfo("暂无数据");
							}
						} else {
							showInfo(arrayOfUserRecovery.getDesc());
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 101) {
			tvTab1();
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
