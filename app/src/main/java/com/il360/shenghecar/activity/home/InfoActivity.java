package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.adapter.InfoAdadpter;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.Info;
import com.il360.shenghecar.model.InfoCheck;
import com.il360.shenghecar.model.MyInfo;
import com.il360.shenghecar.model.home.Contact;
import com.il360.shenghecar.model.home.OutContact;
import com.il360.shenghecar.util.ContactUtil;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.IdCardUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SIMCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.view.CustomDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EActivity(R.layout.activity_info)
public class InfoActivity extends BaseWidgetActivity {

    @ViewById
    EditText etName;//客户姓名
    @ViewById
    EditText etCertificate;//身份证号
    @ViewById
    EditText etOperator;//归属业务员
    @ViewById
    EditText etOperatorPhone;//业务员联系号码
    @ViewById
    ImageView ivAddressRight;
    @ViewById
    ImageView ivAddressRigh;
    @ViewById
    TextView tvCompany;//分公司
    @ViewById TextView tvEmergencyContact;
    @ViewById TextView tvEmergencyPhone;
    @ViewById
    RelativeLayout rlContact;//紧急联系人

    private static Timer timer = null;//定时器
    private Info info;
    private MyInfo response;
    private int status = 1;
//    @Extra
//    String topConteactsName;//传值
//    @Extra
//    String topConteactsNunber;
    @Extra
    String branchName;//分公司名称
    @Extra
    Integer branchId;//分公司ID
    @ViewById
    TextView tvSubmit;
    private   String topConteactsName;
    private   String topConteactsNumber;

    private List<InfoCheck> list = new ArrayList<>();//信息登记列表
    private InfoAdadpter adapter;
    protected ProgressDialog transDialog;
    private static List<Contact> contactList = new ArrayList<Contact>();

    @AfterViews
    void init() {
        contactList = ContactUtil.getContactList(com.il360.shenghecar.activity.home.InfoActivity.this);

        if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {//未登记
            initViews();
            tvSubmit.setVisibility(View.VISIBLE);
        } else {//已登记
            initData();
            etName.setFocusable(false);
            etCertificate.setFocusable(false);
            etOperator.setFocusable(false);
            etOperatorPhone.setFocusable(false);
            tvCompany.setFocusable(false);
            tvEmergencyContact.setFocusable(false);
            tvEmergencyPhone.setFocusable(false);
            tvCompany.setClickable(false);
            ivAddressRight.setVisibility(View.GONE);
            ivAddressRigh.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.GONE);
        }
    }

    private void initViews() {//置空查询
        etName.setText("");
        etCertificate.setText("");
        etOperator.setText("");
        etOperatorPhone.setText("");
        tvCompany.setText("");
        tvEmergencyContact.setText("");
        tvEmergencyPhone.setText("");
    }

    @Click
    void tvCompany() {//分公司
        Intent intent = new Intent();
        intent.setClass(this, CompanyActivity_.class);
        startActivityForResult(intent, 0);
    }

    @Click
    void tvSubmit() {//提交
        tvSubmit.setClickable(false);
        if (isOk()) {
            transDialog = ProgressDialog.show(InfoActivity.this, null, "提交中...", true);
            initInfo();
        } else {//定时器
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
            timer.schedule(task, 1000 * 1);//一秒一次
        }
    }

    private boolean isOk() {
       if  (TextUtils.isEmpty(ViewUtil.getText(etName))) {
            showInfo("客户姓名不能为空！");
        } else  if (TextUtils.isEmpty(ViewUtil.getText(etCertificate))) {
            showInfo("身份证号不能为空！");
        } else  if (!IdCardUtil.isIdcard(ViewUtil.getText(etCertificate))) {
            showInfo("请输入正确的身份证号");
        } else if (TextUtils.isEmpty(ViewUtil.getText(tvCompany))) {
           showInfo("请选择分公司！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etOperator))) {
           showInfo("归属业务员不能为空！");
        } else if (TextUtils.isEmpty(ViewUtil.getText(etOperatorPhone))) {
           showInfo("业务员号码不能为空！");
        } else if (!SIMCardUtil.isMobileNo(ViewUtil.getText(etOperatorPhone))) {
           showInfo("手机号码格式有误！");
       } else if (TextUtils.isEmpty(ViewUtil.getText(tvEmergencyContact))) {
           showInfo("请选择紧急联系人");
        } else {
            return true;
        }
       return false;
    }

    private void initInfo() {//信息登记接口
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("infoCheckJson", makeJson());
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "goods/upInfoCheck", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(InfoActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            JSONObject obj = new JSONObject(result.getResult());
                            if (obj.getInt("code") == 1) {
                                UserUtil.getUserInfo().setCheck(1);// 记录<已上传:1  未上传:0>
                                showInfo("提交成功！");
                                InfoActivity.this.finish();
                                //intent.putExtra("mShowTabIndex", 0);
                            } else {
                                showInfo(obj.getString("desc"));
                            }
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("InfoActivity", "initInfo()", e);//错误信息
                    LogUmeng.reportError(InfoActivity.this, e);
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

    private String makeJson() {
        info = new Info();
        info.setBranchId(branchId);
        info.setUserId(UserUtil.getUserInfo().getUserId());
        info.setName(ViewUtil.getText(etName));
        info.setCertificate(ViewUtil.getText(etCertificate));
        info.setOperator(ViewUtil.getText(etOperator));
        info.setOperatorPhone(ViewUtil.getText(etOperatorPhone));
        if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {//未登记
            info.setEmergencyContact(topConteactsName);
            info.setEmergencyPhone(topConteactsNumber);
        }
        else {//已登记
            info.setEmergencyContact(list.get(0).getEmergencyContact());
            info.setEmergencyPhone(list.get(0).getEmergencyPhone());
        }
        return FastJsonUtils.getJsonString(info);
    }

    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(InfoActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 101) {//分公司
            Bundle b = data.getExtras();
            branchId=b.getInt("branchId");//公司编号
            branchName = b.getString("branchName");//公司名称
            tvCompany.setText(branchName);
        }else if (requestCode == 1000 && resultCode == RESULT_OK) {
            if (data != null) {//紧急联系人
                Uri uri = data.getData();
                String[] contact = getPhoneContacts(uri);
                if (contact != null) {
                    topConteactsName = contact[0];// 联系人
                    topConteactsNumber = formatPhoneNum(contact[1]);// 手机号
                    postUserContact();
                 //  showDialog("是否上传紧急联系人：" + topConteactsName + "(" + topConteactsNumber + ")");
                }
            }
        }
    }

    private void initData() {//获取信息登记列表
        ExecuteTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "goods/queryInfoCheck",params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {  //超时登录
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(InfoActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                            MyInfo response = FastJsonUtils.getSingleBean(result.getResult(), MyInfo.class);
                            if (response.getCode() == 1) {//数组形式list[]
                                if(response.getResult() != null && response.getResult().size() > 0){
                                    list = response.getResult();
                                } else {
                                    showInfo(getResources().getString(R.string.no_data));
                                }
                            } else {
                                showInfo(response.getDesc());
                            }
                        }
                    } else {
                       showInfo(result.getResult());
                    }
                } catch (Exception e) {
                    Log.e("InfoActivity", "initData", e);
                    LogUmeng.reportError(InfoActivity.this, e);
                } finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(list.get(0) != null) {
                                etName.setText(list.get(0).getName());
                                etCertificate.setText(list.get(0).getCertificate());
                                etOperator.setText(list.get(0).getOperator());
                                etOperatorPhone.setText(list.get(0).getOperatorPhone());
                                tvCompany.setText(list.get(0).getBranchName());
                                tvEmergencyContact.setText(list.get(0).getEmergencyContact());
                                tvEmergencyPhone.setText(list.get(0).getEmergencyPhone());
                            }
                            else{
                                showInfo(getResources().getString(R.string.no_data));
                            }
                        }
                    });
                }
            }
        });
    }

    @Click
    void rlContact() {//上传紧急联系人
        if(UserUtil.judgeUserInfo()) {
            if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==1) {//信息已登记
            } else {//信息未登记
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        } else {//返回登录页面
            Intent intent = new Intent(com.il360.shenghecar.activity.home.InfoActivity.this,LoginActivity_.class);
            startActivity(intent);
        }
    }

    private String makeJsonPost() {//紧急联系人
        OutContact outContact = new OutContact();
        outContact.setList(contactList);
        return FastJsonUtils.getJsonString(outContact);
    }

    private void postUserContact() {//上传用户通讯录
        if (contactList != null && contactList.size() > 0) {
            transDialog = ProgressDialog.show(com.il360.shenghecar.activity.home.InfoActivity.this, null, "上传中...", true);
            ExecuteTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("token", UserUtil.getToken());
                        params.put("userContactJson", makeJsonPost());
                        TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                                "goods/postUserContact", params);
                        if (result.getSuccess()) {
                            if (ResultUtil.isOutTime(result.getResult()) != null) {
                                showInfo(ResultUtil.isOutTime(result.getResult()));
                                Intent intent = new Intent(com.il360.shenghecar.activity.home.InfoActivity.this, LoginActivity_.class);
                                startActivity(intent);
                            } else {
                                JSONObject obj = new JSONObject(result.getResult());
                                if (obj.getInt("code") == 1) {
                                    //登记信息上传成功
                                    UserUtil.getUserInfo().setIsUp(1);
                                }
                             //   showInfo(obj.getString("desc"));//提示信息:上传成功
                            }
                        }
                    } catch (Exception e) {//
                        showInfo(getString(R.string.A2));
                        Log.e("InfoActivity", "postUserContact", e);
                        LogUmeng.reportError(com.il360.shenghecar.activity.home.InfoActivity.this, e);
                    } finally {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (transDialog != null && transDialog.isShowing()) {
                                    transDialog.dismiss();
                                }
                                tvEmergencyContact.setText(topConteactsName);//联系人
                                tvEmergencyPhone.setText(topConteactsNumber);//号码
                            }
                        });
                    }
                }
            });

        } else {
            showInfo("请确保通讯录不为空");
        }
    }

    /**
     * 读取联系人信息
     * @param uri
     */

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];

        // 得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            // 取得联系人姓名和号码
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("contacts", contact[0]);
            Log.i("contactsUsername", contact[1]);
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

    private String formatPhoneNum(String phoneNum) {//正则表达式
        String regex = "(\\+86)|(600)|[^0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.replaceAll("");
    }

    private void showDialog(String message) {//通讯录上传弹框

        CustomDialog.Builder builder = new CustomDialog.Builder(com.il360.shenghecar.activity.home.InfoActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton("上传", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postUserContact();//通讯录上传
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
}
