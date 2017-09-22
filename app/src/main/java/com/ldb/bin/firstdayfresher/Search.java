package com.ldb.bin.firstdayfresher;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Search extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;
    ProgressDialog pDialog;
    ImageView imageClose,imageSearch,image_search;
    EditText editSearch;
    ListView listViewSearch;
    TextView textView_title,textView_duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        AnhXa();
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSearch.getText().toString().equals("") || editSearch.getText().toString().trim().equals("") )
                {

                    Log.e(TAG,"data " + editSearch.getText().toString().length());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Search.this);
                    dialog.setTitle("Hãy nhập từ khóa muốn tìm").show();
                }
                else
                {
                    Search_info search_info = new Search_info();
                    search_info.setUrl(editSearch.getText().toString());
                    search_info.execute();
                }
            }
        });
    }

    private void AnhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        imageClose = (ImageView) findViewById(R.id.close);
        editSearch = (EditText) findViewById(R.id.searchtext);
        imageSearch = (ImageView) findViewById(R.id.search_click);
        listViewSearch = (ListView) findViewById(R.id.listview_search);

    }

    private class Search_info extends AsyncTask<Void, Void, Void>
    {
        private String url;
        private String reponse;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Search.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler(Search.this);

            // Making a request to url and getting response
            this.reponse = sh.makeServiceCall("http://api.danet.vn/products/advanced_search?q="+this.url.trim());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                if(reponse.equals("false"))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Search.this);
                    dialog.setTitle("Không có dữ liệu cần tìm! ").show();
                }
                else
                {
                    JSONObject reponse_info = new JSONObject(reponse);
                    JSONArray data = reponse_info.getJSONArray("data");
                    ArrayList<SearchItem> arrayList_item = new ArrayList<SearchItem>();
                    for (int i=0; i< data.length();i++)
                    {
                        JSONObject json_search = data.getJSONObject(i);
                        String title = json_search.getString("title");
                        String image = json_search.getJSONObject("image").getString("base_uri");
                        String duration = json_search.getString("duration");
                        String data_item = json_search.toString();
                        SearchItem search_info = new SearchItem();
                        search_info.setImage(image);
                        search_info.setDuration(duration);
                        search_info.setTitle(title);
                        search_info.setData(data_item);
                        arrayList_item.add(search_info);
                    }
                    ListViewSearchAdapter adapter = new ListViewSearchAdapter(Search.this,R.layout.dong_list_search,arrayList_item);
                    listViewSearch.setAdapter(adapter);
                }

            } catch (JSONException e) {

            }
        }
    }
}
