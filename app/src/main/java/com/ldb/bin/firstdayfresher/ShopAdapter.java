package com.ldb.bin.firstdayfresher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bin on 09/08/2017.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder>{

    private Context context;
    private ArrayList<HinhAnh> dataShops;

    public ShopAdapter(Context context,ArrayList<HinhAnh> dataShops) {
        this.context = context;
        this.dataShops = dataShops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.dong_layout,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(holder.imgHinh.getContext()).load(dataShops.get(position).getHinh()).into(holder.imgHinh);
        holder.txtView.setText(dataShops.get(position).getTen());
    }

    @Override
    public int getItemCount() {
        return dataShops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgHinh;
        TextView txtView;
        public ViewHolder(View itemView) {
            super(itemView);
            imgHinh = (ImageView) itemView.findViewById(R.id.imageviewHinhAnh);
            txtView = (TextView) itemView.findViewById(R.id.txt_name);
        }
    }
}
