package com.il360.shenghecar.activity.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.home.InfoActivity_;
import com.il360.shenghecar.captcha.Captcha;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.user.LoginUserReturnMessage;
import com.il360.shenghecar.model.user.SliderInfo;
import com.il360.shenghecar.util.AESEncryptor;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ImagePathFromTxyUtil;
import com.il360.shenghecar.util.SharedPreferencesUtil;
import com.il360.shenghecar.util.ThreeDES;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.ViewUtil;
import com.il360.shenghecar.view.CustomDialog;
import com.umeng.message.PushAgent;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EActivity(R.layout.act_login)
public class LoginActivity extends BaseWidgetActivity {

	@ViewById
	LinearLayout llLoginPwd;//密码登录
	@ViewById
	TextView line1;
	@ViewById
	RelativeLayout rlLoginPwd;

	@ViewById
	LinearLayout llVerificaCode;//短信登录
	@ViewById
	TextView line2;
	@ViewById
	RelativeLayout rlVerificaCode;
	@ViewById
	EditText etVerificaCode;
	@ViewById
	TextView tvGetCode;

	private int loginType = 1;//1密码登录2短信登录

	/** 倒计时器 */
	private Timer timer = new Timer();
	private static final int TIME_DOWN_COUNT = 60;// 倒计时60秒
	private int time = 0;// 倒计时计时数,初始为0


	/** 登录账户 **/
	@ViewById
	EditText etAccount;
	/** 登录密码 **/
	@ViewById
	EditText etPassword;
	/** 忘记密码 **/
	@ViewById
	TextView tvForgetPwd;
	/** 注册 **/
	@ViewById
	TextView btnRegister;
	/** 登录 **/
	@ViewById
	Button btnLogin;
	/** 密码显示/隐藏 **/
	@ViewById
	LinearLayout lly_isshow_pwd;
	@ViewById
	ImageView iv_isshow_pwd;
	/** 密码当前状态 （显示/隐藏） **/
	private boolean isshow = false;

	protected ProgressDialog transDialog;

	private String device_token;

	private static String encryTop;
	private static String encrtLeft;

	private static String bigImage;
	private static String blockImage;
	private static String top;

	private static Bitmap picPathBigImage;
	private static Bitmap picPathBlockImage;
	private static String left;

	public static String getLeft() {
		return left;
	}

	public static void setLeft(String left) {
		LoginActivity.left = left;
	}

	public static Bitmap getPicPathBigImage() {
		return picPathBigImage;
	}

	public static void setPicPathBigImage(Bitmap picPathBigImage) {
		LoginActivity.picPathBigImage = picPathBigImage;
	}

	public static Bitmap getPicPathBlockImage() {
		return picPathBlockImage;
	}

	public static void setPicPathBlockImage(Bitmap picPathBlockImage) {
		LoginActivity.picPathBlockImage = picPathBlockImage;
	}

	private int flag = 0;
	private int flag2 = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		PushAgent mPushAgent = PushAgent.getInstance(this);
		device_token = mPushAgent.getRegistrationId();
	}

	@AfterViews
	void init() {
//		GlobalPara.appNameList = AppInfoUtil.getAppNameList(LoginActivity.this);

		initPop();
		initTimeDown();

		loginType = 1;

		if(SharedPreferencesUtil.getLastLoginName().length() == 11) {
			etAccount.setText(SharedPreferencesUtil.getLastLoginName());
			etPassword.setFocusable(true);
			etPassword.setFocusableInTouchMode(true);
			etPassword.requestFocus();
		}

	}

	/** 验证倒计时 */
	private void initTimeDown() {
		timer.schedule(task, 100, 1000);
	}

	/** 倒计时的内容 */
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(time--);
		}
	};

	/** handler */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what > 0) {
				tvGetCode.setText(msg.what + "秒后重发");
				tvGetCode.setEnabled(false);
			} else {
				if (!ViewUtil.getText(etAccount).equals("")) {
					if (validateAccount()) {
						tvGetCode.setText("发送验证码");
						tvGetCode.setEnabled(true);
					} else {
						tvGetCode.setEnabled(false);
					}
				} else {
					tvGetCode.setEnabled(false);
				}
			}
		};
	};

	@Click
	void tvGetCode() {
		tvGetCode.setClickable(false);
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", etAccount.getText().toString());
					params.put("verifyType", "3");// 3用户登录验证码
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"user/attachUserVerifyCode", params);
					if (result.getSuccess()) {
						try {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								JSONObject objRes = obj.getJSONObject("result");
								String desc = (String) objRes.getString("returnMessage");
								showInfo(desc);
								time = TIME_DOWN_COUNT;
							} else {
								showInfo(obj.getString("result"));
							}
						} catch (JSONException e) {
							Log.e("LoginActivity", "tvGetCode", e);
							LogUmeng.reportError(LoginActivity.this, e);
						}
					} else {
						showInfo(getResources().getString(R.string.A6));
					}
				} catch (Exception e) {
					showInfo(getResources().getString(R.string.A2));
				} finally {
					if (!tvGetCode.isClickable()) {
						tvGetCode.setClickable(true);
					}
				}
			}
		});
	}

	@AfterTextChange
	void etAccount() {
		validateLogin();
	}

	@AfterTextChange
	void etPassword() {
		validateLogin();
	}

	@AfterTextChange
	void etVerificaCode() {
		validateLogin();
	}

	@Click
	void llLoginPwd() {
		line1.setVisibility(View.VISIBLE);
		rlLoginPwd.setVisibility(View.VISIBLE);
		line2.setVisibility(View.INVISIBLE);
		rlVerificaCode.setVisibility(View.GONE);
		loginType = 1;
		validateLogin();
	}

	@Click
	void llVerificaCode() {
		line1.setVisibility(View.INVISIBLE);
		rlLoginPwd.setVisibility(View.GONE);
		line2.setVisibility(View.VISIBLE);
		rlVerificaCode.setVisibility(View.VISIBLE);
		loginType = 2;
		validateLogin();
	}

	/**
	 * 密码显示/隐藏
	 */
	@Click
	void lly_isshow_pwd() {
		if (isshow) {
			isshow = false;
			iv_isshow_pwd.setBackgroundResource(R.drawable.ic_login_pwd_hide);
			etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());// 隐藏
		} else {
			isshow = true;
			iv_isshow_pwd.setBackgroundResource(R.drawable.ic_login_pwd_show);
			etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// 显示
		}
	}

	/**
	 * 忘记密码
	 */
	@Click
	void tvForgetPwd() {
		Intent intent = new Intent(LoginActivity.this, GetBackPasswordActivity_.class);// 手机找回
		startActivity(intent);
	}

	/**
	 * 注册
	 */
	@Click
	void btnRegister() {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity_.class);
		startActivity(intent);
	}

	/**
	 * 登录
	 */
	@Click                                              //隐藏软键盘
	void btnLogin(View view) {
		if (isok()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// 获取软键盘的显示状态
			if(imm.isActive()) {
			//	if(imm!=null){
				// 强制隐藏软键盘
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}

			if(flag == 1) {
				runOnUiThread(new Runnable() {
					public void run() {
						showCaptcha();
					}
				});
			} else {
				initLogin();
			}
		}
	}

	private boolean isok() {
		if (ViewUtil.getText(etAccount).length() != 11 || !validateAccount()) {
			showInfo(getString(R.string.account_format_error));
		} else if (loginType == 1 && ViewUtil.getText(etPassword).length() < 6) {
			showInfo("登录密码至少6位");
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 验证登录按钮可用状态
	 */
	private void validateLogin() {
		if (ViewUtil.getText(etAccount).length() > 0) {
			if(loginType == 1 && ViewUtil.getText(etPassword).length() > 5){
				btnLogin.setEnabled(true);
			} else if(loginType == 2 && ViewUtil.getText(etVerificaCode).length() > 3){
				btnLogin.setEnabled(true);
			} else {
				btnLogin.setEnabled(false);
			}
		} else {
			btnLogin.setEnabled(false);
		}
	}

	/**
	 * 验证用户名输入格式规范 以字母开头，数字、下划线、“.“号组合，不超过16位
	 *
	 * @return Boolean true:正常; false:错误;
	 */
	private boolean validateAccount() {
		Pattern p1 = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");//     正则 手机号码new Regex(@"^1[3|4|5|7|8][0-9]\d{8}$").IsMatch(phone)
		Matcher m1 = p1.matcher(etAccount.getText().toString().trim());
		return m1.matches();
	}

	private void initLogin() {

		if(flag == 0){
			btnLogin.setClickable(false);
			transDialog = ProgressDialog.show(LoginActivity.this, null, "加载中...", true);
		}

		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("loginName", ViewUtil.getText(etAccount));
					params.put("type", loginType + "");
					if(flag == 1 || flag ==2){
						params.put("param1", encrtLeft);
						params.put("param2",encryTop);
					}
					params.put("loginPwd", AESEncryptor.encrypt(ThreeDES.encryptDESCBC(ViewUtil.getText(etPassword))));
					if (loginType == 1) {
						params.put("loginPwd", AESEncryptor.encrypt(ThreeDES.encryptDESCBC(ViewUtil.getText(etPassword))));
					} else if (loginType == 2) {
						params.put("verifyCode", ViewUtil.getText(etVerificaCode));
					}
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "user/loginIn",params);
					flag = 0;

					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							runOnUiThread(new Runnable() {
								public void run() {
									popWin.dismiss();
								}
							});

							SharedPreferencesUtil.setLastLoginName(ViewUtil.getText(etAccount));

							JSONObject objRes = obj.getJSONObject("result");
							LoginUserReturnMessage msg = FastJsonUtils.getSingleBean(objRes.toString(),
									LoginUserReturnMessage.class);

								if (msg.getReturnCode() == 1) { //登录成功
									UserUtil.loadUserInfo(msg.getReturnResult());//未登记提示去登记
									if(UserUtil.getUserInfo().getCheck()!=null&&UserUtil.getUserInfo().getCheck()==0) {
										//showInfo(String.valueOf(UserUtil.getUserInfo().getCheck()));
										runOnUiThread(new Runnable() {
											public void run() {
												showDialog2();
											//	doGoto();
											}
										});
								} else {
										upDateUser();
										doGoto();
									}

								} else {
									showInfo(msg.getReturnMessage());
								}
						} else if (obj.getInt("code") == -3) {//密码出错
							flag = 1;
							LoginActivity.setPicPathBigImage(null);
							LoginActivity.setPicPathBlockImage(null);
							showInfo(obj.getString("desc"));
							JSONObject objRes = obj.getJSONObject("result");
							SliderInfo info = FastJsonUtils.getSingleBean(objRes.toString(),
									SliderInfo.class);
							bigImage = info.getBigImage();
							blockImage = info.getBlockImage();
							top = AESEncryptor.decrypt(AESEncryptor.KEY, info.getCaptchaParam());

							ImagePathFromTxyUtil.loadImage(LoginActivity.this, bigImage, "1");
							ImagePathFromTxyUtil.loadImage(LoginActivity.this, blockImage, "2");

							runOnUiThread(new Runnable() {
								public void run() {
									popWin.dismiss();
								}
							});

						} else if (obj.getInt("code") == -100) {//滑块滑错
							flag = 2;
							LoginActivity.setPicPathBigImage(null);
							LoginActivity.setPicPathBlockImage(null);
							showInfo(obj.getString("desc"));
							JSONObject objRes = obj.getJSONObject("result");
							SliderInfo info = FastJsonUtils.getSingleBean(objRes.toString(),
									SliderInfo.class);
							bigImage = info.getBigImage();
							blockImage = info.getBlockImage();
							top = AESEncryptor.decrypt(AESEncryptor.KEY, info.getCaptchaParam());

							ImagePathFromTxyUtil.loadImage(LoginActivity.this, bigImage, "1");
							ImagePathFromTxyUtil.loadImage(LoginActivity.this, blockImage, "2");

							Thread.sleep(1500);

							if (LoginActivity.getPicPathBigImage() != null && LoginActivity.getPicPathBlockImage() != null) {
								runOnUiThread(new Runnable() {
									public void run() {
										showCaptcha();//弹框
									}
								});
							}

						} else if (obj.getInt("code") == -200) {//滑块滑错5次
							flag = 0;
							showInfo(obj.getString("desc"));
							runOnUiThread(new Runnable() {
								public void run() {
									popWin.dismiss();
								}
							});
						} else if (obj.getInt("code") == -400) {//验证码信息不正确
							flag = 1;
							LoginActivity.setPicPathBigImage(null);
							LoginActivity.setPicPathBlockImage(null);
							JSONObject objRes = obj.getJSONObject("result");
							SliderInfo info = FastJsonUtils.getSingleBean(objRes.toString(),
									SliderInfo.class);
							bigImage = info.getBigImage();
							blockImage = info.getBlockImage();
							top = AESEncryptor.decrypt(AESEncryptor.KEY, info.getCaptchaParam());

							ImagePathFromTxyUtil.loadImage(LoginActivity.this, bigImage, "1");
							ImagePathFromTxyUtil.loadImage(LoginActivity.this, blockImage, "2");

							Thread.sleep(1500);

							if (LoginActivity.getPicPathBigImage() != null && LoginActivity.getPicPathBlockImage() != null) {
								runOnUiThread(new Runnable() {
									public void run() {
										showCaptcha();
									}
								});
							}
						} else {
							runOnUiThread(new Runnable() {
								public void run() {

									popWin.dismiss();
								}
							});
							if(loginType == 1) {
								showInfo(obj.getString("desc"));
							} else {
								showInfo(obj.getString("result"));
							}
						}
					} else {
						showInfo(result.getResult());
					}

				} catch (Exception e) {
					showInfo("网络请求失败");
					Log.e("LoginActivity", "initLogin", e);
					LogUmeng.reportError(LoginActivity.this, e);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!btnLogin.isClickable()) {
								btnLogin.setClickable(true);
							}

							if (transDialog != null && transDialog.isShowing()) {
								transDialog.dismiss();
							}

						}
					});

				}
			}
		});
	}

	protected void upDateUser() {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("userJson", makeUserJson());// userJson
			params.put("token", UserUtil.getToken());
			TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "user/updateUser", params);
			if (result.getSuccess()) {}
		} catch (Exception e) {
			Log.e("LoginActivity", "upDateUser", e);
			LogUmeng.reportError(LoginActivity.this, e);
		}
	}

	private String makeUserJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("deviceTokens", device_token);
			return json.toString();
		} catch (Exception e) {
			Log.e("UserInfoFragment", "makeJsonPost", e);
		}
		return null;
	}

	private void doGoto() {
		runOnUiThread(new Runnable() {
			public void run() {
//				ViewUtil.backHomeActivity(LoginActivity.this);
				LoginActivity.this.finish();
			}
		});
	}

	/** pop */
	private View pop;
	private PopupWindow popWin;
	/** 初始化Pop */
	private void initPop() {
		pop = LayoutInflater.from(LoginActivity.this).inflate(R.layout.view_pop_captcha_show, null);
		popWin = new PopupWindow(pop, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		popWin.setFocusable(true);
	}

	private void showCaptcha() {
		final Captcha captcha = (Captcha)pop.findViewById(R.id.captCha);
		ImageView ivCancel = (ImageView)pop.findViewById(R.id.ivCancel);
		captcha.setSeekBarStyle(R.drawable.po_seekbar,R.drawable.thumb_bg);

		new Thread(new Runnable() {
			@Override
			public void run() {
				getCaptcha(captcha);
			}
		}).start();

		captcha.setCaptchaListener(new Captcha.CaptchaListener() {
			@Override
			public String onAccess(long time) {
//				Toast.makeText(LoginActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
				double value = Long.valueOf(time).doubleValue()/ 1000;//long转double

//				if(flag != 2){
//					popWin.dismiss();//点击pop外围消失
//				}

				try {
					encryTop = ThreeDES.encryptDESCBC(top);
					encrtLeft = ThreeDES.encryptDESCBC(LoginActivity.getLeft());

				} catch (Exception e) {
					e.printStackTrace();
				}
				initLogin();

				return String.format("耗时%1$.1f秒", value);

			}

			@Override
			public String onFailed(int count) {
				Toast.makeText(LoginActivity.this, "验证失败,失败次数" + count, Toast.LENGTH_SHORT).show();
				return "验证失败";
			}

			@Override
			public String onMaxFailed() {
				Toast.makeText(LoginActivity.this, "验证超过次数，你的帐号被封锁", Toast.LENGTH_SHORT).show();
				return "可以走了";
			}
		});

		ivCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				doGoto();
				popWin.dismiss();//点击pop外围消失
			}
		});

		Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
			@Override
			public boolean queueIdle() {
				popWin.showAtLocation(pop,Gravity.CENTER, 0, 0);
				return false;
			}
		});
	}

	private  void getCaptcha(final Captcha captcha) {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					captcha.setBitmap(LoginActivity.getPicPathBigImage(), LoginActivity.getPicPathBlockImage(), Integer.valueOf(top));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (transDialog != null && transDialog.isShowing()) {
					transDialog.dismiss();
				}
				Toast.makeText(LoginActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showDialog2() {//信息未登记提示前往登记
		CustomDialog.Builder builder = new CustomDialog.Builder(LoginActivity.this);

		builder.setTitle("友情提示");
		builder.setMessage("信息未登记请去登记");
//		builder.setPositiveButton("取消", new android.content.DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				doGoto();
//			}
//		});
		builder.setNegativeButton("去登记", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent = new Intent(LoginActivity.this, InfoActivity_.class);
				startActivity(intent);
				dialog.dismiss();
				doGoto();
			}
		});
		builder.create().show();
	}
}
