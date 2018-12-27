package com.il360.shenghecar.activity.mydata;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.order.RepaymentActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.activity.user.MyBankCardActivity_;
import com.il360.shenghecar.activity.user.MyInfoActivity_;
import com.il360.shenghecar.activity.user.VerifiedActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.home.ArrayOfSwitch;
import com.il360.shenghecar.model.home.OutContact;
import com.il360.shenghecar.model.home.RemainTimes;
import com.il360.shenghecar.model.home.TuthenticationChannel;
import com.il360.shenghecar.model.user.OutUserRz;
import com.il360.shenghecar.model.user.UserAmount;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog;
import com.moxie.client.exception.ExceptionType;
import com.moxie.client.exception.MoxieException;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.model.MxParam;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.fraudmetrix.octopus.aspirit.main.OctopusManager;
import cn.fraudmetrix.octopus.aspirit.main.OctopusTaskCallBack;

/**
 * Created by lepc on 2018/7/9.
 */


@EActivity(R.layout.act_authen)
public class AuthenActivity extends BaseWidgetActivity {
    private String mUserId = ""; //合作方系统中的客户ID
    private static final String TAG = "MoxieSDK";

    MxParam mxParam = new MxParam();

    @ViewById
    PullToRefreshScrollView pull_refresh_scrollview;

    @ViewById
    TextView tvAvailable,tvCreditLine,tvRepay;
    @ViewById TextView tvToPay;//还款
    @ViewById TextView tvPromote;//明细

    @ViewById
    RelativeLayout rlCreditLine;

    @ViewById
    RelativeLayout rlIdentify, rlTaoBao, rlPhone, rlBankCard;
    @ViewById
    ImageView ivIdentifyStatus, ivTaoBaoStatus, ivPhoneStatus,ivBankCardStatus;

    protected ProgressDialog transDialog;

    private UserAmount userAmount;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onResume() {
        super.onResume();
        if (!UserUtil.judgeUserInfo()) {
            initStatus();
            tvAvailable.setText("-.--");
            tvCreditLine.setText("-.--");
            tvRepay.setText("-.--");
            tvToPay.setVisibility(View.GONE);
            tvPromote.setVisibility(View.GONE);

        } else {
            initSwitch();
            initCreditLine();
//            initPromoteAmount();
            initUserRz();

            mUserId = UserUtil.getUserInfo().getPhone() + "," + Variables.MX_PLATFORM;
            mxParam.setUserId(mUserId);
            mxParam.setApiKey(Variables.MX_APIKEY);
        }
    }

    @AfterViews
    void init() {
        initPull();
    }

    private void initPull() {
        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (UserUtil.judgeUserInfo()) {
                    initSwitch();
                    initCreditLine();
//                    initPromoteAmount();
                    initUserRz();
                } else {
                    try {
                        Thread.sleep(1000);
                        pull_refresh_scrollview.onRefreshComplete();
                    } catch (Exception e) {
                    }
                }

            }
        });
    }

//    protected void initPromoteAmount() {
//        ExecuteTask.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("token", UserUtil.getToken());
//                    params.put("pageNo", "1");
//                    params.put("pageSize","10");
//                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
//                            "user/queryPromoteAmount", params);
//                    if (result.getSuccess()) {
//                        JSONObject obj = new JSONObject(result.getResult());
//                        if (obj.getInt("code") == 1) {
//                            JSONObject objRes = obj.getJSONObject("result");
//                            JSONObject objResRes = objRes.getJSONObject("returnResult");
//                            ArrayOfPromoteAmount response = FastJsonUtils.getSingleBean(objResRes.toString(), ArrayOfPromoteAmount.class);
//                            if (response != null && response.getList()!= null && response.getList().size() > 0) {
//                                runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            tvPromote.setVisibility(View.VISIBLE);
//                                        }
//                                    });
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                }
//            }
//        });
//    }

    protected void initUserRz() {
        transDialog = ProgressDialog.show(AuthenActivity.this, null, "努力加载中...", true, false);
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "card/queryUserInfo", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            GlobalPara.outUserRz = null;
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                JSONObject objRes = obj.getJSONObject("result");
                                JSONObject objRetRes = objRes.getJSONObject("returnResult");
                                GlobalPara.outUserRz = FastJsonUtils.getSingleBean(objRetRes.toString(), OutUserRz.class);

                                if(GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getAppCount() != null && GlobalPara.getOutUserRz().getAppCount() == 1){
                                    //已上传app列表
                                } else {
                                    upAPPName();//上传app列表
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("AuthenFragment", "initUserRz", e);
                    showInfo(getResources().getString(R.string.A2));
                    LogUmeng.reportError(AuthenActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                            public void run() {
                                if (transDialog != null && transDialog.isShowing()) {
                                    transDialog.dismiss();
                                }
                                if (GlobalPara.outUserRz != null) {
                                    initViews();
                                }
                                pull_refresh_scrollview.onRefreshComplete();
                            }
                        });
                }
            }
        });
    }

    private String makeJsonPost() {
        OutContact outContact = new OutContact();
        outContact.setAppList(GlobalPara.getAppNameList());
        return FastJsonUtils.getJsonString(outContact);
    }

    private void upAPPName() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userContactJson", makeJsonPost());
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "usercredit/postUserInstallApp", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            GlobalPara.getOutUserRz().setAppCount(1);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    private void initSwitch() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "switch/queryAll", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        ArrayOfSwitch arrayOfSwitch = FastJsonUtils.getSingleBean(obj.toString(), ArrayOfSwitch.class);
                        if (arrayOfSwitch.getCode() == 1) {
                            GlobalPara.mySwitchList = null;
                            GlobalPara.mySwitchList = arrayOfSwitch.getSwitchConfigs();

                            if (!UserUtil.judgeAuthentication() && GlobalPara.getCanAutoVerified()) {
                                initTimes();
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void initTimes(){
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", UserUtil.getToken());
            TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "fc/queryRemainTimes", params);
            if (result.getSuccess()) {
                JSONObject obj = new JSONObject(result.getResult());
                final RemainTimes remainTimes = FastJsonUtils.getSingleBean(obj.toString(), RemainTimes.class);
                if(remainTimes.getCode() != null && remainTimes.getCode() == 1){
                    GlobalPara.remainTimes = 0;
                    GlobalPara.maxTimes = 0;
                    GlobalPara.remainTimes = remainTimes.getRmTimes();
                    GlobalPara.maxTimes = remainTimes.getMaxTimes();
                }
            }
        } catch (Exception e) {
        }
    }

    private void initCreditLine() {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
                            "user/queryUserAmount", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            JSONObject objRes = obj.getJSONObject("result");
                            userAmount = FastJsonUtils.getSingleBean(objRes.toString(), UserAmount.class);
                        } else {
                            showInfo(obj.getString("desc"));
                        }
                    }
                } catch (Exception e) {
                    Log.e("AuthenActivity", "initCreditLine", e);
                    LogUmeng.reportError(AuthenActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                            public void run() {
                                if (userAmount != null) {
                                    tvCreditLine.setText(df.format(userAmount.getAllAmount()));
                                    tvRepay.setText(df.format(userAmount.getUseAmount()));
                                    tvAvailable.setText(
                                            df.format(userAmount.getAllAmount().subtract(userAmount.getUseAmount())));

                                    if(userAmount.getUseAmount().doubleValue() > 0){
                                        tvToPay.setVisibility(View.VISIBLE);
                                    } else {
                                        tvToPay.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                }
            }
        });
    }

    private void initViews() {
        initStatus();

        if (UserUtil.judgeAuthentication()) {
            ivIdentifyStatus.setBackgroundResource(R.drawable.ic_my_hua_ok);
            initBankCard();
            initTaoBao();
            initPhone();

        } else {
            if (GlobalPara.getOutUserRz().getNameRz() != null && GlobalPara.getOutUserRz().getNameRz() == -1) {
                ivIdentifyStatus.setBackgroundResource(R.drawable.ic_my_hua_failed);
            } else if (GlobalPara.getOutUserRz().getNameRz() != null && GlobalPara.getOutUserRz().getNameRz() == 2) {
                ivIdentifyStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
            } else {
                ivIdentifyStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
            }
        }

    }

    private void initStatus() {
        ivIdentifyStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        ivBankCardStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
    }

    private void initTaoBao() {
        if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == -1) {
            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_failed);
        } else if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == 1) {
            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_ok);
        } else if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == 2) {
            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
        } else {
            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        }
    }

    private void initPhone() {
        if (GlobalPara.getOutUserRz().getPhoneRz() != null && GlobalPara.getOutUserRz().getPhoneRz() == -1) {
            ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_failed);
        } else if (GlobalPara.getOutUserRz().getPhoneRz() != null && GlobalPara.getOutUserRz().getPhoneRz() == 1) {
            ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_ok);
        } else if (GlobalPara.getOutUserRz().getPhoneRz() != null && GlobalPara.getOutUserRz().getPhoneRz() == 2) {
            ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
        } else if (GlobalPara.getOutUserRz().getPhoneRz() != null && GlobalPara.getOutUserRz().getPhoneRz() == 3) {
            ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
        } else {
            ivPhoneStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        }
    }

    private void initBankCard() {
        if (GlobalPara.getOutUserRz().getBankRz() != null && GlobalPara.getOutUserRz().getBankRz() == -1) {
            ivBankCardStatus.setBackgroundResource(R.drawable.ic_my_hua_failed);
        } else if (GlobalPara.getOutUserRz().getBankRz() != null && GlobalPara.getOutUserRz().getBankRz() == 1) {
            ivBankCardStatus.setBackgroundResource(R.drawable.ic_my_hua_ok);
        } else if (GlobalPara.getOutUserRz().getBankRz() != null && GlobalPara.getOutUserRz().getBankRz() == 2) {
            ivBankCardStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
        } else {
            ivBankCardStatus.setBackgroundResource(R.drawable.ic_my_hua_no);
        }
    }

    private void gotoVerified() {
        Intent intent = new Intent();
        if (GlobalPara.getCanAutoVerified() && GlobalPara.getRemainTimes() > 0 && GlobalPara.getOutUserRz() != null
                && GlobalPara.getOutUserRz().getNameRz() != null && GlobalPara.getOutUserRz().getNameRz() == 0) {
            intent.setClass(AuthenActivity.this, AutoVerifiedActivity_.class);
        } else {
            intent.setClass(AuthenActivity.this, VerifiedActivity_.class);
        }
        startActivity(intent);
    }

    @Click
    void rlCreditLine() {
//        if (UserUtil.judgeUserInfo()) {
//            Intent intent = new Intent(AuthenActivity.this, PromoteAmountActivity_.class);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
//            startActivity(intent);
//        }
    }

    @Click
    void rlIdentify() {
        if (UserUtil.judgeUserInfo()) {
            if (UserUtil.judgeAuthentication()) {
                Intent intent = new Intent(AuthenActivity.this, MyInfoActivity_.class);
                startActivity(intent);
            } else {
                gotoVerified();
            }
        } else {
            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
            startActivity(intent);
        }

    }

    @Click
    void rlTaoBao() {
        if (UserUtil.judgeUserInfo()) {
            if (UserUtil.judgeAuthentication()) {

                if(GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getAppCount() != null && GlobalPara.getOutUserRz().getAppCount() == 1){
                    //已上传app列表
                } else {
                    upAPPName();//上传app列表
                }

                if (UserUtil.judgeBankCard()) {
                    if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == -1) {
                        showDialog2("淘宝认证失败！");
                    } else if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == 1) {
                        showDialog2("淘宝认证已通过！");
                    } else if (GlobalPara.getOutUserRz().getTaobaoRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() == 2) {
                        showDialog2("正在等待认证结果，请耐心等待！");
                    } else {
                        initType(2);
                    }
                } else {
                    showDialog3();
                }
            } else {
                showDialog(getResources().getString(R.string.H2));
            }
        } else {
            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void rlPhone() {
        if (UserUtil.judgeUserInfo()) {
            if (UserUtil.judgeAuthentication()) {

                if(GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getAppCount() != null && GlobalPara.getOutUserRz().getAppCount() == 1){
                    //已上传app列表
                } else {
                    upAPPName();//上传app列表
                }

                if (UserUtil.judgeBankCard()) {
                    if (GlobalPara.getOutUserRz().getPhoneRz() != null && GlobalPara.getOutUserRz().getPhoneRz() == 1) {
                        showDialog2("运营商认证已通过！");
                    } else if(GlobalPara.getOutUserRz().getPhoneRz() != null && (GlobalPara.getOutUserRz().getPhoneRz() == 2
                            || GlobalPara.getOutUserRz().getPhoneRz() == 3)){
                        showDialog2("正在等待认证结果，请耐心等待！");
                    } else {
                        Intent intent = new Intent(AuthenActivity.this, OperatorInfoActivity_.class);
                        startActivity(intent);
                    }
                } else {
                    showDialog3();
                }
            } else {
                showDialog(getResources().getString(R.string.H2));
            }
        } else {
            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void rlBankCard() {
        if (UserUtil.judgeUserInfo()) {
            if (UserUtil.judgeAuthentication()) {

                if(GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getAppCount() != null && GlobalPara.getOutUserRz().getAppCount() == 1){
                    //已上传app列表
                } else {
                    upAPPName();//上传app列表
                }

                Intent intent = new Intent(AuthenActivity.this, MyBankCardActivity_.class);
                startActivity(intent);
            } else {
                showDialog(getResources().getString(R.string.H2));
            }
        } else {
            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void tvToPay(){
        if (UserUtil.judgeUserInfo()) {//跳转到白条账单
            Intent intent = new Intent(AuthenActivity.this, RepaymentActivity_.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
            startActivity(intent);
        }
    }

//	@Click
//	void tvPromote() {
//		if (UserUtil.judgeUserInfo()) {
//			Intent intent = new Intent(getActivity(), PromoteAmountActivity_.class);
//			startActivity(intent);
//		} else {
//			Intent intent = new Intent(getActivity(), LoginActivity_.class);
//			startActivity(intent);
//		}
//	}

    private void initType(final int type) {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    params.put("type", type + "");
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,"card/queryTuthenticationChannel", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(AuthenActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                JSONObject objRes = obj.getJSONObject("result");
                                TuthenticationChannel tc = FastJsonUtils.getSingleBean(objRes.toString(),TuthenticationChannel.class);
                                if (type == 1) {// 支付宝
                                    if (tc.getTuthenticationMethod().intValue() == 1) {
                                        Intent intent = new Intent(AuthenActivity.this, HuaBeiActivity_.class);
                                        startActivity(intent);
                                    } else if (tc.getTuthenticationMethod().intValue() == 2) {
                                        mxParam.setTaskType(MxParam.PARAM_TASK_ALIPAY);

                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                moxieCallback();
                                            }
                                        });
                                    } else if(tc.getTuthenticationMethod().intValue() == 3) {
                                        tongDunCallback("005004 ",3);
                                    }
                                } else if (type == 2) {// 淘宝
                                    if (tc.getTuthenticationMethod().intValue() == 1) {
                                        Intent intent = new Intent(AuthenActivity.this, TaoBaoActivity_.class);
                                        startActivity(intent);
                                    } else if (tc.getTuthenticationMethod().intValue() == 2) {
                                        mxParam.setTaskType(MxParam.PARAM_TASK_TAOBAO);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                moxieCallback();
                                            }
                                        });
                                    } else if(tc.getTuthenticationMethod().intValue() == 3) {
                                        tongDunCallback("005003 ",5);
                                    }
                                } else if (type == 3) {// 京东
                                    if (tc.getTuthenticationMethod().intValue() == 1) {
                                        Intent intent = new Intent(AuthenActivity.this, JingDongActivity_.class);
                                        startActivity(intent);
                                    } else if (tc.getTuthenticationMethod().intValue() == 2) {
                                        mxParam.setTaskType(MxParam.PARAM_TASK_JINGDONG);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                moxieCallback();
                                            }
                                        });
                                    } else if(tc.getTuthenticationMethod().intValue() == 3) {
                                        tongDunCallback("005011",6);
                                    }
                                }
                            } else {
                                showInfo(obj.getString("desc"));
                            }
                        }
                    }
                } catch (Exception e) {
                    showInfo(getResources().getString(R.string.A2));
                }
            }
        });
    }

    private void showDialog(String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(AuthenActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoVerified();
                dialog.dismiss();
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


    private void showDialog3() {
        CustomDialog.Builder builder = new CustomDialog.Builder(AuthenActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("当前用户尚未通过银行卡认证，请先去银行卡认证");
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AuthenActivity.this, MyBankCardActivity_.class);
                startActivity(intent);
                dialog.dismiss();
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

    private void showDialog2(String message) {
        CustomDialog.Builder builder = new CustomDialog.Builder(AuthenActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void showInfo(final String info) {
        runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(AuthenActivity.this, info, Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void moxieCallback(){

        MoxieSDK.getInstance().start(AuthenActivity.this, mxParam, new MoxieCallBack() {
            /**
             *
             *  物理返回键和左上角返回按钮的back事件以及sdk退出后任务的状态都通过这个函数来回调
             *
             * @param moxieContext       可以用这个来实现在魔蝎的页面弹框或者关闭魔蝎的界面
             * @param moxieCallBackData  我们可以根据 MoxieCallBackData 的code来判断目前处于哪个状态，以此来实现自定义的行为
             * @return                   返回true表示这个事件由自己全权处理，返回false会接着执行魔蝎的默认行为(比如退出sdk)
             *
             *   # 注意，假如设置了MxParam.setQuitOnLoginDone(MxParam.PARAM_COMMON_YES);
             *   登录成功后，返回的code是MxParam.ResultCode.IMPORTING，不是MxParam.ResultCode.IMPORT_SUCCESS
             */
            @Override
            public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {

                /**
                 *  # MoxieCallBackData的格式如下：
                 *  1.1.没有进行账单导入，未开始！(后台没有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_UNSTART, "taskType" : "mail", "taskId" : "", "message" : "", "account" : "", "loginDone": false, "businessUserId": ""
                 *  1.2.平台方服务问题(后台没有通知)
                 *      "code" : MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR, "taskType" : "mail", "taskId" : "", "message" : "", "account" : "xxx", "loginDone": false, "businessUserId": ""
                 *  1.3.魔蝎数据服务异常(后台没有通知)
                 *      "code" : MxParam.ResultCode.MOXIE_SERVER_ERROR, "taskType" : "mail", "taskId" : "", "message" : "", "account" : "xxx", "loginDone": false, "businessUserId": ""
                 *  1.4.用户输入出错（密码、验证码等输错且未继续输入）
                 *      "code" : MxParam.ResultCode.USER_INPUT_ERROR, "taskType" : "mail", "taskId" : "", "message" : "密码错误", "account" : "xxx", "loginDone": false, "businessUserId": ""
                 *  2.账单导入失败(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_FAIL, "taskType" : "mail",  "taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112", "account" : "xxx", "loginDone": false, "businessUserId": ""
                 *  3.账单导入成功(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORT_SUCCESS, "taskType" : "mail",  "taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112", "account" : "xxx", "loginDone": true, "businessUserId": "xxxx"
                 *  4.账单导入中(后台有通知)
                 *      "code" : MxParam.ResultCode.IMPORTING, "taskType" : "mail",  "taskId" : "ce6b3806-57a2-4466-90bd-670389b1a112", "account" : "xxx", "loginDone": true, "businessUserId": "xxxx"
                 *
                 *  code           :  表示当前导入的状态
                 *  taskType       :  导入的业务类型，与MxParam.setFunction()传入的一致
                 *  taskId         :  每个导入任务的唯一标识，在登录成功后才会创建
                 *  message        :  提示信息
                 *  account        :  用户输入的账号
                 *  loginDone      :  表示登录是否完成，假如是true，表示已经登录成功，接入方可以根据此标识判断是否可以提前退出
                 *  businessUserId :  第三方被爬取平台本身的userId，非商户传入，例如支付宝的UserId
                 */
                if (moxieCallBackData != null) {
                    Log.d("AuthenActivity", "MoxieSDK Callback Data : "+ moxieCallBackData.toString());
                    switch (moxieCallBackData.getCode()) {
                        /**
                         * 账单导入中
                         *
                         * 如果用户正在导入魔蝎SDK会出现这个情况，如需获取最终状态请轮询贵方后台接口
                         * 魔蝎后台会向贵方后台推送Task通知和Bill通知
                         * Task通知：登录成功/登录失败
                         * Bill通知：账单通知
                         */
                        case MxParam.ResultCode.IMPORTING:
                            if(moxieCallBackData.isLoginDone()) {
                                //状态为IMPORTING, 且loginDone为true，说明这个时候已经在采集中，已经登录成功
                                Log.d(TAG, "任务已经登录成功，正在采集中，SDK退出后不会再回调任务状态，任务最终状态会从服务端回调，建议轮询APP服务端接口查询任务/业务最新状态");

                            } else {
                                //状态为IMPORTING, 且loginDone为false，说明这个时候正在登录中
                                Log.d(TAG, "任务正在登录中，SDK退出后不会再回调任务状态，任务最终状态会从服务端回调，建议轮询APP服务端接口查询任务/业务最新状态");
                            }
                            break;
                        /**
                         * 任务还未开始
                         *
                         * 假如有弹框需求，可以参考 {@link BigdataFragment#showDialog(MoxieContext)}
                         *
                         * example:
                         *  case MxParam.ResultCode.IMPORT_UNSTART:
                         *      showDialog(moxieContext);
                         *      return true;
                         * */
                        case MxParam.ResultCode.IMPORT_UNSTART:
                            Log.d(TAG, "任务未开始");
                            break;
                        case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
                            Toast.makeText(AuthenActivity.this, "导入失败(平台方服务问题)", Toast.LENGTH_SHORT).show();
                            break;
                        case MxParam.ResultCode.MOXIE_SERVER_ERROR:
                            Toast.makeText(AuthenActivity.this, "导入失败(魔蝎数据服务异常)", Toast.LENGTH_SHORT).show();
                            break;
                        case MxParam.ResultCode.USER_INPUT_ERROR:
                            Toast.makeText(AuthenActivity.this, "导入失败(" + moxieCallBackData.getMessage() + ")", Toast.LENGTH_SHORT).show();
                            break;
                        case MxParam.ResultCode.IMPORT_FAIL:
                            Toast.makeText(AuthenActivity.this, "导入失败", Toast.LENGTH_SHORT).show();
                            break;
                        case MxParam.ResultCode.IMPORT_SUCCESS:
                            Log.d(TAG, "任务采集成功，任务最终状态会从服务端回调，建议轮询APP服务端接口查询任务/业务最新状态");

                            //根据taskType进行对应的处理
                            switch (moxieCallBackData.getTaskType()) {
                                case MxParam.PARAM_TASK_ALIPAY:
                                    updateStatusForMoXie(1);
                                    break;
                                case MxParam.PARAM_TASK_TAOBAO:
                                    updateStatusForMoXie(2);
                                    break;
                                case MxParam.PARAM_TASK_JINGDONG:
                                    updateStatusForMoXie(3);
                                    break;
                                default:
                                    Toast.makeText(AuthenActivity.this, "导入成功", Toast.LENGTH_SHORT).show();
                            }
                            moxieContext.finish();
                            return true;
                    }
                }
                return false;
            }

            /**
             * @param moxieContext    可能为null，说明还没有打开魔蝎页面，使用前要判断一下
             * @param moxieException  通过exception.getExceptionType();来获取ExceptionType来判断目前是哪个错误
             */
            @Override
            public void onError(MoxieContext moxieContext, MoxieException moxieException) {
                super.onError(moxieContext, moxieException);
                if(moxieException.getExceptionType() == ExceptionType.SDK_HAS_STARTED) {
                    Toast.makeText(AuthenActivity.this, moxieException.getMessage(), Toast.LENGTH_SHORT).show();
                } else if(moxieException.getExceptionType() == ExceptionType.SDK_LACK_PARAMETERS) {
                    Toast.makeText(AuthenActivity.this,moxieException.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (moxieException.getExceptionType() == ExceptionType.WRONG_PARAMETERS) {
                    Toast.makeText(AuthenActivity.this, moxieException.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.d("AuthenActivity", "MoxieSDK onError : " + (moxieException != null ? moxieException.toString() : ""));
            }
        });
    }


    /**
     * moXieType: 1支付宝，2淘宝，3京东
     * */
    private void updateStatusForMoXie(final int moXieType) {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    params.put("type", moXieType + "");
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "card/updateUserRz", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (moXieType == 1) {
//                                            GlobalPara.getOutUserRz().setZfbRz(2);
//                                            ivZhiFuBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        } else if (moXieType == 2) {
                                            GlobalPara.getOutUserRz().setTaobaoRz(2);
                                            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        } else if (moXieType == 3) {
//                                            GlobalPara.getOutUserRz().setJdRz(2);
//                                            ivJingDongStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        }
                                    }
                                });
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    /**
     *channelCode:  005004 支付宝，005003 淘宝，005011 京东
     * tongDunType: 3 支付宝，5淘宝，6京东
     * */
    private void tongDunCallback(String channelCode, final int tongDunType) {
        OctopusManager.getInstance().setNavImgResId(R.drawable.ic_back);//
        OctopusManager.getInstance().setPrimaryColorResId(R.color.main_logo);//
        OctopusManager.getInstance().setTitleColorResId(R.color.black);//
        OctopusManager.getInstance().setTitleSize(16);//sp
        OctopusManager.getInstance().setShowWarnDialog(true);
        OctopusManager.getInstance().setStatusBarBg(R.color.black);
        OctopusManager.getInstance().getChannel(AuthenActivity.this, channelCode, new OctopusTaskCallBack() {
            @Override
            public void onCallBack(int code, String taskId) {

                if (code == 0) {//code
                    updateStatusForTongDun(taskId,tongDunType);
                } else {
                    showInfo("failure：" + code);
                }
            }
        });
    }

    private void updateStatusForTongDun(final String taskId,final int tongDunType) {
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    params.put("type", tongDunType + "");
                    params.put("taskId", taskId);
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "card/uploadTdTbData", params);
                    if (result.getSuccess()) {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(tongDunType == 3){
//                                            GlobalPara.getOutUserRz().setZfbRz(2);
//                                            ivZhiFuBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        } else if (tongDunType == 5) {
                                            GlobalPara.getOutUserRz().setTaobaoRz(2);
                                            ivTaoBaoStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        } else if (tongDunType == 6) {
//                                            GlobalPara.getOutUserRz().setJdRz(2);
//                                            ivJingDongStatus.setBackgroundResource(R.drawable.ic_my_hua_audit);
                                        }
                                    }
                                });
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //用来清理数据或解除引用
        MoxieSDK.getInstance().clear();
    }
}
