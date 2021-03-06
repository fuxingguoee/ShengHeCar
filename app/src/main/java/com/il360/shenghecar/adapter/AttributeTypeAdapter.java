package com.il360.shenghecar.adapter;

import java.util.List;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.goods.AttributeType;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttributeTypeAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<AttributeType> list;
	private int typeId;

	public AttributeTypeAdapter(List<AttributeType> list, Context context, int typeId) {
		this.context = context;
		this.list = list;
		this.typeId = typeId;
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
			convertView = mInflater.inflate(R.layout.listitem_attribute, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(list.get(position).getTypeDesc());
		if (list.get(position).getTypeId().intValue() == typeId) {
			holder.tvName.setBackgroundResource(R.drawable.bg_corners_transparent_red);
			holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.red));
		} else {
			if(list.get(position).getFlag() != null && list.get(position).getFlag().intValue() == 0){
				holder.tvName.setBackgroundResource(R.drawable.bg_corners_transparent_gray3);
				holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_gray2));
			} else {
				holder.tvName.setBackgroundResource(R.drawable.bg_corners_transparent_gray);
				holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black));
			}
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvName;
	}
}
