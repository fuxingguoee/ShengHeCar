package com.il360.shenghecar.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.common.Variables;
import com.il360.shenghecar.model.goods.Goods;
import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.ImageLoaderUtil;

import java.util.List;

public class GoodsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<Goods> list;

	public GoodsAdapter(List<Goods> list, Context context) {
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
			convertView = mInflater.inflate(R.layout.listitem_goods, null);
			holder.ivGoodsPic = (ImageView) convertView.findViewById(R.id.ivGoodsPic);
			holder.tvGoodsName = (TextView) convertView.findViewById(R.id.tvGoodsName);
			holder.tvGoodsPrice = (TextView) convertView.findViewById(R.id.tvGoodsPrice);
			holder.tvGoodsPrice2 = (TextView) convertView.findViewById(R.id.tvGoodsPrice2);
			convertView.setTag(holder);
			
			if(list.get(position).getSmallPic() != null){

				if(list.get(position).getSmallPic().startsWith("http")) {//腾讯云
				        ImageFromTxyUtil.loadImage(context, list.get(position).getSmallPic(), holder.ivGoodsPic);
			        } else {//本地服务器
					ImageLoaderUtil.getInstance().displayListItemImage(Variables.APP_BASE_URL + list.get(position).getSmallPic() , holder.ivGoodsPic);
				    }
				}
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvGoodsName.setText(list.get(position).getGoodsName() + list.get(position).getGoodsDesc());
		holder.tvGoodsPrice.setText("￥" + list.get(position).getDiscountPrice());
		holder.tvGoodsPrice2.setText("￥" + list.get(position).getGoodsPrice());
		holder.tvGoodsPrice2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
		holder.tvGoodsPrice2.getPaint().setAntiAlias(true);// 抗锯齿
		return convertView;
	}

	class ViewHolder {
		ImageView ivGoodsPic;
		TextView tvGoodsName;
		TextView tvGoodsPrice;
		TextView tvGoodsPrice2;
	}
}
