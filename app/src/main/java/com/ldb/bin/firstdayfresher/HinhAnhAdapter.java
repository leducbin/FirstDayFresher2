package com.ldb.bin.firstdayfresher;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bin on 09/07/2017.
 */

public class HinhAnhAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<HinhAnh> hinhAnhList;

    public HinhAnhAdapter(Context context, int layout, List<HinhAnh> hinhAnhList) {
        this.context = context;
        this.layout = layout;
        this.hinhAnhList = hinhAnhList;
    }

    @Override
    public int getCount() {
        return hinhAnhList.size();
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
        ImageView imgHinh;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.imgHinh = (ImageView) convertView.findViewById(R.id.imageviewHinhAnh);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        HinhAnh hinhAnh = hinhAnhList.get(position);
        Picasso.with(holder.imgHinh.getContext()).load(hinhAnh.getHinh()).into(holder.imgHinh);
        return convertView;
    }
}
