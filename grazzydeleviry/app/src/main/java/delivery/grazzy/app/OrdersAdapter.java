package delivery.grazzy.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by developer.nithin@gmail.com
 */
public class OrdersAdapter extends BaseAdapter implements View.OnClickListener {
    Activity activity;

    ArrayList<String> order_number;
    ArrayList<String> preparation_time;
    ArrayList<String> restaurant_name;
    ArrayList<String> delivered_on;
    ArrayList<String> delivery_location;
    ArrayList<String> status;
    Home mContext;

    LayoutInflater inflater;
    Intent i;
    Bundle b;

    SimpleDateFormat sdf;
    SimpleDateFormat newsdf;
    Date date = null;


    public OrdersAdapter(Activity activity, ArrayList<String> order_number, ArrayList<String> preparation_time, ArrayList<String> restaurant_name, ArrayList<String> delivered_on, ArrayList<String> delivery_location, ArrayList<String> status, Home mContext) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.order_number = order_number;
        this.preparation_time = preparation_time;
        this.restaurant_name = restaurant_name;
        this.delivered_on = delivered_on;
        this.delivery_location = delivery_location;
        this.status = status;
        this.mContext = mContext;
        i = new Intent(activity, Track.class);
        b = new Bundle();

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        newsdf = new SimpleDateFormat("hh:mm a ");

        sdf.setTimeZone(TimeZone.getDefault());
        newsdf.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return order_number.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int id) {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub

        if(inflater == null)
        {
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        //new
        // the status is assigned
        //time and location name with accept buttion

        Log.e("position", position+"");

        if (AppController.getInstance().option_selectd == 0 && status.get(position).equals("Assigned")) {

            if (convertView == null || !convertView.getTag().toString().equals("order_cell")) {
                convertView = inflater.inflate(R.layout.order_cell, null);
                convertView.setTag("order_cell");
            }

            TextView order_no = (TextView) convertView.findViewById(R.id.order_no);
            TextView delivery = (TextView) convertView.findViewById(R.id.delivery);
            TextView pickup = (TextView) convertView.findViewById(R.id.pickup);
            TextView action = (TextView) convertView.findViewById(R.id.action);
            LinearLayout row = (LinearLayout) convertView.findViewById(R.id.row);

            order_no.setText("" + order_number.get(position));
            try {
                delivery.setText("" + newsdf.format((Date) sdf.parse(delivered_on.get(position))) + "\n" + delivery_location.get(position));
            } catch (Exception e) {
                Log.e("date parse error"+position, e.toString());
            }

            pickup.setText("" + preparation_time.get(position) + "\n" + restaurant_name.get(position));
            action.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            action.setText("Accept");

            action.setTag(position+"");
            action.setOnClickListener(this);


        }else if (AppController.getInstance().option_selectd == 1 && (status.get(position).equals("Accepted") || status.get(position).equals("Picked Up"))) {

            if (convertView == null || !convertView.getTag().toString().equals("order_cell")) {
                convertView = inflater.inflate(R.layout.order_cell, null);
                convertView.setTag("order_cell");
            }

            TextView order_no = (TextView) convertView.findViewById(R.id.order_no);
            TextView delivery = (TextView) convertView.findViewById(R.id.delivery);
            TextView pickup = (TextView) convertView.findViewById(R.id.pickup);
            TextView action = (TextView) convertView.findViewById(R.id.action);
            LinearLayout row = (LinearLayout) convertView.findViewById(R.id.row);

            order_no.setText("" + order_number.get(position));
            try {
                delivery.setText("" + newsdf.format((Date) sdf.parse(delivered_on.get(position))) + "\n" + delivery_location.get(position));
            } catch (Exception e) {
                Log.e("date parse error"+position, e.toString());
            }

            pickup.setText("" + preparation_time.get(position) + "\n" + restaurant_name.get(position));
            action.setTextColor(ContextCompat.getColor(activity, R.color.black));
            action.setText(status.get(position)+"");
            row.setTag(position+"");
            row.setOnClickListener(this);


        }else if (AppController.getInstance().option_selectd == 2 &&  status.get(position).equals("Shipped")) {

            if (convertView == null || !convertView.getTag().toString().equals("order_cell")) {
                convertView = inflater.inflate(R.layout.order_cell, null);
                convertView.setTag("order_cell");
            }

            TextView order_no = (TextView) convertView.findViewById(R.id.order_no);
            TextView delivery = (TextView) convertView.findViewById(R.id.delivery);
            TextView pickup = (TextView) convertView.findViewById(R.id.pickup);
            TextView action = (TextView) convertView.findViewById(R.id.action);
            LinearLayout row = (LinearLayout) convertView.findViewById(R.id.row);

            order_no.setText("" + order_number.get(position));
            try {
                delivery.setText("" + newsdf.format((Date) sdf.parse(delivered_on.get(position))) + "\n" + delivery_location.get(position));
            } catch (Exception e) {
                Log.e("date parse error"+position, e.toString());
            }

            pickup.setText("" + preparation_time.get(position) + "\n" + restaurant_name.get(position));
            action.setTextColor(ContextCompat.getColor(activity, R.color.black));
            action.setText("Delivered");



        }else
        {
//
//            Log.e("false", "cuz");
//            Log.e("option_selectd == 1",( AppController.getInstance().option_selectd == 1)+"");
//            Log.e("status.equals(Accepted)", ""+status.get(position).equals("Accepted"));

            if (convertView == null || !convertView.getTag().toString().equals("empty")) {
                convertView = inflater.inflate(R.layout.empty_row, null);
                convertView.setTag("empty");
            }


            Log.e(AppController.getInstance().option_selectd+"", "false");
        }


        //accepted
        //the status is accepted || picked up
        //time and location name with accept buttion


        //delivered
        //shipped

        if(convertView!=null)
        {
            Log.e("convertView", "was not null");
        }else {
            Log.e("convertView", "was null");
        }

        return convertView;
    }


    @Override
    public void onClick(View v) {

        int position =Integer.parseInt(v.getTag().toString());

        switch (v.getId())
        {
            case R.id.row:
                Log.e("row","clicked"+restaurant_name.get(Integer.parseInt(v.getTag().toString())));
                ((Home)mContext).start_map_activity(position);
                break;

            case R.id.action:
                Log.e("action","clicked"+restaurant_name.get(Integer.parseInt(v.getTag().toString())));
                ((Home)mContext).accept_popup(position);
                break;
        }

    }
}
