package com.il360.shenghecar.activity.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.RepayAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.ArrayOfOrderExt;
import com.il360.shenghecar.model.order.OrderExt;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.ListViewForScrollView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_repayment)
public class RepaymentActivity extends BaseWidgetActivity {

	@ViewById PullToRefreshScrollView pull_refresh_scrollview;
	
	@ViewById TextView tvStayStill;
	@ViewById TextView tvAlreadyRepaid;
	@ViewById TextView tvLine1;
	@ViewById TextView tvLine2;
	
	@ViewById ListViewForScrollView myList;
	
	private List<OrderExt> myOrderExtList= new ArrayList<OrderExt>();
	private RepayAdapter myAdapter;
	
	private int flag;//0未还款1已还款
	/** 当前页码 **/
	private int pageNo = 1;
	/** 默认每页加载个数 **/
	private int pageSize = 20;
	
	private ArrayOfOrderExt arrayOfOrderExt;
	
	protected ProgressDialog transDialog;
	
	@AfterViews
	void init(){
		initViews();
		initRefreshListener();
		tvStayStill();
	}


	private void initViews() {
//		pull_refresh_scrollview.setMode(Mode.BOTH);
		
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
	
	protected boolean isLast() {
		if (arrayOfOrderExt != null && pageNo * pageSize < arrayOfOrderExt.getTotalCount()) {
			return false;
		} else {
			return true;
		}
	}


	@Click
	void tvStayStill(){
		tvStayStill.setTextColor(ContextCompat.getColor(RepaymentActivity.this, R.color.main_logo2));
		tvAlreadyRepaid.setTextColor(ContextCompat.getColor(RepaymentActivity.this, R.color.black));
		tvLine1.setBackgroundResource(R.color.main_logo2);
		tvLine2.setBackgroundResource(R.color.line_d);
		flag = 0;
		myOrderExtList.clear();
		showList();
	}
	

	@Click
	void tvAlreadyRepaid(){
		tvStayStill.setTextColor(ContextCompat.getColor(RepaymentActivity.this, R.color.black));
		tvAlreadyRepaid.setTextColor(ContextCompat.getColor(RepaymentActivity.this, R.color.main_logo2));
		tvLine1.setBackgroundResource(R.color.line_d);
		tvLine2.setBackgroundResource(R.color.main_logo2);
		flag = 1;
		myOrderExtList.clear();
		showList();
	}
	
	
	private void showList() {
		if (flag == 0 || flag == 1) {
			pull_refresh_scrollview.setMode(Mode.BOTH);
			myAdapter = new RepayAdapter(myOrderExtList, RepaymentActivity.this);
			myList.setAdapter(myAdapter);
			myList.setOnItemClickListener(new OnItemClickListener());
			myAdapter.notifyDataSetChanged();
			initMyList();
		} else {
			pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
			pull_refresh_scrollview.onRefreshComplete();
		}
	}
	
	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			if(myOrder.get(position).getPayStatus() == 1){
//				Intent intent = new Intent(RepaymentActivity.this,PayBackActivity_.class);
//				intent.putExtra("status", "1");
//				intent.putExtra("orderNo", myOrder.get(position).getOrderNo());
//				startActivity(intent);
//			} else if(myOrder.get(position).getPayStatus() == 0){
//				Intent intent = new Intent(RepaymentActivity.this,NotPayBackActivity_.class);
//				intent.putExtra("status", "0");
//				intent.putExtra("orderNo", myOrder.get(position).getOrderNo());
//				startActivity(intent);
//			}
			
		}
	}


	private void initMyList() {
		transDialog = ProgressDialog.show(RepaymentActivity.this, null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("pageNo", pageNo + "");
					params.put("pageSize", pageSize + "");
					params.put("token", UserUtil.getToken());
					params.put("status", flag + "");
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"order/queryOrderExtList", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(RepaymentActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								JSONObject objResult = objRes.getJSONObject("returnResult");
								arrayOfOrderExt = FastJsonUtils.getSingleBean(objResult.toString(), ArrayOfOrderExt.class);
								if (arrayOfOrderExt != null) {
									if (pageNo == 1) {
										myOrderExtList.clear();
									}
									myOrderExtList.addAll(arrayOfOrderExt.getList());
								}
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("RepaymentActivity", "initMyList", e);
					LogUmeng.reportError(RepaymentActivity.this, e);
				} finally {

					runOnUiThread(new Runnable() {
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
		});
	}
	
	
	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				if (transDialog != null && transDialog.isShowing()) {
					transDialog.dismiss();
				}
				Toast.makeText(RepaymentActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
