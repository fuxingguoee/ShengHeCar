package com.il360.shenghecar.activity.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.CommisApplyAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.user.ArrayOfCommisApply;
import com.il360.shenghecar.model.user.UserWithdrawals;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.ListViewForScrollView;

import android.content.Intent;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

@EActivity(R.layout.act_putforward_record)
public class PutForwardRecordActivity extends BaseWidgetActivity {
	
	@ViewById
	ListViewForScrollView commisList;
	@ViewById
	PullToRefreshScrollView pull_refresh_scrollview;
	
	private List<UserWithdrawals> commisApplyList = new ArrayList<UserWithdrawals>();
	private CommisApplyAdapter applyAdapter;
	/** 当前页码 **/
	private int pageNo = 1;
	/** 默认每页加载个数 **/
	private int pageSize = 20;
	
	private ArrayOfCommisApply applyResponse;
	
	@AfterViews
	void init() {
		initRefreshListener();
		showList();
	}

	private void initRefreshListener() {
		pull_refresh_scrollview.setMode(Mode.BOTH);
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				commisList.postDelayed(new Runnable() {
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
					commisList.postDelayed(new Runnable() {
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
	
	private boolean isLast() {
		if (applyResponse != null && pageNo * pageSize < applyResponse.getTotalCount()) {
			return false;
		} else {
			return true;
		}
	}
	
	
	private void showList() {
		runOnUiThread(new Runnable() {
			public void run() {
				applyAdapter = new CommisApplyAdapter(commisApplyList, PutForwardRecordActivity.this);
				commisList.setAdapter(applyAdapter);
				applyAdapter.notifyDataSetChanged();
			}
		});
		
		initCommisApplyList();
	}
	
	private void initCommisApplyList() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("pageNo", pageNo + "");
					params.put("pageSize", pageSize + "");
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"user/queryUserWithdrawals", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(PutForwardRecordActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								JSONObject objResult = objRes.getJSONObject("returnResult");
								applyResponse = FastJsonUtils.getSingleBean(objResult.toString(), ArrayOfCommisApply.class);
								if (applyResponse != null) {
									if (pageNo == 1) {
										commisApplyList.clear();
									}
									commisApplyList.addAll(applyResponse.getList());
								}
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("PutForwardRecordActivity", "initCommisApplyList", e);
					LogUmeng.reportError(PutForwardRecordActivity.this, e);
				} finally {

					runOnUiThread(new Runnable() {
						public void run() {
							applyAdapter.notifyDataSetChanged();
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
				Toast.makeText(PutForwardRecordActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
