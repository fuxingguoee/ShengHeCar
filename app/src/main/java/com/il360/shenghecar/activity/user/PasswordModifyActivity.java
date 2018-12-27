package com.il360.shenghecar.activity.user;

import android.app.ProgressDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.user.UserCommonMessage;
import com.il360.shenghecar.util.AESEncryptor;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ThreeDES;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.act_password_modify)
public class PasswordModifyActivity extends BaseWidgetActivity {
    @ViewById
    EditText etOldPwd;
    @ViewById
    EditText etNewPwd1;
    @ViewById
    EditText etNewPwd2;
    @ViewById
    Button btnOk;

    protected ProgressDialog transDialog;

    @AfterTextChange
    void etOldPwd() {
        validateInput();
    }

    @AfterTextChange
    void etNewPwd1() {
        validateInput();
    }

    @AfterTextChange
    void etNewPwd2() {
        validateInput();
    }

    /**
     * 验证用户基本输入，修改按钮状态
     */
    private void validateInput() {
        if (!ViewUtil.getText(etOldPwd).equals("") && !ViewUtil.getText(etNewPwd1).equals("")
                && !ViewUtil.getText(etNewPwd2).equals("")) {
            btnOk.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
        }
    }

    @Click
    void btnOk() {
        String newPwd1 = ViewUtil.getText(etNewPwd1);
        String newPwd2 = ViewUtil.getText(etNewPwd2);
        if (newPwd1.length() >= 6) {
            if (newPwd1.equals(newPwd2)) {

                transDialog = ProgressDialog.show(PasswordModifyActivity.this, null, "请稍等...", true);
                ExecuteTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initData();
                        } catch (Exception e) {
                            LogUmeng.reportError(PasswordModifyActivity.this, e);
                            showInfo(getString(R.string.A7));
                        }
                    }
                });
            } else {
                showInfo(getString(R.string.password_different));
            }
        } else {
            showInfo("密码至少6位");
        }
    }

    private void initData() {
        Map<String, String> params;
        try {
            params = new HashMap<String, String>();
            params.put("oldPwd", AESEncryptor.encrypt(ThreeDES.encryptDESCBC(ViewUtil.getText(etOldPwd))));
            params.put("newPwd", AESEncryptor.encrypt(ThreeDES.encryptDESCBC(ViewUtil.getText(etNewPwd1))));
            params.put("token", UserUtil.getToken());
            TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "user/updateUserPwd", params);
            if (result.getSuccess()) {
                JSONObject obj = new JSONObject(result.getResult());
                if (obj.getInt("code") == 1) {
                    JSONObject objRes = obj.getJSONObject("result");
                    UserCommonMessage response = FastJsonUtils.getSingleBean(objRes.toString(), UserCommonMessage.class);
                    if (response.getReturnCode() == 1) {

                        PasswordModifyActivity.this.finish();
                    }
                    showInfo(response.getReturnMessage());
                } else {
                    showInfo(obj.getString("desc"));
                }
            } else {
                showInfo(result.getResult());
            }
        } catch (Exception e) {
            LogUmeng.reportError(PasswordModifyActivity.this, e);
            showInfo(getString(R.string.A7));
        } finally {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (transDialog != null && transDialog.isShowing()) {
                        transDialog.dismiss();
                    }
                }
            });
        }
    }

    private void showInfo(final String info) {

        runOnUiThread(new Runnable() {
            public void run() {
                if (transDialog != null && transDialog.isShowing()) {
                    transDialog.dismiss();
                }
                Toast.makeText(PasswordModifyActivity.this, info, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
