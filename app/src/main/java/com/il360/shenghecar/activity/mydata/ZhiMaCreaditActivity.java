package com.il360.shenghecar.activity.mydata;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.moblie.zmxy.antgroup.creditsdk.app.CreditApp;
import com.android.moblie.zmxy.antgroup.creditsdk.app.ICreditListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.alipay.ZmxySign;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.alipay.CreditAuthHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

@EActivity(R.layout.act_zhima_creadit)
public class ZhiMaCreaditActivity extends BaseWidgetActivity {
	
	protected ProgressDialog transDialog;
	
	@AfterViews
	void init() {
		transDialog = ProgressDialog.show(ZhiMaCreaditActivity.this, null,"正在认证中...", true);
		submitPhone();
	}

	private void submitPhone() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("model", makeJson());
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "zmxy/rsajiami",
							params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(ZhiMaCreaditActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							JSONObject objRes = obj.getJSONObject("result");
							if (obj.getInt("code") == 1) {
								// 去认证
								ZmxySign zmxySign = FastJsonUtils.getSingleBean(objRes.toString(), ZmxySign.class);
								doCreditRequest(zmxySign.getAppId(), zmxySign.getParams(), zmxySign.getSign());
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(result.getResult());
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				}
			}
		});
	}

	private String makeJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("certNo", GlobalPara.getOutUserRz().getIdNo());
			json.put("certType", "IDENTITY_CARD");
			json.put("name", GlobalPara.getOutUserRz().getName());
			return json.toString();
		} catch (Exception e) {
			Log.e("ZhiMaCreaditActivity", "makeJson", e);
		}
		return null;
	}

	private void doCreditRequest(String appId, String params, String sign) {
		// 测试数据，此部分数据，请由商户服务端生成下发，具体见开放平台商户对接文档
		// 请注意params、sign为encode过后的数据
		// extParams参数可以放置一些额外的参数，例如当biz_params参数忘记组织auth_code参数时，可以通过extParams参数带入auth_code。
		// 不过建议auth_code参数组织到biz_params里面进行加密加签。
		Map<String, String> extParams = new HashMap<>();
		// extParams.put("auth_code", "M_FACE");

		try {
			// 请求授权
			CreditAuthHelper.creditAuth(ZhiMaCreaditActivity.this, appId, params, sign, extParams,
					new ICreditListener() {
						@Override
						public void onComplete(Bundle result) {
							// toast message
//							showInfo("授权成功");
							// 从result中获取params参数,然后解析params数据,可以获取open_id。
							if (result != null) {
								submitResult(result.getString("params"), result.getString("sign"));
							}
						}

						@Override
						public void onError(Bundle result) {
							// toast message
							showInfo("授权错误");
							Log.d(TAG, "DemoPresenterImpl.doCreditAuthRequest.onError.");
						}

						@Override
						public void onCancel() {
							// toast message

							showInfo("授权失败");
							Log.d(TAG, "DemoPresenterImpl.doCreditAuthRequest.onCancel.");
						}
					});
		} catch (Exception e) {
			Log.e(TAG, "DemoPresenterImpl.doCreditAuthRequest.exception=" + e.toString());
		}
	}

	private void submitResult(final String paramsStr, final String signStr) {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("params", paramsStr);
					params.put("sign", signStr);
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "zmxy/rsajiemi",
							params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(ZhiMaCreaditActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								// 认证完成
								showInfo("授权成功");
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(result.getResult());
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				} finally {
					if (transDialog != null && transDialog.isShowing()) {
						transDialog.dismiss();
					}
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
				Toast.makeText(ZhiMaCreaditActivity.this, info, Toast.LENGTH_SHORT).show();
				ZhiMaCreaditActivity.this.finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "DemoActivity.onActivityResult");
		// onActivityResult callback
		CreditApp.onActivityResult(requestCode, resultCode, data);
	}
}
