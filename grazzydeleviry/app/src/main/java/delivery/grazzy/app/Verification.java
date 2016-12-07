package delivery.grazzy.app;

/**
 * Created by developer.nithin@gmail.com
 */
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



public class Verification extends AppCompatActivity {

    ImageButton edit_number;
    ProgressBar timer;
    TextView time;
    EditText code;
    int time_left = 0, sec = 0;
    SimpleDateFormat format;
    CountDownTimer countDownTimer;
    Boolean cancel = false;
    StringRequest sms_request;
    String Ph, verification_code, url;
    Random random = new Random();

    Dialog d;
    LinearLayout linearlayout;
    TextView ph_number;
    Boolean changed=false;
    static Verification mInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification);

        mInstance=this;

        Ph=AppController.getInstance().sharedPreferences.getString("phone","");

        linearlayout=(LinearLayout)findViewById(R.id.linearlayout);

        d= new Dialog(Verification.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.loading);

        AppController.getInstance().check_sms = true;

        ph_number=(TextView)findViewById(R.id.ph_number);
        timer = (ProgressBar) findViewById(R.id.timer);
        time = (TextView) findViewById(R.id.time);
        code = (EditText) findViewById(R.id.code);

        edit_number=(ImageButton)findViewById(R.id.edit_number);

        ph_number.setText(Ph);

        edit_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Verification.this,Home.class));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Verification");
        setSupportActionBar(toolbar);

        if(!ConnectivityReceiver.isConnected())
        {
            Snackbar snackbar = Snackbar
                    .make(linearlayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();

        }else
        { dispatch_sms();
        }



        format = new SimpleDateFormat("mm:ss");

        countDownTimer = new CountDownTimer(300000, 1000)
        {

            @Override
            public void onTick(long tick)
            {
                // TODO Auto-generated method stub
                if (!cancel)
                {
                    time_left++;
                    timer.setProgress(time_left);
                    sec = (int) ((tick / 1000) % 60);

                    if (sec < 10)
                    {
                        time.setText((int) (((tick / 1000) / 60) % 60) + ":0" + sec);

                    } else
                    {
                        time.setText((int) (((tick / 1000) / 60) % 60) + ":" + sec);
                    }

                    if (AppController.getInstance().verification)
                    {
                        Log.e("Ticking", "Ticking");
                        code.setText(AppController.getInstance().verification_code);
                        stop_ticking();

                    }
                }
            }

            @Override
            public void onFinish()
            {
                // TODO Auto-generated method stub

                //incomplete when code is not received even after 5 mins of wait


            }
        }.start();


        code.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                // TODO Auto-generated method stub

                if(code.getText().toString().equals(AppController.getInstance().verification_code))
                {
                    AppController.getInstance().verification = true;

                    stop_ticking();

                }

            }
        });


    }

    public static synchronized Verification getInstance() {
        return mInstance;
    }

    private void stop_ticking() {

        Snackbar.make(linearlayout, "Verification Successful", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        d.show();

        cancel = true;
        countDownTimer.cancel();
        countDownTimer.onFinish();


        if(!ConnectivityReceiver.isConnected())
        {
            Snackbar snackbar = Snackbar
                    .make(linearlayout, "No internet connection!", Snackbar.LENGTH_LONG);

            snackbar.show();

        }else
        {
            if(!changed)
            {
                changed=true;
                AppController.getInstance().valid=true;
                AppController.getInstance().register_user();
            }
        }



    }



    private void dispatch_sms() {

        // TODO Auto-generated method stub

        verification_code=""+random.nextInt(9)+random.nextInt(9)+random.nextInt(9)+random.nextInt(9);
        AppController.getInstance().verification_code=verification_code;
        url = getString(R.string.sms)+getString(R.string.user_name)+"&"+getString(R.string.password)+"&sender=BIGPER&to="+Ph+"&message=Verification+Code+is+"+verification_code+"&route_id=7";

        Log.e("url", ""+url);

        sms_request = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response)
                    {
                        // TODO Auto-generated method stub
                        //incomplete
                        Log.e("response", response);
                    }

                }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                // TODO Auto-generated method stub
                //incomplete
            }
        });

        AppController.getInstance().getRequestQueue().add(sms_request);
        Log.e("sms", "sent");
    }

    public void finish_activity()
    {
        startActivity(new Intent(Verification.this, Home.class));
        finish();
    }

}
