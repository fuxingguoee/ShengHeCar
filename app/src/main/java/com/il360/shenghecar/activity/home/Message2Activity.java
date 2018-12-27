package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RelativeLayout;
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
import com.il360.shenghecar.model.home.Contact;
import com.il360.shenghecar.model.home.OutContact;
import com.il360.shenghecar.util.ContactUtil;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EActivity(R.layout.activity_message2)
public class Message2Activity extends BaseWidgetActivity {
    @ViewById
    RelativeLayout rlTContacts;
    @ViewById
    TextView tvTContacts;
    @ViewById
    RelativeLayout rlTopContacts;//紧急联系人
    @ViewById
    TextView tvTopContacts;
   // private static List<Contact> contactList = new ArrayList<Contact>();
    private Handler handler = null;

    private String picPath = "";
    protected ProgressDialog transDialog;
    private static List<Contact> contactList = new ArrayList<Contact>();

    @Extra
    String topConteactsName;
    @Extra
    String topConteactsNunber;
    @AfterViews
    void initViews() {

        //创建属于主线程的handler
        handler = new Handler();

        contactList = ContactUtil.getContactList(Message2Activity.this);
    }

    void init() {
        if (UserUtil.judgeUserInfo()) { // 是否

            if(UserUtil.getUserInfo().getIsUp() != null && UserUtil.getUserInfo().getIsUp() == 1) {//通讯录上传

                tvTopContacts.setText("已上传");
                tvTopContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_blue));
                tvTContacts.setText("已上传");
                tvTContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_blue));
            } else {
                tvTopContacts.setText("未上传");
                tvTopContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_gray));
                tvTContacts.setText("未上传");
                tvTContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_gray));
            }

        } else {
            tvTopContacts.setText("");
            tvTContacts.setText("");
        }
        }

    @Click
    void rlTopContacts() {//上传紧急联系人
        if(UserUtil.judgeUserInfo()) {
            if(UserUtil.getUserInfo().getIsUp() != null && UserUtil.getUserInfo().getIsUp() == 1) {
                //已上传
                // showInfo("您已上传紧急联系人了！");

                Intent intent = new Intent(Message2Activity.this, InfoActivity_.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        } else {//返回登录页面
            Intent intent = new Intent(Message2Activity.this,LoginActivity_.class);
            startActivity(intent);
        }
    }




    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {

                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(Message2Activity.this, info, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getNameFromUrl(String loadUrl) {
        String[] a = loadUrl.split("/");
        String s = a[a.length - 1];
        return s;
    }

    private String makeJsonPost() {//紧急联系人
        OutContact outContact = new OutContact();
        outContact.setList(contactList);
        return FastJsonUtils.getJsonString(outContact);
    }

    private void postUserContact() {//上传联系人
        if (contactList != null && contactList.size() > 0) {
            transDialog = ProgressDialog.show(Message2Activity.this, null, "上传中...", true);
            ExecuteTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("token", UserUtil.getToken());
                        params.put("userContactJson", makeJsonPost());
                        TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                                "goods/postUserContact", params);//端口+地址
                        if (result.getSuccess()) {
                            if (ResultUtil.isOutTime(result.getResult()) != null) {//超时信息不为空
                                showInfo(ResultUtil.isOutTime(result.getResult()));
                                Intent intent = new Intent(Message2Activity.this, LoginActivity_.class);
                                startActivity(intent);
                            } else {
                                JSONObject obj = new JSONObject(result.getResult());
                                if (obj.getInt("code") == 1) {
                                    //上传成功
                                    UserUtil.getUserInfo().setIsUp(1);
                                }
                                showInfo(obj.getString("desc"));
                            }
                        }
                    } catch (Exception e) {
                        showInfo(getString(R.string.A2));
                        Log.e("UserActivity", "postUserContact", e);

                        LogUmeng.reportError(Message2Activity.this, e);
                    } finally {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (transDialog != null && transDialog.isShowing()) {
                                    transDialog.dismiss();
                                }

                                if(UserUtil.getUserInfo().getIsUp() != null && UserUtil.getUserInfo().getIsUp() == 1) {
                                    tvTopContacts.setText("已上传");
                                    tvTopContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_blue));

                                    tvTContacts.setText("已上传");
                                    tvTContacts.setTextColor(ContextCompat.getColor(Message2Activity.this, R.color.text_blue));
                                }
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
            // 取得联系人姓名
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

    private String formatPhoneNum(String phoneNum) {//
        String regex = "(\\+86)|(600)|[^0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.replaceAll("");
    }

    private void showDialog(String message) {//对话框
        CustomDialog.Builder builder = new CustomDialog.Builder(Message2Activity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton("上传", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postUserContact();
                if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {
                    Intent intent = new Intent(Message2Activity.this, InfoActivity_.class);
                    intent.putExtra("emergencyContact", topConteactsName);
                    intent.putExtra("emergencyPhone", topConteactsNunber);
                    startActivity(intent);
                    dialog.dismiss();
                }else {
                    Intent intent = new Intent(Message2Activity.this, InfoActivity_.class);
                    startActivity(intent);
                    dialog.dismiss();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//请求码
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String[] contact = getPhoneContacts(uri);
                if (contact != null) {
                    String topConteactsName = contact[0];// 姓名
                    String topConteactsNunber = formatPhoneNum(contact[1]);// 手机号
                    showDialog("是否上传紧急联系人：" + topConteactsName + "(" + topConteactsNunber + ")");

                }
            }
        }
    }
}