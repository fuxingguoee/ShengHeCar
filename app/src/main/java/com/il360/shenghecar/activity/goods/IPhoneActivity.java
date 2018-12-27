package com.il360.shenghecar.activity.goods;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.GoodsAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.goods.ArrayOfGoods;
import com.il360.shenghecar.model.goods.Goods;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.view.MyGridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_iphone)
public class IPhoneActivity extends BaseWidgetActivity {

	@ViewById TextView tvActionTitle;//标题栏
	@ViewById PullToRefreshScrollView pull_refresh_scrollview;//下拉刷新
	@ViewById MyGridView gvIPhone;

	@Extra Integer ext1;

	private GoodsAdapter adapter;
	private List<Goods> list = new ArrayList<Goods>();
	protected ProgressDialog transDialog;

	@AfterViews
	void init() {
		initData();
		if (ext1 == 1) {
			tvActionTitle.setText("二手车列表");
		} else if(ext1 == 0) {
			tvActionTitle.setText("商品列表");
		}

		initViews();
		initRefreshListener();
	}

	private void initRefreshListener() {
		pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//下拉刷新
		pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				gvIPhone.postDelayed(new Runnable() {
					@Override
					public void run() {

						initData();
					}
				}, 1000);
			}
		});

	}

	private void initViews() {//商品显示
		adapter = new GoodsAdapter(list, IPhoneActivity.this);
		gvIPhone.setAdapter(adapter);
		gvIPhone.setOnItemClickListener(new OnItemClickListener());
		adapter.notifyDataSetChanged();
	}

	class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(IPhoneActivity.this,GoodsDetailsActivity_.class);
			intent.putExtra("goods", list.get(position));//按索引取值
			startActivity(intent);
		}
	}

	private void initData() {
		transDialog = ProgressDialog.show(IPhoneActivity.this, null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("goodsJson", makeJson());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"goods/queryGoodsList", params);//端口+地址
					if (result.getSuccess()) {
						ArrayOfGoods response = FastJsonUtils.getSingleBean(result.getResult(),ArrayOfGoods.class);
						if(response.getCode() == 1) {   //成功
							if(response.getResult() != null && response.getResult().size() > 0) {
								list.clear();   //<区分二手车/商品>调用clear方法清空集合中的所有数据
								//list.addAll(response.getResult());
								for (int i = 0; i < response.getResult().size(); i++) {
									if (response.getResult().get(i).getExt1() == ext1)
										list.add(response.getResult().get(i));
									}
							} else {
								list.clear();
								showInfo("暂无数据");
							}

						} else {
							list.clear();
							showInfo(response.getDesc());
						}

					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getString(R.string.A2));
					Log.e("IPhoneActivity", "initData", e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}
							adapter.notifyDataSetChanged();
							pull_refresh_scrollview.onRefreshComplete();//刷新完成
						}
					});
				}
			}
		});
	}

	private String makeJson() {
		Goods goods = new Goods();
		goods.setExt1(ext1);
		goods.setIsShow(1);
		return FastJsonUtils.getJsonString(goods);
	}

	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(transDialog!=null && transDialog.isShowing()){
					transDialog.dismiss();//对话框消失
				}
				Toast.makeText(IPhoneActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
