package delivery.grazzy.app;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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


import java.util.HashMap;

import java.util.Map;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by developer.nithin@gmail.com
 */
public class Feedback extends AppCompatActivity {

    Toolbar toolbar;

    TextInputLayout input_layout_name;
    TextView input_name, submit, msg, back_btn;
    LinearLayout parent_layout;
    RatingBar ratingBar;
    StringRequest insert_feedback;

    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(Color.parseColor(getString(R.string.my_statusbar_color)));
        }

        setContentView(R.layout.feedback);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);


        loading = new Dialog(Feedback.this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.loading);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Feedback To "+getIntent().getExtras().getString("to"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
        input_name = (TextView) findViewById(R.id.input_name);
        submit = (TextView) findViewById(R.id.submit);
        msg = (TextView) findViewById(R.id.msg);
        back_btn = (TextView) findViewById(R.id.back_btn);
        input_layout_name = (TextInputLayout) findViewById(R.id.input_layout_name);


        if (getIntent().getExtras().getString("to").contains("Restaurant"))
        {
            msg.setText("You picked up order from Restaurant");
        }else {
            msg.setText("You delivered the order to the Customer");
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectivityReceiver.isConnected()) {
                    Snackbar snackbar = Snackbar
                            .make(parent_layout, "No internet connection!", Snackbar.LENGTH_LONG);
                    snackbar.show();

                } else {
                    if (validateName()) {
                        insert_feedback();
                    }

                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }

    private void insert_feedback() {
        loading.show();
        insert_feedback = new StringRequest(Request.Method.POST, getString(R.string.base_url) + getString(R.string.delboyfeedback),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();

                        Log.e("response", "" + response);

                        if (response.contains("success")) {
                            Toast.makeText(Feedback.this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(parent_layout, "Error Occured, Please try later", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


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

                Snackbar snackbar = Snackbar
                        .make(parent_layout, error_msg, Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        }) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {

                JSONObject params = new JSONObject();

                try {
                    params.put("feedbackfrom", "" + AppController.getInstance().sharedPreferences.getString("id", ""));
                    if (getIntent().getExtras().getString("to").equals("Restaurant")) {

                        params.put("feedbackto", "" + AppController.getInstance().restaurant_id);
                        params.put("feedbacktype", "7");

                    } else {
                        params.put("feedbackto", "" + AppController.getInstance().customer_id);
                        params.put("feedbacktype", "8");

                    }

                    params.put("order_number", "" + AppController.getInstance().order_number);
                    params.put("comments", "" + input_name.getText().toString());
                    params.put("ratings", "" + ((int) ratingBar.getRating()));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.e("params values", "" + params.toString());
                return params.toString().getBytes();
            };
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/text");
                headers.put("charset", "TYPE_UTF8_CHARSET");

                Log.e("headers", "" + headers.toString());
                return headers;
            }
        };

        AppController.getInstance().getRequestQueue().add(insert_feedback);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateName() {
        if (input_name.getText().toString().length() == 0) {
            input_layout_name.setError("Please enter your feedback");
            requestFocus(input_name);
            return false;
        } else {
            input_layout_name.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;

            }
        }

    }
}
