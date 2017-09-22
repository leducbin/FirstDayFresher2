package com.ldb.bin.firstdayfresher;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class RecyclerRelatedAdapter extends  RecyclerView.Adapter<RecyclerRelatedAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Related> data;
    Activity activity;

    public RecyclerRelatedAdapter(Context context, ArrayList<Related> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerRelatedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.dong_rela,parent,false);

        return new RecyclerRelatedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerRelatedAdapter.ViewHolder holder, final int position) {
        Picasso.with(holder.imageView.getContext()).load(data.get(position).getHinhanh()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView =  (ImageView) itemView.findViewById(R.id.image_rela);
        }
    }
}