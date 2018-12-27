package com.il360.shenghecar.adapter;

import java.util.List;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.coupon.UserCoupon;
import com.il360.shenghecar.util.DataUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CouponAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<UserCoupon> list;

	public CouponAdapter(List<UserCoupon> list, Context context) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_coupon, null);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
			holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
			
			holder.relativeLayout1 = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
			holder.tvBuyNow = (TextView) convertView.findViewById(R.id.tvBuyNow);
			holder.ivShiXiao = (ImageView) convertView.findViewById(R.id.ivShiXiao);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (list.get(position).getNumber().intValue() > list.get(position).getUsedNumber().intValue()) {
			holder.relativeLayout1.setBackgroundResource(R.drawable.bg_quane_1);
			holder.tvBuyNow.setVisibility(View.VISIBLE);
			holder.ivShiXiao.setVisibility(View.GONE);
			holder.tvNumber.setText(
					"拥有" + (list.get(position).getNumber().intValue() - list.get(position).getUsedNumber().intValue()) + "张");
		} else {
			holder.relativeLayout1.setBackgroundResource(R.drawable.bg_shixiao_1);
			holder.tvBuyNow.setVisibility(View.GONE);
			holder.ivShiXiao.setVisibility(View.VISIBLE);
			holder.tvNumber.setText("已用" + list.get(position).getUsedNumber().intValue() + "张");
		}
		holder.tvTime.setText("有效时间：" + DataUtil.getLongToDateShort2(list.get(position).getStartTime()) + "-"
				+ DataUtil.getLongToDateShort2(list.get(position).getEndTime()));
		holder.tvMoney.setText(list.get(position).getReduceAmount().toString());
		
		return convertView;
	}

	class ViewHolder {
		TextView tvTime;
		TextView tvMoney;
		TextView tvNumber;
		RelativeLayout relativeLayout1;
		TextView tvBuyNow;
		ImageView ivShiXiao;
	}
}
