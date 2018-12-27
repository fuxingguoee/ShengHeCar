package com.il360.shenghecar.adapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.home.PayActivity_;
import com.il360.shenghecar.model.order.OrderExt;
import com.il360.shenghecar.util.DataUtil;
import com.il360.shenghecar.view.CustomDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RepayAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<OrderExt> list;
	DecimalFormat df = new DecimalFormat("0.00");
	SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");

	public RepayAdapter(List<OrderExt> list, Context context) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_repay, null);
			holder.llStayStill = (LinearLayout) convertView.findViewById(R.id.llStayStill);
			holder.tvLastData = (TextView) convertView.findViewById(R.id.tvLastData);
			holder.tvRepayment = (TextView) convertView.findViewById(R.id.tvRepayment);
			holder.ivTip = (ImageView) convertView.findViewById(R.id.ivTip);
			holder.tvOrderData = (TextView) convertView.findViewById(R.id.tvOrderData);
			holder.tvOrderNo = (TextView) convertView.findViewById(R.id.tvOrderNo);
			holder.tvOrderPrice = (TextView) convertView.findViewById(R.id.tvOrderPrice);
			holder.tvPay = (TextView) convertView.findViewById(R.id.tvPay);

			holder.llAlreadyRepaid = (RelativeLayout) convertView.findViewById(R.id.llAlreadyRepaid);
			holder.tvOrderData2 = (TextView) convertView.findViewById(R.id.tvOrderData2);
			holder.tvOrderNo2 = (TextView) convertView.findViewById(R.id.tvOrderNo2);
			holder.tvOrderPrice2 = (TextView) convertView.findViewById(R.id.tvOrderPrice2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(list.get(position).getStatus() != null && list.get(position).getStatus() == 0) {
			holder.llStayStill.setVisibility(View.VISIBLE);
			holder.llAlreadyRepaid.setVisibility(View.GONE);

			Date date = new Date(Long.valueOf(list.get(position).getExpireDay()));
			String dateString = formatter.format(date);
			holder.tvLastData.setText(dateString);

			if(list.get(position).getOverdueFee() != null && list.get(position).getOverdueFee().compareTo(BigDecimal.ZERO) == 1) {
				holder.ivTip.setVisibility(View.VISIBLE);
				holder.tvRepayment.setText(df.format(list.get(position).getOrderAmount().add(list.get(position).getOverdueFee())) + "元");
			} else {
				holder.ivTip.setVisibility(View.GONE);
				holder.tvRepayment.setText(df.format(list.get(position).getOrderAmount()) + "元");
			}
			holder.tvOrderData.setText(DataUtil.getLongToDateShort(Long.valueOf(list.get(position).getCreateTime())));
			holder.tvOrderNo.setText(list.get(position).getOrderNo());
			holder.tvOrderPrice.setText(df.format(list.get(position).getOrderAmount()) + "元");

		} else if(list.get(position).getStatus() != null && list.get(position).getStatus() == 1) {
			holder.llStayStill.setVisibility(View.GONE);
			holder.llAlreadyRepaid.setVisibility(View.VISIBLE);

			holder.tvOrderNo2.setText("账单" + list.get(position).getOrderNo());
			holder.tvOrderData2.setText(DataUtil.getLongToDate(Long.valueOf(list.get(position).getCloseDay())));

			if(list.get(position).getOverdueFee() != null && list.get(position).getOverdueFee().compareTo(BigDecimal.ZERO) == 1) {
				holder.tvOrderPrice2.setText(df.format(list.get(position).getOrderAmount().add(list.get(position).getOverdueFee())) + "元");
			} else {
				holder.tvOrderPrice2.setText(df.format(list.get(position).getOrderAmount()) + "元");
			}

		} else {
			holder.llStayStill.setVisibility(View.GONE);
			holder.llAlreadyRepaid.setVisibility(View.GONE);
		}

		holder.ivTip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showDialog(list.get(position).getOverdueRate(),list.get(position).getOverdueFee());
			}
		});

		holder.tvPay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, PayActivity_.class);
				intent.putExtra("myOrderExt",list.get(position));
				context.startActivity(intent);
			}
		});

		return convertView;
	}


	private void showDialog(BigDecimal overdueRate,BigDecimal overdueFee) {
		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setTitle(R.string.app_name);
		builder.setMessage("您已逾期，逾期利率："+overdueRate+"%，逾期费：" + overdueFee + "元");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	

	class ViewHolder {
		LinearLayout llStayStill;
		TextView tvLastData;
		TextView tvRepayment;
		ImageView ivTip;
		TextView tvOrderData;
		TextView tvOrderNo;
		TextView tvOrderPrice;
		TextView tvPay;

		RelativeLayout llAlreadyRepaid;
		TextView tvOrderData2;
		TextView tvOrderNo2;
		TextView tvOrderPrice2;
	}
}
