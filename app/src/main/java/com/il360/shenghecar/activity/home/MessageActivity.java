package com.il360.shenghecar.activity.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
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
import com.il360.shenghecar.model.Me;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SIMCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.act_message)
public class MessageActivity extends BaseWidgetActivity {


    @ViewById EditText etName;//姓名
    @ViewById EditText etPhone;//电话
    @ViewById EditText etComment;//留言信息
    @ViewById TextView tvSubmit;

    private Me me;//接口实体类<extral:存储  private:实体类>

    protected ProgressDialog transDialog;

    @AfterViews
    void init() {
    }

    @Click
    void tvSubmit() {
        if(isOk()){
            tvSubmit.setClickable(false);
            transDialog = ProgressDialog.show(MessageActivity.this, null, "提交中...", true);
            initUpMessage();
        }
    }

    private void initUpMessage() {//上传留言信息
        ExecuteTask.execute(new Runnable() {//

            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("messageJson", makeJson());
                    params.put("token", UserUtil.getToken());
                    TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
                            "goods/upMessage", params);
                    if (result.getSuccess()) {
                        if (ResultUtil.isOutTime(result.getResult()) != null) {//
                            showInfo(ResultUtil.isOutTime(result.getResult()));
                            Intent intent = new Intent(MessageActivity.this, LoginActivity_.class);
                            startActivity(intent);
                        } else {
                        JSONObject obj = new JSONObject(result.getResult());
                        if (obj.getInt("code") == 1) {//提交成功不返回首页
                            showInfo("提交成功！");
                            setResult(101);
                            MessageActivity.this.finish();
                        } else {
                            showInfo(obj.getString("desc"));
                        }
                        }
                    } else {
                        showInfo(getString(R.string.A6));
                    }
                } catch (Exception e) {
                    Log.e("MessageActivity", "initUpMessage()", e);
                    LogUmeng.reportError(MessageActivity.this, e);
                    showInfo(getString(R.string.A2));
                }finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (transDialog != null && transDialog.isShowing()) {
                                transDialog.dismiss();//隐藏对话框也可用hide()
                            }
                            tvSubmit.setClickable(true);//可以点击,false不可以
                        }
                    });
                }
            }
        });
    }

    private String makeJson(){
        Me me = new Me();
        me.setName(ViewUtil.getText(etName));
        me.setPhone(ViewUtil.getText(etPhone));
        me.setComment(ViewUtil.getText(etComment));
        return FastJsonUtils.getJsonString(me);
    }
    private boolean isOk() {//判断条件
        if(TextUtils.isEmpty(ViewUtil.getText(etName))){
            showInfo("请输入您的姓名");
        } else if(TextUtils.isEmpty(ViewUtil.getText(etPhone))){
            showInfo("请输入您的手机号码");
        } else if(!SIMCardUtil.isMobileNo(ViewUtil.getText(etPhone))){
            showInfo("您输入的手机号码格式有误");
        } else if(TextUtils.isEmpty(ViewUtil.getText(etComment))){
            showInfo("请输入您的留言信息");
        } else {
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 101) {
            Bundle b = data.getExtras();
            if(b != null){




            }
        }
    }

    private void showInfo(final String info) {

        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(MessageActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
