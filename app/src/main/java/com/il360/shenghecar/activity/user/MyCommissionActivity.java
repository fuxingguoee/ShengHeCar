package com.il360.shenghecar.activity.user;

import java.text.DecimalFormat;
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
import com.il360.shenghecar.activity.coupon.RankListActivity_;
import com.il360.shenghecar.adapter.CommisGetAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.user.ArrayOfCommisGet;
import com.il360.shenghecar.model.user.UserAmount;
import com.il360.shenghecar.model.user.UserReward;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.view.CustomDialog;
import com.il360.shenghecar.view.ListViewForScrollView;
import com.umeng.socialize.UMShareAPI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_my_commission)
public class MyCommissionActivity extends BaseWidgetActivity{
	private Context context = this;
	
	private List<UserReward> commisGetList = new ArrayList<UserReward>();

	private CommisGetAdapter getAdapter;
	
	/** 当前页码 **/
	private int pageNo = 1;
	/** 默认每页加载个数 **/
	private int pageSize = 20;
	
	@ViewById
	TextView tvTextClick;
	@ViewById 
	TextView tvPutForward;
	@ViewById
	ListViewForScrollView commisList;
	
	@ViewById
	PullToRefreshScrollView pull_refresh_scrollview;
	
	@ViewById
	TextView tvTotalCommis;//可提现
	@ViewById
	TextView tvLastMonthCommis;//已提现
	
	@ViewById
	LinearLayout llRanking;
	
	private UserAmount userAmount;
	
	DecimalFormat df = new DecimalFormat("0");
	
	private ArrayOfCommisGet getResponse;
	
	@AfterViews
	void init() {
		initViews();
		initCommission();
		initRefreshListener();
		showList();
	}
	
	private void initViews() {
		tvTextClick.setText("提现记录");
		pull_refresh_scrollview.setMode(Mode.BOTH);
	}
	
	@Click
	void tvTextClick() {
		Intent intent = new Intent(MyCommissionActivity.this,PutForwardRecordActivity_.class);
		startActivity(intent);
	}
	
	@Click
	void llRanking (){
		Intent intent = new Intent(MyCommissionActivity.this, RankListActivity_.class);
		startActivity(intent);
	}
	
	@Click
	void tvPutForward() {
		if (UserUtil.judgeAuthentication()) {
			if (UserUtil.judgeBankCard()) {
				showDialog();
			} else {
				showDialog2();
			}
		} else {
			showDialog1();
		}
	}

	private void initCommission() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"user/queryUserAmount", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(MyCommissionActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								userAmount = FastJsonUtils.getSingleBean(objRes.toString(), UserAmount.class);
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					}
				} catch (Exception e) {
					Log.e("MyCommissionActivity", "initCommission", e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
//							if(userAmount != null && userAmount.getInvitationAmount() != null){
////								amount = userAmount.getInvitationAmount().doubleValue();
//								tvTotalCommis.setText(df.format(userAmount.getInvitationAmount()));
//								tvLastMonthCommis.setText(df.format(userAmount.getUseInvitationAmount()));
//							} else {
////								amount = 0.00;
//								tvTotalCommis.setText("0.00");
//								tvLastMonthCommis.setText("0.00");
//							}
						}
					});
				}
			}
		});
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
						initCommission();
					}
				}, 500);
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if(isLast()){
					commisList.postDelayed(new Runnable() {
						@Override
						public void run() {
							pull_refresh_scrollview.onRefreshComplete();
							showInfo("已经到底部了！");
						}
					}, 1000);
				}else{
					pageNo++;
					showList();
				}
			}
		});
	}
	
	private boolean isLast() {
		if (getResponse != null && pageNo * pageSize < getResponse.getTotalCount()) {
			return false;
		} else {
			return true;
		}
	}

	private void showList() {
		runOnUiThread(new Runnable() {
			public void run() {
				getAdapter = new CommisGetAdapter(commisGetList, context);
				commisList.setAdapter(getAdapter);
				getAdapter.notifyDataSetChanged();
			}
		});
		
		initCommisGetList();
	}
	
	private void initCommisGetList() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("pageNo", pageNo + "");
					params.put("pageSize", pageSize + "");
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"user/queryReward", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(MyCommissionActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								JSONObject objResult = objRes.getJSONObject("returnResult");
								getResponse = FastJsonUtils.getSingleBean(objResult.toString(), ArrayOfCommisGet.class);
								if (getResponse != null) {
									if (pageNo == 1) {
										commisGetList.clear();
									}
									commisGetList.addAll(getResponse.getList());
								}
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("MyCommissionActivity", "initCommisGetList", e);
					LogUmeng.reportError(MyCommissionActivity.this, e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							getAdapter.notifyDataSetChanged();
							pull_refresh_scrollview.onRefreshComplete();
						}
					});

				}
			}
		});
	}
	
	private void showDialog() {
		final CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setTitle("奖励金申请提现");
		builder.setMessage("请输入需要提款金额，本次最多可提款："+ViewUtil.getText(tvTotalCommis)+"元");
		builder.setMessageSize(14);
		builder.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		builder.setEtMessageHint("请输入提款金额(不可低于50元)");
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isOK(builder.getEtMessage())) {
					initApplyCommis(builder.getEtMessage());
					dialog.dismiss();
				}
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	protected void initApplyCommis(final String applyMoney) {

		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("amount", applyMoney);
					params.put("token", UserUtil.getToken());
					params.put("bankNo", GlobalPara.getOutUserBank().getBankNo());
					params.put("bankName", GlobalPara.getOutUserBank().getBankName());
					params.put("name", GlobalPara.getOutUserBank().getCardName());
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"user/addUserWithdrawals", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(MyCommissionActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							showInfo(obj.getString("desc"));
							if (obj.getInt("code") == 1) {
								pageNo = 1;
								showList();
								initCommission();
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("MyCommissionActivity", "initCommisApplyList", e);
					LogUmeng.reportError(MyCommissionActivity.this, e);
				}
			}
		});

	}

	private boolean isOK(String applyMoney) {
		if (applyMoney == null || applyMoney == "" || applyMoney.length() == 0) {
			showInfo("请输入申请金额");
		} else if (Integer.parseInt(applyMoney) < 50) {
			showInfo("申请金额至少50元");
		} else if (Integer.parseInt(applyMoney) > Double.valueOf(ViewUtil.getText(tvTotalCommis))) {
			showInfo("申请金额超过了可申请额度");
		} else {
			return true;
		}
		return false;
	}
	
	private void showDialog1() {//认证提示
		CustomDialog.Builder builder = new CustomDialog.Builder(MyCommissionActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(R.string.H2);
		builder.setPositiveButton("去认证", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MyCommissionActivity.this, VerifiedActivity_.class);
				MyCommissionActivity.this.startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	private void showDialog2() {//绑定银行卡
		CustomDialog.Builder builder = new CustomDialog.Builder(MyCommissionActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(R.string.H3);
		builder.setPositiveButton("去认证", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MyCommissionActivity.this, MyBankCardActivity_.class);
				MyCommissionActivity.this.startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(MyCommissionActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
