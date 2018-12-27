package com.il360.shenghecar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.il360.shenghecar.R;
import com.il360.shenghecar.model.InfoCheck;

import java.util.List;

/**
 * Created by Administrator on 2018/12/6 0006.
 */

public class InfoAdadpter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<InfoCheck> list;

    public InfoAdadpter(List<InfoCheck> list, Context context) {
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
            convertView = mInflater.inflate(R.layout.activity_info, null);
            holder.etName = (TextView) convertView.findViewById(R.id.etName);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.etName.setText(list.get(position).getName());
        return convertView;
    }
    class ViewHolder{
        TextView etName;
        TextView etCertificate;
        TextView tvCompany;
        TextView etOperatorPhone;
        TextView etOperator;
        TextView etEmergencyContact;
        TextView etEmergencyPhone;
    }
}
