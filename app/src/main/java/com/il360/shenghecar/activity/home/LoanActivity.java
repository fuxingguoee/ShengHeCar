package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.address.ProvinceActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.Loan;
import com.il360.shenghecar.model.address.UserAddress;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SIMCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.view.CustomDatePicker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.act_loan)
public class LoanActivity extends BaseWidgetActivity {
    @ViewById
    EditText etName;//类似退货申请
    @ViewById
    EditText etPhone;//电话
    @ViewById
    EditText etPrice;//车价
    @ViewById
    EditText etKm;//公里数
    @ViewById
    EditText etModel;//车型
    @ViewById
    EditText etAmount;//贷款金额
    @ViewById
    EditText etTime;//年份

    @ViewById //请选择上牌地
            RelativeLayout rlAddress;
    @ViewById
    TextView tvAddress;

    private int status = 1;
    private static Timer timer = null;
    private Loan loan;
    @Extra
    UserAddress userAddress;
    private String myProvince;
    private String myCity;
    private String myArea;
    @ViewById
    RadioButton rbCheckOK;//新车
    @ViewById
    RadioButton rbCheckNO;//二手车
    @ViewById
    TextView tvOrderNo;

    @ViewById
    TextView tvSubmit;

    private TextView currentDate, currentTime;
    protected ProgressDialog transDialog;
    private CustomDatePicker customDatePicker1, customDatePicker2;

    @AfterViews
    void init() {
        if (userAddress != null && userAddress.getAddressId() != null) {
            myProvince = userAddress.getProvince() != null ? userAddress.getProvince() : "";
            myCity = userAddress.getCity() != null ? userAddress.getCity() : "";
            myArea = userAddress.getArea() != null ? userAddress.getArea() : "";
            tvAddress.setText(myProvince + " " + myCity + " " + myArea + " ");

            if (userAddress.getIsDefault() != null && userAddress.getIsDefault() != 1) {
                // rbCheckNO.setChecked(true);
                // rbCheckOK.setChecked(false);
            } else {
                //rbCheckOK.setChecked(true);
                // rbCheckNO.setChecked(false);
            }
        } else {
        }
    }

    @Click
    void rlAddress() {
        Intent intent = new Intent(LoanActivity.this, ProvinceActivity_.class);
        startActivityForResult(intent, 0);
    }

    @Click
    void tvSubmit() {
        tvSubmit.setClickable(false);
        if (isOk()) {
            transDialog = ProgressDialog.show(LoanActivity.this, null, "提交中...", true);
            initLoan();
            initData();
        } else {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            tvSubmit.setClickable(true);
                        }
                    });
                }
            };
            timer.schedule(task, 1000 * 1);
        }
    }

    private void initLoan() {//贷款申请接口
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("loanApplyJson", makeJson());
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "goods/upLoanApply", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {//token参数验证超时
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(LoanActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo("提交成功！");
                                setResult(101);
                                LoanActivity.this.finish();//页面结束
                            } else {
                                showInfo(obj.getString("desc"));
                            }
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("LoanActivity", "initLoan()", e);
                    LogUmeng.reportError(LoanActivity.this, e);
                    showInfo(getString(R.string.A2));
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (transDialog != null && transDialog.isShowing()) {
                                transDialog.dismiss();
                            }
                            tvSubmit.setClickable(true);
                        }
                    });
                }
            }
        });
    }

    private String makeJson() {//实体类字段名同步后台，否则后台没有数据
        loan = new Loan();
        loan.setUserId(UserUtil.getUserInfo().getUserId());
        loan.setPrice(ViewUtil.getText(etPrice));
        loan.setKilometers(ViewUtil.getText(etKm));
        loan.setCarModel(ViewUtil.getText(etModel));
        loan.setTime(ViewUtil.getText(etTime));
        loan.setStatus(status);
        loan.setAmount(ViewUtil.getText(etAmount));
        if (rbCheckOK.isChecked()) {//新车
            loan.setType(1);
        } else {//二手车
            loan.setType(2);
        }
        loan.setName(ViewUtil.getText(etName));
        loan.setPhone(ViewUtil.getText(etPhone));
        loan.setLocation(ViewUtil.getText(tvAddress));
        return FastJsonUtils.getJsonString(loan);
    }

    private void initData() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    params.put("addressJson", makeJsonPost());
                    TResult<Boolean, String> result;
                    if (userAddress != null && userAddress.getAddressId() != null) {
                        result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "user/updateUserAddress", params);
                    } else {
                        result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "user/addUserAddress", params);
                    }
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(LoanActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo(obj.getString("desc"));
                                setResult(101);
                                LoanActivity.this.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("LoanActivity", "initData", e);
                    LogUmeng.reportError(LoanActivity.this, e);
                }
            }
        });
    }

    private boolean isOk() {
        if (TextUtils.isEmpty(ViewUtil.getText(etName))) {
            showInfo("姓名不能为空！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etPhone))) {
            showInfo("电话不能为空！");
        } else if (!SIMCardUtil.isMobileNo(ViewUtil.getText(etPhone))) {
            showInfo("手机号码格式有误！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etModel))) {
            showInfo("车型不能为空");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etTime))) {
            showInfo("年份不能为空");
        } else if ((ViewUtil.getText(etTime).length() != 4)) {
            showInfo("年份格式有误");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etPrice))) {
            showInfo("车价不能为空");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etKm))) {
            showInfo("公里数不能为空");
        } else if (TextUtils.isEmpty(ViewUtil.getText(tvAddress))) {
            showInfo("请选择上牌地！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etAmount))) {
            showInfo("贷款金额不为空");
        } else {
            return true;
        }
        return false;
    }

    /**
     * 生成注册POST信息
     *
     * @return String userJson
     */
    private String makeJsonPost() {
        UserAddress myUserAddress = new UserAddress();
        if (userAddress != null && userAddress.getAddressId() != null) {
            myUserAddress.setAddressId(userAddress.getAddressId());
        }
        myUserAddress.setProvince(myProvince);
        myUserAddress.setCity(myCity);
        myUserAddress.setArea(myArea);
        myUserAddress.setAddress(ViewUtil.getText(tvAddress));
        myUserAddress.setName(ViewUtil.getText(etName));
        myUserAddress.setPhone(ViewUtil.getText(etPhone));
        myUserAddress.setStatus(status);
        myUserAddress.setUserId(UserUtil.getUserInfo().getUserId());
        return FastJsonUtils.getJsonString(myUserAddress);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {//上牌地
        if (resultCode == Variables.ADDRESS_STATUS_CODE_SECCESS) {
            Bundle b = data.getExtras();//存值
            if (b != null) {
                myProvince = b.getString("province");
                myCity = b.getString("city");
                myArea = b.getString("area");
                tvAddress.setText(myProvince + " " + myCity + " " + myArea + " ");//赋值
            }
        }
    }

    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(LoanActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
