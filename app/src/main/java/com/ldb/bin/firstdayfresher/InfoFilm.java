package com.ldb.bin.firstdayfresher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class InfoFilm extends AppCompatActivity {
    public static final String railURL = "http://api.danet.vn/data/rails/go";
    public static final String railCineURL = "http://api.danet.vn/data/rails/cineplex";
    public static final String railBuffURL = "http://api.danet.vn/data/rails/buffet";
    private String TAG = MainActivity.class.getSimpleName();
    VideoView videoView;
    RelativeLayout listView;
    Toolbar toolbar;
    ImageView imageView,image_video;
    TextView textView;
    ProgressDialog pDialog;
    DrawerLayout drawerLayout;
    TextView txttitle,txtclassification,txtdescription,txtgenres,txtactors,txtlanguage;
    RecyclerView recyclerView,recyclerView_ep,recyclerView_related;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_film);

        AnhXa();

        Intent intent = getIntent();
        String href = intent.getStringExtra("href");
        String url = intent.getStringExtra("url");
        switch (url){
            case railURL:
                textView.setText("MIỄN PHÍ");
                break;
            case railBuffURL:
                textView.setText("THUÊ PHIM");
                break;
            case railCineURL:
                textView.setText("PHIM GÓI");
                break;
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
            }
        });


        String[] separated = href.split("/");
        Log.e(TAG,separated[1].equals("series")+"kiem tra");
        if (separated[1].equals("series"))
        {
            GetInfo getInfo = new GetInfo();
            getInfo.setUrl(separated[2]);
            getInfo.execute();
        }
        else
        {

        }
    }

    private class GetInfo extends AsyncTask<Void, Void, Void>
    {

        private String url;
        private String reponse,eps_reponse;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InfoFilm.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler(InfoFilm.this);

            // Making a request to url and getting response
            this.reponse = sh.makeServiceCall("http://api.danet.vn/products/"+this.url);
            HttpHandler eps = new HttpHandler(InfoFilm.this);
            this.eps_reponse = eps.makeServiceCall("http://api.danet.vn/products/"+this.url+"/episodes");

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                JSONObject json_reponse = new JSONObject(reponse);
                JSONObject image = json_reponse.getJSONObject("image");
                String bg = image.getString("base_uri");
                Log.e(TAG,"url " + bg);
                Picasso.with(InfoFilm.this).load(bg).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        drawerLayout.setBackground(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                txttitle.setText( json_reponse.getString("title"));
                txttitle.setTextSize(26);
                txtclassification.setText(json_reponse.getString("released")+"("+json_reponse.getString("classification")+")");
                txtdescription.setText(json_reponse.getString("description"));
                JSONArray argenres = json_reponse.getJSONArray("genres");

                String genres = "";
                for (int i=0;i<argenres.length();i++ ){
                    genres = genres + argenres.get(i) +", ";
                    Log.e(TAG,genres);
                }
                txtgenres.setText(genres);
                JSONArray jsonArray_2 = json_reponse.getJSONArray("actors");
                String actors = "";
                for (int k = 0; k < jsonArray_2.length();k++)
                {
                    actors = actors + jsonArray_2.get(k)+ ", ";
                }


                JSONArray jsonArray_3 = json_reponse.getJSONArray("directors");
                String directors = "";
                for (int l=0; l < jsonArray_3.length();l++)
                {
                    directors = directors + jsonArray_3.get(l)+ ", ";
                }
                txtactors.setText("Diễn viên " +actors +"\n" + "Đạo diễn " + directors);
                JSONObject jsonObject_2 = json_reponse.getJSONObject("language");
                JSONArray jsonArray_4 = jsonObject_2.getJSONArray("subtitles");
                String subtitles = "";
                for (int k=0;k < jsonArray_4.length();k++)
                {
                    subtitles = subtitles + jsonArray_4.get(k).toString()+ ", ";
                }
                JSONArray jsonArray_5 = jsonObject_2.getJSONArray("audios");
                String audio = "";
                for (int l=0;l< jsonArray_5.length();l++)
                {
                    if (jsonArray_5.get(l) == null)
                    {
                        audio = "null";
                    }
                    else
                    {
                        audio = audio + jsonArray_5.get(l).toString()+ ", ";
                    }
                }
                txtlanguage.setText("Phụ đề " +subtitles +"\n" + "Lồng tiếng " +  audio);

                JSONObject json_eps = new JSONObject(eps_reponse);
                JSONArray data = json_eps.getJSONArray("data");
                ArrayList<Episodes> arrayList_ep = new ArrayList<Episodes>();
                for (int z=0;z<data.length();z++)
                {
                    JSONObject eps_num = data.getJSONObject(z);
                    Episodes episodes = new Episodes();
                    episodes.setNumber(eps_num.getInt("episode_number"));
                    episodes.setArrayList(eps_num.toString());
                    arrayList_ep.add(episodes);
                }
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(InfoFilm.this,LinearLayoutManager.HORIZONTAL,false);
                recyclerView.setLayoutManager(layoutManager);
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(InfoFilm.this,arrayList_ep,0);
                recyclerView.setAdapter(recyclerViewAdapter);




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void AnhXa() {

        videoView = (VideoView) findViewById(R.id.videoview);

        listView = (RelativeLayout) findViewById(R.id.listview_video);
        imageView = (ImageView) findViewById(R.id.cencelview);
        drawerLayout = (DrawerLayout) findViewById(R.id.layout_video);
        textView = (TextView) findViewById(R.id.txtbar_video);
        image_video = (ImageView) findViewById(R.id.image_video);
        txttitle = (TextView) findViewById(R.id.txttitle);
        txtclassification = (TextView) findViewById(R.id.classification);
        txtdescription = (TextView) findViewById(R.id.description);
        txtgenres = (TextView) findViewById(R.id.genres);
        txtactors = (TextView) findViewById(R.id.actors);
        txtlanguage = (TextView) findViewById(R.id.language);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview_ep);
        recyclerView_related = (RecyclerView) findViewById(R.id.recycleview_related);
    }
}
