package com.il360.shenghecar.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.main.MainActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.common.MyApplication;
import com.il360.shenghecar.common.MyPermissions;
import com.il360.shenghecar.common.URLFactory;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.home.ArrayOfAdvert;
import com.il360.shenghecar.model.hua.ArrayOfCardConfig;
import com.il360.shenghecar.model.hua.CardConfig;
import com.il360.shenghecar.util.AppUtil.AppUtil;
import com.il360.shenghecar.util.AppUtil.PermissionsResultListener;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.FileUtil;
import com.il360.shenghecar.util.SharedPreferencesUtil;
import com.il360.shenghecar.util.StartPageLayout;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.GetObjectRequest;
import com.tencent.cos.task.listener.IDownloadTaskListener;
import com.umeng.message.PushAgent;

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

@EActivity(R.layout.act_welcome)
public class WelcomeActivity extends AppCompatActivity {
	
	public static String TAG;// 日志过滤TAG
    protected Context mContext;
    
    private List<CardConfig> list = null;
	private static COSClient cos;
	
	@ViewById ImageView ivStartPage;
	
	@ViewById ViewPager vp_adv_change;
	@ViewById LinearLayout ll_adv_circle;
	@ViewById TextView tvPass;
	
	private int pageNum;
    
 // For Android 6.0
    private PermissionsResultListener mListener;
    //申请标记值
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    //手动开启权限requestCode
    public static final int SETTINGS_REQUEST_CODE = 200;
    //拒绝权限后是否关闭界面或APP
    private boolean mNeedFinish = false;
    //界面传递过来的权限列表,用于二次申请
    private ArrayList<String> mPermissionsList = new ArrayList<String>();
	
    private void initConfig() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"goods/queryConfig", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							JSONObject objRes = obj.getJSONObject("result");
							JSONObject objRetRes = objRes.getJSONObject("returnResult");
							ArrayOfCardConfig arrayOfCardConfig = FastJsonUtils.getSingleBean(objRetRes.toString(),ArrayOfCardConfig.class);
							if (arrayOfCardConfig.getList() != null && arrayOfCardConfig.getList().size() > 0) {
								list = arrayOfCardConfig.getList();
								GlobalPara.cardConfigList = arrayOfCardConfig.getList();

								for (int i = 0; i < list.size(); i++) {
									if (list.get(i).getConfigGroup().equals("txy") && list.get(i).getConfigName().equals("appId")) {
										GlobalPara.cosAppID = list.get(i).getConfigValue();
									} else if (list.get(i).getConfigGroup().equals("txy") && list.get(i).getConfigName().equals("bucketName")) {
										GlobalPara.cosName = list.get(i).getConfigValue();
									} else if (list.get(i).getConfigGroup().equals("txy") && list.get(i).getConfigName().equals("cosEndpoint")) {
										GlobalPara.cosEndPoint = list.get(i).getConfigValue();
									} else if(list.get(i).getConfigGroup().equals("app") && list.get(i).getConfigName().equals("telephone")){
										GlobalPara.telephone = list.get(i).getConfigValue();
									} else if(list.get(i).getConfigGroup().equals("discover") && list.get(i).getConfigName().equals("discoverHomeUrl")) {
										GlobalPara.discoverHomeUrl = list.get(i).getConfigValue();
									}
								}
								initCOSClient();
							}
						} else {
							showInfo(obj.getString("desc"));
						}
					} else {
						showInfo(getString(R.string.A6));
					}
				} catch (Exception e) {
					Log.e("WelcomeActivity", "initConfig()", e);
					LogUmeng.reportError(WelcomeActivity.this, e);
					showInfo(getString(R.string.A2));
				}
			}
		});
		
	}
	
	
	public static COSClient getCOSClient(){
		return cos;
	}
	
	public void initCOSClient(){
		String appid = GlobalPara.getCosAppID();
		String peristenceId = null;

		// 创建COSClientConfig对象，根据需要修改默认的配置参数
		COSClientConfig config = new COSClientConfig();
		// 如设置园区
		config.setEndPoint(GlobalPara.getCosEndPoint());

		cos = new COSClient(MyApplication.getContextObject(), appid, config, peristenceId);
	}
	
	
	
	private void initStartPage() {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("type", "4");
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL,
							"card/queryAdvertList", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							JSONObject objRes = obj.getJSONObject("result");
							JSONObject objRetRes = objRes.getJSONObject("returnResult");
							ArrayOfAdvert arrayOfAdvert = FastJsonUtils.getSingleBean(objRetRes.toString(), ArrayOfAdvert.class);
							if(arrayOfAdvert != null && arrayOfAdvert.getTotalCount() != null && arrayOfAdvert.getTotalCount() > 0) {
								initStartPagePic();//初始化
								for (int i = 0; i < arrayOfAdvert.getTotalCount(); i++) {
									initSignForUrl(arrayOfAdvert.getList().get(i).getPicUrl());
								}
								
							}
						}
					}
				} catch (Exception e) {
					Log.e("WelcomeActivity", "initStartPage", e);
				}
			}
		});
	}

	protected void initSignForUrl(final String picUrl) {
		ExecuteTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("picurl", picUrl);
					TResult<Boolean, String> result = HttpRequestUtil.sendGetRequest(UrlEnum.BIZ_URL, "card/querySign", params);
					if (result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							loadPic(obj.getString("result"),picUrl);
						}
					}
				} catch (Exception e) {
				}
			}
		});
	}


	private void loadPic(String sign, final String picUrl) {
		
		final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "load";

		GetObjectRequest getObjectRequest = new GetObjectRequest(picUrl, savePath);
		getObjectRequest.setSign(sign);
		getObjectRequest.setListener(new IDownloadTaskListener() {
			@Override
			public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
			}

			@Override
			public void onSuccess(COSRequest cosRequest, COSResult cosResult) {

				String fileName = getNameFromUrl(picUrl);
				String picPath = savePath + File.separator + fileName;
				setStartPagePic(picPath);
				
				Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
			}

			@Override
			public void onFailed(COSRequest COSRequest, COSResult cosResult) {
				
				Log.w("TEST", "code =" + cosResult.code + "; msg =" + cosResult.msg);
			}

			@Override
			public void onCancel(COSRequest arg0, COSResult arg1) {
				
			}
		});

		WelcomeActivity.getCOSClient().getObject(getObjectRequest);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// 在所有的Activity 的onCreate 方法或在应用的BaseActivity的onCreate方法中添加：
		PushAgent.getInstance(this).onAppStart();
		
		PushAgent mPushAgent = PushAgent.getInstance(this);
		String device_token = mPushAgent.getRegistrationId();
		
		initNetwork();
		initConfig();
		
		 //取消横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //输入法弹出的时候不顶起布局
        //如果我们不设置"adjust..."的属性，对于没有滚动控件的布局来说，采用的是adjustPan方式，
        // 而对于有滚动控件的布局，则是采用的adjustResize方式。
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mContext = this;
		

	}
	
	@AfterViews
	void init(){
		
        requestPermission(MyPermissions.FORCE_REQUIRE_PERMISSIONS, true, new PermissionsResultListener() {
            @Override
            public void onPermissionGranted() {
            	initPageShow();
            }

            @Override
            public void onPermissionDenied() {
            	initPageShow();
            }
        });
        
		initStartPage();
	}

	private void initPageShow() {
		if(SharedPreferencesUtil.getOpenFirstTime()){
			SharedPreferencesUtil.setOpenFirstTime(false);
			List<Integer> listUrl = new ArrayList<Integer>();
			listUrl.add(R.drawable.bg_welcome);
			new StartPageLayout(WelcomeActivity.this, ll_adv_circle, vp_adv_change,tvPass ,listUrl);
			pageNum = listUrl.size();
		} else {
			if(getStartPagePic() != null) {
				List<String> listUrl = new ArrayList<String>();
				
				String[] pics = getStartPagePic().split(";");
				for (int i = 0; i < pics.length; i++) {
					if(FileUtil.isFileExit(pics[i])){
						listUrl.add(pics[i]);
					}
				}
				new StartPageLayout(WelcomeActivity.this, ll_adv_circle, vp_adv_change,tvPass ,listUrl, false);
				pageNum = listUrl.size();
			} else {
				List<Integer> listUrl = new ArrayList<Integer>();
				listUrl.add(R.drawable.bg_welcome);
				new StartPageLayout(WelcomeActivity.this, ll_adv_circle, vp_adv_change,tvPass ,listUrl);
				pageNum = listUrl.size();
			}
		}
		goToMain(pageNum);
	}


	private void goToMain(int pageNum) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if(!WelcomeActivity.this.isFinishing()){
					Intent intent = new Intent(WelcomeActivity.this, MainActivity_.class);
					startActivity(intent);
					WelcomeActivity.this.finish();
				}
			}
		}, pageNum *2000);// 延迟n秒后跳转到MainActivity.java
	}
	
	@Click
	void tvPass() {
		Intent intent = new Intent(WelcomeActivity.this, MainActivity_.class);
		startActivity(intent);
		WelcomeActivity.this.finish();
	}
	
	
	   /**
     * 权限允许或拒绝对话框
     *
     * @param permissions 需要申请的权限
     * @param needFinish  如果必须的权限没有允许的话，是否需要finish当前 Activity
     * @param callback    回调对象
     */
	protected void requestPermission(final ArrayList<String> permissions, final boolean needFinish,
			final PermissionsResultListener callback) {
		if (permissions == null || permissions.size() == 0) {
			return;
		}
		mNeedFinish = needFinish;
		mListener = callback;
		mPermissionsList = permissions;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// 获取未通过的权限列表
			ArrayList<String> newPermissions = checkEachSelfPermission(permissions);
			if (newPermissions.size() > 0) {// 是否有未通过的权限
				requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]));
			} else {// 权限已经都申请通过了
				if (mListener != null) {
					mListener.onPermissionGranted();
				}
			}
		} else {
			if (mListener != null) {
				mListener.onPermissionGranted();
			}
		}
	}
 
    /**
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */
	private void requestEachPermissions(String[] permissions) {
		if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
			showRationaleDialog(permissions);
		} else {
			ActivityCompat.requestPermissions(WelcomeActivity.this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
		}
	}
 
    /**
     * 弹出声明的 Dialog
     *
     * @param permissions
     */
	private void showRationaleDialog(final String[] permissions) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("为了应用可以正常使用，请您点击确认申请权限。")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityCompat.requestPermissions(WelcomeActivity.this, permissions,
								REQUEST_CODE_ASK_PERMISSIONS);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (mNeedFinish)
							finish();
					}
				}).setCancelable(false).show();
	}
 
    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return newPermissions.size > 0 表示有权限需要申请
     */
    private ArrayList<String> checkEachSelfPermission(ArrayList<String> permissions) {
        ArrayList<String> newPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        return newPermissions;
    }
 
    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }
 
    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && permissions != null) {
			// 获取被拒绝的权限列表
			ArrayList<String> deniedPermissions = new ArrayList<>();
			for (String permission : permissions) {
				if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
					deniedPermissions.add(permission);
				}
			}
			// 判断被拒绝的权限中是否有包含必须具备的权限
			ArrayList<String> forceRequirePermissionsDenied = checkForceRequirePermissionDenied(
					MyPermissions.FORCE_REQUIRE_PERMISSIONS, deniedPermissions);
			if (forceRequirePermissionsDenied != null && forceRequirePermissionsDenied.size() > 0) {
				// 必备的权限被拒绝，
				if (mNeedFinish) {
					showPermissionSettingDialog();
				} else {
					if (mListener != null) {
						mListener.onPermissionDenied();
					}
				}
			} else {
				// 不存在必备的权限被拒绝，可以进首页
				if (mListener != null) {
					mListener.onPermissionGranted();
				}
			}
		}
	}
 
    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
 
	private ArrayList<String> checkForceRequirePermissionDenied(ArrayList<String> forceRequirePermissions,
			ArrayList<String> deniedPermissions) {
		ArrayList<String> forceRequirePermissionsDenied = new ArrayList<>();
		if (forceRequirePermissions != null && forceRequirePermissions.size() > 0 && deniedPermissions != null
				&& deniedPermissions.size() > 0) {
			for (String forceRequire : forceRequirePermissions) {
				if (deniedPermissions.contains(forceRequire)) {
					forceRequirePermissionsDenied.add(forceRequire);
				}
			}
		}
		return forceRequirePermissionsDenied;
	}
 
    /**
     * 手动开启权限弹窗
     */
	private void showPermissionSettingDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("必要的权限被拒绝").setPositiveButton("去设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				AppUtil.getAppDetailsSettings(WelcomeActivity.this, SETTINGS_REQUEST_CODE);
//				getAppDetailSettingIntent(WelcomeActivity.this);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
				if (mNeedFinish)
					AppUtil.restart(WelcomeActivity.this);
			}
		}).setCancelable(false).show();
	}
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == SETTINGS_REQUEST_CODE) {
            requestPermission(mPermissionsList, mNeedFinish, mListener);
        }
    }
    
    
//    /**
//     * 跳转到权限设置界面
//     */
//    private void getAppDetailSettingIntent(Context context){
//
//        // vivo 点击设置图标>加速白名单>我的app
//        //      点击软件管理>软件管理权限>软件>我的app>信任该软件
//        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
//        if(appIntent != null){
//            context.startActivity(appIntent);
//            floatingView = new SettingFloatingView(this, "SETTING", getApplication(), 0);
//            floatingView.createFloatingView();
//            return;
//        }
//
//        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
//        //      点击权限隐私>自启动管理>我的app
//        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
//        if(appIntent != null){
//            context.startActivity(appIntent);
//            floatingView = new SettingFloatingView(this, "SETTING", getApplication(), 1);
//            floatingView.createFloatingView();
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if(Build.VERSION.SDK_INT >= 9){
//            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            intent.setData(Uri.fromParts("package", getPackageName(), null));
//        } else if(Build.VERSION.SDK_INT <= 8){
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
//            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//        }
//        startActivity(intent);
//    }
    
    public void initStartPagePic(){
		try {
			SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("caimeifenqi_startpagepic", null);
			editor.commit();
		} catch (Exception e) {
			Log.e("WelcomeActivity", "setStartPagePic", e);
		}
    }
    
	public void setStartPagePic(String pic) {
		try {
			String allPic = getStartPagePic();
			SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
			Editor editor = sp.edit();
			if(allPic != null){
				allPic = allPic + ";" + pic;
			} else {
				allPic = pic;
			}
			
			editor.putString("caimeifenqi_startpagepic", allPic);
			editor.commit();
		} catch (Exception e) {
			Log.e("WelcomeActivity", "setStartPagePic", e);
		}
	}
	
	public String getStartPagePic() {
		String deviceNum = null;
		try {
			SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
			deviceNum = sp.getString("caimeifenqi_startpagepic", null);
			return deviceNum;
		} catch (Exception e) {
			Log.e("WelcomeActivity", "getStartPagePic", e);
		}
		return deviceNum;
	}
	
//	public void setOpenFirstTime(boolean status) {
//		try {
//			SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
//			Editor editor = sp.edit();
//			
//			editor.putBoolean("caimeifenqi_isopenfirsttime", status);
//			editor.commit();
//		} catch (Exception e) {
//			Log.e("WelcomeActivity", "setOpenFirstTime", e);
//		}
//	}
//	
//	public boolean getOpenFirstTime() {
//		boolean isFirst = true;
//		try {
//			SharedPreferences sp = WelcomeActivity.this.getSharedPreferences("SP", MODE_PRIVATE);
//			isFirst = sp.getBoolean("caimeifenqi_isopenfirsttime", true);
//		} catch (Exception e) {
//			Log.e("WelcomeActivity", "getOpenFirstTime", e);
//		}
//		return isFirst;
//	}
	
	private String getNameFromUrl(String loadUrl) {
		String[] a = loadUrl.split("/");
		String s = a[a.length - 1];
		return s;
	}

	/**
	 * 初始化服务器地址
	 */
	private void initNetwork() {
		URLFactory.PRE_URL = URLFactory.DIANXIN_URL;
	}
	
	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(WelcomeActivity.this, info, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
