package com.ldb.bin.firstdayfresher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bin on 09/11/2017.
 */

public class ViewPagerAdapter extends PagerAdapter{

    private String TAG = "ViewPagerAdapter";
    private Context context;
    private ArrayList<Carousel> data;
    LayoutInflater layoutInflater;
    private ImageView[] ivArrayDotsPager;
    Activity activity;

    public ViewPagerAdapter(Context context, ArrayList<Carousel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.pager_items,container,false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_image);
        TextView textView = (TextView) itemView.findViewById(R.id.txtViewpages);


        //Set image and textview
        final Carousel carousel = data.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,InfoFilm.class);
                intent.putExtra("href",carousel.getHref());
                intent.putExtra("url",carousel.getTen());
                activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
            }
        });
        Picasso.with(imageView.getContext()).load(carousel.getBanner()).into(imageView);
        //Add view to container
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((View) object);
    }

}
