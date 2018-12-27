package com.il360.shenghecar.activity.home;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.fragment.UserFragment;
import com.il360.shenghecar.model.MyInfo;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_myinfo)
public class MyInfoActivity extends BaseWidgetActivity {

    @ViewById
    TextView tvTextClick;

    @ViewById TextView etName;
    @ViewById TextView etCertificate;
    @ViewById TextView etEmergencyContact;
    @ViewById TextView etEmergencyPhone;
    @ViewById TextView etOperator;
    @ViewById TextView etOperatorPhone;//业务员联系号码
    @ViewById TextView tvCompany;

    private MyInfo response;

    @AfterViews
    void init() {
        if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {
            initViews();
        } else {
            initData();
        }
    }

    private void initData() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "goods/queryInfoCheck",params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(MyInfoActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                JSONObject objRes = obj.getJSONObject("result");
                                response = FastJsonUtils.getSingleBean(objRes.toString(), MyInfo.class);
                            } else{
                            }
                        }
                    } else {
                        showInfo(result.getResult());
                    }
                } catch (Exception e) {
                    Log.e("MyInfoActivity", "initData", e);
                    LogUmeng.reportError(MyInfoActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            initViews();
                        }
                    });
                }
            }
        });
    }

    private void initViews() {

            etName.setText("");
            etCertificate.setText("");
            etEmergencyContact.setText("");
            etEmergencyPhone.setText("");
            etOperator.setText("");
            etOperatorPhone.setText("");
            tvCompany.setText("");
          //  tvSubmit.setVisibility(View.VISIBLE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserFragment.CODE_SECCESS) {
            initData();
        }
    }

//	@Click
//	void tvSubmit(){
//		tvSubmit.setClickable(false);
//		unBindingData();
//	}

    private void showInfo(final String info) {

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MyInfoActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
