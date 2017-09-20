package com.ldb.bin.firstdayfresher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bin on 09/12/2017.
 */

public class SubnavigationAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private ArrayList<Subnavigation> subnavigationArrayList;

    public SubnavigationAdapter(Context context, int layout, ArrayList<Subnavigation> subnavigationArrayList) {
        this.context = context;
        this.layout = layout;
        this.subnavigationArrayList = subnavigationArrayList;
    }

    @Override
    public int getCount() {
        return subnavigationArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.textView = (TextView) convertView.findViewById(R.id.txtMenu);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        Subnavigation subnavigation = subnavigationArrayList.get(position);
        holder.textView.setText(subnavigation.getName());
        return convertView;
    }
}
