package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.adapter.CompanyAdapter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.Branch;
import com.il360.shenghecar.model.Company;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.UserUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.act_company)
public class CompanyActivity extends BaseWidgetActivity {//参考快递列表

    @ViewById
    ListView companyList;
    private List<Company> list = new ArrayList<Company>();
    protected ProgressDialog transDialog;
    private CompanyAdapter adapter;
    private String branchName;//名称
    private Integer branchId;//编号


        @AfterViews
        void init() {
            transDialog = ProgressDialog.show(CompanyActivity.this, null, "请稍等...", true);
            ExecuteTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        initData();
                    } catch (Exception e) {
                        Log.e(TAG, "init", e);
                        LogUmeng.reportError(CompanyActivity.this, e);
                        showInfo(getString(R.string.A7));
                    }
                }
            });
        }
    /**
     *
     */
        private void initData() {
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", UserUtil.getToken());//
                TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "goods/queryBranchList",
                        params);
                if (result.getSuccess()) {
                    Branch response = FastJsonUtils.getSingleBean(result.getResult(), Branch.class);
                    if (response.getCode() == 1) {
                        if(response.getResult() != null && response.getResult().size() > 0){
                            list = response.getResult();
                        } else {
                            showInfo(getResources().getString(R.string.no_data));
                        }
                    } else {
                        showInfo(response.getDesc());
                    }
                } else {
                    showInfo(result.getResult());
                }
            } catch (Exception e) {
                LogUmeng.reportError(CompanyActivity.this, e);
                showInfo(getString(R.string.A7));
            } finally {
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter = new CompanyAdapter(list, CompanyActivity.this);
                        companyList.setAdapter(adapter);
                        companyList.setOnItemClickListener(new OnItemClickListener());

                        if (transDialog != null && transDialog.isShowing()) {
                            transDialog.dismiss();
                        }
                    }
                });
            }
        }

    class OnItemClickListener implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            branchName = list.get(position).getBranchName();//分公司名称
            branchId   = list.get(position).getBranchId();  //分公司id
            Intent intent = new Intent();

            Bundle b = new Bundle();
            b.putString("branchName", branchName);//String
            b.putInt("branchId", branchId);//Integer
            intent.putExtras(b);
            setResult(101, intent);//结果码
            finish();
        }
    }
        private void showInfo(final String info) {


            runOnUiThread(new Runnable() {
                public void run() {
                    if (transDialog != null && transDialog.isShowing()) {
                        transDialog.dismiss();
                    }
                    Toast.makeText(CompanyActivity.this, info, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
