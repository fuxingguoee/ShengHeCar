package com.il360.shenghecar.adapter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.order.GoodsOrderActivity;
import com.il360.shenghecar.common.ExecuteTask;
import com.il360.shenghecar.common.LogUmeng;
import com.il360.shenghecar.connection.HttpRequestUtil;
import com.il360.shenghecar.connection.TResult;
import com.il360.shenghecar.connection.UrlEnum;
import com.il360.shenghecar.model.order.Order;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.UserUtil;
import com.il360.shenghecar.view.CustomDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

public class OrderBuyAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private List<Order> list;
	DecimalFormat df = new DecimalFormat("0.00");
	private GoodsOrderActivity.ListCallback callback;

	public OrderBuyAdapter(List<Order> list, Context context, GoodsOrderActivity.ListCallback callback) {
		this.context = context;
		this.list = list;
		this.callback = callback;
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
			convertView = mInflater.inflate(R.layout.listitem_order_buy, null);
			holder.ivOrderPic = (ImageView) convertView.findViewById(R.id.ivOrderPic);
			holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
			holder.tvOrderTime = (TextView) convertView.findViewById(R.id.tvOrderTime);
			holder.tvOrderName = (TextView) convertView.findViewById(R.id.tvOrderName);
			holder.tvGoodsPrice = (TextView) convertView.findViewById(R.id.tvGoodsPrice);
			holder.tvGoodsNum = (TextView) convertView.findViewById(R.id.tvGoodsNum);
			holder.tvOrderPrice = (TextView) convertView.findViewById(R.id.tvOrderPrice);
			holder.tvPayWay = (TextView) convertView.findViewById(R.id.tvPayWay);
			holder.tvCancel = (TextView) convertView.findViewById(R.id.tvCancel);
			holder.tvRefund = (TextView) convertView.findViewById(R.id.tvRefund);
			holder.viewLine = (View) convertView.findViewById(R.id.viewLine);
			convertView.setTag(holder);
			
			// 从腾讯云下载图片
			if(list.get(position).getSmallPic() != null) {
				ImageFromTxyUtil.loadImage(context, list.get(position).getSmallPic(), holder.ivOrderPic);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvOrderTime.setText(list.get(position).getCreateTime().toString());
		
		// 状态0 等待商家发货  1 退款中  2 等待打款 3 退款完成 4 拒绝退款 5 已取消
		holder.tvStatus.setText(list.get(position).getStatusDesc());
		if (list.get(position).getStatus() != null && list.get(position).getStatus() == 0) {
			holder.tvCancel.setVisibility(View.VISIBLE);
			holder.tvRefund.setVisibility(View.VISIBLE);
			holder.viewLine.setVisibility(View.VISIBLE);
		} else {
			holder.tvCancel.setVisibility(View.GONE);
			holder.tvRefund.setVisibility(View.GONE);
			holder.viewLine.setVisibility(View.GONE);
		}

		holder.tvOrderName.setText(list.get(position).getGoodsDesc());
		holder.tvGoodsPrice.setText("￥"+df.format(list.get(position).getGoodsPrice()));
		holder.tvGoodsNum.setText("x" + list.get(position).getOrderNum());
		holder.tvPayWay.setText("白条付款");
		double orderPrice = list.get(position).getGoodsPrice().doubleValue() * list.get(position).getOrderNum();
		holder.tvOrderPrice.setText("￥" + df.format(orderPrice));


		holder.tvCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog("确定取消该笔订单？", list.get(position).getOrderNo());
				
			}
		});

		holder.tvRefund.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.goToRefund(list.get(position));
			}
		});
		
		return convertView;
	}


	private void showDialog(String message,  final String orderNo) {

		CustomDialog.Builder builder = new CustomDialog.Builder(context);
		builder.setTitle(R.string.app_name);
		builder.setMessage(message);
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dealOrder(orderNo);
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

	private void dealOrder(final String orderNo) {
		ExecuteTask.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserUtil.getToken());
					params.put("orderNo", orderNo);
					TResult<Boolean, String> result = HttpRequestUtil.sendPostRequest(UrlEnum.BIZ_URL,
							"order/cancelOrder", params);

					if (result != null && result.getSuccess()) {
						JSONObject obj = new JSONObject(result.getResult());
						if (obj.getInt("code") == 1) {
							//回调刷新列表
							callback.initList();
						}
					}
				} catch (Exception e) {
					Log.e("OrderBuyAdapter", "dealOrder", e);
					LogUmeng.reportError(context, e);
				}
			}
		});
	}

	class ViewHolder {
		TextView tvOrderTime;
		TextView tvStatus;
		ImageView ivOrderPic;
		TextView tvOrderName;
		TextView tvGoodsPrice;
		TextView tvGoodsNum;
		TextView tvPayWay;
		TextView tvOrderPrice;
		TextView tvCancel;
		TextView tvRefund;
		View viewLine;
	}
}
