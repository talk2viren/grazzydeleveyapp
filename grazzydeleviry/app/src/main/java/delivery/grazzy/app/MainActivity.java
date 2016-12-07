package delivery.grazzy.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private EditText input_name, input_ph;
    private TextInputLayout input_layout_name, input_layout_ph;
    Button generate;
    LinearLayout parent_layout;

    int COARSE_LOCATION_permission, FINE_LOCATION_permission, READ_SMS_permission, RECEIVE_SMS_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        COARSE_LOCATION_permission = ContextCompat.checkSelfPermission(MainActivity.this,
                "android.permission.ACCESS_COARSE_LOCATION");

        FINE_LOCATION_permission = ContextCompat.checkSelfPermission(MainActivity.this,
                "android.permission.ACCESS_FINE_LOCATION");

        READ_SMS_permission = ContextCompat.checkSelfPermission(MainActivity.this,
                "android.permission.READ_SMS_permission");

        RECEIVE_SMS_permission = ContextCompat.checkSelfPermission(MainActivity.this,
                "android.permission.RECEIVE_SMS_permission");

        setContentView(R.layout.activity_main);

        if (AppController.getInstance().sharedPreferences.getBoolean("login", false)) {
            Intent i = new Intent(MainActivity.this,Home.class);
            startActivity(i);
            finish();
        }else {
            getdid();
        }

        parent_layout=(LinearLayout)findViewById(R.id.parent_layout);

        input_layout_name = (TextInputLayout) findViewById(R.id.input_layout_name);
        input_layout_ph = (TextInputLayout) findViewById(R.id.input_layout_ph);

        input_name = (EditText) findViewById(R.id.input_name);
        input_ph = (EditText) findViewById(R.id.input_ph);

        generate = (Button) findViewById(R.id.generate);

        input_name.addTextChangedListener(new MyTextWatcher(input_name));
        input_ph.addTextChangedListener(new MyTextWatcher(input_ph));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        input_name.clearFocus();


        Toast.makeText(this,"Welcome",Toast.LENGTH_LONG).show();

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateName() && validatePh()) {
                    AppController.getInstance().sharedPreferences_editor.putString("firstname", input_name.getText().toString());
                    AppController.getInstance().sharedPreferences_editor.putString("phone", input_ph.getText().toString());
                    AppController.getInstance().sharedPreferences_editor.commit();

                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                    Log.e("currentapiVersion " , currentapiVersion+"");

                    if (currentapiVersion >= Build.VERSION_CODES.M){

                        pop_up();

                    } else{

                        // do something for phones running an SDK before lollipop

                        Log.e("isConnected", ConnectivityReceiver.isConnected()+"");

                        if(!ConnectivityReceiver.isConnected())
                        {
                            Snackbar snackbar = Snackbar
                                    .make(parent_layout, "No internet connection!", Snackbar.LENGTH_LONG);

                            snackbar.show();

                        }else
                        {
                            startActivity(new Intent(MainActivity.this,Verification.class));
                            finish();
                        }
                    }


                } else {
                    Log.e("validateName() " + validateName(), " validatePh() " + validatePh());
                }



            }
        });


    }

    private void getdid() {

        if (FirebaseInstanceId.getInstance().getToken() != null) {

            Log.e("old token : ", "" + FirebaseInstanceId.getInstance().getToken());
            AppController.getInstance().token=true;
            AppController.getInstance().token_value=FirebaseInstanceId.getInstance().getToken();

        } else {

            Log.e("fetching : ", "new did");
            FirebaseInstanceId.getInstance().getToken();

        }


    }

    private boolean validateName() {
        if (input_name.getText().toString().length() == 0) {
            input_layout_name.setError("Enter your name");
            requestFocus(input_name);
            return false;
        } else {
            input_layout_name.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePh() {
        if (input_ph.getText().toString().length() == 0) {
            input_layout_ph.setError("Enter your mobile number");
            requestFocus(input_ph);
            return false;
        } else {
            input_layout_ph.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void pop_up() {
        // TODO Auto-generated method stub

        if (COARSE_LOCATION_permission != PackageManager.PERMISSION_GRANTED || FINE_LOCATION_permission != PackageManager.PERMISSION_GRANTED || READ_SMS_permission != PackageManager.PERMISSION_GRANTED || RECEIVE_SMS_permission != PackageManager.PERMISSION_GRANTED ) {
            Log.e("", "COARSE_LOCATION_permission "+COARSE_LOCATION_permission +" FINE_LOCATION_permission "+FINE_LOCATION_permission+" READ_SMS_permission "+READ_SMS_permission +" RECEIVE_SMS_permission "+RECEIVE_SMS_permission);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Grazzy App requires SMS and Location permissions.Please grant them to continue.")
                    .setTitle("Permission required");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    Log.e("", "Clicked");
                    makeRequest();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        }else
        {
            Log.e("isConnected", ConnectivityReceiver.isConnected()+"");


            if(!ConnectivityReceiver.isConnected())
            {
                Snackbar snackbar = Snackbar
                        .make(parent_layout, "No internet connection!", Snackbar.LENGTH_LONG);

                snackbar.show();

            }else
            {
                startActivity(new Intent(MainActivity.this, Verification.class));
                finish();
            }

        }

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.RECEIVE_SMS", "android.permission.READ_SMS"},
                10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {


                Toast.makeText(MainActivity.this,"length"+grantResults.length,Toast.LENGTH_LONG).show();

                if (grantResults.length == 0   || grantResults[0] != PackageManager.PERMISSION_GRANTED) {



                } else {

//                    incomplete check for the length of grantResults.length ==

                    if(!ConnectivityReceiver.isConnected())
                    {
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, "No internet connection!", Snackbar.LENGTH_LONG);

                        snackbar.show();

                    }else
                    {
                        startActivity(new Intent(MainActivity.this, Verification.class));
                        finish();
                    }

                }
                return;
            }
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
                case R.id.input_ph:
                    validatePh();
                    break;

            }
        }

    }

}
