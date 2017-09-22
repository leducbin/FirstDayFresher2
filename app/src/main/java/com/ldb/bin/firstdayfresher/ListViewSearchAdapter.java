package com.ldb.bin.firstdayfresher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewSearchAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SearchItem> data;
    Activity activity;

    public ListViewSearchAdapter(Context context, int layout, ArrayList<SearchItem> data) {
        this.context = context;
        this.layout = layout;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
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
        TextView textView_title,textView_duration;
        ImageView image_search;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.textView_title = (TextView) convertView.findViewById(R.id.list_text);
            holder.textView_duration = (TextView) convertView.findViewById(R.id.list_duration);
            holder.image_search = (ImageView) convertView.findViewById(R.id.list_image);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchItem searchItem = data.get(position);
        holder.textView_title.setText(searchItem.getTitle());
        holder.textView_duration.setText(searchItem.getDuration());
        Picasso.with(context).load(searchItem.getImage()).into(holder.image_search);
        try {
            final JSONObject jsonObject = new JSONObject(searchItem.getData());
            final String type = jsonObject.getString("type");
            final Integer integer = jsonObject.getInt("id");
            final String url = jsonObject.getString("package_type");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,InfoFilm.class);
                intent.putExtra("href","/"+type+"/"+integer.toString());
                intent.putExtra("url",url);
                activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}