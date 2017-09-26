package com.ldb.bin.firstdayfresher;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity {

    final long DELAY_MS = 6000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 8000; // time in milliseconds between successive task executions.
    int currentPage = 0;
    Timer timer;
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ProgressDialog pDialog2;
    private Handler handler_1;
    private ListView lv;
    ViewFlipper viewFlipper;
    RecyclerView recyclerView;
    float tuvitri, denvitri;

    // URL to get contacts JSON
    public static final String railURL = "http://api.danet.vn/data/rails/go";
    public static final String railCineURL = "http://api.danet.vn/data/rails/cineplex";
    public static final String railBuffURL = "http://api.danet.vn/data/rails/buffet";

    // url to get playlist
    public static final String playlistURL = "http://api.danet.vn/playlist/list/";

    public static int page = 0;

    ArrayList<Carousel> listCarousel;
    ArrayList<Subnavigation> listNavigation;
    ArrayList<ListId> listPlaylistID;
    ArrayList<HinhAnh> listPlaylist;

    ArrayList<ArrayList> arraylistReccycle = new ArrayList<ArrayList>();

    ArrayList<String> contactList;
    ArrayList<HinhAnh> contactList_2;
    ArrayList<HinhAnh> contactList_3;
    HinhAnhAdapter adapter;
    ListAdapter listAdapter;
    ImageView[] ivArrayDotsPager;
    ViewPager viewPager;
    NavigationView naviView;
    ListView listMenu,listviewmain;
    Toolbar menuToolbar;
    DrawerLayout drawerLayout;
    TextView textViewdefault,textView,textView2,textViewmanhinh;
    ImageView imageView,imageView_search;
    int menu = 0;
    int REQUEST_CODE_EDIT = 123;
    int REQUEST_LOGOUT = 234;
    Button buttonLogin;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();



        if(!sharedPreferences.getString("accessToken","").equals(""))
        {
            buttonLogin.setText(sharedPreferences.getString("family_name","Đăng Nhập - Đăng Ký") + " " + sharedPreferences.getString("given_name",""));
        }
        listviewmain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_SHORT).show();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("accessToken","").equals(""))
                {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(intent,REQUEST_CODE_EDIT);
                    overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, Profile.class);
                    startActivityForResult(intent,REQUEST_LOGOUT);
                    overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                }
            }
        });
        textViewdefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value !=0 ) {
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    myIntent.putExtra("key", value);
                    MainActivity.this.startActivity(myIntent);
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                myIntent.putExtra("key", 1); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                myIntent.putExtra("key", 2); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
        Intent intent = getIntent();
        int value = intent.getIntExtra("key",0);
        create(value);

        imageView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_search = new Intent(MainActivity.this,Search.class);
                startActivity(intent_search);
                overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
            }
        });


        //new GetContacts().execute();

    }

    private void AnhXa() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        naviView = (NavigationView) findViewById(R.id.navi_menu);
        listMenu = (ListView) findViewById(R.id.listview_menu);
        menuToolbar = (Toolbar) findViewById(R.id.toolbarmanhinhchinh);
        imageView = (ImageView) findViewById(R.id.menubar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        listviewmain = (ListView) findViewById(R.id.listview_main);
        textViewdefault = (TextView) findViewById(R.id.railgo);
        textViewmanhinh = (TextView) findViewById(R.id.txtbar);
        buttonLogin = (Button) findViewById(R.id.login);
        textView = (TextView) findViewById(R.id.railcine);
        textView2 = (TextView) findViewById(R.id.railbuffet);
        imageView_search = (ImageView) findViewById(R.id.search_info_film);
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);

    }

    private void create(int menu){
        ActionBar();
        GetContacts getAsyn = new GetContacts();

        switch (menu){
            case 1:
                getAsyn.setURL(railCineURL);
                Log.e(TAG,"get data of URL" + railCineURL );
                getAsyn.execute();
                break;
            case 2:
                getAsyn.setURL(railBuffURL);
                Log.e(TAG,"get data of URL" + railBuffURL );
                getAsyn.execute();
                break;
            default:
                getAsyn.setURL(railURL);
                Log.e(TAG,"get data of URL" + railURL );
                getAsyn.execute();
                break;

        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        private String url;
        private String reponse;
        private String url_title;

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
            super.onPreExecute();
            switch (this.url){
                case railURL:
                case railCineURL:
                case railBuffURL:
                {
                    // if init view show circle loading
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    break;
                }
                default:{
                    break;
                }

            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler(MainActivity.this);

            // Making a request to url and getting response
            this.reponse = sh.makeServiceCall(this.url);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {




            super.onPostExecute(result);

//            Log.e(TAG, "Response from url: " + this.url);
            switch (this.url){
                case railURL:
                case railCineURL:
                case railBuffURL:
                {
                    if(this.url == railURL)
                    {
                        textViewmanhinh.setText("MIỄN PHÍ");
                    }else if (this.url == railCineURL)
                    {
                        textViewmanhinh.setText("PHIM GÓI");
                    }
                    else
                    {
                        textViewmanhinh.setText("THUÊ PHIM");
                    }
                    // if init view show circle loading
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    // handle data;

                    try {
                        Log.e(TAG, this.reponse);
                        JSONObject jsonReponse = new JSONObject(this.reponse);

                        JSONArray data = jsonReponse.getJSONArray("data");

                        listCarousel = new ArrayList<Carousel>();
                        listNavigation =  new ArrayList<Subnavigation>();
                        listPlaylistID = new ArrayList<ListId>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String type = item.getString("type");

                            if( type.equals("carousel")){
                                JSONArray carousels = item.getJSONArray("items");

                                for (int k = 0; k < carousels.length(); k++) {
                                    JSONObject careosel  = carousels.getJSONObject(k);

                                    JSONObject background = careosel.getJSONObject("background");
                                    JSONArray items_arr = careosel.getJSONArray("items");
                                    String href = items_arr.getJSONObject(2).getString("href");
                                    String image = background.getString("image");

                                    Carousel tmp = new Carousel();

                                    tmp.setBanner(image);
                                    tmp.setHref(href);
                                    tmp.setTen(this.url);
                                    listCarousel.add(tmp);
                                }
                                ViewPagerAdapter myadapter = new ViewPagerAdapter(MainActivity.this,listCarousel);
                                viewPager.setAdapter(myadapter);
                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public static final int NUM_PAGES = 1 ;

                                    public void run() {
                                        if (currentPage == NUM_PAGES-1) {
                                            currentPage = 0;
                                        }else if (viewPager.getCurrentItem() == listCarousel.size()-1)
                                        {
                                            currentPage = 0;
                                        }
                                        viewPager.setCurrentItem(currentPage++, true);
                                    }
                                };

                                timer = new Timer(); // This will create a new Thread
                                timer .schedule(new TimerTask() { // task to be scheduled

                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, DELAY_MS, PERIOD_MS);
                                ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                                        .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.pager_image, 2, 2));
                                viewPager.setPageTransformer(true, pageTransformer);
                                LinearLayout llPagerDots = (LinearLayout) findViewById(R.id.pager_dots);
                                ivArrayDotsPager = new ImageView[listCarousel.size()];
                                for (int t = 0; t < ivArrayDotsPager.length; t++) {
                                    ivArrayDotsPager[t] = new ImageView(MainActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(5, 0, 5, 0);
                                    ivArrayDotsPager[t].setLayoutParams(params);
                                    ivArrayDotsPager[t].setImageResource(R.drawable.unslectdraw);
                                    ivArrayDotsPager[t].setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view) {
                                            view.setAlpha(1);
                                        }
                                    });
                                    llPagerDots.addView(ivArrayDotsPager[t]);
                                    llPagerDots.bringToFront();
                                }
                                ivArrayDotsPager[0].setImageResource(R.drawable.selectdraw);

                                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                                {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        for (int p = 0; p < ivArrayDotsPager.length; p++) {
                                            ivArrayDotsPager[p].setImageResource(R.drawable.unslectdraw);
                                        }
                                        ivArrayDotsPager[position].setImageResource(R.drawable.selectdraw);
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {

                                    }
                                });
                            }else if(type.equals("subnavigation")){
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
                                SubnavigationAdapter subadapter = new SubnavigationAdapter(MainActivity.this,R.layout.dong_menu,listNavigation);
                                listMenu.setAdapter(subadapter);
                                listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        try {
                                            JSONObject jsonObject_sub = new JSONObject(listNavigation.get(position).getData());
                                            Intent intent_searchtype = new Intent(MainActivity.this,SearchType.class);
                                            intent_searchtype.putExtra("offerings",jsonObject_sub.getString("offering"));
                                            intent_searchtype.putExtra("category",jsonObject_sub.getString("category"));
                                            intent_searchtype.putExtra("genre",jsonObject_sub.getString("genre"));
                                            intent_searchtype.putExtra("url",url);
                                            MainActivity.this.startActivity(intent_searchtype);
                                            overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                                        } catch (JSONException e) {

                                        }
                                    }
                                });

                                }else{
                                //  xu li tuong tu de co list playlist
                                    JSONObject playlist  = data.getJSONObject(i);
                                    Integer id = playlist.getInt("id");
                                    String name = playlist.getString("name");
                                    ListId listId = new ListId();
                                    listId.setName(name);
                                    listId.setId(id);
                                    listPlaylistID.add(listId);
                            }
                        }
                        for (int z=0;z<listPlaylistID.size();z++)
                        {
                            GetContacts tmp = new GetContacts();
                            tmp.setUrl_title(this.url);
                            tmp.setURL(playlistURL+listPlaylistID.get(z).getId());
                            tmp.execute();
                        }

                    }catch(final JSONException e){
                        // error handle

                        Log.e(TAG,"Lỗi cmnr");

                    }

                    break;
                }
                default:{
                    try{

                        listPlaylist = new ArrayList<HinhAnh>();
                        JSONObject response = new JSONObject(this.reponse);
                        JSONArray reponseList = response.getJSONArray("list");
                        String playlistname = response.getString("playlist_name");
                        for (int m = 0; m < reponseList.length();m++)
                        {
                            JSONObject reponesePoster = reponseList.getJSONObject(m);
                            String poster = reponesePoster.getString("poster");
                            String title = reponesePoster.getString("title");
                            HinhAnh img = new HinhAnh();
                            img.setHinh(poster);
                            img.setTen(title);
                            img.setData(reponesePoster.toString());
                            listPlaylist.add(img);
                        }
                        arraylistReccycle.add(listPlaylist);
                        if(arraylistReccycle.size()== listPlaylistID.size())
                        {
                            ListAdapter listAdapter = new ListAdapter(MainActivity.this,R.layout.dong_list_recycler,listPlaylistID,arraylistReccycle,this.url_title);
                            listviewmain.setAdapter(listAdapter);
                        }

//                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);
//                                recyclerView.setHasFixedSize(true);
//                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
//                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
//                                recyclerView.addItemDecoration(dividerItemDecoration);
//                                recyclerView.setLayoutManager(layoutManager);
//                                ShopAdapter shopAdapter = new ShopAdapter(listPlaylist,getApplicationContext());
//                                recyclerView.setAdapter(shopAdapter);
//
//                        }


                    }catch (final JSONException e){
                        Log.e(TAG,"Lỗi default cmnr");
                    }
                    break;
                }

            }
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
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
                buttonLogin.setText(jsonObject_new.getString("family_name") + " " +jsonObject_new.getString("given_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(requestCode == REQUEST_LOGOUT && resultCode==RESULT_OK)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().commit();
            buttonLogin.setText("Đăng Nhập - Đăng Ký");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
