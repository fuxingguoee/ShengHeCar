package com.il360.shenghecar.activity.goods;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.WelcomeActivity;
import com.il360.shenghecar.activity.home.InfoActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.home.Contact;
import com.il360.shenghecar.model.home.OutContact;
import com.il360.shenghecar.util.ContactUtil;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.FileUtil;
import com.il360.shenghecar.util.PicUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SDCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.view.CircleImageView;
import com.il360.shenghecar.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.GetObjectRequest;
import com.tencent.cos.task.listener.IDownloadTaskListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lepc on 2018/8/2.
 */
@EActivity(R.layout.act_user)
public class UserActivity extends BaseWidgetActivity {
    @ViewById
    CircleImageView userImage;

    @ViewById
    TextView tvLogin;
    @ViewById
    TextView tvLoginName;

    @ViewById
    RelativeLayout rlClear;//清理缓存
    @ViewById
    TextView tvEditClear;
    @ViewById
    RelativeLayout rlServiceNum;//客服电话1
    @ViewById
    TextView tvEditServiceNum;
    @ViewById
    RelativeLayout rlServiceNum2;//客服电话2
    @ViewById
    TextView tvEditServiceNum2;
    @ViewById
    RelativeLayout rlNews;//最新消息
    @ViewById
    TextView tvEditNews;
    @ViewById
    RelativeLayout rlInfo;
    @ViewById
    TextView tvEditInfo;
    @ViewById
    RelativeLayout rlTContacts;
    @ViewById
    TextView tvTContacts;
    @ViewById
    RelativeLayout rlTopContacts;//紧急联系人
    @ViewById
    TextView tvTopContacts;

    @ViewById
    TextView tvEditVersion;

    @ViewById
    TextView tvLoginOut;//退出登录

    protected ProgressDialog transDialog;
    final File file = new File(Variables.APP_CACHE_SDPATH);

    private Handler handler = null;

    private String picPath = "";

    private static List<Contact> contactList = new ArrayList<Contact>();


    @Override
    public void onResume() {
        super.onResume();
        init();
//        if (UserUtil.judgeUserInfo()) { //
//            showDialog2();
//        }
    }

    @AfterViews
    void initViews() {


        //创建属于主线程的handler
        handler = new Handler();

        contactList = ContactUtil.getContactList(UserActivity.this);
    }


    void init() {
        if (UserUtil.judgeUserInfo()) { // 是否登录
           initUserHead();
            tvLoginOut.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
            tvLoginName.setVisibility(View.VISIBLE);
            tvLoginName.setText(UserUtil.getUserInfo().getLoginName());

            if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==1) {//

                tvTopContacts.setText("已上传");
                tvTopContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_blue));
                tvTContacts.setText("已上传");
                tvTContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_blue));
            } else {
                tvTopContacts.setText("未上传");
                tvTopContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_gray));
                tvTContacts.setText("未上传");
                tvTContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_gray));
            }

        } else {
            tvLogin.setVisibility(View.VISIBLE);
            tvLoginOut.setVisibility(View.GONE);
            tvLoginName.setVisibility(View.GONE);
            tvTopContacts.setText("");
            tvTContacts.setText("");
        }
        userImage.setImageResource(R.drawable.ic_touxiang);

        try {
            PackageInfo pInfo = UserActivity.this.getPackageManager().getPackageInfo(UserActivity.this.getPackageName(), 0);
            tvEditVersion.setText(pInfo.versionName );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (GlobalPara.getTelephone() != null) {
            String[] phoneArray = GlobalPara.getTelephone().split("/");
            if(phoneArray.length > 1) {
                rlServiceNum2.setVisibility(View.VISIBLE);
                tvEditServiceNum.setText(phoneArray[0]);
                tvEditServiceNum2.setText(phoneArray[1]);
            } else {//获取客服电话
                rlServiceNum2.setVisibility(View.GONE);
                tvEditServiceNum.setText(phoneArray[0]);
            }
        }

        float s = file.length() / 1024;
        tvEditClear.setText(Float.toString(s) + "KB");
    }

    /**
     * 显示用户头像
     */
    private void initUserHead() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (UserUtil.getUserInfo() != null && !TextUtils.isEmpty(UserUtil.getUserInfo().getTxyUserPic())
                        && SDCardUtil.hasSDCard(UserActivity.this)) {
                    initSignForUrl();
                } else {
                    userImage.setImageResource(R.drawable.ic_touxiang);
                }
            }
        });

    }

    protected void initSignForUrl() {
        transDialog = ProgressDialog.show(UserActivity.this, null, "加载中...", true);
        ExecuteTask.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserUtil.getToken());//传入相关参数
                    params.put("picurl", UserUtil.getUserInfo().getTxyUserPic());
                    TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "goods/querySign", params);
                    if (result.getSuccess()) {
                        final JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {
                            loadPic(obj.getString("result"));
                        } else {
                            showInfo(obj.getString("desc"));
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    showInfo(getString(R.string.A2));
                    Log.e("UserActivity", "initSignForUrl()", e);
                    LogUmeng.reportError(UserActivity.this, e);
                }
            }
        });
    }

    private void loadPic(String sign) {

        final String headUrl = UserUtil.getUserInfo().getTxyUserPic();
        final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "load";

        GetObjectRequest getObjectRequest = new GetObjectRequest(headUrl, savePath);
        getObjectRequest.setSign(sign);
        getObjectRequest.setListener(new IDownloadTaskListener() {
            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
            }

            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {

                new Thread() {
                    public void run() {
                        String fileName = getNameFromUrl(headUrl);
                        picPath = savePath + File.separator + fileName;
                        handler.post(runnableUi);
                    }
                }.start();

                Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
            }

            @Override
            public void onFailed(COSRequest COSRequest, COSResult cosResult) {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
            }

            @Override
            public void onCancel(COSRequest arg0, COSResult arg1) {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
            }
        });

        WelcomeActivity.getCOSClient().getObject(getObjectRequest);
    }


    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            //更新界面
            Bitmap signBitmap = PicUtil.getSmallBitmap(picPath);
            userImage.setImageBitmap(signBitmap);
            if (transDialog != null && transDialog.isShowing()) {
                transDialog.dismiss();
            }
        }
    };

    @Click
    void userImage() {//修改头像<头像功能去掉>
//        if (UserUtil.judgeUserInfo()) {
//           Intent intent = new Intent(UserActivity.this, AccountPictureActivity_.class);
//    startActivity(intent);
      //  }
      //  showDialog2();
    }

    @Click
    void rlNews() {//
		if(UserUtil.judgeUserInfo()) {
			Intent intent = new Intent(UserActivity.this, NewsActivity_.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(UserActivity.this,LoginActivity_.class);
			startActivity(intent);
		}
    }

//    @Click
//    void rlInfo(){
//        if(UserUtil.judgeUserInfo()) {
//            if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {
//                Intent intent = new Intent(UserActivity.this, InfoActivity_.class);
//                startActivity(intent);
//            } else {
//                Intent intent = new Intent(UserActivity.this,InfoActivity_.class);
//                startActivity(intent);
//            }
//        } else {
//            Intent intent = new Intent(UserActivity.this,LoginActivity_.class);
//            startActivity(intent);
//        }
//}

    @Click
    void rlTopContacts() {//上传紧急联系人
        if(UserUtil.judgeUserInfo()) {
//            if(UserUtil.getUserInfo().getIsUp() != null && UserUtil.getUserInfo().getIsUp() == 1) {//
            if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==1){
                //已上传
                // showInfo("您已上传紧急联系人了！");
                Intent intent = new Intent(UserActivity.this, InfoActivity_.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(UserActivity.this, InfoActivity_.class);
                startActivity(intent);
            }
//            } else {
//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                startActivityForResult(intent, 1000);
//            }
        } else {//返回登录页面
            Intent intent = new Intent(UserActivity.this,LoginActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void tvLogin() {//登录
        Intent intent = new Intent(UserActivity.this, LoginActivity_.class);
        startActivity(intent);
    }

    @Click
    void rlServiceNum() {//客服电话1
        if(!TextUtils.isEmpty(ViewUtil.getText(tvEditServiceNum))){
            Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tvEditServiceNum.getText()));
            startActivity(intent);
        }
    }

    @Click
    void rlServiceNum2() {//客服电话2
        if(!TextUtils.isEmpty(ViewUtil.getText(tvEditServiceNum2))){
            Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + tvEditServiceNum2.getText()));
            startActivity(intent);
        }
    }

    @Click
    void rlClear(){//清理缓存
        showInfo(getResources().getString(R.string.deleting));
        FileUtil.RecursionDeleteFile(file);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiscCache();
        if(!file.exists()){
            showInfo(getResources().getString(R.string.clean_cache_sucess));
            tvEditClear.setText("0.0"+"KB");
        }
    }

    @Click
    void tvLoginOut() {//退出登录
        GlobalPara.clean();
        UserUtil.clearUserInfo();
        Intent intent = new Intent(UserActivity.this, LoginActivity_.class);
        startActivity(intent);
    }

    private void showInfo(final String info) {
        runOnUiThread(new Runnable() {
            public void run() {

                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(UserActivity.this, info, Toast.LENGTH_SHORT).show();
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
			transDialog = ProgressDialog.show(UserActivity.this, null, "上传中...", true);
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
								Intent intent = new Intent(UserActivity.this, LoginActivity_.class);
								startActivity(intent);
							} else {
								JSONObject obj = new JSONObject(result.getResult());
								if (obj.getInt("code") == 1) {
                                    UserUtil.getUserInfo().setIsUp(1);
								}
								showInfo(obj.getString("desc"));	//上传成功
							}
						}
					} catch (Exception e) {
						showInfo(getString(R.string.A2));
						Log.e("UserActivity", "postUserContact", e);

						LogUmeng.reportError(UserActivity.this, e);
					} finally {
						runOnUiThread(new Runnable() {
								public void run() {
									if (transDialog != null && transDialog.isShowing()) {
										transDialog.dismiss();
									}

                                    if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==1) {
                                        tvTopContacts.setText("已上传");
                                        tvTopContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_blue));

                                        tvTContacts.setText("已上传");
                                        tvTContacts.setTextColor(ContextCompat.getColor(UserActivity.this, R.color.text_blue));
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
        CustomDialog.Builder builder = new CustomDialog.Builder(UserActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(message);
        builder.setPositiveButton("上传", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postUserContact();
                Intent intent = new Intent(UserActivity.this, InfoActivity_.class);
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
    //对话框
    private void showDialog2() {//信息未登记提示前往登记
        CustomDialog.Builder builder = new CustomDialog.Builder(UserActivity.this);

        builder.setTitle("友情提示");
        builder.setMessage("信息未登记请去登记");
//        builder.setPositiveButton("取消", new android.content.DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        builder.setNegativeButton("去登记", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(UserActivity.this, InfoActivity_.class);

                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
