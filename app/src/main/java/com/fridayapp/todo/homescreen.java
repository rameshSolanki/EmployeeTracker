package com.fridayapp.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homescreen extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    FloatingActionButton fab; //Button to Add new task
    FusedLocationProviderClient mFusedLocationClient;
    final List<Task> lstTask = new ArrayList<>();
    private ImageView empty;
    final String GET_ALL_TASK_URL = "https://app0209.000webhostapp.com/todo/getTask.php";
    Button retryButton;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        retryButton = findViewById(R.id.retryButton);
        recyclerView = findViewById(R.id.recyclerview_id1);

        Bundle bundle = getIntent().getExtras();
        final String username = bundle.getString("username");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Welcome " + username + "");

        recyclerView = findViewById(R.id.recyclerview_id1);
        final RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(homescreen.this, lstTask);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getApplicationContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        fab = findViewById(R.id.fab);
        empty = findViewById(R.id.imageView4);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        displayTask(GET_ALL_TASK_URL, username, myAdapter);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryButton.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                displayTask(GET_ALL_TASK_URL, username, myAdapter);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
                String restoredText = sharedPreferences.getString("username", "no");
                Log.d("restored 1", restoredText);
                Bundle bundle = new Bundle();
                bundle.putString("username", restoredText);
                Intent intent = new Intent(homescreen.this, AddStatusActivity.class);
                intent.putExtras(bundle);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    void displayTask(String GET_ALL_TASK_URL, final String username, final RecyclerViewAdapter myAdapter) {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(homescreen.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TASK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {

                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject serverData = array.getJSONObject(i);
                        String Taskid = serverData.getString("TaskId");
                        String image_url = serverData.getString("imageurl");
                        String checkin_location = serverData.getString("checkin_location");
                        String checkin_date = serverData.getString("checkin_date");
                        String checkout_location = serverData.getString("checkout_location");
                        String checkout_date = serverData.getString("checkout_date");
                        String status = serverData.getString("status");
                        lstTask.add(new Task(image_url, checkin_location, checkin_date, checkout_location, checkout_date, status, Taskid));
                    }
                    if (lstTask.isEmpty())
                        empty.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Toast.makeText(homescreen.this, "Failed to retrieve data.", Toast.LENGTH_LONG).show();
                }
                myAdapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        if (!InternetConnection.isInternetAvailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                            retryButton.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            retryButton.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(homescreen.this, "Failed to retrieve data.", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("username", username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(homescreen.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(homescreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
