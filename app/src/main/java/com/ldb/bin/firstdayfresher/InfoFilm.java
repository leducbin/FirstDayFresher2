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
import android.support.v7.app.AlertDialog;
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
    ImageView videoView;
    RelativeLayout listView;
    Toolbar toolbar;
    ImageView imageView,image_video;
    TextView textView;
    ProgressDialog pDialog;
    DrawerLayout drawerLayout;
    TextView txttitle,txtclassification,txtdescription,txtgenres,txtactors,txtlanguage,textView_epi;
    RecyclerView recyclerView,recyclerView_ep,recyclerView_related;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_film);

        AnhXa();

        Intent intent = getIntent();
        String href = intent.getStringExtra("href");
        Log.e(TAG,"href " +href);
        String url = intent.getStringExtra("url");
        Log.e(TAG,"url " + url);
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
            case "AVOD":
                textView.setText("MIỄN PHÍ");
                break;
            case "SVOD":
                textView.setText("THUÊ PHIM");
                break;
            case "TVOD":
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
            getInfo.setType(separated[1]);
            getInfo.execute();
        }
        else if(separated[1].equals("movie"))
        {
            GetInfo getInfo = new GetInfo();
            getInfo.setUrl(separated[2]);
            getInfo.setType(separated[1]);
            getInfo.execute();

        }
    }

    private class GetInfo extends AsyncTask<Void, Void, Void>
    {

        private String url,type;
        private String reponse,eps_reponse,rela_reponse;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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
            HttpHandler rela = new HttpHandler(InfoFilm.this);
            this.rela_reponse = rela.makeServiceCall("http://api.danet.vn/products/"+this.url+"/related");
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
                        BitmapDrawable bg = new BitmapDrawable(getResources(),bitmap);
                        drawerLayout.setBackground(bg);
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
                if(type.equals("series"))
                {
                    JSONObject json_eps = new JSONObject(eps_reponse);
                    final JSONArray data = json_eps.getJSONArray("data");
                    final ArrayList<Episodes> arrayList_ep = new ArrayList<Episodes>();
                    for (int z=0;z<data.length();z++)
                    {
                        JSONObject eps_num = data.getJSONObject(z);
                        Episodes episodes = new Episodes();
                        episodes.setNumber(eps_num.getInt("episode_number"));
                        episodes.setArrayList(eps_num.toString());
                        arrayList_ep.add(episodes);
                    }
                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "series videoview click");
                            Intent intent = new Intent(InfoFilm.this,VideoPlay.class);
                            intent.putExtra("url",url);
                            try {
                                Log.e(TAG, "Start activity");
                                intent.putExtra("id",data.getJSONObject(0).getString("id"));
                                InfoFilm.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                            } catch (JSONException e) {
                            }

                        }
                    });
                    recyclerView.setHasFixedSize(true);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(InfoFilm.this,LinearLayoutManager.HORIZONTAL,false);
                    recyclerView.setLayoutManager(layoutManager);
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(InfoFilm.this,arrayList_ep,0);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(InfoFilm.this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {
                                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(InfoFilm.this,arrayList_ep,position);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                    try {
                                        JSONObject jsonObject = new JSONObject(arrayList_ep.get(position).getArrayList());
                                        String id_video_ep = jsonObject.getString("id");
                                        Intent intent = new Intent(InfoFilm.this,VideoPlay.class);
                                        intent.putExtra("url",url);
                                        intent.putExtra("id",id_video_ep);
                                        Log.e(TAG,"data info "+ id_video_ep + url);
                                        InfoFilm.this.startActivity(intent);
                                        overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                                    } catch (JSONException e) {

                                    }

                                }

                                @Override public void onLongItemClick(View view, int position) {
                                    Toast.makeText(InfoFilm.this,position + "long length",Toast.LENGTH_LONG).show();
                                }
                            })
                    );
                }
                else if (type.equals("movie"))
                {
                    JSONObject json_eps = new JSONObject(eps_reponse);
                    final JSONArray data = json_eps.getJSONArray("data");
                    textView_epi.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    videoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e(TAG, "movie videoview click");
                            Intent intent = new Intent(InfoFilm.this,VideoPlay.class);
                            intent.putExtra("url",url);
                            try {
                                intent.putExtra("id",data.getJSONObject(0).getString("id"));
                                InfoFilm.this.startActivity(intent);
                                overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                            } catch (JSONException e) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(InfoFilm.this);
                                dialog.setTitle("Hãy đăng nhập để được xem phim !").show();
                            }

                        }
                    });

                }

                JSONObject json_rela = new JSONObject(rela_reponse);
                JSONArray data_rela = json_rela.getJSONArray("data");
                ArrayList<Related> arrayList_re = new ArrayList<Related>();
                for (int f=0;f<data_rela.length();f++)
                {
                    JSONObject ob_rela = data_rela.getJSONObject(f);
                    JSONObject image_rela = ob_rela.getJSONObject("image");
                    String image_url = image_rela.getString("base_uri");
                    Related tmp = new Related();
                    tmp.setData(ob_rela.toString());
                    tmp.setHinhanh(image_url);
                    arrayList_re.add(tmp);
                }
                recyclerView_related.setHasFixedSize(true);
                LinearLayoutManager layoutManager_related = new LinearLayoutManager(InfoFilm.this,LinearLayoutManager.HORIZONTAL,false);
                recyclerView_related.setLayoutManager(layoutManager_related);
                RecyclerRelatedAdapter recyclerViewAdapter_related = new RecyclerRelatedAdapter(InfoFilm.this,arrayList_re);
                recyclerView_related.setAdapter(recyclerViewAdapter_related);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void AnhXa() {

        videoView = (ImageView) findViewById(R.id.videoview);
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
        textView_epi = (TextView) findViewById(R.id.epi);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
    }
}
