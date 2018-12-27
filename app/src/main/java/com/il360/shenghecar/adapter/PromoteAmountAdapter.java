package com.il360.shenghecar.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.recovery.PromoteAmount;
import com.il360.shenghecar.util.DataUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PromoteAmountAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private List<PromoteAmount> list;
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	public PromoteAmountAdapter(List<PromoteAmount> list, Context context) {
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
			convertView = mInflater.inflate(R.layout.listitem_promote_amount, null);
			holder.tvAddAmount = (TextView) convertView.findViewById(R.id.tvAddAmount);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvAddAmount.setText(list.get(position).getAmount() + "元");
		holder.tvTime.setText(DataUtil.getLongToDateShort(list.get(position).getCreateTime()));
		
		return convertView;
	}
	class ViewHolder{
		TextView tvAddAmount;
		TextView tvTime;
	}
}
