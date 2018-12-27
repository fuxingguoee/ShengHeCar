package com.il360.shenghecar.activity.order;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.recovery.UserRecovery;
import com.il360.shenghecar.util.DataUtil;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_details_recovery)
public class DetailsRecoveryOrderActivity extends BaseWidgetActivity {

	@ViewById TextView tvOrderNo;
	@ViewById RelativeLayout rlOrderStatus;
	@ViewById TextView tvOrderStatus;
	@ViewById TextView tvOrderTime;
	@ViewById TextView tvPhoneName;
	@ViewById TextView tvAssessDetails;
	@ViewById TextView tvAssessPrice;
	@ViewById TextView tvRecoveryType;
	@ViewById TextView tvLogistics;
	@ViewById TextView tvCancelOrder;
	@ViewById ImageView ivOrderPic;
	
	@ViewById LinearLayout llReturnPhone;
	@ViewById TextView tvReturnAdd;
	@ViewById TextView tvReturnLogistics;
	
	@Extra UserRecovery userRecovery;
	
	@AfterViews
	void init() {
		if(userRecovery != null){
			initViews();
			initBuutons();
		} else {
			showInfo("内部传参错误！");
		}
	}

	private void initViews() {
		
		if(userRecovery.getRecoveryPic() != null){
			ImageFromTxyUtil.loadImage(DetailsRecoveryOrderActivity.this, userRecovery.getRecoveryPic(), ivOrderPic);
		} else {
			ivOrderPic.setBackgroundResource(R.drawable.ic_phone2);
		}
		
		tvOrderNo.setText(userRecovery.getOrderNo());
		tvOrderStatus.setText(userRecovery.getStatusDesc());
		tvOrderTime.setText(DataUtil.getLongToDate(Long.valueOf(userRecovery.getCreateTime())));
		tvPhoneName.setText(userRecovery.getPhoneName());
		tvAssessDetails.setText(userRecovery.getAssessmentDetails());
		tvAssessPrice.setText("评估价格：￥" + userRecovery.getAmount());
		tvRecoveryType.setText(userRecovery.getRecoveryType());
		
		
		if(userRecovery.getExpressCompany() != null && userRecovery.getExpressNo() != null) {
			tvLogistics.setText(userRecovery.getExpressCompany() + " " + userRecovery.getExpressNo());
		} else {
			tvLogistics.setText("暂无");
		}
		
		if(userRecovery.getStatus() == 5) {
			llReturnPhone.setVisibility(View.VISIBLE);
			tvReturnAdd.setText("暂无");
			if(userRecovery.getExpressCompany2() != null && userRecovery.getExpressNo2() != null) {
				tvReturnLogistics.setText(userRecovery.getExpressCompany2() + " " + userRecovery.getExpressNo2());
			} else {
				tvReturnLogistics.setText("暂无");
			}
		} else {
			llReturnPhone.setVisibility(View.GONE);
		}
	}
	
	private void initBuutons() {
		if (userRecovery.getStatus() == 0) {
			tvCancelOrder.setVisibility(View.VISIBLE);
		} else {
			tvCancelOrder.setVisibility(View.INVISIBLE);
		}
	}
	
	@Click
	void rlOrderStatus(){
		Intent intent = new Intent(DetailsRecoveryOrderActivity.this, OrderSatusActivity_.class);
		intent.putExtra("type", "2");
		intent.putExtra("orderNo", userRecovery.getOrderNo());
		startActivity(intent);
	}
	
	@Click
	void tvCancelOrder(){
		showDialog();
	}
	
	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(DetailsRecoveryOrderActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("确定要取消该笔订单吗？");
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				initCancelOrder();
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
	
	protected void initCancelOrder() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("orderNo", userRecovery.getOrderNo());
					params.put("token", UserUtil.getToken());
					params.put("status", "-1");
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"recovery/updateUserRecovery", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(DetailsRecoveryOrderActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								showInfo("操作成功");
								setResult(101);
								DetailsRecoveryOrderActivity.this.finish();
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("DetailsRecoveryOrder", "initCancelOrder", e);
					LogUmeng.reportError(DetailsRecoveryOrderActivity.this, e);
				}
			}
		});
	}

	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				
				Toast.makeText(DetailsRecoveryOrderActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
