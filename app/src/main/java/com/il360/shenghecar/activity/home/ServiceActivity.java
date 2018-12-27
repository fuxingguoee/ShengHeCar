package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.model.MyService;
import com.il360.shenghecar.util.IdCardUtil;
import com.il360.shenghecar.util.SharedPreferencesUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.util.alipay.HttpUtils;
import com.il360.shenghecar.view.CustomDialog1;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.act_service)
public class ServiceActivity extends BaseWidgetActivity {
    @ViewById
    EditText etFIdCard;
    @ViewById
    TextView tvSettle;//结清办理
    @ViewById TextView tvInsurance;//保险理赔

    protected ProgressDialog transDialog;
    @ViewById TextView tvUserName;
    @ViewById TextView tvLoanPrice;
    @ViewById TextView tvMonthPrice;
    @ViewById TextView tvLoanTime;
    @ViewById TextView tvQiShu;
    @ViewById TextView tvPayFee;
    @ViewById TextView tvCarInfo;
    @ViewById TextView tvReturnNo;
    @ViewById TextView tvSearch;
    @ViewById
    RelativeLayout rlServiceNum;//客服电话1
    @ViewById
    TextView tvEditServiceNum;
    @ViewById
    RelativeLayout rlServiceNum2;//客服电话2
    @ViewById
    TextView tvEditServiceNum2;
    private MyService myService;//实体类

    @AfterViews
    void init() {
        if (GlobalPara.getTelephone() != null) {
            String[] phoneArray = GlobalPara.getTelephone().split("/");
            tvEditServiceNum.setText(phoneArray[0]);
        }

        if(SharedPreferencesUtil.getUserIDNo() != null &&
                (SharedPreferencesUtil.getUserIDNo().length() == 18)
                ) {
            etFIdCard.setText(SharedPreferencesUtil.getUserIDNo());
            initService();//显示我的服务信息
        }
    }

    @Click
    void tvSearch() {
        if (isOk()) {//置空查询:点击查询相当于刷新
            tvUserName.setText("");
            tvLoanPrice.setText("");
            tvMonthPrice.setText("");
            tvLoanTime.setText("");
            tvQiShu.setText("");
            tvPayFee.setText("");
            tvCarInfo.setText("");
            tvReturnNo.setText("");
            initService();
        }
    }
    @Click
    void tvSettle() {
        showDialog();
    }

    @Click
    void tvInsurance() {
        showDialog();
    }

    private boolean isOk() {
        if (!IdCardUtil.isIdcard(ViewUtil.getText(etFIdCard))) {
            showInfo("请输入正确的身份证号");
        }
        else {
            return true;
        }
        return  false;
    }

    protected void initService() {//还款信息接口
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {//参考huabei
                try {
                    String url = "http://114.55.68.197:8090/CustApi.asmx/H5_CustPayInfo?FIdCard=" + etFIdCard.getText().toString();//String类型
                    String result = HttpUtils.get(url);
                    if (!TextUtils.isEmpty(result) && !result.equals("null")) {//判断不为空
                        JSONObject obj = new JSONObject(result);//string转json
                        String s = obj.getString("H5_CustInfo");//第一层
                        JSONObject objRes = new JSONObject(s);//string转json

                        SharedPreferencesUtil.setUserIDNo(etFIdCard.getText().toString());//获取身份证号

                        final  String FrustId = objRes.getString("fcustid");
                        final  String UserName = objRes.getString("客户姓名");
                        final  String FIdCard = objRes.getString("身份证号码");
                        final  String  LoanPrice= objRes.getString("贷款本金");
                        final  String MonthPrice = objRes.getString("月供");
                        final  String LoanTime = objRes.getString("贷款年限");
                        final  String QiShu = objRes.getString("剩余期数");
                        final  String CarInfo = objRes.getString("车辆信息");
                        final  String ReturnNo = objRes.getString("还款卡号");
                        final  String PayFee = objRes.getString("当欠金额");

                        runOnUiThread(new Runnable() {
                            public void run() {//相关参数显示
                                tvLoanPrice.setText(LoanPrice+"元");
                                tvReturnNo.setText(ReturnNo);
                                tvCarInfo.setText(CarInfo);
                                tvMonthPrice.setText(MonthPrice+"元");
                                tvLoanTime.setText(LoanTime);
                                tvQiShu.setText(QiShu);
                                tvPayFee.setText(PayFee+"元");
                                tvUserName.setText(UserName);
                            }
                        });
                    } else {
                        showInfo("查询失败");
                    }
                }
                catch (Exception e) {
                   // showInfo(getString(R.string.A6));
                }
            }
        });
    }
    public void showInfo(final String info) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ServiceActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {//联系客服
        CustomDialog1.Builder builder = new CustomDialog1.Builder(ServiceActivity.this);

        builder.setTitle("客服助手");
        builder.setMessage(GlobalPara.getTelephone());
        builder.setPositiveButton("联系客服", new android.content.DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {//先打电话后返回首页

                if(!TextUtils.isEmpty(ViewUtil.getText(tvEditServiceNum))) {
                    Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tvEditServiceNum.getText()));
                    startActivity(intent);
                    dialog.dismiss();//让对话框消失
                }
            }
        });
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}