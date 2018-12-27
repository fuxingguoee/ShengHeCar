package com.il360.shenghecar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.Company;

import java.util.List;

/**
 * Created by Administrator on 2018/11/29 0029.
 */

public class CompanyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<Company> list;

    public CompanyAdapter(List<Company> list, Context context) {
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
            convertView = mInflater.inflate(R.layout.company, null);
            holder.branchName = (TextView) convertView.findViewById(R.id.branchName);
            holder.branchId = (TextView) convertView.findViewById(R.id.branchId);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.branchName.setText(list.get(position).getBranchName());
        return convertView;
    }
    class ViewHolder{
        TextView branchName;
        TextView branchId;
    }
}
