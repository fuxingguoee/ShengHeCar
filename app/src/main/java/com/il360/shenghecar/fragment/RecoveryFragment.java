package com.il360.shenghecar.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.recovery.PhoneTypesActivity_;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@EFragment(R.layout.fra_recovery)
public class RecoveryFragment extends MyFragment {

	@ViewById
	TextView tvAssess;
	@ViewById
	ImageView ivRecovery;
	
	private int width;
	
	@AfterViews
	void init(){
		initViews();
	}

	private void initViews() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		ViewGroup.LayoutParams lp = ivRecovery.getLayoutParams();
		lp.width = width;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		ivRecovery.setLayoutParams(lp);
		ivRecovery.setMaxWidth(width);
		ivRecovery.setMaxHeight((int) (width * 5));// 这里其实可以根据需求而定
	}

	@Click
	void tvAssess() {
		Intent intent = new Intent(getActivity(),PhoneTypesActivity_.class);
		startActivity(intent);
	}
}
