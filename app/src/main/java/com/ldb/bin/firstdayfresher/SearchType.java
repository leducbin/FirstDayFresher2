package com.ldb.bin.firstdayfresher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.R.attr.value;

public class SearchType extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    NavigationView naviView;
    ListView listMenu;
    Toolbar menuToolbar;
    DrawerLayout drawerLayout;
    ProgressDialog pDialog;
    TextView textViewdefault,textView,textView2,textViewmanhinh;
    ImageView imageView,imageView_search;
    Button buttonLogin;
    GridView gridView;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_type);
        AnhXa();
        ActionBar();
        textViewdefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchType.this, MainActivity.class);
                myIntent.putExtra("key", value);
                SearchType.this.startActivity(myIntent);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchType.this, MainActivity.class);
                myIntent.putExtra("key", 1); //Optional parameters
                SearchType.this.startActivity(myIntent);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SearchType.this, MainActivity.class);
                myIntent.putExtra("key", 2); //Optional parameters
                SearchType.this.startActivity(myIntent);
            }
        });
        Intent intent = getIntent();
        String offerings = intent.getStringExtra("offerings");
        String category = intent.getStringExtra("category");
        String genre = intent.getStringExtra("genre");
        String url_menu = intent.getStringExtra("url");
        try {
            String url_search =
                    "http://api.danet.vn/products/search?types="+category + "&genres="+
                            URLEncoder.encode(genre, "utf-8").toString()+"&offerings=" +offerings +"&page=1&limit=12";
            Log.e(TAG,url_search+"");
            GetContacts search_type = new GetContacts();
            search_type.setURL(url_menu);
            Log.e(TAG,"data url" + url_menu);
            search_type.setUrl_search(url_search);
            search_type.setUrl_title(url_menu);
            search_type.execute();
        } catch (UnsupportedEncodingException e) {

        }



    }

    private void AnhXa() {
        naviView = (NavigationView) findViewById(R.id.navi_menu);
        listMenu = (ListView) findViewById(R.id.listview_menu);
        menuToolbar = (Toolbar) findViewById(R.id.toolbarmanhinhchinh);
        imageView = (ImageView) findViewById(R.id.menubar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        textViewdefault = (TextView) findViewById(R.id.railgo);
        textViewmanhinh = (TextView) findViewById(R.id.txtbar);
        buttonLogin = (Button) findViewById(R.id.login);
        textView = (TextView) findViewById(R.id.railcine);
        textView2 = (TextView) findViewById(R.id.railbuffet);
        imageView_search = (ImageView) findViewById(R.id.search_info_film);
        gridView = (GridView) findViewById(R.id.gridview_search);
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
    }

    private void ActionBar()
    {
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        private String url,url_search;
        private String reponse,reponse_search;
        private String url_title;

        public String getUrl_search() {
            return url_search;
        }

        public void setUrl_search(String url_search) {
            this.url_search = url_search;
        }

        public String getUrl_title() {
            return url_title;
        }

        public void setUrl_title(String url_title) {
            this.url_title = url_title;
        }

        public void setURL(String url){
            this.url = url;
        }
        public String getURL(){
            return this.url;
        }


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SearchType.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler(SearchType.this);
            // Making a request to url and getting response
            this.reponse = sh.makeServiceCall(this.url);
            Log.e(TAG,"data " +reponse );
//            HttpHandler sh_search = new HttpHandler(SearchType.this);
//            // Making a request to url and getting response
//            this.reponse_search = sh_search.makeServiceCall(this.url_search);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                JSONObject jsonReponse = new JSONObject(this.reponse);
                JSONArray data = jsonReponse.getJSONArray("data");

                final ArrayList<Subnavigation> listNavigation =  new ArrayList<Subnavigation>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    String type = item.getString("type");
                    if(type.equals("subnavigation")){
                        // the same process with carousel
                        JSONArray subnavigations = item.getJSONArray("items");
                        for (int k = 0; k < subnavigations.length(); k++) {
                            JSONObject subnavigation  = subnavigations.getJSONObject(k);
                            String title = subnavigation.getString("title");
                            Subnavigation tmp = new Subnavigation();
                            tmp.setName(title);
                            tmp.setData(subnavigation.toString());
                            listNavigation.add(tmp);
                        }
                        SubnavigationAdapter subadapter = new SubnavigationAdapter(SearchType.this,R.layout.dong_menu,listNavigation);
                        listMenu.setAdapter(subadapter);
                        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    JSONObject jsonObject_sub = new JSONObject(listNavigation.get(position).getData());
                                    Intent intent_searchtype = new Intent(SearchType.this,SearchType.class);
                                    intent_searchtype.putExtra("offerings",jsonObject_sub.getString("offering"));
                                    intent_searchtype.putExtra("category",jsonObject_sub.getString("category"));
                                    intent_searchtype.putExtra("genre",jsonObject_sub.getString("genre"));
                                    intent_searchtype.putExtra("url",url_title);
                                    SearchType.this.startActivity(intent_searchtype);
                                    overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                                } catch (JSONException e) {
                                    Toast.makeText(SearchType.this,"Lỗi rồi anh em ey",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(SearchType.this,"Lỗi rồi anh em ey",Toast.LENGTH_LONG).show();
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(SearchType.this,"Lỗi rồi anh em ey",Toast.LENGTH_LONG).show();
            }



        }
    }

}
