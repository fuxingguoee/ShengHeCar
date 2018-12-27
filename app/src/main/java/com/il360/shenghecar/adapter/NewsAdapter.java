package com.il360.shenghecar.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.activity.recovery.PhoneAssessActivity.ListCallback;
import com.il360.shenghecar.model.home.News;
import com.il360.shenghecar.model.recovery.AssessDetails;
import com.il360.shenghecar.util.DataUtil;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<News> list;

	public NewsAdapter(List<News> list, Context context) {
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
		final ViewHolder holder;
		if (convertView == null) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_news, null);
			holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);
			holder.tvNewsTitle = (TextView) convertView.findViewById(R.id.tvNewsTitle);
			holder.tvNewsTime = (TextView) convertView.findViewById(R.id.tvNewsTime);
			holder.tvNewsContext = (TextView) convertView.findViewById(R.id.tvNewsContext);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvNewsTitle.setText(list.get(position).getTitle());
		holder.tvNewsTime.setText(DataUtil.getLongToDate(Long.valueOf(list.get(position).getSendTime())));
		holder.tvNewsContext.setText(list.get(position).getContext());
		if (list.get(position).isCheck() != null && list.get(position).isCheck()) {
			holder.tvNewsContext.setVisibility(View.VISIBLE);
		} else {
			holder.tvNewsContext.setVisibility(View.GONE);
		}
		
		holder.linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list.get(position).isCheck() != null && list.get(position).isCheck()) {
					list.get(position).setCheck(false);
					holder.tvNewsContext.setVisibility(View.GONE);
				} else {
					list.get(position).setCheck(true);
					holder.tvNewsContext.setVisibility(View.VISIBLE);
				}
			}
		});
		
		return convertView;
	}

	class ViewHolder {
		LinearLayout linearLayout;
		TextView tvNewsTitle;
		TextView tvNewsTime;
		TextView tvNewsContext;
	}
}
