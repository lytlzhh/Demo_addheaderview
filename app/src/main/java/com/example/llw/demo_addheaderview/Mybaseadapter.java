package com.example.llw.demo_addheaderview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by llw on 2016/4/7.
 */
public class Mybaseadapter extends BaseAdapter {
    private List<Mygetitem> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public Mybaseadapter(List<Mygetitem> list, MainActivity thread) {
        this.list = list;
        this.context = thread;
        layoutInflater = LayoutInflater.from(context);
    }

    //????????????????????????????????????????????????????????????????????????
    public void onDateChange(List<Mygetitem> list) {
        this.list = list;
        this.notifyDataSetChanged();//如果数据改变的界面也跟随改变
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
        ViewHolder viewholder = null;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.layout, null);
            viewholder.textview = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.textview.setText(list.get(position).srt);
        return convertView;
    }

    public class ViewHolder {
        TextView textview;
    }
}
