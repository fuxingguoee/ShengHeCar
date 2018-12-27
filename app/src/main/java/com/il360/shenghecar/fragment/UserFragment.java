package com.il360.shenghecar.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.WelcomeActivity;
import com.il360.shenghecar.activity.main.UrlToWebActivity_;
import com.il360.shenghecar.activity.mydata.AuthenActivity_;
import com.il360.shenghecar.activity.mydata.AutoVerifiedActivity_;
import com.il360.shenghecar.activity.order.GoodsOrderActivity_;
import com.il360.shenghecar.activity.order.RepaymentActivity_;
import com.il360.shenghecar.activity.user.AboutUsActivity_;
import com.il360.shenghecar.activity.user.AccountPictureActivity_;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.activity.user.ManageActivity_;
import com.il360.shenghecar.activity.user.MyInfoActivity_;
import com.il360.shenghecar.activity.user.VerifiedActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.coupon.ArrayOfCoupon;
import com.il360.shenghecar.model.home.ArrayOfSwitch;
import com.il360.shenghecar.model.home.OutContact;
import com.il360.shenghecar.model.home.RemainTimes;
import com.il360.shenghecar.model.user.OutUserRz;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.PicUtil;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.SDCardUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CircleImageView;
import com.il360.shenghecar.view.MyGridView;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.GetObjectRequest;
import com.tencent.cos.task.listener.IDownloadTaskListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@EFragment(R.layout.fra_user)
public class UserFragment extends MyFragment{
	
	@ViewById PullToRefreshScrollView pull_refresh_scrollview;
	@ViewById ImageView iv_share;
	@ViewById ImageView iv_setting;

	@ViewById RelativeLayout rlUserInfo;//已登录
	@ViewById CircleImageView userImage;
	@ViewById TextView userName;
	@ViewById TextView loginName;
	@ViewById ImageView isVerified;
	
	@ViewById RelativeLayout rlNoLogin;//未登录
	@ViewById CircleImageView noLoginImage;
	@ViewById TextView tvLogin;

	@ViewById MyGridView gvMyServer;
	@ViewById MyGridView gvMore;

	@ViewById LinearLayout llBaiTiaoBill;
	@ViewById LinearLayout llShopBill;


	private SimpleAdapter myServerAdapter;
	private SimpleAdapter moreAdapter;

	
	@ViewById TextView tvLoginOut;//退出登录
	
	public static final int CODE_SECCESS = 1011;// 成功
	public static final int CODE_NEED_BACK = 1012;// 需要返回结果
	
	final File file = new File(Variables.APP_CACHE_SDPATH);

	protected ProgressDialog transDialog;
	
	private Handler handler = null;
	
	private String picPath = "";
	
	@Override
	public void onResume() {
		super.onResume();
		init();
	}
	
	@AfterViews
	void initViews() {
		
		//创建属于主线程的handler  
		handler = new Handler();
		initView();
	}

	private void initView() {
		int[] gvMyServerPics = { R.mipmap.ic_server_rzzl,R.mipmap.ic_server_zhgl, R.mipmap.ic_server_lxwm};
		String[] strMyServer = getResources().getStringArray(R.array.main_manager_server);
		ArrayList<HashMap<String, Object>> listMyServer = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gvMyServerPics.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", gvMyServerPics[i]);
			map.put("title", strMyServer[i]);
			listMyServer.add(map);
		}
		myServerAdapter = new SimpleAdapter(getActivity(), listMyServer,R.layout.griditem_main_home_menus,
				new String[] { "image", "title" }, new int[] { R.id.main_menus_image,R.id.main_menus_title });
		gvMyServer.setAdapter(myServerAdapter);
		gvMyServer.setOnItemClickListener(new OnItemClickListenerForMyServer());



		int[] gvMorePics = {R.mipmap.ic_more_bzzx,R.mipmap.ic_more_xsyd, R.mipmap.ic_more_gywm};
		String[] strMore = getResources().getStringArray(R.array.main_manager_more);
		ArrayList<HashMap<String, Object>> listMore = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gvMorePics.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", gvMorePics[i]);
			map.put("title", strMore[i]);
			listMore.add(map);
		}
		moreAdapter = new SimpleAdapter(getActivity(), listMore,R.layout.griditem_main_home_menus,
				new String[] { "image", "title" }, new int[] { R.id.main_menus_image,R.id.main_menus_title });
		gvMore.setAdapter(moreAdapter);
		gvMore.setOnItemClickListener(new OnItemClickListenerForMore());
	}
	
	
	private void initPull() {
		pull_refresh_scrollview.setMode(Mode.PULL_FROM_START);
		pull_refresh_scrollview.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (UserUtil.judgeUserInfo()) {
					initSwitch();
//					initCoupon();
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
	
	private void initCoupon() {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				GlobalPara.userCouponList = null;
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("status", "1");
					params.put("useScope", "1");// 1全额2分期
					params.put("token", UserUtil.getToken());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"coupon/queryUserCoupon", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							JSONObject objRes = obj.getJSONObject("result");
							JSONObject objResRes = objRes.getJSONObject("returnResult");
							ArrayOfCoupon response = FastJsonUtils.getSingleBean(objResRes.toString(),
									ArrayOfCoupon.class);
							if (response != null && response.getTotalCount() > 0) {
								GlobalPara.userCouponList = response.getList();
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}
	
	protected void initUserRz() {
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
							Intent intent = new Intent(getActivity(), LoginActivity_.class);
							startActivity(intent);
						} else {
							JSONObject obj = new JSONObject(result.getResult());
							if (obj.getInt("code") == 1) {
								GlobalPara.outUserRz = null;
								JSONObject objRes = obj.getJSONObject("result");
								JSONObject objRetRes = objRes.getJSONObject("returnResult");
								GlobalPara.outUserRz = FastJsonUtils.getSingleBean(objRetRes.toString(), OutUserRz.class);
								if(UserUtil.judgeAuthentication()){
									if(GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getAppCount() != null && GlobalPara.getOutUserRz().getAppCount() == 1){
										//已上传app列表
									} else {
										upAPPName();//上传app列表
									}
								}
							}
						}
					}
				} catch (Exception e) {
					Log.e("UserFragment", "initUserReg", e);
					LogUmeng.reportError(getActivity(), e);
				}finally {
					FragmentActivity fragAct = getActivity();
					if (fragAct != null) {
						fragAct.runOnUiThread(new Runnable() {
							public void run() {
								init();
								pull_refresh_scrollview.onRefreshComplete();
							}
						});
					}
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
						ArrayOfSwitch arrayOfSwitch = FastJsonUtils.getSingleBean(obj.toString(),ArrayOfSwitch.class);
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
	
	private String makeJsonPost() {
		OutContact outContact = new OutContact();
		outContact.setAppList(GlobalPara.getAppNameList());
		return FastJsonUtils.getJsonString(outContact);
	}

	private void upAPPName() {
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


	void init() {
		if (UserUtil.judgeUserInfo()) { // 是否登录
			initUserHead();
			initPull();
			tvLoginOut.setVisibility(View.VISIBLE);
			rlUserInfo.setVisibility(View.VISIBLE);
			rlNoLogin.setVisibility(View.GONE);
			if (UserUtil.judgeAuthentication()) { // 是否实名
				userName.setText(GlobalPara.getOutUserRz().getName());
				userName.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
				isVerified.setBackgroundResource(R.drawable.ic_verified);
			} else {
				userName.setTextColor(ContextCompat.getColor(getActivity(), R.color.deepskyblue));
				userName.setText("点击实名认证");
				userName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
				userName.getPaint().setAntiAlias(true);// 抗锯齿
				isVerified.setBackgroundResource(R.drawable.ic_unverified);
			}
			loginName.setText(UserUtil.getUserInfo().getLoginName());
			
		} else {
			rlNoLogin.setVisibility(View.VISIBLE);
			tvLoginOut.setVisibility(View.GONE);
			rlUserInfo.setVisibility(View.GONE);
			noLoginImage.setImageResource(R.drawable.ic_touxiang);
		}

	}

	/** 显示用户头像 */
	private void initUserHead() {
		FragmentActivity fragAct = getActivity();
		if (fragAct != null) {
			fragAct.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (UserUtil.getUserInfo() != null &&  !TextUtils.isEmpty(UserUtil.getUserInfo().getTxyUserPic())
							&& SDCardUtil.hasSDCard(getActivity())) {
						initSignForUrl();
					} else {
						userImage.setImageResource(R.drawable.ic_touxiang);
					}
				}
			});
		}
	}
	
	protected void initSignForUrl() {
		transDialog = ProgressDialog.show(getActivity(), null, "加载中...", true);
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("picurl", UserUtil.getUserInfo().getTxyUserPic());
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "card/querySign", params);
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
					Log.e("UserFragment", "initSignForUrl()", e);
					LogUmeng.reportError(getActivity(), e);
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
    Runnable  runnableUi=new  Runnable(){  
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


	class OnItemClickListenerForMyServer implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			try {
				Intent intent = null;
				switch (position + 1) {

//					case 0: // 优惠券
//						if (UserUtil.judgeUserInfo()) {
//							intent = new Intent(getActivity(), MyCouponsActivity_.class);
//						} else {
//							intent = new Intent(getActivity(), LoginActivity_.class);
//						}
//						break;

					case 1: // 认证资料
						if (UserUtil.judgeUserInfo()) {
							intent = new Intent(getActivity(), AuthenActivity_.class);
						} else {
							intent = new Intent(getActivity(), LoginActivity_.class);
						}
						break;

					case 2: // 账户管理
						if (UserUtil.judgeUserInfo()) {
							intent = new Intent(getActivity(), ManageActivity_.class);
						} else {
							intent = new Intent(getActivity(), LoginActivity_.class);
						}
						break;

					case 3: // 联系我们（）
						if (!TextUtils.isEmpty(GlobalPara.getTelephone())) {
							intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + GlobalPara.getTelephone()));
						}
						break;

					default:
						showInfo("正在火热开发中...");
				}
				if (null == intent) {
					return;
				}
				getActivity().startActivityForResult(intent, 3);
			} catch (Exception e) {
			}
		}
	}


	class OnItemClickListenerForMore implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			try {
				Intent intent = null;
				switch (position + 1) {

//					case 0: // 分享有礼
//						if (UserUtil.judgeUserInfo()) {
//
//						} else {
//							intent = new Intent(getActivity(), LoginActivity_.class);
//						}
//						break;

					case 1: // 帮助中心
						intent = new Intent(getActivity(), UrlToWebActivity_.class);
						intent.putExtra("title","帮助中心");
						intent.putExtra("url", "http://www.wxkj2018.com/xjsc/xjsc-help.html");
						break;

					case 2: // 新手引导
						intent = new Intent(getActivity(), UrlToWebActivity_.class);
						intent.putExtra("title","新手引导");
						intent.putExtra("url", "http://www.wxkj2018.com/xjsc/xjsc-guide.html");
						break;

					case 3: // 关于我们
						intent = new Intent(getActivity(), AboutUsActivity_.class);
						break;

					default:
						showInfo("正在火热开发中...");
				}
				if (null == intent) {
					return;
				}
				getActivity().startActivityForResult(intent, 3);
			} catch (Exception e) {
			}
		}
	}


	@Click
	void iv_share() {
//		if (UserUtil.judgeUserInfo()) {
			platformShare();
//		} else {
//			Intent intent = new Intent(getActivity(), LoginActivity_.class);
//			startActivity(intent);
//		}
	}
	
	@Click
	void iv_setting(){
//		Intent intent = new Intent(getActivity(), SettingActivity_.class);
//		startActivity(intent);
	}

	@Click
	void llBaiTiaoBill() {
		if (UserUtil.judgeUserInfo()) {
			Intent intent = new Intent(getActivity(), RepaymentActivity_.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity_.class);
			startActivity(intent);
		}
	}

	@Click
	void llShopBill() {
		if (UserUtil.judgeUserInfo()) {
			Intent intent = new Intent(getActivity(), GoodsOrderActivity_.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity_.class);
			startActivity(intent);
		}
	}
	
	@Click
	void userName() {
		if(!UserUtil.judgeAuthentication()){
			gotoVerified();
		} else {
			Intent intent = new Intent(getActivity(), MyInfoActivity_.class);
			startActivity(intent);
		}
	}
	
	private void gotoVerified() {
		Intent intent = new Intent();
		if (GlobalPara.getCanAutoVerified() && GlobalPara.getRemainTimes() > 0 && GlobalPara.getOutUserRz() != null
				&& GlobalPara.getOutUserRz().getNameRz() != null && GlobalPara.getOutUserRz().getNameRz() == 0) {
			intent.setClass(getActivity(), AutoVerifiedActivity_.class);
		} else {
			intent.setClass(getActivity(), VerifiedActivity_.class);
		}
		startActivity(intent);
	}
	
	@Click
	void userImage() {//修改头像
		Intent intent = new Intent(getActivity(), AccountPictureActivity_.class);
		startActivity(intent);
	}
	
	@Click
	void tvLogin(){//登录
		Intent intent = new Intent(getActivity(), LoginActivity_.class);
		startActivity(intent);
	}
	
	@Click
	void tvLoginOut(){//退出登录
		GlobalPara.clean();
		UserUtil.clearUserInfo();
		Intent intent = new Intent(getActivity(), LoginActivity_.class);
		startActivity(intent);
	}



	private void showInfo(final String info) {
		FragmentActivity fragAct = getActivity();
		if (fragAct != null) {
			fragAct.runOnUiThread(new Runnable() {
				public void run() {

					if (transDialog != null && transDialog.isShowing()) {
						transDialog.dismiss();
					}
					Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	private String getNameFromUrl(String loadUrl) {
		String[] a = loadUrl.split("/");
		String s = a[a.length - 1];
		return s;
	}
}
