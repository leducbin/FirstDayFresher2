package com.ldb.bin.firstdayfresher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class ListViewSearchAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<SearchItem> data;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}