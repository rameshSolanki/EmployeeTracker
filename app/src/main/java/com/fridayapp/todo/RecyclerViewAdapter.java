package com.fridayapp.todo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Task> mData;
    private View popupInputDialogView = null;
    private EditText taskName = null;
    private EditText taskDescription = null;
    private CheckBox cbstatus = null;
    private ImageView saveUserDataButton = null;
    private ImageView cancelUserDataButton = null;
    final private String UPDATE_TASK_URL = "https://app0209.000webhostapp.com/todo/updateTask.php";
    final private String DELETE_TASK_URL = "https://app0209.000webhostapp.com/todo/deleteTask.php";
    final private String white = "#ffffff";
    private final String red = "#F44336";
    EditText tv_latitude = null;
    private Button btnCheckIn = null;
    double latitude;
    double longhitud;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Location location;
    RelativeLayout layoutPicture;

    public RecyclerViewAdapter(Context mContext, List<Task> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardveiw_item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.progressBar.setVisibility(View.VISIBLE);

        holder.tv_id.setText(mData.get(position).getId());
        holder.tv_status.setText(mData.get(position).getStatus());
        holder.checkout_date.setText(mData.get(position).getCheckin_date());
        holder.checkout_location.setText(mData.get(position).getCheckin_location());
        holder.checkout_date1.setText(mData.get(position).getCheckout_date());
        holder.checkout_location1.setText(mData.get(position).getCheckout_location());
        Picasso.get().load(mData.get(position).getImage_url())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(holder.emp_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.emp_image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        if ((mData.get(position).getStatus().equals("1"))) {
            holder.cardView.setBackgroundColor(Color.parseColor(white));
            holder.img_edit.setEnabled(false);
        }
        else {
            holder.cardView.setBackgroundColor(Color.parseColor(white));
            holder.img_edit.setEnabled(true);
        }
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // DELETE button functionality
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete")
                        .setMessage("" + mData.get(position).getId())
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final String id = mData.get(position).getId();
                                deleteTask(id, position, v);
                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();


            }
        });
        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = mData.get(position).getId();
                String name = mData.get(position).getCheckin_date();
                String desc = mData.get(position).getCheckin_location();
                String status = mData.get(position).getStatus();
                final String image_url = mData.get(position).getImage_url();
                //  Toast.makeText(mContext, name+"\n"+desc, Toast.LENGTH_LONG).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                // Set title, icon, can not cancel properties.
                alertDialogBuilder.setTitle("Check out");
                alertDialogBuilder.setIcon(R.drawable.ic_my_location_black_24dp);
                alertDialogBuilder.setCancelable(true);

                LayoutInflater layoutInflater = LayoutInflater.from(mContext);

                // Inflate the popup dialog from a layout xml file.
                popupInputDialogView = layoutInflater.inflate(R.layout.addtask__popup, null);
                layoutPicture = popupInputDialogView.findViewById(R.id.layoutPicture);
                tv_latitude = popupInputDialogView.findViewById(R.id.tv_latitude);
                btnCheckIn = popupInputDialogView.findViewById(R.id.checkinBtn);
                // Get user input edittext and button ui controls in the popup dialog.
                cbstatus = (CheckBox) popupInputDialogView.findViewById(R.id.cb_status);
                saveUserDataButton = popupInputDialogView.findViewById(R.id.bt_save);
                cancelUserDataButton = popupInputDialogView.findViewById(R.id.bt_cancel);

                layoutPicture.setVisibility(View.GONE);
                alertDialogBuilder.setView(popupInputDialogView);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                btnCheckIn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Check In Clicked", Toast.LENGTH_SHORT).show();
                        if (checkPermissions()) {
                            if (isLocationEnabled()) {
                                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                                        new OnCompleteListener<Location>() {
                                            @Override
                                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                                                location = task.getResult();
                                                if (location == null) {
                                                    requestNewLocationData();
                                                } else {
                                                    latitude = location.getLatitude();
                                                    longhitud = location.getLongitude();
//                                                    tv_latitude.setText("Latitude : " + String.valueOf(latitude)
//                                                            + "\nLongitude : " + String.valueOf(longhitud));

                                                    Geocoder geocoder;
                                                    List<Address> addresses;
                                                    geocoder = new Geocoder(mContext, Locale.getDefault());

                                                    try {
                                                        addresses = geocoder. getFromLocation(location.getLatitude(),
                                                                location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                                        String city = addresses.get(0).getLocality();
                                                        String state = addresses.get(0).getAdminArea();
                                                        String country = addresses.get(0).getCountryName();
                                                        String postalCode = addresses.get(0).getPostalCode();
                                                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                                                        tv_latitude.setText(address);
                                                        System.out.println(address+"-------------");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                );
                            } else {
                                Toast.makeText(mContext, "Turn on location", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(intent);
                            }
                        } else {
                            requestPermissions();
                        }

                    }
                });

                if (status.equals("1")) {
                    cbstatus.setChecked(true);
                } else
                    cbstatus.setChecked(false);


                saveUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String checkout_location = tv_latitude.getText().toString().trim();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy 'at' hh:mm aaa", Locale.getDefault());
                        final String checkout_date = sdf.format(new Date());
                        final String checkin_date = mData.get(position).getCheckin_date();
                        final String checkin_location = mData.get(position).getCheckin_location();
                        final String image_url = mData.get(position).getImage_url();
                        String TaskStatus = "0";
                        if (cbstatus.isChecked())
                            TaskStatus = "1";

                        if (tv_latitude.length() >= 1 && cbstatus.isChecked()) {
                            final String finalTaskStatus = TaskStatus;
                            final String finalTaskStatus1 = TaskStatus;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_TASK_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                                    Picasso.get().load(mData.get(position).getImage_url()).into(holder.emp_image);
                                    holder.tv_id.setText(id);
                                    holder.tv_status.setText(mData.get(position).getStatus());
                                    holder.checkout_date1.setText(checkout_date);
                                    holder.checkout_location1.setText(checkout_location);
                                    holder.checkout_date.setText(checkin_date);
                                    holder.checkout_location.setText(checkin_location);
                                    if (finalTaskStatus1 == "1") {
                                        holder.tv_status.setText("1");
                                        holder.cardView.setBackgroundColor(Color.parseColor(white));
                                    } else {
                                        holder.tv_status.setText("0");
                                        holder.cardView.setBackgroundColor(Color.parseColor(white));
                                    }

                                    Task temp = new Task(image_url, checkin_location, checkin_date, checkout_location, checkout_date,
                                            finalTaskStatus, id);
                                    for (int i = 0; i < mData.size(); i++) {
                                        if (mData.get(i).getId().equals(id)) {
                                            mData.set(i, temp);
                                            break;
                                        }
                                    }
                                    notifyDataSetChanged();

                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("taskId", id);
                                    params.put("imageurl", image_url);
                                    params.put("checkin_location", checkin_location);
                                    params.put("checkin_date", checkin_date);
                                    params.put("checkout_location", checkout_location);
                                    params.put("checkout_date", checkout_date);
                                    params.put("status", finalTaskStatus);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(stringRequest);
                            alertDialog.cancel();
                        } else {
                            Toast.makeText(mContext, "Locating", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                cancelUserDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public void reload() {
        notifyDataSetChanged();
    }

    public void deleteTask(final String Taskid, final int position, final View v) {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Deleting...");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_TASK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                if (response.length() > 3) {
                    mData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size());
                    Snackbar.make(v, "Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    dialog.dismiss();
                    Snackbar.make(v, "Unable to Delete", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(mContext, "Unable to Delete", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("taskId", Taskid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    // -----------------Map code ---------

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                                location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = location.getLatitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(mContext, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longhitud = mLastLocation.getLongitude();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (Activity) mContext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView checkout_date, checkout_date1, checkout_location1, checkout_location, tv_status, tv_id;
        ImageView img_edit, img_delete;
        CircularImageView emp_image;
        CardView cardView;
        ProgressBar progressBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            checkout_date1 = (TextView) itemView.findViewById(R.id.cv_taskName1);
            checkout_location1 = (TextView) itemView.findViewById(R.id.cv_taskDescription1);
            checkout_date = (TextView) itemView.findViewById(R.id.cv_taskName);
            checkout_location = (TextView) itemView.findViewById(R.id.cv_taskDescription);
            tv_status = (TextView) itemView.findViewById(R.id.cv_status);
            tv_id = (TextView) itemView.findViewById(R.id.cv_taskId);
            emp_image = (CircularImageView) itemView.findViewById(R.id.emp_image);
            img_edit = (ImageView) itemView.findViewById(R.id.bt_edit);
            img_delete = (ImageView) itemView.findViewById(R.id.bt_delete);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
            progressBar = itemView.findViewById(R.id.progress_bar);

        }
    }
}
