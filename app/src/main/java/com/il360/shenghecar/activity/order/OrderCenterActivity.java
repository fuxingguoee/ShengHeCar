package com.il360.shenghecar.activity.order;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.MyFragmentActivity;
import com.il360.shenghecar.adapter.FragmentTabAdapter;
import com.il360.shenghecar.fragment.order.OrderBuyFragment_;
import com.il360.shenghecar.fragment.order.OrderRecoveryFragment_;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

@EActivity(R.layout.act_order_center)
public class OrderCenterActivity extends MyFragmentActivity {

	@ViewById ImageView header_image_return;
	/** 导航栏 **/
	@ViewById
	RadioGroup rgpNavMenu;
	@ViewById
	LinearLayout llLine;

	@ViewById
	RadioButton rbOrderBuy;
	@ViewById
	RadioButton rbOrderRecovery;

	@ViewById
	TextView tvLine1;
	@ViewById
	TextView tvLine2;

	@ViewById
	FrameLayout flytOrderContent;

	/** 页面碎片 **/
	public static List<Fragment> fragments = new ArrayList<Fragment>();
	/** 处理fragment不重绘的适配器 **/
	private FragmentTabAdapter adapter;
	@Extra
	int mShowTabIndex;

	// fragment切换位置
	private int fragmentIndex = 0;

	@AfterViews
	void init() {
		
		tvLine1.setBackgroundResource(R.color.text_red);
		tvLine2.setBackgroundResource(R.color.line_d);
		
		initView();
	}

	private void initView() {
		if (fragments != null) {
			fragments.clear();
		}
		fragments.add(new OrderBuyFragment_());
		fragments.add(new OrderRecoveryFragment_());
		adapter = new FragmentTabAdapter(this, fragments, R.id.flytOrderContent, rgpNavMenu, mShowTabIndex);
		adapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
				fragmentIndex = index;
			}
		});
	}

	@Click
	void rbOrderBuy(){
		tvLine1.setBackgroundResource(R.color.text_red);
		tvLine2.setBackgroundResource(R.color.line_d);
	}
	
	@Click
	void rbOrderRecovery(){
		tvLine1.setBackgroundResource(R.color.line_d);
		tvLine2.setBackgroundResource(R.color.text_red);
	}
	
	@Click
	void header_image_return(){
		OrderCenterActivity.this.finish();
	}
	
}
