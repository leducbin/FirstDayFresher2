package com.ldb.bin.firstdayfresher;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Bin on 09/13/2017.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<ListId> listIds;
    private ArrayList<ArrayList> listArray;
    private String TAG = MainActivity.class.getSimpleName();

    public ListAdapter(Context context, int layout, ArrayList<ListId> listIds, ArrayList<ArrayList> listArray) {
        this.context = context;
        this.layout = layout;
        this.listIds = listIds;
        this.listArray = listArray;
    }

    @Override
    public int getCount() {
        return listArray.size();
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
        RecyclerView recyclerView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.textView = (TextView) convertView.findViewById(R.id.textrecycle);
            holder.recyclerView = (RecyclerView) convertView.findViewById(R.id.recycleview);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        ListId subnavigation = listIds.get(position);
        ArrayList<HinhAnh> arr;
        arr = listArray.get(position);
        holder.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.recyclerView.getContext(), layoutManager.getOrientation());
        holder.recyclerView.addItemDecoration(dividerItemDecoration);
        holder.recyclerView.setLayoutManager(layoutManager);
//        Log.e(TAG,"Xem array coi" + arr.get(i).getTen());
        ShopAdapter shopAdapter = new ShopAdapter(context,arr);
        holder.recyclerView.setAdapter(shopAdapter);
        holder.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, holder.recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(context,position + "short length",Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(context,position + "long length",Toast.LENGTH_LONG).show();
                    }
                })
        );
        holder.textView.setText(subnavigation.getName());
        return convertView;
    }

}
