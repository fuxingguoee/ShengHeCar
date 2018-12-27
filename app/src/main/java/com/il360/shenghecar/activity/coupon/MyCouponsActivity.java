package com.il360.shenghecar.activity.coupon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.goods.IPhoneActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.CouponAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.coupon.ArrayOfCoupon;
import com.il360.shenghecar.model.coupon.UserCoupon;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_my_coupon)
public class MyCouponsActivity extends BaseWidgetActivity {
	@ViewById ListView lvCoupon;

	private List<UserCoupon> list = new ArrayList<UserCoupon>();
	private CouponAdapter adapter;
	
	protected ProgressDialog transDialog;
	
	@AfterViews
	void init() {
		initList();
	}
	
	
	private void initList() {
		transDialog = ProgressDialog.show(MyCouponsActivity.this, null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("status", "1");
					params.put("useScope", "1");//1全额2分期
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"coupon/queryUserCoupon", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(MyCouponsActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							list.clear();
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								JSONObject objResRes = objRes.getJSONObject("returnResult");
								ArrayOfCoupon response = FastJsonUtils.getSingleBean(objResRes.toString(),ArrayOfCoupon.class);
								if(response!= null && response.getTotalCount() > 0){
									list.addAll(response.getList());
								} else {
									showInfo(obj.getString("暂无数据"));
								}
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("CityActivity", "initList", e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							
							initViews();
							
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}
						}
					});
				}
			}
		});
	}
	
	private void initViews() {
		adapter = new CouponAdapter(list, MyCouponsActivity.this);
		lvCoupon.setAdapter(adapter);
		lvCoupon.setOnItemClickListener(new OnItemClickListener());
	}
	
	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(MyCouponsActivity.this, IPhoneActivity_.class);
			startActivity(intent);
		}
	}
	
	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(transDialog!=null && transDialog.isShowing()){
					transDialog.dismiss();
				}
				Toast.makeText(MyCouponsActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
