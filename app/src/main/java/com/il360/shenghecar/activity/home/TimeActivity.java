package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.il360.shenghecar.R;
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
import com.il360.shenghecar.view.DateTimeDialogOnlyYMD;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.act_time)//日期选择器
public class TimeActivity extends AppCompatActivity implements DateTimeDialogOnlyYMD.MyOnDateSetListener{
    //Activity&&AppCompatActivity
    @ViewById
    RelativeLayout y;
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
    TextView tvTime;//年份

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
    TextView tvSubmit;

    protected ProgressDialog transDialog;//

    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYM;
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYMD;
    private DateTimeDialogOnlyYMD dateTimeDialogOnlyYear;
    //
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @AfterViews
    void init() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        dateTimeDialogOnlyYMD = new DateTimeDialogOnlyYMD(this, this, true, true, true);
        dateTimeDialogOnlyYM = new DateTimeDialogOnlyYMD(this, this, false, true, true);
        dateTimeDialogOnlyYear = new DateTimeDialogOnlyYMD(this, this, false, false, true);
        if (userAddress != null && userAddress.getAddressId() != null) {
            myProvince = userAddress.getProvince() != null ? userAddress.getProvince() : "";
            myCity = userAddress.getCity() != null ? userAddress.getCity() : "";
            myArea = userAddress.getArea() != null ? userAddress.getArea() : "";
            tvAddress.setText(myProvince + " " + myCity + " " + myArea + " ");
        }
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @Click
    void y() {
        showYear();
    }

    private void showYear() {//显示年
        dateTimeDialogOnlyYear.hideOrShow();
    }

    private void showYM() {//显示年+月
        dateTimeDialogOnlyYM.hideOrShow();
    }

    private void showYMD() {//显示年+月+日
        dateTimeDialogOnlyYMD.hideOrShow();
    }

    @Override
    public void onDateSet(Date date, int type) {//赋值
        String str = mFormatter.format(date);
        String[] s =  str.split("-");
        if (type ==1){
            tvTime.setText(s[0]);
        }else if (type ==2){
            tvTime.setText(s[0]+"-"+s[1]);
        }else if (type ==3){
            tvTime.setText(s[0]+"-"+s[1]+"-"+s[2]);
        }
    }
    @Click
    void rlAddress() {
        Intent intent = new Intent(TimeActivity.this, ProvinceActivity_.class);
        startActivityForResult(intent, 0);
    }

    @Click
    void tvSubmit() {
        tvSubmit.setClickable(false);
        if (isOk()) {
            transDialog = ProgressDialog.show(TimeActivity.this, null, "提交中...", true);
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
                            Intent intent = new Intent(TimeActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo("提交成功！");
                                setResult(101);
                                TimeActivity.this.finish();
                            } else {
                                showInfo(obj.getString("desc"));
                            }
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("TimeActivity", "initLoan()", e);
                    LogUmeng.reportError(TimeActivity.this, e);
                    showInfo(getString(R.string.A2));
                }finally {
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

    private String makeJson(){//实体类字段名同步后台，否则后台没有数据
        loan = new Loan();
        loan.setUserId(UserUtil.getUserInfo().getUserId());//需要登录
        loan.setPrice(ViewUtil.getText(etPrice));
        loan.setKilometers(ViewUtil.getText(etKm));
        loan.setCarModel(ViewUtil.getText(etModel));
        loan.setTime(ViewUtil.getText(tvTime));
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
                            Intent intent = new Intent(TimeActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo(obj.getString("desc"));
                                setResult(101);
                                TimeActivity.this.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("TimeActivity", "initData", e);
                    LogUmeng.reportError(TimeActivity.this, e);
                }
            }
        });
    }

    private boolean isOk() {
        if         (TextUtils.isEmpty(ViewUtil.getText(etName))) {
            showInfo("姓名不能为空！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etPhone))) {
            showInfo("电话不能为空！");
        } else if (!SIMCardUtil.isMobileNo(ViewUtil.getText(etPhone))) {
            showInfo("手机号码格式有误！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etModel))) {
            showInfo("车型不能为空");
        } else if (TextUtils.isEmpty(ViewUtil.getText(tvTime))) {
            showInfo("年份不能为空");
        } else if ( (ViewUtil.getText(tvTime).length()!=4) ){
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
            Bundle b = data.getExtras();
            if (b != null) {
                myProvince = b.getString("province");
                myCity = b.getString("city");
                myArea = b.getString("area");
                tvAddress.setText(myProvince +  " "  + myCity +  " " +  myArea + " ");//赋值
            }
        }
    }
    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(TimeActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}