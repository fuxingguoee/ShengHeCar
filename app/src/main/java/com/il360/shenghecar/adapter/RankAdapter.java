package com.il360.shenghecar.adapter;

import java.util.List;

import com.il360.shenghecar.model.coupon.AgentUser;
import com.il360.shenghecar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RankAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<AgentUser> list;

	public RankAdapter(List<AgentUser> list, Context context) {
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
			convertView = mInflater.inflate(R.layout.listitem_rank, null);
			holder.ivRank = (ImageView) convertView.findViewById(R.id.ivRank);
			holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
			holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
			holder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_1);
		} else if (position == 1) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_2);
		} else if (position == 2) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_3);
		} else if (position == 3) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_4);
		} else if (position == 4) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_5);
		} else if (position == 5) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_6);
		} else if (position == 6) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_7);
		} else if (position == 7) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_8);
		} else if (position == 8) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_9);
		} else if (position == 9) {
			holder.ivRank.setBackgroundResource(R.drawable.ic_rank_10);
		}
		holder.tvPhone.setText(list.get(position).getLoginNameStr());
		holder.tvAmount.setText(list.get(position).getInvitationAmount().toString());
		holder.tvNumber.setText(list.get(position).getNumber().toString());
		return convertView;
	}

	class ViewHolder {
		ImageView ivRank;
		TextView tvPhone;
		TextView tvAmount;
		TextView tvNumber;
	}
}
