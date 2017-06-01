package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer.nithin@gmail.com
 */
public class AppController extends Application {

    private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public Boolean check_sms=false,verification = false;
    public String verification_code	 = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferences_editor;
    LruBitmapCache lruBitmapCache;
    public String token_value;
    Boolean token=false,valid=false;
    StringRequest verify_or_add_user_request;
    String error_msg="";
    int option_selectd=0;
    String order_number,customer_id,restaurant_id,total_cost,order_type,delivered_by,passcode,delivered_on,delivery_location,firstname,phone,restaurant_address,restaurant_phone,preparation_time,restaurant_latitude,restaurant_langitude,shipping_lat,shipping_long,id,status;
    Boolean changed=false;
    int no_of_notifications=0;
    NotificationCompat.Builder builder;
    Intent go_to_notifications;
    PendingIntent pendingIntent;
    Uri alarmSound;
    NotificationManager nm;
    String restaurant_name;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        mInstance = this;
        lruBitmapCache = new LruBitmapCache();
        sharedPreferences = getSharedPreferences("basic_parameters", 0);
        sharedPreferences_editor = sharedPreferences.edit();
        go_to_notifications = new Intent(this, Home.class);
        builder = new NotificationCompat.Builder(this);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    public AppController() {
        super();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader()
    {
        getRequestQueue();
        if (mImageLoader == null)
        {
            mImageLoader = new ImageLoader(this.mRequestQueue,lruBitmapCache);
        }


        return this.mImageLoader;
    }


    public void set_login(Boolean value)
    {
        // TODO Auto-generated method stub
        sharedPreferences_editor.putBoolean("login", true);
        sharedPreferences_editor.commit();


    }


    public void create_notification()
    {
        // TODO Auto-generated method stub


        no_of_notifications++;

        // Use NotificationCompat.Builder to set up our notification.

        // icon appears in device notification bar and right hand corner of
        // notification

        builder.setSmallIcon(R.mipmap.ic_launcher);

        // This intent is fired when notification is clicked
        go_to_notifications.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        pendingIntent = PendingIntent.getActivity(this, 0, go_to_notifications, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        // builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_launcher));

        // Content title, which appears in large type at the top of the
        // notification
        builder.setContentTitle("Eatsapp");

        // Content text, which appears in smaller text below the title
        builder.setContentText("You have " + no_of_notifications + " notifications");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        // builder.setSubText("Tap to view documentation about notifications.");

        builder.setAutoCancel(true);

        builder.setLights(Color.BLUE, 500, 500);

        long[] pattern = {200,200,100,100};
        builder.setVibrate(pattern);

        builder.setStyle(new NotificationCompat.InboxStyle());
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound != null)
        {
            builder.setSound(alarmSound);
        }

          // Will display the notification in the notification bar
        nm.notify(0, builder.build());

}

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }


     void register_user() {


         if(token && valid) {

             Verification.getInstance().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {

                     Verification.getInstance().d.show();

                 }
             });


             verify_or_add_user_request = new StringRequest(Request.Method.POST, getResources().getString(R.string.base_url)+getResources().getString(R.string.login), new Response.Listener<String>()
             {

                 @Override
                 public void onResponse(String response)
                 {
                     // TODO Auto-generated method stub

                     Log.e("response", response);

                     try {

                         if(response.contains("id"))
                         {
                             JSONArray jsonArray = new JSONArray(response);

                             sharedPreferences_editor.putString("id",jsonArray.getJSONObject(0).get("id").toString());


                         }else {

                             sharedPreferences_editor.putString("id",response);
                         }


                         sharedPreferences_editor.commit();

                         set_login(true);

                         Verification.getInstance().finish_activity();


                     }catch (Exception e)
                     {
                         Verification.getInstance().runOnUiThread(new Runnable() {
                             @Override
                             public void run() {

                                 Verification.getInstance().d.dismiss();

                             }
                         });
                         Log.e("e", e.toString());
                     }

                     Verification.getInstance().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                             Verification.getInstance().d.dismiss();

                         }
                     });

                 }
             }, new Response.ErrorListener()
             {

                 @Override
                 public void onErrorResponse(VolleyError volleyError)
                 {
                     // TODO Auto-generated method stub



                     Log.e("error", "" + volleyError.toString());

                     Verification.getInstance().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                             Verification.getInstance().d.dismiss();

                         }
                     });

                     if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError)
                     {
                         error_msg="No Internet Connection";

                     } else if (volleyError instanceof AuthFailureError)
                     {
                         error_msg="Error Occured,Please try later" ;

                     } else if (volleyError instanceof ServerError)
                     {
                         error_msg="Server Error,Please try later";

                     } else if (volleyError instanceof NetworkError)
                     {
                         error_msg="Network Error,Please try later";

                     } else if (volleyError instanceof ParseError)
                     {
                         error_msg="Error Occured,Please try later";
                     }

                     Verification.getInstance().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                             Snackbar snackbar = Snackbar
                                     .make(Verification.getInstance().linearlayout, error_msg, Snackbar.LENGTH_LONG) ;
                             snackbar.show();

                         }
                     });


                 }
             })
             {

                 @Override
                 protected Map<String, String> getParams() throws AuthFailureError
                 {
                     // TODO Auto-generated method stub
                     Map<String, String> params = new HashMap<String, String>();

                     params.put(getString(R.string.Name),AppController.getInstance().sharedPreferences.getString("firstname",""));
                     params.put(getString(R.string.phone_number),AppController.getInstance().sharedPreferences.getString("phone",""));
                     params.put(getString(R.string.token),AppController.getInstance().token_value);


                     Log.e("params", "" + params.toString());


                     return params;

                 }

                 @Override
                 public Map<String, String> getHeaders() throws AuthFailureError
                 {
                     // TODO Auto-generated method stub
                     Map<String, String> params = new HashMap<String, String>();
                     params.put("Content-Type", "application/x-www-form-urlencoded");
                     return params;

                 }

             };

             Log.e("request", "sent");


             AppController.getInstance().getRequestQueue().add(verify_or_add_user_request);

         }
    }

}

