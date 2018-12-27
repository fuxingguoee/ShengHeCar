package com.il360.shenghecar.activity.coupon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.RankAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.coupon.AgentUser;
import com.il360.shenghecar.model.coupon.ArrayOfAgentUser;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.ListViewForScrollView;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_rank_list)
public class RankListActivity extends BaseWidgetActivity {
	
	@ViewById TextView tvNumber;
	@ViewById TextView tvRanking;
	@ViewById TextView tvAmount;
	
	@ViewById ListViewForScrollView rankingList;
	
	
	private List<AgentUser> list = new ArrayList<AgentUser>();
	private RankAdapter adapter;
	
	private ArrayOfAgentUser arrayOfAgentUser;
	
	@AfterViews
	void init() {
		initRankList();
	}


	private void initRankList() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"card/queryRecommendList", params);
					if (result.getSuccess()) {
						if (ResultUtil.isOutTime(result.getResult()) != null) {
							showInfo(ResultUtil.isOutTime(result.getResult()));
							Intent intent = new Intent(RankListActivity.this, LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								arrayOfAgentUser = FastJsonUtils.getSingleBean(objRes.toString(), ArrayOfAgentUser.class);
							} else {
								showInfo(obj.getString("desc"));
							}
						}
					}
				} catch (Exception e) {
					Log.e("RankListActivity", "initRankList", e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if(arrayOfAgentUser != null){
								initViews();
							}
						}
					});
				}
			}
		});
	}
	
	
	private void initViews() {
		if(arrayOfAgentUser.getAgentUser() != null){
			tvNumber.setText(arrayOfAgentUser.getAgentUser().getNumber().toString());
			tvRanking.setText(arrayOfAgentUser.getAgentUser().getRank().toString());
			tvAmount.setText(arrayOfAgentUser.getAgentUser().getInvitationAmount().toString());
		}
		
		if(arrayOfAgentUser.getList() != null && arrayOfAgentUser.getList().size() > 0){
			list.clear();
			list.addAll(arrayOfAgentUser.getList());
			adapter = new RankAdapter(list, RankListActivity.this);
			rankingList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}
	
	private void showInfo(final String info) {

		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(RankListActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
