package com.il360.shenghecar.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.FundCityAdapter;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.model.home.FundCity;
import com.il360.shenghecar.model.home.FundProvince;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.act_fund_city)
public class FundCityActivity extends BaseWidgetActivity {
	@ViewById
	ListView lvCity;
	
	private List<FundCity> list = new ArrayList<FundCity>();
	private FundCityAdapter adapter;
	
	private String cityCode,cityName;
	
	@Extra
	FundProvince cityList;

	@AfterViews
	void init() {
		list = cityList.getCityList();
		adapter = new FundCityAdapter(list, FundCityActivity.this);
		lvCity.setAdapter(adapter);
		lvCity.setOnItemClickListener(new OnItemClickListener());
	}
	
	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			cityCode = list.get(position).getCityCode();
			cityName = list.get(position).getCityName();
			Intent intent = new Intent();
			Bundle b = new Bundle();
			b.putString("cityCode", cityCode);
			b.putString("cityName", cityName);
			intent.putExtras(b);
			setResult(Variables.ADDRESS_STATUS_CODE_SECCESS, intent);
			FundCityActivity.this.finish();
		}
	}
}
