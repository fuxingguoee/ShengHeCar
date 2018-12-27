package com.il360.shenghecar.activity.address;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.AddressAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.address.Address;
import com.il360.shenghecar.model.address.ArrayOfAddress;
import com.il360.shenghecar.util.FastJsonUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_address)
public class ProvinceActivity extends BaseWidgetActivity{
	
	@ViewById ListView lvAddress;
	
	private List<Address> list = new ArrayList<Address>();
	private AddressAdapter adapter;
	
	protected ProgressDialog transDialog;
	
	private String province;
	
	@AfterViews
	void init(){
		initList();
	}

	private void initList() {
		transDialog = ProgressDialog.show(ProvinceActivity.this, null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"goods/querypcaList", params);
					if (result.getSuccess()) {
						ArrayOfAddress arrayOfAddress = FastJsonUtils.getSingleBean(result.getResult(), ArrayOfAddress.class);
						if (arrayOfAddress.getCode() == 1) {
							list.clear();
							list.addAll(arrayOfAddress.getResult());
						} else {
							showInfo(arrayOfAddress.getDesc());
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("ProvinceActivity", "initList", e);
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
		adapter = new AddressAdapter(list, ProvinceActivity.this, 1);
		lvAddress.setAdapter(adapter);
		lvAddress.setOnItemClickListener(new OnItemClickListener());
	}
	
	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			province = list.get(position).getProvince();
			Intent intent = new Intent(ProvinceActivity.this,CityActivity_.class);
			intent.putExtra("province", province);
			startActivityForResult(intent, 0);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Variables.ADDRESS_STATUS_CODE_SECCESS){
			setResult(Variables.ADDRESS_STATUS_CODE_SECCESS,data);
			ProvinceActivity.this.finish();
		}
	}
	
	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(transDialog!=null && transDialog.isShowing()){
					transDialog.dismiss();
				}
				Toast.makeText(ProvinceActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
