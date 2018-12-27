package com.il360.shenghecar.activity.order;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.RecordAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.ArrayOfRecord;
import com.il360.shenghecar.model.order.Record;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_order_status)
public class OrderSatusActivity extends BaseWidgetActivity {

	@ViewById TextView tvOrderNo;
	
	@ViewById ListView myList;
	
	private List<Record> myRecord = new ArrayList<Record>();
	private RecordAdapter adapter;
	
	@Extra
	String type, orderNo;

	@AfterViews
	void init() {
		tvOrderNo.setText(orderNo);
		initData();
	}

	private void initData() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("type", type);//参数
					params.put("token", UserUtil.getToken());
					params.put("orderNo", orderNo);
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"order/queryRecordList", params);//端口加地址
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(OrderSatusActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							ArrayOfRecord arrayOfRecord = FastJsonUtils.getSingleBean(result.getResult(),ArrayOfRecord.class);
							if (arrayOfRecord.getCode() == 1) {
								if (arrayOfRecord.getResult() != null && arrayOfRecord.getResult().size() > 0) {
									myRecord.clear();
									for (int i = 0; i < arrayOfRecord.getResult().size(); i++) {
										myRecord.add(arrayOfRecord.getResult().get(arrayOfRecord.getResult().size() - i - 1));
									}
								}
							} else {
								showInfo(arrayOfRecord.getDesc());
							}
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("OrderSatusActivity", "initData", e);
					LogUmeng.reportError(OrderSatusActivity.this, e);
				} finally {

					runOnUiThread(new Runnable() {
						public void run() {
							if(myRecord != null && myRecord.size() > 0){
								adapter = new RecordAdapter(myRecord, OrderSatusActivity.this);
								myList.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							}
						}
					});

				}
			}
		});
	}
	
	
	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				
				Toast.makeText(OrderSatusActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
