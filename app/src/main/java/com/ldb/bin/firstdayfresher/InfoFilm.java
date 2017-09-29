package com.ldb.bin.firstdayfresher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.blurry.Blurry;

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
    int REQUEST_CODE_EDIT = 123;
    int REQUEST_LOGOUT = 234;
    DrawerLayout drawerLayout;
    TextView txttitle,txtclassification,txtdescription,txtgenres,txtactors,txtlanguage,textView_epi;
    RecyclerView recyclerView,recyclerView_ep,recyclerView_related;
    LinearLayout linearLayout;
    ScrollView scrollView;
    SharedPreferences sharedPreferences;
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
            Log.e(TAG,"data rela" + rela_reponse);
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
                JSONObject profile = image.getJSONObject("profile");
                JSONArray poster = profile.getJSONArray("poster");
                for (int z=0;z<poster.length();z++)
                {

                    Log.e(TAG,"url " + poster.getJSONObject(z).toString());
                    if(poster.getJSONObject(z).getInt("width") == 210){
                        String bg_info = poster.getJSONObject(z).getString("url");
                        Picasso.with(InfoFilm.this).load(bg_info).into(new Target() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                Bitmap blurredBitmap = BlurBuilder.blur( InfoFilm.this, bitmap );
                                scrollView.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                }


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
                            if(sharedPreferences.getString("accessToken","").equals(""))
                            {
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(InfoFilm.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(InfoFilm.this);
                                }
                                builder.setTitle("Hãy đăng nhập để xem hoặc mua phim!!!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(InfoFilm.this, Login.class);
                                                startActivityForResult(intent,REQUEST_CODE_EDIT);
                                                overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
//
                            }
                            else if(sharedPreferences.getString("accessToken","") != null)
                            {
                                final String token_user = sharedPreferences.getString("accessToken","");
                                String url_user = "http://api.danet.vn/products/1692";
                                RequestQueue requestQueue = Volley.newRequestQueue(InfoFilm.this);
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_user, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response != null){
                                            try {
                                                JSONObject jsonObject_user = new JSONObject(response);
                                                Toast.makeText(InfoFilm.this,"Đã có trả về rồi",Toast.LENGTH_LONG).show();
                                                Log.e(TAG, "data " + jsonObject_user);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                ){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("Movideo-Auth", token_user);
                                        return params;
                                    }
                                }
                                        ;

                                requestQueue.add(stringRequest);
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
        scrollView = (ScrollView) findViewById(R.id.scrollView_info);
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode==RESULT_OK && data !=null )
        {
            String data_new = data.getStringExtra("data");
            try {
                JSONObject jsonObject_new = new JSONObject(data_new);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("identifier",jsonObject_new.getString("identifier"));
                editor.putString("accessToken",jsonObject_new.getString("accessToken"));
                editor.putString("phone",jsonObject_new.getString("phone"));
                editor.putString("currency",jsonObject_new.getString("currency"));
                editor.putString("object",jsonObject_new.getString("object"));
                editor.putString("credits",jsonObject_new.getString("credits"));
                editor.putString("given_name",jsonObject_new.getString("given_name"));
                editor.putString("family_name",jsonObject_new.getString("family_name"));
                editor.putString("date_of_birth",jsonObject_new.getString("date_of_birth"));
                editor.putString("devices",jsonObject_new.getString("family_name"));
                editor.putString("subscription",jsonObject_new.getString("subscription"));
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(requestCode == REQUEST_LOGOUT && resultCode==RESULT_OK)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private static class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 7.5f;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }
}
