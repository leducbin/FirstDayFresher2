package com.ldb.bin.firstdayfresher;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionsList extends AppCompatActivity {

    Button btn_60,btn_30,btn_15;
    TextView txtTitle,txtDescript;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions_list);
        AnhXa();

        Get_list get_list = new Get_list();
        get_list.execute();


    }

    private void AnhXa() {
        btn_60 = (Button) findViewById(R.id.button2);
        btn_30 = (Button) findViewById(R.id.button4);
        btn_15 = (Button) findViewById(R.id.button3);
        txtTitle = (TextView) findViewById(R.id.textTitle);
        txtDescript = (TextView) findViewById(R.id.textDescript);
    }

    private class Get_list extends AsyncTask<Void ,Void ,Void>
    {
        private String reponse;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SubscriptionsList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler(SubscriptionsList.this);
            this.reponse = sh.makeServiceCall("http://api.danet.vn/subscriptions/credits");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                JSONObject json_reponse = new JSONObject(reponse);
                JSONArray json_array = json_reponse.getJSONArray("data");
                btn_60.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


            } catch (JSONException e) {

            }
        }
    }
}
