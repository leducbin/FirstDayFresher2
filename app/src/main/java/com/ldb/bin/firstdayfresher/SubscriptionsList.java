package com.ldb.bin.firstdayfresher;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionsList extends AppCompatActivity {

    Button btn_60,btn_30,btn_15;
    TextView txtTitle,txtDescript;
    ProgressDialog pDialog;
    private final SharedPreferences sharedPreferences;

    public SubscriptionsList(SharedPreferences sharedPreferences) {
        this.sharedPreferences =  getSharedPreferences("dataLogin",MODE_PRIVATE);;
    }

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
                final JSONArray json_array = json_reponse.getJSONArray("data");
                btn_60.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(sharedPreferences.getString("credits","0")) >= 60)
                        {
                            try {
                                Toast.makeText(SubscriptionsList.this,"Data ID: " + json_array.getJSONObject(0).getInt("id"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(SubscriptionsList.this,"Dữ liệu false",Toast.LENGTH_LONG).show();
                            }
                        }else
                        {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(SubscriptionsList.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(SubscriptionsList.this);
                            }
                            builder.setTitle("Đã xảy ra lỗi, tài khoản của bạn không đủ điểm!!!")
                                    .setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });
                btn_30.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(sharedPreferences.getString("credits","0")) >= 30)
                        {
                            try {
                                Toast.makeText(SubscriptionsList.this,"Data ID: " + json_array.getJSONObject(1).getInt("id"),Toast.LENGTH_LONG).show();
                                String url_purchase ="http://api.danet.vn/subscriptions/"+ json_array.getJSONObject(1).getInt("id") +"/purchase";

                                RequestQueue requestQueue = Volley.newRequestQueue(SubscriptionsList.this);
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url_purchase, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response != null){
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                Toast.makeText(SubscriptionsList.this,"Đã thanh toán thành công và cập nhật lại tài khoản! ",Toast.LENGTH_LONG).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(SubscriptionsList.this,"Đã xảy ra lỗi trong quá trình thanh toán",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                ){
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("Movideo-Auth", sharedPreferences.getString("accessToken",""));
                                        return params;
                                    }
                                }
                                        ;

                                requestQueue.add(stringRequest);

                            } catch (JSONException e) {
                                Toast.makeText(SubscriptionsList.this,"Dữ liệu false cmnr",Toast.LENGTH_LONG).show();
                            }
                        }else
                        {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(SubscriptionsList.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(SubscriptionsList.this);
                            }
                            builder.setTitle("Đã xảy ra lỗi, tài khoản của bạn không đủ điểm!!!")
                                    .setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });
                btn_15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Integer.parseInt(sharedPreferences.getString("credits","0")) >= 15)
                        {
                            try {
                                Toast.makeText(SubscriptionsList.this,"Data ID: " + json_array.getJSONObject(0).getInt("id"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(SubscriptionsList.this,"Dữ liệu false",Toast.LENGTH_LONG).show();
                            }
                        }else
                        {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(SubscriptionsList.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(SubscriptionsList.this);
                            }
                            builder.setTitle("Đã xảy ra lỗi, tài khoản của bạn không đủ điểm!!!")
                                    .setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    }
                });


            } catch (JSONException e) {

            }
        }
    }
}
