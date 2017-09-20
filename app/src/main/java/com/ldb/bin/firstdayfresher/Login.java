package com.ldb.bin.firstdayfresher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText username,password;
    Button login,register;
    ImageButton btnBack;
    ProgressDialog pDialog;
    public static final String authURL = "http://api.danet.vn/user/authenticate";
    private static final String TAG = HttpHandler.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        AnhXa();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.this.finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContacts getAsyn = new GetContacts();
                getAsyn.setURL(authURL);
                getAsyn.setParam_1(username.getText().toString());
                getAsyn.setParam_2(password.getText().toString().trim());
                getAsyn.execute();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(Login.this, Register.class);
                startActivity(intentRegister);
            }
        });
    }

    private void AnhXa() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        btnBack = (ImageButton) findViewById(R.id.backApp);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void>{


        private MotionEvent event;
        private String url;
        private String reponse,param_1,param_2;

        public void setParam_1(String param_1) {
            this.param_1 = param_1;
        }

        public void setParam_2(String param_2) {
            this.param_2 = param_2;
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
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            String response = null;
            RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, authURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response != null){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(Login.this,"Bạn đã đăng nhập thành công",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.putExtra("data", jsonObject.toString());
                            setResult(RESULT_OK,intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this,"Tài Khoản và Mật Khẩu của bạn không đúng!!! ",Toast.LENGTH_LONG).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("identifier",param_1.trim());
                    params.put("password",param_2.trim());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
