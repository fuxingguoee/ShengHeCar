package com.il360.shenghecar.activity.order;

import android.content.Intent;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.BaseWidgetActivity;
import com.il360.shenghecar.common.GlobalPara;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
@EActivity(R.layout.act_deal_rule)
public class DaiKouRuleActivity extends BaseWidgetActivity {
	@ViewById
	WebView wv_show;
	
	@ViewById
	TextView tvBack;
	@ViewById
	TextView tvSign;

	@Extra int type;
	@Extra String allNumber,principal,periodsMoney;//总期数,本金,每期应还金额
	
	@AfterViews
	void init(){
		wv_show.requestFocus(); // 请求焦点
		wv_show.getSettings().setJavaScriptEnabled(true); // 设置是否支持JavaScript
		wv_show.getSettings().setSupportZoom(true); // 设置是否支持缩放
		wv_show.getSettings().setBuiltInZoomControls(false); // 设置是否显示内建缩放工具
		wv_show.setHorizontalScrollBarEnabled(false);//滚动条水平是否显示
		wv_show.setVerticalScrollBarEnabled(false); //滚动条垂直是否显示
		wv_show.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		wv_show.loadUrl("http://www.ycaomei.com/cmfq/委托扣款授权书.html"
				+ "?name=" + GlobalPara.getOutUserBank().getCardName()
				+ "&bankName=" + GlobalPara.getOutUserBank().getBankName()
				+ "&bankCard=" + GlobalPara.getOutUserBank().getBankNo()
				+ "&idCard=" + GlobalPara.getOutUserRz().getIdNo()
				+ "&phone=" + GlobalPara.getOutUserBank().getPhone()); // 加载在线网页
		wv_show.setWebViewClient(new MyWebViewClient());
	}
	
	
	private class MyWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}
	
	@Click
	void tvBack(){
		DaiKouRuleActivity.this.finish();
	}
	
	@Click
	void tvSign(){
		Intent intent = new Intent(DaiKouRuleActivity.this,DealRuleActivity_.class);
		intent.putExtra("allNumber", allNumber);//传值
		intent.putExtra("type", type);
		intent.putExtra("principal", principal);
		intent.putExtra("periodsMoney", periodsMoney);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED && data != null) {
			setResult(RESULT_CANCELED, data);
			DaiKouRuleActivity.this.finish();
		}
	}

}
