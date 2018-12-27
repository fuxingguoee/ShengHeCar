package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.address.ProvinceActivity_;
import com.il360.shenghecar.activity.main.MainActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.Car;
import com.il360.shenghecar.model.address.UserAddress;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SIMCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.act_ce)
public class CeActivity extends BaseWidgetActivity {

    @ViewById
    EditText etKm;//公里数
    @ViewById
    EditText etModel;//车型
    @ViewById
    RelativeLayout rlAddress;
    @ViewById
    TextView tvAddress;//上牌地
    @ViewById
    EditText etTime;//年份
    @ViewById
    TextView tvOrderNo;
    @ViewById
    EditText etName;//类似退货申请
    @ViewById EditText etPhone;

    private int status = 1;
    private String myProvince;
    private String myCity;
    private String myArea;
    private Car car;//实体类

    @Extra
      String photo1;
    @Extra
      String photo2;
    @Extra
      String photo3;
    @Extra
      String photo4;
    @Extra
      String photo5;
    @Extra
      String photo6;
    @Extra
      String photo7;
    @Extra
      String photo8;
    @Extra
      String photo9;
    @Extra
    UserAddress userAddress;

    @ViewById TextView tvSubmit;
    protected ProgressDialog transDialog;

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
            // tvTextClick.setText("");
            //  rbCheckOK.setChecked(true);
            // rbCheckNO.setChecked(false);
        }
    }

    @Click
    void rlAddress() {
        Intent intent = new Intent(CeActivity.this, ProvinceActivity_.class);
        startActivityForResult(intent, 0);
    }

    @Click
    void tvSubmit() {//
        if(isOk()){
            tvSubmit.setClickable(false);
            transDialog = ProgressDialog.show(CeActivity.this, null, "提交中...", true);
            initCar();
            initData();
        }
    }

    private void initCar() {
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("carExchangeJson", makeJson());
                    params.put("token", UserUtil.getToken());//判断是否超时
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "goods/upCarExchange", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(CeActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo("提交成功！");//返回首页
                                Intent intent = new Intent(CeActivity.this, MainActivity_.class);
                                startActivity(intent);
                                tvSubmit.setClickable(false);//不可点击
                                //setResult(101);
                                //CeActivity.this.finish();
                            } else {
                                showInfo(obj.getString("desc"));
                            }
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("CeActivity", "initCar()", e);
                    LogUmeng.reportError(CeActivity.this, e);
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
/**/
    private String makeJson(){
        Car car = new Car();
        car.setUserId(UserUtil.getUserInfo().getUserId());
        car.setKilometers(ViewUtil.getText(etKm));
        car.setCarModel(ViewUtil.getText(etModel));
        car.setTime(ViewUtil.getText(etTime));
        car.setStatus(status);
        car.setName(ViewUtil.getText(etName));
        car.setPhone(ViewUtil.getText(etPhone));
        car.setLocation(ViewUtil.getText(tvAddress));
        car.setFrontPic(photo1);
        car.setBackPic(photo2);
        car.setLeftPic(photo3);
        car.setRightPic(photo4);
        car.setPlatePic(photo5);
        car.setFramePic(photo6);
        car.setKilometersPic(photo7);
        car.setLicenseFrontPic(photo8);
        car.setLicenseBackPic(photo9);
        return FastJsonUtils.getJsonString(car);
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
                            Intent intent = new Intent(CeActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                showInfo(obj.getString("desc"));
                                setResult(101);
                                CeActivity.this.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("CeActivity", "initData", e);
                    LogUmeng.reportError(CeActivity.this, e);
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
            showInfo("请输入年份");
        } else if ( (ViewUtil.getText(etTime).length()!=4) ){
            showInfo("年份格式有误");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etKm))) {
            showInfo("公里数不能为空");
        } else if (TextUtils.isEmpty(ViewUtil.getText(tvAddress))) {
            showInfo("请选择上牌地！");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Variables.ADDRESS_STATUS_CODE_SECCESS) {
            Bundle b = data.getExtras();
            if (b != null) {
                myProvince = b.getString("province");
                myCity = b.getString("city");
                myArea = b.getString("area");
                tvAddress.setText(myProvince + " " + myCity + " " + myArea + " ");

            }
        }
    }
    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {

                    transDialog.dismiss();
                }
                Toast.makeText(CeActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

