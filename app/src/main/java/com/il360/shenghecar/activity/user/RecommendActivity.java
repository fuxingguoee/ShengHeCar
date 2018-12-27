package com.il360.shenghecar.activity.user;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.activity.coupon.RankListActivity_;
import com.il360.shenghecar.common.MyApplication;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.util.CRequestUtil;
import com.il360.shenghecar.util.UserUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.act_recommend)
public class RecommendActivity extends BaseWidgetActivity {
	
	@ViewById TextView tvTextClick;
//	@ViewById ImageView imageView;
//	@ViewById TextView tvRecommend;
	
	@ViewById WebView webView;
	
	@Extra int flag;//99代表要计事事件
	
	@AfterViews
	void init() {
		tvTextClick.setHeight(24);
		tvTextClick.setWidth(24);
		tvTextClick.setBackgroundResource(R.drawable.ic_ranking_list);
		
		initWebView();
	}

	private void initWebView() {
		webView.requestFocus(); // 请求焦点
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setJavaScriptEnabled(true); // 设置是否支持JavaScript
		webView.getSettings().setSupportZoom(true); // 设置是否支持缩放
		webView.getSettings().setBuiltInZoomControls(true); // 设置是否显示内建缩放工具
		webView.setHorizontalScrollBarEnabled(true);// 滚动条水平是否显示
		webView.setVerticalScrollBarEnabled(true); // 滚动条垂直是否显示
		webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webView.getSettings().setDomStorageEnabled(true); // 设置支持DomStorage
		// 这里是注入Java对象
//		wv_show.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		webView.loadUrl("http://www.klfx888.com/share1/share.html"); // 加载在线网页
		// 触摸焦点起作用
		webView.setWebViewClient(new MyWebViewClient());
	}
	
	final class MyWebViewClient extends WebViewClient {
		// 在WebView中而不是系统默认浏览器中显示页面

		/**
		 * 拦截 url 跳转,在里边添加点击链接跳转或者操作
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			String newUrl = "";
			try {
				newUrl = java.net.URLDecoder.decode(url,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> map = CRequestUtil.URLRequest(newUrl);
			
			if (flag == 99) {
				MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_share");
			}
			platformShare(map.get("imageUrl"),map.get("title"),map.get("content"));
			
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		// 页面载入前调用
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			super.onPageStarted(view, url, favicon);
		}

		// 页面载入完成后调用
		@Override
		public void onPageFinished(WebView view, String url) {

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

	@Click
	void tvTextClick() {
		Intent intent = new Intent(RecommendActivity.this, RankListActivity_.class);
		startActivity(intent);
	}

//	@Click
//	void tvRecommend() {
//		if(flag == 99){
//			MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_share");
//		}
//		platformShare();
//	}
	
	//分享
	public void platformShare(String imageUrl,String title,String content) {
		final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[] { SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE };
		UMImage image = new UMImage(mContext,imageUrl);
		String url = "";
		if (UserUtil.judgeUserInfo()) {
			url = Variables.APP_BASE_URL + "tui/personalRecommend.html?agentNo=" + UserUtil.getUserInfo().getLoginName();
		} else {
			url = Variables.APP_BASE_URL + "tui/personalRecommend.html";
		}
		
		UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(content);//描述
		
		
		new ShareAction(RecommendActivity.this)
		.setDisplayList(displaylist)
		.setCallback(shareListener)
	    .withMedia(web)
	    .open();
	}

	private UMShareListener shareListener = new UMShareListener() {
		/**
		 * @descrption 分享开始的回调
		 * @param platform
		 *            平台类型
		 */
		@Override
		public void onStart(SHARE_MEDIA platform) {

		}

		/**
		 * @descrption 分享成功的回调
		 * @param platform
		 *            平台类型
		 */
		@Override
		public void onResult(SHARE_MEDIA platform) {
			if(flag == 99){
				 if(platform.getName().equals("WEIXIN")){
					MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_success","微信好友");
				} else if(platform.getName().equals("WEIXIN_CIRCLE")){
					MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_success","微信朋友圈");
				} else if(platform.getName().equals("QQ")){
					MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_success","QQ");
				} else if(platform.getName().equals("QZONE")) {
					MobclickAgent.onEvent(MyApplication.getContextObject(), "redbag_success","QQ空间");
				}
			}
			Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
		}

		/**
		 * @descrption 分享失败的回调
		 * @param platform
		 *            平台类型
		 * @param t
		 *            错误原因
		 */
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
		}

		/**
		 * @descrption 分享取消的回调
		 * @param platform
		 *            平台类型
		 */
		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(mContext, "已取消分享", Toast.LENGTH_SHORT).show();
		}
	};
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
