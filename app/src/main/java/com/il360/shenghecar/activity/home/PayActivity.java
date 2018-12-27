package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.activity.user.MyBankCardActivity_;
import com.il360.shenghecar.adapter.PayAdapter;
import com.il360.shenghecar.alipay.AliPayActivity_;
import com.il360.shenghecar.alipay.PaySuccessActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.hua.ArrayOfPayWay;
import com.il360.shenghecar.model.hua.PayWay;
import com.il360.shenghecar.model.order.OrderExt;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.act_pay) 
public class PayActivity extends BaseWidgetActivity{
	//1支付宝2快捷支付
	private static final String ALI_PAY = "1";
	private static final String KUAIJIE_PAY = "2";
	
	@ViewById TextView tvMoney;
	@ViewById ListView payList;

	@ViewById
	LinearLayout llAllRepay;
	@ViewById
	ImageView ivAllRepay;
	@ViewById
	LinearLayout llPartRepay;
	@ViewById
	ImageView ivPartRepay;
	
	@ViewById TextView tvBankList;
	
	@ViewById TextView tvSubmit;
	
	protected ProgressDialog transDialog;
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	@Extra
	OrderExt myOrderExt;
	
	private ArrayOfPayWay arrayOfPayWay;
	private PayAdapter adapter;
	private List<PayWay> list = new ArrayList<PayWay>();

	private int myPosition = 0;//列表第一种支付方式
	private String buyType = "2";//默认全额还款
	
	@AfterViews
	void init(){
		buyType = "2";
		ivAllRepay.setBackgroundResource(R.drawable.ic_radio_press);
		ivPartRepay.setBackgroundResource(R.drawable.ic_radio_normal);
		if(myOrderExt.getOverdueFee() != null && myOrderExt.getOverdueFee().compareTo(BigDecimal.ZERO) == 1) {
			tvMoney.setText("￥" + df.format(myOrderExt.getOrderAmount().add(myOrderExt.getOverdueFee())));
		} else {
			tvMoney.setText("￥" + df.format(myOrderExt.getOrderAmount()));
		}
		initPayWay();
	}
	
	@Click
	void tvBankList() {
//		Intent intent = new Intent(PayActivity.this, UrlToWebActivity_.class);
//		intent.putExtra("title", "支持支付的银行列表");
//		intent.putExtra("supportZoom", false);
//		intent.putExtra("url", "http://www.ycaomei.com/cmfq/%E6%94%AF%E6%8C%81%E9%93%B6%E8%A1%8C.html");
//		startActivity(intent);
	}

	@Click
	void llAllRepay() {//全额
		buyType = "2";
		ivAllRepay.setBackgroundResource(R.drawable.ic_radio_press);
		ivPartRepay.setBackgroundResource(R.drawable.ic_radio_normal);
		if(myOrderExt.getOverdueFee() != null && myOrderExt.getOverdueFee().compareTo(BigDecimal.ZERO) == 1) {
			tvMoney.setText("￥" + df.format(myOrderExt.getOrderAmount().add(myOrderExt.getOverdueFee())));//小数
		} else {
			tvMoney.setText("￥" + df.format(myOrderExt.getOrderAmount()));
		}
	}

	@Click
	void llPartRepay() {//分期
		buyType = "1";
		ivAllRepay.setBackgroundResource(R.drawable.ic_radio_normal);
		ivPartRepay.setBackgroundResource(R.drawable.ic_radio_press);
		if(myOrderExt.getOverdueFee() != null && myOrderExt.getOverdueFee().compareTo(BigDecimal.ZERO) == 1) {
			tvMoney.setText("￥" + df.format(myOrderExt.getFee().add(myOrderExt.getOverdueFee())));
		} else {
			tvMoney.setText("￥" + df.format(myOrderExt.getFee()));
		}
	}

	private void initPayWay() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "card/payTypeList",params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(PayActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							arrayOfPayWay = FastJsonUtils.getSingleBean(result.getResult().toString(), ArrayOfPayWay.class);
							if(arrayOfPayWay.getCode() == 1){
								if (arrayOfPayWay.getResult() != null && arrayOfPayWay.getResult().size() > 0) {
									list.addAll(arrayOfPayWay.getResult());
								}
							} else {
								showInfo(arrayOfPayWay.getDesc());
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("PayActivity", "initPayWay()", e);
					LogUmeng.reportError(PayActivity.this, e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if(list != null && list.size() > 0){
								adapter = new PayAdapter(list, PayActivity.this, myPosition);
								payList.setAdapter(adapter);
								payList.setOnItemClickListener(new OnItemClickListener());
								adapter.notifyDataSetChanged();
								
								tvSubmit.setVisibility(View.VISIBLE);
							} else {
								tvSubmit.setVisibility(View.GONE);
								showInfo("暂无支付方式");
							}
						}
					});
				}
			}
		});
	}
	
	
	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			myPosition = position;
			adapter = new PayAdapter(list, PayActivity.this, myPosition);
			payList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Click
	void tvSubmit(){
		tvSubmit.setClickable(false);
		
		pay();
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				tvSubmit.setClickable(true);
			}
		};
		timer.schedule(task, 1000 * 2);
	}
	
	
	private void pay(){
		if (list.get(myPosition).getType() == 1) {
			start(ALI_PAY);
		} else if (list.get(myPosition).getType() == 2) {
			if(UserUtil.judgeBankCard()){
				start(KUAIJIE_PAY);
			} else {
				showDialog();
			}
		} else {
			showInfo("请选择支付方式");
		}
	}
	
	private void initQuickPayCode() {
		transDialog = ProgressDialog.show(PayActivity.this, null, "签约中...", true, false);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"card/quickPayCode", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(PayActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JsonObject obj = new JsonParser().parse(result.getResult()).getAsJsonObject();
							if (obj.get("code").getAsInt() == 1) {
								showDialog4(obj.get("desc").getAsString());
							} else {
								showInfo(obj.get("desc").getAsString());
							}
						}
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}
							
						}
					});
				}
			}
		});
	}
	
	private void initQuickCode(final String sn, final String smsCode) {
		transDialog = ProgressDialog.show(PayActivity.this, null, "提交中...", true, false);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("sn", sn);
					params.put("voidcode", smsCode);
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"card/quickCode", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(PayActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JsonObject obj = new JsonParser().parse(result.getResult()).getAsJsonObject();
							if (obj.get("code").getAsInt() == 1) {
//								showInfo("签约成功");
								placeOrder(KUAIJIE_PAY);
							} else {
								showInfo(obj.get("desc").getAsString());
							}
						}
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				} 
			}
		});
	}

	private void start(String channel) {
		if (TextUtils.isEmpty(UserUtil.getToken())) {
			showInfo("长时间未登录，请先登录");
		} else {
			transDialog = ProgressDialog.show(PayActivity.this, null, "提交中...", true, false);
			placeOrder(channel);
		}
	}

	private void placeOrder(final String channel) {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("orderNo",  myOrderExt.getOrderNo());
					params.put("token", UserUtil.getToken());
					params.put("busType", buyType);//1续还2全额还款
					params.put("type", channel);
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"pay/generatePayOrder", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(PayActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JsonObject obj = new JsonParser().parse(result.getResult()).getAsJsonObject();
							if (obj.get("code").getAsInt() == 1) {
								if (ALI_PAY.equals(channel)){
									sendAliPay(obj.get("result").getAsJsonObject());
								} else if (KUAIJIE_PAY.equals(channel)){
									sendBanCard(obj.get("result").getAsJsonObject());
								} else {
									showInfo("调用失败");
								}
							} else if(obj.get("code").getAsInt() == 9999) {
								
								runOnUiThread(new Runnable() {
									public void run() {
										if (transDialog != null && transDialog.isShowing()) {
											transDialog.dismiss();
										}
										showDialog3();
									}
								});
							} else {
								showInfo(obj.get("desc").getAsString());
							}
						}
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}
						}
					});
				}
			}
		});

	}
	
	private void sendAliPay(JsonObject node) {
		String repInfo = node.get("payOrderResp").toString().replace("\\", "");
		String payInfo = repInfo.substring(1, repInfo.length() - 1);
		
		Intent intent = new Intent(PayActivity.this,AliPayActivity_.class);
		intent.putExtra("paySn", node.get("sendTn").getAsString());
		intent.putExtra("payInfo", payInfo);
		startActivityForResult(intent, 0);
	}
	
	private void sendBanCard(JsonObject node) {
		Intent intent = new Intent(PayActivity.this, PaySuccessActivity_.class);
        intent.putExtra("desc", "支付成功");
        PayActivity.this.startActivity(intent);
		PayActivity.this.finish();
	}
	
	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("请先通过银行卡认证！");
		builder.setPositiveButton("去认证", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(PayActivity.this,MyBankCardActivity_.class);
				startActivity(intent);
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
	
//	private void showDialog2() {
//		CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
//		builder.setTitle(R.string.app_name);
//		builder.setMessage(NumReplaceUtil.newBankNum(GlobalPara.getOutUserBank().getBankNo()) + "\n是否同意以代扣方式支付");
//		builder.setPositiveButton("同意", new android.content.DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				start(BANKCARD_PAY);
//				dialog.dismiss();
//			}
//		});
//
//		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}
	
	private void showDialog3() {
		CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("您绑定的银行卡未签约快捷支付，是否签约？");
		builder.setPositiveButton("签约", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				initQuickPayCode();
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
	
	private void showDialog4(final String sn) {
		runOnUiThread(new Runnable() {
			public void run() {
				final CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
				builder.setTitle(R.string.app_name);
				builder.setMessage("请输入短信验证码");
				builder.setEtMessageHint1("请输入短信验证码");
				builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String smsCode = builder.getEtMessage1();
						if (smsCode != null && smsCode.length() > 0) {
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}

							initQuickCode(sn,smsCode);
							dialog.dismiss();
						} else {
							showInfo("输入的验证码不能为空");
						}
					}
				});
				
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (transDialog != null && transDialog.isShowing()) {
							transDialog.dismiss();
						}
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}
	
	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				if (transDialog != null && transDialog.isShowing()) {
					transDialog.dismiss();
				}
				Toast.makeText(PayActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
