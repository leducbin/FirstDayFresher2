package com.ldb.bin.firstdayfresher;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    RelativeLayout relativeLayout_bg;
    ImageView imageView_close;
    EditText given_name,family_name,identifier,phone,date_of_birth,password,password_confirmation;
    CheckBox checkbox_confirm;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AnhXa();
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("vi","VN"));

                date_of_birth.setText(sdf.format(myCalendar.getTime()));
            }

        };

        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        identifier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("thuxem nao","data "+isEmailValid(identifier.getText().toString().trim()));
                if(!isEmailValid(identifier.getText().toString().trim()))
                {
                   identifier.setBackgroundColor(0xFF00FF00);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isEmailValid(identifier.getText().toString().trim()))
                {
                    identifier.setBackgroundColor(Color.BLUE);
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(given_name.getText().toString().trim() != null && family_name.getText().toString().trim() != null
                        && identifier.getText().toString().trim() != null
                        && phone.getText().toString().trim() != null
                        && date_of_birth.getText().toString().trim() != null
                        && password.getText().toString().trim() != null
                        && password_confirmation.getText().toString().trim() != null
                        && checkbox_confirm.isChecked() == true
                ) {
                    Log.e("asfdafdfa","data test " +  isEmailValid(identifier.getText().toString().trim()));

                    if (password_confirmation.getText().toString().trim().equals(password.getText().toString().trim())) {
                    String response = "http://api.danet.vn/user";
                    RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, response, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {

                                Toast.makeText(Register.this, "Bạn đã đăng ký thành công! Hãy đăng nhập lại vào hệ thống!!", Toast.LENGTH_LONG).show();
                                finish();

                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Register.this, " Đăng ký xảy ra lỗi, kiểm tra lại kết nối! ", Toast.LENGTH_LONG).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("given_name", given_name.getText().toString().trim());
                            params.put("family_name", family_name.getText().toString().trim());
                            params.put("identifier", identifier.getText().toString().trim());
                            params.put("phone", phone.getText().toString().trim());
                            params.put("provider", "movideo");
                            params.put("date_of_birth", date_of_birth.getText().toString().trim());//dd/mm/yyyy
                            params.put("password", password.getText().toString().trim());
                            params.put("password_confirmation", password_confirmation.getText().toString().trim());
                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);
                    }else
                    {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(Register.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(Register.this);
                        }
                        builder.setTitle("Bạn chưa nhập chính xác mật khẩu xác nhận!!!")
                                .setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }else
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(Register.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(Register.this);
                    }
                    builder.setTitle("Bạn chưa nhập đầy đủ thông tin đăng ký!")
                            .setNegativeButton("Oke", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }
    private void AnhXa() {
        relativeLayout_bg = (RelativeLayout) findViewById(R.id.bg_register);
        imageView_close = (ImageView) findViewById(R.id.close);
        given_name = (EditText) findViewById(R.id.given_name);
        family_name = (EditText) findViewById(R.id.family_name);
        identifier = (EditText) findViewById(R.id.identifier);
        phone = (EditText) findViewById(R.id.phone);
        date_of_birth = (EditText) findViewById(R.id.date_of_birth);
        password = (EditText) findViewById(R.id.password);
        password_confirmation = (EditText) findViewById(R.id.password_confirmation);
        checkbox_confirm = (CheckBox) findViewById(R.id.checkbox_register);
        register = (Button) findViewById(R.id.register);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
