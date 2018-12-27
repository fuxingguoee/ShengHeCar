package com.il360.shenghecar.adapter;

import java.util.List;

import com.il360.shenghecar.util.ImageFromTxyUtil;
import com.il360.shenghecar.util.SDCardUtil;
import com.il360.shenghecar.R;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<String> list;
	private boolean flag;
	private int width;

	public ImageAdapter(List<String> list, Context context) {
		this.context = context;
		this.list = list;
		flag = SDCardUtil.hasSDCard(context);
		
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        width = dm.widthPixels;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_image, null);
			holder.ivImageShow = (ImageView) convertView.findViewById(R.id.ivImageShow);
			
			int screenWidth = width;
			ViewGroup.LayoutParams lp = holder.ivImageShow.getLayoutParams();
			lp.width = screenWidth;
			lp.height = screenWidth * 5;
			holder.ivImageShow.setLayoutParams(lp);


//			int screenWidth = width;
//			ViewGroup.LayoutParams lp = holder.ivImageShow.getLayoutParams();
//			lp.width = screenWidth;
//			lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//			holder.ivImageShow.setLayoutParams(lp);
//			holder.ivImageShow.setMaxWidth(screenWidth);
//			holder.ivImageShow.setMaxHeight((int) (screenWidth * 10));// 这里其实可以根据需求而定

			if (flag && list.get(position)!= null) {
				ImageFromTxyUtil.loadImage(context, list.get(position), holder.ivImageShow);
			} else {
				holder.ivImageShow.setImageResource(R.drawable.ic_image404);
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	class ViewHolder {
		ImageView ivImageShow;
	}
}