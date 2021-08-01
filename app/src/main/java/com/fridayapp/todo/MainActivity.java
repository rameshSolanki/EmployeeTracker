package com.fridayapp.todo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private String[] permissions;
    private int pCode = 12321;
    public static PermissionListener permissionListener;
    Button b1;
    TextView t1;
    EditText et_usr, et_pass;
    SharedPreferences sharedPreferences;
    final String SEND_URL = "https://app0209.000webhostapp.com/todo/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        String restoredText = sharedPreferences.getString("username", "no");
        Log.d("restored 1", restoredText);
        if (!restoredText.equals("no")) {
            Bundle bundle = new Bundle();
            bundle.putString("username", restoredText);
            Intent intent = new Intent(MainActivity.this, homescreen.class);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        Log.d("restored 2", restoredText);

        b1 = findViewById(R.id.b_login);
        et_usr = findViewById(R.id.et_unme);
        et_pass = findViewById(R.id.et_upass);
        t1 = findViewById(R.id.txtsignup);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog;
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Please Wait...");
                dialog.show();
                final String username, password;
                username = et_usr.getText().toString();
                password = et_pass.getText().toString();

                if (username.length() > 1 && password.length() > 1) {


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Log.d("resp", response);
                            if (response.equals(" -1")) // Response codes sent via Server
                            {
                                Toast.makeText(MainActivity.this, "Invalid Details", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Welcome " + response, Toast.LENGTH_LONG).show();

                  //************* Session Management by Shared PReferences ********//

                                sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putString("username", username);
                                editor.commit();
                                Bundle bundle = new Bundle();
                                bundle.putString("username", username);
                                Intent intent = new Intent(MainActivity.this, homescreen.class);
                                intent.putExtras(bundle);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }


                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap();
                            params.put("username", username);
                            params.put("password", password);

                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    requestQueue.add(stringRequest);

                }
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

    }

    private void checkPermissions1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

            boolean flag = false;
            for (String s : permissions)
                if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                    flag = true;

            if (flag) {
                requestPermissions(permissions, pCode);
            } else {
                permissionListener.permissionResult(true);
                finish();
            }
        }else
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == pCode) {
            boolean flag = true;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
                for (int i = 0, len = permissions.length; i < len; i++)
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        flag = false;
            if (flag) {
                if (permissionListener != null)
                    permissionListener.permissionResult(true);
            } else if (permissionListener != null)
                permissionListener.permissionResult(false);
            finish();
        }
    }
}