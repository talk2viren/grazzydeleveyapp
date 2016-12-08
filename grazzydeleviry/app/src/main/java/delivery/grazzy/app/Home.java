package delivery.grazzy.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.util.Map;

/**
 * Created by developer.nithin@gmail.com
 */
public class Home extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    Home mContext;

    LayoutInflater inflater;

    StringRequest get_new_orders, update_order_status,insert_location_into_db;

    LinearLayout new_bg, accepted_bg, delivered_bg, logout_bg;
    TextView new_text, accepted_text, delivered_text, logout_text;
    Dialog loading;

    ArrayList<String> order_number = new ArrayList<String>();
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<String> customer_id = new ArrayList<String>();
    ArrayList<String> customer_name = new ArrayList<String>();
    ArrayList<String> restaurant_id = new ArrayList<String>();
    ArrayList<String> restaurant_name = new ArrayList<String>();
    ArrayList<String> restaurant_phone = new ArrayList<String>();
    ArrayList<String> restaurant_address = new ArrayList<String>();
    ArrayList<String> restaurant_latitude = new ArrayList<String>();
    ArrayList<String> restaurant_langitude = new ArrayList<String>();
    ArrayList<String> preparation_time = new ArrayList<String>();
    ArrayList<String> phone = new ArrayList<String>();
    ArrayList<String> ordered_on = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> order_type = new ArrayList<String>();
    ArrayList<String> total_cost = new ArrayList<String>();
    ArrayList<String> shipping_lat = new ArrayList<String>();
    ArrayList<String> shipping_long = new ArrayList<String>();
    ArrayList<String> delivered_by = new ArrayList<String>();
    ArrayList<String> passcode = new ArrayList<String>();
    ArrayList<String> delivered_on = new ArrayList<String>();
    ArrayList<String> delivery_location = new ArrayList<String>();
    ArrayList<String> customer_image = new ArrayList<String>();


    OrdersAdapter ordersAdapter;

    ListView listView;

    LinearLayout header_layout;

    TextView action_status_distance;



    // location
    LocationManager manager = null;
    LatLng co_ordinates;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener locationListener;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 3000; // 3 sec
    private static int FATEST_INTERVAL = 3000; // 3 sec
    private static int DISPLACEMENT = 10; // 10 meters


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mContext = this;

        inflater = this.getLayoutInflater();

        header_layout = (LinearLayout) inflater.inflate(R.layout.header, null);
        action_status_distance = (TextView) header_layout.findViewById(R.id.action_status_distance);

        loading = new Dialog(Home.this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.loading);

        new_bg = (LinearLayout) findViewById(R.id.new_bg);
        accepted_bg = (LinearLayout) findViewById(R.id.accepted_bg);
        delivered_bg = (LinearLayout) findViewById(R.id.delivered_bg);
        logout_bg = (LinearLayout) findViewById(R.id.logout_bg);


        new_text = (TextView) findViewById(R.id.new_text);
        accepted_text = (TextView) findViewById(R.id.accepted_text);
        delivered_text = (TextView) findViewById(R.id.delivered_text);
        logout_text = (TextView) findViewById(R.id.logout_text);
        listView = (ListView) findViewById(R.id.listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Grazzy Delivery App");
        setSupportActionBar(toolbar);

        new_bg.setOnClickListener(this);
        accepted_bg.setOnClickListener(this);
        delivered_bg.setOnClickListener(this);
        logout_bg.setOnClickListener(this);

        ordersAdapter = new OrdersAdapter(Home.this, order_number, preparation_time, restaurant_name, delivered_on, delivery_location, status, mContext);
        listView.setAdapter(ordersAdapter);

        listView.addHeaderView(header_layout);

        ordersAdapter.notifyDataSetChanged();

        if (!ConnectivityReceiver.isConnected()) {

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        } else {
            get_new_orders();
        }


//        startActivity(new Intent(Home.this, Feedback.class));

//        startActivity(new Intent(Home.this,Track.class));

        if (checkPlayServices()) {
            // Building the GoogleApi client

            Log.e("checkPlayServices","was true");
            buildGoogleApiClient();

            createLocationRequest();
        }

        locationListener = new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation = location;

                if (mLastLocation != null) {


                    Log.e("new location",mLastLocation.toString());
                    
                    insert_location_into_db();

                }


            }

        };

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }


    }

    private void insert_location_into_db() {

        insert_location_into_db = new StringRequest(Request.Method.POST,getString(R.string.base_url)+getString(R.string.addlocation),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                        Log.e("location insert", "" +response);




                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                // TODO Auto-generated method stub

                Map<String, String> params = new HashMap<String, String>();

                params.put("deliveryboy_id",""+AppController.getInstance().sharedPreferences.getString("id", ""));
                params.put("latitude",""+mLastLocation.getLatitude());
                params.put("langitude",""+mLastLocation.getLongitude());

                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        AppController.getInstance().getRequestQueue().add(insert_location_into_db);
    }

    protected void startLocationUpdates() {

//        Log.e("startLocationUpdates","startLocationUpdates");


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
        }



    }


    protected void createLocationRequest() {
//        Log.e("createLocationRequest","createLocationRequest");


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {

//        Log.e("buildGoogleApiClient","buildGoogleApiClient");


        mGoogleApiClient = new GoogleApiClient.Builder(Home.this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
//        Log.e("checkPlayServices","checkPlayServices");


        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Home.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,Home.this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(Home.this, "This device is not supported.", Toast.LENGTH_LONG).show();

            }
            return false;
        }
        return true;
    }

    protected void stopLocationUpdates() {
        Log.e("stopLocationUpdates","stopLocationUpdates");

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("onConnectionFailed",  connectionResult.getErrorCode()+"");

    }



    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location

        Log.e("onConnected","onConnected");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Log.e("onConnectionSuspended","onConnectionSuspended");

        mGoogleApiClient.connect();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

            Log.e("onStart","connect");

        }

    }

    @Override
    public void onClick(View v) {

        Log.e("click", "registered");


        new_bg.setBackgroundColor(getResources().getColor(R.color.white));
        accepted_bg.setBackgroundColor(getResources().getColor(R.color.white));
        delivered_bg.setBackgroundColor(getResources().getColor(R.color.white));
        logout_bg.setBackgroundColor(getResources().getColor(R.color.white));

        new_text.setTypeface(null, Typeface.NORMAL);
        accepted_text.setTypeface(null, Typeface.NORMAL);
        delivered_text.setTypeface(null, Typeface.NORMAL);
        logout_text.setTypeface(null, Typeface.NORMAL);

        new_text.setTextColor(getResources().getColor(R.color.black));
        accepted_text.setTextColor(getResources().getColor(R.color.black));
        delivered_text.setTextColor(getResources().getColor(R.color.black));
        logout_text.setTextColor(getResources().getColor(R.color.black));


        switch (v.getId()) {

            case R.id.new_bg:
                AppController.getInstance().option_selectd = 0;
                action_status_distance.setText("Action");
                new_bg.setBackgroundResource(R.color.bg);
                new_text.setTypeface(null, Typeface.BOLD);
                new_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                filter();
                break;


            case R.id.accepted_bg:
                AppController.getInstance().option_selectd = 1;
                action_status_distance.setText("Status");
                accepted_bg.setBackgroundResource(R.color.bg);
                accepted_text.setTypeface(null, Typeface.BOLD);
                accepted_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                filter();
                break;

            case R.id.delivered_bg:
                AppController.getInstance().option_selectd = 2;
                action_status_distance.setText("Distance");
                delivered_bg.setBackgroundResource(R.color.bg);
                delivered_text.setTypeface(null, Typeface.BOLD);
                delivered_text.setTextColor(getResources().getColor(R.color.colorPrimary));
                filter();
                break;

            case R.id.logout_bg:
                AppController.getInstance().option_selectd = 3;
                logout_bg.setBackgroundResource(R.color.bg);
                logout_text.setTypeface(null, Typeface.BOLD);
                logout_text.setTextColor(getResources().getColor(R.color.colorPrimary));

                AppController.getInstance().verification = false;
                AppController.getInstance().sharedPreferences_editor.putBoolean("login", false);
                AppController.getInstance().sharedPreferences_editor.commit();
                startActivity(new Intent(Home.this, MainActivity.class));
                finish();

//                startActivity(new Intent(Home.this, Track.class));

                break;

            default:
                break;
        }

    }

    private void filter() {

        Log.e("filter", "filter");

        loading.show();
        ordersAdapter.notifyDataSetChanged();
        loading.dismiss();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menus, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.sync) {

            if (!ConnectivityReceiver.isConnected()) {

                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            } else {
                get_new_orders();
            }


            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void get_new_orders() {

        order_number.clear();
        id.clear();
        customer_id.clear();
        customer_name.clear();
        restaurant_id.clear();
        restaurant_name.clear();
        restaurant_phone.clear();
        restaurant_address.clear();
        restaurant_latitude.clear();
        restaurant_langitude.clear();
        preparation_time.clear();
        phone.clear();
        ordered_on.clear();
        status.clear();
        order_type.clear();
        total_cost.clear();
        shipping_lat.clear();
        shipping_long.clear();
        delivered_by.clear();
        passcode.clear();
        delivered_on.clear();
        delivery_location.clear();
        customer_image.clear();

        loading.show();

        get_new_orders = new StringRequest(Request.Method.GET, getString(R.string.base_url) + getString(R.string.orders) + AppController.getInstance().sharedPreferences.getString("id", ""),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.contains("Orders could not be found"))
                        {

                            Toast.makeText(Home.this, "Orders have not been assigned yet", Toast.LENGTH_SHORT).show();

                        }else
                        {


                            Log.e("response", "" + response);

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                Log.e("jsonArray", "" + jsonArray.length());

                                if (jsonArray.length() == 0) {
                                    Toast.makeText(Home.this, "No new Orders", Toast.LENGTH_SHORT).show();
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    order_number.add(jsonArray.getJSONObject(i).get("order_number").toString());
                                    id.add(jsonArray.getJSONObject(i).get("id").toString());
                                    customer_id.add(jsonArray.getJSONObject(i).get("customer_id").toString());
                                    customer_name.add(jsonArray.getJSONObject(i).get("firstname").toString());
                                    restaurant_id.add(jsonArray.getJSONObject(i).get("restaurant_id").toString());
                                    restaurant_name.add(jsonArray.getJSONObject(i).get("restaurant_name").toString());
                                    restaurant_phone.add(jsonArray.getJSONObject(i).get("restaurant_phone").toString());
                                    restaurant_address.add(jsonArray.getJSONObject(i).get("restaurant_address").toString());
                                    restaurant_latitude.add(jsonArray.getJSONObject(i).get("restaurant_latitude").toString());
                                    restaurant_langitude.add(jsonArray.getJSONObject(i).get("restaurant_langitude").toString());
                                    preparation_time.add(jsonArray.getJSONObject(i).get("preparation_time").toString());
                                    phone.add(jsonArray.getJSONObject(i).get("phone").toString());
                                    ordered_on.add(jsonArray.getJSONObject(i).get("ordered_on").toString());
                                    status.add(jsonArray.getJSONObject(i).get("status").toString());
                                    order_type.add(jsonArray.getJSONObject(i).get("order_type").toString());
                                    total_cost.add(jsonArray.getJSONObject(i).get("total_cost").toString());
                                    shipping_lat.add(jsonArray.getJSONObject(i).get("shipping_lat").toString());
                                    shipping_long.add(jsonArray.getJSONObject(i).get("shipping_long").toString());
                                    delivered_by.add(jsonArray.getJSONObject(i).get("delivered_by").toString());
                                    passcode.add(jsonArray.getJSONObject(i).get("passcode").toString());
                                    delivered_on.add(jsonArray.getJSONObject(i).get("delivered_on").toString());
                                    delivery_location.add(jsonArray.getJSONObject(i).get("delivery_location").toString());
                                    customer_image.add(jsonArray.getJSONObject(i).get("customer_image").toString());


                                }

                                ordersAdapter.notifyDataSetChanged();


                            } catch (JSONException e) {


                                Log.e("JSONException", "" + e.toString());

                            }
                        }

                        loading.dismiss();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                String error_msg = "";

                Log.e("error", "" + volleyError.toString());

                loading.dismiss();

                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    error_msg = "No Internet Connection";

                } else if (volleyError instanceof AuthFailureError) {
                    error_msg = "Error Occured, Please try later";

                } else if (volleyError instanceof ServerError) {
                    error_msg = "Server Error, Please try later";

                } else if (volleyError instanceof NetworkError) {
                    error_msg = "Network Error, Please try later";

                } else if (volleyError instanceof ParseError) {
                    error_msg = "Error Occured, Please try later";
                }

                Toast.makeText(Home.this, error_msg, Toast.LENGTH_SHORT).show();

            }
        });

        AppController.getInstance().getRequestQueue().add(get_new_orders);


    }

    public void accept_popup(final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setMessage("Do you want to accept order #" + order_number.get(position) + " ? Pickup from " + restaurant_name.get(position) + " (" + restaurant_address.get(position) + ") at " + preparation_time.get(position) + " and deliver to " + delivery_location.get(position) + " by " + delivered_on.get(position)).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                dialog.cancel();
                loading.show();
                if (!ConnectivityReceiver.isConnected()) {

                    Toast.makeText(Home.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                } else {
                    update_order_status(position, "Accepted");
                }

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();

            }
        });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void update_order_status(final int position, final String status_value) {

        loading.show();
        update_order_status = new StringRequest(Request.Method.POST, getString(R.string.base_url) + getString(R.string.changestatus),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.e("response", "" + response);

                        if (response.contains("success")) {
                            status.set(position, "Accepted");
                            ordersAdapter.notifyDataSetChanged();


                            start_map_activity(position);




                        }

                        loading.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                String error_msg = "";

                Log.e("error", "" + volleyError.toString());

                loading.dismiss();

                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    error_msg = "No Internet Connection";

                } else if (volleyError instanceof AuthFailureError) {
                    error_msg = "Error Occured, Please try later";

                } else if (volleyError instanceof ServerError) {
                    error_msg = "Server Error, Please try later";

                } else if (volleyError instanceof NetworkError) {
                    error_msg = "Network Error, Please try later";

                } else if (volleyError instanceof ParseError) {
                    error_msg = "Error Occured, Please try later";
                }

                Toast.makeText(Home.this, error_msg, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // TODO Auto-generated method stub

                Map<String, String> params = new HashMap<String, String>();

                Log.e("ids", "" + id.toString());

                params.put("id", "" + id.get(position));
                params.put("status", status_value);

                Log.e("params", "" + params.toString());

                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };

        AppController.getInstance().getRequestQueue().add(update_order_status);


    }

    public void start_map_activity(int position) {

        AppController.getInstance().id =id.get(position);
        AppController.getInstance().order_number =order_number.get(position);
        AppController.getInstance().customer_id=customer_id.get(position);
        AppController.getInstance().restaurant_id=restaurant_id.get(position);
        AppController.getInstance().total_cost=total_cost.get(position);
        AppController.getInstance().order_type=order_type.get(position);
        AppController.getInstance().delivered_by=delivered_by.get(position);
        AppController.getInstance().passcode=passcode.get(position);
        AppController.getInstance().delivered_on=delivered_on.get(position);
        AppController.getInstance().delivery_location=delivery_location.get(position);
        AppController.getInstance().firstname=customer_name.get(position);
        AppController.getInstance().phone=phone.get(position);
        AppController.getInstance().restaurant_address=restaurant_address.get(position);
        AppController.getInstance().restaurant_phone=restaurant_phone.get(position);
        AppController.getInstance().preparation_time=preparation_time.get(position);
        AppController.getInstance().restaurant_latitude=restaurant_latitude.get(position);
        AppController.getInstance().restaurant_langitude=restaurant_langitude.get(position);
        AppController.getInstance().shipping_lat=shipping_lat.get(position);
        AppController.getInstance().shipping_long=shipping_long.get(position);
        AppController.getInstance().status=status.get(position);

        startActivity(new Intent(Home.this, Track.class));
    }


    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if(AppController.getInstance().changed)
        {
            AppController.getInstance().changed=false;
            get_new_orders();
        }


    }


    private void buildAlertMessageNoGps() {
        // TODO Auto-generated method stub


        final AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();

            }
        });
        final AlertDialog alert = builder.create();
        alert.show();

    }


}
