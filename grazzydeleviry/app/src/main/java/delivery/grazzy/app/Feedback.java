package delivery.grazzy.app;

import android.app.Dialog;
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

/**
 * Created by developer.nithin@gmail.com
 */
public class Feedback extends AppCompatActivity {

    Toolbar toolbar;

    TextInputLayout input_layout_name;
    TextView input_name,submit,msg,back_btn;
    LinearLayout parent_layout;
    RatingBar ratingBar;
    StringRequest insert_feedback;

    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        ratingBar=(RatingBar)findViewById(R.id.ratingBar);



        loading = new Dialog(Feedback.this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.loading);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Feedback To Restaurant");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
        input_name = (TextView) findViewById(R.id.input_name);
        submit = (TextView) findViewById(R.id.submit);
        msg = (TextView) findViewById(R.id.msg);
        back_btn = (TextView) findViewById(R.id.back_btn);
        input_layout_name = (TextInputLayout) findViewById(R.id.input_layout_name);


        msg.setText("You picked up order "+getIntent().getExtras().getString("to"));


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
        insert_feedback = new StringRequest(Request.Method.POST,getString(R.string.base_url)+getString(R.string.delboyfeedback),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();

                        Log.e("response", "" +response);

                        if(response.contains("success"))
                        {
                            Toast.makeText(Feedback.this, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Snackbar snackbar = Snackbar
                                    .make(parent_layout, "Error Occured, Please try later", Snackbar.LENGTH_LONG) ;
                            snackbar.show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                String error_msg="";

                Log.e("error", "" + volleyError.toString());

                loading.dismiss();

                if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError)
                {
                    error_msg="No Internet Connection";

                } else if (volleyError instanceof AuthFailureError)
                {
                    error_msg="Error Occured, Please try later" ;

                } else if (volleyError instanceof ServerError)
                {
                    error_msg="Server Error, Please try later";

                } else if (volleyError instanceof NetworkError)
                {
                    error_msg="Network Error, Please try later";

                } else if (volleyError instanceof ParseError)
                {
                    error_msg="Error Occured, Please try later";
                }

                Snackbar snackbar = Snackbar
                        .make(parent_layout, error_msg, Snackbar.LENGTH_LONG) ;
                snackbar.show();

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                // TODO Auto-generated method stub

                Map<String, String> params = new HashMap<String, String>();

                params.put("feedbackfrom",""+AppController.getInstance().sharedPreferences.getString("id", ""));

                    if(getIntent().getExtras().getString("to").equals("Restaurant"))
                {

                    params.put("feedbackto",""+AppController.getInstance().restaurant_id);
                    params.put("feedbacktype","7");

                }else
                {
                    params.put("feedbackto",""+AppController.getInstance().customer_id);
                    params.put("feedbacktype","8");

                }

                params.put("order_number",""+AppController.getInstance().order_number);
                params.put("comments",""+input_name.getText().toString());
                params.put("ratings",""+((int)ratingBar.getRating()));


                Log.e("params",params.toString());

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
