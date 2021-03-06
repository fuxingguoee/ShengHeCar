package com.il360.shenghecar.adapter;

import java.util.List;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.user.UserReward;
import com.il360.shenghecar.util.DataUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommisGetAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private List<UserReward> list;
	
	public CommisGetAdapter(List<UserReward> list, Context context) {
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
		if(convertView==null){
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_commis_get, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvMoney = (TextView) convertView.findViewById(R.id.tvMoney);
			holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(list.get(position).getUserNameStr() + "\n" + list.get(position).getLoginNameStr());
		holder.tvMoney.setText(list.get(position).getAmount().toString());
		holder.tvDate.setText(list.get(position).getCreateTime() != null ? DataUtil.getLongToDateShort(list.get(position).getCreateTime()) : "");
		holder.tvStatus.setText(list.get(position).getDesc());
		return convertView;
	}
	class ViewHolder{
		TextView tvName;
		TextView tvDate;
		TextView tvMoney;
		TextView tvStatus;
	}
}
