package com.il360.shenghecar.activity.mydata;


/**
 * 上传日志错误步骤 1账户信息；2认证信息；3白条金条额度信息；4小金库信息；5白条已出订单；6白条未出订单；7白条还款流水；8白条退款记录；9白条消费明细；
 * 10金条订单；11小金库-收益；12小金库-转入；13小金库-转出；14小金库-冻结/解冻;15提交数据
 */

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.user.LoginActivity_;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiao1;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiao2;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiao3;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiao4;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiao5;
import com.il360.shenghecar.model.jd.ArrayOfBaiTiaoBillDet;
import com.il360.shenghecar.model.jd.ArrayOfJinTiao1;
import com.il360.shenghecar.model.jd.ArrayOfMyBaiTiaoBill;
import com.il360.shenghecar.model.jd.ArrayOfMyJinTiaoBill;
import com.il360.shenghecar.model.jd.AssetsAggregated;
import com.il360.shenghecar.model.jd.BasicInfo;
import com.il360.shenghecar.model.jd.JKAccount;
import com.il360.shenghecar.model.jd.JingDongInfo;
import com.il360.shenghecar.model.jd.MyBaiTiaoBill;
import com.il360.shenghecar.model.jd.MyBaiTiaoBillDet;
import com.il360.shenghecar.model.jd.MyBill;
import com.il360.shenghecar.model.jd.MyJinTiaoBill;
import com.il360.shenghecar.model.jd.MyJinTiaoBillDet;
import com.il360.shenghecar.util.FastJsonUtils;
import com.il360.shenghecar.util.ResultUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.util.alipay.HttpUtils;
import com.il360.shenghecar.view.CustomDialog;
import com.il360.shenghecar.view.TextProgressBar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
@SuppressLint("SimpleDateFormat")
@EActivity(R.layout.act_jingdong_info)
public class JingDongActivity extends BaseWidgetActivity{
	@ViewById
	WebView wv_show;
	@ViewById
	LinearLayout llAuthen;
	@ViewById
	TextProgressBar pbAuthen;
	@ViewById
	ImageView header_image_return;
	
	private Handler mHandler = new Handler();
	
	protected ProgressDialog transDialog;
	
	private String mBeginDate = "";
	private String mEndDate = "";
	private int pageNum1 = 0;
	private int pageNum2 = 0;
	private int pageNum3 = 0;
	private int pageNum4 = 0;
	private int baiTiaoPageNum3 = 0;
	private int baiTiaoPageNum5 = 0;
	private boolean isfirst = true;
	
	private static final String loginUrl = "https://passport.jd.com/new/login.aspx?ReturnUrl=http%3A%2F%2Fjr.jd.com%2F";
	private static final String afterLoginUrl = "http://jr.jd.com/";
	private static final String afterLoginUrl2 = "https://m.jr.jd.com/spe/qyy/main/index.html?userType=";
	private static final String authenInfoUrl = "https://authpay.jd.com/auth/toAuthSuccessPage.action";
	private static final String accountInfoUrl = "https://i.jd.com/user/info";
	private static final String borrowBalanceUrl = "http://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt";
	private static final String balanceUrl = "http://xjk.jr.jd.com/gold/account";
	private static final String baiTiaoJiLu1 = "https://baitiao.jd.com/v3/ious/getBillList";
	private static final String baiTiaoJiLu2 = "https://baitiao.jd.com/v3/ious/queryNotOutAccount";
	private static final String baiTiaoJiLu3 = "https://baitiao.jd.com/v3/ious/billRepayment";
	private static final String baiTiaoJiLu4 = "https://baitiao.jd.com/v3/ious/queryRefundList";
	private static final String baiTiaoJiLu5 = "https://baitiao.jd.com/v3/ious/billConsumeList";
	private static final String baiTiaoDetUrl = "https://baitiao.jd.com/v3/ious/getBillOrderDetail";
	private static final String jinTiaoJiLu = "https://baitiao.jd.com/v3/ious/getJtDetailList";
	
	
	private static final Pattern authenInfop = Pattern.compile("<div class=\"name\">([^<>]*?)（([^<>]*?)）</div>");
	private static final Pattern bindPhonep = Pattern.compile("<div[^>]*?>绑定手机：</div><div[^>]*?><span>([^<>]*?)</span>");
	private static final Pattern accountInfop = Pattern.compile("<span[^>]*?>用户名：</span><div[^>]*?><div><strong>([^<>]*?)</strong>.*?登录名：</span><div[^>]*?><strong>([^<>]*?)</strong>.*?昵称：</span><div[^>]*?><input[^>]*?><input[^>]*?><input[^>]*?value=\"([^<>]*?)\">.*?<a href=\"//vip.jd.com\"[^>]*?title=\"([^<>]*?)\">[^>]*?</a></span></div><div[^>]*?>小白信用：<a href=\"//credit.jd.com\"[^>]*?>([^<>]*?)</a>");
	private static final Pattern borrowBalancep = Pattern.compile("<input id=\"assetsAggregated\" value=\'([^<>]*?)\'>");
	private static final Pattern pageNump = Pattern.compile("<span class=\"mr10\">[^>]*共([^<>]*?)页</span>");
	
	private static final Pattern xiaoJinKup = Pattern.compile("<td>([^<>]*?)</td><td>([^<>]*?)</td><td[^>]*?>([^<>]*?)</td><td>([^<>]*?)</td><td>([^<>]*?)</td>");
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
	private final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
	
	
	private BasicInfo basicInfo = new BasicInfo();
	private List<ArrayOfMyBaiTiaoBill> baiTiaoList = new ArrayList<ArrayOfMyBaiTiaoBill>();
	private List<ArrayOfMyJinTiaoBill> jinTiaoList = new ArrayList<ArrayOfMyJinTiaoBill>();
	private List<MyBill> billList = new ArrayList<MyBill>();
	
	/**
	 * 手机信息 型号+sdk版本+版本号
	 */
	private String phoneInfo = "";
	
	
	@AfterViews
	void init() {
		initViews();
		initWebView();
	}
	
	private void initViews() {
		phoneInfo = android.os.Build.MODEL + "," + android.os.Build.VERSION.SDK + ","
				+ android.os.Build.VERSION.RELEASE;

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -2);// 得到前2个月
			Date formNow2Month = calendar.getTime();
			mBeginDate = sdf2.format(formNow2Month) + "-01";
			String mEndDate2 = sdf2.format(new Date()) + "-01";
			
			Date date = sdf3.parse(mEndDate2);
			calendar.setTime(date);
			calendar.add(Calendar.DATE, -1);
			Date newEndDate = calendar.getTime();
			mEndDate = sdf3.format(newEndDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		llAuthen.setVisibility(View.GONE);
		
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void initWebView() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {

				wv_show.requestFocus(); // 请求焦点
				wv_show.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
				wv_show.getSettings().setJavaScriptEnabled(true); // 设置是否支持JavaScript
				wv_show.getSettings().setSupportZoom(true); // 设置是否支持缩放
				wv_show.getSettings().setBuiltInZoomControls(true); // 设置是否显示内建缩放工具
				wv_show.setHorizontalScrollBarEnabled(true);// 滚动条水平是否显示
				wv_show.setVerticalScrollBarEnabled(true); // 滚动条垂直是否显示
				wv_show.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放
				wv_show.getSettings().setDomStorageEnabled(true); // 设置支持DomStorage
				wv_show.getSettings().setLoadWithOverviewMode(true);
				wv_show.loadUrl(loginUrl); // 加载在线网页
				// 触摸焦点起作用
				wv_show.requestFocus();// 如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事
				// Allow third party cookies for Android Lollipop
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					CookieManager cookieManager = CookieManager.getInstance();
					cookieManager.setAcceptThirdPartyCookies(wv_show, true);
				}
				wv_show.setWebViewClient(new MyWebViewClient());
			}
		});
	}

	final class MyWebViewClient extends WebViewClient {
		// 在WebView中而不是系统默认浏览器中显示页面

		/**
		 * 拦截 url 跳转,在里边添加点击链接跳转或者操作
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(afterLoginUrl) || url.startsWith(afterLoginUrl2)) {
				wv_show.setVisibility(View.GONE);
				llAuthen.setVisibility(View.VISIBLE);
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		// 页面载入前调用
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			tvTextClick.setVisibility(View.GONE);
			super.onPageStarted(view, url, favicon);
		}

		// 页面载入完成后调用
		@Override
		public void onPageFinished(WebView view, String url) {

			// 注入js
			CookieManager cookieManager = CookieManager.getInstance();
			final String cokieStr = cookieManager.getCookie(url);

			if (url.equals(loginUrl)) {
				
				String js = "function removeClassName(name){var eles = document.getElementsByClassName(name);var i;for (i = 0; i < eles.length; i++) {eles[i].remove();}};function setupLoginHomeUI(){"
						+ " document.getElementsByClassName('login-tab login-tab-r')[0].click();document.getElementsByClassName('w')[0].style.width='100%';document.getElementsByClassName('login-form')[0].style.margin=\"auto\";document.getElementsByClassName('login-wrap')[0].getElementsByClassName('login-form')[0].style.cssFloat='none';document.getElementsByClassName('login-banner')[0].style.backgroundColor='#e93854';document.getElementsByClassName('tips-wrapper')[1].style.height='40px';"
						+ "removeClassName('w');removeClassName('coagent');removeClassName('tips-wrapper');removeClassName('links');removeClassName('coagent');removeClassName('forget-pw-safe');};setupLoginHomeUI();";
				
				view.loadUrl("javascript:" + js);
				
			} else if (cokieStr != null && (url.startsWith(afterLoginUrl) || url.startsWith(afterLoginUrl2))) {
				ExecuteTask.execute(new Runnable() {
					@Override
					public void run() {
						if(isfirst){
							isfirst = false;
							captureData(cokieStr);
						}
					}
				});
			}

			super.onPageFinished(view, url);
		}

		@SuppressWarnings("deprecation")
		@Override
		/**
		 * 在每一次请求资源时，都会通过这个函数来回调
		 */
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			Log.d(TAG, "shouldInterceptRequest " + url);
			return super.shouldInterceptRequest(view, url);
		}
	}

	private void captureData(String cookie) {
		if(!JingDongActivity.this.isFinishing()){
			boolean authenInfo = getAuthenInfo(authenInfoUrl, cookie);// 认证信息
			if(authenInfo){
				if(!JingDongActivity.this.isFinishing()){
					boolean borrowBalance = getBorrowBalance(borrowBalanceUrl, cookie);// 白条金条额度信息
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean balance = getBalance(balanceUrl, cookie);// 小金库信息
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean baiTiao1 = getBaiTiao1(baiTiaoJiLu1, cookie);//白条订单
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean baiTiao2 = getBaiTiao2(baiTiaoJiLu2, cookie);
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean baiTiao3 = getBaiTiao3(baiTiaoJiLu3, cookie , 1);
					if (baiTiao3 && baiTiaoPageNum3 > 1) {
						getBaiTiao3(baiTiaoJiLu3, cookie , 2);
					}
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean baiTiao4 = getBaiTiao4(baiTiaoJiLu4, cookie);
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean baiTiao5 = getBaiTiao5(baiTiaoJiLu5, cookie , 1);
					if (baiTiao5 && baiTiaoPageNum5 > 1) {
						getBaiTiao5(baiTiaoJiLu5, cookie , 2);
					}
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean jinTiao1 = getJinTiao1(jinTiaoJiLu, cookie);//金条订单
				}
				
				if(!JingDongActivity.this.isFinishing()){
					boolean xiaojinku1 = getXiaoJinKu1(1, cookie);//小金库
					if (xiaojinku1) {
						for (int i = 1; i < pageNum1; i++) {
							getXiaoJinKu1(i+1, cookie);
						}
					}
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean xiaojinku2 = getXiaoJinKu2(1, cookie);
					if (xiaojinku2) {
						for (int i = 1; i < pageNum2; i++) {
							getXiaoJinKu2(i+1, cookie);
						}
					}
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean xiaojinku3 = getXiaoJinKu3(1, cookie);
					if (xiaojinku3) {
						for (int i = 1; i < pageNum3; i++) {
							getXiaoJinKu3(i+1, cookie);
						}
					}
				}
				if(!JingDongActivity.this.isFinishing()){
					boolean xiaojinku4 = getXiaoJinKu4(1, cookie);
					if (xiaojinku4) {
						for (int i = 1; i < pageNum4; i++) {
							getXiaoJinKu4(i+1, cookie);
						}
					}
				}
			}
		}
		if(!JingDongActivity.this.isFinishing()){
			boolean accountInfo = getAccountInfo(accountInfoUrl, cookie);// 账户信息
		}
		
		if(!JingDongActivity.this.isFinishing()){
			initPostJdData();
		}
		
	}
	
	private String assembleData() {
		JingDongInfo jingDongInfo = new JingDongInfo();
	    
		jingDongInfo.setBasicInfo(basicInfo);
		jingDongInfo.setBillList(billList);
		jingDongInfo.setBaiTiaoList(baiTiaoList);
		jingDongInfo.setJinTiaoList(jinTiaoList);
		
		showProgress(95);
		
		return FastJsonUtils.getJsonString(jingDongInfo);
	}

	private void initPostJdData() {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("jdjson", assembleData());
			params.put("token", UserUtil.getToken());
			TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL, "card/postJdData",params);
			if (result.getSuccess()) {
				if (ResultUtil.isOutTime(result.getResult()) != null) {
					showInfo(ResultUtil.isOutTime(result.getResult()));
					Intent intent = new Intent(JingDongActivity.this, LoginActivity_.class);
					startActivity(intent);
				} else {
					JSONObject obj = new JSONObject(result.getResult());
					if (obj.getInt("code") == 1) {
						showProgress(100);
					} else {
						upLogInfo("15", "card/postJdData接口返回obj.getInt(\"code\")为" + obj.getInt("code"), result.getResult());
						showErrorAndOut(obj.getString("desc"));
					}
				}
			} else {
				upLogInfo("15", "card/postJdData接口返回result.getSuccess()为false", result.getResult());
				showErrorAndOut("认证失败，请重试！");
			}
		} catch (Exception e) {
			upLogInfo("15", "方法postJdData报异常", e.getMessage());
			showErrorAndOut("认证失败，请重试！");
		} finally {
			if(!JingDongActivity.this.isFinishing()){
				JingDongActivity.this.finish();
			}
		}
		
	}

	private boolean getAuthenInfo(String url, String cookie) {
		showProgress(5);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "authpay.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://trade.jr.jd.com/centre/browse.action");
			heades.put("Upgrade-Insecure-Requests", "1");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				String html = HttpUtils.replRtnSpace(result);
				List<String[]> authenInfoList = HttpUtils.getListArray(html, authenInfop);
				String bindPhone = HttpUtils.getValue(html, bindPhonep);
				basicInfo.setBindPhone(bindPhone.trim());
				if (authenInfoList != null && authenInfoList.size() > 0) {
					basicInfo.setRealName(authenInfoList.get(0)[0].trim());
					basicInfo.setIdCard(authenInfoList.get(0)[1].trim());
					return true;
				} else {
					upLogInfo("2", "认证信息获取失败", result);
					return false;
				}
			} else {
				upLogInfo("2", "认证信息获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	private boolean getAccountInfo(String url, String cookie){
		addProgress(5);//+5%
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "i.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://i.jd.com/user/userinfo/more.html");
			heades.put("Upgrade-Insecure-Requests", "1");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				String html = HttpUtils.replRtnSpace(result);
				List<String[]> accountInfoList = HttpUtils.getListArray(html, accountInfop);
				if (accountInfoList != null && accountInfoList.size() > 0) {
					basicInfo.setUserName(accountInfoList.get(0)[0].trim());
					basicInfo.setLoginName(accountInfoList.get(0)[1].trim());
					basicInfo.setNickName(accountInfoList.get(0)[2].trim());
					basicInfo.setJdValue(accountInfoList.get(0)[3].trim());
					basicInfo.setJdCredit(accountInfoList.get(0)[4].trim());

					return true;
				} else {
					upLogInfo("1", "个人账号信息获取失败", result);
					return false;
				}
			} else {
				upLogInfo("1", "个人账号信息获取失败", result);
				return false;
			}
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBorrowBalance(String url, String cookie) {
		showProgress(10);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("Upgrade-Insecure-Requests", "1");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条金条额度", result);
				String html = HttpUtils.replRtnSpace(result);
				String resultJsonStr = HttpUtils.getValue(html, borrowBalancep);
				AssetsAggregated assetsAggregated = FastJsonUtils.getSingleBean(resultJsonStr, AssetsAggregated.class);
				if (assetsAggregated != null && assetsAggregated.getResult() != null
						&& assetsAggregated.getResult().getIsSuccess().equals("true")) {

					basicInfo.setBtAmt(yuanToFen(assetsAggregated.getBill().getTotalAsset()));
					basicInfo.setBtAvailableAmt(yuanToFen(assetsAggregated.getBill().getAvailableLimit()));
					basicInfo.setJtAmt(yuanToFen(assetsAggregated.getBullion().getTotalAsset()));
					basicInfo.setJtAvailableAmt(yuanToFen(assetsAggregated.getBullion().getAvailableLimit()));

					return true;
				} else {
					upLogInfo("3", "白条/金条额度获取失败", result);
					return false;
				}
			} else {
				upLogInfo("3", "白条/金条额度获取失败", result);
				return false;
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBalance(String url, String cookie) {
		showProgress(15);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "*/*");
			heades.put("Accept-Encoding", "	gzip, deflate");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "xjk.jr.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				JKAccount jkAccount = FastJsonUtils.getSingleBean(result, JKAccount.class);
				if (jkAccount != null) {
					basicInfo.setCoffersTotalAmt(yuanToFen(jkAccount.getAccountResult().getTotal()));
					basicInfo.setCoffersBal(yuanToFen(jkAccount.getAccountResult().getAvailable()));
					basicInfo.setFrozenAmt(yuanToFen(jkAccount.getAccountResult().getFrozen()));
					basicInfo.setEarningsAmt(yuanToFen(jkAccount.getAccountResult().getPreIncome()));
					basicInfo.setTotalEarnings(yuanToFen(jkAccount.getAccountResult().getAllIncome()));
					basicInfo.setFinancialAmt(yuanToFen(jkAccount.getAccountResult().getFinanceTotal()));
					return true;
				} else {
					upLogInfo("4", "小金库额度获取失败", result);
					return false;
				}
			} else {
				upLogInfo("4", "小金库额度获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBaiTiao1(String url, String cookie){
		showProgress(20);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");

			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", "1");
			params.put("pageSize", "10");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条已出账单：", result);
				ArrayOfBaiTiao1 arrayOfBaiTiao1 = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiao1.class);
				if (arrayOfBaiTiao1 != null && arrayOfBaiTiao1.getBillList() != null & arrayOfBaiTiao1.getBillList().size() > 0) {
					for (int i = 0; i < arrayOfBaiTiao1.getBillList().size(); i++) {
						ArrayOfMyBaiTiaoBill arrayOfMyBaiTiaoBill = new ArrayOfMyBaiTiaoBill();
						List<MyBaiTiaoBillDet> detailList= new ArrayList<MyBaiTiaoBillDet>();
						MyBaiTiaoBill monthBill = new MyBaiTiaoBill();
						
						monthBill.setBillAmt(yuanToFen(arrayOfBaiTiao1.getBillList().get(i).getBillAmt()));
						Date billDate = sdf1.parse(arrayOfBaiTiao1.getBillList().get(i).getBillDate());
						String newBillDate = sdf.format(billDate);
						monthBill.setBillDay(newBillDate);
						Date billLimiDate = sdf1.parse(arrayOfBaiTiao1.getBillList().get(i).getBillLimitDate());
						String newbillLimiDate = sdf.format(billLimiDate);
						monthBill.setBillLimitTime(newbillLimiDate);
						monthBill.setBillRemainAmt(yuanToFen(arrayOfBaiTiao1.getBillList().get(i).getSdpAmt()));
						monthBill.setJdBillId(arrayOfBaiTiao1.getBillList().get(i).getBillId());

						if (arrayOfBaiTiao1.getBillList().get(i).getStatus().equals("2")) { // 已结清
							monthBill.setStatus("1");
						} else {
							monthBill.setStatus("2");
						}
						Map<String, String> paramsDet = new HashMap<String, String>();
						if (i == 0) {
							paramsDet.put("billType", "1");
						} else {
							paramsDet.put("billType", "2");
						}
						
						paramsDet.put("billId", arrayOfBaiTiao1.getBillList().get(i).getBillId());
						ArrayOfBaiTiaoBillDet arrayOfBaiTiaoBillDet = getBaiTiaoDet(baiTiaoDetUrl, cookie, paramsDet);
						
						if (arrayOfBaiTiaoBillDet != null && arrayOfBaiTiaoBillDet.getLastbill() != null) {
							MyBaiTiaoBillDet myBaiTiaoBillDet = new MyBaiTiaoBillDet();
							myBaiTiaoBillDet.setAmount(yuanToFen(arrayOfBaiTiaoBillDet.getLastbill().getNextOverAmt()));
							myBaiTiaoBillDet.setBillTitle(arrayOfBaiTiaoBillDet.getLastbill().getProductName());
							Date billDetDate = sdf1.parse(arrayOfBaiTiaoBillDet.getLastbill().getBillDate());
							String newBillDetDate = sdf.format(billDetDate);
							myBaiTiaoBillDet.setBillTime(newBillDetDate);
							detailList.add(myBaiTiaoBillDet);
						}
						
						if (arrayOfBaiTiaoBillDet != null && arrayOfBaiTiaoBillDet.getBillList() != null && arrayOfBaiTiaoBillDet.getBillList().size() > 0) {
							for (int j = 0; j < arrayOfBaiTiaoBillDet.getBillList().size(); j++) {
								MyBaiTiaoBillDet myBaiTiaoBillDet = new MyBaiTiaoBillDet();
								myBaiTiaoBillDet.setAmount(yuanToFen(arrayOfBaiTiaoBillDet.getBillList().get(j).getAmount()));
								myBaiTiaoBillDet.setBillTitle(arrayOfBaiTiaoBillDet.getBillList().get(j).getProductName());
								myBaiTiaoBillDet.setOrderId(arrayOfBaiTiaoBillDet.getBillList().get(j).getOrderId());
								Date billDetDate = sdf1.parse(arrayOfBaiTiaoBillDet.getBillList().get(j).getConsumerDate());
								String newBillDetDate = sdf.format(billDetDate);
								myBaiTiaoBillDet.setBillTime(newBillDetDate);
								detailList.add(myBaiTiaoBillDet);
							}
						}
						
						arrayOfMyBaiTiaoBill.setMonthBill(monthBill);
						arrayOfMyBaiTiaoBill.setDetailList(detailList);
						
						baiTiaoList.add(arrayOfMyBaiTiaoBill);
					}
				}
				return true;
			} else {
				upLogInfo("5", "白条已出账单获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
//			upLogInfo("5", "方法getBankList报异常", e.getMessage());
			return false;
		}
	}
	
	private ArrayOfBaiTiaoBillDet getBaiTiaoDet(String url, String cookie, Map<String,String> params) {
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条账单详情：", result);
				ArrayOfBaiTiaoBillDet arrayOfBaiTiaoBillDet = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiaoBillDet.class);
				if(arrayOfBaiTiaoBillDet != null) {
					return arrayOfBaiTiaoBillDet;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	private boolean getBaiTiao2(String url, String cookie){
		showProgress(25);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String result = HttpUtils.post(url, "", heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条未出账单：", result);
				ArrayOfBaiTiao2 arrayOfBaiTiao2 = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiao2.class);
				if (arrayOfBaiTiao2 != null && arrayOfBaiTiao2.getBillNOTOut() != null) {
					ArrayOfMyBaiTiaoBill arrayOfMyBaiTiaoBill = new ArrayOfMyBaiTiaoBill();
					List<MyBaiTiaoBillDet> detailList= new ArrayList<MyBaiTiaoBillDet>();
					MyBaiTiaoBill monthBill = new MyBaiTiaoBill();
					
					monthBill.setBillAmt(yuanToFen(arrayOfBaiTiao2.getBillNOTOut().getBillAmt()));
	
					Date billDate = sdf1.parse(arrayOfBaiTiao2.getBillNOTOut().getBillDate());
					String newBillDate = sdf.format(billDate);
					monthBill.setBillDay(newBillDate);
	
					monthBill.setBillRemainAmt(yuanToFen(arrayOfBaiTiao2.getBillNOTOut().getSdpAmt()));
					monthBill.setJdBillId(arrayOfBaiTiao2.getBillNOTOut().getBillId());
					monthBill.setStatus("2");
					Map<String, String> paramsDet = new HashMap<String, String>();
					paramsDet.put("billId", arrayOfBaiTiao2.getBillNOTOut().getBillId());
					paramsDet.put("billType", "0");
					paramsDet.put("isNotAcount", "1");
					ArrayOfBaiTiaoBillDet arrayOfBaiTiaoBillDet = getBaiTiaoDet(baiTiaoDetUrl, cookie, paramsDet);
					
					if (arrayOfBaiTiaoBillDet != null && arrayOfBaiTiaoBillDet.getLastbill() != null) {
						MyBaiTiaoBillDet myBaiTiaoBillDet = new MyBaiTiaoBillDet();
						myBaiTiaoBillDet.setAmount(yuanToFen(arrayOfBaiTiaoBillDet.getLastbill().getNextOverAmt()));
						myBaiTiaoBillDet.setBillTitle(arrayOfBaiTiaoBillDet.getLastbill().getProductName());
						Date billDetDate = sdf1.parse(arrayOfBaiTiaoBillDet.getLastbill().getBillDate());
						String newBillDetDate = sdf.format(billDetDate);
						myBaiTiaoBillDet.setBillTime(newBillDetDate);
						detailList.add(myBaiTiaoBillDet);
					}
					
					if (arrayOfBaiTiaoBillDet != null && arrayOfBaiTiaoBillDet.getBillList() != null && arrayOfBaiTiaoBillDet.getBillList().size() > 0) {
						for (int j = 0; j < arrayOfBaiTiaoBillDet.getBillList().size(); j++) {
							MyBaiTiaoBillDet myBaiTiaoBillDet = new MyBaiTiaoBillDet();
							myBaiTiaoBillDet.setAmount(yuanToFen(arrayOfBaiTiaoBillDet.getBillList().get(j).getAmount()));
							myBaiTiaoBillDet.setBillTitle(arrayOfBaiTiaoBillDet.getBillList().get(j).getProductName());
							myBaiTiaoBillDet.setOrderId(arrayOfBaiTiaoBillDet.getBillList().get(j).getOrderId());
							Date billDetDate = sdf1.parse(arrayOfBaiTiaoBillDet.getBillList().get(j).getConsumerDate());
							String newBillDetDate = sdf.format(billDetDate);
							myBaiTiaoBillDet.setBillTime(newBillDetDate);
							detailList.add(myBaiTiaoBillDet);
						}
					}
					
					arrayOfMyBaiTiaoBill.setMonthBill(monthBill);
					arrayOfMyBaiTiaoBill.setDetailList(detailList);
					
					baiTiaoList.add(arrayOfMyBaiTiaoBill);
				}
				return true;
			} else {
				upLogInfo("6", "白条未出账单获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBaiTiao3(String url, String cookie , int page){
		showProgress(30);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");

			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", page + "");
			params.put("pageSize", "10");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条还款流水：", result);
				ArrayOfBaiTiao3 arrayOfBaiTiao3 = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiao3.class);
				if (arrayOfBaiTiao3 != null && arrayOfBaiTiao3.getBillPayList() != null
						&& arrayOfBaiTiao3.getBillPayList().size() > 0) {
					
					baiTiaoPageNum3 = arrayOfBaiTiao3.getTotalPage();
					
					for (int i = 0; i < arrayOfBaiTiao3.getBillPayList().size(); i++) {
						MyBill myBill = new MyBill();
						myBill.setBillType("1");
						myBill.setBillAmount(yuanToFen(arrayOfBaiTiao3.getBillPayList().get(i).getAmount()));

						Date billDate = sdf1.parse(arrayOfBaiTiao3.getBillPayList().get(i).getPayTime());
						String newBillDate = sdf.format(billDate);
						myBill.setBillTime(newBillDate);

						myBill.setBillTitle("白条还款");
						myBill.setBillSn(arrayOfBaiTiao3.getBillPayList().get(i).getRepaymentNo());
						int status = arrayOfBaiTiao3.getBillPayList().get(i).getStatus();
						if (status == 0) {
							myBill.setStatusName("待还款");
						} else if (status == 1) {
							myBill.setStatusName("已还款");
						} else if (status == 2) {
							myBill.setStatusName("部分退款");
						} else if (status == 3) {
							myBill.setStatusName("已退款");
						} else if (status == 4) {
							myBill.setStatusName("已违约");
						} else if (status == 5) {
							myBill.setStatusName("部分还款");
						} else if (status == 6) {
							myBill.setStatusName("处理中");
						} else {
							myBill.setStatusName("未知");
						}

						billList.add(myBill);
					}
				}

				return true;
			} else {
				upLogInfo("7", "白条还款流水获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBaiTiao4(String url, String cookie){
		showProgress(35);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");

			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", "1");
			params.put("pageSize", "10");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条退款记录：", result);
				ArrayOfBaiTiao4 arrayOfBaiTiao4 = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiao4.class);
				if (arrayOfBaiTiao4 != null && arrayOfBaiTiao4.getBtRefundVoList() != null
						&& arrayOfBaiTiao4.getBtRefundVoList().size() > 0) {
					for (int i = 0; i < arrayOfBaiTiao4.getBtRefundVoList().size(); i++) {
						MyBill myBill = new MyBill();
						myBill.setBillType("2");
						myBill.setBillAmount(yuanToFen(arrayOfBaiTiao4.getBtRefundVoList().get(i).getAmount()));
						Date billDate = sdf1.parse(arrayOfBaiTiao4.getBtRefundVoList().get(i).getCreateDate());
						String newBillDate = sdf.format(billDate);
						myBill.setBillTime(newBillDate);
						myBill.setBillTitle(arrayOfBaiTiao4.getBtRefundVoList().get(i).getProductName());
						myBill.setBillSn(arrayOfBaiTiao4.getBtRefundVoList().get(i).getRefundId());
						myBill.setStatusName("已退款");
						billList.add(myBill);
					}
				}
				return true;
			} else {
				upLogInfo("8", "白条退款记录获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getBaiTiao5(String url, String cookie , int page){
		showProgress(40);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");

			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", page + "");
			params.put("pageSize", "10");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("白条消费明细：", result);
				ArrayOfBaiTiao5 arrayOfBaiTiao5 = FastJsonUtils.getSingleBean(result, ArrayOfBaiTiao5.class);
				if (arrayOfBaiTiao5 != null && arrayOfBaiTiao5.getDetailsInfo() != null & arrayOfBaiTiao5.getDetailsInfo().size() > 0) {
					
					baiTiaoPageNum5 = arrayOfBaiTiao5.getPageCount();
					
					for (int i = 0; i < arrayOfBaiTiao5.getDetailsInfo().size(); i++) {
						MyBill myBill = new MyBill();
						myBill.setBillType("3");
						myBill.setBillAmount(yuanToFen(arrayOfBaiTiao5.getDetailsInfo().get(i).getOrderAmount()));
						myBill.setBillSn(arrayOfBaiTiao5.getDetailsInfo().get(i).getOrderId());
						myBill.setBillTitle(arrayOfBaiTiao5.getDetailsInfo().get(i).getProductName());

						Date billDate = sdf1.parse(arrayOfBaiTiao5.getDetailsInfo().get(i).getConsumerDate());
						String newBillDate = sdf.format(billDate);
						myBill.setBillTime(newBillDate);
						myBill.setStatusName("交易成功");

						billList.add(myBill);
					}
				}

				return true;
			} else {
				upLogInfo("9", "白条消费明细获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getJinTiao1(String url, String cookie) {
		showProgress(45);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			heades.put("Cookie", cookie);
			heades.put("Host", "baitiao.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");

			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNum", "1");
			params.put("pageSize", "10");
			params.put("funCode", "ALL");
			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
			if (!TextUtils.isEmpty(result)) {
				Log.i("金条全部：", result);
				ArrayOfJinTiao1 arrayOfJinTiao1 = FastJsonUtils.getSingleBean(result, ArrayOfJinTiao1.class);
				if (arrayOfJinTiao1 != null && arrayOfJinTiao1.getDetailsInfo() != null
						&& arrayOfJinTiao1.getDetailsInfo().size() > 0) {
					for (int i = 0; i < arrayOfJinTiao1.getDetailsInfo().size(); i++) {
						ArrayOfMyJinTiaoBill arrayOfMyJinTiaoBill = new ArrayOfMyJinTiaoBill();
						List<MyJinTiaoBillDet> detailList = new ArrayList<MyJinTiaoBillDet>();
						MyJinTiaoBill jintiaoBill = new MyJinTiaoBill();
						
						jintiaoBill.setAmount(yuanToFen(arrayOfJinTiao1.getDetailsInfo().get(i).getTotalPaymentAmount()));
						jintiaoBill.setBillSn(arrayOfJinTiao1.getDetailsInfo().get(i).getJdOrderNo());
						jintiaoBill.setPeriods(arrayOfJinTiao1.getDetailsInfo().get(i).getPayPlanNum());

						Date billDate = sdf3.parse(arrayOfJinTiao1.getDetailsInfo().get(i).getConsumerDate());
						String newBillDate = sdf.format(billDate);
						jintiaoBill.setBillTime(newBillDate);
						
						if(arrayOfJinTiao1.getDetailsInfo().get(i).getFinishPayDate() != null){
							Date finishDate = sdf3.parse(arrayOfJinTiao1.getDetailsInfo().get(i).getFinishPayDate());
							String newFinishDate = sdf.format(finishDate);
							jintiaoBill.setPayOffTime(newFinishDate);
						}

						int billRemainAmtInt = 0;
						int periods = Integer.valueOf(arrayOfJinTiao1.getDetailsInfo().get(i).getPayPlanNum());
						for (int j = 0; j < periods; j++) {
							if (!arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getStatus().equals("1")) {
								billRemainAmtInt = billRemainAmtInt + Integer.valueOf(yuanToFen(
										arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getAmount()));
							}
							
							MyJinTiaoBillDet myJinTiaoBillDet = new MyJinTiaoBillDet();
							
							myJinTiaoBillDet.setInterestAmt(yuanToFen(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getDayAmount()));
							myJinTiaoBillDet.setPrincipalAmt(yuanToFen(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getAmount()));
							int totelAmount = Integer.valueOf(yuanToFen(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getDayAmount())) 
									+ Integer.valueOf(yuanToFen(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getAmount()));
							myJinTiaoBillDet.setAmount(totelAmount + "");
							Date limitDate = sdf1.parse(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getLimitPayDate());
							String newLimitDate = sdf.format(limitDate);
							myJinTiaoBillDet.setBillTime(newLimitDate);
							myJinTiaoBillDet.setPeriods(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getCurPlanNum());
							myJinTiaoBillDet.setStatus(arrayOfJinTiao1.getDetailsInfo().get(i).getPlans().get(j).getStatus());
							
							detailList.add(myJinTiaoBillDet);
						}

						if (billRemainAmtInt > 0) {
							jintiaoBill.setStatus("2");
						} else {
							jintiaoBill.setStatus("1");
						}
						
						arrayOfMyJinTiaoBill.setDetailList(detailList);
						arrayOfMyJinTiaoBill.setJintiaoBill(jintiaoBill);
						jinTiaoList.add(arrayOfMyJinTiaoBill);
					}
				}
				return true;
			} else {
				upLogInfo("10", "金条全部订单获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

//	private boolean getJinTiao2(String url, String cookie) {
//		try {
//			Map<String, String> heades = new HashMap<String, String>();
//			heades.put("Accept", "application/json, text/javascript, */*; q=0.01");
//			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
//			heades.put("Cache-Control", "no-cache");
//			heades.put("Connection", "keep-alive");
//			heades.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			heades.put("Cookie", cookie);
//			heades.put("Host", "baitiao.jd.com");
//			heades.put("Pragma", "no-cache");
//			heades.put("Referer", "https://baitiao.jd.com/v3/ious/list?from=myzc-left-jrbt");
//			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
//			heades.put("X-Requested-With", "XMLHttpRequest");
//
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("pageNum", "1");
//			params.put("pageSize", "10");
//			params.put("funCode", "UPAY");
//			String result = HttpUtils.post(url, params, heades, Charset.defaultCharset());
//			Log.i("金条待还款：", result);
//			// ArrayOfBankInfo arrayOfBankInfo =
//			// FastJsonUtils.getSingleBean(result, ArrayOfBankInfo.class);
//			return true;
//		} catch (Exception e) {
//			Log.e(TAG, e.getMessage());
////			upLogInfo("5", "方法getBankList报异常", e.getMessage());
//			return false;
//		}
//	}
	
	private boolean getXiaoJinKu1(int page, String cookie) {
		showProgress(55);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "*/*");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "xjk.jr.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String url = "https://xjk.jr.jd.com/gold/trades?tradeType=INCOME&startDate="+ mBeginDate +"&endDate=" + mEndDate + "&pageSize=10&pageNo=" + page;
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			String html = HttpUtils.replRtnSpace(result);
			Log.i("小金库收益第" + page + "页：", html);
			if (pageNum1 == 0) {
				String pageNumStr = HttpUtils.getValue(html, pageNump);
				if (!TextUtils.isEmpty(pageNumStr)) {
					pageNum1 = Integer.parseInt(pageNumStr.trim());
				} else {
					pageNum1 = 1;
				}
			}
			
			List<String[]> xiaoJinKu1List = HttpUtils.getListArray(html, xiaoJinKup);
			if(xiaoJinKu1List != null && xiaoJinKu1List.size() > 0){
				for(int i = 0; i < xiaoJinKu1List.size();i++){
					MyBill myBill = new MyBill();
					myBill.setBillType("4");
					
					Date billDate = sdf3.parse(xiaoJinKu1List.get(i)[0].trim()); 
					String newBillDate = sdf.format(billDate);
					myBill.setBillTime(newBillDate);
					
					myBill.setBillTitle(xiaoJinKu1List.get(i)[1].trim());
					String money = xiaoJinKu1List.get(i)[2];
					if(money.startsWith("-") || money.startsWith("+")){
						myBill.setBillAmount(yuanToFen(money.substring(1, money.length()).trim()));
					} else {
						myBill.setBillAmount(yuanToFen(money.trim()));
					}
					myBill.setStatusName(xiaoJinKu1List.get(i)[3].trim());
					myBill.setOtherName(xiaoJinKu1List.get(i)[4].trim());
					
					billList.add(myBill);
				}
				return true;
			} else {
				upLogInfo("11", "小金库-收益获取失败", result);
				return false;
			}
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getXiaoJinKu2(int page, String cookie) {
		showProgress(65);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "*/*");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "xjk.jr.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String url = "https://xjk.jr.jd.com/gold/trades?tradeType=TRANSIN&startDate="+ mBeginDate +"&endDate=" + mEndDate + "&pageSize=10&pageNo=" + page;
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			String html = HttpUtils.replRtnSpace(result);
			Log.i("小金库转入第" + page + "页：", html);
			if (pageNum2 == 0) {
				String pageNumStr = HttpUtils.getValue(html, pageNump);
				if (!TextUtils.isEmpty(pageNumStr)) {
					pageNum2 = Integer.parseInt(pageNumStr.trim());
				} else {
					pageNum2 = 1;
				}
			}
			
			List<String[]> xiaoJinKu1List = HttpUtils.getListArray(html, xiaoJinKup);
			if(xiaoJinKu1List != null && xiaoJinKu1List.size() > 0){
				for(int i = 0; i < xiaoJinKu1List.size();i++){
					MyBill myBill = new MyBill();
					myBill.setBillType("5");
					
					Date billDate = sdf3.parse(xiaoJinKu1List.get(i)[0].trim()); 
					String newBillDate = sdf.format(billDate);
					myBill.setBillTime(newBillDate);
					
					myBill.setBillTitle(xiaoJinKu1List.get(i)[1].trim());
					String money = xiaoJinKu1List.get(i)[2];
					if(money.startsWith("-") || money.startsWith("+")){
						myBill.setBillAmount(yuanToFen(money.substring(1, money.length()).trim()));
					} else {
						myBill.setBillAmount(yuanToFen(money.trim()));
					}
					myBill.setStatusName(xiaoJinKu1List.get(i)[3].trim());
					myBill.setOtherName(xiaoJinKu1List.get(i)[4].trim());
					
					billList.add(myBill);
				}
				return true;
			} else {
				upLogInfo("12", "小金库-转入获取失败", result);
				return false;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getXiaoJinKu3(int page, String cookie) {
		showProgress(75);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "*/*");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "xjk.jr.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String url = "https://xjk.jr.jd.com/gold/trades?tradeType=TRANSOUT&startDate="+ mBeginDate +"&endDate=" + mEndDate + "&pageSize=10&pageNo=" + page;
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			String html = HttpUtils.replRtnSpace(result);
			Log.i("小金库转出第" + page + "页：", html);
			if (pageNum3 == 0) {
				String pageNumStr = HttpUtils.getValue(html, pageNump);
				if (!TextUtils.isEmpty(pageNumStr)) {
					pageNum3 = Integer.parseInt(pageNumStr.trim());
				} else {
					pageNum3 = 1;
				}
			}
			List<String[]> xiaoJinKu1List = HttpUtils.getListArray(html, xiaoJinKup);
			if(xiaoJinKu1List != null && xiaoJinKu1List.size() > 0){
				for(int i = 0; i < xiaoJinKu1List.size();i++){
					MyBill myBill = new MyBill();
					myBill.setBillType("6");
					
					Date billDate = sdf3.parse(xiaoJinKu1List.get(i)[0].trim()); 
					String newBillDate = sdf.format(billDate);
					myBill.setBillTime(newBillDate);
					
					myBill.setBillTitle(xiaoJinKu1List.get(i)[1].trim());
					String money = xiaoJinKu1List.get(i)[2];
					if(money.startsWith("-") || money.startsWith("+")){
						myBill.setBillAmount(yuanToFen(money.substring(1, money.length()).trim()));
					} else {
						myBill.setBillAmount(yuanToFen(money.trim()));
					}
					myBill.setStatusName(xiaoJinKu1List.get(i)[3].trim());
					myBill.setOtherName(xiaoJinKu1List.get(i)[4].trim());
					
					billList.add(myBill);
				}
				return true;
			} else {
				upLogInfo("13", "小金库-转出获取失败", result);
				return false;
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	private boolean getXiaoJinKu4(int page, String cookie) {
		showProgress(85);
		try {
			Map<String, String> heades = new HashMap<String, String>();
			heades.put("Accept", "*/*");
			heades.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
			heades.put("Cache-Control", "no-cache");
			heades.put("Connection", "keep-alive");
			heades.put("Cookie", cookie);
			heades.put("Host", "xjk.jr.jd.com");
			heades.put("Pragma", "no-cache");
			heades.put("Referer", "http://xjk.jr.jd.com/gold/page.htm");
			heades.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
			heades.put("X-Requested-With", "XMLHttpRequest");
			String url = "https://xjk.jr.jd.com/gold/trades?tradeType=FROZEN_UNFROZEN&startDate="+ mBeginDate +"&endDate=" + mEndDate + "&pageSize=10&pageNo=" + page;
			String result = HttpUtils.get(url, heades, Charset.defaultCharset());
			String html = HttpUtils.replRtnSpace(result);
			Log.i("小金库冻结/解冻第" + page + "页：", html);
			if (pageNum4 == 0) {
				String pageNumStr = HttpUtils.getValue(html, pageNump);
				if (!TextUtils.isEmpty(pageNumStr)) {
					pageNum4 = Integer.parseInt(pageNumStr.trim());
				} else {
					pageNum4 = 1;
				}
			}
			List<String[]> xiaoJinKu1List = HttpUtils.getListArray(html, xiaoJinKup);
			if(xiaoJinKu1List != null && xiaoJinKu1List.size() > 0){
				for(int i = 0; i < xiaoJinKu1List.size();i++){
					MyBill myBill = new MyBill();
					myBill.setBillType("7");
					
					Date billDate = sdf3.parse(xiaoJinKu1List.get(i)[0].trim()); 
					String newBillDate = sdf.format(billDate);
					myBill.setBillTime(newBillDate);
					
					myBill.setBillTitle(xiaoJinKu1List.get(i)[1].trim());
					String money = xiaoJinKu1List.get(i)[2];
					if(money.startsWith("-") || money.startsWith("+")){
						myBill.setBillAmount(yuanToFen(money.substring(1, money.length()).trim()));
					} else {
						myBill.setBillAmount(yuanToFen(money.trim()));
					}
					myBill.setStatusName(xiaoJinKu1List.get(i)[3].trim());
					myBill.setOtherName(xiaoJinKu1List.get(i)[4].trim());
					
					billList.add(myBill);
				}
				return true;
			} else {
				upLogInfo("14", "小金库-冻结/解冻获取失败", result);
				return false;
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	//上传日志
	private void upLogInfo(final String phase, final String expInfo, final String content) {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("userId", UserUtil.getUserInfo().getUserId().toString());
					params.put("phoneType", "1");
					params.put("type", phoneInfo);
					params.put("phase", phase);
					params.put("expInfo", expInfo);
					params.put("content", TextUtils.isEmpty(content) ? "" : content);
//					params.put("tengxunUrl", "");
					params.put("styleType", "2");
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.LOG_URL, "log/appLog", params);
					if (result.getSuccess()) {}
				} catch (Exception e) {
				}
			}
		});
	}
	
	@Click
	void header_image_return() {
		showDialog();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showDialog();
			return true;
		}
		return false;
	}

	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(JingDongActivity.this);
		builder.setTitle(R.string.app_name);
		builder.setMessage("离开认证会失败，需要重新认证，确定离开？");
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				JingDongActivity.this.finish();
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
	
	private String yuanToFen(String s){ //元转分
		int i = 0;
		if(s != null){
			double d = Double.parseDouble(s);
			i = (int)(d * 100);
		} 
		return i+"" ;
	}

	private void addProgress(final int diff) {
		runOnUiThread(new Runnable() {
			public void run() {
				pbAuthen.incrementProgressBy(diff);
			}
		});
	}

	private void showProgress(final int diff) {
		runOnUiThread(new Runnable() {
			public void run() {
				pbAuthen.setProgress(diff);
			}
		});
	}

	private void showErrorAndOut(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (!JingDongActivity.this.isFinishing()) {
					Toast.makeText(JingDongActivity.this, info, Toast.LENGTH_SHORT).show();
					JingDongActivity.this.finish();
				}
			}
		});
	}

	private void showInfo(final String info) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (!JingDongActivity.this.isFinishing()) {
					Toast.makeText(JingDongActivity.this, info, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
}
