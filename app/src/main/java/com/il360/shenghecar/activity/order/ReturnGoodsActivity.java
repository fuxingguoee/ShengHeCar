package com.il360.shenghecar.activity.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.WelcomeActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.Order;
import com.il360.shenghecar.util.FileUtil;
import com.il360.shenghecar.util.PicUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.act_return_goods)
public class ReturnGoodsActivity extends BaseWidgetActivity {

	@ViewById TextView tvMoney;
	@ViewById TextView tvDays;
	@ViewById TextView tvOverdueRate;

	@ViewById TextView tvRate;
	@ViewById TextView tvInterest;
	@ViewById TextView tvPrincipal;

	@ViewById CheckBox cbAgree;
	@ViewById TextView tvRule;

	@ViewById LinearLayout llSignature;
	@ViewById ImageView ivSignature;
	@ViewById RelativeLayout rlSignature;

	@ViewById TextView tvRefund;//退款申请

	private String signNamePic = null;
	private String cosPath = "";
	private String srcPath = "";
	private String sourceURL = "";
	protected ProgressDialog transDialog;
	double orderPrice = 0.00;

	DecimalFormat df = new DecimalFormat("0.00");

	private double rate = 0.00;

	@Extra Order myOrder;

	@AfterViews
	void init() {
		initViews();
	}

	private void initViews() {
		for (int i = 0; i < GlobalPara.getCardConfigList().size(); i++) {
			if(GlobalPara.getCardConfigList().get(i).getConfigGroup().equals("rate")
					&& GlobalPara.getCardConfigList().get(i).getConfigName().equals("buyRate")) {
				rate = Double.valueOf(GlobalPara.getCardConfigList().get(i).getConfigValue()) / 100;
				tvRate.setText("代收利息(" + GlobalPara.getCardConfigList().get(i).getConfigValue() +"%)");
			} else if(GlobalPara.getCardConfigList().get(i).getConfigGroup().equals("penaltyfee")
					&& GlobalPara.getCardConfigList().get(i).getConfigName().equals("penaltyfee1")) {
				tvOverdueRate.setText(GlobalPara.getCardConfigList().get(i).getConfigValue() +"%");
			} else if(GlobalPara.getCardConfigList().get(i).getConfigGroup().equals("day")
					&& GlobalPara.getCardConfigList().get(i).getConfigName().equals("applyDay")){
				tvDays.setText(GlobalPara.getCardConfigList().get(i).getConfigValue());
			}
		}

		orderPrice = myOrder.getGoodsPrice().doubleValue() * myOrder.getOrderNum();
		double interest = orderPrice * rate;
		double principal = orderPrice - interest;

		tvMoney.setText(df.format(orderPrice));
		tvInterest.setText("￥" + df.format(interest));
		tvPrincipal.setText("￥" + df.format(principal));

		String str = "<font color='#666666'>我已阅读并同意</font>"
                + "<font color= '#00A0E9'>"+"《" +  ReturnGoodsActivity.this.getResources().getString(R.string.deal_rule) + "》"+"</font>";
		tvRule.setText(Html.fromHtml(str));
	}


	@Click
	void rlSignature() {
//		Intent intent = new Intent(PlaceOrderActivity.this,DealRuleActivity_.class);
		/*Intent intent = new Intent(PlaceOrderActivity.this,DaiKouRuleActivity_.class);
		intent.putExtra("allNumber", list.get(myPosition).getStagesNumber() + "");
		intent.putExtra("type", type);
		intent.putExtra("principal", df.format(principal));
		intent.putExtra("periodsMoney", df.format(periodsMoney));
		startActivityForResult(intent, 0);*/

		Intent intent = new Intent(ReturnGoodsActivity.this, DealRuleActivity_.class);
		intent.putExtra("orderNo", myOrder.getOrderNo());
		intent.putExtra("principal", df.format(orderPrice)+ "");
		startActivityForResult(intent, 0);
	}

	@Click
	void tvRule() {
		Intent intent = new Intent(ReturnGoodsActivity.this, DealRuleActivity_.class);
		intent.putExtra("orderNo", myOrder.getOrderNo());
		intent.putExtra("principal", df.format(orderPrice) + "");
		startActivityForResult(intent, 0);
	}

	@Click
	void tvRefund() {//订单申请
		if(isOk()) {
			tvRefund.setClickable(false);
			transDialog = ProgressDialog.show(ReturnGoodsActivity.this, null, "提交中...", true);
			initSign();
		}
	}

	private void initReturnGoods() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("orderNo", myOrder.getOrderNo());
					params.put("orderSign", sourceURL);
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"order/applyReturn", params);

					if (result != null && result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							showInfo("提交成功");
							ReturnGoodsActivity.this.setResult(1001);
							ReturnGoodsActivity.this.finish();
						}
					}
				} catch (Exception e) {
					Log.e("ReturnGoodsActivity", "dealOrder", e);
					LogUmeng.reportError(ReturnGoodsActivity.this, e);
				}
			}
		});
	}

	private boolean isOk() {
 		if (signNamePic == null) {
			showInfo("请签署电子合同");
		} else if (!cbAgree.isChecked()) {
			showInfo("请阅读并同意《" +  ReturnGoodsActivity.this.getResources().getString(R.string.deal_rule) + "》");
		} else {
			return true;
		}
		return false;
	}

	protected void initSign() {

		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {

				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("type", "5");// 3身份证，5签名，6头像
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "card/querySign",params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(ReturnGoodsActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								upSignPic(obj.getString("result"));
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("ReturnGoodsActivity", "initSign()", e);
					LogUmeng.reportError(ReturnGoodsActivity.this, e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}
							tvRefund.setClickable(true);
						}
					});
				}
			}
		});
	}

	private void upSignPic(final String sign) {
		PutObjectRequest putObjectRequest = new PutObjectRequest();
		putObjectRequest.setBucket(GlobalPara.getCosName());

		cosPath = "/sign/" + FileUtil.cosFileName("userSign", UserUtil.getUserInfo().getUserId()) + ".png";
		putObjectRequest.setCosPath(cosPath);
		putObjectRequest.setSrcPath(srcPath);
		putObjectRequest.setSign(sign);
		putObjectRequest.setInsertOnly("1");// 0允许覆盖，1不允许覆盖

		putObjectRequest.setListener(new IUploadTaskListener() {
			@Override
			public void onSuccess(COSRequest cosRequest, COSResult cosResult) {

				PutObjectResult result = (PutObjectResult) cosResult;
				if (result.code == 0) {
					sourceURL = result.source_url;
					// PicUtil.deleteTempFile(srcPath);
					initReturnGoods();
				}
			}

			@Override
			public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
				sourceURL = "";
				showInfo("签名上传失败");
			}

			@Override
			public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
			}

			@Override
			public void onCancel(COSRequest arg0, COSResult arg1) {
			}
		});
		WelcomeActivity.getCOSClient().putObject(putObjectRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_CANCELED && data != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Bitmap signBitmap = PicUtil.getSmallBitmap(data.getExtras().getString("signNamePic"));
						ivSignature.setImageBitmap(signBitmap);
						signNamePic = PicUtil.bitmapToString(signBitmap, 30);
						srcPath = data.getExtras().getString("signNamePic");
						cbAgree.setChecked(true);
					} catch (Exception e) {
						Log.e("ReturnGoodsActivity", "onActivityResult", e);
					}
				}
			});
		}
	}
	
	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				if (transDialog != null && transDialog.isShowing()) {
					transDialog.dismiss();
				}
				Toast.makeText(ReturnGoodsActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
